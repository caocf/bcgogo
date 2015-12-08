/**
 * Activity Checker
 * @required jquery-1.4.2.js+, base.js, application.js
 */
;
(function () {
    App.namespace("Module.ActivityChecker");

    var ActivityChecker = function () {
        this._param;
        this._lastTs = (new Date()).getTime();

        // "activity", "inactivity"
        this._status = {};
        this._checkerAsync = {};
        this._suffix;
        this._threadCount = 0;
    };

    /**
     * @param {
     *     interval:1000, // 非必须
     *     timeout:3500, // 必须
     *     onInactivity:function(){} // 非必须
     * }
     */
    ActivityChecker.method("init", function (param) {
        this._param = param;

        if(!param) {
            throw new Error("Please set parameters when you call init function!");
        }

        param.interval = param.interval || 1000;
        param.timeout = param.timeout || 3500;
        param.times = param.times || 0;
        param.onInactivity = param.onInactivity || function() {G.warning("User Is Inactivity!");};

        return this;
    });

    ActivityChecker.method("start", function () {
        var _ = this;

        if (_._status.length >= 1) {
            G.error("can't create more than" + _._status.length + "thread");
            return _;
        }
        _._suffix = (new Date()).getTime();

        var onUpdate = function(event) {
            _._lastTs = (new Date()).getTime();
        };
        $(document).bind("click mouseover", onUpdate);

        _._lastTs = (new Date()).getTime();
        var asyncFunction = eval(Wind.compile("async", function (inst, param) {
            var suffix = inst._suffix;

            inst._threadCount++;
            while (inst._status[suffix] === "activity") {
                $await(Wind.Async.sleep(param.interval));

                if (((new Date()).getTime() - inst._lastTs) > param.timeout) {
                    inst.stop();
                    $(document)
                        .unbind("click", onUpdate)
                        .unbind("mouseover", onUpdate);

                    inst._threadCount--;
                }
            }

            delete inst._status[suffix];
            delete inst._checkerAsync[suffix];
        }));
        _._status[_._suffix] = "activity";
        _._checkerAsync[_._suffix] = asyncFunction;
        asyncFunction(_, _._param).start();

        return _;
    });

    ActivityChecker.method("stop", function () {
        var _ = this;

        if (_._status[_._suffix] === "inactivity") {
            G.error("Checker inactivity!");
            return _;
        }
        _._status[_._suffix] = "inactivity";
    });

    ActivityChecker.method("status", function() {
        return this._status[this._suffix] || "inactivity";
    });

    App.Module.ActivityChecker = ActivityChecker;
}());