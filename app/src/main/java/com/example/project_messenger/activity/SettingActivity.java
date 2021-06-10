package com.example.project_messenger.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.project_messenger.R;
import com.example.project_messenger.databinding.ActivitySettingBinding;
import com.example.project_messenger.model.Users;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class SettingActivity extends AppCompatActivity {

    ActivitySettingBinding binding;
    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        //set arrow back
        binding.returnArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //set date picker
        binding.etBirthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int y = c.get(Calendar.YEAR);
                int m = c.get(Calendar.MONTH);
                int d = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(SettingActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        binding.etBirthDay.setText(day + "/" + (month + 1) + "/" + year);
                    }
                }, y, m, d);
                dialog.show();
            }
        });

        //set button btnChange
        binding.btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String birthDay = binding.etBirthDay.getText().toString();
                String username = binding.etChangeUserName.getText().toString();
                String phone = binding.etPhone.getText().toString();
                String city = binding.spnCity.getSelectedItem().toString();


                HashMap<String, Object> obj = new HashMap<>();
                obj.put("userName", username);
                obj.put("birthDay", birthDay);
                obj.put("phone", phone);
                obj.put("city", city);

                database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                        .updateChildren(obj);

                Toast.makeText(SettingActivity.this, "Cập nhật thông tin cá nhân thành công",
                        Toast.LENGTH_LONG).show();
            }
        });


       /* After clicked btnChange, use addListenerForSingleValueEvent to get data immediately*/
        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue(Users.class);

                        Glide.with(SettingActivity.this).load(users.getProfilePicture())
                                .placeholder(R.drawable.avatar)
                                .into(binding.profileImage);

                        binding.etBirthDay.setText(users.getBirthDay());
                        binding.etChangeUserName.setText(users.getUserName());
                        binding.etPhone.setText(users.getPhone());

                        //Set Spinner City
                        List<String> cityList = new ArrayList<>();
                        cityList.add("Hà Nội");
                        cityList.add("Thái Bình");
                        cityList.add("Ninh bình");
                        cityList.add("Nghệ An");
                        ArrayAdapter arrayAdapter = new ArrayAdapter(SettingActivity.this, android.R.layout.simple_spinner_item, cityList);
                        binding.spnCity.setAdapter(arrayAdapter);
                        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                        binding.spnCity.setSelection(cityList.indexOf(users.getCity()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        //Click icon change avatar in SettingActivity
        binding.changeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);

                //Set Type of content is image, /* : all extension file
                intent.setType("image/*");
                startActivityForResult(intent, 25);
            }
        });
    }

    //Save image in Firebase Storage
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data.getData() != null){
            Uri sFile = data.getData();
            binding.profileImage.setImageURI(sFile);

            final StorageReference reference = storage.getReference().child("profile_pictures")
                    .child(FirebaseAuth.getInstance().getUid());
            reference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                                    .child("profilePicture").setValue(uri.toString());

                            Toast.makeText(SettingActivity.this, "Thay đổi avatar thành công",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }
            });
        }
    }
}
