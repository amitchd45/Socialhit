package com.socialhit.app.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.socialhit.app.R;

public class StartFragment extends Fragment implements View.OnClickListener {
    private View view;
    private Button btnLogin,btnRegister;

    public StartFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_start, container, false);

        findId();
        onClick();

        return view;
    }

    private void onClick() {
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    private void findId() {
        btnRegister=view.findViewById(R.id.btnRegister);
        btnLogin=view.findViewById(R.id.btnLogin);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnLogin:
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_loginFragment2);
                break;

            case R.id.btnRegister:
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registerFragment);
                break;
        }
    }
}