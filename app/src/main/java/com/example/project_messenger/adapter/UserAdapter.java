package com.example.project_messenger.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project_messenger.R;
import com.example.project_messenger.activity.ChatActivity;
import com.example.project_messenger.databinding.RowConversationBinding;
import com.example.project_messenger.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.userViewHolder>{

    Context context;
    ArrayList<Users> users;

    public UserAdapter() {
    }

    public UserAdapter(Context context, ArrayList<Users> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public userViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_conversation, parent, false);
        return new userViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  UserAdapter.userViewHolder holder, int position) {

        //Get position of user who we want to send message
        Users user= users.get(position);

        //senderId : user send Message
        String senderId = FirebaseAuth.getInstance().getUid();

        //senderRoom: id of user send message + id of user receive message
        String senderRoom = senderId + user.getUserId();

        //Check has message between 2 user
        FirebaseDatabase.getInstance().getReference().child("Chats").child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                        if(snapshot.exists()){
                            String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                            long time = snapshot.child("lastMsgTime").getValue(Long.class);
                            SimpleDateFormat dateFormat = new SimpleDateFormat(" dd/MM/yyyy hh:mm a");
                            holder.binding.msgTime.setText(dateFormat.format(new Date(time)));
                            holder.binding.lastMessage.setText(lastMsg);
                        } else{
                            holder.binding.lastMessage.setText("Nhấn để chat");
                            holder.binding.msgTime.setText("");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

        //Set userName and image of user
        holder.binding.userName.setText(user.getUserName());
        Glide.with(context).load(user.getProfilePicture())
                .placeholder(R.drawable.avatar)
                .into(holder.binding.avatar);

        //Click to Chat
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("userNameChat", user.getUserName());
                intent.putExtra("profilePicture", user.getProfilePicture());
                intent.putExtra("userId", user.getUserId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class userViewHolder extends RecyclerView.ViewHolder{
        RowConversationBinding binding;
        public userViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowConversationBinding.bind(itemView);
        }
    }
}
