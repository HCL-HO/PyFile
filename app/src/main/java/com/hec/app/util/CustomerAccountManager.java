package com.hec.app.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.hec.app.entity.CustomerInfo;


/**
 * 包装与客户相关的一系列操作。包括设置与获取用户登陆后，从服务端获取的的AuthenticationKey与客户信息等。
 * 
 */
public final class CustomerAccountManager {

	/**
	 * 该回调表示，当用户登录成功，执行的一段逻辑。
	 */
	public interface OnCheckLoginListener extends Parcelable {
		/**
		 * 登录成功后，执行该方法。
		 * 
		 * @param customer
		 *            登录的客户信息。
		 * @param bundle
		 * 			    回调方法中需要用到的数据
		 */
		void OnLogined(CustomerInfo customer, Bundle bundle);
	}
	
	public static final String INTENT_ONLOGIN_SUCCESSFULLY_CALLBACK = "INTENT_ONLOGIN_SUCCESSFULLY_CALLBACK";
	public static final String INTENT_ONLOGIN_CALLBACK_PARAMS= "INTENT_ONLOGIN_CALLBACK_PARAMS";
	
	private static CustomerAccountManager instance = new CustomerAccountManager();

	private String mAuthenticationKey = "";
	private CustomerInfo mCustomer;

	private CustomerAccountManager() {
		
	}

	/**
	 * 获取 {@link CustomerAccountManager} 的唯一实例。
	 * 
	 * @return {@link CustomerAccountManager} 的唯一实例。
	 */
	public static CustomerAccountManager getInstance() {
		return instance;
	}

	/**
	 * 
	 * 客户登陆成功后，服务端会返回一个<code>String</code>类型的Key，以后调用需要登陆成功后才能调用的服务，要把这个Key加入
	 * 到Http Header中提交给服务端。此方法就是在登陆成功后获取到服务端返回的Key，保存它的方法。
	 * <p>
	 * 注销时需要调用此方法清空Key。
	 * 
	 * @param key
	 *            服务端会返回的Key。
	 * 
	 * @see #getAuthenticationKey()
	 */
	public void setAuthenticationKey(String key) {
		mAuthenticationKey = key;
	}

	/**
	 * 获取用户Key。
	 * 
	 * @return 获取用户Key。
	 * 
	 * @see #setAuthenticationKey(String)
	 */
	public String getAuthenticationKey() {
		return mAuthenticationKey;
	}

	/**
	 * 打开一个Activity。若用户未登录，则弹出登录框要求用户登录，登陆成功后跳转至目标Activity。
	 * 若用户已登录，则直接跳转至目标Activity。
	 * 
	 * @param activity
	 *            当前Activity
	 * @param cls
	 *            目标Activity
	 */
	public void checkLogin(Activity activity, Class<?> cls) {
		if (mCustomer == null) {
			// TODO: 实现登录逻辑
		} else {
			Intent intent = new Intent(activity, cls);
			activity.startActivity(intent);
		}
	}

	/**
	 * 执行一个{@link OnCheckLoginListener}类型的回调。若用户未登录，则弹出登录框要求用户登录，登陆成功后执行之。
	 * 若用户已登录，则直接执行之。
	 * 
	 * @param activity
	 *            当前Activity
	 * @param loginClass
	 *            登录页面
	 * @param listener
	 *            期望执行的回调
	 * @param bundle 
	 * 			     回调中可能用到的数据
	 */
	public void checkLogin(Activity activity, Class<?> loginClass, OnCheckLoginListener listener,Bundle bundle) {
		
		if (mCustomer == null) {
			Intent intent = new Intent(activity, loginClass);
			intent.putExtra(INTENT_ONLOGIN_SUCCESSFULLY_CALLBACK, listener);
			intent.putExtra(INTENT_ONLOGIN_CALLBACK_PARAMS, bundle);
			activity.startActivity(intent);
		} else {
			listener.OnLogined(mCustomer,bundle);
		}
	}

	/*
	 * 打开一个Activity，不管用户是否已经登录，都弹出登录框要求用户登录，登陆成功后打开之。
	 * 
	 * @param activity
	 *            当前Activity
	 * @param cls
	 *            目标Activity
	 */
	public void forceUserLogin(Activity activity, Class<?> loginClass,OnCheckLoginListener listener,Bundle bundle) {
		
		Intent intent = new Intent(activity, loginClass);
		intent.putExtra(INTENT_ONLOGIN_SUCCESSFULLY_CALLBACK, listener);
		intent.putExtra(INTENT_ONLOGIN_CALLBACK_PARAMS, bundle);
		activity.startActivity(intent);
	}

	/**
	 * @return 获取客户信息。
	 */
	public CustomerInfo getCustomer() {
		return mCustomer;
	}

	/**
	 * 清空客户信息。
	 */
	public void clearCustomer() {
		mCustomer = null;
		mAuthenticationKey = null;
	}

	/**
	 * 设置客户信息。
	 * 
	 * @param customer
	 *            客户信息
	 */
	public void setCustomer(CustomerInfo customer) {
		mCustomer = customer;
	}
	
	public Boolean isCustomerLogined() {
		CustomerInfo customer = CustomerAccountManager.getInstance()
				.getCustomer();
		return !(customer == null || customer.getUserID() == null
				|| customer.getUserID().length() == 0);

	}

	public void logOut(){
		getInstance().setCustomer(null);
	}
}