// Menu Data tools Class, include traversal tree methods
;
(function () {
    App.namespace("Module.MenuDataProxy");

    var MenuDataProxy = function () {
    };

    /**
     * @param nodeToBeClone
     * @param itemChild
     * @param isRoot
     * @returns {{}}
     * @private
     */
    MenuDataProxy.prototype._generateRoadNode = function (nodeToBeClone, itemChild, isRoot) {
        var node = {};

        if (isRoot) {
            node.root = nodeToBeClone.root;
        } else {
            node.label = nodeToBeClone.label;
        }

        node.href = nodeToBeClone.href;
        node.uid = nodeToBeClone.uid;

        if (itemChild) {
            node.item = [];
            node.item.push(itemChild);
        }

        return node;
    };


    /**
     * 查找 uid 所指定的节点在 itemList 中的位置，在这里要注意， uid 所指定的对象， 与 itemList 中的其他元素是同级元素， 即他们的关系是 siblings
     * @param itemList
     * @param uid
     * @returns {number}
     */
    MenuDataProxy.prototype.getItemIndex = function (itemList, uid) {
        var index = -1;
        // 查看是否存在 uid 相等的节点，并返回 index
        for (var i = 0, len = itemList.length; i < len; i++) {
            var obj = itemList[i];
            if (obj.uid === uid) {
                index = G.Array.indexOf(itemList, obj);
            }
        }

        return index;
    };

    /**
     * 此方法的用法为， 首先 data 是你完整的数据; uid 则是你需要查找的节点所指定的 唯一标示值; 而 isRoot ，则是标示你的最高节点 是不是root;   TODO 最后这点的确需要改进， 接口其实是可以省略这点的， 不过现在请先这样使用 ，我会尽快进行更改
     * @param data
     * @param uid
     * @param isRoot
     * @returns {*}
     */
    MenuDataProxy.prototype.getRoad = function (data, uid, isRoot) {
        if (!data) {
            return null;
        }

        if (isRoot) {
            if (data.uid === uid) {
                return this._generateRoadNode(data, null, true);
            } else if (false === data.hasOwnProperty("item")) {
                return null;
            } else {
                var itemChild = this.getRoad(data.item, uid, false);

                return itemChild ?
                    this._generateRoadNode(data, itemChild, true) : null;
            }
        } else {
            var itemList = data,
                catchIndex = -1;

            // 查看是否存在与 uid 相等的节点, 并存储 index
            catchIndex = this.getItemIndex(itemList, uid);

            if (catchIndex < 0) {
                for (var i = 0, len = itemList.length; i < len; i++) {
                    if (false === itemList[i].hasOwnProperty("item")) {
                        continue;
                    }

                    var itemChild = this.getRoad(itemList[i].item, uid, false);
                    if (itemChild) {
                        return this._generateRoadNode(itemList[i], itemChild, false);
                    } else {
                        continue;
                    }
                }

                if (i === len) {
                    return null;
                }
            } else {
                return this._generateRoadNode(itemList[catchIndex], null, false);
            }
        }
    };

    /**
     * 此方法的用法为， 首先 data 是你完整的数据; uid 则是你需要查找的节点所指定的 唯一标示值; 而 isRoot ，则是标示你的最高节点 是不是root;   TODO 最后这点的确需要改进， 接口其实是可以省略这点的， 不过现在请先这样使用 ，我会尽快进行更改
     * @param data
     * @param uid
     * @param isRoot
     * @returns {*}
     */
    MenuDataProxy.prototype.getSibling = function (data, uid, isRoot) {
        if (!data) {
            return null;
        }

        if (isRoot) {
            if (data.uid === uid) {
                return null;
            } else if (false === data.hasOwnProperty("item")) {
                return null;
            } else {
                return this.getSibling(data.item, uid, false);
            }
        } else {
            var itemList = data,
                catchIndex = -1,
                newSiblingList = [];

            // 查看是否存在 uid 相等的节点，并存储节点 index
            catchIndex = this.getItemIndex(itemList, uid);

            // 复制除 catchIndex 所指节点外的引用 至新数组
            newSiblingList = G.Array.remove(itemList, catchIndex);

            // 若 uid 所标记元素不在 此深度， 则继续往下遍历
            if (newSiblingList.length === 0) {
                for (var i = 0, len = itemList.length; i < len; i++) {
                    if (false === itemList[i].hasOwnProperty("item")) {
                        continue;
                    }

                    // 如果 此 节点树下的 子节点都没有 命中 uid 所标记对象
                    // 那么继续循环, 搜索统计菜单
                    var result = this.getSibling(itemList[i].item, uid, false);
                    if (result) {
                        return result;
                    } else {
                        continue;
                    }
                }

                // 如果没有遍历到结果， 直接返回 null
                if (i === len) {
                    return null;
                }
            } else {
                return newSiblingList;
            }

        } // end if(isRoot)
    };// end getSibling(data, uid, isRoot) definition

    App.Module.MenuDataProxy = MenuDataProxy;
})();


/**
 * menu bar 组件
 */
;
(function () {
    App.namespace("Module.MenuBar");

    var menuDataProxy = new App.Module.MenuDataProxy();

    var MenuBar = function () {
        this._$;
        this._param;
    };

    MenuBar.prototype.T = {
        content: '' +
            '<div class="bcgogo-menubar">' +
//            '    <ul class="Jmenu">' +
//            '        <li class="Jcontent">首页</li>' +
//            '        <li class="Jarrow">&gt;</li>' +
//            '        <li class="Jcontent">第二级菜单</li>' +
//            '        <li class="Jarrow">&gt;</li>' +
//            '        <li class="Jcontent">第三级菜单</li>' +
//            '    </ul>' +
//            '    <div style="clear:both;float:none;"></div>' +
//            '    <ul class="Jsibling">' +
//            '        <li>兄弟节点1</li>' +
//            '        <li>兄弟节点2</li>' +
//            '        <li>兄弟节点3</li>' +
//            '        <li>兄弟节点4</li>' +
//            '        <li>兄弟节点5</li>' +
//            '    </ul>' +
//            '    <div style="clear:both;float:none;"></div>' +
            '</div>',
        clear: '<div class="Jclear" style="clear:both;float:none;"></div>'

    };

    /**
     * @param param data
     * @private
     */
    MenuBar.prototype._get$menu = function (param) {
        var $menu = $("<ul class='Jmenu'></ul>");

        if(param.home){
            //首页
            var home = param.home;
            var T = {
                content: "<li class='Jcontent'></li>",
                arrow: "<li class='Jarrow'>&gt;</li>"
            };
            var $content = $(T.content);
            $content.text(home.label)
                .attr("href", home.href)
                .bind("click", function (event) {
                    window.location.href = $(this).attr("href");
                });
            $menu.append($content);
            var $arrow = $(T.arrow);
            $menu.append($arrow);
        }
        if(param.road){
            //增加子菜单
            var items = param.road;
            var itemArray = this._get$itemByNode(items);

            for (var i = 0, len = itemArray.length; i < len; i++) {
                $menu.append(itemArray[i]);
            }
        }
        if(param.lastItem){
            //增加最后一级菜单
            var lastItem = param.lastItem;
            var T = {
                content: "<li class='JDcontent'></li>",
                arrow: "<li class='Jarrow'>&gt;</li>"
            };
            var $arrow = $(T.arrow);
            $menu.append($arrow);

            var $content = $(T.content);
            $content.html(lastItem.label);
            $menu.append($content);
        }
        return $menu;
    };

    MenuBar.prototype._onDefaultSelect = function (This, event) {
        if (This._param.config.autoTurnning === false) {
            event.preventDefault();
            event.returnValue = false;
        }

        if (This._param.config.onSelect) {
            var road = menuDataProxy.getRoad(This._param.data, $(event.currentTarget).data("uid"), true);
            This._param.config.onSelect(road, This._param.data, $(event.currentTarget).data("uid"), event);
        }
    };

    MenuBar.prototype._get$itemByNode = function (param) {
        var This = this,
            itemArray = [];

        var T = {
            content: "<li class='Jcontent'></li>",
            arrow: "<li class='Jarrow'>&gt;</li>"
        };

        var $content = $(T.content)
        $content
            .text(param.root)
            .attr("href", param.href)
            .bind("click", function (event) {
                This._onDefaultSelect(This, event);
            });
        $content.data("uid", param.uid);

        itemArray.push($content);


        if (!param.item) {
            return itemArray;
        }
        var $arrow = $(T.arrow);
        itemArray.push($arrow);

        var param_0 = param.item[0]
        $content = $(T.content);
        $content
            .text(param_0.label)
            .attr("href", param_0.href)
            .bind("click", function (event) {
                This._onDefaultSelect(This, event);
            });
        $content.data("uid", param_0.uid);

        itemArray.push($content);


        if (!param.item[0].item) {
            return itemArray;
        }
        $arrow = $(T.arrow);
        itemArray.push($arrow);

        var param_0_0 = param.item[0].item[0];
        $content = $(T.content);
        $content
            .text(param_0_0.label)
            .attr("href", param_0_0.href)
            .bind("click", function (event) {
                This._onDefaultSelect(This, event);
            });
        $content.data("uid", param_0_0.uid);

        itemArray.push($content);

        return itemArray;
    };


    /**
     * @returns {*|jQuery|HTMLElement}
     * @private
     */
    MenuBar.prototype._get$clear = function () {
        return $(this.T.clear);
    };

    /**
     *
     * @param param [
     *     {
     *         label:"",
     *         href:""
     *     },
     *     {
     *         label:"",
     *         href:""
     *     }
     * ]
     * @private
     */
    MenuBar.prototype._get$sibling = function (param) {
        var This = this,
            $sibling = $("<ul class='Jsibling'></ul>");

        for (var i = 0; i < param.length; i++) {
            var $li = $("<li>"),
                data = param[i];

            $li
                .text(data.label)
                .attr("href", data.href)
                .bind("click", function (event) {
                    This._onDefaultSelect(This, event);
                });

            $li.data("uid", data.uid);

            $sibling.append($li);
        }

        return $sibling;
    };

    MenuBar.prototype._getSiblingData = function (param) {
        var data = param.data,
            uid = param.uid,
            isRoot = true;

        return menuDataProxy.getSibling(data, uid, isRoot);
    };


    /**
     *
     * @param param {
     *     // 只包含 路径的树形结构
     *     road:{
     *        root:"xxx",
     *        href:"",
     *        item:{
     *            label:"",
     *            href:"",
     *            item:{
     *                label:"",
     *                href:""
     *            }
     *        }
     *     },
     *
     *     // 完整的 树形数据结构
     *     data:{
     *        root:"xxx",
     *        href:"",
     *        item:{
     *            label:"",
     *            href:"",
     *            item:{
     *                label:"",
     *                href:""
     *            }
     *        }
     *     },
     *     config:{
     *         selector:"xxx",
     *         autoTurnning:true,
     *         onSelect:function(road, data, uid, event)
     *     }
     * }
     */
    MenuBar.prototype.show = function (param) {
        var This = this;
        var _$content = $(This.T.content),
            _$ = $("<div id='id-bcgogo-menupanel_" + G.generateUUID() + "'></div>");

        _$.append(_$content);
        This._$ = _$;
        This._param = param;

        if (!param || !param.data || !param.road) {
            return;
        }

        _$content.append(This._get$menu(param));
        _$content.append(This._get$clear());

        // bind event to item
        $(".Jcontent", _$)
            .bind("mouseenter", function (event) {
                var $sibling = $(".Jsibling", _$),
                    siblingData = menuDataProxy.getSibling(This._param.data, $(this).data("uid"), true);

                if ($sibling[0]) {
                    return;
                }

                if (!siblingData) {
                    return;
                }

                $(this).addClass("Jhover");

                $sibling = This._get$sibling(siblingData);

                _$content.append($sibling);
                _$content.append(This._get$clear());

                //hack
                var left = $(this).position().left;

                $sibling.css("left", left);

                if ($sibling.width() < $(this).width()) {
                    $sibling.width($(this).width());
                }

                $sibling.bind("mouseleave", function (event) {
                    if (($(this).offset().top) >= event.pageY) {
                        return;
                    }

                    _$.find(".Jcontent").removeClass("Jhover");
                    $(this).remove();
                    _$.find(".Jclear").eq(0).remove();
                });
            })
            .bind("mouseleave", function (event) {
                if (false === $(this).hasClass("Jhover")) {
                    return;
                }

                if (($(this).offset().top + $(this).height()) <= event.pageY) {
                    return;
                }

                $(this).removeClass("Jhover");
                _$.find(".Jsibling").remove();
                _$.find(".Jclear").eq(0).remove();
            });

        _$.appendTo(param.config.selector || "body");
    };

    /**
     *
     * @param param
     */
    MenuBar.prototype.hide = function (param) {
        this._$.remove();
    };

    App.Module.MenuBar = MenuBar;
})();


/**
 * menupanel , 供页面 header 使用的 menu 级联 panel
 */
;
(function () {
    App.namespace("Module.MenuPanel");

    var menuDataProxy = new App.Module.MenuDataProxy();

    var MenuPanel = function () {
        this._$ = null;
        this._$root = null;
        this._param = null;
        this._data = null;
    };

    MenuPanel.prototype.T = {
        mousehit: "" +
            "<div class='bcgogo_menupanel_mousehit'></div>",
        content: "" +
            "<div class='bcgogo_menupanel'>" +
            "    <div class='Jarrow_content'><div class='Jarrow'></div></div>" +
            "    <div class='Jpanel'>" +
            //       add rows
            //           add column
            //               add title
            //               add item
            //           add clear
            "    </div>" +
            "</div>",
        row: "" +
            "<div class='Jrow'>" +
            // add column
            "</div>",
        column: "" +
            "<div class='Jcolumn'>" +
            // add title,  add item
            "</div>",
        title: "" +
            "<a class='Jtitle'></a>",
        item: "" +
            "<a class='Jitem'></a>",
        clear: "" +
            "<div class='Jclear'></div>"
    };

    /**
     *
     * @param param {
     *     road:{},
     *     data:{
     *         root:"xxx",
     *         href:"xxx",
     *         item:[
     *             {
     *                 label:"",
     *                 href:"",
     *                 item:[
     *                     {label:"", href:""},
     *                     {label:"", href:""},
     *                     {label:"", href:""},
     *                     {label:"", href:""}
     *                 ]
     *             },
     *             {
     *                 label:"",
     *                 href:"",
     *                 item:[
     *                     {label:"", href:""}
     *                 ]
     *             }
     *         ]
     *     },
     *     config:{
     *         autoTurnning:true,
     *         column:3,
     *         align:"right",
     *         hookSelector:xxxx,
     *         manualPosition:false,
     *         onSelect:function(road, data, uid, event){}
     *     },
     *     selector:xxx
     * }
     */
    MenuPanel.prototype.show = function (param) {
        var _ = this;
        _._param = param;
        _._$root = $(param.selector ? param.selector : "body");

        _._$ = $(_.T.mousehit);
        var _$ = _._$;
        _._$.attr("id", "id_bcgogo_menupanel_" + G.generateUUID());

        var $content = $(_.T.content);
        $content.appendTo(_$);

        var data = param.data;
        _._data = data;

        var viewData = _._parseData(_._data, param.config.column || 3);

        for (var i = 0; i < viewData.length; i++) {
            _$.find(".Jpanel").append(_._get$row(viewData[i]));
        }

        _$.appendTo(_._$root);


        var columnGroup = [],
            $columnList = _$.find(".Jcolumn");

        // get parse columnGroup
        for (var columnIndex = 0; columnIndex < param.config.column; columnIndex++) {

            for (var itemIndex = columnIndex, rowIndex = 0;
                 itemIndex < _._data["item"].length;
                 rowIndex++, itemIndex = columnIndex + (param.config.column * rowIndex)) {

                if (!columnGroup[columnIndex]) {
                    columnGroup[columnIndex] = [];
                }

                columnGroup[columnIndex][rowIndex] = $columnList.eq(itemIndex);
            }
        }


        // hack firefox render bug,  trigger render engine manually
        _$.css("position", "relative").css("position", "absolute");
        // calculate width
        for (var groupIndex = 0; groupIndex < columnGroup.length; groupIndex++) {
            var maxWidth = 0;
            $.each(columnGroup[groupIndex], function (index, $node) {
                maxWidth = Math.max(maxWidth, $node.width());
            });

            // set max width to same column
            $.each(columnGroup[groupIndex], function (index, $node) {
                $node.width(maxWidth);
            });
        }

        // arrow position
        var align = param.config.align || "left",
            $arrow = _$.find(".Jarrow"),
            $menupanel = _$.find(".bcgogo_menupanel"),
            $hook = $(param.config.hookSelector || "body"),
            hookInfo = $hook.offset();

        $arrow.addClass("Jarrow_" + align);
        if (align === "left") {
//            var left = 0.5 * (columnGroup[0][0].outerWidth() - $arrow.width());
            var left = 0.5 * $hook.width() - parseInt($menupanel.css("padding-left")) - $arrow.width() * 0.5;
            $arrow.css("margin-left", left);
        } else {
//            var right = 0.5 * (columnGroup[columnGroup.length - 1][0].outerWidth() - $arrow.width());
            var right = 0.5 * $hook.width() - parseInt($menupanel.css("padding-right")) - $arrow.width() * 0.5;
            $arrow.css("margin-right", right);
        }


        // content position
        if (param.config.manualPosition) {
            return;
        }

        var selfOffset = {};

        hookInfo["left"] = G.getX($hook[0]);
        hookInfo["top"] = G.getY($hook[0]);
        hookInfo["width"] = $hook.outerWidth();
        hookInfo["height"] = $hook.outerHeight();

        _$
            .css("position", "absolute")
            .css("float", "left")
            .css("z-index", param.config["z-index"] || 20);

        selfOffset.top = hookInfo.top + hookInfo.height;
        if (align === "left") {
            selfOffset.left = hookInfo.left - parseInt(_$.find(".bcgogo_menupanel").css("margin-left"));
        } else {
            selfOffset.left = hookInfo.left + hookInfo.width - _$.outerWidth();
        }

        _$.css(selfOffset);

        // TOOD use mouseleave to check is leave
        _$.bind("mouseleave", function (event) {
            var px = (event.offsetX || event.layerX),
                pY = (event.offsetY || event.layerY);

            if (pY < 0) {
                return;
            }
            _.remove();
        });

        //
        if (_$.outerWidth() < hookInfo.width) {
            _$.outerWidth(hookInfo.width);
        }


        // $root mouseleave triggered
        var _rootMouseleaveHandler = function (event) {
            var p = {
                x: (event.pageY || event.layerX),
                y: (event.pageY || event.layerY)
            };

            var $target = $(event.currentTarget);

            var offsetTarget = $target.offset(),
                pTarget = {
                    w: $target.width(),
                    h: $target.height(),
                    x: offsetTarget.left,
                    y: offsetTarget.top
                };

            if(p.y >= (pTarget.y + pTarget.h) ) return;

            _.remove();
        };
        $(_._param.config.hookSelector).bind("mouseleave", _rootMouseleaveHandler);
        _._rootMouseleaveHandler = _rootMouseleaveHandler;
    };

    /**
     * 清除 menupanel 所对应的 jqDom 视图对象
     */
    MenuPanel.prototype.remove = function () {
        if (this._$) {
            this._$.unbind("mouseleave");
            this._$.remove();
            $(this._param.config.hookSelector).unbind("mouseleave", this._rootMouseleaveHandler);
        }
        this._$ = null;
        this._data = null;
        this._$root = null;
        this._param = null;
    };

    /**
     * 返回 menupanel 所对应的视图 jqDom 对象
     * @returns {*|jQuery|HTMLElement}
     */
    MenuPanel.prototype.getJqDom = function () {
        return $(this._$);
    };

    /**
     *
     * @param inData
     * @param column
     * @return {Array} [
     *     [
     *         {
     *             title:{label:"", href:""},
     *             item:[
     *                 {label:"", href:""},
     *                 {label:"", href:""}
     *             ]
     *         },
     *         {
     *             title:{label:"", href:""},
     *             item:[
     *                 {label:"", href:""},
     *                 {label:"", href:""}
     *             ]
     *         },
     *         {...}
     *     ],
     *     [...]
     * ]
     * @private
     */
    MenuPanel.prototype._parseData = function (inData, column) {
        var retData = [],
            itemData = inData["item"];

        for (var i = 0, len = itemData.length; i < len; i++) {
            var index = Math.floor(i / column),
                subIndex = i % column;
            retData[index] = retData[index] || [];
            retData[index][subIndex] = itemData[i];
        }
        return retData;
    };

    /**
     * @param inData {
     *     "label":"xxxx",
     *     "href":"xxxx"
     * }
     * @return {*|jQuery|HTMLElement}
     */
    MenuPanel.prototype._get$item = function (inData) {
        var This = this;
        var $item = $(this.T.item),
            hrefValue = G.normalize(inData["href"]);
        if (hrefValue === "") {
            $item.css("cursor", "default");
        } else {
            $item.css("cursor", "pointer").addClass("Javailable");
        }

        $item
            .html(inData["label"])
            .attr("href", hrefValue)
            .bind("click", function (event) {
                if (This._param.config.autoTurnning === false) {
                    event.preventDefault();
                    event.returnValue = false;
                }

                if (This._param.config.onSelect) {
                    var road = menuDataProxy.getRoad(This._param.data, $(event.currentTarget).data("uid"), true);
                    This._param.config.onSelect(road, This._data, $(event.currentTarget).data("uid"), event);
                }
            })
            .data("uid", inData["uid"]);
        return $item;
    };

    /**
     * 生成并返回 一个 title jqDom 节点对象
     * @param inData
     * @returns {*|jQuery|HTMLElement}
     * @private
     */
    MenuPanel.prototype._get$title = function (inData) {
        var This = this;
        var $title = $(this.T.title),
            hrefValue = G.normalize(inData["href"]);
        if (hrefValue === "") {
            $title.css("cursor", "default")
        } else {
            $title.css("cursor", "pointer").addClass("Javailable");
        }

        $title
            .html(inData["label"])
            .attr("href", hrefValue)
            .bind("click", function (event) {
                if (This._param.config.autoTurnning === false) {
                    event.preventDefault();
                    event.returnValue = false;
                }

                if (This._param.config.onSelect) {
                    var road = menuDataProxy.getRoad(This._param.data, $(this).data("uid"), true);
                    This._param.config.onSelect(road, This._param.data, $(this).data("uid"), event);
                }
            })
            .data("uid", inData["uid"]);
        return $title;
    };

    /**
     * 生成并返回 列jqDom对象 ， 列对象(column)是行对象(row)的子元素
     * @param inData
     * @returns {*|jQuery|HTMLElement}
     * @private
     */
    MenuPanel.prototype._get$column = function (inData) {
        var $column = $(this.T.column);
        $column.append(this._get$title(inData));
        if (inData["item"]) {
            for (var i = 0; i < inData["item"].length; i++) {
                $column.append(this._get$item(inData["item"][i]));
            }
        }
        return $column;
    };

    /**
     * 生成并返回 行jqDom对象
     * @param inData
     * @returns {*|jQuery|HTMLElement}
     * @private
     */
    MenuPanel.prototype._get$row = function (inData) {
        var $row = $(this.T.row);
        for (var i = 0; i < inData.length; i++) {
            $row.append(this._get$column(inData[i]));
        }
        $row.find(".Jcolumn").last().addClass("Jlast");
        $row.append(this._get$clear());
        return $row;
    };

    /**
     * 返回浮动清除 jqDom 对象
     * @returns {*|jQuery|HTMLElement}
     * @private
     */
    MenuPanel.prototype._get$clear = function () {
        return $(this.T.clear);
    };


    App.Module.MenuPanel = MenuPanel;
})();