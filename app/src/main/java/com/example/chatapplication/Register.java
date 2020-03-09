package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends AppCompatActivity {

    private EditText regnamee;
    private EditText regemaill;
    private EditText regpasss;
    private Button regbutt;
    private FirebaseAuth mAuth;
    private Toolbar mtoolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        regnamee=(EditText)findViewById(R.id.regname);
        regemaill=(EditText)findViewById(R.id.regemail);
        regpasss=(EditText)findViewById(R.id.regpass);
        regbutt =(Button) findViewById(R.id.regbutton);
        mtoolbar=(Toolbar)findViewById(R.id.register_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        regbutt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=regnamee.getText().toString();
                String email=regemaill.getText().toString();
                String pass=regpasss.getText().toString();
                reg_user(name,email,pass);
            }
        });
    }
    private void reg_user(String name,String email,String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    Intent mainIntent=new Intent(Register.this,MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
                else{
                    Toast.makeText(Register.this,"You got some error",Toast.LENGTH_LONG);

                }
            }
        });
    }

}
