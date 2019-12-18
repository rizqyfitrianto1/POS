package com.tugasbesarkotlin5.pointofsales.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tugasbesarkotlin5.pointofsales.Model.Product;
import com.tugasbesarkotlin5.pointofsales.R;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {
    FirebaseUser firebaseUser;
    DatabaseReference database;
    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null){
            database = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());
            database.child( "Store" ).addValueEventListener( new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        startActivity(new Intent( LoginActivity.this, MainActivity.class));
                    }else {
                        startActivity(new Intent( LoginActivity.this, AddStoreActivity.class).putExtra("userid", firebaseUser.getUid()));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            } );
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent exit = new Intent(Intent.ACTION_MAIN);
        exit.addCategory(Intent.CATEGORY_HOME);
        exit.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity(exit);
        finish();
    }

    EditText edt_email, edt_password;
    Button btn_login;
    TextView tv_regist;
    String email, password;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        edt_email = findViewById( R.id.edt_email );
        edt_password = findViewById( R.id.edt_password );
        btn_login = findViewById( R.id.btn_login );
        tv_regist = findViewById( R.id.tv_regist );

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        tv_regist.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent(LoginActivity.this, RegisterActivity.class) );
            }
        } );

        btn_login.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = edt_email.getText().toString();
                password = edt_password.getText().toString();

                if (email.isEmpty() || password.isEmpty()){
                    Toasty.warning( getApplicationContext(), "All field are required" ,Toasty.LENGTH_SHORT, true).show();
                }else {
                    cekDatabase(email,password);
                }
            }
        } );
    }

    private void cekDatabase(final String email, final String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toasty.error(LoginActivity.this, "Gagal login karena " + task.getException().getMessage(), Toast.LENGTH_LONG, true).show();
                        } else {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class).putExtra("email", email));
                            finish();
                        }
                    }
                });
    }
}
