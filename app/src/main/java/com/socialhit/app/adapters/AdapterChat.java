package com.socialhit.app.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.socialhit.app.R;
import com.socialhit.app.activities.ChatActivity;
import com.socialhit.app.modelsClass.ModelChat;
import com.socialhit.app.modelsClass.ModelsUser;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyViewHolder> {
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    private Context context;
    private List<ModelChat> chatList;
    private String imageUrl;

    FirebaseUser fUser;

    public AdapterChat(Context context, List<ModelChat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false);
            return new MyViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false);
            return new MyViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String message = chatList.get(position).getMessage();
        String timeStamp = chatList.get(position).getTimestamp();

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm:aa", calendar).toString();

        holder.messageTv.setText(message);
        holder.timeTv.setText(dateTime);
        try {
            Glide.with(context).load(imageUrl).placeholder(R.drawable.ic_user).into(holder.profileIv);
        } catch (Exception e) {

        }

        holder.messageLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure to delete this message?");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        deleteMessage(position);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                    }
                });

                builder.create().show();

                return false;
            }
        });

// set seen and deleiver

        if (position == chatList.size() - 1) {
            if (chatList.get(position).isSeen()) {
                holder.isSeenTv.setText("Seen");
                Log.i("status", "onBindViewHolder: ===================seen");

            } else {
                holder.isSeenTv.setText("Delivered");
                Log.i("status", "onBindViewHolder: ===================Delivered");
            }
        } else {
            holder.isSeenTv.setVisibility(View.GONE);
        }
    }

    private void deleteMessage(int position) {

        String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String msgTimeStamp = chatList.get(position).getTimestamp();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        Query query = dbRef.orderByChild("timestamp").equalTo(msgTimeStamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("sender").getValue().equals(myUID)) {
//                        ds.getRef().removeValue();// method 1 to delete message                                             \
                        HashMap<String, Object> hashMap = new HashMap<>();//2nd method
                        hashMap.put("message", "This message was deleted...");
                        ds.getRef().updateChildren(hashMap);
                        Toast.makeText(context, "message deleted...", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "you can delete only your messages...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(fUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView profileIv;
        private TextView messageTv, timeTv, isSeenTv;
        private LinearLayout messageLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profileIv = itemView.findViewById(R.id.profileIv);
            messageTv = itemView.findViewById(R.id.messageTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            isSeenTv = itemView.findViewById(R.id.isSeenTv);
            messageLayout = itemView.findViewById(R.id.messageLayout);

        }
    }

}
