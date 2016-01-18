# Game Closure Devkit Plugin : Admob

## Installation
Install the module using using the standard devkit install process:

~~~
devkit install https://github.com/gameclosure/admob.git
~~~

## Admob account
You need to have Advertisement Unit Id, so that Google knows who to pay for the advertisement impressions.

To sign-up for Google Admob, please go to this website:
http://www.google.com/admob/

## Usage
The admob plugin supports both interstitial and banner advertisements. You will need only 2 simple calls to have admob works, without any setup on client source code.

At the top of your game's `src/Application.js`:
~~~
import admob;
~~~

### Interstitial advertisement

To run interstitial advertisement, you need to load it first with parameter `adUnitId`. This parameter is the advertisement unit id you registered with admob, make sure that the unit is suitable for interstitial type or else nothing will be shown.

To handle the advertisement events, you need to provide the handler functions.

~~~
admob.loadInterstitial({
	adUnitId: "ca-app-pub-1234567890123456/123456789",
	onAdAvailable: function () {
		// Do something when the advertisement is ready
	},
	onAdNotAvailable: function () {
		// Do something when the advertisement could not be loaded.
	},
	onAdDismissed: function () {
		// Do something when the advertisement is closed by user.
	}
});
~~~

To show the interstitial ad, just call the simple show method. If the ad is not ready, this call will be ignored.

~~~
admob.showInterstitial();
~~~

### Banner advertisement
To show banner advertisement, you need to create the view first. This way, the banner is downloaded from Google Admob, and when it is fully loaded, you can start showing.

To handle the advertisement events, you need to provide the handler functions.

~~~
admob.showAdView({
	adUnitId: "ca-app-pub-8112894826901791/3036535668",
	adSize: "smart_banner",
	onAdAvailable: function () {
		// Do something when the advertisement is ready
	},
	onAdNotAvailable: function () {
		// Do something when the advertisement could not be loaded.
	},
	onAdDismissed: function () {
		// Do something when the advertisement is closed by user.
	}
});
~~~

The `adSize` parameter can be either:
+ banner
+ large_banner 
+ medium_rectangle 
+ full_banner 
+ leaderboard 
+ smart_banner

After the banner is fully loaded, you can show it by calling `showAdView`

~~~
admob.showAdView({
	horizontalAlign: "center",
	verticalAlign: "top"
});
~~~	

The `horizontalAlign` can be either: left, center, right.

The `verticalAlign` can be either: top, middle, bottom.

To hide the banner ad, just call `hideAdView`.
~~~
hideAdView();
~~~

To reload the banner in order to offer new advertisement, call `reloadAdView`.
~~~
reloadAdView();
~~~

## Example
~~~
import ui.TextView as TextView;
import admob;

exports = Class(GC.Application, function () {

  this.initUI = function () {

    var tvStatus = new TextView({
		  superview: this.view,
		  text: 'Hello admob!',
		  color: 'white',
		  x: 0,
		  y: 400,
		  width: this.view.style.width,
		  height: 100
    });
    
    admob.loadInterstitial({
        adUnitId: "ca-app-pub-1234567890123456/123456789",
        onAdAvailable: function () {
            admob.showInterstitial();
            tvStatus.setText('Instertial ad was loaded!');
        },
        onAdNotAvailable: function () {
            tvStatus.setText('Instertial ad errors!');
        },
        onAdDismissed: function () {
            tvStatus.setText('Instertial ad closed!');
        }
    });
    
	admob.createAdView({
		adUnitId: "ca-app-pub-8112894826901791/3036535668",
		adSize: "smart_banner",
        onAdAvailable: function () {
            admob.showAdView({
                horizontalAlign: "center",
                verticalAlign: "top"      
            });
            tvStatus.setText('Banner ad was loaded!');
        },		
	});
  };

  this.launchUI = function () {
  };

});
~~~
