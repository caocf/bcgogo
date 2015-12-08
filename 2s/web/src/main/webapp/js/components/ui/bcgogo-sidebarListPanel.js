;(function() {
    App.namespace("Module.SidebarListPanel");

    var C = {
            "sidebarListPanel":"sidebar-list-panel",
            "listGroup":"list-group",
            "listItem":"list-item",
            "itemLabel":"item-label",
            "itemArrow":"item-arrow",
            "cl":"cl",
            "panelGroup":"panel-group",
            "groupItemTitle":"group-item-title",
            "groupItemContent":"group-item-content",
            "contentItem":"content-item",
            "active":"active",
            "itemLast":"item-last",
            "maskGroup":"mask-group"
        },
        T = {
        content:"" +
            "<div class='" + C.sidebarListPanel + "'>" +
//            "    <ul class='list-group'>" +
//            "        <li class='list-item'>" +
//            "            <div class='item-label'></div>" +
//            "            <div class='item-arrow'></div>" +
//            "            <div class='cl'></div>" +
//            "        </li>" +
//            "    </ul>" +
//            "    <dl class='panel-group'>" +
//            "        <dt class='group-item-title'></dt>" +
//            "        <dd class='group-item-content'>" +
//            "            <span class='content-item'></span> " +
//            "            <span class='content-item active'></span> " +
//            "            <span class='content-item item-last'></span> " +
//            "        </dd>" +
//            "        <div class='mask-group'></div>" +
//            "        <dt class='group-item-title'></dt>" +
//            "        <dd class='group-item-content'>" +
//            "            <span class='content-item'></span> " +
//            "            <span class='content-item active'></span> " +
//            "            <span class='content-item item-last'></span> " +
//            "        </dd>" +
//            "    </dl>" +
//            "    <div class='mask-group'></div>" +
            "</div>"
    };

    var fn = {
        dataEncode:function(o) {
            return encodeURIComponent(JSON.stringify(o));
        },
        dataDecode:function(o) {
            return JSON.parse(decodeURIComponent(o));
        }
    };

    var SidebarListPanel = function () {
        this._$ = undefined;
        this._data = undefined;
        this._param = undefined;
        this._$hook = undefined;
    };

    /**
     * @param {
     *     "selector":"xxxx",
     *     "data":json,
     *     "onSelect":function
     * }
     */
    SidebarListPanel.method("init", function(param) {
        this._$hook = $(param.selector);
        this._data = param.data;
        this._param = param;

        this._render();
    });

    SidebarListPanel.method("_render", function() {
        var that = this;

        that._$ = $(T.content);
        that._$hook.append(that._$);

        // that._data()
        var $listGroup = this._getListGroup();
        this._$.append($listGroup);

        this._$.bind("mouseleave", function(event) {
            that._$.find("." + C.panelGroup).remove();
            that._$.find("." + C.maskGroup).remove();
            $(this).find("." + C.listGroup + " ." + C.listItem).removeClass("hover");
        });

        var height = $listGroup.height();
        that._$.height(height);
    });

    SidebarListPanel.method("_getListGroup", function() {
        var that = this,
            $group = $("<ul class='" + C.listGroup + "'></ul>"),
            treeData = this._getTreeIdData(this._data);

        for (var k in treeData) {
            var listData = that._getListData(that._data, "firstCategoryIdStr", k);
            var liStr = "" +
                "<li class='" + C.listItem + "' data-stored-id='" + listData[0]["firstCategoryIdStr"] + "' data-stored-value='" + fn.dataEncode(listData) + "'>" +
                "    <div class='" + C.itemLabel + "'>" + listData[0]["firstCategoryName"] + "</div>" +
                "    <div class='" + C.itemArrow + "'>&gt;</div>" +
                "    <div class='" + C.cl + "'></div>" +
                "</li>",
                $li = $(liStr);
            $group.append($li);
        }

        $group
            .find("." + C.listItem)
            .bind("mouseenter", function(event) {
                that._$.find("." + C.panelGroup).remove();
                that._$.find("." + C.maskGroup).remove();

                var data = $(this).attr("data-stored-value"),
                    $panelGroup = that._getPanelGroup(fn.dataDecode(data)),
                    $maskGroup = that._getMaskGroup();

                that._$.append($panelGroup);
                that._$.append($maskGroup);
                var top = $(this).position().top;
                $maskGroup.css("top", top);
                if($panelGroup.height()>$(this).height()*2){
                    top = top-$panelGroup.height()/2;
                }

                $panelGroup.css("top", top<0?0:top);

                $(this).siblings("li").andSelf().removeClass("hover");
                $(this).addClass("hover");
            });

        return $group;
    });

    SidebarListPanel.method("_getMaskGroup", function() {
        return $("<div class='mask-group'></div>");
    });

    SidebarListPanel.method("_getPanelGroup", function(inData) {
        var that = this,
            $group = $("<dl class='" + C.panelGroup + "'>");

        // get tree from "secondCategoryIdStr" and "thirdCategoryIdStr".
        var _getTreeData = function(inData) {
            var retTree = {};
            for (var i = 0; i < inData.length; i++) {
                var o = inData[i], key2 = "secondCategoryIdStr", key3 = "thirdCategoryIdStr";
                retTree[o[key2]] = retTree[o[key2]] || {};
                if(!G.isEmpty(o[key3])){
                    retTree[o[key2]][o[key3]] = retTree[o[key2]][o[key3]] || {};
                }
            }
            return retTree;
        };

        var treeData = _getTreeData(inData);
        for (var key in treeData) {
            var item = treeData[key];

            var $dt = $("<dt class='" + C.groupItemTitle + "'></dt>");
            var $dd = $("<dd class='" + C.groupItemContent + "'></dd>");
            var $cl = $("<div class='" + C.cl + "'></div>");

            var data = that._getListData(inData, "secondCategoryIdStr", key);
            $dt.html(data[0]["secondCategoryName"]);
            $dt.attr("data-stored-value", fn.dataEncode(data));

            $group
                .append($dt)
                .append($dd)
                .append($cl);
            if(!G.isEmpty(item)){
                for (var keySub in item) {
                    var $contentItem = $("<span class='" + C.contentItem + "'></span>");
                    var dataSub = that._getListData(inData, "thirdCategoryIdStr", keySub);
                    $contentItem.html(dataSub[0]["thirdCategoryName"]);
                    $contentItem.attr("data-stored-value", fn.dataEncode(dataSub));
                    $dd.append($contentItem);
                }
            }else{
                $group.css("width","200px");
            }

//            $dd.find("." + C.contentItem).last().addClass(C.itemLast);
        }

        // bind events
        $group.find("." + C.groupItemTitle).bind("click", function(event) {
//            G.warning("clicked");

            var data = fn.dataDecode($(this).attr("data-stored-value"));

            if(!that._param.onSelect) return;

            that._param.onSelect(data, event);
        });

        $group.find("." + C.groupItemContent + " ." + C.contentItem).bind("click", function(event) {
//            G.warning("clicked");

            var data = fn.dataDecode($(this).attr("data-stored-value"));

            if(!that._param.onSelect) return;

            that._param.onSelect(data, event);
        });

        return $group;
    });

    SidebarListPanel.method("_getListData", function(inData, keyName, keyValue) {
        var list = [];
        for (var i = 0,  len = inData.length; i < len; i++) {
            var item = inData[i];
            if(item[keyName] && (item[keyName] === keyValue)) list.push(item);
        }
        return list;
    });

    SidebarListPanel.method("getData", function(keyName, keyValue, fatherData) {
        return this._getListData((fatherData || this._data), keyName, keyValue);
    });

    SidebarListPanel.method("_getTreeIdData", function(inData) {
        var tree = {};
        for (var i = 0; i < inData.length; i++) {
            var item = inData[i], key1 = "firstCategoryIdStr", key2 = "secondCategoryIdStr", key3 = "thirdCategoryIdStr";
            tree[item[key1]] = tree[item[key1]] || {};
            tree[item[key1]][item[key2]] = tree[item[key1]][item[key2]] || {};
            tree[item[key1]][item[key2]][item[key3]] = tree[item[key1]][item[key2]][item[key3]] || {};
        }
        return tree;
    });

    App.Module.SidebarListPanel = SidebarListPanel;
} ());