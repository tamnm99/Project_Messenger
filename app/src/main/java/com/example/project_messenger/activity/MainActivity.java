package com.example.project_messenger.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.example.project_messenger.R;
import com.example.project_messenger.adapter.UserAdapter;
import com.example.project_messenger.databinding.ActivityMainBinding;
import com.example.project_messenger.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseDatabase database;
    ArrayList<Users> users;
    UserAdapter userAdapter;
    ProgressDialog dialog;
    Users user;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Set dialog for up image story
        dialog = new ProgressDialog(this);
        dialog.setMessage("Đang cập nhật hình ảnh...!");
        dialog.setCancelable(false);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        users = new ArrayList<>();

        //Read data from Firebase database
        // user: user is session logging in
        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        user = snapshot.getValue(Users.class);
                    }
                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    }
                });


        //Set User Adapter
        userAdapter = new UserAdapter(this, users);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(userAdapter);

        binding.recyclerView.showShimmerAdapter();

        //Read child Users from database
        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Users user = snapshot1.getValue(Users.class);

                    //Add all user from database except user is logging in
                    if (!user.getUserId().equals(FirebaseAuth.getInstance().getUid())) {
                        users.add(user);
                    }
                    binding.recyclerView.hideShimmerAdapter();
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        /*binding.bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mNews:
                        Intent intentNews = new Intent(MainActivity.this, NewsActivity.class);
                        startActivity(intentNews);
                        break;

                    default:
                        break;
                }
            }
        });*/

        //Set click on Float Button
        /*binding.floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 75);
            }
        });*/
    }




    //Override method to menu top
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top, menu);

        //Click icon Search in menu top
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                processSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                processSearch(s);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    //Process Search User
    private void processSearch(String s) {
        ArrayList<Users> searchList = new ArrayList<>();
        for (Users object : users) {
            if (object.getUserName().toLowerCase().contains(s.toLowerCase())) {
                searchList.add(object);
            }
        }
        UserAdapter searchUserAdapter = new UserAdapter(this, searchList);
        binding.recyclerView.setAdapter(searchUserAdapter);
    }

    //If user is online
    @Override
    protected void onResume() {
        super.onResume();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("Presence").child(currentId).setValue("Online");
    }

    //Click Menu top
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                Intent intentSetting = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intentSetting);
                break;

            case R.id.logOut:
                String currentId = FirebaseAuth.getInstance().getUid();
                database.getReference().child("Presence").child(currentId).setValue("Offline");
                auth.signOut();
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}