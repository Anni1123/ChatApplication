package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView userstatus, username, userfriend;
    private Button sendreq, declinereq;
    private FirebaseUser mcurrentUser;
    private DatabaseReference databaseReference;
    private DatabaseReference mDatabase;
    private DatabaseReference notificationDatabase;
    private ProgressDialog progressDialog;
    private String current_state;
    private DatabaseReference friendDatabse;
    private Toolbar mtoolbar;
    private FirebaseAuth firebaseAuth;
String mcurrent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        final String user_id = getIntent().getStringExtra("user_id");
        declinereq = (Button) findViewById(R.id.decline);
        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mcurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        username = (TextView) findViewById(R.id.useid);
        profileImage = (ImageView) findViewById(R.id.imagprofil);
        mcurrent=mcurrentUser.getUid();
        mDatabase=FirebaseDatabase.getInstance().getReference();
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

        mDatabase.child("Chat").child(mcurrent).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(user_id)){
                    Map chatAddMap=new HashMap();
                    chatAddMap.put("seen",false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);
                    Map chatusermap=new HashMap();
                    chatusermap.put("Chat/"+mcurrent+"/"+user_id,chatAddMap);
                    chatusermap.put("Chat/"+user_id+"/"+mcurrent,chatAddMap);
                    mDatabase.updateChildren(chatusermap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError!=null){
                                Log.d("chat app",databaseError.getMessage().toString());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
        //-----Friendlist\Request Feature-------------//

