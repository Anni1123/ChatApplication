package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.inappmessaging.FirebaseInAppMessaging;

public class  MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private ViewPager mviewPager;
    private FirebaseAuth firebaseAuth;
    private Toolbar mtoolbar;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout mTabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mtoolbar=(Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("My Chat App");
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
           senttostart();
        }
    }
    public void senttostart(){
        Intent startIntent=new Intent(MainActivity.this,StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.mainmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.menulogout){

            FirebaseAuth.getInstance().signOut();
            senttostart();
        }
        if(item.getItemId()==R.id.menu_setting){
            Intent settingIntent=new Intent(MainActivity.this,SettingActivity.class);
            startActivity(settingIntent);
        }
        if(item.getItemId()==R.id.menu_user){
            Intent mainIntent=new Intent(MainActivity.this,UserActivity.class);
            startActivity(mainIntent);
        }
        return true;
    }
}
