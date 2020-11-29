package com.socialhit.app.activities.ui.users;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.socialhit.app.R;
import com.socialhit.app.activities.ChatActivity;
import com.socialhit.app.adapters.AdapterUsers;
import com.socialhit.app.modelsClass.ModelsUser;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {

    private View view;
    private EditText editTextSearch;
    private RecyclerView rv_userList;
    private AdapterUsers adapterUsers;
    private List<ModelsUser>userList=new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ProgressBar progress_circular;

    //firebase
    private FirebaseUser currentUser;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_users, container, false);

        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Users");

        init();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    ModelsUser modelsUser=snapshot.getValue(ModelsUser.class);
                    if (!modelsUser.getUid().equals(currentUser.getUid())){
                        userList.add(modelsUser);
                    }
                }
                adapterUsers=new AdapterUsers(getActivity(), userList, new AdapterUsers.Select() {
                    @Override
                    public void onClick(String UID) {
                        Intent intent=new Intent(getActivity(), ChatActivity.class);
                        intent.putExtra("hisUid",UID);
                        startActivity(intent);
                    }
                });
                rv_userList.setAdapter(adapterUsers);
//                adapterUsers.notifyDataSetChanged();
                progress_circular.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progress_circular.setVisibility(View.INVISIBLE);
                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }


    private void init() {
        progress_circular=view.findViewById(R.id.progress_circular);
        rv_userList=view.findViewById(R.id.rv_userList);
        editTextSearch=view.findViewById(R.id.editTextSearch);

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                if (!editable.toString().equalsIgnoreCase("")){
                    filter(editable.toString());
                }
            }
        });
    }

    private void filter(String text) {
        List<ModelsUser> list = new ArrayList<>();

        //looping through existing elements
        for (ModelsUser user : userList) {
            //if the existing elements contains the search input
            if (user.getName().toLowerCase().contains(text.toLowerCase()) || user.getEmail().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                list.add(user);
            }
        }
        //calling a method of the adapter class and passing the filtered list
//        if (list!=null){
            adapterUsers.filterList(list);
//        }
    }
}