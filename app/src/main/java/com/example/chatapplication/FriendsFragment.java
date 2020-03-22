package com.example.chatapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentViewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class FriendsFragment extends Fragment {
    private RecyclerView mFrienhdList;
    private DatabaseReference mFriendDatabse;
    FirebaseRecyclerAdapter<Friends,FriendsViewHolder> friendsRecyclerViewAdapter;
    private FirebaseAuth mAuth;
    private String mCurrentUserid;
    private View mMainVIew;
    public FriendsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainVIew=inflater.inflate(R.layout.fragment_friends, container, false);
        mFrienhdList=(RecyclerView)mMainVIew.findViewById(R.id.recyclerview);
        mAuth=FirebaseAuth.getInstance();
        mCurrentUserid=mAuth.getCurrentUser().getUid();
        if(mCurrentUserid!=null)
            mFriendDatabse= FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrentUserid);
        mFrienhdList.setHasFixedSize(true);
        mFrienhdList.setLayoutManager(new LinearLayoutManager(getContext()));
        return mMainVIew;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query query=mFriendDatabse.limitToLast(50).orderByPriority();
        FirebaseRecyclerOptions<Friends> options =new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(query,Friends.class).build();
        friendsRecyclerViewAdapter =new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_single_layout, parent, false);
                return new FriendsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FriendsViewHolder holder, int position, @NonNull Friends model) {

                holder.setDate(model.getDate());

            }

        };
        mFrienhdList.setAdapter(friendsRecyclerViewAdapter);


    }
    public class FriendsViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);

            mView=itemView;
        }
        public void setDate(String date){

            TextView userStatusView = mView.findViewById(R.id.default_status);
            userStatusView.setText(date);

        }
    }
    @Override
    public void onStop() {
        super.onStop();
        friendsRecyclerViewAdapter.stopListening();
    }
}
