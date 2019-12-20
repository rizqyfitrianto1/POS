package com.tugasbesarkotlin5.pointofsales.View;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tugasbesarkotlin5.pointofsales.Adapter.ReceiptAdapter;
import com.tugasbesarkotlin5.pointofsales.Model.Product;
import com.tugasbesarkotlin5.pointofsales.Model.Store;
import com.tugasbesarkotlin5.pointofsales.Model.Transaction;
import com.tugasbesarkotlin5.pointofsales.Model.User;
import com.tugasbesarkotlin5.pointofsales.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import es.dmoral.toasty.Toasty;

public class PembayaranFragment extends BottomSheetDialogFragment {
    LinearLayout line_cash,line_debit;
    Button btn_50, btn_100;
    TextView tv_total_price, tv_kembalian;
    TextView tv_total_price3, tv_total_tax, tv_total_price2,tv_receipt_date,tv_receipt_time,tv_receipt_id,id_card,bayar_cash,kembalian,tv_receipt_name_cashier;
    ImageView tv_store_logo;
    TextView tv_store_name, tv_store_address, tv_store_telp;
    CardView btn_debit, btn_cash;
    EditText card_number, edt_cash;
    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    View dialogView;
    private DatabaseReference database;
    FirebaseUser firebaseUser;
    private ArrayList<Product> daftarProduk;
    ReceiptAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate( R.layout.activity_pembayaran, container, false);
        final String total_harga = this.getArguments().getString("total_bayar");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());

        tv_total_price = v.findViewById( R.id.tv_total_price );
        tv_total_price.setText( total_harga );

        btn_debit = v.findViewById( R.id.btn_debit );
        btn_debit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFormDebit(total_harga);
            }
        } );

        btn_cash = v.findViewById( R.id.btn_cash );
        btn_cash.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFormCash(total_harga);
            }
        } );
        return v;
    }

    private void DialogFormDebit(final String total_harga) {
        dialog = new AlertDialog.Builder(getActivity());
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.form_debit, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("ID Card Number:");

        card_number  = (EditText) dialogView.findViewById(R.id.card_number);

        dialog.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String number_card = card_number.getText().toString();

                Calendar calendar = Calendar.getInstance();
                String pattern = "yyyy-MM-dd";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                String date = simpleDateFormat.format(calendar.getTime());

                String patternTime = "HH:mm:ss";
                SimpleDateFormat simpleTimeFormat = new SimpleDateFormat(patternTime);
                String time = simpleTimeFormat.format(calendar.getTime());

                String id = (date + time).replaceAll("[-:]", "" );

                saveTransactionDebit(new Transaction(id,total_harga,date,time, "Debit",number_card ));

                dialogPrintReceipt(date,time,number_card,id);

                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void dialogPrintReceipt(String date, String time, String number_card, String id) {
        dialog = new AlertDialog.Builder(getActivity());
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.lay_receipt, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        recyclerView = dialogView.findViewById(R.id.recyclerview);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        tv_receipt_date = dialogView.findViewById( R.id.tv_receipt_date );
        tv_receipt_time = dialogView.findViewById( R.id.tv_receipt_time );
        tv_receipt_id = dialogView.findViewById( R.id.tv_receipt_id );
        tv_total_price3 = dialogView.findViewById( R.id.tv_total_price3 );
        tv_total_tax = dialogView.findViewById( R.id.tv_total_tax );
        tv_total_price2 = dialogView.findViewById( R.id.tv_total_price2 );
        id_card = dialogView.findViewById( R.id.id_card );
        tv_receipt_name_cashier = dialogView.findViewById( R.id.tv_receipt_name_cashier );

        tv_store_name = dialogView.findViewById( R.id.tv_store_name );
        tv_store_address = dialogView.findViewById( R.id.tv_store_address );
        tv_store_telp = dialogView.findViewById( R.id.tv_store_telp );
        tv_store_logo = dialogView.findViewById( R.id.tv_store_logo );

        database.child( "Store" ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Store store = dataSnapshot.getValue( Store.class );
                assert store != null;
                tv_store_name.setText( store.getNama_toko() );
                tv_store_telp.setText( store.getTelp_toko() );
                tv_store_address.setText( store.getAlamat() );
                Glide.with(getActivity()).load(store.getLogo()).into(tv_store_logo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        database.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue( User.class );
                tv_receipt_name_cashier.setText( user.getUsername() );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        id_card.setText( number_card );
        tv_receipt_date.setText( date );
        tv_receipt_time.setText( time );
        tv_receipt_id.setText( id );

        database.child( "Draft" ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int total_price = 0;
                daftarProduk = new ArrayList<>();
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    Product product = noteDataSnapshot.getValue( Product.class );
                    daftarProduk.add( product );

                    int t_price = Integer.parseInt(product.getPrice());
                    total_price += t_price;
                }
                adapter = new ReceiptAdapter( daftarProduk, getActivity() );
                recyclerView.setAdapter( adapter );

                tv_total_price3.setText(String.valueOf( total_price ));

                Float tax = 0.1f;
                Float total_price2 = Float.valueOf( tv_total_price3.getText().toString() );
                Float hitung_tax = tax * total_price2;
                int tax2 = Math.round( hitung_tax );
                String tax3 = String.valueOf( tax2 );
                tv_total_tax.setText( tax3);

                Float total_semua = total_price2 + hitung_tax;
                int total_semua2 = Math.round( total_semua );
                String total_gross = String.valueOf( total_semua2 );
                tv_total_price2.setText( total_gross );

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );

        dialog.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(database!=null) {
                    database.child( "Draft" ).removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    } );
                }
                dismiss();
            }
        });

        dialog.show();
    }

    private void saveTransactionDebit(final Transaction transaction) {
        database.child("Transaction").child( transaction.getDate() ).push().setValue(transaction).addOnSuccessListener( getActivity(), new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toasty.info( getActivity(),"Printing Receipt ...", Toasty.LENGTH_LONG, true ).show();
            }
        });
    }

    private void DialogFormCash(final String total_harga) {
        dialog = new AlertDialog.Builder(getActivity());
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.form_cash, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Uang diterima:");

        edt_cash  = (EditText) dialogView.findViewById(R.id.edt_cash);
        btn_50  = (Button) dialogView.findViewById(R.id.btn_50);
        btn_100  = (Button) dialogView.findViewById(R.id.btn_100);

        btn_50.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edt_cash.setText( "50000" );
            }
        } );

        btn_100.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edt_cash.setText( "100000" );
            }
        } );

        dialog.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String cash = edt_cash.getText().toString();
                int bayar = Integer.valueOf( cash );
                int total = Integer.valueOf( total_harga );
                int kembalian = bayar - total;

                if (kembalian < 0){
                    Toasty.warning( getActivity(),"Masukkan nominal lebih besar", Toasty.LENGTH_SHORT, true ).show();
                    return;
                }else {
                    String total_kembalian = String.valueOf( kembalian );
                    String harga_total = String.valueOf( total_harga );

                    Calendar calendar = Calendar.getInstance();
                    String pattern = "yyyy-MM-dd";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    String date = simpleDateFormat.format(calendar.getTime());

                    String patternTime = "HH:mm:ss";
                    SimpleDateFormat simpleTimeFormat = new SimpleDateFormat(patternTime);
                    String time = simpleTimeFormat.format(calendar.getTime());

                    String id2 = (date + time).replaceAll("[-:]", "" );

                    saveTransactionCash(new Transaction(id2,harga_total,date,time, "Cash","" ));

                    dialogKembalian(total_kembalian,date,time,id2,cash);

                }
            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void saveTransactionCash(Transaction transaction) {
        database.child("Transaction").child( transaction.getDate() ).push().setValue(transaction).addOnSuccessListener(getActivity(), new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });
    }

    private void dialogKembalian(final String cash, final String date, final String time, final String id2, final String total_kembalian) {
        dialog = new AlertDialog.Builder(getActivity());
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.form_kembalian, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Kembalian:");
        tv_kembalian  = (TextView) dialogView.findViewById(R.id.tv_kembalian);
        tv_kembalian.setText( cash );
        dialog.setPositiveButton("OK",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                Toasty.info( getActivity(),"Printing Receipt ...", Toasty.LENGTH_LONG, true ).show();
                dialogPrintReceiptCash(date,time,total_kembalian,id2,cash);
                dialog.dismiss();
                }
            });
        dialog.show();
    }

    private void dialogPrintReceiptCash(String date, String time, String total_kembalian, String id2, String cash) {
        dialog = new AlertDialog.Builder(getActivity());
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.lay_receipt, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        recyclerView = dialogView.findViewById(R.id.recyclerview);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        line_cash = dialogView.findViewById( R.id.line_cash );
        line_debit = dialogView.findViewById( R.id.line_debit );
        tv_receipt_date = dialogView.findViewById( R.id.tv_receipt_date );
        tv_receipt_time = dialogView.findViewById( R.id.tv_receipt_time );
        tv_receipt_id = dialogView.findViewById( R.id.tv_receipt_id );
        tv_total_price3 = dialogView.findViewById( R.id.tv_total_price3 );
        tv_total_tax = dialogView.findViewById( R.id.tv_total_tax );
        tv_total_price2 = dialogView.findViewById( R.id.tv_total_price2 );
        bayar_cash = dialogView.findViewById( R.id.bayar_cash );
        kembalian = dialogView.findViewById( R.id.kembalian );
        tv_receipt_name_cashier = dialogView.findViewById( R.id.tv_receipt_name_cashier );

        line_cash.setVisibility( View.VISIBLE );
        line_debit.setVisibility( View.GONE );

        tv_store_name = dialogView.findViewById( R.id.tv_store_name );
        tv_store_address = dialogView.findViewById( R.id.tv_store_address );
        tv_store_telp = dialogView.findViewById( R.id.tv_store_telp );
        tv_store_logo = dialogView.findViewById( R.id.tv_store_logo );

        database.child( "Store" ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Store store = dataSnapshot.getValue( Store.class );
                assert store != null;
                tv_store_name.setText( store.getNama_toko() );
                tv_store_telp.setText( store.getTelp_toko() );
                tv_store_address.setText( store.getAlamat() );
                Glide.with(getActivity()).load(store.getLogo()).into(tv_store_logo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        database.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue( User.class );
                tv_receipt_name_cashier.setText( user.getUsername() );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        tv_receipt_date.setText( date );
        tv_receipt_time.setText( time );
        tv_receipt_id.setText( id2 );
        bayar_cash.setText( total_kembalian );
        kembalian.setText( cash );

        database.child( "Draft" ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int total_price = 0;
                daftarProduk = new ArrayList<>();
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    Product product = noteDataSnapshot.getValue( Product.class );
                    daftarProduk.add( product );

                    int t_price = Integer.parseInt(product.getPrice());
                    total_price += t_price;
                }
                adapter = new ReceiptAdapter( daftarProduk, getActivity() );
                recyclerView.setAdapter( adapter );

                tv_total_price3.setText(String.valueOf( total_price ));

                Float tax = 0.1f;
                Float total_price2 = Float.valueOf( tv_total_price3.getText().toString() );
                Float hitung_tax = tax * total_price2;
                int tax2 = Math.round( hitung_tax );
                String tax3 = String.valueOf( tax2 );
                tv_total_tax.setText( tax3);

                Float total_semua = total_price2 + hitung_tax;
                int total_semua2 = Math.round( total_semua );
                String total_gross = String.valueOf( total_semua2 );
                tv_total_price2.setText( total_gross );

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );

        dialog.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(database!=null) {
                    database.child( "Draft" ).removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    } );
                }
                dismiss();
            }
        });

        dialog.show();
    }

}
