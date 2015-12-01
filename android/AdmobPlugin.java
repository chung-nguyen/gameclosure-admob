package com.tealeaf.plugin.plugins;
import com.tealeaf.TeaLeaf;
import com.tealeaf.logger;
import com.tealeaf.plugin.IPlugin;
import com.tealeaf.EventQueue;
import com.tealeaf.event.*;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import android.os.Bundle;
import android.content.pm.PackageManager;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.FrameLayout;
import android.view.Gravity;

import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdListener;

public class AdmobPlugin implements IPlugin {

	private String TAG = "{admob}";

	private Activity mActivity;
	private InterstitialAd mInterstitial = null;
	private AdView mAdView = null;
	private int mAdViewGravity = Gravity.CENTER;
    private Object mAdViewLock = new Object();
    private boolean mIsAdViewVisibile = false;
    private boolean mShowAdView = false;
	private boolean mInterstitialHasError = false;
	private boolean mAdViewHasError = false;

	public void AdmobPlugin() {}

	public void onCreateApplication(Context applicationContext) {}


	public void onCreate(Activity activity, Bundle savedInstanceState) {
		mActivity = activity;
	}

	public void onResume() {}

	public void onStart() {}

	public void onPause() {}

	public void onStop() {}

	public void onDestroy() {
		if (mAdView != null) {
			mAdView.destroy();
			mAdView = null;
		}
	}

	public void onNewIntent(Intent intent) {}

	public void setInstallReferrer(String referrer) {}

	public void onActivityResult(Integer request, Integer result, Intent data) {}

	public boolean consumeOnBackPressed() {
		return true;
	}

	public void onBackPressed() {}
	
	public boolean hasInterstitialError() {
		return mInterstitialHasError;
	}
	
	public void showInterstitial(String jsonData) {
		logger.log(TAG, "Showing interstitial view");
		
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				if (mInterstitial != null && mInterstitial.isLoaded())
				{
					logger.log(TAG, "Show succeed");
					mInterstitial.show();
				}
			}
		});
	}
	
	public void loadInterstitial(String jsonData) {
		logger.log(TAG, "Creating interstitial view");
		mInterstitialHasError = false;
        
		JSONObject jObject = null;
		try {
			jObject = new JSONObject(jsonData);
		} catch (Exception ex) {
			logger.log(TAG, ex.toString());
		}

		final String adUnitId = getJsonString(jObject, "adUnitId");
		
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				try {
					if (mInterstitial == null) {
						
						mInterstitial = new InterstitialAd(mActivity);
       					mInterstitial.setAdUnitId(adUnitId);
						   
						mInterstitial.setAdListener(new AdListener() {
							@Override
							public void onAdClosed() {
								AdRequest adRequest = new AdRequest.Builder()
										.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
										.build();
						
								mInterstitial.loadAd(adRequest);
							}
							
							@Override
							public void onAdFailedToLoad(int errorCode) {
								logger.log(TAG, "no interstitial ad loaded");
								
								mInterstitialHasError = true;
							}
						});
					}
				
					AdRequest adRequest = new AdRequest.Builder()
							.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
							.build();
			
					mInterstitial.loadAd(adRequest);
					
				} catch (Exception ex) {
					logger.log(TAG, ex.toString());
					mInterstitial = null;
				}
			}
		});
	}

	public void showAdView(String jsonData) {
		logger.log(TAG, "Creating ad view");
        mShowAdView = true;

		JSONObject jObject = null;
		try {
			jObject = new JSONObject(jsonData);
		} catch (Exception ex) {
			logger.log(TAG, ex.toString());
		}

		final String adUnitId = getJsonString(jObject, "adUnitId");
		final AdSize adSize = parseJSonBannerSize(jObject, "adSize");
		final int horizontalGravity = parseJsonGravity(jObject, "horizontalAlign");
		final int verticalGravity = parseJsonGravity(jObject, "verticalAlign");
		final boolean isReloading = getJsonBoolean(jObject, "reload");

		mAdViewGravity = horizontalGravity | verticalGravity;

		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				if (mAdView == null) {
					try {
						mAdView = new AdView(mActivity);
						mAdView.setAdSize(adSize);
						mAdView.setAdUnitId(adUnitId);

						loadAdView();

						AdListener adListener = new AdListener() {@Override
							public void onAdLoaded() {
								logger.log(TAG, "ad loaded");
								logger.log(TAG, "Trying to show ad");
                                
                                if (mShowAdView) {
								    addAdViewToRoot();
                                }
							}

							@Override
							public void onAdFailedToLoad(int errorCode) {
								logger.log(TAG, "no ad loaded");
								//logger.log(TAG, getErrorReason(errorCode));
								mAdViewHasError = true;
							}

							@Override
							public void onAdClosed() {
								logger.log(TAG, "ad killed");
							}

							@Override
							public void onAdLeftApplication() {
								// Ad left application
							}

							@Override
							public void onAdOpened() {
								// Ad opened
							}
						};
						mAdView.setAdListener(adListener);

					} catch (Exception ex) {
						logger.log(TAG, ex.toString());
						mAdView = null;
					}
				} else {
					if (isReloading) {
						loadAdView();
					} else {
						addAdViewToRoot();
					}
				}
			}
		});
	}

	private void addAdViewToRoot() {

        synchronized (mAdViewLock) {
            if (!mIsAdViewVisibile) {            
                mIsAdViewVisibile = true;

                /// Set the initial frame
                FrameLayout.LayoutParams initialFrm = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                mAdViewGravity);

                final ViewGroup windowContentView = (ViewGroup) mActivity.getWindow().getDecorView().getRootView().findViewById(android.R.id.content);
                windowContentView.addView(mAdView, initialFrm);
                windowContentView.requestLayout();
            }
        }
	}

	private void removeAdViewFromRoot() {
        synchronized (mAdViewLock) {
            if (mIsAdViewVisibile) {
                mIsAdViewVisibile = false;
                
                final ViewGroup windowContentView = (ViewGroup) mActivity.getWindow().getDecorView().getRootView().findViewById(android.R.id.content);
                windowContentView.removeView(mAdView);
                windowContentView.requestLayout();
            }
        }
	}

	public void loadAdView(String jsonData) {
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				loadAdView();
			}
		});
	}

	private void loadAdView() {
		if (mAdView != null) {
			mAdViewHasError = false;
			
			AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
			mAdView.loadAd(adRequest);
		}
	}

	public void hideAdView(String jsonData) {
        mShowAdView = false;
        
		if (mAdView != null) {
			mActivity.runOnUiThread(new Runnable() {
				public void run() {
					removeAdViewFromRoot();
				}
			});
		}
	}

	private static boolean getJsonBoolean(JSONObject jObject, String key) {
		if (jObject == null) {
			return false;
		}

		boolean res = false;
		try {
			res = jObject.getBoolean(key);
		} catch (Exception ex) {}
		return res;
	}

	private static String getJsonString(JSONObject jObject, String key) {
		if (jObject == null) {
			return "";
		}

		String res = "";
		try {
			res = jObject.getString(key);
		} catch (Exception ex) {}
		return res;
	}

	private static AdSize parseJSonBannerSize(JSONObject jObject, String key) {
		String s = getJsonString(jObject, key);
		if (s.equals("banner")) {
			return AdSize.BANNER;
		} else if (s.equals("large_banner")) {
			return AdSize.LARGE_BANNER;
		} else if (s.equals("medium_rectangle")) {
			return AdSize.MEDIUM_RECTANGLE;
		} else if (s.equals("full_banner")) {
			return AdSize.FULL_BANNER;
		} else if (s.equals("leaderboard")) {
			return AdSize.LEADERBOARD;
		}

		return AdSize.SMART_BANNER;
	}

	private static int parseJsonGravity(JSONObject jObject, String key) {
		String s = getJsonString(jObject, key);
		if (s.equals("left")) {
			return Gravity.LEFT;
		} else if (s.equals("right")) {
			return Gravity.RIGHT;
		} else if (s.equals("center")) {
			return Gravity.CENTER_HORIZONTAL;
		} else if (s.equals("top")) {
			return Gravity.TOP;
		} else if (s.equals("bottom")) {
			return Gravity.BOTTOM;
		} else if (s.equals("middle")) {
			return Gravity.CENTER_VERTICAL;
		}

		return Gravity.CENTER;
	}
}
