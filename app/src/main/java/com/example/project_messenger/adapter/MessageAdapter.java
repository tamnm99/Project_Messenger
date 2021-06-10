package com.example.project_messenger.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project_messenger.R;
import com.example.project_messenger.databinding.ItemReceiveBinding;
import com.example.project_messenger.databinding.ItemSendBinding;
import com.example.project_messenger.model.Message;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter {


    Context context;
    ArrayList<Message> messages;

    final int ITEM_SENT = 1;
    final int ITEM_RECEIVE = 2;

    String senderRoom;
    String receiverRoom;

    public MessageAdapter(Context context, ArrayList<Message> messages, String senderRoom, String receiverRoom) {
        this.context = context;
        this.messages = messages;
        this.senderRoom = senderRoom;
        this.receiverRoom = receiverRoom;
    }

    //Init View
    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_send, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_receive, parent, false);
            return new ReceiveViewHolder(view);
        }
    }

    //Determine which of viewType, Sent Or Receive
    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (FirebaseAuth.getInstance().getUid().equals(message.getSenderId())) {
            return ITEM_SENT;
        } else {
            return ITEM_RECEIVE;
        }
    }

    //Click to view
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Message message = messages.get(position);

        //Array integer reactions
        int reactions[] = new int[]{
                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry
        };

        //Config for reaction
        ReactionsConfig config = new ReactionsConfigBuilder(context).withReactions(reactions).build();

        //Show popup
        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {

            //Choose set feeling in message send or message receive
            if(pos <0 || pos > 5){
                if (holder.getClass() == SentViewHolder.class) {
                    SentViewHolder viewHolder = (SentViewHolder) holder;
                    viewHolder.binding.feeling.setVisibility(View.GONE);
                }
                else {
                    ReceiveViewHolder viewHolder = (ReceiveViewHolder) holder;
                    viewHolder.binding.feeling.setVisibility(View.GONE);
                }
            }
            else{
                if (holder.getClass() == SentViewHolder.class) {
                    SentViewHolder viewHolder = (SentViewHolder) holder;
                    viewHolder.binding.feeling.setImageResource(reactions[pos]);
                    viewHolder.binding.feeling.setVisibility(View.VISIBLE);
                }
                else {
                    ReceiveViewHolder viewHolder = (ReceiveViewHolder) holder;
                    viewHolder.binding.feeling.setImageResource(reactions[pos]);
                    viewHolder.binding.feeling.setVisibility(View.VISIBLE);
                }
            }


            message.setFeeling(pos);

            //Save to database in both senderRoom and receiverRoom
            FirebaseDatabase.getInstance().getReference()
                    .child("Chats")
                    .child(senderRoom)
                    .child("messages")
                    .child(message.getMessageId()).setValue(message);
            FirebaseDatabase.getInstance().getReference()
                    .child("Chats")
                    .child(receiverRoom)
                    .child("messages")
                    .child(message.getMessageId()).setValue(message);

            return true; // true is closing popup, false is requesting a new selection
        });

        //Item is message send
        if (holder.getClass() == SentViewHolder.class) {
            SentViewHolder viewHolder = (SentViewHolder) holder;

            //if send photo
            if(message.getMessage().equals("*photo*")){
                viewHolder.binding.imageSend.setVisibility(View.VISIBLE);
                viewHolder.binding.messageSend.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageUrl())
                        .placeholder(R.drawable.image_place_holder)
                        .into(viewHolder.binding.imageSend);
            }

            //if send text
            viewHolder.binding.messageSend.setText(message.getMessage());

            //Message had feeling or not
            if (message.getFeeling() >= 0) {
                viewHolder.binding.feeling.setImageResource(reactions[message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }

            //Click to message text to show popup reaction
            viewHolder.binding.messageSend.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v, event);
                    return false;
                }
            });

            //Click to message image to show popup reaction
            viewHolder.binding.imageSend.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v, event);
                    return false;
                }
            });
        }
        //Item is message recive
        else {

            ReceiveViewHolder viewHolder = (ReceiveViewHolder) holder;

            //if send photo
            if(message.getMessage().equals("*photo*")){
                viewHolder.binding.imageReceive.setVisibility(View.VISIBLE);
                viewHolder.binding.messageReceive.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageUrl())
                        .placeholder(R.drawable.image_place_holder)
                        .into(viewHolder.binding.imageReceive);
            }

            //if send text
            viewHolder.binding.messageReceive.setText(message.getMessage());

            //Message had feeling or not
            if (message.getFeeling() >= 0) {
                viewHolder.binding.feeling.setImageResource(reactions[message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }

            //Click to message text to show popup reaction
            viewHolder.binding.messageReceive.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v, event);
                    return false;
                }
            });

            //Click to message image to show popup reaction
            viewHolder.binding.imageReceive.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v, event);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {

        return messages.size();
    }

    //Class for viewHolder send message
    public class SentViewHolder extends RecyclerView.ViewHolder {
        ItemSendBinding binding;

        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSendBinding.bind(itemView);
        }
    }

    //Class for viewHolder receive message
    public class ReceiveViewHolder extends RecyclerView.ViewHolder {
        ItemReceiveBinding binding;

        public ReceiveViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemReceiveBinding.bind(itemView);
        }
    }
}
