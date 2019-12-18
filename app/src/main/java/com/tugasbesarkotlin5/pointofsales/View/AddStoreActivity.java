package com.tugasbesarkotlin5.pointofsales.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tugasbesarkotlin5.pointofsales.Model.Store;
import com.tugasbesarkotlin5.pointofsales.Model.User;
import com.tugasbesarkotlin5.pointofsales.R;

import java.io.IOException;
import java.util.UUID;

import es.dmoral.toasty.Toasty;

public class AddStoreActivity extends AppCompatActivity {
    EditText  edt_nama_toko, edt_telepon, edt_alamat;
    Button btn_pilih_image, btn_save;
    ImageView img_view;
    String nama_toko, telepon, alamat, logo;
    DatabaseReference reference;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;
    FirebaseStorage storage;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_store );

        edt_nama_toko = findViewById( R.id.edt_nama_toko );
        edt_telepon = findViewById( R.id.edt_telepon );
        edt_alamat = findViewById( R.id.edt_alamat );
        btn_pilih_image = findViewById( R.id.btn_pilih_image );
        btn_save = findViewById( R.id.btn_save );
        img_view = findViewById( R.id.img_view );

        reference = FirebaseDatabase.getInstance().getReference("User");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        btn_pilih_image.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        } );

        btn_save.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nama_toko = edt_nama_toko.getText().toString();
                telepon = edt_telepon.getText().toString();
                alamat = edt_alamat.getText().toString();

                if (nama_toko.isEmpty() || telepon.isEmpty() || alamat.isEmpty() ){
                    Toasty.warning( AddStoreActivity.this, "Please fill all required", Toast.LENGTH_SHORT, true ).show();
                }else {

                                if (filePath != null) {

                                    final StorageReference ref = storageReference.child( "images/" + UUID.randomUUID().toString());
                                    ref.putFile(filePath).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                         @Override
                                          public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                                           {
                                               ref.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                                                   @Override
                                                   public void onSuccess(Uri uri) {
                                                       Toasty.success( AddStoreActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT,true).show();
                                                       logo = uri.toString();
                                                       saveStore(new Store( logo,nama_toko, telepon,alamat ));
                                                   }
                                               } );
                                           }
                                    })

                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e)
                                                {
                                                    Toasty.error( AddStoreActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT, true).show();
                                                }
                                            });
                                }
                }
            }
        } );
    }

    private void SelectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult( Intent.createChooser( intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap( getContentResolver(), filePath);
                img_view.setImageBitmap(bitmap);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveStore(Store store) {
        String userid = getIntent().getStringExtra( "userid" );
        reference.child( userid ).child( "Store" ).setValue(store).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toasty.success( getApplicationContext(),"Berhasil mendaftar", Toasty.LENGTH_SHORT, true ).show();
                Intent intent = new Intent( AddStoreActivity.this, MainActivity.class );
                startActivity( intent );
                finish();
            }
        });
    }
}
