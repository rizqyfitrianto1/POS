package com.tugasbesarkotlin5.pointofsales.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tugasbesarkotlin5.pointofsales.Model.Store;
import com.tugasbesarkotlin5.pointofsales.Model.User;
import com.tugasbesarkotlin5.pointofsales.R;

public class StoreActivity extends AppCompatActivity {
    ImageView img_view;
    TextView tv_nama_toko,tv_telepon,tv_alamat;
    FirebaseUser firebaseUser;
    private DatabaseReference database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_store );

        img_view = findViewById( R.id.img_view );
        tv_nama_toko = findViewById( R.id.tv_nama_toko );
        tv_telepon = findViewById( R.id.tv_telepon );
        tv_alamat = findViewById( R.id.tv_alamat );

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());

        database.child( "Store" ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Store store = dataSnapshot.getValue( Store.class );
                assert store != null;
                tv_nama_toko.setText( store.getNama_toko() );
                tv_telepon.setText( store.getTelp_toko() );
                tv_alamat.setText( store.getAlamat() );
                Glide.with(getApplicationContext()).load(store.getLogo()).into(img_view);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.store_menu, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_edit:
                startActivity( new Intent( StoreActivity.this, EditStoreActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
            default:
                return super.onOptionsItemSelected( item );
        }
    }
}
