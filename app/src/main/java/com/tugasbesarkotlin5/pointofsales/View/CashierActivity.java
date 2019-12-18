package com.tugasbesarkotlin5.pointofsales.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.tugasbesarkotlin5.pointofsales.Adapter.DraftAdapter;
import com.tugasbesarkotlin5.pointofsales.Interface.Potrait;
import com.tugasbesarkotlin5.pointofsales.Model.Product;
import com.tugasbesarkotlin5.pointofsales.R;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class CashierActivity extends AppCompatActivity {
    ImageView btn_scan;
    Button btn_delete_all, btn_settle;
    String code, name, price;
    TextView tv_total_price, tv_total_tax, tv_total_price2;

    DraftAdapter adapter;
    FirebaseUser firebaseUser;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference database;
    private ArrayList<Product> daftarProduk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_cashier );

        btn_scan = findViewById( R.id.btn_scan );
        btn_delete_all = findViewById( R.id.btn_delete_all );
        btn_settle = findViewById( R.id.btn_settle );
        recyclerView = findViewById(R.id.recyclerview);
        tv_total_price = findViewById( R.id.tv_total_price );
        tv_total_tax = findViewById( R.id.tv_total_tax );
        tv_total_price2 = findViewById( R.id.tv_total_price2 );

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference().child( "User" ).child(firebaseUser.getUid());

        btn_scan.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanow();
            }
        } );

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        tampilData();

        btn_delete_all.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAll();
            }
        } );

        btn_settle.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settle();
            }
        } );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult( requestCode,resultCode,data );
        if (result != null){
            if (result.getContents() == null){
                Toasty.info( this,"Result Not Found", Toasty.LENGTH_SHORT, true ).show();
            }else {
                code = result.getContents();

                checkDatabase(code);
            }
        }else {
            super.onActivityResult( requestCode,resultCode,data );
        }
    }

    private void scanow() {
        IntentIntegrator integrator = new IntentIntegrator( this );
        integrator.setCaptureActivity( Potrait.class );
        integrator.setOrientationLocked( false );
        integrator.setDesiredBarcodeFormats( IntentIntegrator.ALL_CODE_TYPES );
        integrator.setPrompt( "Scan Your Barcode" );
        integrator.initiateScan();
    }

    private void checkDatabase(final String code) {
        database.child( "Product" ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child( code ).exists()){
                    Product product = dataSnapshot.child(code).getValue(Product.class);
                    product.setCode(code);
                    name = product.getName();
                    price = product.getPrice();

                    saveDraft(new Product( code,name,price ));
                }else {
                    Toasty.error( getApplicationContext(),"Product tidak terdaftar di Database", Toasty.LENGTH_SHORT, true ).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

    }

    private void saveDraft(final Product product) {
        database.child("Draft").child( code ).setValue(product).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });
    }

    private void tampilData() {
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
                    adapter = new DraftAdapter( daftarProduk, CashierActivity.this );
                    recyclerView.setAdapter( adapter );

                    tv_total_price.setText(String.valueOf( total_price ));

                    Float tax = 0.1f;
                    Float total_price2 = Float.valueOf( tv_total_price.getText().toString() );
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
    }

    private void deleteAll() {
        if(database!=null) {
            database.child( "Draft" ).removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toasty.success( getApplicationContext(),"Success Delete", Toasty.LENGTH_SHORT, true ).show();
                }
            } );
        }
    }

    private void settle(){
        if (tv_total_price2.getText().toString().equals( "0" )){
            return;
        }else {
            String total_bayar = tv_total_price2.getText().toString();
            Intent intent = new Intent( CashierActivity.this, PembayaranActivity.class );
            intent.putExtra( "total_bayar", total_bayar );
            startActivity( intent );
        }
    }
}
