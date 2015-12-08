/**
 * @description scanning's UI-panel
 * @required jquery 1.4.2+ , base.js, application.js
 */
;
(function () {
    if (window.top.APP_BCGOGO.Module.scanningPanel) {
        return;
    }

    APP_BCGOGO.namespace("Module.scanningPanel");

    var me,
        C = {
            scanning: "scanning_panel",
            checkbox: "checkbox_bg",
            checkbox_active: "checkbox_active",
            hide: "scanning_group_hide"
        },
        T = {
            content: "" +
                "<div class='" + C.scanning + "'>" +
                "    <ul>" +
                "        <li><a class='" + C.checkbox + "' customstate='barcode'>扫描枪</a></li>" +
                "        <li><a class='" + C.checkbox + "' customstate='card'>刷卡机</a></li>" +
                "    </ul>" +
                "</div>"
        },
        URL = {
            statusApi: "admin.do?method=getScanningGroupStatus"
        }/*,
        SESSION_STORAGE = {
            STATE_KEY: "scanning_statekey"
        }*/
        ;

    APP_BCGOGO.Module.scanningPanel = {
        scanningState: "keyboard",
        SCANNING_STATES: {
            KEYBOARD: "keyboard",
            CARD: "card",
            BARCODE: "barcode"
        },

        HOOK_INSTANCE: "#scanningGroup",

        _$: null,

        init: function () {
            // init Html structure
            me._$ = $(T.content);

            var _$ = me._$;

            _$
                .attr("title", "请选择您所使用的输入设备")
                .hide();
            $(me.HOOK_INSTANCE, window.top.document.body).append(me._$);

            var $itemList = _$.find("." + C.checkbox);

            $itemList.each(function (index, node) {
                $(node).bind("click", function (event) {
                    var state = $(this).attr("customstate");

                    me.setScanningState(state);
                });
            });

            me.updateStatus();
        },

        /**
         * @param state barcode|card
         */
        setScanningState: function (state) {
            var _$ = me._$,
                $liList = $("li", _$),
                stateSelector = "li a[customstate='" + state + "']",
                hasStateSelector = !!$liList.has(stateSelector)[0];

            if (hasStateSelector) {
                $liList.not(stateSelector).find("a").removeClass(C.checkbox_active);
                $liList.has(stateSelector).find("a").addClass(C.checkbox_active);
            } else {
                $liList.removeClass(C.checkbox_active);
            }
            me.scanningState = state;
            sStorage.setItem(storageKey.STATE_KEY, state);
        },

//        getSessionStorageItem: function () {
//            try {
//                return window.defaultStorage.getItem(SESSION_STORAGE.STATE_KEY) || null;
//            } catch (e) {
//                return null;
//            }
//        },

//        setSessionStorageItem: function (state) {
//            try {
//                window.defaultStorage.setItem(SESSION_STORAGE.STATE_KEY, state);
//            } catch (e) {
//                G.error("Your Browser not support HTML5 SessionStorage!")
//            }
//        },

        generateStateWithServerTagAndSessionStorage: function (param) {
            var param = param,
                storedState = sStorage.getItem(storageKey.STATE_KEY),
                newState;

            if(param.card && param.barcode) {
                if(!storedState || storedState === me.SCANNING_STATES.KEYBOARD) {
                    newState = me.SCANNING_STATES.CARD;
                } else {
                    newState = storedState;
                }
            } else if(param.card) {
                newState = me.SCANNING_STATES.CARD;
            } else if(param.barcode) {
                newState = me.SCANNING_STATES.BARCODE;
            } else {
                newState = me.SCANNING_STATES.KEYBOARD;
            }
            me.setScanningState(newState);

            return me.scanningState;
        },

        showOrHideWithServerTag: function (param) {
            if (param["barcode"] && param["card"]) {
                if (!me._$.is(":visible")) {
                    me._$.show();
                }
            } else {
                me._$.hide();
            }
        },

        updateStatus: function () {
            App.Net.syncGet({
                url: URL.statusApi,
                dataType: "json",
                success: function (json) {
                    var data = json,
                    // cover 3 cases:  undefined | "on" | "off"
                        scanning_card_allowed = json["scanning_card"] === "on" ? true : false,
                        scanning_barcode_allowrd = json["scanning_barcode"] === "on" ? true : false,
                        tagParam = {
                            "barcode": scanning_barcode_allowrd,
                            "card": scanning_card_allowed
                        };

                    var state = me.generateStateWithServerTagAndSessionStorage(tagParam);

                    me.setScanningState(state);
                    me.showOrHideWithServerTag(tagParam);
                },
                error: function () {
                    me.setScanningState(me.SCANNING_STATES.KEYBOARD);
                    me._$.hide();
                    G.error("failed to invoke request");
                }
            })
        },

        getScanningState: function () {
            return me.scanningState;
        }

    };
    me = APP_BCGOGO.Module.scanningPanel;
})();


/**
 * @description scanning, core
 * @required  jquery 1.4.2+ , base.js, application.js
 */
;
(function () {
    //Create namespace.
    APP_BCGOGO.namespace("Module.scanning");

    var listeningCharNum = 0,
        timerId = 0,
        source = "",
        nameStr = "",
        delayMax = 70,
        charSequentialMinLength = 5;

    //Get the key name according to the keycode.
    var keyCodeMap = {
        "gecko": {"173": "-", "61": "+"},
        "webkit": {"189": "-", "187": "+"},
        "msie": {"189": "-", "187": "+"}
    };
    var getKeyName = function (keycode) {
        return ($.browser.mozilla ? keyCodeMap["gecko"][keycode + ""] : keyCodeMap["webkit"][keycode + ""])
            || String.fromCharCode(G.betweenNum(keycode, 96, 105, "[]") ? keycode - 48 : keycode);
    };

    var calculator = {
        // num array, It used to accumulator.
        _numArr: [],

        // get average of increment
        getAvgInc: function (stringLen) {
            if (stringLen <= 1) {
                return 0;
            }

            var arr = this._numArr;
            // increment
            inc = arr[this._numArr.length - 1] - arr[0];
            return parseFloat(inc) / (stringLen - 1);
        },

        add: function (number) {
            this._numArr.push(number);
        },

        clear: function () {
            this._numArr = [];
        }
    };

    var self;
    APP_BCGOGO.Module.scanning = {
        _getDevNameFromInterval: function (interval, stringLen) {
            if (interval <= delayMax) {
                return window.top.App.Module.scanningPanel.getScanningState();
            } else {
                return "keyboard";
            }
        },

        sourceDetect: function (scanningCallback) {
            // start UI
            if (window.top.App.Module.scanningPanel
                && !window.top.App.Module.scanningPanel._$) {
                window.top.App.Module.scanningPanel.init();
            }

            var keyStr = "";
            $(document).bind("keydown", function (event) {
                listeningCharNum++;

                calculator.add((new Date()).getTime());

                //过滤掉keycode为16(shift)的键.
                if (event.which.toString() != '16') {
                    keyStr += event.which.toString() + ",";
                    nameStr += getKeyName(event.which);
                }

                clearTimeout(timerId);
                timerId = setTimeout(function () {
                    G.info("listeningCharNum : " + listeningCharNum);
                    G.info("charSequentialMinLength : " + charSequentialMinLength);
                    if (listeningCharNum >= charSequentialMinLength) {
                        //Create repeat object.
                        var repeatObj = {},
                            strPosition = keyStr.indexOf(",") + 1;
                        var strSubstring = keyStr.substring(0, strPosition);
                        var strReplaced = keyStr.replace(new RegExp(strSubstring, 'gi'), '');
                        repeatObj[strSubstring] = (keyStr.length - strReplaced.length) / strPosition;

                        //If has the same keycode.
                        if (keyStr.length / strPosition == repeatObj[strSubstring]) {
                            source = "keyboard";
                            G.debug("From keyboard.");
                        } else {
                            source = self._getDevNameFromInterval(calculator.getAvgInc(nameStr.length), listeningCharNum);
                            G.debug("From scanner.\tkeycode:  " + keyStr + "\tkeyname:  " + nameStr);
                        }
                    } else {
                        source = "keyboard";
                        G.debug("From keyboard.");
                    }
                    //If from the text box id "input_search_Name".
                    var eventTarget = event.target;
                    if (eventTarget.tagName != "INPUT" && eventTarget.tagName != "TEXTAREA") {
                        if (eventTarget.id != 'input_search_Name'
                            && eventTarget.id != 'memberNo'
                            && nameStr.search(/[^a-zA-Z\d\n\r-]+/g, "") === -1) {
                            //Callback function.
                            scanningCallback(source, nameStr, calculator.getAvgInc(nameStr.length));
                        } else {
                            G.warning("Source :" + source + "    have illegal characters");
                        }
                    }

                    //Clear the data.
                    listeningCharNum = 0;
                    keyStr = "";
                    nameStr = "";

                    // clear date tag
                    calculator.clear();
                }, delayMax);

                //Output the keycode and keyname.
                var conoleMessage;
                conoleMessage = "keycode: " + event.which.toString() + "\t";
                if (G.Number.between(event.which, 48, 105, "[]")) {
                    conoleMessage += "keyname: " + getKeyName(event.which);
                }
                G.debug(conoleMessage);
            });
        }
    };
    self = APP_BCGOGO.Module.scanning;

})();