package com.hec.app;

import android.app.Instrumentation;
import android.widget.AbsListView;

import com.hec.app.util.TestUtil;
import com.robotium.solo.*;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import junit.framework.Test;


@SuppressWarnings("rawtypes")
public class ApplicationTest extends ActivityInstrumentationTestCase2 {
    private Solo solo;

    private static final String LAUNCHER_ACTIVITY_FULL_CLASSNAME = "com.hec.app.activity.StartActivity";

    private static Class<?> launcherActivityClass;
    static{
        try {
            launcherActivityClass = Class.forName(LAUNCHER_ACTIVITY_FULL_CLASSNAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public ApplicationTest() throws ClassNotFoundException {
        super(launcherActivityClass);
    }

    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation());
        getActivity();
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        super.tearDown();
    }

    public void testRun() {
        //Wait for activity: 'com.hec.app.activity.StartActivity'
        solo.waitForActivity("StartActivity", 2000);
        //Wait for activity: 'com.hec.app.activity.LoginActivity'
        assertTrue("LoginActivity is not found!", solo.waitForActivity("LoginActivity"));
        //Click on Empty Text View
        solo.clickOnView(solo.getView("btnUserLogin"));
        //Wait for activity: 'com.hec.app.activity.HomeActivity'
        assertTrue("HomeActivity is not found!", solo.waitForActivity("HomeActivity"));
        for(int l = 0; l < 100; l ++) {
            //Click on LinearLayout
            solo.clickInList(l % 3,0);
            //Wait for activity: 'com.hec.app.activity.LotteryActivity'
            assertTrue("LotteryActivity is not found!", solo.waitForActivity("LotteryActivity"));

            LinearLayout ball = (LinearLayout) solo.getView("layoutContainer");
            int childcount = ball.getChildCount();
            for (int i = 1; i < childcount; i++) {
                if (ball.getChildAt(i) instanceof LinearLayout) {
                    LinearLayout v = (LinearLayout) ball.getChildAt(i);
                    for (int j = 0; j < v.getChildCount(); j++) {
                        LinearLayout asd = (LinearLayout) v.getChildAt(1);
                        solo.clickOnView(asd);
                    }
                }
            }
            solo.clearEditText(0);
            solo.enterText(0,"0.01");
            //Click on 去投注
            solo.clickOnView(solo.getView("ll_ok"));

            while(!solo.waitForActivity("LotterySettleActivity", 2000)){
                solo.clickOnView(solo.getView("ll_ok"));
            }
            //Wait for activity: 'com.hec.app.activity.LotterySettleActivity'
            assertTrue("LotterySettleActivity is not found!", solo.waitForActivity("LotterySettleActivity"));
            //Click on 去结算
            solo.clickOnView(solo.getView("ll_ok"));
            //Click on 确认
            solo.clickOnView(solo.getView("ll_ok"));
            solo.sleep(2000);
            while(!solo.waitForActivity("LotteryResultActivity", 2000)){
                solo.clickOnView(solo.getView("ll_ok"));
            }
            //Wait for activity: 'com.hec.app.activity.LotteryResultActivity'
            assertTrue("LotteryResultActivity is not found!", solo.waitForActivity("LotteryResultActivity"));
            //Click on 返回投注界面
            solo.clickOnView(solo.getView("ll_return"));

            solo.clickOnView(solo.getView(android.widget.ImageButton.class, 0));
        }
        //Click on ImageView
        solo.clickOnView(solo.getView("imgPersonalCenter"));
        //Click on 设置
        solo.clickOnView(solo.getView("sliding_menu_setting"));
        //Wait for activity: 'com.hec.app.activity.SettingActivity'
        assertTrue("SettingActivity is not found!", solo.waitForActivity("SettingActivity"));
        //Click on 登出 tomtom
        solo.clickOnView(solo.getView("logout"));
        //Click on  确认
        solo.clickOnView(solo.getView("logout_yes"));
        //Wait for activity: 'com.hec.app.activity.LoginActivity'
        assertTrue("LoginActivity is not found!", solo.waitForActivity("LoginActivity"));
    }

}