package com.tugasbesarkotlin5.pointofsales.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tugasbesarkotlin5.pointofsales.Adapter.TransactionAdapter;
import com.tugasbesarkotlin5.pointofsales.Model.Transaction;
import com.tugasbesarkotlin5.pointofsales.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    TextView date,tv_total_price,tv_total_tax,tv_total_nett;
    ImageView pick_date;
    TransactionAdapter adapter;
    RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference database;
    private ArrayList<Transaction> daftarTransaction;
    FirebaseUser firebaseUser;
    String datepicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_history );

        date = findViewById( R.id.date );
        pick_date = findViewById( R.id.pick_date );
        tv_total_price = findViewById( R.id.tv_total_price );
        tv_total_tax = findViewById( R.id.tv_total_tax );
        tv_total_nett = findViewById( R.id.tv_total_nett );
        recyclerView = findViewById( R.id.recyclerview );

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        Calendar calendar = Calendar.getInstance();
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        datepicker = simpleDateFormat.format(calendar.getTime());
        date.setText( datepicker );

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());

        database.child("Transaction").child( datepicker ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int total_price = 0;
                daftarTransaction = new ArrayList<>();
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    Transaction transaction = noteDataSnapshot.getValue( Transaction.class );
                    daftarTransaction.add( transaction );

                    assert transaction != null;
                    int t_price = Integer.parseInt(transaction.getTotal_price());
                    total_price += t_price;
                }
                adapter = new TransactionAdapter( daftarTransaction, HistoryActivity.this );
                recyclerView.setAdapter( adapter );

                Locale localeID = new Locale("in", "ID");
                NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
                tv_total_price.setText(formatRupiah.format( (double) total_price ));

                Float nett = 1.1f;
                Float total_nett = total_price / nett;
                int n = Math.round( total_nett );
                String total_nett2 = String.valueOf( n );
                tv_total_nett.setText( formatRupiah.format( (double) n ));

                Float total_price2 = (float) total_price;
                Float hitung_tax = total_price2 - n;
                int tax2 = Math.round( hitung_tax );
                String tax3 = String.valueOf( tax2 );
                tv_total_tax.setText( formatRupiah.format( (double) tax2 ));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        pick_date.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get( Calendar.DAY_OF_MONTH );
                int month = calendar.get( Calendar.MONTH );
                int year = calendar.get( Calendar.YEAR );

                DatePickerDialog dpd = new DatePickerDialog( HistoryActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                        date.setText( mYear + "-" + (mMonth+1) + "-" + mDay );

                        datepicker = date.getText().toString();
                        database.child("Transaction").child( datepicker ).addValueEventListener( new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int total_price = 0;
                                daftarTransaction = new ArrayList<>();
                                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                                    Transaction transaction = noteDataSnapshot.getValue( Transaction.class );
                                    daftarTransaction.add( transaction );

                                    assert transaction != null;
                                    int t_price = Integer.parseInt(transaction.getTotal_price());
                                    total_price += t_price;
                                }
                                adapter = new TransactionAdapter( daftarTransaction, HistoryActivity.this );
                                recyclerView.setAdapter( adapter );

                                Locale localeID = new Locale("in", "ID");
                                NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
                                tv_total_price.setText(formatRupiah.format( (double) total_price ));

                                Float nett = 1.1f;
                                Float total_nett = total_price / nett;
                                int n = Math.round( total_nett );
                                String total_nett2 = String.valueOf( n );
                                tv_total_nett.setText( formatRupiah.format( (double) n ));

                                Float total_price2 = (float) total_price;
                                Float hitung_tax = total_price2 - n;
                                int tax2 = Math.round( hitung_tax );
                                String tax3 = String.valueOf( tax2 );
                                tv_total_tax.setText( formatRupiah.format( (double) tax2 ));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        } );
                    }
                }, year,month,day);
                dpd.show();
            }
        } );



    }
}
