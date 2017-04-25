package com.android.dailydeal.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.dailydeal.R;
import com.android.dailydeal.activities.MainActivity;
import com.android.dailydeal.basics.AddressComponents;
import com.android.dailydeal.basics.Product;
import com.android.dailydeal.callbacks.OnGetDealsListener;
import com.android.dailydeal.callbacks.OnPlaceDetailsListener;
import com.android.dailydeal.utils.DatabaseUtils;
import com.android.dailydeal.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductListFragment extends Fragment implements OnPlaceDetailsListener, OnGetDealsListener, LoaderManager.LoaderCallbacks<String> {
    @BindView(R.id.rv_recycler)
    RecyclerView rvRecycler;
    @BindView(R.id.tv_no_deals)
    TextView tvNoDeals;

    private static final String ARG_PRODUCTS = "arg_products";
    private static final String ARG_PLACE_ID = "arg_place_id";
    private static final int PLACE_DETAILS_LOADER = 2;

    private ProductListAdapter mAdapter;

    public ProductListFragment() {
    }

    public static ProductListFragment newInstance(String placeId) {
        ProductListFragment fragment = new ProductListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLACE_ID, placeId);
        fragment.setArguments(args);
        return fragment;
    }

    public static ProductListFragment newInstance(ArrayList<Product> products) {
        ProductListFragment fragment = new ProductListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PRODUCTS, products);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);
        ButterKnife.bind(this, view);

        if (getArguments() != null) {
            mAdapter = new ProductListAdapter();
            rvRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
            rvRecycler.setAdapter(mAdapter);
            String placeId = getArguments().getString(ARG_PLACE_ID);
            getPlaceDetails(placeId);
        }

        return view;
    }

    @Override
    public void onPlaceDetailsListenerResponse(String[] treeAddress) {
        DatabaseUtils.getDeals(this, treeAddress);
    }

    @Override
    public void onGetDealsListenerResponse(ArrayList<Product> products) {
        mAdapter.setData(products);

        if (products.isEmpty()) {
            showEmptyList();
        } else {
            showList();
        }

        if (getActivity() != null) {
            ((MainActivity) getActivity()).hideProgressDialog();
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
                            ((MainActivity) getActivity()).showNetworkErrorDialog();
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

    public void updateProductList(String placeId) {
        getPlaceDetails(placeId);
    }

    public void showList() {
        rvRecycler.setVisibility(View.VISIBLE);
        tvNoDeals.setVisibility(View.GONE);
    }

    public void showEmptyList() {
        rvRecycler.setVisibility(View.GONE);
        tvNoDeals.setVisibility(View.VISIBLE);
    }
}
