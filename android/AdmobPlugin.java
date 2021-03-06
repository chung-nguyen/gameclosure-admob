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

/**
 * This is a Android interface that wraps Google Admob functionalities to use as a
 * Gameclosure devkit plugin.
 */
public class AdmobPlugin implements IPlugin {

	// The logging tag for debug
	private String TAG = "{admob}";

	// The devkit application activity
	private Activity mActivity;
	
	// Interstitial advertisement object
	private InterstitialAd mInterstitial = null;
	
	// Banner advertisement object
	private AdView mAdView = null;
	private int mAdViewGravity = Gravity.CENTER;
    private Object mAdViewLock = new Object();
    private boolean mIsAdViewVisibile = false;
    
    public class AdmobInterstitialAdAvailable extends com.tealeaf.event.Event {
        public AdmobInterstitialAdAvailable() {
            super("AdmobInterstitialAdAvailable");
        }
    }

    public class AdmobInterstitialAdNotAvailable extends com.tealeaf.event.Event {
        public AdmobInterstitialAdNotAvailable() {
            super("AdmobInterstitialAdNotAvailable");
        }
    }

    public class AdmobInterstitialAdDismissed extends com.tealeaf.event.Event {
        public AdmobInterstitialAdDismissed () {
            super("AdmobInterstitialAdDismissed");
        }
    }
    
    public class AdmobBannerAdAvailable extends com.tealeaf.event.Event {
        public AdmobBannerAdAvailable() {
            super("AdmobBannerAdAvailable");
        }
    }

    public class AdmobBannerAdNotAvailable extends com.tealeaf.event.Event {
        public AdmobBannerAdNotAvailable() {
            super("AdmobBannerAdNotAvailable");
        }
    }

    public class AdmobBannerAdDismissed extends com.tealeaf.event.Event {
        public AdmobBannerAdDismissed () {
            super("AdmobBannerAdDismissed");
        }
    }
	
	/** 
     * Class constructor.
     */
	public void AdmobPlugin() {}

	/**
	 * Handle the event of plugin creation.
	 *
	 * @param activity The activity object of devkit application.
	 * @param savedInstanceState The saved state of the activity instance.	 
	 */
	public void onCreate(Activity activity, Bundle savedInstanceState) {
		mActivity = activity;
	}

	/**
	 * Handle the event of application destruction.	 
	 */
	public void onDestroy() {
		if (mAdView != null) {
			mAdView.destroy();
			mAdView = null;
		}
	}
	
	public void onCreateApplication(Context applicationContext) {}
		
	public void onResume() {}

	public void onStart() {}

	public void onPause() {}

	public void onStop() {}

	public void onNewIntent(Intent intent) {}

	public void setInstallReferrer(String referrer) {}

	public void onActivityResult(Integer request, Integer result, Intent data) {}

	public boolean consumeOnBackPressed() {
		return true;
	}

	public void onBackPressed() {}
	
	/**
	 * Show the interstitial advertisement.
	 *
	 * @param jsonData Not used.
	 */	
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

	/**
	 * Load the interstitial advertisement.
	 *
	 * @param jsonData Options for the advertisement.
	 */		
	public void loadInterstitial(String jsonData) {
		logger.log(TAG, "Creating interstitial view");
		
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
						   
						mInterstitial.setAdListener(new AdListener() {
							@Override
							public void onAdClosed() {
								EventQueue.pushEvent(new AdmobInterstitialAdDismissed());
							}
							
							@Override
							public void onAdFailedToLoad(int errorCode) {								
                                EventQueue.pushEvent(new AdmobInterstitialAdNotAvailable());
							}
                            
                            @Override
							public void onAdLoaded() {
                                EventQueue.pushEvent(new AdmobInterstitialAdAvailable());
							}
						});
					}
				
     				mInterstitial.setAdUnitId(adUnitId);
     				
					AdRequest adRequest = new AdRequest.Builder()
							.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
							.build();
			
					mInterstitial.loadAd(adRequest);
					
				} catch (Exception ex) {
					logger.log(TAG, ex.toString());
					mInterstitial = null;
                    
                    EventQueue.pushEvent(new AdmobInterstitialAdNotAvailable());
				}
			}
		});
	}

	/**
	 * Create the banner advertisement.
	 *
	 * @param jsonData Options for the advertisement.
	 *
	 * @see #parseJSonBannerSize(JSONObject, String)
	 * @see #parseJsonGravity(JSONObject, String)
	 */	
	public void createAdView(String jsonData) {
		logger.log(TAG, "Creating ad view");        

		JSONObject jObject = null;
		try {
			jObject = new JSONObject(jsonData);
		} catch (Exception ex) {
			logger.log(TAG, ex.toString());
		}

		final String adUnitId = getJsonString(jObject, "adUnitId");
		final AdSize adSize = parseJSonBannerSize(jObject, "adSize");

		mActivity.runOnUiThread(new Runnable() {
			public void run() {
                try {
                    if (mAdView == null) {
                        mAdView = new AdView(mActivity);    
                        
                        AdListener adListener = new AdListener() {@Override
                            public void onAdLoaded() {
                                logger.log(TAG, "ad loaded");        
                                
                                EventQueue.pushEvent(new AdmobBannerAdAvailable());               
                            }

                            @Override
                            public void onAdFailedToLoad(int errorCode) {
                                logger.log(TAG, "no ad loaded");
                                //logger.log(TAG, getErrorReason(errorCode));
                                
                                EventQueue.pushEvent(new AdmobBannerAdNotAvailable());
                            }

                            @Override
                            public void onAdClosed() {
                                logger.log(TAG, "ad killed");
                                
                                EventQueue.pushEvent(new AdmobBannerAdDismissed());
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
                    }
                    
                    mAdView.setAdSize(adSize);
                    mAdView.setAdUnitId(adUnitId);                    
                    loadAdView();

                } catch (Exception ex) {
                    logger.log(TAG, ex.toString());
                    mAdView = null;
                    
                    EventQueue.pushEvent(new AdmobBannerAdNotAvailable());
                }
			}
		});
	}

	/**
	 * Add the banner advertisement view to the root view of the devkit application.
	 */	
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

	/**
	 * Remove the banner advertisement view from the root view of the devkit application.
	 */	
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

	/**
	 * Load the banner advertisement. To be called by javascript.
	 *
	 * @param jsonData Not used.
	 */	
	public void reloadAdView(String jsonData) {
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				loadAdView();
			}
		});
	}

	/**
	 * Load the banner advertisement.	 
	 */	
	private void loadAdView() {
		if (mAdView != null) {			
			AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
			mAdView.loadAd(adRequest);
		}
	}
    
    /**
	 * Show the banner advertisement.
	 *
	 * @param jsonData Not used.
	 */	
    public void showAdView(String jsonData) {
		JSONObject jObject = null;
		try {
			jObject = new JSONObject(jsonData);
		} catch (Exception ex) {
			logger.log(TAG, ex.toString());
		}
                
        final int horizontalGravity = parseJsonGravity(jObject, "horizontalAlign");
		final int verticalGravity = parseJsonGravity(jObject, "verticalAlign");
		
		mAdViewGravity = horizontalGravity | verticalGravity;
        
        if (mAdView != null) {
			mActivity.runOnUiThread(new Runnable() {
				public void run() {
					addAdViewToRoot();
				}
			});
		}
    }

	/**
	 * Hide the banner advertisement.
	 *
	 * @param jsonData Not used.
	 */	
	public void hideAdView(String jsonData) {
		if (mAdView != null) {
			mActivity.runOnUiThread(new Runnable() {
				public void run() {
					removeAdViewFromRoot();
				}
			});
		}
	}

	/**
	 * Parse the boolean value from javascript.
	 *
	 * @param jObject	The json object.
	 * @param key		The value key.
	 *
	 * @return The java boolean value.
	 */	
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

	/**
	 * Parse the string value from javascript.
	 *
	 * @param jObject	The json object.
	 * @param key		The value key.
	 *
	 * @return The java string value.
	 */	
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

	/**
	 * Parse the Google Admob AdSize value from javascript string.
	 *
	 * @param jObject	The json object.
	 * @param key		The value key.
	 *
	 * @return The AdSize value.
	 */	
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

	/**
	 * Parse the Android View Gravity value from javascript.
	 *
	 * @param jObject	The json object.
	 * @param key		The value key.
	 *
	 * @return The Gravity constant value.
	 */	
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
