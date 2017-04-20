package com.android.dailydeal.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.dailydeal.R;
import com.android.dailydeal.activities.MainActivity;
import com.android.dailydeal.basics.Product;
import com.android.dailydeal.callbacks.OnGetDealsListener;
import com.android.dailydeal.callbacks.OnPlaceDetailsListener;
import com.android.dailydeal.utils.DatabaseUtils;
import com.android.dailydeal.utils.LocationUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductListFragment extends Fragment implements OnPlaceDetailsListener, OnGetDealsListener {
    @BindView(R.id.rv_recycler)
    RecyclerView rvRecycler;
    @BindView(R.id.tv_no_deals)
    TextView tvNoDeals;

    private static final String ARG_PRODUCTS = "arg_products";
    private static final String ARG_PLACE_ID = "arg_place_id";

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
            LocationUtils.getPlaceDetails(this, placeId);
        }

        return view;
    }

    @Override
    public void onPlaceDetailsListenerResponse(String[] treeAddress) {
        DatabaseUtils.getDeals(this, treeAddress);
    }

    @Override
    public void onGetDealsListenerResponse(Product product) {
        mAdapter.add(product);
        ((MainActivity) getActivity()).hideProgressDialog();
    }

    @Override
    public void onGetDealsListenerResponse(ArrayList<Product> products) {
        mAdapter.setData(products);

        if (products.isEmpty()) {
            showEmptyList();
        } else {
            showList();
        }

        ((MainActivity) getActivity()).hideProgressDialog();
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
