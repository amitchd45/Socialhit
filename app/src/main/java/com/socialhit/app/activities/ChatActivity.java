package com.socialhit.app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.socialhit.app.adapters.AdapterChat;
import com.socialhit.app.modelsClass.ModelChat;
import com.socialhit.app.modelsClass.ModelsUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    private ImageView profileIv;
    private TextView userNameTv, userStatusTv;
    private RecyclerView chat_recyclerView;
    private EditText messageEt;
    private ImageButton btnSend;
    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private String hisUid;
    private String myUid;
    private String hisImage;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Activity activity;
    private LinearLayout ll_back;

    private ValueEventListener seenListner;
    private DatabaseReference userRefForSeen;

    private List<ModelChat>chatList=new ArrayList<>();
    private AdapterChat adapterChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        activity=ChatActivity.this;

        init();
        onClick();
        messageEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {

                if (s.toString().trim().length()==0){

                    checkTypingStatus("noOne");
                }else {
                    checkTypingStatus(hisUid);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void onClick() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message =messageEt.getText().toString().trim();
                if (TextUtils.isEmpty(message)){
                    Toast.makeText(ChatActivity.this, "Cannot send empty message.", Toast.LENGTH_SHORT).show();
                }else {
                    sendMessage(message);
                }
            }
        });

        readMessage();

        seenMeassage();
    }

    private void seenMeassage() {
        userRefForSeen=FirebaseDatabase.getInstance().getReference("Chats");
        seenListner=userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    ModelChat modelChat=snapshot.getValue(ModelChat.class);
                    if (modelChat.getReceiver().equals(myUid) && modelChat.getSender().equals(hisUid)){
                        HashMap<String,Object>hasSeen=new HashMap<>();
                        hasSeen.put("isSeen",true);
                        snapshot.getRef().updateChildren(hasSeen);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMessage() {
        DatabaseReference dbRefrance=FirebaseDatabase.getInstance().getReference("Chats");
        dbRefrance.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    ModelChat modelChat=snapshot.getValue(ModelChat.class);
                    if (modelChat.getReceiver().equals(myUid) && modelChat.getSender().equals(hisUid) ||
                            modelChat.getReceiver().equals(hisUid) && modelChat.getSender().equals(myUid)){
                        chatList.add(modelChat);
                    }
                    adapterChat=new AdapterChat(ChatActivity.this,chatList,hisImage);
                    adapterChat.notifyDataSetChanged();
                    chat_recyclerView.setAdapter(adapterChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String message) {

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
        String timestamp=String.valueOf(System.currentTimeMillis());
        HashMap<String,Object>result=new HashMap<>();
        result.put("sender",myUid);
        result.put("receiver",hisUid);
        result.put("message",message);
        result.put("timestamp",timestamp);
        result.put("isSeen",false);

        databaseReference.child("Chats").push().setValue(result);
        messageEt.setText("");
    }

    private void checkOnlineStatus(String status){
        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("onlineStatus",status);
        dbRef.updateChildren(hashMap);

    }

    private void checkTypingStatus(String typing){
        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("typingTo",typing);
        dbRef.updateChildren(hashMap);

    }



    private void init() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ll_back = findViewById(R.id.ll_back);
        profileIv = findViewById(R.id.profileIv);
        userNameTv = findViewById(R.id.userNameTv);
        userStatusTv = findViewById(R.id.userStatusTv);
        chat_recyclerView = findViewById(R.id.chat_recyclerView);
        messageEt = findViewById(R.id.messageEt);
        btnSend = findViewById(R.id.btnSend);

        Intent intent=getIntent();
        hisUid=intent.getStringExtra("hisUid");

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Users");

        Query query=databaseReference.orderByChild("uid").equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    String name=""+snapshot.child("name").getValue();
                    hisImage=""+snapshot.child("image").getValue();
                    String typingStatus=""+snapshot.child("typingTo").getValue();


                    // check typing status
                    if (typingStatus.equalsIgnoreCase(myUid)){
                        userStatusTv.setText("Typing...");
                    }else {
                        //get value of online status
                        String onlineStatus=""+snapshot.child("onlineStatus").getValue();
                        if (onlineStatus.equalsIgnoreCase("online")){
                            userStatusTv.setText(onlineStatus);
                        }else {
                            Calendar calendar=Calendar.getInstance(Locale.ENGLISH);
                            calendar.setTimeInMillis(Long.parseLong(onlineStatus));
                            String dateTime= DateFormat.format("dd/MM/yyyy hh:mm:aa",calendar).toString();
                            userStatusTv.setText("Last seen at: "+dateTime);
                        }
                    }

                    if (!activity.isFinishing()) {
                        userNameTv.setText(name);
                        Glide.with(activity).load(hisImage).placeholder(R.drawable.ic_user).into(profileIv);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                logoutDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Do you want logout?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseAuth.getInstance().signOut();
                checkUserStatus();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private void checkUserStatus() {
        FirebaseUser user =  FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){

            myUid=user.getUid();
        }else {
            startActivity(new Intent(this, MainActivity.class));
            finishAffinity();
        }
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        //set online status
        checkOnlineStatus("online");
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        String timestamp= String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timestamp);
        checkTypingStatus("noOne");
        userRefForSeen.removeEventListener(seenListner);
    }

    @Override
    protected void onResume() {
        checkOnlineStatus("online");
        super.onResume();
    }
}