package com.example.project_messenger.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.project_messenger.R;
import com.example.project_messenger.databinding.ActivitySignUpBinding;
import com.example.project_messenger.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        //Set progressDialog for click button "Đăng Ký"
        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Đang tạo Tài Khoản mới !");
        progressDialog.setMessage("Chúng tôi đang tạo Tài Khoản cho bạn");

        //Click button Sign Up
        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Validation UserName & Email & Password
                if(binding.etUserName.getText().toString().isEmpty()){
                    binding.etUserName.setError("Vui lòng nhập UserName");
                }
                if (binding.etEmail.getText().toString().isEmpty()) {
                    binding.etEmail.setError("Vui lòng nhập email");
                    return;
                }
                if (binding.etPassword.getText().toString().isEmpty()) {
                    binding.etPassword.setError("Vui lòng nhập password");
                    return;
                }

                //show progressDialog
                progressDialog.show();

                //Create new User with email and password
                auth.createUserWithEmailAndPassword(binding.etEmail.getText().toString(),
                        binding.etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull  Task<AuthResult> task) {

                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            String id = task.getResult().getUser().getUid();

                            Users user = new Users(binding.etUserName.getText().toString(),
                                    binding.etEmail.getText().toString(), binding.etPassword.getText().toString(), id);


                            database.getReference().child("Users").child(id).setValue(user);
                            Toast.makeText(SignUpActivity.this, "Tạo Tài Khoản mới thành công !!!",
                                    Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(SignUpActivity.this, "Tài khoản email này đã được đăng ký!!!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        binding.tvAlreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

    }
}