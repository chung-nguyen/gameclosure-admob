#import <GoogleMobileAds/GoogleMobileAds.h>
#import "PluginManager.h"

@interface AdmobPlugin : GCPlugin<GADBannerViewDelegate>

@property (nonatomic, retain) TeaLeafAppDelegate *appDelegate;
@property (nonatomic, retain) GADBannerView *bannerView;

@end
