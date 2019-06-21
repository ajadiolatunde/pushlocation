package com.physson.getphys;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOError;
import java.io.IOException;


public class Home extends AppCompatActivity {
    static {
        System.loadLibrary("native-lib");
    }

    public static native String phyIn(String t);
    public static native String phyOut(String t);

    private TabLayout mTabLayout;

    private int[] mTabsIcons = {
            R.drawable.ic_home_black_24dp,
            R.drawable.ic_me_black_24dp,
            R.drawable.ic_notifications_black_24dp,
            R.drawable.ic_account_circle_black_24dp,};

    @Override
    public void onNewIntent(Intent intent) {
        Bundle extras = intent.getExtras(); // always null
        //System.out.println("Tunde  home  bundel............  ");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        String in = phyIn("SrhmIt4bTuOd-KMWXe5M5A");
        Singleton1.getInstance(getApplicationContext()).getAllpref();
        //System.out.println("Tunde ...jni...."+in);

        Bundle extras = getIntent().getExtras(); // always null
        //Needs attention
        Singleton1.getInstance(getApplicationContext()).setGpsLatlon("");

        // Setup the viewPager
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());

        if (viewPager != null) {
            //viewPager.setPagingEnabled(false);
            viewPager.setAdapter(pagerAdapter);
        }

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        if (mTabLayout != null) {
            mTabLayout.setupWithViewPager(viewPager);

            for (int i = 0; i < mTabLayout.getTabCount(); i++) {
                TabLayout.Tab tab = mTabLayout.getTabAt(i);
                if (tab != null)
                    tab.setCustomView(pagerAdapter.getTabView(i));
            }

            mTabLayout.getTabAt(0).getCustomView().setSelected(true);
        }
    }


    private class MyPagerAdapter extends FragmentPagerAdapter {

        public final int PAGE_COUNT = 4;

        private final String[] mTabsTitle = {"Home", "Contact","Notice","Info"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public View getTabView(int position) {
            // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
            View view = LayoutInflater.from(Home.this).inflate(R.layout.tablayout, null);
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(mTabsTitle[position]);
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            icon.setImageResource(mTabsIcons[position]);
            return view;
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {

                case 0:
                    return Home_fragment1.newInstance(1);

                case 1:
                    return Home_fragment2.newInstance(2);
                case 2:
                    return Home_fragment3.newInstance(3);
                case 3:
                    return Home_fragment4.newInstance(4);


            }
            return null;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabsTitle[position];
        }
    }

    @Override
    public void onBackPressed() {
    }


}

