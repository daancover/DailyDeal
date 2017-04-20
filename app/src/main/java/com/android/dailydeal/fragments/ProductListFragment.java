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
import com.android.dailydeal.basics.Product;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductListFragment extends Fragment {
    @BindView(R.id.rv_recycler)
    RecyclerView rvRecycler;
    @BindView(R.id.tv_no_deals)
    TextView tvNoDeals;

    private static final String ARG_PRODUCTS = "arg_products";

    public ProductListFragment() {
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
            ArrayList<Product> products = getArguments().getParcelableArrayList(ARG_PRODUCTS);
            rvRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
            rvRecycler.setAdapter(new ProductListAdapter(products));
        }

        return view;
    }
}
