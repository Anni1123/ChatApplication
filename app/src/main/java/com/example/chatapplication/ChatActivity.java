package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;

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
    private StorageTask uploadTask;
    ImageButton image;
    EditText text;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;
    private RecyclerView mMessageList;
    private DatabaseReference friendDatabse;
    private Toolbar mtoolbar;
    private int itemPos = 0;
    private static final int GALLERY_PICK = 1;
    private ProgressDialog mProgressDialog;
    private SwipeRefreshLayout mRefresh;
    private String mLastKey = "";
    private String mPrevKey = "";
    private FirebaseAuth firebaseAuth;
    String user_id;
    String myUrl;
    ValueEventListener seenListener;
    private DatabaseReference mMessageDatabase;
String mcurrent;
    private StorageReference mImageStorage;
private static final int TOTAL_ITEM_TO_LOAD=10;
private int mcurrentpage=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        user_id = getIntent().getStringExtra("user_id");
        image=(ImageButton)findViewById(R.id.chatimagesend);
        declinereq = (Button) findViewById(R.id.decline);
        firebaseAuth=FirebaseAuth.getInstance();
        mMessageList=(RecyclerView)findViewById(R.id.messagelist);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mcurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        username = (TextView) findViewById(R.id.useid);
        mRefresh=(SwipeRefreshLayout)findViewById(R.id.msgswipe);
        profileImage = (ImageView) findViewById(R.id.imagprofil);
        mcurrent=mcurrentUser.getUid();
        mDatabase=FirebaseDatabase.getInstance().getReference();
        btnsend=(ImageButton)findViewById(R.id.chatsend);
        text=(EditText)findViewById(R.id.chatmsg);
        mtoolbar = (Toolbar) findViewById(R.id.msg_toolbar);
        mImageStorage = FirebaseStorage.getInstance().getReference();
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
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
               startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });
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
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mcurrentpage++;
                itemPos=0;
                loadMoreMessages();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setTitle("Uploading Image...");
            mProgressDialog.setMessage("Please wait while image is uploading");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
            Uri imageUrl = data.getData();
            CropImage.activity(imageUrl)
                    .setAspectRatio(1, 1)
                    .start(this);
            final String currentuserref = "messages/" + mcurrent + "/" + user_id;
            final String chatuserref = "messages/" + user_id + "/" + mcurrent;

            DatabaseReference usermsgpush = mDatabase.child("messages").child(mcurrent).child(user_id).push();
            final String pushid = usermsgpush.getKey();
            final StorageReference filepath = mImageStorage.child("msg_img").child(pushid + ".jpg");
            uploadTask=filepath.putFile(imageUrl);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            mProgressDialog.dismiss();
                            String url = uri.toString();
                        Map messageMap = new HashMap();
                        messageMap.put("message", url);
                        messageMap.put("send", false);
                        messageMap.put("type", "image");
                        messageMap.put("time", ServerValue.TIMESTAMP);
                        messageMap.put("from", mcurrent);
                        Map messageusermap = new HashMap();
                        messageusermap.put(currentuserref + "/" + pushid, messageMap);
                        messageusermap.put(chatuserref + "/" + pushid, messageMap);
                        text.setText("");

                        mDatabase.updateChildren(messageusermap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    Log.d("chat app", databaseError.getMessage().toString());
                                }
                            }
                        });
                    }
                });
            }
    });
        }
    }
            public void loadMoreMessages(){
        DatabaseReference databaseReference= mDatabase.child("messages").child(mcurrent).child(user_id);
        Query message=databaseReference.orderByKey().endAt(mLastKey).limitToLast(10);
        message.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);

                String messageKey = dataSnapshot.getKey();
                if(!mPrevKey.equals(messageKey)){
                    messagesList.add(itemPos++,messages);
                }
                else {
                    mPrevKey=mLastKey;
                }
                if(itemPos == 1){

                    mLastKey = messageKey;

                }

                mAdapter.notifyDataSetChanged();
                mRefresh.setRefreshing(false);
                mLinearLayout.scrollToPositionWithOffset(10,0);

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
    private void loadMessages(){
        DatabaseReference databaseReference= mDatabase.child("messages").child(mcurrent).child(user_id);
        Query message=databaseReference.limitToLast(mcurrentpage*TOTAL_ITEM_TO_LOAD);
        message.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);
                itemPos++;
                if(itemPos == 1){

                    String messageKey = dataSnapshot.getKey();

                    mLastKey = messageKey;
                    mPrevKey=messageKey;

                }
                messagesList.add(messages);
                mAdapter.notifyDataSetChanged();
                mMessageList.scrollToPosition(messagesList.size()-1);
                mRefresh.setRefreshing(false);
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
             messageMap.put("from",mcurrent);
             Map messageusermap=new HashMap();
             messageusermap.put(currentuserref +"/"+pushid,messageMap);
             messageusermap.put(chatuserref +"/"+pushid,messageMap);
             text.setText("");
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

}


