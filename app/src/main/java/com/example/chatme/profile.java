package com.example.chatme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

public class profile extends AppCompatActivity {

    private CardView imagecard;
    private ImageView imageView;
    private static int PICK_IMAGE = 123;
    private Uri imagepath;

    private EditText getUsername;
    private Button saveProfile;

    private FirebaseAuth firebaseAuth;
    private String name;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private String getImageUriToken;
    private FirebaseFirestore firebaseFirestore;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore =FirebaseFirestore.getInstance();

        getUsername = findViewById(R.id.userName);
        imageView = findViewById(R.id.image);
        imagecard = findViewById(R.id.userImage);
        saveProfile = findViewById(R.id.saveprofile);
        progressBar = findViewById(R.id.save_progress);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE);

            }
        });

        saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = getUsername.getText().toString();
                if (name.isEmpty()){
                    Toast.makeText(getApplicationContext(), "UserName Missing", Toast.LENGTH_SHORT).show();
                }
                else if (imagepath==null){
                    Toast.makeText(getApplicationContext(), "Image Required", Toast.LENGTH_SHORT).show();
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    sendDataForNewUser();
                    progressBar.setVisibility(View.INVISIBLE);
                    startActivity(new Intent(profile.this, ChatArea.class));
                    finish();
                }
            }
        });

    }

    private void sendDataForNewUser(){
        SendToRealTimeDatabase();
    }
    private void SendToRealTimeDatabase(){

        name = getUsername.getText().toString().trim();
        FirebaseDatabase firebaseDatabase =  FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        UserProfileDetails userProfile = new UserProfileDetails(name, firebaseAuth.getUid());
        databaseReference.setValue(userProfile);
        Toast.makeText(getApplicationContext(), "user Profile Added successfully", Toast.LENGTH_SHORT).show();

        sendImagetoDatabase();

    }
    private void sendImagetoDatabase(){
        // compress image
        StorageReference imageref = storageReference.child("images").child(firebaseAuth.getUid()).child("Profile Picture");
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagepath);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

//        storing the image
        UploadTask uploadTask = imageref.putBytes(data);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        getImageUriToken = uri.toString();
                        Toast.makeText(getApplicationContext(), "URI GET SUCCESSFUL", Toast.LENGTH_SHORT).show();
                        sendDataToCloudFireStore();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "URI GET FAILED", Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(getApplicationContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Image not uploaded", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendDataToCloudFireStore() {
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        Map<String, Object> userdata=new HashMap<>();
        userdata.put("name", name);
        userdata.put("Image", getImageUriToken);
        userdata.put("UID", firebaseAuth.getUid());
        userdata.put("Status","Online");

        documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Data sent on cloud Firestore successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode==PICK_IMAGE && resultCode==RESULT_OK){
            imagepath = data.getData();
            imageView.setImageURI(imagepath);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}