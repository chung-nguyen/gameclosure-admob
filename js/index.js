var admob = Class(function () {

    this.showAdView = function (opts) {
        NATIVE.plugins.sendEvent("AdmobPlugin", "showAdView", JSON.stringify(opts));
    }

    this.hideAdView = function () {
        NATIVE.plugins.sendEvent("AdmobPlugin", "hideAdView", JSON.stringify({}));
    }

    this.loadAdView = function () {
        NATIVE.plugins.sendEvent("AdmobPlugin", "loadAdView", JSON.stringify({}));
    }
    
    this.showInterstitial = function() {
        NATIVE.plugins.sendEvent("AdmobPlugin", "showInterstitial", JSON.stringify({}));
    }
    
    this.loadInterstitial = function(opts) {
        NATIVE.plugins.sendEvent("AdmobPlugin", "loadInterstitial", JSON.stringify(opts));
    }
});

exports = new admob();
