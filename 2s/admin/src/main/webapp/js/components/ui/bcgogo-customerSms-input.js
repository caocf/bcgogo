/**
 * @description 联系人， 手机号；  多选输入组件
 * @author 潘震
 * @date 2012-09-14
 * @License ?
 * @Copyright 版权所有(c) 2012 -苏州威尼尤至软件科技有限公司，保留所有权利。
 */
APP_BCGOGO.namespace("Module.customerSmsInput");

;
(function() {
// Component "defined"
    var customerSmsInput = {
        _target:null,
        _data:[],
        _noticeDialog:null,
        STATE:{
            "CREATE":"create",
            "CHANGE":"change"
        },
        _state:"create",

        _getSaveButtonLabelFromState:function(state) {
            switch (state) {
                case "change":
                    return "修改计划";
                    break;

                case "create":
                    return "新增计划";
                default:
                    break;
            }
        },

        init:function(p) {
            // init params
            this._target = p.selector;
            this.onSelectPerson = p.onSelectPerson || this.onSelectPerson;
            this.onSave = p.onSave || this.onSave;
            this.onClear = p.onClear || this.onClear;

            var s = ""
                + "<div class='bcgogo-customerSmsInput'>"
                + "    <div class='bcgogo-customerSmsInput-inputtingContainer'>"
                + "        <div class='bcgogo-customerSmsInput-button-selectPerson'></div>"
                + "        <input class='bcgogo-customerSmsInput-name' type='text' style='margin:auto 5px auto 5px;width:100px;'/>"
                + "        <input class='bcgogo-customerSmsInput-mobile' type='text' style='margin:auto 5px auto 5px;width:100px;'/>"
                + "        <div class='bcgogo-customerSmsInput-button-add'></div>"
                + "        <p>（请输入：联系人&nbsp;&nbsp;&nbsp;手机号）</p>"
                + "    </div>"
                + "    <ul class='bcgogo-customerSmsInput-optionContainer'></ul>"
                + "    <div class='bcgogo-customerSmsInput-saveContainer'>"
                + "        <span>马上发送短信</span>"
                + "        <input type='checkbox'/>"
                + "        <div class='bcgogo-customerSmsInput-button-save'></div>"
                + "        <div class='bcgogo-customerSmsInput-button-clear'></div>"
                + "    </div>"
                + "    <div class='bcgogo-customerSmsInput-noticeDialog' title='提示'><p class='noticeContent'></p></div>"
                + "</div>"
            $(this._target).html(s);
            $(".bcgogo-customerSmsInput", this._target).css("width", (p.width || 800) + "px");

            $(".bcgogo-customerSmsInput-button-selectPerson", this._target)
                .button({label:"选择人员"})
                .bind("click", this.onSelectPerson);

            $(".bcgogo-customerSmsInput-button-add", this._target)
                .button({label:"添加"})
                .bind("click", this._onAdd);

            $(".bcgogo-customerSmsInput-button-save", this._target)
                .button({label:this._getSaveButtonLabelFromState(this._state)})
                .bind("click", this._onSaveBefore);

            $(".bcgogo-customerSmsInput-button-clear", this._target)
                .button({label:"清空"})
                .bind("click", this.onClear);

            var $name = $(".bcgogo-customerSmsInput-name", this._target),
                $mobile = $(".bcgogo-customerSmsInput-mobile", this._target),
                foo = APP_BCGOGO.Module.customerSmsInput;

            $name
                .bind("focus", function(event) {
                    $(this).css("border-color", "#4297d7");
                })
                .bind("blur", function(event) {
                    $(this).css("border-color", "#ADADAD");
                })
                .bind("keyup", function(event) {
                    if (GLOBAL.Interactive.keyNameFromEvent(event) === "enter") {
                        $name.blur();
                        $mobile.focus().select();
                    } else {
                        var s = foo._getNormalizeString($(this).val());
                        s = s.replace(/[^\u4e00-\u9fa5a-zA-Z\d-_]/g, "");
                        s = APP_BCGOGO.StringFilter.stringLengthFilter(s, 30);
                        $(this).val(s);
                    }
                });

            $mobile
                .bind("focus", function(event) {
                    $(this).css("border-color", "#4297d7");
                })
                .bind("blur", function(event) {
                    $(this).css("border-color", "#ADADAD");
                })
                .bind("keyup", function(event) {
                    if (GLOBAL.Interactive.keyNameFromEvent(event) === "enter") {
                        foo._onAdd(event);
                    } else {
                        var s = foo._getNormalizeString($(this).val());
                        s = APP_BCGOGO.StringFilter.stringLengthFilter(s, 11);
                        s = APP_BCGOGO.StringFilter.inputtingIntFilter(s);
                        $(this).val(s);
                    }
                });

            $(".bcgogo-customerSmsInput-noticeDialog .noticeContent", foo._target).text("手机号格式不对!");

            foo._noticeDialog = $(".bcgogo-customerSmsInput-noticeDialog", foo._target);
            foo._noticeDialog
                .css("font-size", "14px")
                .dialog({
                    "buttons":[
                        {
                            "text":"确认",
                            "click":function() {
                                $(this).dialog("close");
                                setTimeout(function(){
                                    $mobile.focus().select();
                                },200);
                            }
                        }
                    ],
                    "modal":true,
                    "autoOpen":false,
                    "draggable":false,
                    "resizable":false
                });

        },

        onSelectPerson:function(event) {
            GLOBAL.debug("clicked 选择人员 button!");
        },

        _onAdd:function(event) {
            var $name = $(".bcgogo-customerSmsInput-name", this._target),
                $mobile = $(".bcgogo-customerSmsInput-mobile", this._target),
                foo = APP_BCGOGO.Module.customerSmsInput;

            if (foo._getNormalizeString($name.val()) === ""
                && foo._getNormalizeString($mobile.val()) === "") {
                return;
            } else {
                if (APP_BCGOGO.Validator.stringIsMobilePhoneNumber($mobile.val()) || $mobile.val() === "") {
                    foo.addData([
                        {
                            "name": foo._getNormalizeString($name.val()),
                            "mobile": foo._getNormalizeString($mobile.val()),
                            "userId": GLOBAL.Util.generateUUID()
                        }
                    ]);
                    $mobile.blur();
                    $name.focus().select();
                } else {
                    $mobile.blur();
                    foo._noticeDialog.dialog("open");
                }
            }
        },

        _onSaveBefore:function(event) {
            var foo = APP_BCGOGO.Module.customerSmsInput;
            foo.onSave(event, $(".bcgogo-customerSmsInput-saveContainer input[type='checkbox']").attr("checked"));
        },

        onSave:function(event, isSendImmediately) {
            GLOBAL.debug("clicked 保存 button! \n\t isSendImmediately:" + isSendImmediately);
        },

        onClear:function(event) {
            GLOBAL.debug("clicked 清空 button!");
        },

        addData:function(data) {
            if (this._isNullOrNullArray(data)) {
                return;
            }
            var uniqueData = this._getUniqueData(data, this._data);
            this._data = this._data.concat(uniqueData);
            this.render(uniqueData);
        },

        _getUniqueData:function(inData, baseData) {
            var uniqueData = [];
            $.each(inData, function(index, value) {
                var uniqued = true;
                for (var i = 0,len = baseData.length; i < len; i++) {
                    if (value["userId"] === baseData[i]["userId"]) {
                        uniqued = false;
                        break;
                    }
                }
                if (uniqued)
                    uniqueData.push(value);
            });
            return uniqueData;
        },

        _isNullOrNullArray:function(o) {
            var gl = GLOBAL.Lang;
            return gl.isNull(o) || gl.isUndefined(o) || (gl.isArray(o) && o.length === 0);
        },

        _getNormalizeString:function(s) {
            var gl = GLOBAL.Lang;
            return gl.isNull(s) || gl.isUndefined(s) || s === "null" || s === "undefined" ? "" : s;
        },

        _clearUIOptions:function() {
            $(".bcgogo-customerSmsInput-name", this._target).val("");
            $(".bcgogo-customerSmsInput-mobile", this._target).val("");
			$(".bcgogo-customerSmsInput-saveContainer input[type='checkbox']", this._target).attr("checked", false);
            $(".bcgogo-customerSmsInput-optionContainer", this._target).html("");
        },

        //"<li class='bcgogo-multi-input-option'><p>"+p["label"]+"</p><span>×</span></li>"
        render:function(data) {
            var s = "", label = "", foo = APP_BCGOGO.Module.customerSmsInput;
            for (var i = 0,len = data.length; i < len; i++) {
                label = (this._getNormalizeString(data[i]["name"]) + " : " + this._getNormalizeString(data[i]["mobile"])).replace(/^ +:|: +$/g, "");
                s += "<li class='bcgogo-customerSmsInput-option' userid='" + data[i]["userId"] + "'><p>" + label + "</p><span>×</span></li>"
            }
            $(s)
                .appendTo($(".bcgogo-customerSmsInput-optionContainer", this._target))
                .bind("click", function(event) {
                    if ($(event.target).is("li span")) {
                        foo.delData($(event.target).closest("li").attr("userid"));
                    }
                });
        },

        delData:function(userId) {
            var gl = GLOBAL.Lang, foo = APP_BCGOGO.Module.customerSmsInput;
            if (gl.isNull(userId) || gl.isUndefined(userId) || gl.isEmpty(userId) || foo._data.length === 0) {
                return;
            }

            // del item from array-hash
            $.each(foo._data, function(index, value) {
                if (value.userId === userId) {
                    foo._data.splice(index, 1);
                    return false;
                }
            });

            // del item from ui
            $(".bcgogo-customerSmsInput-optionContainer li", this._target).each(function() {
                if ($(this).attr("userid") === userId) {
                    $(this).remove();
                    return false;
                }
            });
        },

        setState:function(state) {
            this._state = state;
            $(".bcgogo-customerSmsInput-button-save", this._target)
                .button("option", "label", this._getSaveButtonLabelFromState(this._state));
        },

        getState:function() {
            return this._state;
        },

        clearData:function() {
            this._data = [];
            this._clearUIOptions();
        },

        getData:function() {
            return this._data;
        },

        getValuesByKey:function(key) {
            if (GLOBAL.Lang.isString(key) === false && key.search(/name|mobile|userId/g) === -1) {
                return;
            }
            var arr = [];
            $.each(this._data, function(index, value) {
				if( $.trim(value[key]) === "" ) {
					return;
				}
                arr.push(value[key]);
            });
            return arr.join(",");
        }
    };
    APP_BCGOGO.Module.customerSmsInput = customerSmsInput;
})();