package com.android.dailydeal.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.dailydeal.R;
import com.android.dailydeal.activities.AddDealActivity;
import com.android.dailydeal.basics.Place;
import com.android.dailydeal.basics.Product;
import com.android.dailydeal.callbacks.OnListFragmentInteractionListener;
import com.android.dailydeal.callbacks.OnPlaceDetailsListener;
import com.android.dailydeal.utils.LocationUtils;
import com.android.dailydeal.utils.TextUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDealFragment extends Fragment implements OnListFragmentInteractionListener, OnPlaceDetailsListener {
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
            LocationUtils.getPlaceDetails(this, mSelectedPlace.getPlaceId());
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
