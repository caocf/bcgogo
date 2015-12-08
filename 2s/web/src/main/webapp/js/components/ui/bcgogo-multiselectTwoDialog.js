/**
 *
 */
;
(function () {
    App.namespace("Module.MultiSelectTwoDialog");

    var C = {
        titleGroup:"bcgogo-multiselect-two-dialog-title",
        content: "bcgogo-multiselect-two-dialog",
        selectDialog: "select-dialog",
        ensureDialog: "ensure-dialog",
        categoryList: "category-list",
        selectItem: "select-item",
        hoverItem: "hover-item",
        itemSpread: "item-spread",
        itemRetract: "item-retract",
        itemLabel:"item-label",
        itemButton:"item-button",
        treeList: "tree-list",
        secondTreeLevelItem: "second-tree-level-item",
        thirdTreeLevelItem: "third-tree-level-item",
        itemYellow: "item-yellow",
        itemBlue: "item-blue",
        deleteButton: "delete-button",
        itemActived:"item-actived",
        itemSelected:"item-selected"
    };

    var MultiSelectTwoDialog = function () {
        this._$ = undefined;
        this._data = undefined;
        this._originalData = undefined;
        this._$target = undefined;

        // ensure dialog list
        this._ensureDataList = [];
    };

    /**
     * @public
     * @param param {
     *     data:{},
     *     selector:"",
     *     onCategorySelect:function(firstLetter, event) {},
     *     onSelect:function(data, event) {},
     *     onDelete:function() {data, event}
     * }
     */
    MultiSelectTwoDialog.prototype.init = function (param) {
        this._param = param;
        this._originalData = param.data;
        this._data = this._parseDataToTree(this._originalData);
        this._$target = $(param.selector);

        var $titleGroup = $("<div class='"+ C.titleGroup+"'></div>"),
            $titleSelectDialog = this._createSelectDialogTitle(),
            $titleEnsureDialog = this._createEnsureDialogTitle(this._getEnsureDialogClearHandler());

        $titleGroup.append($titleSelectDialog).append($titleEnsureDialog).append($("<div style='clear:both;float:none;'></div>"));
        this._$target.append($titleGroup).append($("<div style='clear:both;float:none;'></div>"));


        var $content = $("<div class='" + C.content + "'></div>"),
            $selectDialog = this._getSelectDialog(),
            $ensureDialog = this._getEnsureDialog([]);

        $content
            .append($selectDialog)
            .append($ensureDialog);

        this._$target.append($content);
    };

    /**
     * @private
     * @param fnOnSelect function(event, name) {}
     */
    MultiSelectTwoDialog.prototype._createSelectDialogCategoryList = function (fnOnSelect) {
        var abbrArr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split(""),
            sHtml = "<div class='" + C.categoryList + "'>",
            $group;

        for (var i = 0; i < abbrArr.length; i++) {
            sHtml += "<span data-key-value='" + abbrArr[i] + "'>" + abbrArr[i] + "</span>";
        }
        sHtml += "<span data-key-value='" + "*" + "'>" + "&nbsp;[全部]" + "</span>";
        sHtml += "<span style='clear:both;float:none;'></span>";
        sHtml += "</div>";

        $group = $(sHtml);
        $group.find("span").bind("click", function (event) {

            if ($("#disableClick").val() == "true") {
              return false;
            }
            var firstLetter = $(this).attr("data-key-value"),
                $this = $(this),
                $spanList = $this.parent().find("span");

            $spanList.removeClass(C.itemActived);
            $(this).addClass(C.itemActived);

            fnOnSelect(firstLetter, event);
        });
        return $group;
    };

    /**
     * @private
     * @param firstLetter {String}
     * @param fnOnSelect function() {
     *
     * }
     */
    MultiSelectTwoDialog.prototype._createSelectDialogTreeList = function (firstLetter, fnOnSelect) {
        var $group = $("<div class='" + C.treeList + "'></div>");

        // render ui
        var arr = [];
        if (firstLetter && firstLetter !== "*") {
            arr = [firstLetter];
        }
        $group.html("").append(this._getSelectDialogTree(arr, fnOnSelect));
        return $group;
    };

    /**
     * @private
     */
    MultiSelectTwoDialog.prototype._getSelectDialogTree = function (firstLetterArray, fnOnSelect) {
        var that = this;

        var _getJqList = function (arr, data) {
            var $tree = $("<ul></ul>");

            if (arr.length === 0) {
                arr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
            }

            for (var i = 0; i < arr.length; i++) {
                var firstLetter = arr[i];

                if (data[firstLetter]) {
                    for (var brandName in data[firstLetter]) {
                        var brandDataList = (function(dataList) {
                            var list = [];
                            for (var o in dataList) {
                                list.push(dataList[o]);
                            }
                            return list;
                        } (data[firstLetter][brandName]));

                        var strLi = "" +
                            "<li id='li_"+brandDataList[0]["brandId"]+"' class='" + C.secondTreeLevelItem + "'>" +
                            "    <div>" +
                            "        <span class='" + C.itemSpread + "' onselectstart='return false;'>-</span>" +
                            "        <span class='" + C.itemRetract + "' onselectstart='return false;'>+</span>" +
                            "        <span class='"+ C.itemLabel+" "+ C.itemButton+"'>" + brandName + "</span>" +
                            "        <div style='clear:both;float:none;'></div>" +
                            "    </div>" +
                            "</li>";
                        var $brandLi = $(strLi);

                        $brandLi.append($("<ul></ul>"));
                        $brandLi.appendTo($tree);

                        var $brandLiUl = $brandLi.children("ul");

                        for (var modelName in data[firstLetter][brandName]) {
                            var val = data[firstLetter][brandName][modelName];
                            var $modelLi = $("<li id='li_"+val["modelId"]+"' class='" + C.thirdTreeLevelItem + "'><div class='"+ C.itemButton+"' onselectstart='return false;'>" + modelName + "</div></li>");

                            $modelLi.attr("data-stored", encodeURIComponent(JSON.stringify(val)));
                            $modelLi.appendTo($brandLiUl);
                        }

                        $brandLi.find("." + C.itemSpread).hide();
                        $brandLi.children("ul").hide();

                        $brandLi.attr("data-stored", encodeURIComponent(JSON.stringify(brandDataList)));
                    }
                }
            }
            return $tree;
        };

        var $tree = _getJqList(firstLetterArray, this._data);

        // 处理展开收起
        var $secondTreeList = $tree.find("." + C.secondTreeLevelItem);
        if($secondTreeList.length < 1) {
            $secondTreeList.eq(0).find("." + C.itemSpread).show();
            $secondTreeList.eq(0).find("." + C.itemRetract).hide();
            $secondTreeList.eq(0).find("ul").show();
        }

        $secondTreeList.find("." + C.itemSpread).bind("click", function(event) {
            if ($("#disableClick").val() == "true") {
              return false;
            }
            var $secondTreeTitle = $(this).parent(),
                $secondTreeContent = $secondTreeTitle.parent();

            $(this).hide();
            $secondTreeTitle.find("." + C.itemRetract).show();
            $secondTreeContent.children("ul").hide();
        });


        $secondTreeList.find("." + C.itemRetract).bind("click", function(event) {
            if ($("#disableClick").val() == "true") {
              return false;
            }
            var $secondTreeTitle = $(this).parent(),
                $secondTreeContent = $secondTreeTitle.parent();

            $(this).hide();
            $secondTreeTitle.find("." + C.itemSpread).show();
            $secondTreeContent.children("ul").show();
        });

        $tree.find("." + C.itemButton).bind("click", function(event) {
            if ($("#disableClick").val() == "true") {
              return false;
            }
            var $li = $(this).closest("li");
            var itemData = JSON.parse(decodeURIComponent($li.attr("data-stored")));
            if(G.isArray(itemData) === false) {
                itemData = [itemData];
            }
            fnOnSelect(itemData, event)
        });
        return $tree;
    };

    /**
     * @private
     */
    MultiSelectTwoDialog.prototype._getSelectDialog = function (firstLetter) {
        var $selectDialog = $("<div class='" + C.selectDialog + "'></div>"),
            $categoryList = this._createSelectDialogCategoryList(this._getSelectDialogCategorySelectHandler()),
            $tree = this._createSelectDialogTreeList("*", this._getSelectDialogSelectHandler());

        $selectDialog.append($categoryList).append($tree);
        return $selectDialog;
    };

    MultiSelectTwoDialog.prototype._createSelectDialogTitle = function() {
        return $("<div class='group-select-dialog-title'>请从下表中挑选</div><input type='hidden' id='disableClick' value='false'/>");
    };

    MultiSelectTwoDialog.prototype._createEnsureDialogTitle = function(fnOnClear) {
        var that = this,
            title = "<div class='group-ensure-dialog-title'>已选择适用车型<span class='button-clear'>[清空]</span></div>",
            $title = $(title),
            $btClear = $title.find(".button-clear");

        $btClear.bind("click", function(event) {
            if ($("#disableClick").val() == "true") {
              return false;
            }
            that._clearDataFromEnsureDialog();
            fnOnClear(event);
        });
        return $title;
    };

    /**
     * if developer not set , we use default handler
     * @private
     */
    MultiSelectTwoDialog.prototype._getSelectDialogCategorySelectHandler = function () {
        var that = this;

        var handler = function (firstLetter, event) {
            that.changeSelectDialogTreeByFirstLetter(firstLetter);
            if (firstLetter && firstLetter !== "*") {
                that._$target.find("." + C.itemSpread).show();
                that._$target.find("." + C.itemRetract).hide();
                that._$target.find("."+ C.secondTreeLevelItem).children("ul").show();
            }

            if (that._param.onCategorySelect) {
                that._param.onCategorySelect(firstLetter, event);
            }
        };
        return handler;
    };

    MultiSelectTwoDialog.prototype._getSelectDialogSelectHandler = function() {
        var that = this;

        var handler = function(itemData, event) {
            that.setDataToEnsureDialog(itemData);
            if(that._param.onSelect) {
                that._param.onSelect(itemData, event);
            }
        };

        return handler;
    };

    MultiSelectTwoDialog.prototype.setDataToEnsureDialog = function(itemData) {
        var that = this,
            $content = this._$target.find("." + C.content);

        var fnHasSame = function (obj, arr) {
            var hasSame = false;

            for (var i = 0; i < arr.length; i++) {
                var o = arr[i];
                if(obj["modelId"] === o["modelId"]) {
                    hasSame = true;
                    break;
                }
            }
            return hasSame;
        };
        for (var i = 0; i < itemData.length; i++) {
            if(fnHasSame(itemData[i], this._ensureDataList) === false) {
                this._ensureDataList = this._ensureDataList.concat(itemData[i]);
            }
        }

        $content.find("." + C.ensureDialog).remove();
        $content.append(this._getEnsureDialog(this._ensureDataList, this._getEnsureDialogItemDeleteHandler()));
    };

    MultiSelectTwoDialog.prototype._getEnsureDialogItemDeleteHandler = function() {
        var that = this;
        var handler = function(itemData, event) {
            var liId;
            if(itemData.length === 1){
                liId = itemData[0]["modelId"];
            }else{
                liId = itemData[0]["brandId"];
            }
            $("#li_"+liId).find("."+ C.itemButton).removeClass(C.itemSelected);
            if(that._param.onDelete) {
                that._param.onDelete(itemData, event);
            }
        };
        return handler;
    };

    MultiSelectTwoDialog.prototype._getEnsureDialogClearHandler = function() {
        var that = this;
        var handler = function(event) {
            that._$target.find("."+ C.itemSelected).removeClass(C.itemSelected);
            if(that._param.onClear) {
                that._param.onClear(event);
            }
        };
        return handler;
    };
    /**
     * @public
     */
    MultiSelectTwoDialog.prototype.changeSelectDialogTreeByFirstLetter = function (firstLetter) {
        var $selectDialog = this._$target.find("." + C.selectDialog);

        $selectDialog.find("." + C.treeList).remove();

        var $treeList = this._createSelectDialogTreeList(firstLetter, this._getSelectDialogSelectHandler());
        $selectDialog.append($treeList);

        var _getJsonByKey = function (inList, key) {
            var retVal = {};
            for (var i = 0, len = inList.length; i < len; i++) {
                var obj = inList[i];
                retVal[obj[key]] = retVal[obj[key]] || [];
                retVal[obj[key]].push(obj);
            }
            return retVal;
        };

        var dataJson = _getJsonByKey(this._ensureDataList, "brandId"),
            totalJson = _getJsonByKey(this._originalData, "brandId");
        for (var k in dataJson) {
            var addedCount = dataJson[k].length,
                totalCount = totalJson[k].length;

            if (addedCount === totalCount) {
                // set 2nd-level
                $("#li_"+dataJson[k][0]["brandId"]).find("."+ C.itemButton).addClass(C.itemSelected);
            } else {
                // set 3rd-level
                for (var j = 0; j < dataJson[k].length; j++) {
                    var obj = dataJson[k][j];
                    $("#li_"+obj["modelId"]).find("."+ C.itemButton).addClass(C.itemSelected);
                }
            }

        }
    };

    /**
     * @private
     */
    MultiSelectTwoDialog.prototype._getEnsureDialog = function (dataList, fnOnDelete) {
        var that = this,
            $group = $("<div class='" + C.ensureDialog + "'><ul></ul></div>");

        if (!dataList || dataList.length <= 0) {
            return $group;
        }

        // { modelId1:[ itemList... ], modelId2:[ itemList... ] }
        var _getJsonByKey = function (inList, key) {
            var retVal = {};
            for (var i = 0, len = inList.length; i < len; i++) {
                var obj = inList[i];
                retVal[obj[key]] = retVal[obj[key]] || [];
                retVal[obj[key]].push(obj);
            }
            return retVal;
        };

        var dataJson = _getJsonByKey(dataList, "brandId"),
            totalJson = _getJsonByKey(this._originalData, "brandId");

        for (var k in dataJson) {
            var $li,
                addedCount = dataJson[k].length,
                totalCount = totalJson[k].length;

            if (addedCount === totalCount) {
                // set 2nd-level
                $li = $("<li><span></span><div class='" + C.deleteButton + "'></div><div style='clear:both;float:none;'></div></li>");
                $li.find("span").text(dataJson[k][0]["brandName"]);
                $li.attr("data-stored-val", encodeURIComponent(JSON.stringify(dataJson[k])));
                $group.find("ul").append($li);
                $("#li_"+dataJson[k][0]["brandId"]).find("."+ C.itemButton).addClass(C.itemSelected);
            } else {
                // set 3rd-level
                for (var j = 0; j < dataJson[k].length; j++) {
                    var obj = dataJson[k][j];
                    $li = $("<li><span></span><div class='" + C.deleteButton + "'></div><div style='clear:both;float:none;'></div></li>");
                    $li.find("span").text(obj["modelName"]);
                    $li.attr("data-stored-val", encodeURIComponent(JSON.stringify(obj)));
                    $group.find("ul").append($li);
                    $("#li_"+obj["modelId"]).find("."+ C.itemButton).addClass(C.itemSelected);
                }
            }

        }

        $group.find(".delete-button").bind("click", function (event) {
            if ($("#disableClick").val() == "true") {
              return false;
            }
            var $li = $(this).closest("li"),
                data = JSON.parse(decodeURIComponent($li.attr("data-stored-val")));

            if(G.isArray(data) === false) {
                data = [data];
            }

            $li.remove();

            for (var i = 0; i < data.length; i++) {
                var obj1 = data[i];

                for (var j = 0; j < that._ensureDataList.length; j++) {
                    var obj2 = that._ensureDataList[j];

                    if(obj1["modelId"] === obj2["modelId"]) {
                        that._ensureDataList.splice(j, 1);
                        break;
                    }
                }
            }

            fnOnDelete(data, that._ensureDataList, event);
        });

        return $group;
    };

    MultiSelectTwoDialog.prototype._clearDataFromEnsureDialog = function() {
        var that = this;

        this._ensureDataList = [];
        this._$target.find("." + C.ensureDialog).find("ul li").remove();
    };


    /**
     * @private
     * @param indata this._originalData
     */
    MultiSelectTwoDialog.prototype._parseDataToTree = function (indata) {
        // parse data to tree
        var tree = {};
        for (var i = 0; i < indata.length; i++) {
            var obj = indata[i],
                firstLetter = obj["firstLetter"],
                brandId = obj["brandName"],
                modelId = obj["modelName"];

            tree[firstLetter] = tree[firstLetter] || {};
            tree[firstLetter][brandId] = tree[firstLetter][brandId] || {};
            tree[firstLetter][brandId][modelId] = obj;
        }

//        console.log(tree);
        return tree;
    };

    MultiSelectTwoDialog.prototype.getAddedData = function() {
        return this._ensureDataList;
    };
    MultiSelectTwoDialog.prototype.clearAllSelectedData = function() {
        this._clearDataFromEnsureDialog();
    };
    MultiSelectTwoDialog.prototype.initSelectedData = function(dataList) {
        var that = this;
        if (!dataList || dataList.length <= 0) {
            return;
        }
        that.setDataToEnsureDialog(dataList);
    };

    App.Module.MultiSelectTwoDialog = MultiSelectTwoDialog;
}());