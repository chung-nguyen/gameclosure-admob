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
});

exports = new admob();
