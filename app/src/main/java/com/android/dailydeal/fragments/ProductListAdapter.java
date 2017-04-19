package com.android.dailydeal.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.dailydeal.R;
import com.android.dailydeal.basics.Product;
import com.android.dailydeal.utils.TextUtils;

import java.util.List;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    private final List<Product> mValues;

    public ProductListAdapter(List<Product> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_deal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mProduct.setText(mValues.get(position).getProduct());
        holder.mPlaceName.setText(mValues.get(position).getPlace().getName());
        holder.mAddress.setText(mValues.get(position).getPlace().getAddress());
        TextUtils.createPriceTextView(holder.mPrice, mValues.get(position).getOldPrice() + "", mValues.get(position).getNewPrice() + "");
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mProduct;
        public final TextView mPlaceName;
        public final TextView mAddress;
        public final TextView mPrice;
        public Product mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mProduct = (TextView) view.findViewById(R.id.tv_product);
            mPlaceName = (TextView) view.findViewById(R.id.tv_place);
            mAddress = (TextView) view.findViewById(R.id.tv_address);
            mPrice = (TextView) view.findViewById(R.id.tv_price);
        }
    }
}
