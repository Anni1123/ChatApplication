package com.example.chatapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

public class UserActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private RecyclerView mRecycle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mtoolbar=(Toolbar)findViewById(R.id.user_layout);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Accounts Update");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecycle=(RecyclerView)findViewById(R.id.recycle);
    }
}
