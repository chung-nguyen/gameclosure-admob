#import <GoogleMobileAds/GoogleMobileAds.h>
#import "PluginManager.h"

/*!
	@class AdmobPlugin
	@discussion Objective C wrapper over Google Admob functionalities for use as a devkit plugin.
*/
@interface AdmobPlugin : GCPlugin<GADBannerViewDelegate, GADInterstitialDelegate>

/*!
	@property appDelegate
	@brief The devkit application delegate.
*/
@property (nonatomic, retain) TeaLeafAppDelegate *appDelegate;

/*!
	@property bannerView
	@brief The advertisement banner view object.
*/
@property (nonatomic, strong) GADBannerView *bannerView;

/*!
	@property interstitial
	@brief The interstitial advertisement object.
*/
@property (nonatomic, strong) GADInterstitial *interstitial;

/*!
	@property bannerHorizontalAlign
	@brief The selected horizontal alignment of the banner.
*/
@property (nonatomic, retain) NSString *bannerHorizontalAlign;

/*!
	@property bannerVerticalAlign
	@brief The selected vertical alignment of the banner.
*/
@property (nonatomic, retain) NSString *bannerVerticalAlign;

/*!
	Parse the banner size value from javascript parameter.
	@param jObject
		The javascript json data.
	@param key
		The key name.
	@result
		Return the Admob AdSize value.
*/
+ (GADAdSize) parseJSonBannerSize:(NSDictionary *)jObject key:(NSString *) key;

@end
