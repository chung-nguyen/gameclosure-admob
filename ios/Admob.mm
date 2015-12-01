#import "Admob.h"

@implementation AdmobPlugin

// The plugin must call super dealloc.
- (void) dealloc {
    self.appDelegate = nil;
    
    [super dealloc];
}

// The plugin must call super init.
- (id) init {
    self = [super init];
    if (!self) {
        return nil;
    }
    
    self.appDelegate = nil;
    
    return self;
}

- (void) initializeWithManifest:(NSDictionary *)manifest appDelegate:(TeaLeafAppDelegate *)appDelegate {
    @try {
        NSLog(@"{admob} Initializing with manifest moPubID:");
        
        self.appDelegate = appDelegate;
    }
    @catch (NSException *exception) {
        NSLog(@"{admob} Failure to get ios:moPubID key from manifest file: %@", exception);
    }
}

- (void) showInterstitial:(NSDictionary *)jsonObject {
    if (self.interstitial != nil && [self.interstitial isReady]) {
        [self.interstitial presentFromRootViewController: self.appDelegate.tealeafViewController];
    }
}

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

/// Called when an interstitial ad request failed.
- (void)interstitial:(GADInterstitial *)ad didFailToReceiveAdWithError:(GADRequestError *)error {
    NSLog(@"interstitialDidFailToReceiveAdWithError: %@", [error localizedDescription]);
}

- (void)interstitialDidDismissScreen:(GADInterstitial *)interstitial {
    GADInterstitial *newInterstitial = [[GADInterstitial alloc] initWithAdUnitID:self.interstitial.adUnitID];
    newInterstitial.delegate = self;
    
    GADRequest *request = [GADRequest request];
    request.testDevices = @[kGADSimulatorID];
    [newInterstitial loadRequest:request];
    
    self.interstitial = newInterstitial;
}

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

- (void) loadAdView:(NSDictionary *)jsonObject {
    @try {
        NSLog(@"{admob} Reloading adview");
        
        [self.bannerView loadRequest: [GADRequest request]];
    }
    @catch (NSException *exception) {
        NSLog(@"{admob} Failure during interstitial: %@", exception);
    }
}

- (void) hideAdView:(NSDictionary *)jsonObject {
    @try {
        NSLog(@"{admob} Hiding adview");
        
        [self.bannerView setHidden: YES];
    }
    @catch (NSException *exception) {
        NSLog(@"{moPub} Failure during interstitial: %@", exception);
    }
}

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

- (void)adView:(GADBannerView *)bannerView didFailToReceiveAdWithError:(GADRequestError *)error {
    NSLog(@"adView:didFailToReceiveAdWithError:%@", [error localizedDescription]);
}

@end
