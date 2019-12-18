package com.tugasbesarkotlin5.pointofsales.View;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tugasbesarkotlin5.pointofsales.Interface.DataLoadListener;
import com.tugasbesarkotlin5.pointofsales.Model.Product;

import java.util.ArrayList;

public class ProductListRepository {

    static ProductListRepository instance;
    private ArrayList<Product> products = new ArrayList<>(  );

    static Context mContext;
    static DataLoadListener dataLoadListener;
    public static ProductListRepository getInstance(Context context){
        mContext = context;
        if (instance == null){
            instance = new ProductListRepository();
        }
        dataLoadListener = (DataLoadListener) mContext;
        return instance;
    }

    public MutableLiveData<ArrayList<Product>> getProduct(){
        loadProduct();

//        if(products.size() == -1){
//            loadProduct();
//        }

        MutableLiveData<ArrayList<Product>> product =  new MutableLiveData<>(  );
        product.setValue( products );
        
        return product;
    }

    private void loadProduct() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child( "User" ).child(firebaseUser.getUid());

        Query query = reference.child( "Product" );
        query.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    products.add( snapshot.getValue(Product.class) );
                }
                dataLoadListener.onProductLoaded();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }
}
