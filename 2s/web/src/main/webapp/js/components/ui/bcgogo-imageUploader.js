/**
 * a image-uploader component with flash core
 *
 * @see imageUploaderSample.jsp
 *
 * @author zhen.pan
 * */
;(function($, document, swfobject) {
/** define primary options */
    var componentKeyName    = "imageUploader",
        pathPrefix          = "js/components/themes/res/imageupload/"

/** define self-scope public Functions */
    function getFlashContentPreloadTemplate () {
        var flashId = nodeId = componentKeyName + "_" + (new Date).getTime();

        var tiles = "" +
            "<div id=\"" + nodeId + "\">" +
            "    <p>请安装Flash插件.</p>" +
            "    <a href=\"http://mail.bcgogo.com:8088/install_flash_player.exe\">" +
            "        <img src=\"http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif\" alt=\"获取Flash插件\" />" +
            "    </a>" +
            "</div>";

        return {tiles:tiles, flashId: flashId};
    }

    function getFlashBasicOption(flashId, pathPrefix) {
        var flashvars = {};
        var params = {
            quality:            "high",
            bgcolor:            "#ffffff",
            allowscriptaccess:  "sameDomain",
            allowfullscreen:    "true",
            wmode:              "transparent" // "transparent" | "opaque" | "window"
        };
        var attributes = {
            id:     flashId,
            name:   flashId,
            align:  "middle"
        };

        // To use express install, set to playerProductInstall.swf, otherwise the empty string.
        var xiSwefUrlStr = pathPrefix + "expressInstall.swf";
        // For version detection, set to min. required Flash Player version, or 0 (or 0.0.0), for no version detection.
        var swfVersionStr = "11.8.0";
        var appSwfUrlStr = pathPrefix + "ImageUploader.swf";

        return {
            flashvars:      flashvars,
            params:         params,
            attributes:     attributes,
            xiSwefUrlStr:   xiSwefUrlStr,
            swfVersionStr:  swfVersionStr,
            appSwfUrlStr:   appSwfUrlStr
        };
    }

    function getFlash (flashId) {
        return swfobject.getObjectById(flashId);
    }

    /**
     * @param param {
     *     appSwfUrlStr:"",
     *     flashId:"",
     *     swfVersionStr:"",
     *     xiSwefUrlStr:"",
     *     flashvars:"",,
     *     params:"",
     *     attributes
     * }
     */
    function createFlash (param) {
        swfobject.embedSWF(
            param.appSwfUrlStr,     param.flashId,
            param.width + "",       param.height + "",
            param.swfVersionStr,    param.xiSwefUrlStr,
            param.flashvars,        param.params,           param.attributes);
        // JavaScript enabled so display the flashContent div in case it is not replaced with a swf object.
        swfobject.createCSS("#" + param.flashId, "display:block;text-align:left;");
        return getFlash(param.flashId);
    }





/** define component ImageUploader */
    var ImageUploader = function() {
        this._$         = undefined;
        this._$hook     = undefined;
        this._option    = undefined;
        this._flashId   = undefined;
    };

    /**
     * @param option {
     *     "selector":"xxx",
     *     "flashvars":{}
     *     ""
     * }
     */
    ImageUploader.method("init", function(option) {
        var that = this;
        if(this._$) {
            return;
        }

        this._option    = option;
        this._$hook     = $(option.selector);

        // generate a template
        var template = getFlashContentPreloadTemplate();
        this._$         = $(template.tiles);
        this._flashId   = template.flashId;

        var flashBasicOption = getFlashBasicOption(this._flashId, pathPrefix);

        // set your customized vars
        var flashvars = flashBasicOption.flashvars;
        $.extend(flashvars, $.imageUploader.defaultOption.flashvars);
        $.extend(flashvars, option.flashvars);
        flashvars.ext = JSON.stringify(flashvars.ext); // ext params to json string
        // add js2asFlashReadyFn
        var js2asFlashReadyFnName = "js2asFlashReadyFn_" + (new Date()).getTime();
        App.namespace("tmp.flash." + js2asFlashReadyFnName);
        flashvars.js2asFlashReadyFn = "App.tmp.flash." + js2asFlashReadyFnName;

        // define callbacks
        App.namespace("tmp." + this._flashId + ".selectFileCallback");
        App.namespace("tmp." + this._flashId + ".exceedFileCallback");
        App.namespace("tmp." + this._flashId + ".deleteFileCallback");
        App.namespace("tmp." + this._flashId + ".startUploadCallback");
        App.namespace("tmp." + this._flashId + ".uploadCompleteCallback");
        App.namespace("tmp." + this._flashId + ".uploadErrorCallback");
        App.namespace("tmp." + this._flashId + ".uploadAllCompleteCallback");

        var empFn = function () {;}
        App.tmp[this._flashId].selectFileCallback           = option.selectFileCallback || empFn;
        App.tmp[this._flashId].exceedFileCallback           = option.exceedFileCallback || empFn;
        App.tmp[this._flashId].deleteFileCallback           = option.deleteFileCallback || empFn;
        App.tmp[this._flashId].startUploadCallback          = option.startUploadCallback || empFn;
        App.tmp[this._flashId].uploadCompleteCallback       = option.uploadCompleteCallback || empFn;
        App.tmp[this._flashId].uploadErrorCallback          = option.uploadErrorCallback || empFn;
        App.tmp[this._flashId].uploadAllCompleteCallback    = option.uploadAllCompleteCallback || empFn;

        // set js2asFlashReadyFn callback
        App.tmp.flash[js2asFlashReadyFnName] = function() {
            var flashObj = that.getFlashObject(that._flashId);

            flashObj[$.imageUploader.constOption.as2jsFlashInit]();
            flashObj[$.imageUploader.constOption.as2jsSetJSFuncName] ([
                "App.tmp." + that._flashId + ".selectFileCallback",
                "App.tmp." + that._flashId + ".exceedFileCallback",
                "App.tmp." + that._flashId + ".deleteFileCallback",
                "App.tmp." + that._flashId + ".startUploadCallback",
                "App.tmp." + that._flashId + ".uploadCompleteCallback",
                "App.tmp." + that._flashId + ".uploadErrorCallback",
                "App.tmp." + that._flashId + ".uploadAllCompleteCallback"
            ]);

            // set flashId on flashObj attribute
            $(flashObj).attr("data-flash-id", that._flashId);
        };

        // init template.tiles to DOM
        this._$hook.append(this._$);

        // create flashs
        createFlash({
            width:              flashvars.width,
            height:             flashvars.height,
            appSwfUrlStr:       flashBasicOption.appSwfUrlStr,
            flashId:            this._flashId,
            swfVersionStr:      flashBasicOption.swfVersionStr,
            xiSwefUrlStr:       flashBasicOption.xiSwefUrlStr,
            flashvars:          flashvars,
            params:             flashBasicOption.params,
            attributes:         flashBasicOption.attributes
        });
    });

    ImageUploader.method("getFlashId", function() {
        return this._flashId;
    });

    ImageUploader.method("getFlashObject", function() {
        return swfobject.getObjectById(this._flashId);
    });

    ImageUploader.method("getJqFlashObject", function() {
        return $(swfobject.getObjectById(this._flashId));
    });

    ImageUploader.method("remove", function() {
        swfobject.removeSWF(this._flashId);
        this._$.remove();

        App.tmp[this._flashId]  = undefined;
        this._$                 = undefined;
        this._$hook             = undefined;
    });

    ImageUploader.method("show", function() {
        var $flash = $(this.getFlashObject());

        if ( !$flash ) return;

        $flash
            .attr("width",   this._option.flashvars.width)
            .attr("height",  this._option.flashvars.height);
    });

    ImageUploader.method("hide", function() {
        var $flash = $(this.getFlashObject());

        if ( !$flash ) return;

        $flash
            .attr("width",   1)
            .attr("height",  1);
    });


/** define setting */
    $.imageUploader = {};
    $.extend($.imageUploader, {
        constOption:{
            as2jsSetJSFuncName:     "setJSFuncName",
            as2jsFlashInit:         "flashInit"
        },
        defaultOption:{
            selector:               "",
            flashvars:{
                "debug":            "on",
                "transparent":      "false",
                "width":            157,
                "height":           45,
                "url":              "http://v0.api.upyun.com/bcgogo-dev/",
                "currentItemNum":   0,
                "buttonBgUrl":      "",
                "buttonOverBgUrl":  "",
                // will be encoded to json string
                "fileType":{
                    "description":  "Image Files",
                    "extension":    "*.jpeg;*.png;*.jpg;"
                },
                "maxFileNum":       "4",
                "maxFileSize":      "5",  // unit is (MB)
                "dataFieldName":    "file",
                "descFieldName":    "Images",
                // will be encoded to json string
                "ext":{
                    "policy":       "",
                    "signature":    ""
                }
            },
            selectFileCallback:     undefined,
            exceedFileCallback:     undefined,
            deleteFileCallback:     undefined,
            startUploadCallback:    undefined,
            uploadCompleteCallback: undefined,
            uploadErrorCallback:    undefined,
            uploadAllCompleteCallback:undefined
        }
    });

    // make a alias to custom namespace
    App.namespace("Module.ImageUploader");
    App.Module.ImageUploader = ImageUploader;

} (jQuery, document, swfobject));





/**
 * ImageUploaderView view-component,  use this together with ImageUploader
 *
 * @see imageUploaderSample.jsp
 *
 * @author zhen.pan
 * */
;
(function ($, document, swfobject) {
/** define primary options*/
    var C = {
        content:        "imageuploader-view",
        frameImage:     "frame-image",
        frameUploading: "frame-uploading",
        imageItem:      "image-item",
        imageNone:      "image-none",
        imageName:      "image-name",
        imageAssist:    "image-assist",
        buttonDelete:   "button-delete"
    };

    var T = {
        content:"" +
            "<div class='" + C.content + "'></div>",
        frameImage:"" +
            "<ul class='" + C.frameImage + "'></ul>",
        frameUploading:"" +
            "<ul class='" + C.frameUploading + "'></ul>",
        item:"" +
            "<li>" +
            "    <div class='" + C.imageItem + "'>" +
            "        <img src='' alt=''/>" +
            "    </div>" +
            "    <div class='" + C.imageNone + "'>" +
            "        <img src='' alt=''/>" +
            "    </div>" +
            "    <div class='" + C.imageName+ "'></div>" +
            "    <div class='" + C.imageAssist + "'></div>" +
            "    <div class='" + C.buttonDelete + "'>" + unescape("%u2715") + "</div>" +
            "    <div style='clear:both;float:none;'></div>" +
            "</li>"
    };


/** define ImageUploaderView */
    var ImageUploaderView = function () {
        this._$         = undefined;
        this._$hook     = undefined;
        this._data      = undefined;
        this._state     = "idle"; // idle | uploading
        this._option    = undefined;
    };

    ImageUploaderView.method("init", function (option) {
        var that = this;

        this._option = {};
        $.extend(this._option, $.imageUploaderView.defaultOption);
        $.extend(this._option, option);
        option = this._option;

        this._$hook = $(option.selector);
        this._$ = $(T.content);

        var $content = this._$;
        $content.css({
            "width":        option.width,
            "height":       option.height,
            "border":       option.borderVisible ? "solid 1px " + option.borderColor : "none"
        });

        var $frameImage = $(T.frameImage),
            $frameUploading = $(T.frameUploading),
            $clear = $("<div style='clear:both;float:none;'></div>");

        $frameImage.css({
            "width":        option.width - option.paddingLeft - option.paddingRight,
            "height":       option.height - option.paddingTop - option.paddingBottom,
            "marginLeft":   option.paddingLeft,
            "marginRight":  option.paddingRight,
            "marginTop":    option.paddingTop,
            "marginBottom": option.paddingBottom
        });

        $frameUploading.css({
            "width":        option.width - option.paddingLeft - option.paddingRight,
            "height":       option.height - option.paddingTop - option.paddingBottom,
            "line-height":  option.height - option.paddingTop - option.paddingBottom + "px",
            "left":         option.paddingLeft,
            "right":        option.paddingRight,
            "top":          option.paddingTop,
            "bottom":       option.paddingBottom
        });

        $frameUploading.html(option["waitingInfo"]);
        if(option["showWaitingImage"] === false) {
            $frameUploading.css("background", "none");
        }

        $content
            .append($frameImage)
            .append($clear)
            .append($frameUploading);

        $frameImage.show();
        $frameUploading.hide();

        this._$hook.append($content);

        this.update([]);

        return this;
    });

    /**
     * @param state "idle" | "uploading"
     * */
    ImageUploaderView.method("setState", function (state) {
        var $frameUploading = $("." + C.frameUploading, this._$);

        if(state === "uploading") {
            this._state = "uploading";
            $frameUploading.show();
        } else {
            this._state = "idle";
            $frameUploading.hide();
        }

        return this;
    });

    ImageUploaderView.method("doDelete", function(event) {
        var that = this,
            $frameImage     = $("." + C.frameImage,     this._$);
        var $thisLi         = $(event.currentTarget).parent("li"),
            thisIndex       = $frameImage.find("li").index($thisLi),
            thisData        = that._data[thisIndex];

        that._data.splice(thisIndex , 1);
        that.update(that._data);

        if(that._option.onDelete) {
            that._option.onDelete(event, thisData, thisIndex);
        }
    });

    /**
     * @param data [
     *     {
     *         "url":           "xxxxx",
     *         "name":          "xxxxx",
     *
     *         "color":         "",  // not necessary
     *
     *         "isPlaceholder": true | false,  // not necessary
     *
     *         "isEmphasis":    true | false,  // not necessary
     *         "emphasisColor": "#xxxxx", // not necessary, but make sure "isEmphasis = true"
     *
     *         "isAssist":      true | false,  // not necessary
     *         "assistButtonLabel":""
     *     }
     * ]
     * */
    ImageUploaderView.method("_update", function(data) {
        var that = this,
            $frameImage         = $("." + C.frameImage,     this._$);

        this.resetView();

        var $item           = undefined,
            $imageItem      = undefined,
            $imageName      = undefined,
            $imageAssist    = undefined,
            $buttonDelete   = undefined;

        var hasImageName = function (iData) {
            var has = false;
            if(iData) {
                for (var i = 0; i < iData.length; i++) {
                    var dataItem = iData[i];
                    if(!G.isEmpty(dataItem.name)) {
                        has = true;
                        break;
                    }
                }
            }
            return has;
        };

        var hasImageAssist = function(iData) {
            var has = false;
            if(iData) {
                for (var i = 0; i < iData.length; i++) {
                    var dataItem = iData[i];
                    if(dataItem.isAssist === true) {
                        has = true;
                        break;
                    }
                }
            }
            return has;
        };

        var isHasImageName = hasImageName(data);
        var isHasImageAssist = hasImageAssist(data);
        for (var i = 0; i < data.length; i++) {
            var dataItem = data[i];

            $item       = $(T.item);
            $imageItem  = $item.find("." + C.imageItem);
            $imageName  = $item.find("." + C.imageName);

            var itemHeight = undefined;
            if( isHasImageName ) {
                itemHeight = that._option.iHeight + that._option.labelHeight;
            } else {
                itemHeight = that._option.iHeight;
            }

            $item.css({
                "width":                that._option.iWidth,
                "height":               itemHeight,
                "marginRight":          that._option.horizontalGap,
                "marginBottom":         that._option.verticalGap
            });

            $imageItem.css({
                "width":                that._option.iWidth,
                "height":               that._option.iHeight,
                "border":               that._option.iBorderVisible ? "solid 1px" + that._option.iBorderColoer : "none"
            });
            $imageItem.find("img")
                .css({
                    "width":            that._option.iWidth,
                    "height":           that._option.iHeight
                })
                .attr("src", dataItem["url"]);

            if(isHasImageName) {
                $imageName
                    .css({
                        "width":        that._option.iWidth,
                        "height":       that._option.labelHeight,
                        "line-height":  that._option.labelHeight + "px"
                    })
                    .html(dataItem.name)
                    .show();

                if(dataItem.isEmphasis) {
                    // use default styles
                    $imageName.css("color", dataItem.emphasisColor || "#ff7800" );
                } else {
                    $imageName.css("color", dataItem.color || "#3b3b3b" );
                }
            } else {
                $imageName.hide();
            }


            $imageAssist = $item.find("." + C.imageAssist);
            if(isHasImageAssist) {
                $imageAssist
                    .css({
                        "width":        that._option.iWidth,
                        "height":       that._option.labelHeight,
                        "line-height":  that._option.labelHeight + "px"
                    });

                if(dataItem.isAssist) {
                    $imageAssist
                        .html(dataItem.assistButtonLabel || "")
                        .bind("click", function(event) {
                            var $thisLi = $(this).closest("li"),
                                $liList = $thisLi.parent().find("li"),
                                index   = $liList.index($thisLi);

                            if(that._option.onAssistButtonClick) {
                                that._option.onAssistButtonClick(event, data[index], index);
                            }
                        });
                }
            }



            $buttonDelete = $item.find("." + C.buttonDelete);
            // onDelete handler
            $buttonDelete.bind("click", function(event){
                if( that._option.beforeDelete) {
                    that._option.beforeDelete(event)
                    return;
                }
                that.doDelete(event);
            });



            $buttonDelete.hide();
            $item
                .bind("mouseenter", function(event) {
                    var $this       = $(this),
                        $itemList   = $this.parent().find("li"),
                        index       = $itemList.index($this);
                    if( that._option.isDeletable && that._state !== "uploading" && !data[index]["isPlaceholder"] ) {
                        $this.find("." + C.buttonDelete).show();
                    }
                })
                .bind("mouseleave", function(event) {
                    var $this       = $(this),
                        $itemList   = $this.parent().find("li"),
                        index       = $itemList.index($this);
                    if( that._option.isDeletable && that._state !== "uploading" && !data[index]["isPlaceholder"] ) {
                        $this.find("." + C.buttonDelete).hide();
                    }
                });

            $frameImage.append($item);
        } /*end for*/

        return this;
    });

    /**
     * @param data [
     *     {
     *         "url":  "xxxxx",
     *         "name": "xxxxx",
     *         "color":"", // not necessary
     *
     *         "isEmphasis":    true | false,  // not necessary
     *         "emphasisColor": "#xxxxx", // not necessary, but make sure "isEmphasis = true"
     *
     *         "isAssist":      true | false,  // not necessary
     *         "assistButtonLabel":"",
     *         "onAssistButtonClick":function() {}
     *     }
     * ]
     * */
    ImageUploaderView.method("update", function (data) {
        var $frameUploading = $("." + C.frameUploading, this._$);

        // if nodata , show placeholder image
        if( !data || (G.isArray(data) && data.length === 0 ) ) {
            this._data = [];
        }

        // store data
        this._data = data;
        $frameUploading.hide();

        var placeholderItemNum = this._option.maxFileNum - data.length,
            placeholderDataArr = [];

        for (var i = 0; i < placeholderItemNum; i++) {
            var dataItem = {
                "isPlaceholder": true,
                "url":this._option.placeholderUrl,
                "name":""
            };
            placeholderDataArr.push(dataItem);
        }

        this._update(data.concat(placeholderDataArr));

        return this;
    });/*end method update*/

    ImageUploaderView.method("remove", function () {
        this.resetView();
        this._$.remove();

        this._$         = undefined;
        this._$hook     = undefined;
        this._data      = undefined;
        this._state     = "idle"; // idle | uploading
        this._option    = undefined;

        return this;
    });

    ImageUploaderView.method("resetView", function() {
        var $liList = this._$.find("li");
        $liList.remove();

        return this;
    });

    ImageUploaderView.method("setIsDeletable", function(bool) {
        this._option.isDeletable = bool;
        if( bool ) {
            this._$.find("." + C.buttonDelete).hide();
        }

        return this;
    });

    ImageUploaderView.method("getIsDeletable", function() {
        return this._option.isDeletable;
    });



/** define setting */
    $.imageUploaderView = {};
    $.extend($.imageUploaderView, {
        constOption: {},
        defaultOption: {
            selector:       "",
            isDeletable:    true,
            width:          580,
            height:         200,
            borderVisible:  true,
            borderColor:    "#ddd",
            iWidth:         100,
            iHeight:        100,
            iBorderVisible: true,
            iBorderColoer:  "#ddd",
            labelHeight:    20,
            horizontalGap:  20,
            verticalGap:    20,
            paddingTop:     20,
            paddingBottom:  20,
            paddingLeft:    20,
            paddingRight:   20,
            waitingInfo:    "图片正在加载中...",
            placeholderUrl: "js/components/themes/res/imageupload/bg_none_uploaded_pic.png",  // url of placeholder
            maxFileNum:     4,
            showWaitingImage:true,
            onAssistButtonClick:function(event, data, index) {
                G.warning("event : " + event);
                G.warning("data  : " + data );
                G.warning("index : " + index);
            },
            onDelete: function (event, data, index) {
                G.warning("event : " + event);
                G.warning("data  : " + data );
                G.warning("index : " + index);
            }
        }
    });

    App.namespace("Module.ImageUploaderView");
    App.Module.ImageUploaderView = ImageUploaderView;
}(jQuery, document, swfobject));