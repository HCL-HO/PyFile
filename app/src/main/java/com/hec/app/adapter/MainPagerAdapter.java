//package com.hec.app.adapter;
//
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//
//import com.hec.app.fragment.EconomicsFragment;
//import com.hec.app.fragment.EntertainFragment;
//import com.hec.app.fragment.HeadlinesFragment;
//import com.hec.app.fragment.ScienceFragment;
//import com.hec.app.fragment.SocietyFragment;
//import com.hec.app.fragment.SportFragment;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MainPagerAdapter1 extends FragmentPagerAdapter {
//    private List<Fragment> mFragments;
//    private String[] mViewpager_title;
//
//    public MainPagerAdapter1(FragmentManager fm) {
//        super(fm);
//        mFragments = new ArrayList<>();
//        mFragments.add(new HeadlinesFragment());
//        mFragments.add(new EntertainFragment());
//        mFragments.add(new EconomicsFragment());
//        mFragments.add(new ScienceFragment());
//        mFragments.add(new SocietyFragment());
//        mFragments.add(new SportFragment());
//        mFragments.add(new SportFragment());
//        mFragments.add(new SportFragment());
//
//        mViewpager_title = new String[]{"头条", "娱乐", "经济", "自然", "社会", "运动","科技","文化"};
//    }
//
//    @Override
//    public CharSequence getPageTitle(int position) {
//        // TODO Auto-generated method stub
//        return mViewpager_title[position];
//    }
//
//    @Override
//    public Fragment getItem(int arg0) {
//        return mFragments.get(arg0);
//    }
//
//    @Override
//    public int getCount() {
//        // TODO Auto-generated method stub
//        return mFragments != null ? mFragments.size() : 0;
//    }
//}
