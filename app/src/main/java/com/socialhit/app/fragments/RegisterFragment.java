package com.socialhit.app.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.socialhit.app.R;
import com.socialhit.app.modelsClass.User;

import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment implements View.OnClickListener {
    private View view;
    private RelativeLayout rlayout;
    private Animation animation;
    private Button btnRegister;
    private EditText et_phone,username,email,password,confirm_password,phone;
    private ProgressDialog progressDialog;
    private String mUsername,mEmail,mPassword,mConfirm_password,mPhone;
    private TextView btnBack;
    private FirebaseAuth mAuth;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private ValueEventListener mListener;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_register, container, false);


        init();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("Users");
        onClick();
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Registering user...");

        return view;
    }

    private void onClick() {
        btnBack.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    private void init() {
        username = view.findViewById(R.id.et_username);
        email = view.findViewById(R.id.et_email);
        phone = view.findViewById(R.id.et_phone);
        password= view.findViewById(R.id.et_password);
        confirm_password= view.findViewById(R.id.et_retype_password);
        btnRegister= view.findViewById(R.id.btnRegister);
        btnBack= view.findViewById(R.id.btnBack);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnRegister:
                validation();
                break;

            case R.id.btnBack:
                getActivity().onBackPressed();
                break;
        }
    }

    private void validation() {

        mUsername=username.getText().toString().trim();
        mEmail=email.getText().toString().trim();
        mPhone=phone.getText().toString().trim();
        mPassword=password.getText().toString().trim();
        mConfirm_password=confirm_password.getText().toString().trim();

        if(mUsername.isEmpty()){
            username.setError("Enter username.");
            username.setFocusable(true);
        }
        else if (mEmail.isEmpty()){
            email.setError("Enter email address.");
            email.setFocusable(true);
        }else if (mPhone.isEmpty()){
            phone.setError("Enter phone.");
            phone.setFocusable(true);
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()){
            email.setError("Invalid email.");
            email.setFocusable(true);
        }
        else if (mPassword.isEmpty()){
            password.setError("Enter password");
            password.setFocusable(true);
        }
        else if (mPassword.length()<6){
            password.setError("Password length must be 6 characters.");
            password.setFocusable(true);
        }
        else if (!mPassword.equals(mConfirm_password)){
            confirm_password.setError("Password Not matching");
            confirm_password.setFocusable(true);
        }
        else {
            registerUser(mEmail,mPassword);
        }
    }
    private void registerUser(String mEmail, final String mPassword) {

        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            saveUserDetails(mUsername,user.getEmail(),"online",mPhone,"N/A","",user.getUid(),"noOne");
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserDetails(String mUsername, String email,String online, String mPhone, String image,String cover, String uid,String typingTo) {
        User user=new User(mUsername,email,online,mPhone,image,cover,uid,typingTo);
        Task<Void> voidTask = mRef.child(uid).setValue(user);
        voidTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Registration successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}