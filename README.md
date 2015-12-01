# Game Closure Devkit Plugin : Admob

## Installation
Install the module using using the standard devkit install process:

~~~
devkit install https://github.com/gameclosure/gameclosure-admob.git#v2.0.1
~~~


## Usage
The admob plugin supports both interstitial and banner advertisements. You will need only 2 simple calls to have admob works, without any setup on client source code.

At the top of your game's `src/Application.js`:
~~~
import admob;
~~~

### Interstitial ad

To run interstitial ad, you need to load it first with parameter `adUnitId`:

~~~
admob.loadInterstitial({
	adUnitId: "ca-app-pub-1234567890123456/123456789"
});
~~~

To show the interstitial ad, just call the simple show method. If the ad is not ready, this call will be ignored.

~~~
admob.showInterstitial();
~~~

### Banner ad
Banner ad requires only one call to show, but with more parameters:

~~~
admob.showAdView({
	adUnitId: "ca-app-pub-8112894826901791/3036535668",
	adSize: "smart_banner",
	horizontalAlign: "center",
	verticalAlign: "top",
	reload: true,
});
~~~

The `adSize` parameter can be either:
+ banner
+ large_banner 
+ medium_rectangle 
+ full_banner 
+ leaderboard 
+ smart_banner

The `horizontalAlign` can be either: left, center, right.

The `verticalAlign` can be either: top, middle, bottom.

To hide the banner ad, just call `hideAdView`. To reload the banner, call `loadAdView`.
