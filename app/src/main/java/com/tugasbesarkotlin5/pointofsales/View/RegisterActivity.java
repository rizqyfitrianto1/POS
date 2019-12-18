package com.tugasbesarkotlin5.pointofsales.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tugasbesarkotlin5.pointofsales.R;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity {
    EditText edt_username, edt_email, edt_password;
    Button btn_regist;
    String username, email, password;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_register );
        edt_username = findViewById( R.id.edt_username );
        edt_email = findViewById( R.id.edt_email );
        edt_password = findViewById( R.id.edt_password );
        btn_regist = findViewById( R.id.btn_regist );

        reference = FirebaseDatabase.getInstance().getReference("User");
        firebaseAuth = FirebaseAuth.getInstance();
        
        btn_regist.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = edt_username.getText().toString();
                email = edt_email.getText().toString();
                password = edt_password.getText().toString();

                if (username.isEmpty() || email.isEmpty() || password.isEmpty() ){
                    Toasty.warning( RegisterActivity.this, "Please fill all required", Toast.LENGTH_SHORT, true ).show();
                }else if (password.length() < 6){
                    Toasty.warning( RegisterActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT, true ).show();
                }else{
                    saveUser(username,email,password);
                }
            }
        } );
    }

    private void saveUser(final String username, final String email, final String password) {
        firebaseAuth.createUserWithEmailAndPassword( email, password )
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){
                            Toasty.error(RegisterActivity.this, "Register gagal karena "+ task.getException().getMessage(), Toast.LENGTH_LONG,true).show();
                        }else {
                            firebaseUser = firebaseAuth.getCurrentUser();

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("username", username);
                            hashMap.put("email", email);
                            hashMap.put("password", password);

                            firebaseUser = firebaseAuth.getCurrentUser();
                            final String userid = firebaseUser.getUid();

                            reference.child( userid ).setValue(hashMap)
                                    .addOnCompleteListener( RegisterActivity.this, new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (!task.isSuccessful()) {
                                                        Toast.makeText(RegisterActivity.this,
                                                                "Gagal login karena " + task.getException().getMessage()
                                                                , Toast.LENGTH_LONG).show();
                                                    } else {
                                                        startActivity(new Intent(RegisterActivity.this, AddStoreActivity.class)
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
