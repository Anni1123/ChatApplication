package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import android.content.Intent;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mimage=(CircleImageView)findViewById(R.id.profile_image);
        mname=(TextView)findViewById(R.id.DisplayName);
        mstatus=(TextView)findViewById(R.id.statusupdate);
        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        String current_Uid=mCurrentUser.getUid();

        mStatusBtn=(Button)findViewById(R.id.ChangeStatus);
        mImageBtn=(Button)findViewById(R.id.imageChange);
        userDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(current_Uid);
        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             String name=dataSnapshot.child("name").getValue().toString();
                String image=dataSnapshot.child("image").getValue().toString();
                String status=dataSnapshot.child("status").getValue().toString();
                String thumb_image=dataSnapshot.child("thumb_image").getValue().toString();

                mname.setText(name);
                mstatus.setText(status);


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


            }
        });
    }
}
