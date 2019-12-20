package com.tugasbesarkotlin5.pointofsales.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.tugasbesarkotlin5.pointofsales.Model.User;
import com.tugasbesarkotlin5.pointofsales.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent exit = new Intent(Intent.ACTION_MAIN);
        exit.addCategory(Intent.CATEGORY_HOME);
        exit.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity(exit);
        finish();
    }
    FirebaseUser firebaseUser;
    TextView tv_date, tv_sales_by_date, tv_username;
    CardView btn_cashier, btn_linechart, btn_product, btn_achiev, btn_store;
    private DatabaseReference database;
    private ArrayList<Transaction> daftarTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        btn_cashier = findViewById( R.id.btn_cashier );
        btn_linechart = findViewById( R.id.btn_linechart );
        btn_product = findViewById( R.id.btn_product );
        btn_achiev = findViewById( R.id.btn_achiev );
        btn_store = findViewById( R.id.btn_store );
        tv_date = findViewById( R.id.tv_date );
        tv_sales_by_date = findViewById( R.id.tv_sales_by_date );
        tv_username = findViewById( R.id.tv_username );

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());

        database.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue( User.class );
                assert user != null;
                tv_username.setText( "Welcome back, "+user.getUsername() );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        Calendar calendar = Calendar.getInstance();
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(calendar.getTime());

        tv_date.setText( date );

        getSBD(date);

        btn_cashier.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( MainActivity.this, CashierActivity.class );
                startActivity( intent );
            }
        } );

        btn_linechart.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( MainActivity.this, HistoryActivity.class );
                startActivity( intent );
            }
        } );

        btn_product.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( MainActivity.this, ProductListActivity.class );
                startActivity( intent );
            }
        } );

        btn_achiev.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( MainActivity.this, AchievementActivity.class );
                startActivity( intent );
            }
        } );

        btn_store.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( MainActivity.this, StoreActivity.class );
                startActivity( intent );
            }
        } );

    }

    private void getSBD(String date) {
        database.child("Transaction").child( date ).addValueEventListener( new ValueEventListener() {
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

                Locale localeID = new Locale("in", "ID");
                NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
                tv_sales_by_date.setText(formatRupiah.format( (double) total_price ));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.main_menu, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity( new Intent( MainActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected( item );
        }

    }
}
