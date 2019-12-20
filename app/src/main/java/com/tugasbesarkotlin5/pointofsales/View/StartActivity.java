package com.tugasbesarkotlin5.pointofsales.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.tugasbesarkotlin5.pointofsales.R;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class StartActivity extends AppCompatActivity {
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
                        startActivity(new Intent( StartActivity.this, MainActivity.class));
                    }else {
                        startActivity(new Intent( StartActivity.this, AddStoreActivity.class).putExtra("userid", firebaseUser.getUid()));
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
    TextView tv_regist, tv_login;
    LinearLayout lin_login, lin_regist;
    FirebaseAuth firebaseAuth;
    EditText edt_username, edt_email_regist, edt_password_regist;
    Button btn_regist;
    String username, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_start );

        edt_email = findViewById( R.id.edt_email );
        edt_password = findViewById( R.id.edt_password );
        btn_login = findViewById( R.id.btn_login );
        tv_regist = findViewById( R.id.tv_regist );
        tv_login = findViewById( R.id.tv_login );
        edt_username = findViewById( R.id.edt_username );
        edt_email_regist = findViewById( R.id.edt_email_regist );
        edt_password_regist = findViewById( R.id.edt_password_regist );
        btn_regist = findViewById( R.id.btn_regist );
        lin_login = findViewById( R.id.lin_login );
        lin_regist = findViewById( R.id.lin_regist );

        tv_login.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lin_login.setVisibility( View.VISIBLE );
                lin_regist.setVisibility( View.GONE );
            }
        } );

        tv_regist.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lin_login.setVisibility( View.GONE );
                lin_regist.setVisibility( View.VISIBLE );
            }
        } );

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

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

        database = FirebaseDatabase.getInstance().getReference("User");
        firebaseAuth = FirebaseAuth.getInstance();

        btn_regist.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = edt_username.getText().toString();
                email = edt_email_regist.getText().toString();
                password = edt_password_regist.getText().toString();

                if (username.isEmpty() || email.isEmpty() || password.isEmpty() ){
                    Toasty.warning( StartActivity.this, "Please fill all required", Toast.LENGTH_SHORT, true ).show();
                }else if (password.length() < 6){
                    Toasty.warning( StartActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT, true ).show();
                }else{
                    saveUser(username,email,password);
                }
            }
        } );
    }

    private void cekDatabase(final String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(StartActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toasty.error(StartActivity.this, "Gagal login karena " + task.getException().getMessage(), Toast.LENGTH_LONG, true).show();
                        } else {
                            startActivity(new Intent(StartActivity.this, MainActivity.class).putExtra("email", email));
                            finish();
                        }
                    }
                });
    }

    private void saveUser(final String username, final String email, final String password) {
        firebaseAuth.createUserWithEmailAndPassword( email, password )
                .addOnCompleteListener(StartActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){
                            Toasty.error(StartActivity.this, "Register gagal karena "+ task.getException().getMessage(), Toast.LENGTH_LONG,true).show();
                        }else {
                            firebaseUser = firebaseAuth.getCurrentUser();

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("username", username);
                            hashMap.put("email", email);
                            hashMap.put("password", password);

                            firebaseUser = firebaseAuth.getCurrentUser();
                            final String userid = firebaseUser.getUid();

                            database.child( userid ).setValue(hashMap)
                                    .addOnCompleteListener( StartActivity.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(StartActivity.this,
                                                        "Gagal login karena " + task.getException().getMessage()
                                                        , Toast.LENGTH_LONG).show();
                                            } else {
                                                startActivity(new Intent(StartActivity.this, AddStoreActivity.class)
                                                        .putExtra("userid", userid));
                                                finish();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}
