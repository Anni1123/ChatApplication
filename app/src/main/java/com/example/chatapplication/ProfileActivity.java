package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView userstatus,username,userfriend;
    private Button sendreq;
    private FirebaseUser mcurrentUser;
    private DatabaseReference databaseReference;
    private DatabaseReference mReqDatabse;
    private ProgressDialog progressDialog;
    private String current_state;
    private DatabaseReference friendDatabse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final String user_id = getIntent().getStringExtra("user_id");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mReqDatabse=FirebaseDatabase.getInstance().getReference().child("req_data");
        friendDatabse=FirebaseDatabase.getInstance().getReference().child("Friends");
        mcurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        username = (TextView) findViewById(R.id.userid);
        profileImage = (ImageView) findViewById(R.id.imageprofile);
        userstatus = (TextView) findViewById(R.id.prostatus);
        userfriend = (TextView) findViewById(R.id.profriend);

        current_state = "not friend";
        sendreq = (Button) findViewById(R.id.proreq);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Image Uploading");
        progressDialog.setMessage("Uploading....");
        progressDialog.setCanceledOnTouchOutside(false);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String disimage = dataSnapshot.child("image").getValue().toString();
                String disname = dataSnapshot.child("name").getValue().toString();
                String disstatus = dataSnapshot.child("status").getValue().toString();

                username.setText(disname);
                userstatus.setText(disstatus);
                Picasso.with(ProfileActivity.this).load(disimage).placeholder(R.drawable.anni).into(profileImage);
                //-----Friendlist\Request Feature-------------//
                mReqDatabse.child(mcurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(user_id)){
                            String req_type=dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if(req_type.equals("received")){
                                sendreq.setEnabled(true);
                                current_state="req_received";
                                sendreq.setText("Accept Friend Request");
                            }
                            else if(req_type.equals("req sent")){
                                current_state="req_sent";
                                sendreq.setText("Cancel Friend Request");
                            }
                            progressDialog.dismiss();
                        }
                        friendDatabse.child(mcurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(user_id)){
                                    current_state="Friends";
                                    sendreq.setText("Unfriend");
                                }
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                progressDialog.dismiss();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        sendreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendreq.setEnabled(false);
//-----------------------Not Friends----------------------//
                if(current_state.equals("not friend")){
                    mReqDatabse.child(mcurrentUser.getUid()).child(user_id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                mReqDatabse.child(user_id).child(mcurrentUser.getUid()).child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        current_state="req sent";
                                        sendreq.setText("Cancel Friend Request");
                                      //  Toast.makeText(ProfileActivity.this,"Request Sent",Toast.LENGTH_LONG).show();

                                    }
                                });
                            }
                            else {
                                Toast.makeText(ProfileActivity.this,"Failed",Toast.LENGTH_LONG).show();
                            }
                            sendreq.setEnabled(true);
                        }
                    });
                }
                //------------------------Cancel Request State---------------------//
                if(current_state.equals("req sent")){
                    mReqDatabse.child(mcurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mReqDatabse.child(user_id).child(mcurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    sendreq.setEnabled(true);
                                    current_state="not friend";
                                    sendreq.setText("Send Friend Request");
                                }
                            });
                        }
                    });
                }
                //---------------Request received State------------//
                if(current_state.equals("req_received")){
                    final String currentdate= DateFormat.getDateTimeInstance().format(new Date());
                    friendDatabse.child(mcurrentUser.getUid()).child(user_id).setValue(currentdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            friendDatabse.child(user_id).child(mcurrentUser.getUid()).setValue(currentdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mReqDatabse.child(mcurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mReqDatabse.child(user_id).child(mcurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    sendreq.setEnabled(true);
                                                    current_state="Friends";
                                                    sendreq.setText("Unfriend");
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }
        });
    }

}
