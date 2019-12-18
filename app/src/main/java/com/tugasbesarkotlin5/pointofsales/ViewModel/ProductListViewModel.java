package com.tugasbesarkotlin5.pointofsales.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tugasbesarkotlin5.pointofsales.Model.Product;
import com.tugasbesarkotlin5.pointofsales.View.ProductListRepository;

import java.util.ArrayList;

public class ProductListViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Product>> product;

    public void init (Context context){
        if (product != null ){
            return;
        }

        product = ProductListRepository.getInstance( context ).getProduct();
    }
    public LiveData<ArrayList<Product>> getProduct() {
        return product;
    }

}