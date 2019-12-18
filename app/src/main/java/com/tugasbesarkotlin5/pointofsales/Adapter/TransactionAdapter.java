package com.tugasbesarkotlin5.pointofsales.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.tugasbesarkotlin5.pointofsales.Model.Transaction;
import com.tugasbesarkotlin5.pointofsales.R;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private ArrayList<Transaction> daftarBarang;
    private Context context;

    public TransactionAdapter(ArrayList<Transaction> barangs, Context ctx){
        daftarBarang = barangs;
        context = ctx;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvPrice, tvPayment, tvCardNumber;

        ViewHolder(View v) {
            super(v);
            tvTime = (TextView) v.findViewById( R.id.tv_time);
            tvPrice = (TextView) v.findViewById(R.id.tv_price);
            tvPayment = (TextView) v.findViewById(R.id.tv_payment);
            tvCardNumber = (TextView) v.findViewById(R.id.tv_card_number);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final String time = daftarBarang.get(position).getTime();
        final String price = daftarBarang.get(position).getTotal_price();
        final String payment = daftarBarang.get(position).getPayment();
        final String card_number = daftarBarang.get(position).getCard_number();
        holder.tvTime.setText(time);
        holder.tvPrice.setText(price);
        holder.tvPayment.setText(payment);
        holder.tvCardNumber.setText(card_number);
    }

    @Override
    public int getItemCount() {
        return daftarBarang.size();
    }
}
