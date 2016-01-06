/**
 * @file 
 * The Gameclosure Devkit plugin for using Google Admob.
 */
var admob = Class(function () {

	/**
	 * Load and then show the advertisement banner.
	 *
	 * @param {object} opts
	 *    Options for the banner advertisement.
	 *    + adUnitId {string}			The advertisement unit id from Goole Admob account.
 	 *    + adUnitId {string}			The advertisement unit id from Goole Admob account.
	 *    + adSize {string}				The size of the advertisement.
	 *    + horizontalAlign {string}	The horizontal placement. 
	 *    + verticalAlign {string}		The vertical placement.
	 *	  + reload {boolean}			Reload the banner or not? 
	 */
    this.showAdView = function (opts) {
        NATIVE.plugins.sendEvent("AdmobPlugin", "showAdView", JSON.stringify(opts));
    }

	/**
	 * Hide the advertisement banner.	 
	 */
    this.hideAdView = function () {
        NATIVE.plugins.sendEvent("AdmobPlugin", "hideAdView", JSON.stringify({}));
    }

	/**
	 * Reload the current advertisement banner.	 
	 */
    this.loadAdView = function () {
        NATIVE.plugins.sendEvent("AdmobPlugin", "loadAdView", JSON.stringify({}));
    }

	/**
	 * Show the interstitial advertisement.	 
	 */    
    this.showInterstitial = function() {
        NATIVE.plugins.sendEvent("AdmobPlugin", "showInterstitial", JSON.stringify({}));
    }
    
	/**
	 * Load or reload the interstitial advertisement.
	 *
	 * @param {object} opts
	 *    Options for the banner advertisement.
	 *    + adUnitId {string} -- The advertisement unit id from Goole Admob account.
	 */    
    this.loadInterstitial = function(opts) {
        NATIVE.plugins.sendEvent("AdmobPlugin", "loadInterstitial", JSON.stringify(opts));
    }
});

exports = new admob();
