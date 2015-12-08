/**
 * a category selector
 * @author zhen.pan
 * */
;
(function ($, document) {

/**define static class and template*/
    var C = {
        listCategory:       "list-category",
        groupSearch:        "group-search",
        searchParam:        "search-param",
        searchIcon:         "search-icon",
        listResult:         "list-result",
        resultInfo:         "result-info",
        iconNext:           "icon-next"
    };

    var T = {
        main: "<ul class='" + C.listCategory + "'></ul>",
        categoryItem: "" +
            "<li class='fl'> " +
            "    <dt></dt>" +
            "    <dd>" +
            "        <div class='" + C.groupSearch + "'>" +
            "            <div class='" + C.searchParam + "'>" +
            "                <input type='text'/>" +
            "            </div>" +
            "            <div class='" + C.searchIcon + "'></div>" +
            "        </div>" +
            "        <ul class='" + C.listResult + "'></ul>" +
            "    </dd>" +
            "</li>",
        resultItem: "" +
            "<li>" +
            "    <span class='" + C.resultInfo + "'></span>" +
            "    <span class='" + C.iconNext + "'>&gt;</span>" +
            "    <div class='cl'></div>" +
            "</li>"
    };




                         //Hi ，丽媛， 今天的那个man 不是那个群里面的吧？
/**define some common function */

    /**
     *
     * @param iData
     * @param prefix
     * @param suffixId
     * @returns {number}
     */
    var getMaxLevel = function(iData, prefix, suffixId) {
        var level = 0,
            hasNext = true;
        while( hasNext ) {
            hasNext = false;
            for (var i = 0; i < iData.length; i++) {
                var obj = iData[i];
                if(!G.isEmpty(obj[prefix + "_" + (level + 1) + "_" + suffixId])) {
                    hasNext = true;
                    level++;
                    break;
                }
            }
        }
        return level;
    };
    var getUniqueDataByProperty = function(iData,property) {
        var key     = undefined,
            value   = undefined;
        (function() {
            for (var k in property) {
                key     = k;
                value   = property[k];
            }
        })();
        for (var i = 0; i < iData.length; i++) {
            if(value === iData[i][key]) {
                return iData[i];
            }
        }
    };
    /**
     * 获取 键名 为 key 的所有键值，当然不能够重复
     * @param iData
     * @param key
     * @param emptyRuleArr
     * @returns {Array}
     */
    var getUniqueViewDataByKey = function(iData, key, emptyRuleArr ) {
        var retVal      = [],
            isMatched   = false;

        for (var i = 0; i < iData.length; i++) {
            isMatched = false;
            for (var k in retVal) {
                if(retVal[k]["id"] === iData[i][key]) {
                    isMatched = true;
                    break;
                }
            }
            if(!isMatched) {

                var fatchAllRequiredKey = true;
                if(emptyRuleArr) {
                    for (var itemIndex in emptyRuleArr) {
                        if(G.isEmpty(iData[i][emptyRuleArr[itemIndex]])) {
                            fatchAllRequiredKey = false;
                            break;
                        }
                    }
                } else {
                    fatchAllRequiredKey = !G.isEmpty(iData[i][key]);
                }

                if(fatchAllRequiredKey) {
                    var name = (function(key, value, iData, targetKeyName) {
                        var nameVal = "";
                        for (var i = 0; i < iData.length; i++) {
                            if(iData[i][key] === value) {
                                nameVal = iData[i][targetKeyName];
                                break;
                            }
                        }
                        return nameVal;
                    }) (key, iData[i][key], iData, key.replace("id", "name"));

                    retVal.push({
                        id:     iData[i][key],
                        name:   name
                    });
                }

            }
        }
        return retVal;
    };

    /**
     * @param iData
     * @param rule {
     *     key1:value1,
     *     key2:value2,
     *     key3:value3
     * }
     * @param emptyRuleArr [
     *     key1, key2, key3
     * ]
     * */
    var groupData = function(iData, rule, emptyRuleArr) {
        var retVal = [];

        for (var dataIndex = 0; dataIndex < iData.length; dataIndex++) {
            var dataItem = iData[dataIndex];

            var matched = true;
            for (var k in rule) {
                if( dataItem[k] !== rule[k] ) {
                    matched = false;
                    break;
                }
            }

            if (emptyRuleArr && matched) {
                for (var emptyRuleIndex = 0; emptyRuleIndex < emptyRuleArr.length; emptyRuleIndex++) {
                    var emptyRuleItem = emptyRuleArr[emptyRuleIndex];
                    if(G.isEmpty(dataItem[emptyRuleItem])) {
                        matched = false;
                        break;
                    }
                }
            }

            if(matched) {
                retVal.push(dataItem);
            }
        }

        return retVal;
    };

//    window.groupData = groupData;

    var getRenderData = function(data, prefix, suffix, keyProp , value) {
        var keyPropNumber = parseInt(keyProp),
            rule = {};

        rule[prefix + "_" + keyPropNumber + "_" + suffix] = value;

        return getUniqueViewDataByKey(groupData(data, rule), (prefix + "_" + (keyPropNumber + 1) + "_" + suffix) );
    };


    /**define ui fn tools*/

     var setCategoryVisible = function ($categoryArr, currentCategoryIndex) {
        var len = $categoryArr.length;

        if(currentCategoryIndex > len - 1) {
            return;
        }
        $categoryArr[currentCategoryIndex].show();
        for (var i = currentCategoryIndex + 1; i < len; i++) {
            $categoryArr[i].hide();
        }
        $categoryArr[currentCategoryIndex].find("." + C.listResult).children("li").each(function(index, value) {
            if($(value).hasClass("actived")) {
                $(value).show();
                return;
            }
        });
    };

    var activedResultListItem = function($node) {
        var $liArr  = $node.parent().find("li"),
            id      = $node.attr("data-id");

        $liArr.removeClass("actived");
        $node.addClass("actived");
    };




/**define component Class*/
    /**
     *
     * @constructor
     */
    var CategorySelector = function() {
        this._$hook             = undefined;
        this._$                 = undefined;
        this._option            = undefined;
        this._originalData      = undefined;
        this._data              = undefined;
        this._$categoryArr      = [];
        this._isHighlight       = undefined;
        this._highlightClassName       = undefined;
    };

    CategorySelector.method("init", function(option) {
        if(this._$)     return;

        if(!option)     return;

        /** init params */
        this._option = {};
        $.extend(this._option, $.categorySelector.defaultOption);
        $.extend(this._option, option);
        option = this._option;

        /** create view structure */
        this._$hook     = $(option.selector);
        this._$         = $(T.main);
        this._$.attr("data-cp-id", "category-selector-" + G.generateUUID());

        this._isHighlight = option.isHighlight;
        if(this._isHighlight)
            this._highlightClassName = option.highlightClassName || "yellow_color";

        this._originalData = option.data;

        this.update(this._originalData);
        this._$hook.append(this._$);

        this._$
            .css("width",   option.width )
            .css("height",  option.height);

        return this;   // Chain Programming
    });

    /**
     * @param data [
     *     {
     *         "level_1_id"     :"",
     *         "level_1_name"   :"",
     *         "level_2_id"     :"",
     *         "level_2_name"   :"",
     *         "level_3_id"     :"",
     *         "level_3_name"   :"",
     *         ......
     *     },
     *     {
     *         "level_1_id"     :"",
     *         "level_1_name"   :"",
     *         "level_2_id"     :"",
     *         "level_2_name"   :"",
     *         "level_3_id"     :"",
     *         "level_3_name"   :"",
     *         ......
     *     }
     * ]
     * */
    CategorySelector.method("update", function(data) {
        var that = this,
            cfg  = $.categorySelector.constOption,
            opt  = this._option;

        if(!data) {
            G.warning("null data!");
            return;
        }

        that._data = data;

        var maxLevel    = getMaxLevel(data, cfg.prefix, cfg.suffixId);

        // start dirty work .....

        // create categories
        (function() {
            for (var i = 0; i < opt.categoryTitleArr.length; i++) {
                var titleName       = opt.categoryTitleArr[i],
                    $categoryItem   = $(T.categoryItem);

                $categoryItem.find("dt").html(titleName);
                $categoryItem
                    .appendTo(that._$)
                    .css("width",   opt.iWidth)
                    .css("height",  opt.iHeight);

                $categoryItem.find("." + C.listResult)
                    .css("height", opt.iHeight - 71)
                    .css("position","relative");
                that._$categoryArr.push($categoryItem);
            }
        })();

        // set show/hide
        (function() {
            for (var i = 0; i < that._$categoryArr.length; i++) {
                if (i === 0) {
                    that._$categoryArr[i].show();
                } else {
                    that._$categoryArr[i].hide();
                }

                if(i === that._$categoryArr.length - 1) {
                    that._$categoryArr[i].addClass("last-child");
                }
            }
        })();

        // init data to UI
        var firstCategoryViewData = getUniqueViewDataByKey(data, cfg.prefix + "_1_" + cfg.suffixId, undefined);
        that.renderCategory(firstCategoryViewData, that._$categoryArr[0].find("." + C.listResult));





        // 处理 li 动作
        this._$categoryArr[0].find("li").bind("click", function(event) {
            that.commonClickHandler.call(that, event)
        });

        // 处理 搜索框 .searchParam > input[type="text"]
        // tools fn
        var keyupHandler = function(event) {
            // 过滤按键
            var eventKeyCode = event.which || event.keyCode;
            if (GLOBAL.Interactive.keyNameFromKeyCode(eventKeyCode).search(/left|up|right|down/g) >-1) {
                return this;
            }

            var $this                   = $(event.currentTarget),
                $category               = $this.parents("li"),
                categoryIndex           = $category.parent().children("li").index($category),
                $listResult             = $category.find("." + C.listResult),
                value                   = $this.val().replace(/[\ |\\]/g, ""),
                cfg                     = $.categorySelector.constOption;


            var parentIdArr = (function() {
                var returnArr = [];
                if(categoryIndex > 0) {
                    for (var i = 0; i < categoryIndex; i++) {
                        var itemKey = cfg.prefix + "_" + (i+1) + "_" + cfg.suffixId,
                            itemValue = undefined;

                        that._$categoryArr[i].find("." + C.listResult).children("li").each(function(index, value) {
                            if($(value).hasClass("actived")) {
                                itemValue = $(value).attr("data-id");
                                return true;
                            }
                        });

                        var item = {};
                        item[itemKey] = itemValue;
                        returnArr.push(item);
                    }
                }
                return returnArr;
            })();

            var rule = (function() {
                var retRule = {};

                for (var i = 0; i < parentIdArr.length; i++) {
                    var obj = parentIdArr[i];

                    for (var k in obj) {
                        retRule[k] = obj[k];
                    }
                }
                return retRule;
            })();

            var emptyRule = (function() {
                var emptyArr = [];

                for (var i = 0; i <= categoryIndex; i++) {
                    emptyArr.push(cfg.prefix + "_" + (i+1) + "_" + cfg.suffixId);
                }
                return emptyArr;
            })();

//            console.log(rule);
//            console.log(emptyRule);

            var viewData = groupData(that._originalData, rule, emptyRule)
            viewData = getUniqueViewDataByKey(viewData, cfg.prefix + "_" + (categoryIndex+1) + "_" + cfg.suffixId);

            that._option.onInput(event, categoryIndex, value, viewData, parentIdArr);
        };

        // catch event from the search field
        this._$.children("li").find("input[type='text']").bind("keyup", keyupHandler);

        this._$.append($("<div class='cl'></div>"));

        return this;   // Chain Programming
    });

    CategorySelector.method("commonClickHandler", function(event){
        var $this = $(event.currentTarget);
        this.defaultClickHandler($this, event);
    });

    CategorySelector.method("defaultClickHandler", function($node, event) {
        var that                    = this,
            opt                     = this._option,
            cfg                     = $.categorySelector.constOption,
            $category               = $node.parents("li"),
            categoryIndex           = $category.parent().children("li").index($category),
            nextCategoryIndex       = categoryIndex + 1,
            $nextCategoryListResult = $category.next().find("." + C.listResult),
            key                     = cfg.prefix + "_" + nextCategoryIndex + "_" + cfg.suffixId,
            value                   = $node.attr("data-id"),
            rule                    = {};

        rule[key] = value;

        var groupedData             = groupData(this._data, rule),
            renderData              = getRenderData(this._data, cfg.prefix, cfg.suffixId, nextCategoryIndex, value);

        activedResultListItem($node);

        that.updateCategory(nextCategoryIndex, renderData, $nextCategoryListResult, that.commonClickHandler);
        if(event && opt.onSelect) {
            opt.onSelect(event, groupedData, categoryIndex);
        }
    });


    /**
     * @param config [
     *     {
     *         key:value
     *     },
     *     {
     *         key:value
     *     },
     *     {
     *         key:value
     *     }
     * ]
     * */
    CategorySelector.method("reset", function(property,currentLevel) {
        var that    = this,
            cfg     = $.categorySelector.constOption,
            opt     = that._option;

        if(G.isEmpty(property)) {
            var renderData = getUniqueViewDataByKey(that._originalData, (cfg.prefix + "_" + 1 + "_" + cfg.suffixId) );
            that.updateCategory(0, renderData, that._$categoryArr[0].find("." + C.listResult), that.commonClickHandler);
        } else {
            var uniqueData = getUniqueDataByProperty(that._originalData,property);
            var config = [];

            (function() {
                for (var k in uniqueData) {
                    if(k.indexOf( "_"+cfg.suffixId)>-1 && !G.isEmpty(uniqueData[k])){
                        var level = parseInt(k.replace(cfg.prefix + "_","").replace("_"+cfg.suffixId,""));
                        if(level<=currentLevel){
                            var configItem = {};
                            configItem[k] = uniqueData[k];
                            config.push(configItem);
                        }
                    }
                }
            })();

            for (var i = 0; i < config.length; i++) {
                var configItem = config[i];
                var key     = undefined,
                    value   = undefined;
                (function() {
                    for (var k in configItem) {
                        key     = k;
                        value   = configItem[k];
                    }
                })();

                var renderData = undefined;
                if(i>0){
                    renderData = getUniqueViewDataByKey(groupData(that._originalData,config[i-1]), key);
                }else{
                    renderData = getUniqueViewDataByKey(that._originalData, key);
                }

                var activeIndex = undefined;
                for (var j = 0; j < renderData.length; j++) {
                    var renderDataItem = renderData[j];
                    if(renderDataItem.id === value) {
                        activeIndex = j;
                        break;
                    }
                }
                that.updateCategory(i, renderData,that._$categoryArr[i].find("." + C.listResult), that.commonClickHandler);

                var $activeNode = that._$categoryArr[i].find("." + C.listResult).children("li").eq(activeIndex);
                if($activeNode && $activeNode.length>0){
                    activedResultListItem($activeNode);
                    $activeNode.closest("." + C.listResult).animate({scrollTop: $activeNode.position().top});
                    if(i===config.length-1)
                        that.defaultClickHandler($activeNode);
                }
            }

        }

        return this;
    });


    /**
     * @param index
     * @param renderData
     * @param $listResult
     * @param clickHandler
     */
    CategorySelector.method("updateCategory", function(index, renderData, $listResult, clickHandler) {
        var that = this;
        if(this._$categoryArr.length > index) {
            this.renderCategory( renderData, $listResult);
            $listResult.find("li").bind("click", function(event) {
                clickHandler.call(that, event);
            });
        }
        setCategoryVisible(this._$categoryArr, index);
        return this;
    });

    /**
     * @param index
     * @param data level_id
     */
    CategorySelector.method("resetCategory", function(index, data,keyWord) {
        var that    = this

        that.updateCategory(
            index,
            data,
            that._$categoryArr[index].find("." + C.listResult),
            that.commonClickHandler
        );
        if(that._isHighlight && !G.isEmpty(keyWord)){
            that._$categoryArr[index].find("." + C.listResult).highlight(keyWord, {className:that._highlightClassName});
        }
    });


    /**
     * @param data
     * @param $node
     * @param activeIndex
     */
    CategorySelector.method("renderCategory", function(data, $node, activeIndex) {
        var $item   = undefined,
            id      = undefined,
            name    = undefined;

        $node.html("");
        if(data && data.length > 0){
            for (var i = 0; i < data.length; i++) {
                var o = data[i];

                if(G.isEmpty(o.id )) continue;

                id      = o.id;
                name    = o.name;

                // create new itemNode
                $item = $(T.resultItem);
                $item.attr("data-id", id);
                $item.html(name);

                $item.appendTo($node);
            }
            if(activeIndex !== undefined && G.isNumber(activeIndex) && $node.children("li").length >= (activeIndex + 1)) {
                activedResultListItem( $node.find("li").eq(activeIndex) );
            }
        }


        return this;  // Chain Programming
    });

    CategorySelector.method("remove", function() {
        this._$.remove();
        this._$             = undefined;
        this._$hook         = undefined;
        this._option        = undefined;
        this._data          = undefined;
        this._$categoryArr   = [];

        return this;  // Chain Programming
    });


/**define default options*/
    $.categorySelector = {};
    $.extend($.categorySelector, {
        constOption:{
            prefix:     "level",
            suffixId:   "id",
            suffixName: "name"
        },
        defaultOption:{
            width:          555,
            height:         300,
            iWidth:         180,
            iHeight:        300,
            isNotice:       false,
            data:           undefined,
            categoryTitleArr:[
                "一级分类列表", "二级分类列表", "三级分类列表"
            ],
            onSelect:function(event, data, index) {
                G.warning( event );
                G.warning( data  );
                G.warning( index );
            },

            /**
             *
             * @param event
             * @param index category 框的 index
             * @param value 搜索框的值
             * @param viewData
             */
            onInput:function(event, index, value, viewData, parentIdArr) {
                G.warning( event    );
                G.warning( index    );
                G.warning( value    );
                G.warning( viewData );
                G.warning( parentIdArr);
            }

        }
    });


/**define namespace alias*/
    App.namespace("Module.CategorySelector");
    App.Module.CategorySelector = CategorySelector;
})(jQuery, document);
