package com.socialhit.app.activities.ui.profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.socialhit.app.R;

import java.util.HashMap;


public class ProfileFragment extends Fragment {
    private View view;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private StorageReference storageReference;
    private String storagePath = "Users_Profile_Cover_imgs/";

    private ImageView avtarIV, coverIv;
    private TextView iv_name, tv_email, tv_phone;
    private FloatingActionButton btnEdit;
    private ProgressDialog pd;
    private String updateType = "", profileOrCoverPhoto = "";
    private Uri image_uri;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_profile, container, false);
        // init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
//        storageReference= FirebaseStorage.getInstance().getReference();

        init();
        onClick();

        pd = new ProgressDialog(getActivity());

        return view;
    }

    private void onClick() {
        btnEdit.setOnClickListener(this::editProfile);
    }

    private void editProfile(View view) {
        showEditProfileDialog();
    }

    private void showEditProfileDialog() {
        String options[] = {"1.) Edit Profile Picture", "2.) Edit Cover Photo", "3.) Edit Name", "4.) Edit Phone"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Options");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    profileOrCoverPhoto = "image";
                    pd.setMessage("Updating Profile Picture");
                    callImagerPicker();

                } else if (i == 1) {
                    profileOrCoverPhoto = "cover";
                    pd.setMessage("Updating Cover Picture");
                    callImagerPicker();
                } else if (i == 2) {
                    updateNameOrPhoneDialog("name");
                } else if (i == 3) {
                    updateNameOrPhoneDialog("phone");
                }
            }
        });

        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            image_uri = data.getData();
            uoloadProfileCoverPhoto(image_uri);

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
        } else {
            Toast.makeText(getActivity(), "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void uoloadProfileCoverPhoto(Uri uri) {
        pd.show();
        String filePathAndName = storagePath + "" + profileOrCoverPhoto + "_" + user.getUid();
        StorageReference storageReference1 = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        storageReference1.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                HashMap<String, Object> results = new HashMap<>();
                                results.put(profileOrCoverPhoto, uri.toString());
                                databaseReference.child(user.getUid()).updateChildren(results)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                pd.dismiss();
                                                profileOrCoverPhoto = "";
                                                Toast.makeText(getActivity(), "Image Uploaded...", Toast.LENGTH_SHORT).show();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        pd.dismiss();
                                        Toast.makeText(getActivity(), "Error Image Uploading...", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }

    private void callImagerPicker() {
        ImagePicker.Companion.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }

    private void init() {
        coverIv = view.findViewById(R.id.coverIv);
        avtarIV = view.findViewById(R.id.avtarIV);
        iv_name = view.findViewById(R.id.iv_name);
        tv_email = view.findViewById(R.id.tv_email);
        tv_phone = view.findViewById(R.id.tv_phone);
        btnEdit = view.findViewById(R.id.btnEdit);

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    getData
                    String name = "" + snapshot.child("name").getValue();
                    String email = "" + snapshot.child("email").getValue();
                    String phone = "" + snapshot.child("phone").getValue();
                    String image1 = String.valueOf(snapshot.child("image").getValue());
                    String cover = String.valueOf(snapshot.child("cover").getValue());

                    iv_name.setText(name);
                    tv_email.setText(email);
                    tv_phone.setText(phone);

                    try {
                        Glide.with(requireActivity()).load(cover).into(coverIv);
                        Glide.with(requireActivity()).load(image1).placeholder(R.drawable.ic_add_image).into(avtarIV);
                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateNameOrPhoneDialog(String key) {
        // AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update " + key);
        // set Linear layout
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setPadding(15, 10, 15, 10);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        // view to set in dialog

        final EditText editText = new EditText(getActivity());
        editText.setHint("Enter " + key);
        editText.setMinEms(16);
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        //Button
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String value = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(value)) {
                    pd.show();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put(key, value);
                    Task<Void> voidTask = databaseReference.child(user.getUid()).updateChildren(result);
                    voidTask.addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "Please Enter " + key, Toast.LENGTH_SHORT).show();
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
}