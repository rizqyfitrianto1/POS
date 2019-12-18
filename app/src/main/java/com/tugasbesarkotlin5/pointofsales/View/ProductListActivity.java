package com.tugasbesarkotlin5.pointofsales.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.tugasbesarkotlin5.pointofsales.Adapter.ProductListAdapter;
import com.tugasbesarkotlin5.pointofsales.Interface.DataLoadListener;
import com.tugasbesarkotlin5.pointofsales.Interface.Potrait;
import com.tugasbesarkotlin5.pointofsales.Model.Product;
import com.tugasbesarkotlin5.pointofsales.Model.Transaction;
import com.tugasbesarkotlin5.pointofsales.R;
import com.tugasbesarkotlin5.pointofsales.ViewModel.ProductListViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import es.dmoral.toasty.Toasty;

public class ProductListActivity extends AppCompatActivity implements DataLoadListener {

    private RecyclerView recyclerView;
    private ProductListAdapter adapter;
    private ProductListViewModel productListViewModel;
    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    View dialogView;

    ImageView btn_scan;
    TextView tv_show_code;
    EditText edt_name, edt_price;
    String code, name, price;
    private DatabaseReference database;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_product_list );
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize( true );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference().child( "User" ).child(firebaseUser.getUid());

        productListViewModel = ViewModelProviders.of( ProductListActivity.this ).get( ProductListViewModel.class );
        productListViewModel.init( ProductListActivity.this );

        adapter = new ProductListAdapter( productListViewModel.getProduct().getValue() );

        recyclerView.setAdapter( adapter );
    }

    @Override
    public void onProductLoaded() {
        productListViewModel.getProduct().observe( this, new Observer<ArrayList<Product>>() {
            @Override
            public void onChanged(ArrayList<Product> products) {
                adapter.notifyDataSetChanged();
            }
        } );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.product_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.action_add_product){
            dialogAdd();
        }

        return true;
    }

    private void dialogAdd() {
        dialog = new AlertDialog.Builder( ProductListActivity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.activity_add_product, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Add Product");

        btn_scan = dialogView.findViewById( R.id.btn_scan );
        tv_show_code = dialogView.findViewById( R.id.tv_show_code );
        edt_name = dialogView.findViewById( R.id.edt_name );
        edt_price = dialogView.findViewById( R.id.edt_price );

        btn_scan.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanow();
            }
        } );

        dialog.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                code = tv_show_code.getText().toString();
                name = edt_name.getText().toString();
                price = edt_price.getText().toString();

                if (name.isEmpty() && price.isEmpty()){
                    Toasty.warning( getApplicationContext(),"Data harus diisi semua", Toasty.LENGTH_SHORT, true ).show();
                }else {
                    addProduct(new Product( code,name,price ));
                }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult( requestCode,resultCode,data );
        if (result != null){
            if (result.getContents() == null){
                Toasty.info( this,"Result Not Found", Toasty.LENGTH_SHORT, true ).show();
            }else {
                tv_show_code.setText( result.getContents() );
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

    private void addProduct(Product product) {
        database.child("Product").child(code).setValue(product).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toasty.success( getApplicationContext(),"Data berhasil ditambahkan", Toasty.LENGTH_SHORT, true ).show();
                tv_show_code.setText("");
                edt_name.setText("");
                edt_price.setText("");
            }
        });
    }
}
