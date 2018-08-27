package com.hec.app;

import android.content.Context;

import com.hec.app.activity.HomeActivity;
import com.hec.app.entity.LotteryInfo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */

@RunWith(MockitoJUnitRunner.class)
public class ExampleUnitTest {

    @Mock
    Context mContext;
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    public void test2(){
        assertNull(new LotteryInfo(0,"",""));
    }
    public void test1(){
        when(mContext.getString(R.string.ac_user_name)).thenReturn("");
        //HomeActivity testObj = new HomeActivity(mContext);
    }
}