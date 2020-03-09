package com.example.chatapplication;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                 RequestsFragment requestsFragment = new RequestsFragment();
                 return requestsFragment;

            case 1:
                 ChatFragment chatFragment=new ChatFragment();
                 return chatFragment;

            case 2:
                 FriendsFragment friendsFragment=new FriendsFragment();
                 return friendsFragment;

            default:
                 return null;
        }
    }



    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Requests";

            case 1:
                return "Chats";

            case 2:
                return "Friends";

            default:
                return null;
        }
    }
}