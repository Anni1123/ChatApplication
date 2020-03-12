package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import android.app.ProgressDialog;
import android.content.Intent;
import java.util.Random;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private DatabaseReference userDatabase;
    private FirebaseUser mCurrentUser;
    private CircleImageView mimage;
    private TextView mname;
    private TextView mstatus;
    private Button mStatusBtn;
    private static final int GALLERY_PICK=1;
    private Button mImageBtn;
    private ProgressDialog mProgressDialog;
    private StorageReference mImageStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mimage=(CircleImageView)findViewById(R.id.profile_image);
        mname=(TextView)findViewById(R.id.DisplayName);
        mstatus=(TextView)findViewById(R.id.statusupdate);
        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        String current_Uid=mCurrentUser.getUid();

        mImageStorage= FirebaseStorage.getInstance().getReference();
        mStatusBtn=(Button)findViewById(R.id.ChangeStatus);
        mImageBtn=(Button)findViewById(R.id.imageChange);
        userDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(current_Uid);
        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                mname.setText(name);
                mstatus.setText(status);
                if (!image.equals("default")) {
                    Picasso.with(SettingActivity.this).load(image).resize(50, 50).
                            centerCrop().into(mimage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mStatusBtn.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       String status_value= mStatusBtn.getText().toString();
                   Intent status_intent=new Intent(SettingActivity.this,StatusActivity.class);
                   status_intent.putExtra("status value",status_value);
                   startActivity(status_intent);
         }
     });
        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent=new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),GALLERY_PICK);
/*
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingActivity.this);
                */
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUrl = data.getData();
            CropImage.activity(imageUrl)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mProgressDialog=new ProgressDialog(SettingActivity.this);
                mProgressDialog.setTitle("Uploading Image...");
                mProgressDialog.setMessage("Plaese wait while image is uploadingt");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
                String current_user_id=mCurrentUser.getUid();
                Uri resultUri = result.getUri();
                StorageReference filepath = mImageStorage.child("profile_images").child(current_user_id + ".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            String download_url = task.getResult().getUploadSessionUri().toString();
                            userDatabase.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(SettingActivity.this,"done",Toast.LENGTH_LONG).show();
                                }
                            });

                        } else {
                            Toast.makeText(SettingActivity.this, "Not Working", Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();
                        }
                    }
                });
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
