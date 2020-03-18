package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView userstatus,username,userfriend;
    private Button sendreq;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        String user_id=getIntent().getStringExtra("user_id");
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        username=(TextView)findViewById(R.id.userid);
        profileImage=(ImageView)findViewById(R.id.imageprofile);
        userstatus=(TextView)findViewById(R.id.prostatus);
        userfriend=(TextView)findViewById(R.id.profriend);
        sendreq=(Button)findViewById(R.id.proreq);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String disimage=dataSnapshot.child("image").getValue().toString();
                String disname=dataSnapshot.child("name").getValue().toString();
                String disstatus=dataSnapshot.child("status").getValue().toString();

                username.setText(disname);
                userstatus.setText(disstatus);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
