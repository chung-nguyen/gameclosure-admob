var admob = Class(function () {

    this.testNative = function() {
        logger.log("{admob} Testing");
        NATIVE.plugins.sendEvent("AdmobPlugin", "testNative", JSON.stringify({}));
    }

});

exports = new admob();
