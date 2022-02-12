package com.example.chatme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class SavedProfile extends AppCompatActivity {

    EditText displayUsername;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    TextView moveToUpdateProfile;
    FirebaseFirestore firebaseFirestore;
    ImageView displayUserImage;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    private String imageURIAccessToken;
    androidx.appcompat.widget.Toolbar viewProfileToolbar;
    ImageButton displayProfileBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savedprofile);

        displayUsername = findViewById(R.id.viewuserName);
        moveToUpdateProfile = findViewById(R.id.redirectupdateprofile);
        displayUserImage = findViewById(R.id.viewImage);
        displayProfileBack = findViewById(R.id.backsavedprofile);
        viewProfileToolbar = findViewById(R.id.toolbarsavedprofile);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        storageReference = firebaseStorage.getReference();

        setSupportActionBar(viewProfileToolbar);

        displayProfileBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    finish();
            }
        });

        storageReference.child("images").child(firebaseAuth.getUid()).child("SavedProfile Picture").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imageURIAccessToken = uri.toString();
                Picasso.get().load(uri).into(displayUserImage);
            }
        });
        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserProfileDetails userProfileDetails = snapshot.getValue(UserProfileDetails.class);
                displayUsername.setText(userProfileDetails.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SavedProfile.this, "Failed to Fetch the User Details", Toast.LENGTH_SHORT).show();

            }
        });

        moveToUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SavedProfile.this, UpdateProfile.class));
            }
        });






    }
}