package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private TextInputEditText mstatus;
    private Button stchanges;
    private DatabaseReference mStatusReference;
    private ProgressDialog mDialog;
    private FirebaseUser mCurrentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        mToolBar=(Toolbar)findViewById(R.id.status_appBar);
        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        String currentUid=mCurrentUser.getUid();

        mStatusReference= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUid);
        setSupportActionBar(mToolBar);

        String status_value=getIntent().getStringExtra("status_value");
        mstatus=(TextInputEditText) findViewById(R.id.statush);
        stchanges= (Button)findViewById(R.id.changes);
        mstatus.getEditableText();
        mstatus.setText(status_value);
        stchanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog=new ProgressDialog(StatusActivity.this);
                mDialog.setTitle("Status Changing");
                mDialog.setMessage("Please Wait while updating");
                mDialog.show();
                String status=mstatus.getEditableText().toString();
                mStatusReference.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mDialog.dismiss();
                            startActivity(new Intent(StatusActivity.this,SettingActivity.class));
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"there is SOme error while Updating",Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }
}
