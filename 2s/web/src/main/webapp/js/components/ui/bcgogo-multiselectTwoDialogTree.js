/**
 * 不是很通用  不支持 无限级树
 */
;
(function () {
    App.namespace("Module.MultiSelectTwoDialogTree");
    var C = {
        titleGroup:"bcgogo-multiselect-two-dialog-title",
        content: "bcgogo-multiselect-two-dialog",
        selectDialog: "select-dialog",
        ensureDialog: "ensure-dialog",
        searchCondition: "search-condition",
        selectItem: "select-item",
        hoverItem: "hover-item",
        itemHide: "item-hide",
        itemShow: "item-show",
        itemSpread: "item-spread",
        itemRetract: "item-retract",
        itemLabel:"item-label",
        itemButton:"item-button",
        treeList: "tree-list",
        parentTreeLevelItem: "parent-tree-level-item",
        leafTreeLevelItem: "leaf-tree-level-item",
        itemYellow: "item-yellow",
        itemBlue: "item-blue",
        deleteButton: "delete-button",
        itemActived:"item-actived",
        emptyInputSearchWord:"请输入关键字",
        itemSelected:"item-selected"
    };

    var MultiSelectTwoDialogTree = function () {
        this._$ = undefined;
        this._data = undefined;
        this._startLevel = undefined;
        this._originalData = undefined;
        this._$target = undefined;
        this._isExpanded = undefined;
        // ensure dialog list
        this._ensureDataList = [];
    };

    /**
     * @public
     * @param param {
     *     data:{},
     *     selector:"",
     *     onSearch:function(firstLetter, event) {},
     *     onSelect:function(data, event) {},
     *     onDelete:function() {data, event}
     * }
     */
    MultiSelectTwoDialogTree.prototype.init = function (param) {
        this._param = param;
        this._startLevel =  param.startLevel || 0;
        this._originalData = param.data;
        this._data = this._parseDataToTree(this._originalData);
        this._$target = $(param.selector);
        this._isExpanded = param.isExpanded || false;
        this._ensureDataList = param.ensureDataList || [];

        var $titleGroup = $("<div class='"+ C.titleGroup+"'></div>"),
            $titleSelectDialog = this._createSelectDialogTitle(),
            $titleEnsureDialog = this._createEnsureDialogTitle(this._getEnsureDialogClearHandler());

        $titleGroup.append($titleSelectDialog).append($titleEnsureDialog).append($("<div style='clear:both;float:none;'></div>"));
        this._$target.append($titleGroup).append($("<div style='clear:both;float:none;'></div>"));


        var $content = $("<div class='" + C.content + "'></div>");
        this._$target.append($content);

        var $selectDialog = this._getSelectDialog();
        $content.append($selectDialog);
        var $ensureDialog = this._getEnsureDialog(this._ensureDataList,this._getEnsureDialogItemDeleteHandler());
        $content.append($ensureDialog);


    };
    /**
     * @private
     * @param indata this._originalData
     */
    MultiSelectTwoDialogTree.prototype._parseDataToTree = function (indata) {
        var getChildrenNode = function (node) {
            var data = [];
            $.each(node,function(index,val){
                if(!val["leaf"] && !G.isEmpty(val["children"])){//不是叶子节点
                    data = data.concat(val["children"]);
                }
            });
            return data;
        };

        // parse data to tree
        var treeData = G.isArray(indata)?indata:[indata];
        if(!G.isEmpty(this._startLevel) && this._startLevel>0){
            for (var i = 0; i < this._startLevel; i++) {
                treeData = getChildrenNode(treeData);
            }
        }
        return treeData;
    };
    /**
     * @private
     * @param fnOnSelect function(event, name) {}
     */
    MultiSelectTwoDialogTree.prototype._createSelectDialogSearchCondition = function (fnOnSelect) {
        var sHtml = "<div class='" + C.searchCondition + "'>",
            $group;

        sHtml += "<input type='text' name='search-input' value='"+ C.emptyInputSearchWord +"' class='search-scope'>";
        sHtml += "<input type='button' name='search-btn' title='搜索' class='search-btn'>";
        sHtml += "<span style='clear:both;float:none;'></span>";
        sHtml += "</div>";

        $group = $(sHtml);
        $group.find("input[name='search-btn']").bind("click", function (event) {
            var searchWord = $group.find("input[name='search-input']").val().replace(/[\ |\\]/g, "");
            if (searchWord == C.emptyInputSearchWord) {
              searchWord = "";
            }
            fnOnSelect(searchWord, event);
        });

        $group.find("input[name='search-input']").bind("click",function (event) {
          if ($(this).val() == C.emptyInputSearchWord) {
            $(this).val("");
          }
        }).bind("blur", function (event) {
              if ($(this).val() == "") {
                $(this).val(C.emptyInputSearchWord);
              }
            });
        return $group;
    };

    /**
     * @private
     * @param fnOnSelect function() {
     *
     * }
     */
    MultiSelectTwoDialogTree.prototype._createSelectDialogTreeList = function (fnOnSelect) {
        var $group = $("<div class='" + C.treeList + "'></div>");
        $group.html("").append(this._getSelectDialogTree(this._data, fnOnSelect));
        return $group;
    };

    /**
     * @private
     */
    MultiSelectTwoDialogTree.prototype._getSelectDialogTree = function (treeData, fnOnSelect) {
        var that = this;
        var _getSefNodeInfo = function ($parentNodeUl, data) {
            if(!data) return;
//            console.log( data["leaf"]);
            $.each(data,function(index,val){
                if (!val["leaf"] && val["children"] && val["children"].length>0){
                    var strLi = "" +
                        "<li id='li_"+val["idStr"]+"' class='" + C.parentTreeLevelItem + "'>" +
                        "    <div>" +
                        "        <span class='" +(this._isExpanded?C.itemHide:C.itemShow)+" "+ C.itemSpread + "' onselectstart='return false;'>+</span>" +
                        "        <span class='" +(this._isExpanded?C.itemShow:C.itemHide)+" "+ C.itemRetract + "' onselectstart='return false;'>-</span>" +
                        "        <span class='"+ C.itemLabel+" "+ C.itemButton+"'>" + val["text"] + "</span>" +
                        "        <div style='clear:both;float:none;'></div>" +
                        "    </div>" +
                        "</li>";
                    var $childrenNodeLi = $(strLi);

                    $childrenNodeLi.append($("<ul class='"+(this._isExpanded?C.itemShow:C.itemHide) +"'></ul>"));
                    $childrenNodeLi.appendTo($parentNodeUl);
                    $childrenNodeLi.attr("data-stored", encodeURIComponent(JSON.stringify(val)));
                    _getSefNodeInfo($childrenNodeLi.children("ul"),val["children"]);
                }else{
                    var $modelLi = $("<li id='li_"+val["idStr"]+"' class='" + C.leafTreeLevelItem + "'><div class='"+ C.itemButton+"' onselectstart='return false;'>" + val["text"] + "</div></li>");
                    $modelLi.attr("data-stored", encodeURIComponent(JSON.stringify(val)));
                    $modelLi.appendTo($parentNodeUl);
                }

            });
        }
        var $tree = $("<ul></ul>");
         _getSefNodeInfo($tree, treeData);

//        // 处理展开收起

        $tree.find("." + C.itemSpread).bind("click", function(event) {
            var $treeTitle = $(this).parent(),
                $treeContent = $treeTitle.parent();

            $(this).removeClass(C.itemShow).addClass(C.itemHide);
            $treeTitle.find("." + C.itemRetract).removeClass(C.itemHide).addClass(C.itemShow);
            $treeContent.children("ul").removeClass(C.itemHide).addClass(C.itemShow);
        });
        $tree.find("." + C.itemRetract).bind("click", function(event) {
            var $treeTitle = $(this).parent(),
                $treeContent = $treeTitle.parent();

            $(this).removeClass(C.itemShow).addClass(C.itemHide);
            $treeTitle.find("." + C.itemSpread).removeClass(C.itemHide).addClass(C.itemShow);
            $treeContent.children("ul").removeClass(C.itemShow).addClass(C.itemHide);
        });

        var $itemButton = $tree.find("." + C.itemButton);
        $itemButton.bind("click", function(event) {
            var $li = $(this).closest("li");
            var itemData = JSON.parse(decodeURIComponent($li.attr("data-stored")));
            fnOnSelect(itemData, event)
        });
        return $tree;
    };

    /**
     * @private
     */
    MultiSelectTwoDialogTree.prototype._getSelectDialog = function () {
        var $selectDialog = $("<div class='" + C.selectDialog + "'></div>"),
            $searchCondition = this._createSelectDialogSearchCondition(this._getSelectDialogSearchHandler()),
            $tree = this._createSelectDialogTreeList(this._getSelectDialogSelectHandler());

        $selectDialog.append($searchCondition).append($tree);
        return $selectDialog;
    };

    MultiSelectTwoDialogTree.prototype._createSelectDialogTitle = function() {
        return $("<div class='group-select-dialog-title' style='text-align: left;padding-left: 4px;'>请从下表中挑选</div>");
    };

    MultiSelectTwoDialogTree.prototype._createEnsureDialogTitle = function(fnOnClear) {
        var that = this,
            title = "<div class='group-ensure-dialog-title'>已选择<span class='button-clear'>[清空]</span></div>",
            $title = $(title),
            $btClear = $title.find(".button-clear");

        $btClear.bind("click", function(event) {
            that._clearDataFromEnsureDialog();
            fnOnClear(event);
        });
        return $title;
    };

    /**
     * if developer not set , we use default handler
     * @private
     */
    MultiSelectTwoDialogTree.prototype._getSelectDialogSearchHandler = function () {
        var that = this;

        var handler = function (searchWord, event) {
            if (that._param.onSearch) {
                var data = that._param.onSearch(searchWord, event);
                that._originalData = data;
                that._data = that._parseDataToTree(that._originalData);
            }
            that.changeSelectDialogTree();
            if(!G.isEmpty(searchWord)){
                that._$target.find("."+ C.itemHide).removeClass(C.itemHide).addClass(C.itemShow);
                that._$target.find("."+ C.itemSpread).removeClass(C.itemShow).addClass(C.itemHide);
                that._$target.find("."+ C.itemRetract).removeClass(C.itemHide).addClass(C.itemShow);
            }
        };
        return handler;
    };

    MultiSelectTwoDialogTree.prototype._getSelectDialogSelectHandler = function() {
        var that = this;

        var handler = function(itemData, event) {
            that.setDataToEnsureDialog(itemData);
            if(that._param.onSelect) {
                that._param.onSelect(itemData, event);
            }
        };

        return handler;
    };
    MultiSelectTwoDialogTree.prototype._getEnsureDialogItemDeleteHandler = function() {
        var that = this;
        var handler = function(itemData, event) {
            $("#li_"+itemData["idStr"]).find("."+ C.itemButton).removeClass(C.itemSelected);
            if(that._param.onDelete) {
                that._param.onDelete(itemData, event);
            }
        };
        return handler;
    };

    MultiSelectTwoDialogTree.prototype._getEnsureDialogClearHandler = function() {
        var that = this;
        var handler = function(event) {
            that._$target.find("."+ C.itemSelected).removeClass(C.itemSelected);
            if(that._param.onClear) {
                that._param.onClear(event);
            }
        };
        return handler;
    };

    MultiSelectTwoDialogTree.prototype.setDataToEnsureDialog = function(itemData) {
        var that = this,
            $content = this._$target.find("." + C.content);

        var getNodeByIdStr = function (node,idStr) {
            var data;
            for (var i = 0; i < node.length; i++) {
                var val=node[i];
                if(idStr===val["idStr"]){
                    data = val;
                    break;
                }else if(!val["leaf"] && !G.isEmpty(val["children"])){//不是叶子节点
                    data = getNodeByIdStr(val["children"],idStr);
                }
            };
            return data;
        };
        var fnHasSame = function (obj, arr) {
            var hasSame = false;
            for (var i = 0; i < arr.length; i++) {
                var obj1 = arr[i];
                if(obj["idStr"] === obj1["idStr"]) {
                    hasSame = true;
                    break;
                }else{
                    if(!obj1["leaf"] && !G.isEmpty(obj1["children"])){//不是叶子节点
                        hasSame = fnHasSame(obj,obj1["children"]);
                        if(hasSame){
                            break;
                        }
                    }
                }
            }
            return hasSame;
        };
        //如果选择的 不存在  已选列表里
        if(fnHasSame(itemData, this._ensureDataList) === false) {
            var childNodeSize=1;
            $.each(this._ensureDataList,function(index,node){
                if(node.parentIdStr===itemData.parentIdStr){
                    childNodeSize++;
                }
            });
            //比较已选节点的父节点拥有的子节点数量  是否 和 已选节点中的 一致  如果一致  选择的节点和已经选择的  用 父节点替换
            var parentItemData = getNodeByIdStr(this._data,itemData.parentIdStr);
            if(!G.isEmpty(parentItemData) && parentItemData["children"].length===childNodeSize){
                itemData = parentItemData;
            }
            //再判断  选择节点的父节点 是否 存在  已选节点中
            if(!itemData["leaf"] && !G.isEmpty(itemData["children"])){//不是叶子节点
                var tempDataList = [];
                $.each(this._ensureDataList,function(index,node){
                    if(fnHasSame(node, [itemData]) === false){//如果选择的 不存在  已选列表里
                        tempDataList.push(node);
                    }
                });
                this._ensureDataList = tempDataList;
            }

            this._ensureDataList.push(itemData);
        }

        $content.find("." + C.ensureDialog).remove();
        $content.append(this._getEnsureDialog(this._ensureDataList, this._getEnsureDialogItemDeleteHandler()));


    };

    /**
     * @public
     */
    MultiSelectTwoDialogTree.prototype.changeSelectDialogTree = function () {
        var $selectDialog = this._$target.find("." + C.selectDialog);

        $selectDialog.find("." + C.treeList).remove();

        var $treeList = this._createSelectDialogTreeList(this._getSelectDialogSelectHandler());
        $selectDialog.append($treeList);
        $.each(this._ensureDataList,function(index,node){
            $("#li_"+node["idStr"]).find("."+ C.itemButton).addClass(C.itemSelected);
        });
    };

    /**
     * @private
     */
    MultiSelectTwoDialogTree.prototype._getEnsureDialog = function (nodeList, fnOnDelete) {
        var that = this,
            $group = $("<div class='" + C.ensureDialog + "'><ul></ul></div>");

        if (!nodeList || nodeList.length <= 0) {
            return $group;
        }
        var $li;
        $.each(nodeList,function(index,node){
            $li = $("<li><span></span><div class='" + C.deleteButton + "'></div><div style='clear:both;float:none;'></div></li>");
            $li.find("span").text(node["text"]);
            $li.attr("data-stored-val", encodeURIComponent(JSON.stringify(node)));
            $group.find("ul").append($li);

            $("#li_"+node["idStr"]).find("."+ C.itemButton).addClass(C.itemSelected);
        });

        $group.find(".delete-button").bind("click", function (event) {
            var $li = $(this).closest("li"),
                data = JSON.parse(decodeURIComponent($li.attr("data-stored-val")));

            $li.remove();

            for (var i = 0; i < that._ensureDataList.length; i++) {
                var obj = that._ensureDataList[i];

                if(data["idStr"] === obj["idStr"]) {
                    that._ensureDataList.splice(i, 1);
                    break;
                }
            }

            fnOnDelete(data, that._ensureDataList, event);
        });
        return $group;
    };

    MultiSelectTwoDialogTree.prototype._clearDataFromEnsureDialog = function() {
        var that = this;

        this._ensureDataList = [];
        this._$target.find("." + C.ensureDialog).find("ul li").remove();
    };

    MultiSelectTwoDialogTree.prototype.getAddedTreeNodeDataList = function() {
        return this._ensureDataList;
    };
    MultiSelectTwoDialogTree.prototype.getAddedLeafDataList = function() {
        var getAllChildren = function (arr) {
            var data = [];
            $.each(arr,function(index,val){
                if(!val["leaf"] && !G.isEmpty(val["children"])){//不是叶子节点
                    data = data.concat(getAllChildren(val["children"]));
                }else{
                    data.push(val);
                }
            });

            return data;
        };
        return getAllChildren(this._ensureDataList);
    };
    MultiSelectTwoDialogTree.prototype.clearAllSelectedData = function() {
        this._clearDataFromEnsureDialog();
    };
    MultiSelectTwoDialogTree.prototype.initSelectedData = function(dataList) {
        var that = this;
        if (!dataList || dataList.length <= 0) {
            return;
        }
        this._ensureDataList = dataList;
        var $content = this._$target.find("." + C.content);
        $content.find("." + C.ensureDialog).remove();
        $content.append(this._getEnsureDialog(this._ensureDataList, this._getEnsureDialogItemDeleteHandler()));
    };

    App.Module.MultiSelectTwoDialogTree = MultiSelectTwoDialogTree;
}());