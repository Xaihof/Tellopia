package com.xoksis.tellopia.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.xoksis.tellopia.R;
import com.xoksis.tellopia.models.User;
import com.xoksis.tellopia.databinding.ActivitySetupProfileBinding;

public class SetupProfileActivity extends AppCompatActivity {

    ActivitySetupProfileBinding activitySetupProfileBinding;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    Uri selectedImage;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySetupProfileBinding = ActivitySetupProfileBinding.inflate(getLayoutInflater());
        setContentView(activitySetupProfileBinding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Profile...");
        progressDialog.setCancelable(false);


        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();



        activitySetupProfileBinding.imageView.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent,45);

        });

        activitySetupProfileBinding.buttonContinue.setOnClickListener(view -> {
            String name = activitySetupProfileBinding.editTextNameBox.getText().toString();

            if (name.isEmpty()){
                activitySetupProfileBinding.editTextNameBox.setError("Please type a name",getDrawable(R.drawable.baseline_error_24));
            }

            progressDialog.show();
            if (selectedImage != null){
                StorageReference storageReference = firebaseStorage.getReference().child("Profiles").child(firebaseAuth.getUid());
                storageReference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()){
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();

                                    String uid = firebaseAuth.getUid();
                                    String phone = firebaseAuth.getCurrentUser().getPhoneNumber();
                                    String name = activitySetupProfileBinding.editTextNameBox.getText().toString();

                                    User user = new User(uid,name,phone,imageUrl);

                                    firebaseDatabase.getReference()
                                            .child("users")
                                            .child(uid)
                                            .setValue(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    progressDialog.dismiss();
                                                    Intent intent = new Intent(SetupProfileActivity.this,MainActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                }
                            });
                        }

                    }
                });
            }else {
                String uid = firebaseAuth.getUid();
                String phone = firebaseAuth.getCurrentUser().getPhoneNumber();

                User user = new User(uid,name,phone,"No Image");

                firebaseDatabase.getReference()
                        .child("users")
                        .child(uid)
                        .setValue(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                Intent intent = new Intent(SetupProfileActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((data!= null)){
            if (data.getData() != null){
                activitySetupProfileBinding.imageView.setImageURI(data.getData());
                selectedImage = data.getData();
            }
        }

    }
}