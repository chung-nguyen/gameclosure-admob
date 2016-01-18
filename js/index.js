/**
 * @file 
 * The Gameclosure Devkit plugin for using Google Admob.
 */

var bannerOpts;
var interstitialOpts;

var admob = Class(function () {

	/**
	 * Create the advertisement banner for showing later.
	 *
	 * @param {object} opts
	 *    Options for the banner advertisement.
	 *    + adUnitId {string}			The advertisement unit id from Goole Admob account.
 	 *    + adUnitId {string}			The advertisement unit id from Goole Admob account.
	 *    + adSize {string}				The size of the advertisement.
     *    + onAdAvailable {function}    Handler when the advertisement is ready to show.
     *    + onAdNotAvailable {function} Handler when the advertisement could not be loaded.
     *    + onAdDismissed {function}    Handler when the advertisement is closed by user.
	 */
    this.createAdView = function (opts) {
        NATIVE.plugins.sendEvent("AdmobPlugin", "createAdView", JSON.stringify(opts));
        bannerOpts = opts;
    }

    /**
	 * Show the advertisement banner.
	 *
	 * @param {object} opts
	 *    + horizontalAlign {string}	The horizontal placement. 
	 *    + verticalAlign {string}		The vertical placement. 
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
    this.reloadAdView = function () {
        NATIVE.plugins.sendEvent("AdmobPlugin", "reloadAdView", JSON.stringify({}));
    }

	/**
	 * Show the interstitial advertisement.	 
	 */
    this.showInterstitial = function () {
        NATIVE.plugins.sendEvent("AdmobPlugin", "showInterstitial", JSON.stringify({}));
    }
    
	/**
	 * Load or reload the interstitial advertisement.
	 *
	 * @param {object} opts
	 *    Options for the banner advertisement.
	 *    + adUnitId {string} -- The advertisement unit id from Goole Admob account.
     *    + onAdAvailable {function}    Handler when the advertisement is ready to show.
     *    + onAdNotAvailable {function} Handler when the advertisement could not be loaded.
     *    + onAdDismissed {function}    Handler when the advertisement is closed by user.
	 */
    this.loadInterstitial = function (opts) {
        NATIVE.plugins.sendEvent("AdmobPlugin", "loadInterstitial", JSON.stringify(opts));
        interstitialOpts = opts;
    }
    
    // Handle interstitial events    
    NATIVE.events.registerHandler("AdmobInterstitialAdDismissed", function () {
        logger.log("[admob][interstital] ad dismissed ");
        var cb = interstitialOpts ? interstitialOpts.onAdDismissed : null;
        if (cb && typeof cb === "function") {
            cb();
        }
    });

    NATIVE.events.registerHandler("AdmobInterstitialAdAvailable", function () {
        logger.log("[admob][interstital] ad available");
        var cb = interstitialOpts ? interstitialOpts.onAdAvailable : null;
        if (cb && typeof cb === "function") {
            cb("admob");
        }
    });

    NATIVE.events.registerHandler("AdmobInterstitialAdNotAvailable", function () {
        logger.log("[admob][interstital] ad not available");
        var cb = bannerOpts ? bannerOpts.onAdNotAvailable : null;
        if (cb && typeof cb === "function") {
            cb();
        }
    });
    
    // Handle banner events    
    NATIVE.events.registerHandler("AdmobBannerAdDismissed", function () {
        logger.log("[admob][interstital] ad dismissed ");
        var cb = bannerOpts ? bannerOpts.onAdDismissed : null;
        if (cb && typeof cb === "function") {
            cb();
        }
    });

    NATIVE.events.registerHandler("AdmobBannerAdAvailable", function () {
        logger.log("[admob][interstital] ad available");
        var cb = bannerOpts ? bannerOpts.onAdAvailable : null;
        if (cb && typeof cb === "function") {
            cb("admob");
        }
    });

    NATIVE.events.registerHandler("AdmobBannerAdNotAvailable", function () {
        logger.log("[admob][interstital] ad not available");
        var cb = bannerOpts ? bannerOpts.onAdNotAvailable : null;
        if (cb && typeof cb === "function") {
            cb();
        }
    });
});

exports = new admob();
