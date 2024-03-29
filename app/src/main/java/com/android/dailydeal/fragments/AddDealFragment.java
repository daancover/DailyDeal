package com.android.dailydeal.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.dailydeal.R;
import com.android.dailydeal.activities.AddDealActivity;
import com.android.dailydeal.basics.AddressComponents;
import com.android.dailydeal.basics.Place;
import com.android.dailydeal.basics.Product;
import com.android.dailydeal.callbacks.OnListFragmentInteractionListener;
import com.android.dailydeal.callbacks.OnPlaceDetailsListener;
import com.android.dailydeal.utils.NetworkUtils;
import com.android.dailydeal.utils.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDealFragment extends Fragment implements OnListFragmentInteractionListener, OnPlaceDetailsListener, LoaderManager.LoaderCallbacks<String> {
    @BindView(R.id.rv_recycler)
    RecyclerView rvRecycler;
    @BindView(R.id.et_name)
    TextInputEditText etName;
    @BindView(R.id.et_address)
    TextInputEditText etAddress;
    @BindView(R.id.tl_product_name)
    TextInputLayout tlProductName;
    @BindView(R.id.et_product_name)
    TextInputEditText etProductName;
    @BindView(R.id.tl_old_price)
    TextInputLayout tlOldPrice;
    @BindView(R.id.et_old_price)
    TextInputEditText etOldPrice;
    @BindView(R.id.tl_new_price)
    TextInputLayout tlNewPrice;
    @BindView(R.id.et_new_price)
    TextInputEditText etNewPrice;

    private static final String ARG_PLACES = "arg_places";
    private static final String ARG_PLACE_ID = "arg_place_id";
    private static final int PLACE_DETAILS_LOADER = 3;

    private Place mSelectedPlace;

    public AddDealFragment() {
    }

    public static AddDealFragment newInstance(ArrayList<Place> places) {
        AddDealFragment fragment = new AddDealFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PLACES, places);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_deal, container, false);
        ButterKnife.bind(this, view);
        rvRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            ArrayList<Place> places = getArguments().getParcelableArrayList(ARG_PLACES);
            rvRecycler.setAdapter(new PlaceListAdapter(places, this));
        }

        etOldPrice.addTextChangedListener(TextUtils.insertCurrencyMask(etOldPrice));
        etNewPrice.addTextChangedListener(TextUtils.insertCurrencyMask(etNewPrice));

        return view;
    }

    @Override
    public void onListFragmentInteraction(Place item) {
        mSelectedPlace = item;
        etName.setText(item.getName());
        etAddress.setText(item.getAddress());
    }

    @Override
    public void onPlaceDetailsListenerResponse(String[] treeAddress) {
        String productName = etProductName.getText().toString();
        double oldPrice = TextUtils.getDoubleValueFromStringCurrency(etOldPrice.getText().toString());
        double newPrice = TextUtils.getDoubleValueFromStringCurrency(etNewPrice.getText().toString());
        Product product = new Product(productName, oldPrice, newPrice, mSelectedPlace);
        ((AddDealActivity) getActivity()).postDeal(treeAddress, product);
        ((AddDealActivity) getActivity()).hideProgressDialog();
        NavUtils.navigateUpFromSameTask(getActivity());
    }

    @OnClick(R.id.bt_submit)
    public void onSubmitClick() {
        if (validateFields()) {
            ((AddDealActivity) getActivity()).showProgressDialog();
            getPlaceDetails(mSelectedPlace.getPlaceId());
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(getContext()) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                if (args == null) {
                    return;
                }

                forceLoad();
            }

            @Override
            public String loadInBackground() {
                String placeId = args.getString(ARG_PLACE_ID);

                if (placeId == null || placeId.isEmpty()) {
                    return null;
                }

                String response = "";

                try {
                    response = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.buildUrl(placeId));
                } catch (IOException e) {
                    e.printStackTrace();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((AddDealActivity) getActivity()).showNetworkErrorDialog();
                        }
                    });
                }

                return response;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        try {
            JSONObject object = new JSONObject(data).getJSONObject("result");
            JSONArray jsonArray = object.getJSONArray("address_components");
            String[] treeAddress = new String[3];

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject addressResult = jsonArray.getJSONObject(i);
                AddressComponents address = new AddressComponents(addressResult);

                if (address.isCountry()) {
                    treeAddress[0] = address.getLongName();
                } else if (address.isState()) {
                    treeAddress[1] = address.getLongName();
                } else if (address.isCity()) {
                    treeAddress[2] = address.getLongName();
                }
            }

            onPlaceDetailsListenerResponse(treeAddress);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    private void getPlaceDetails(String placeId) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PLACE_ID, placeId);
        LoaderManager loaderManager = getActivity().getSupportLoaderManager();
        Loader<String> productListLoader = loaderManager.getLoader(PLACE_DETAILS_LOADER);

        if (productListLoader == null) {
            loaderManager.initLoader(PLACE_DETAILS_LOADER, bundle, this);
        } else {
            loaderManager.restartLoader(PLACE_DETAILS_LOADER, bundle, this);
        }
    }

    private boolean validateFields() {
        boolean success = true;

        clearFieldErrors();

        if (etName.getText().toString().isEmpty() && etAddress.getText().toString().isEmpty()) {
            success = false;
            Snackbar.make(getView(), getString(R.string.label_must_select_place), Snackbar.LENGTH_LONG).show();
        }

        if (etProductName.getText().toString().isEmpty()) {
            success = false;
            tlProductName.setError(getString(R.string.label_field_empty));
        }

        if (etOldPrice.getText().toString().isEmpty()) {
            success = false;
            tlOldPrice.setError(getString(R.string.label_field_empty));
        }

        if (etNewPrice.getText().toString().isEmpty()) {
            success = false;
            tlNewPrice.setError(getString(R.string.label_field_empty));
        }

        if (!etOldPrice.getText().toString().isEmpty() && !etNewPrice.getText().toString().isEmpty()) {
            if (TextUtils.getDoubleValueFromStringCurrency(etOldPrice.getText().toString()) <= TextUtils.getDoubleValueFromStringCurrency(etNewPrice.getText().toString())) {
                success = false;
                tlNewPrice.setError(getString(R.string.label_new_price_lower));
            }
        }

        return success;
    }

    private void clearFieldErrors() {
        tlProductName.setError(null);
        tlOldPrice.setError(null);
        tlNewPrice.setError(null);
    }
}
