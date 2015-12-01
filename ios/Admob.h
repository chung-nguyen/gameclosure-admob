#import <GoogleMobileAds/GoogleMobileAds.h>
#import "PluginManager.h"

@interface AdmobPlugin : GCPlugin<GADBannerViewDelegate, GADInterstitialDelegate>

@property (nonatomic, retain) TeaLeafAppDelegate *appDelegate;
@property (nonatomic, strong) GADBannerView *bannerView;
@property (nonatomic, strong) GADInterstitial *interstitial;

@property (nonatomic, retain) NSString *bannerHorizontalAlign;
@property (nonatomic, retain) NSString *bannerVerticalAlign;

+ (GADAdSize) parseJSonBannerSize:(NSDictionary *)jObject key:(NSString *) key;

@end
