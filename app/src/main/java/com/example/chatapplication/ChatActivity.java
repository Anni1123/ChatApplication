package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView userstatus, username, userfriend;
    private Button sendreq, declinereq;
    private FirebaseUser mcurrentUser;
    private DatabaseReference databaseReference;
    private DatabaseReference mReqDatabse;
    private DatabaseReference notificationDatabase;
    private ProgressDialog progressDialog;
    private String current_state;
    private DatabaseReference friendDatabse;
    private Toolbar mtoolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        final String user_id = getIntent().getStringExtra("user_id");
        declinereq = (Button) findViewById(R.id.decline);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mcurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        username = (TextView) findViewById(R.id.useid);
        profileImage = (ImageView) findViewById(R.id.imagprofil);
        mtoolbar = (Toolbar) findViewById(R.id.msg_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String disimage = dataSnapshot.child("image").getValue().toString();
                String disname = dataSnapshot.child("name").getValue().toString();
                username.setText(disname);
                Picasso.with(ChatActivity.this).load(disimage).placeholder(R.drawable.anni).into(profileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    };
}
        //-----Friendlist\Request Feature-------------//

