/************************************
 *  该文件依赖于 bcgogo-droplist.js
 *  (仅满足于简单应用场景)
 ************************************/

// *  二次简易封装通用droplist.
/* * options.event                  *事件对象
 options.elementId              *元素的Id
 options.hiddenId               *保存id串的hidden的id
 options.id                     *原始json中id字段
 options.name                   *原始json中name字段
 options.data                   *ajax请求的url或者JSON对象(type:string/array[JSON/object])
 options.keyword                *用于查找的url参数
 options.isEditable             *是否可编辑
 options.isDeletable            *是否可删除
 options.autoSet                *是否自动给文本框设置值
 options.loadSuccess            *加载成功的回调函数
 * */

/* * options.onsave                 *保存时的参数
 options.onsave.id               请求时的id
 options.onsave.name             请求时的name
 options.onsave.url              ajax请求的url
 options.onsave.errMsg           保存不成功时的提示信息
 * */

/* * options.ondelete               *删除时的参数
 options.ondelete.id             请求时的id
 options.ondelete.url            ajax请求的url
 options.ondelete.errMsg         删除不成功时的提示信息
 * */

/* * options.beforeSelected         *选择前的回调函数* */

/* * options.afterSelected          *鼠标选择后的回调函数* */

/* * options.afterKeySelected       *键盘选择后的回调函数* */
/************************ 公共droplistLite ************************/
var droplistLite = {
    //Show drop list.
    show: function(options) {

        var droplist = App.Module.droplist,
            event = options.event,
            _self = droplistLite,
            _hiddenId, _hiddenValue;

        options.obj = event.target;
        options.elementId = options.elementId || '_';
        options.hiddenId = options.hiddenId || '_';
        options.autoSet = options.autoSet || true;
        clearTimeout(droplist.delayTimerId || 1);

        droplist.delayTimerId = setTimeout(function() {
            var _uuid = G.Util.generateUUID(),
                _name = $(options.obj).val();

            //Replace the special char.
            var _replaceSpecialChar = function(str) {
                    str = str.replace(/(:|\.|\#|\@|\$|\%|\^|\&|\*|\!)/g, '\\$1');
                    return str;
                };

            //Set the Hidden's value.
            var _setHiddenValue = function(_obj, _idValue) {
                    var _hiddenGroup = _hiddenId.split(',');
                    for(var i = 0; i < _hiddenGroup.length; i++) {
                        if($('#' + _hiddenGroup[i]).length > 0) {
                            $("#" + _hiddenGroup[i]).val(_idValue ? _idValue : "");
                        } else {
                            $("#" + _obj.id.split(".")[0] + "\\." + _hiddenGroup[i]).val(_idValue ? _idValue : "");
                        }
                    }
                };

            //Drop down list handler.
            var _droplistHandler = function(__importData__) {

                    //转成固定JSON格式
                    var _json = _self.convertToDroplistJSON(__importData__, options.id, options.name);

                    if(options.beforeSelected) {
                        _json = options.beforeSelected(_json);
                    }

                    var result = {
                        uuid: _uuid,
                        data: _json
                    };

                    droplist.show({
                        "selector": $(options.event.currentTarget),
                        "originalValue": {
                            label: _name,
                            idStr: _hiddenValue
                        },
                        "autoSet": options.autoSet == "true",
                        "isEditable": options.isEditable == "true",
                        "isDeletable": options.isDeletable == "true",
                        "data": result,
                        "saveWarning": "保存操作会影响全局数据",
                        "deleteWarning": "删除操作会影响全局数据",

                        //mouse select handler.
                        "onSelect": function(event, index, data, hook) {
                            //if exists the custom handler.
                            if(options.afterSelected) {
                                options.afterSelected(event, index, data, hook);
                            } else {
                                var _id = data.id;
                                var _name = data.label;

                                $(hook).val(_name);
                                $(hook).attr("hiddenValue", _name);

                                _setHiddenValue(hook, _id);
                            }

                            //hide the droplist.
                            droplist.hide();
                        },

                        //edit and save handler.
                        "onSave": function(event, index, data, hook) {

                            //if edit callback exists.
                            if(typeof options.onsave.callback === "function") {
                                options.onsave.callback(event, index, data, hook);
                            } else {

                                //if "options.ondelete.id" exists.
                                if(options.onsave.id) {

                                    var _id = data.id;
                                    var _name = $.trim(data.label);

                                    //create JSON form string.
                                    var saveData = '{';
                                    saveData += '"' + options.onsave.id + '"' + ':' + '"' + _id + '"';
                                    saveData += ',';
                                    saveData += '"' + options.onsave.name + '"' + ':' + '"' + _name + '"';
                                    saveData += ',';
                                    saveData += '"now":' + '"' + new Date() + '"';
                                    saveData += '}';

                                    //request save result & handler.
                                    App.Net.syncPost({
                                        url: options.onsave.url,
                                        data: $.parseJSON(saveData),
                                        dataType: "json",
                                        success: function(_result) {

                                            //success.
                                            if(_result.resu == "success") {
                                                data.label = _name;
                                                data.categoryName = _name;
                                                if("it is self" != _result.msg) {

                                                    //get the event
                                                    var $obj;
                                                    if($('#' + options.elementId).length > 0) {
                                                        $obj = $('#' + options.elementId);
                                                    } else {
                                                        $obj = $("input[id$='." + options.elementId + "']");
                                                    }

                                                    //遍历item 把此id的name 和hiddenvalue都变为最新的name
                                                    $obj.each(function() {
                                                        var categoryId = _hiddenValue;

                                                        if(categoryId == _id) {
                                                            $(this).val(_name);
                                                            $(this).attr("hiddenValue", _name);
                                                        }
                                                    });
                                                }

                                                //pop message & reload the page.
                                                nsDialog.jAlert('修改成功!', null, function() {
                                                    droplist.hide();
                                                    $('.tipsy').hide();
                                                });
                                            }

                                            //fail.
                                            else if(!_result || _result.resu == "error" || !_result.flag) {
                                                nsDialog.jAlert(options.onsave.errMsg, null, function() {
                                                    data.label = data.categoryName;
                                                });
                                            }

                                            //exception.
                                            else {
                                                nsDialog.jAlert("数据异常！", null, function() {
                                                    droplist.hide();
                                                    $('.tipsy').hide();
                                                });
                                            }
                                        },
                                        //request error.
                                        error: function() {
                                            nsDialog.jAlert("保存失败！", null, function() {
                                                droplist.hide();
                                                $('.tipsy').hide();
                                            });
                                        }
                                    });
                                }
                            }
                        },

                        //delete handler.
                        "onDelete": function(event, index, data, hook) {

                            //if delete callback exists.
                            if(typeof options.ondelete.callback === "function") {
                                options.ondelete.callback(event, index, data, hook);
                            } else {
                                //if "options.ondelete.id" exists.
                                if(options.ondelete.id) {

                                    //create a JSON form string.
                                    var _saveData = '{';
                                    _saveData += '"' + options.ondelete.id + '"' + ':' + '"' + data.id + '"';
                                    _saveData += ',';
                                    _saveData += '"now":' + '"' + new Date() + '"';
                                    _saveData += '}';

                                    //get the request result.
                                    var _result = App.Net.syncGet({
                                        url: options.ondelete.url,
                                        data: $.parseJSON(_saveData),
                                        dataType: "json"
                                    });

                                    //when failed and successed.
                                    if(_result.resu == "success") {
                                        nsDialog.jAlert("删除成功！", null, function() {
                                            droplist.hide();
                                            $('.tipsy').hide();
                                        });
                                    } else if(!_result || _result.resu == "error" || !_result.flag) {
                                        nsDialog.jAlert("删除失败！", null, function() {
                                            droplist.hide();
                                            $('.tipsy').hide();
                                        });
                                    } else {
                                        nsDialog.jAlert("数据异常！", null, function() {
                                            droplist.hide();
                                            $('.tipsy').hide();
                                        });
                                    }
                                }
                            }
                        },

                        //keybord select handler.
                        "onKeyboardSelect": function(event, index, data, hook) {

                            //if there is the custom keybord select handler.
                            if(options.afterKeySelected) {
                                options.afterKeySelected(event, index, data, hook);
                            } else {
                                var _id = data.id,
                                    _name = data.label;

                                //if this Hidden element exists.
                                _setHiddenValue(hook, _id);

                                //if ID exists.
                                if(_id) {
                                    $(hook).attr("hiddenValue", _name);
                                } else {
                                    $(hook).removeAttr("hiddenValue")
                                }

                                //assign value to event.target.
                                $(hook).val(_name);
                            }
                        }
                    });
                if(_json &&_json[0]){
                    var title;
                    if(!G.isEmpty(_json[0].member_no)){
                        title=_json[0].member_no;
                    }else{
                        title=G.isEmpty(_json[0].name)?_json[0].label:_json[0].name;
                    }
                    G.completer({
                            'domObject':options.event.currentTarget,
                            'keycode':event.keyCode,
                            'title':title
                        }
                    );
                }
            };

            //Main function.
            var _main = function() {

                    droplist.setUUID(_uuid);

                    //get the hidden element's id.
                    if(/\-[\s\S]+\-/.test(options.hiddenId)) {
                        _hiddenId = options.hiddenId.substr(1, options.hiddenId.length - 2);
                    } else {
                        _hiddenId = options.hiddenId;
                    }

                    //transfer the special chars.
                    _hiddenId = _replaceSpecialChar(_hiddenId);

                    //get the hidden element's value.
                    if($('#' + _hiddenId).length > 0) {
                        _hiddenValue = $("#" + _hiddenId).val();
                    } else {
                        _hiddenValue = $("#" + options.obj.id.split(".")[0] + "\\." + _hiddenId).val();
                    }

                    //if "options.data" is a url.
                    if(typeof options.data === 'string') {

                        //set the default keyword.
                        if(typeof options.name != "function") {
                            options.keyword = options.keyword || options.name;
                        }

                        if(typeof options.searchValue == "string"){
                            _name = options.searchValue;
                        }

                        //create a JSON object.
                        var _data = new Object();
//                        _data["uuid"] = _uuid;
                        _data[options.keyword] = _name;
                        _data["now"] = new Date();

                        //get the json data according to "options.data"(<string> url).
                        App.Net.asyncGet({
                            url: options.data,
                            data: _data,
                            dataType: "json",
                            success: function(_result) {
                                if(options.loadSuccess) {
                                    options.loadSuccess(_result);
                                }
                                _droplistHandler(_result);
                            },
                            error:function(data){
                               console.debug(data);
                            }
                        });

                    }
                    //if "options.data" is JSON.
                    else {
                        _droplistHandler(options.data);
                    }
                };

            //init the main function.
            _main();

        }, 200);
    },
    //Hide drop list.
    hide: function() {
        App.Module.droplist.hide();
    },
    //Transfer the original JSON to adapt to the droplist component.
    convertToDroplistJSON: function(_json, _id, _name) {

        //Standard JSON & Array format.
        var _jsonAdapter = function(_json_, _name_) {

                for(var i = 0; i < _json_.length; i++) {

                    var _label = _json_[i][_name_];
                    var _idStr = _json_[i][_id];

                    delete _json_[i][_name_];
                    delete _json_[i][_id];

                    _json_[i]["label"] = _label;
                    _json_[i]["id"] = _idStr;

                }

                return _json_;
            };

        //Another kind of string format.
        var _dataAdapter = function(_data_) {
                var _json_;

                if(_data_[0].suggestionEntry) {

                    _json_ = new Array(_data_.length);
                    for(var i = 0; i < _data_.length; i++) {

                        _json_[i] = {};
                        for(var j = 0, len = _data_[i].suggestionEntry.length; j < len; j++) {
                            var _key = _data_[i].suggestionEntry[j][0] || '',
                                _value = _data_[i].suggestionEntry[j][1] || '';

                            _json_[i][_key.toString()] = _value.toString();
                        }
                    }
                }
                return _json_;
            };

        //Get the custom item format.
        var _gatherNameList = function(_json_, _name_) {

                for(var i = 0; i < _json_.length; i++) {

                    var _nameTemplate = _name_(_json_[i]);
                    var _nameExpList = _nameTemplate.match(/\{\w+\}/g);
                    for(var j = 0; j < _nameExpList.length; j++) {

                        var _nameExp = _nameExpList[j].substring(1, _nameExpList[j].length - 1);
                        var _regexp = new RegExp('\\{' + _nameExp + '\\}', 'g');
                        _nameTemplate = _nameTemplate.replace(_regexp, G.Lang.normalize(_json_[i][_nameExp]));

                    }
                    _json_[i]["label"] = $.trim(_nameTemplate);
                }

                return _json_;
            };

        //Init function.
        var _init = function() {
            if(!_json){
                return;
            }
                //Exists the attribute "data".
                if(_json.data) {

                    _json = _jsonAdapter(_json.data, _name);

                }
                //Not empty and exists the attribute "suggestionEntry".
                else if(_json.length > 0 && _json[0].suggestionEntry) {

                    var _stringData;

                    //When "_name" is a function which is used to format item.
                    if(typeof _name === "function") {

                        _stringData = _dataAdapter(_json);
                        var _jsonData = _jsonAdapter(_stringData, _name);
                        _json = _gatherNameList(_jsonData, _name);

                    } else {

                        _stringData = _dataAdapter(_json);
                        _json = _jsonAdapter(_stringData, _name);
                    }

                }
                //Not empty.
                else if(_json.length > 0) {

                    _json = _jsonAdapter(_json, _name);

                }
                //Empty JSON.
                else {
                    _json = {};
                }

                return _json;
            };

        return _init();
    }
};



/************************ 调用droplistLite ************************/
;(function($) {

    var commaNormalize = function( inStr ) {
        var retStr = inStr.replace(/\，+/g, ",")
            .replace(/\,+/g, ",");

        return retStr;
    };

    var _droplist = {
        _businessCategary: function() {
            $(this).live("click focus keyup", function(event) {

                var keyCode = event.keyCode || event.which;

                if(!keyCode) {
                    return;
                }

                var keyName = G.keyNameFromKeyCode(keyCode);
                if (G.contains(keyName, ["up", "down", "left", "right"])) {
                    return;
                }

                if($(this).val() != $(this).attr("hiddenValue")) {
                    $("#" + this.id.split(".")[0] + "\\.businessCategoryId").val("");
                    $(this).removeAttr("hiddenValue");
                }

                var obj = this;

                droplistLite.show({
                    event: event,
                    isEditable: "true",
                    isDeletable: "true",
                    elementId: "businessCategoryName",
                    hiddenId: "businessCategoryId",
                    id: "idStr",
                    name: "label",
                    keyword: "keyWord",
                    data: "category.do?method=getCategory",
                    loadSuccess: function(result) {
                        var disabledArr = ["洗车", "美容", "精品", "机修", "装潢", "音响", "油漆", "精洗", "膜", "轮胎"];
                        if(result.data) {
                            for(var i = 0; i < result.data.length; i++) {
                                for(var j = 0; j < disabledArr.length; j++) {
                                    if(result.data[i].label == disabledArr[j]) {
                                        result.data[i].isEditable = false;
                                        result.data[i].isDeletable = false;
                                        break;
                                    }
                                }
                            }
                        }
                    },
                    onsave: {
                        id: "categoryId",
                        name: "categoryName",
                        url: "category.do?method=updateCategoryName",
                        errMsg: "营业分类中已经有此分类！"
                    },
                    ondelete: {
                        id: "categoryId",
                        url: "category.do?method=deleteCategory"
                    }
                });
            });
        },
        _worker: function() {

            var _selectData, _currTextKey, _currTextVal, _currStoreKey = '';
            //remove the selected man from JSON according to the NAME in the text box.
            var removeSelectedMan = function(_json, _textVal) {
                if(!_json){
                    return;
                }
                _textVal = commaNormalize(_textVal);
                if(_textVal.search(/,/) != -1){
                    _textVal = _textVal.substr(0, _textVal.lastIndexOf(","));
                }else{
                    _textVal = "";
                }
                var nameList = _textVal.split(',');

                for(var j = 0; j < nameList.length; j++) {
                    for(var i = 0, len = _json.length; i < len; i++) {
                        if(_json[i].label == nameList[j]) {
                            _json.splice(i, 1);
                            i = 0;
                            len = _json.length;
                            break;
                        }
                    }
                }

                return _json;
            };

            var _limitToSix = function(str){
                var valArray = str.split(",");
                var result = "";
                for(var i = 0; i<valArray.length; i++){
                    if(i>5){
                        continue;
                    }
                    result += valArray[i]+",";
                }
                return result;
            };

            //when select items.
            var _selectHandler = function(event, index, data, hook, eventType, obj) {

                //clear the repeat and last commas.
                _currTextKey = commaNormalize(_currTextKey);

                //Get the hidden input.
                var _hiddenInput = $(obj).parent().find('input[type="hidden"]');
                if (_hiddenInput.filter('input[id$="workerIds"]').length > 0) {
                    _hiddenInput = _hiddenInput.filter('input[id$="workerIds"]');
                }

                //mouse click select.
                if (eventType == "onSelect") {
                    _multiSelectHandler(data, "mouse", obj);
                }
                //keyboard select.
                else if (eventType == "onKeyboardSelect") {

                    if (_currTextKey.length <= 0) {
                        $(obj).val(data.label);
                        _hiddenInput.val(data.id);
                        _currStoreKey = '';
                    } else {
                        var _itemAmount = _currTextKey.split(',').length;
                        if (_itemAmount < 6) {
                            //set the text into the text box.
                            _currStoreKey = commaNormalize(_currStoreKey);
                            if (_currStoreKey != data.label) {
                                $(obj).val(_currStoreKey + ',' + data.label);
                                _hiddenInput.val(_currTextVal + ',' + data.id);
                            } else {
                                $(obj).val(_currStoreKey);
                                _hiddenInput.val(_currTextVal);
                            }

                            if (_currStoreKey.length <= 0 || _currTextKey.length <= 0) {
                                _hiddenInput.val('');
                            }
                        }
                    }

                    _selectData = data;
                }

                $(obj).val(commaNormalize($(obj).val()+","));
                $(obj).focus();
            };

            var _multiSelectHandler = function(_data, _selectType, obj) {

                var _currentValue;

                //Get the hidden input.
                var _hiddenInput = $(obj).parent().find('input[type="hidden"]');
                if(_hiddenInput.filter('input[id$="workerIds"]').length > 0) {
                    _hiddenInput = _hiddenInput.filter('input[id$="workerIds"]');
                }

                //get current value according to different select type.
                switch(_selectType) {
                case "mouse":
                    _currentValue = _currTextKey;
                    break;
                case "keybord":
                    _currentValue = _currStoreKey;
                    break;
                default:
                    _currentValue = "";
                    break;
                }


                if(_currentValue.length <= 0) {
                    $(obj).val(_data.label);
                    _hiddenInput.val(_data.id);

                    _currStoreKey = _data.label;
                } else {

                    var itemAmount = _currentValue.split(',').length;
                    //the max of selected items is 6.
//                    if(itemAmount >= 6) {
//                        nsDialog.jAlert('最多选择6人!');
//                    } else {
                        if(_currTextKey.search(/,/) != -1){
                            _currTextKey = _currTextKey.substr(0, _currTextKey.lastIndexOf(","));
                        }else{
                            _currTextKey = "";
                        }
                        if(G.isEmpty(_currTextKey)){
                            _currTextKey = _data.label;
                            _currTextVal = _data.id;
                            _currStoreKey = _data.label;
                        }else{
                            _currTextKey += "," + _data.label;
                            _currTextVal += "," + _data.id;
                            _currStoreKey += "," + _data.label;
                        }

                        _currTextKey = _limitToSix(commaNormalize(_currTextKey));
                        _currTextVal = _limitToSix(commaNormalize(_currTextVal));

                        switch(_selectType) {
                        case "mouse":
                            $(obj).val(commaNormalize(_currTextKey));
                            _hiddenInput.val(_currTextVal);
                            break;
                        case "keybord":
                            $(obj).val(commaNormalize(_currStoreKey));
                            _hiddenInput.val(_currTextVal);
                            break;
                        default:
                            $(obj).val(commaNormalize(_currTextKey));
                            _hiddenInput.val(_currTextVal);
                            break;
                        }
//                    }
                }
            };


            $(this).live("click focus keyup", function(event) {
                var keyCode = event.keyCode || event.which;

                if(!keyCode) {
                    return;
                }

                var keyName = G.keyNameFromKeyCode(keyCode),
                    obj = event.target;

                if (G.contains(keyName, ["up", "down", "left", "right", "enter"])) {
                    return;
                }
                _currTextKey = $(obj).val();

                //Get the hidden input value.
                _hiddenInput = $(obj).parent().find('input[type="hidden"]');
                if(_hiddenInput.filter('input[id$="workerIds"]').length > 0) {
                    _hiddenInput = _hiddenInput.filter('input[id$="workerIds"]');
                }

                _currTextVal = _hiddenInput.val();

                if(keyName === "ctrl" && _selectData && $('.ui-bcgogo-droplist-option').hasClass('ui-bcgogo-droplist-highlighted')) {
                    _multiSelectHandler(_selectData, "keybord", obj);
                }

                if (G.contains(keyName, ["backspace", "delete"])) {
                    _currStoreKey = _currTextKey;
                }

                var searchValue;
                if($(obj).val().search(/[,，、]/)!=-1){
                    var allTxt = $(obj).val().replace(/[,，、]/g, ",");
                    var txtAry = allTxt.split(",");
                    searchValue = txtAry[txtAry.length-1];
                }else{
                    searchValue = $(obj).val();
                }

                droplistLite.show({
                    event: event,
                    //isEditable: "true",
                    hiddenId: "salesManIds",
                    id: "idStr",
                    name: "name",
                    keyword: "keyWord",
                    searchValue: searchValue,
                    data: "txn.do?method=searchWorks",
                    autoSet: "false",
                    // onsave: {
                    //     id: "workerId",
                    //     name: "workerName",
                    //     url: "member.do?method=updateWorkerName",
                    //     errMsg: "销售人名字已存在"
                    // },
                    // ondelete: {
                    //     id: "workerId",
                    //     url: "member.do?method=deleteWorker"
                    // },
                    beforeSelected: function(data) {
                        return removeSelectedMan(data, $(obj).val());
                    },
                    afterSelected: function(event, index, data, hook) {
                        _selectHandler(event, index, data, hook, "onSelect", obj);
                    },
                    afterKeySelected: function(event, index, data, hook) {
                        _selectHandler(event, index, data, hook, "onKeyboardSelect", obj);
                    }
                });
            });

            $(this).live('blur', function(event) {

                var _boxValue = $(event.target).val();
                $(event.target).val(commaNormalize(_boxValue));

                _boxValue = $(event.target).val();
                _currStoreKey = $.trim(_boxValue);

                //分别重新计算中英文字符串长度(全角中文等为2个字符,英文符号等为1个字符)
                var _getStringLength = function(_str) {
                    var cArr = _str.match(/[^\x00-\xff]/ig);
                    return _str.length + (cArr == null ? 0 : cArr.length);
                };

                _boxValueArray = _boxValue.split(',');
                var _itemAmount = _boxValueArray.length;
                //验证人数
//                if(_itemAmount > 6) {
//                    nsDialog.jAlert('请最多选择6名员工!', null, function() {
//                        $(event.target).focus().select();
//                    });
//                }
                //验证总字符数
                if(_getStringLength(_boxValue) > 200) {
                    nsDialog.jAlert('人员名请不要超过200个字符!', null, function() {
                        $(event.target).focus().select();
                    });
                }
                //每个人名的字符数
                else {
                    for(var i=0; i < _boxValueArray.length; i++) {
                        if(_getStringLength(_boxValueArray[i]) > 20) {
                            nsDialog.jAlert('每个人的人名请不要超过20个字符!', null, function() {
                                $(event.target).focus().select();
                            });
                        }
                    }
                }
            });
        }
    };


    //Tab和Esc键时隐藏drop list.
    $(document).bind('keydown', function(event) {
        var keyName = G.keyNameFromEvent(event);

        if (G.contains(keyName, ["tab", "esc"])) {
            droplistLite.hide();
        }
    });

    $.fn._dropdownlist = function(mothed) {
        if(mothed == "worker") {
            return _droplist._worker.apply(this, arguments);
        } else if(mothed == "businessCategary") {
            return _droplist._businessCategary.apply(this, arguments);
        } else {

        }
    };

})(jQuery);