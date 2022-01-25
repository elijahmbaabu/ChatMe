package com.example.chatme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    EditText mgetPhoneNo;
    Button sentOTP;
    CountryCodePicker codePicker;
    ProgressBar progressBar;
    String countrycode;
    String phone;
    FirebaseAuth firebaseAuth;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    String sentcode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mgetPhoneNo = findViewById(R.id.phoneNumber);
        sentOTP = findViewById(R.id.SendOTP);
        codePicker = findViewById(R.id.countryCodePicker);
        progressBar = findViewById(R.id.progress_circular);

        firebaseAuth = FirebaseAuth.getInstance();
        countrycode = codePicker.getDefaultCountryCodeWithPlus();
        codePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countrycode = codePicker.getSelectedCountryCodeWithPlus();
            }
        });

        sentOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = mgetPhoneNo.getText().toString();
                if (number.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please Enter Your Number", Toast.LENGTH_SHORT).show();
                }
                else if (number.length()<10){
                    Toast.makeText(getApplicationContext(), "Please enter the correct phone number", Toast.LENGTH_SHORT).show();
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    phone = countrycode +number;

                    PhoneAuthOptions authOptions =PhoneAuthOptions.newBuilder(firebaseAuth)
                            .setPhoneNumber(phone)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(MainActivity.this)
                            .setCallbacks(callbacks)
                            .build();

                    PhoneAuthProvider.verifyPhoneNumber(authOptions);
                }
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                //how to automatically take the Verification code and verify the phone number.
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(getApplicationContext(), "Verification code sent.", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                sentcode = s;
                Intent intent = new Intent(MainActivity.this, OTP.class);
                intent.putExtra("otp", sentcode);
                startActivity(intent);

            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            Intent intent = new Intent(MainActivity.this, ChatArea.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}