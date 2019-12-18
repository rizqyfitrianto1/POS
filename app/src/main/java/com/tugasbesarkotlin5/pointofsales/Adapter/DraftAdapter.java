package com.tugasbesarkotlin5.pointofsales.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tugasbesarkotlin5.pointofsales.Model.Product;
import com.tugasbesarkotlin5.pointofsales.R;

import java.util.ArrayList;

public class DraftAdapter extends RecyclerView.Adapter<DraftAdapter.ViewHolder> {

    private DatabaseReference database;
    FirebaseUser firebaseUser;
    private ArrayList<Product> daftarBarang;
    private Context context;

    public DraftAdapter(ArrayList<Product> barangs, Context ctx){
        daftarBarang = barangs;
        context = ctx;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCode, tvName, tvPrice, tvCancel;

        ViewHolder(View v) {
            super(v);
            tvCode = (TextView) v.findViewById( R.id.tv_code);
            tvName = (TextView) v.findViewById(R.id.tv_name);
            tvPrice = (TextView) v.findViewById(R.id.tv_price);
            tvCancel = (TextView) v.findViewById(R.id.tv_cancel);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final String code = daftarBarang.get(position).getCode();
        final String name = daftarBarang.get(position).getName();
        final String price = daftarBarang.get(position).getPrice();
        holder.tvCode.setText(code);
        holder.tvName.setText(name);
        holder.tvPrice.setText(price);
        holder.tvCancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                database = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());
                if(database!=null){
                    database.child("Draft").child( code ).removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context,"success delete", Toast.LENGTH_LONG).show();
                    }
                });

                }
            }
        } );
    }

    @Override
    public int getItemCount() {
        return daftarBarang.size();
    }
}