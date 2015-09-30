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

- (void) showAdView:(NSDictionary *)jsonObject {
    @try {
        NSString *adUnitId = jsonObject[@"adUnitId"];
        if (adUnitId == nil)
        {
            adUnitId = @"";
        }
        
        NSLog(@"{admob} Showing Adview: %@", adUnitId);
        
        if ([self bannerView] == nil)
        {
            self.bannerView = [[GADBannerView alloc] initWithAdSize: kGADAdSizeSmartBannerPortrait];
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
        NSLog(@"{admob} Failure during interstitial: %@", exception);
    }
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
    bannerView.frame = CGRectMake(0.0,
                                  self.appDelegate.tealeafViewController.view.frame.size.height -
                                  bannerView.frame.size.height,
                                  bannerView.frame.size.width,
                                  bannerView.frame.size.height);
    [UIView commitAnimations];
}

- (void)adView:(GADBannerView *)bannerView didFailToReceiveAdWithError:(GADRequestError *)error {
    NSLog(@"adView:didFailToReceiveAdWithError:%@", [error localizedDescription]);
}

@end
