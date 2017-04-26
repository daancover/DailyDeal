package com.android.dailydeal.utils;

import com.android.dailydeal.basics.Product;
import com.android.dailydeal.callbacks.OnGetDealsListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Daniel on 20/04/2017.
 */

public class DatabaseUtils {
    public static final String DATABASE_DEALS = "database_deals";

    public static void postDeal(String[] treeAddress, Product product) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(DatabaseUtils.DATABASE_DEALS);
        reference.child(treeAddress[0]).child(treeAddress[1]).child(treeAddress[2]).push().setValue(product);
    }

    public static void getDeals(final OnGetDealsListener listener, String[] treeAddress) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(DatabaseUtils.DATABASE_DEALS);
        reference.child(treeAddress[0]).child(treeAddress[1]).child(treeAddress[2]).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Product> products = new ArrayList<Product>();

                for (DataSnapshot tmpSnapshot : dataSnapshot.getChildren()) {
                    Product product = tmpSnapshot.getValue(Product.class);
                    products.add(product);
                }

                listener.onGetDealsListenerResponse(products);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
