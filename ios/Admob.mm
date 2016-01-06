#import "Admob.h"

@implementation AdmobPlugin

/*!
	The plugin must call super dealloc. 
*/
- (void) dealloc {
    self.appDelegate = nil;
    
    [super dealloc];
}

/*!
	The plugin must call super init.
*/	
- (id) init {
    self = [super init];
    if (!self) {
        return nil;
    }
    
    self.appDelegate = nil;
    
    return self;
}

/*!
	Initialize the plugin.
	
	@param manifest
	@param appDelegate
*/	
- (void) initializeWithManifest:(NSDictionary *)manifest appDelegate:(TeaLeafAppDelegate *)appDelegate {
    @try {
        NSLog(@"{admob} Initializing with manifest moPubID:");
        
        self.appDelegate = appDelegate;
    }
    @catch (NSException *exception) {
        NSLog(@"{admob} Failure to get ios:moPubID key from manifest file: %@", exception);
    }
}

/*!
	Show the interstitial advertisement.
	
	@param jsonObject
*/	
- (void) showInterstitial:(NSDictionary *)jsonObject {
    if (self.interstitial != nil && [self.interstitial isReady]) {
        [self.interstitial presentFromRootViewController: self.appDelegate.tealeafViewController];
    }
}

/*!
	Load the interstitial advertisement.
	
	@param jsonObject
		The advertisement options.
*/	
- (void) loadInterstitial:(NSDictionary *)jsonObject {
    @try {
        NSString *adUnitId = jsonObject[@"adUnitId"];
        if (adUnitId == nil)
        {
            adUnitId = @"";
        }
        
        if (self.interstitial == nil) 
        {
            self.interstitial = [[GADInterstitial alloc] initWithAdUnitID:adUnitId];
            self.interstitial.delegate = self;
        }
        
        GADRequest *request = [GADRequest request];
        request.testDevices = @[kGADSimulatorID];
        [self.interstitial loadRequest:request];
    }
    @catch (NSException *exception) {
        NSLog(@"{admob} Failure during interstitial: %@", exception);
        self.interstitial = nil;
    }
}

/*!
	Called when an interstitial advertisement request failed.
*/
- (void)interstitial:(GADInterstitial *)ad didFailToReceiveAdWithError:(GADRequestError *)error {
    NSLog(@"interstitialDidFailToReceiveAdWithError: %@", [error localizedDescription]);
}

/*!
	Called when an interstitial advertisement dismiss the screen.
*/
- (void)interstitialDidDismissScreen:(GADInterstitial *)interstitial {
    GADInterstitial *newInterstitial = [[GADInterstitial alloc] initWithAdUnitID:self.interstitial.adUnitID];
    newInterstitial.delegate = self;
    
    GADRequest *request = [GADRequest request];
    request.testDevices = @[kGADSimulatorID];
    [newInterstitial loadRequest:request];
    
    self.interstitial = newInterstitial;
}

/*!
	Show the banner advertisement.
	
	@param jsonObject
		The advertisement options.
*/	
- (void) showAdView:(NSDictionary *)jsonObject {
    @try {
        NSString *adUnitId = jsonObject[@"adUnitId"];
        if (adUnitId == nil)
        {
            adUnitId = @"";
        }
        
        self.bannerHorizontalAlign = jsonObject[@"horizontalAlign"];
        self.bannerVerticalAlign = jsonObject[@"verticalAlign"];
        
        NSLog(@"{admob} Showing Adview: %@", adUnitId);
        
        if ([self bannerView] == nil)
        {
            self.bannerView = [[GADBannerView alloc] initWithAdSize: [AdmobPlugin parseJSonBannerSize:jsonObject key: @"adSize"]];
            self.bannerView.delegate = self;
            
            [self.appDelegate.tealeafViewController.view addSubview:self.bannerView];
            
            self.bannerView.adUnitID = adUnitId;
            self.bannerView.rootViewController = self.appDelegate.tealeafViewController;
            [self.bannerView loadRequest: [GADRequest request]];
        }
        else
        {
            [self.bannerView setHidden: NO];
        }
    }
    @catch (NSException *exception) {
        NSLog(@"{admob} Failure during ad view: %@", exception);
        self.bannerView = nil;
    }
}

/*!
	Parse the size of banner advertisement.
	
	@param jObject
		The json data.
	@param key
		The key name.
		
	@return
		The Admob AdSize value.
*/	
+ (GADAdSize) parseJSonBannerSize:(NSDictionary *)jObject key:(NSString *) key {
    NSString *s = jObject[key];
    if (s != nil) {
        if ([s isEqualToString:@"banner"]) {
            return kGADAdSizeBanner;
        } else if ([s isEqualToString:@"large_banner"]) {
            return kGADAdSizeLargeBanner;
        } else if ([s isEqualToString:@"medium_rectangle"]) {
            return kGADAdSizeMediumRectangle;
        } else if ([s isEqualToString:@"full_banner"]) {
            return kGADAdSizeFullBanner;
        } else if ([s isEqualToString:@"leaderboard"]) {
            return kGADAdSizeLeaderboard;
        }
    }
    return kGADAdSizeSmartBannerPortrait;
}

/*!
	Load or reload the showing banner advertisement.
*/
- (void) loadAdView:(NSDictionary *)jsonObject {
    @try {
        NSLog(@"{admob} Reloading adview");
        
        [self.bannerView loadRequest: [GADRequest request]];
    }
    @catch (NSException *exception) {
        NSLog(@"{admob} Failure during interstitial: %@", exception);
    }
}

/*!
	Hide the banner advertisement.
*/
- (void) hideAdView:(NSDictionary *)jsonObject {
    @try {
        NSLog(@"{admob} Hiding adview");
        
        [self.bannerView setHidden: YES];
    }
    @catch (NSException *exception) {
        NSLog(@"{moPub} Failure during interstitial: %@", exception);
    }
}

/*!
	Called when the banner advertisement is loaded.
*/
- (void)adViewDidReceiveAd:(GADBannerView *)bannerView {
    [UIView beginAnimations:@"BannerSlide" context:nil];
    
    CGFloat viewWidth = self.appDelegate.tealeafViewController.view.frame.size.width;
    CGFloat viewHeight = self.appDelegate.tealeafViewController.view.frame.size.height;
    
    CGFloat bannerWidth = bannerView.frame.size.width;
    CGFloat bannerHeight = bannerView.frame.size.height;
    
    CGFloat x = 0.0f;
    CGFloat y = 0.0f;
    
    if ([self.bannerHorizontalAlign isEqualToString:@"center"]) {
        x = (viewWidth - bannerWidth) / 2;

    } else if ([self.bannerHorizontalAlign isEqualToString:@"right"]) {
        x = (viewWidth - bannerWidth);
    }
    
    if ([self.bannerVerticalAlign isEqualToString:@"middle"]) {
        y = (viewHeight - bannerHeight) / 2;
        
    } else if ([self.bannerVerticalAlign isEqualToString:@"bottom"]) {
        y = viewHeight - bannerHeight;
    }
    
    bannerView.frame = CGRectMake(x, y, bannerWidth, bannerHeight);
    [UIView commitAnimations];
}

/*!
	Called when the banner advertisement fails to load.
*/
- (void)adView:(GADBannerView *)bannerView didFailToReceiveAdWithError:(GADRequestError *)error {
    NSLog(@"adView:didFailToReceiveAdWithError:%@", [error localizedDescription]);
}

@end
