package com.hec.app.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.hec.app.activity.OfenPlayLotteryActivity;

public final class IntentUtil
{
    private static Intent getBundleIntent(Context paramContext, Class<?> paramClass, Bundle paramBundle)
    {
        return getNewIntent(paramContext, paramClass).putExtras(paramBundle);
    }

    private static Intent getNewIntent(Context paramContext, Class<?> paramClass)
    {
        return new Intent(paramContext, paramClass);
    }

    public static void redirectToMainActivity(Context paramContext, Class<?> paramClass, int paramInt)
    {
        Activity localActivity = (Activity)paramContext;
        localActivity.setResult(paramInt, getNewIntent(paramContext, paramClass));
        localActivity.finish();
    }

    public static void redirectToMainActivity(Context paramContext, Class<?> paramClass, Bundle paramBundle, int paramInt)
    {
        Activity localActivity = (Activity)paramContext;
        localActivity.setResult(paramInt, getBundleIntent(paramContext, paramClass, paramBundle));
        localActivity.finish();
    }

    public static void redirectToNextActivity(Context paramContext, Class<?> paramClass)
    {
        paramContext.startActivity(getNewIntent(paramContext, paramClass));
//        if(paramContext instanceof Activity && !paramClass.getName().contains("OfenPlayLotteryActivity")){
//            ((Activity) paramContext).finish();
//        }
    }

    public static void redirectToNextActivity(Context paramContext, Class<?> paramClass, Bundle paramBundle)
    {
        paramContext.startActivity(getBundleIntent(paramContext, paramClass, paramBundle));
    }

    public static void redirectToNextActivity(Context paramContext, Class<?> paramClass, String paramString1, String paramString2)
    {
        paramContext.startActivity(getNewIntent(paramContext, paramClass).putExtra(paramString1, paramString2));
    }

    public static void redirectToNextNewActivity(Context paramContext, Class<?> paramClass)
    {
        paramContext.startActivity(getNewIntent(paramContext, paramClass).addFlags(268435456));
    }

    public static void redirectToNextNewActivity(Context paramContext, Class<?> paramClass, Bundle paramBundle)
    {
        paramContext.startActivity(getBundleIntent(paramContext, paramClass, paramBundle).addFlags(268435456));
    }

    public static void redirectToSubActivity(Context paramContext, Class<?> paramClass, int paramInt)
    {
        ((Activity)paramContext).startActivityForResult(getNewIntent(paramContext, paramClass), paramInt);
    }

    public static void redirectToSubActivity(Context paramContext, Class<?> paramClass, Bundle paramBundle, int paramInt)
    {
        ((Activity)paramContext).startActivityForResult(getBundleIntent(paramContext, paramClass, paramBundle), paramInt);
    }
}