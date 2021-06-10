package com.example.project_messenger.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.project_messenger.R;
import com.example.project_messenger.databinding.ActivityProfileBinding;
import com.example.project_messenger.databinding.ActivitySettingBinding;
import com.example.project_messenger.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;
    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase database;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        String receiverUid = getIntent().getStringExtra("receiverUid");

        //set arrow back
        binding.returnArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });

        /* After clicked btnChange, use addListenerForSingleValueEvent to get data immediately*/
        database.getReference().child("Users").child(receiverUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue(Users.class);

                        Glide.with(ProfileActivity.this).load(users.getProfilePicture())
                                .placeholder(R.drawable.avatar)
                                .into(binding.profileImage);

                        binding.etBirthDay.setText(users.getBirthDay());
                        binding.etChangeUserName.setText(users.getUserName());
                        phone = users.getPhone();
                        binding.etPhone.setText(phone);
                        binding.etCity.setText(users.getCity());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent phoneCallIntent = new Intent(Intent.ACTION_DIAL);
               phoneCallIntent.setData(Uri.parse("tel:"+phone));
               startActivity(phoneCallIntent);
            }
        });

        binding.btnSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSMS = new Intent(Intent.ACTION_VIEW);
                intentSMS.setData(Uri.parse("sms:" + phone));
                intentSMS.putExtra("sms_body", "SMS");
                startActivity(intentSMS);
            }
        });
    }

}