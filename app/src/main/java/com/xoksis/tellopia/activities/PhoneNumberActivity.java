package com.xoksis.tellopia.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.xoksis.tellopia.databinding.ActivityPhoneNumberBinding;

public class PhoneNumberActivity extends AppCompatActivity {

    ActivityPhoneNumberBinding activityPhoneNumberBinding;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPhoneNumberBinding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(activityPhoneNumberBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser()!=null){
            Intent intent = new Intent(PhoneNumberActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        activityPhoneNumberBinding.editTextPhone.requestFocus();

        activityPhoneNumberBinding.buttonContinue.setOnClickListener(view -> {

            Intent intent = new Intent(PhoneNumberActivity.this,OTPActivity.class);
            intent.putExtra("phoneNumber",activityPhoneNumberBinding.editTextPhone.getText().toString());
            startActivity(intent);

        });

    }
}