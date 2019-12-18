package com.tugasbesarkotlin5.pointofsales.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.tugasbesarkotlin5.pointofsales.Model.Product;
import com.tugasbesarkotlin5.pointofsales.R;

import java.util.ArrayList;
public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ViewHolder> {

    private DatabaseReference database;
    private ArrayList<Product> daftarBarang;
    private Context context;

    public ReceiptAdapter(ArrayList<Product> barangs, Context ctx){
        daftarBarang = barangs;
        context = ctx;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNo, tvCode, tvName, tvPrice;

        ViewHolder(View v) {
            super(v);
            tvNo = (TextView) v.findViewById(R.id.tv_no);
            tvCode = (TextView) v.findViewById( R.id.tv_code);
            tvName = (TextView) v.findViewById(R.id.tv_name);
            tvPrice = (TextView) v.findViewById(R.id.tv_price);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_product, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final String code = daftarBarang.get(position).getCode();
        final String name = daftarBarang.get(position).getName();
        final String price = daftarBarang.get(position).getPrice();
        holder.tvNo.setText(String.valueOf(position+1));
        holder.tvCode.setText(code);
        holder.tvName.setText(name);
        holder.tvPrice.setText(price);
    }

    @Override
    public int getItemCount() {
        return daftarBarang.size();
    }
}
