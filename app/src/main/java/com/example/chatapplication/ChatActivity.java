package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    ImageButton btnsend;
    EditText text;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;
    private RecyclerView mMessageList;
    private DatabaseReference friendDatabse;
    private Toolbar mtoolbar;
    private int itemPos = 0;

    private String mLastKey = "";
    private String mPrevKey = "";
    private FirebaseAuth firebaseAuth;
    String user_id;
    private DatabaseReference mMessageDatabase;
String mcurrent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        user_id = getIntent().getStringExtra("user_id");
        declinereq = (Button) findViewById(R.id.decline);
        firebaseAuth=FirebaseAuth.getInstance();
        mMessageList=(RecyclerView)findViewById(R.id.messagelist);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mcurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        username = (TextView) findViewById(R.id.useid);
        profileImage = (ImageView) findViewById(R.id.imagprofil);
        mcurrent=mcurrentUser.getUid();
        mDatabase=FirebaseDatabase.getInstance().getReference();
        btnsend=(ImageButton)findViewById(R.id.chatsend);
        text=(EditText)findViewById(R.id.chatmsg);
        mtoolbar = (Toolbar) findViewById(R.id.msg_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mAdapter=new MessageAdapter(messagesList);
        mLinearLayout=new LinearLayoutManager(this);
        mMessageList.setHasFixedSize(true);
        mMessageList.setAdapter(mAdapter);
        mMessageList.setLayoutManager(mLinearLayout);
        mDatabase.child("Chat").child(mcurrent).child(user_id).child("seen").setValue(true);
        loadMessages();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String disimage = dataSnapshot.child("image").getValue().toString();
                String disname = dataSnapshot.child("name").getValue().toString();
                username.setText(disname);
                Picasso.get().load(disimage).placeholder(R.drawable.anni).into(profileImage);
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
        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessgae();
            }
        });
    }
    public void sendMessgae(){

        String message=text.getText().toString();
         if(!TextUtils.isEmpty(message)){
             String currentuserref="messages/" + mcurrent +"/" + user_id;
             String chatuserref="messages/" + user_id +"/"+mcurrent;
             DatabaseReference usermsgpush=mDatabase.child("messages").child(mcurrent).child(user_id).push();
             final String pushid=usermsgpush.getKey();
             Map messageMap=new HashMap();
             messageMap.put("message",message);
             messageMap.put("send",false);
             messageMap.put("type","text");
             messageMap.put("time" ,ServerValue.TIMESTAMP);
             Map messageusermap=new HashMap();
             messageusermap.put(currentuserref +"/"+pushid,messageMap);
             messageusermap.put(chatuserref +"/"+pushid,messageMap);
             mDatabase.updateChildren(messageusermap, new DatabaseReference.CompletionListener() {
                 @Override
                 public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                     if(databaseError!=null){
                         Log.d("chat app",databaseError.getMessage().toString());
                     }
                 }
             });

         }
    }
    private void loadMessages(){
        mDatabase.child("messages").child(mcurrent).child(user_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Messages messages = dataSnapshot.getValue(Messages.class);
                    messagesList.add(messages);
                    mAdapter.notifyDataSetChanged();
                }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}


