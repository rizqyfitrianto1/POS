package com.tugasbesarkotlin5.pointofsales.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tugasbesarkotlin5.pointofsales.Model.Product;
import com.tugasbesarkotlin5.pointofsales.R;

import java.util.ArrayList;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductViewHolder> {

    private final ArrayList<Product> list;

    public ProductListAdapter(ArrayList<Product> list) {
        this.list = list;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate( R.layout.item_list_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, final int position) {
        holder.bind(list.get(position));
        holder.tv_no.setText(String.valueOf(position+1));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView tv_no;
        TextView tv_name;
        TextView tv_code;
        TextView tv_price;

        public ProductViewHolder(View itemView) {
            super(itemView);
            tv_no = itemView.findViewById(R.id.tv_no);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_code = itemView.findViewById(R.id.tv_code);
            tv_price = itemView.findViewById(R.id.tv_price);
        }

        public void bind(Product product) {
//            Glide.with(itemView.getContext()).load(product.getImageUrl()).into(image);
            tv_name.setText(product.getName());
            tv_code.setText(product.getCode());
            tv_price.setText(product.getPrice());
        }
    }
}
