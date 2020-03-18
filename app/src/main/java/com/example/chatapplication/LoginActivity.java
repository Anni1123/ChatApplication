package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText memail;
    private EditText mpass;
    private FirebaseAuth mAuth;
    private Toolbar mtoolbar;
    private Button login;
    public ProgressDialog loginprogress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mtoolbar=(Toolbar)findViewById(R.id.login_toolbar);
        setSupportActionBar(mtoolbar);
        mAuth = FirebaseAuth.getInstance();
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loginprogress=new ProgressDialog(this);
        memail=(EditText)findViewById(R.id.logemail);
        mpass=(EditText)findViewById(R.id.logpass);
        login=(Button)findViewById(R.id.logbut);
        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email=memail.getText().toString();
                String password =mpass.getText().toString();
                if(!TextUtils.isEmpty(email)||!TextUtils.isEmpty(password)){
                    loginprogress.setTitle("Logging In");
                    loginprogress.setMessage("Please Wait ");
                    loginprogress.setCanceledOnTouchOutside(false);
                    loginprogress.show();
                    loginUser(email,password);
                }
            }
        });
    }
   public void loginUser(String email,String password){
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    loginprogress.dismiss();
                    Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    finish();
                }
                else{
                    loginprogress.hide();
                    Toast.makeText(LoginActivity.this,"Cannot Sign In..Plaese Try Again",Toast.LENGTH_LONG);
                }
            }
        });
   }

}
