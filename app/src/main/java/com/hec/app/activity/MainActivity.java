package com.hec.app.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.hec.app.R;
import com.hec.app.activity.base.BaseActivity;
import com.hec.app.config.CommonConfig;
import com.hec.app.fragment.EconomicsFragment;
import com.hec.app.fragment.EntertainFragment;
import com.hec.app.fragment.HeadlinesFragment;
import com.hec.app.fragment.ScienceFragment;
import com.hec.app.fragment.SocietyFragment;
import com.hec.app.fragment.SportFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, HeadlinesFragment.OnArticleSelectedListener {

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.actionbar_bottom);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        TabLayout tabs = (TabLayout)findViewById(R.id.tabs);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        //tabs.setViewPager(pager);
        tabs.setupWithViewPager(pager);
        tabs.setTabsFromPagerAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_zone) {
            return toggleDrawerLayout();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected boolean toggleDrawerLayout(){
        //如果左边的已打开，则关闭左边的，不进行后续操作
        if (drawer.isDrawerOpen(Gravity.LEFT)) {
            drawer.closeDrawer(Gravity.LEFT);
            return true;
        }
        //如果左边的没打开，右边的打开了关闭，关闭了打开
        if (drawer.isDrawerOpen(Gravity.RIGHT)) {
            drawer.closeDrawer(Gravity.RIGHT);
        } else {
            drawer.openDrawer(Gravity.RIGHT);
        }
        return true;
    }

    @Override
    public void onArticleSelected(int articleID) {
        Intent intent = new Intent(this,NewsDetailActivity.class);
        intent.putExtra("rid", articleID);
        intent.putExtra("title", articleID);
        intent.putExtra("content", articleID);

        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isExitingAppliation(keyCode, event)) {
            if (needConfirmWhenExit()) {
                buildExitConfirmDialog().show();
            } else {
                killProcessAndExit();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private boolean needConfirmWhenExit() {
        SharedPreferences settings = getBaseContext().getSharedPreferences(CommonConfig.KEY_SETTING_PREFERENCE, MODE_PRIVATE);
        return settings.getBoolean(CommonConfig.KEY_CONFIRM_WHEN_EXIT, true);
    }

    private Dialog buildExitConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_leave_title);
        builder.setMessage(R.string.dialog_leave_message);
        builder.setPositiveButton(R.string.dialog_determine, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                killProcessAndExit();
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, null);

        return builder.create();
    }

    private void killProcessAndExit() {
        setApplicationFirst();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

    private boolean isExitingAppliation(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0;
    }

    private class MainPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragments;
        private String[] mViewpager_title;

        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragments = new ArrayList<>();
            mFragments.add(new HeadlinesFragment());
            mFragments.add(new EntertainFragment());
            mFragments.add(new EconomicsFragment());
            mFragments.add(new ScienceFragment());
            mFragments.add(new SocietyFragment());
            mFragments.add(new SportFragment());
            mFragments.add(new SportFragment());
            mFragments.add(new SportFragment());

            mViewpager_title = new String[]{"头条", "娱乐", "经济", "自然", "社会", "运动","科技","文化"};
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // TODO Auto-generated method stub
            return mViewpager_title[position];
        }

        @Override
        public Fragment getItem(int arg0) {
            return mFragments.get(arg0);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mFragments != null ? mFragments.size() : 0;
        }
    }
}


