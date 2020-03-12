package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserActivity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
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
        mUserDatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        mRecycle=(RecyclerView)findViewById(R.id.recycleview);
        mRecycle.setHasFixedSize(true);
        mRecycle.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Users> options =new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(mUserDatabase,Users.class).build();
        FirebaseRecyclerAdapter<Users, UserviewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UserviewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserviewHolder holder, int position, @NonNull Users model) {

                holder.setName(model.getName());

            }


            @NonNull
            @Override
            public UserviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }
        };
        mRecycle.setAdapter(firebaseRecyclerAdapter);
    }
    public class UserviewHolder extends RecyclerView.ViewHolder{

        View mView;
        public UserviewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setName(String name){
            TextView usersname=(TextView)mView.findViewById(R.id.display_name);
            usersname.setText(name);
        }

    }
}
