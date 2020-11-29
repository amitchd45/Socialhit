package com.socialhit.app.fragments;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.socialhit.app.R;
import com.socialhit.app.activities.HomeMainActivity;

public class SplashScreenFragment extends Fragment {
    private View view;
    private FirebaseAuth mAuth;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_splash_screen, container, false);
        mAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check if user is signed in (non-null) and update UI accordingly.
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser!=null){
                    startActivity(new Intent(getActivity(), HomeMainActivity.class));
                    getActivity().finish();
                }else {
                    Navigation.findNavController(view).navigate(R.id.action_splashScreenFragment_to_loginFragment);
                }

            }
        },3000);

        return view;
    }

}