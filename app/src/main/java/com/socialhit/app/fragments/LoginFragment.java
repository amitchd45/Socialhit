package com.socialhit.app.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.socialhit.app.R;
import com.socialhit.app.activities.HomeMainActivity;
import com.socialhit.app.modelsClass.User;

public class LoginFragment extends Fragment implements View.OnClickListener {
    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient mGoogleSignInClient;
    private View view;
    private EditText email, password;
    private ProgressDialog progressDialog;
    private String mUsername, mEmail, mPassword, mConfirm_password, mPhone;
    private TextView btnRegister, tv_forgotPassword;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private SignInButton googleLoginBtn;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);
        init();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient= GoogleSignIn.getClient(getActivity(),gso);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("Users");
        onClick();
        progressDialog = new ProgressDialog(getActivity());
        return view;
    }

    private void onClick() {
        btnLogin.setOnClickListener(this);
        tv_forgotPassword.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        googleLoginBtn.setOnClickListener(this);
    }

    private void init() {
        email = view.findViewById(R.id.et_email);
        password = view.findViewById(R.id.et_password);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnRegister = view.findViewById(R.id.btnRegister);
        tv_forgotPassword = view.findViewById(R.id.tv_forgotPassword);
        googleLoginBtn = view.findViewById(R.id.googleLoginBtn);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                validation();
                break;

            case R.id.btnRegister:
                Navigation.findNavController(view).navigate(R.id.action_loginFragment2_to_registerFragment);
                break;

            case R.id.tv_forgotPassword:
                openRevoverPasswordDialog();
                break;
            case R.id.googleLoginBtn:
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
        }
    }

    private void openRevoverPasswordDialog() {
        // AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Recover Password");
        // set Linear layout
        LinearLayout linearLayout = new LinearLayout(getActivity());
        // view to set in dialog

        final EditText editText = new EditText(getActivity());
        editText.setHint("Email");
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        editText.setMinEms(16);

        linearLayout.addView(editText);
        linearLayout.setPadding(15, 10, 15, 10);

        builder.setView(linearLayout);

        //Button
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String mEmail = editText.getText().toString().trim();
                if (mEmail.isEmpty()) {
                    Toast.makeText(getActivity(), "Enter email..", Toast.LENGTH_SHORT).show();
                } else {
                    beginRecover(mEmail);
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        //show dialog
        builder.create().show();

    }

    private void beginRecover(String mEmail) {
        progressDialog.setMessage("Sending email...");
        progressDialog.show();
        mAuth.sendPasswordResetEmail(mEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Email sent.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validation() {
        mEmail = email.getText().toString().trim();
        mPassword = password.getText().toString().trim();

        if (mEmail.isEmpty()) {
            email.setError("Enter email address.");
            email.setFocusable(true);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
            email.setError("Invalid email.");
            email.setFocusable(true);
        } else if (mPassword.isEmpty()) {
            password.setError("Enter password");
            password.setFocusable(true);
        } else if (mPassword.length() < 6) {
            password.setError("Password length must be 6 characters.");
            password.setFocusable(true);
        } else {
            login(mEmail, mPassword);
        }
    }

    private void login(String mEmail, String mPassword) {
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getActivity(), "Login Successfully...", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), HomeMainActivity.class));
                            getActivity().finishAffinity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("login", "signInWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed." + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUserDetails(String displayName, String email, String s,String online, String image,String cover, String uid,String typingTo) {
        User user=new User(displayName,email,s,online,image,cover,uid,typingTo);
        Task<Void> voidTask = mRef.child(uid).setValue(user);
        voidTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                progressDialog.dismiss();
                Log.i("details", "onSuccess: Social details saved==========");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken(),account.getDisplayName());

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(getActivity(), "Google sign in failed"+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken, final String mUsername) {
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
//                            Log.i("details", "onActivityResult: image url ====="+task.getResult().getUser().getPhotoUrl());

                            if (task.getResult().getAdditionalUserInfo().isNewUser()){
                                saveUserDetails(mUsername,task.getResult().getUser().getEmail(),"online","N/A","N/A","",task.getResult().getUser().getUid(),"noOne");
                            }

                            Toast.makeText(getActivity(), "Login successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), HomeMainActivity.class));
                            getActivity().finishAffinity();

//                            updateUI(user);
                        } else {
                            Toast.makeText(getActivity(), "signInWithCredential:failure : "+task.getException(), Toast.LENGTH_SHORT).show();
                            Snackbar.make(view, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}