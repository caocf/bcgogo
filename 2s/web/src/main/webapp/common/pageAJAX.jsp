    <%-- 不建议使用，可使用<bcgogo:ajaxPaging>代替 --%>
<%@ page language="java" contentType="text/html" pageEncoding="UTF-8" %>
<script type="text/javascript" src="js/extension/json2/json2.js"></script>
<script type="text/javascript">

$(function () {
    /**前台传过来AJAX分页的URL(不能用&携带参数的URL)*/
    var url = '<%=request.getParameter("url")%>';
    /**中途需要改变的url（商品引导页中可能会用到，需要开发）*/
    var urlChange = '<%=request.getParameter("urlChange")%>';
    /**处理AJAX请求成功后返回的json数据的方法名*/
    var functionName = '<%=request.getParameter("jsHandleJson")%>';
    /**传过来的字符串，每次调用AJAX分页的时候必须传不同的值，用来合成动态ID*/
    var dynamical = '<%=request.getParameter("dynamical")%>';
    /**代办事项中的页面一开始数据要只显示前五条，所以只在加载的时候调用hideIt方法*/
    var hide = '<%=request.getParameter("hide")%>';
    /**处理上下翻页复合框中打钩的情况*/
    var jstogetChecked = '<%=request.getParameter("jstogetChecked")%>';
    /**默认请求传入的参数*/
    var data = {startPageNo:1, maxRows:10};      //todo by qxy 如果修改maxrows 同时需要到stockSearchControll.waitcoming 方法中修改。


    /**中途需要改变的data(需要开发*/
    var dataChange = <%=request.getParameter("dataChagne")%>;

    var inputValue = "";
    /**吐过前台有data传过来就替换默认的data*/
    if (null != <%=request.getParameter("data")%> && 'null' != <%=request.getParameter("data")%>) {
        data = eval(<%=request.getParameter("data")%>);
    }

    $("#url" + dynamical).val(url);
    $("#functionName"+dynamical).val(functionName);

    //如果true  加载
    if("none"!="<%=request.getParameter("display")%>"){
        $("#data" + dynamical).val(JsonToStr(data));

        $.ajax({
            type:"POST",
            url:url,
            data:data,
            cache:false,
            dataType:"json",
            success:function (jsonStr) {
                if( !jsonStr ) return false;

                eval(functionName)(jsonStr);
                /**只是代办事项中会遇到*/
                if ('hideIt' == hide) {
                    eval(hide + "()");
                }
                initPages(jsonStr, dynamical, url, urlChange, functionName, hide, jstogetChecked, data, dataChange);
            },
            error:function(jsonStr){
                GLOBAL.error("获取分页数据出错。");
                GLOBAL.error(jsonStr);
            }
        });
    }

    $(".J_span_sort" + "_" + dynamical)
            .bind("mouseover",function () {
                $(this).find(".J_sort_div_info").show();
            })
            .bind("mouseout",function () {
                $(this).find(".J_sort_div_info").hide();
            })
            .bind("click", function () {
                $(".J_span_sort" + "_" + dynamical).each(function(){
                    $(this).removeClass("hover");
                });
                $(this).addClass("hover");
                var currentSortStatus = $(this).attr("currentSortStatus");
                $(this).find(".J_sort_span_image").removeClass("arrowDown").removeClass("arrowUp");
                if(currentSortStatus == "Desc"){
                    $(this).find(".J_sort_span_image").addClass("arrowUp");
                    $(this).attr("currentSortStatus","Asc");
                    $(this).find(".J_sort_div_info_val").html($(this).attr("descContact"));
                } else {
                    $(this).find(".J_sort_span_image").addClass("arrowDown");
                    $(this).attr("currentSortStatus", "Desc");
                    $(this).find(".J_sort_div_info_val").html($(this).attr("ascContact"));
                }
                var sortStr = $(this).attr("sortFiled") +  $(this).attr("currentSortStatus");
                data = strToJson($("#data" + dynamical).val());
                data.sortStatus = sortStr;

                var functionName = $("#functionName"+dynamical).val();
                pagesAjaxPost({
                    "dynamical":dynamical, "url":url,
                    "urlChange":urlChange, "functionName":functionName,
                    "hide":hide, "jstogetChecked":jstogetChecked,
                    "data":data, "dataChange": dataChange
                });
            });

    //老的排序
    $(".j_sort").die("click").die("click").live("click", function () {
        var sortStr = $(this).attr("sortField");
        if ($(this).hasClass("ascending")) {
            $(this).addClass("descending").removeClass("ascending");
            sortStr += " Desc";
        } else {
            $(this).addClass("ascending").removeClass("descending");
            sortStr += " Asc";
        }
        var dom = this;
        $(".j_sort").each(function () {
            if (dom.id != this.id) {
                $(this).addClass("ascending").removeClass("descending");
            }
        });

        data = strToJson($("#data" + dynamical).val());
        data.sortStatus = sortStr;

        var functionName = $("#functionName"+dynamical).val();
        pagesAjaxPost({
            "dynamical":dynamical, "url":url,
            "urlChange":urlChange, "functionName":functionName,
            "hide":hide, "jstogetChecked":jstogetChecked,
            "data":data, "dataChange": dataChange
        });

    });

    /**上一页按钮事件*/
    $("#prePage" + dynamical).die("click").live('click', function () {
        data = strToJson($("#data" + dynamical).val());
        var currentPage = $("#currentPage" + dynamical).val() * 1;
        /**当前页已经是第一页就返回*/
        if (currentPage == 1) {
            return;
        }

        data.startPageNo = currentPage - 1;
        if (null != urlChange && 'null' != urlChange) {
            url = urlChange;
        }
        if (null != dataChange && 'null' != dataChange) {
            data = dataChange;
            data.startPageNo = num;
        }
        var functionName = $("#functionName"+dynamical).val();
        pagesAjaxPost({
            "dynamical":dynamical, "url":url,
            "urlChange":urlChange, "functionName":functionName,
            "hide":hide, "jstogetChecked":jstogetChecked,
            "data":data, "dataChange": dataChange
        });
    });

    /**下一页按钮事件*/
    $("#nextPage" + dynamical).die("click").live('click', function () {
        data = strToJson($("#data" + dynamical).val());
        var currentPage = $("#currentPage" + dynamical).val() * 1;

        var totalPage = $("#totalPage" + dynamical).html() * 1;
        /**当前页已经是最后一页就返回*/
        if (currentPage == totalPage) {
            return;
        }

        data.startPageNo = currentPage + 1;
        if (null != urlChange && 'null' != urlChange) {
            url = urlChange;
        }
        if (null != dataChange && 'null' != dataChange) {
            data = dataChange;
            data.startPageNo = num;
        }
        var functionName = $("#functionName"+dynamical).val();
        pagesAjaxPost({
            "dynamical":dynamical, "url":url,
            "urlChange":urlChange, "functionName":functionName,
            "hide":hide, "jstogetChecked":jstogetChecked,
            "data":data, "dataChange": dataChange
        });
    });

    /**首页事件*/
    $("#firstPage" + dynamical).die("click").live('click', function () {
        data = strToJson($("#data" + dynamical).val());
        var currentPage = $("#currentPage" + dynamical).val() * 1;
        /**当前页已经是第一页就返回*/
        if (currentPage == 1) {
            return;
        }

        data.startPageNo = 1;
        if (null != urlChange && 'null' != urlChange) {
            url = urlChange;
        }
        if (null != dataChange && 'null' != dataChange) {
            data = dataChange;
            data.startPageNo = num;
        }
        var functionName = $("#functionName"+dynamical).val();
        pagesAjaxPost({
            "dynamical":dynamical, "url":url,
            "urlChange":urlChange, "functionName":functionName,
            "hide":hide, "jstogetChecked":jstogetChecked,
            "data":data, "dataChange": dataChange
        });
    });
    /**尾页事件*/
    $("#lastPage" + dynamical).die("click").live('click', function () {
        data = strToJson($("#data" + dynamical).val());
        var currentPage = $("#currentPage" + dynamical).val() * 1;
        var totalPage = $("#totalPage" + dynamical).html() * 1;
        /**当前页已经是最后一页就返回*/
        if (currentPage == totalPage) {
            return;
        }

        data.startPageNo = totalPage;
        if (null != urlChange && 'null' != urlChange) {
            url = urlChange;
        }
        if (null != dataChange && 'null' != dataChange) {
            data = dataChange;
            data.startPageNo = num;
        }
        var functionName = $("#functionName"+dynamical).val();
        pagesAjaxPost({
            "dynamical":dynamical, "url":url,
            "urlChange":urlChange, "functionName":functionName,
            "hide":hide, "jstogetChecked":jstogetChecked,
            "data":data, "dataChange": dataChange
        });
    });

    /**点击跳转按钮跳页事件*/
    $("#button" + dynamical).die("click").live('click', function () {
        data = strToJson($("#data" + dynamical).val());
        var currentPage = $("#currentPage" + dynamical).val() * 1;
        var totalPage = $("#totalPage" + dynamical).html() * 1;
        /**输入框中输入页码*/

        var num = $("#getPage" + dynamical).val() * 1;

        if (isNaN(num)) {
            $("#getPage" + dynamical).val(currentPage);
            return;
        }
        /**输入页码小于1把页码置1*/
        if (num < 1) {
            num = 1;
            $("#getPage" + dynamical).val(num);
        }
        /**输入页码大于总页码把页码置为总页码*/
        if (num > totalPage) {
            num = totalPage;
            $("#getPage" + dynamical).val(num);
        }
        /**输入页码等于当前页就返回*/
        if (num == currentPage) {
            return;
        }

        data.startPageNo = num;
        if (null != urlChange && 'null' != urlChange) {
            url = urlChange;
        }
        if (null != dataChange && 'null' != dataChange) {
            data = dataChange;
            data.startPageNo = num;
        }
        var functionName = $("#functionName"+dynamical).val();
        pagesAjaxPost({
            "dynamical":dynamical, "url":url,
            "urlChange":urlChange, "functionName":functionName,
            "hide":hide, "jstogetChecked":jstogetChecked,
            "data":data, "dataChange": dataChange
        });
    });

    document.getElementById("getPage" + dynamical).onkeyup = function (evt) {
        evt = evt || event;
        var k = window.event ? evt.keyCode : evt.which
        if (this.value == '0') {
            this.value = '';
            return;
        }

        if (k == 108 || k == 13) {
            return;
        } else if ((k >= 96 && k <= 105) || (k >= 48 && k <= 57)) {
            if (this.value.substring(0, 1) == '0') {
                this.value = inputValue;
                return;
            }
            inputValue = this.value;
        } else if (k == 8) {
            inputValue = this.value;
        } else {
            this.value = inputValue;
        }
    }
    /**输入框中输入页码直接回车事件，和跳转按钮事件一样*/
    $("#getPage" + dynamical).die("click").live('keypress', function (e) {
        e = e || event;
        var k = window.event ? e.keyCode : e.which
        if (k == 13 || k == 108) {
            data = strToJson($("#data" + dynamical).val());
            var currentPage = $("#currentPage" + dynamical).val() * 1;
            var totalPage = $("#totalPage" + dynamical).html() * 1;
            var num = $("#getPage" + dynamical).val() * 1;
            if (isNaN(num)) {
                $("#getPage" + dynamical).val(currentPage);
                return;
            }
            if (num < 1) {
                num = 1;
                $("#getPage" + dynamical).val(num);
            }

            if (num > totalPage) {
                num = totalPage;
                $("#getPage" + dynamical).val(num);
            }

            if (num == currentPage)
                return;

            data.startPageNo = num;
            if (null != urlChange && 'null' != urlChange) {
                url = urlChange;
            }
            if (null != dataChange && 'null' != dataChange) {
                data = dataChange;
                data.startPageNo = num;
            }
            var functionName = $("#functionName"+dynamical).val();
            pagesAjaxPost({
                "dynamical":dynamical, "url":url,
                "urlChange":urlChange, "functionName":functionName,
                "hide":hide, "jstogetChecked":jstogetChecked,
                "data":data, "dataChange": dataChange
            });
        }
    });

});
/**动态初始化组件*/
function initPages(jsonStr, dynamicalID, url, urlChange, functionName, hide, jstogetChecked, data, dataChange) {
    url = $("#url" + dynamicalID).val();
    /***/
    if(null == jsonStr ||"[]"==JSON.stringify(jsonStr) || (jsonStr[jsonStr.length-1].totalRows)*1 <1) {
        $("#i_pageBtn"+dynamicalID).hide();
        $("#data" + dynamicalID).val(JsonToStr(data));
        return;
    } else {
        $("#i_pageBtn" + dynamicalID).show();
    }

    $("#functionName"+dynamicalID).val(functionName);

    $("#totalRowS" + dynamicalID).html(jsonStr[jsonStr.length - 1].totalRows);
    $("#totalPage" + dynamicalID).html(jsonStr[jsonStr.length - 1].totalPage);
    $("#getPage" + dynamicalID + ","
            + "#currentPage" + dynamicalID).val( jsonStr[jsonStr.length - 1].currentPage );

    var totalPage = jsonStr[jsonStr.length - 1].totalPage * 1;
    var currentPage = jsonStr[jsonStr.length - 1].currentPage * 1;
    var prefixArr = [];

    if (currentPage == totalPage) {
        $("#nextPage" + dynamicalID + ","
                + "#lastPage" + dynamicalID).removeClass('wordButton');
    }else {
        $("#nextPage" + dynamicalID + ","
                + "#lastPage" + dynamicalID).addClass('wordButton');
    }
    if (currentPage == 1) {
        $("#prePage" + dynamicalID + ","
                + "#firstPage" + dynamicalID).removeClass('wordButton');
    }else {
        $("#prePage" + dynamicalID + ","
                + "#firstPage" + dynamicalID).addClass('wordButton');
    }

    var tr = ' ';
    $($("#firstshenglue" + dynamicalID).nextUntil("#lastshenglue" + dynamicalID)).remove();

    var dataString = JsonToStr(data);
    $("#data" + dynamicalID).val(dataString);
    for (var i = 1; i <= totalPage; i++) {
        tr = tr + '<a class="num numButton" id="menu_id' + dynamicalID + i + '" style="display:none" onclick = "goto(' + i + ',' + currentPage + ',\'' + dynamicalID + '\',\'' + url + '\',\'' + urlChange + '\',\'' + functionName + '\',\'' + hide + '\',\'' + jstogetChecked + '\',' + dataString + ',\'' + dataChange + '\')">' + i + '</a>';
    }

    $("#firstshenglue" + dynamicalID).after(tr);
    $('#menu_id' + dynamicalID + currentPage).addClass('numButton_selected');
    if (totalPage <= 5) {
        for (var i = 1; i <= totalPage; i++) {
            $("#menu_id" + dynamicalID + i).css("display", "block");
        }
    }else {
        if (currentPage - 2 <= 1) {
            for (var i = 1; i <= 5; i++) {
                $("#menu_id" + dynamicalID + i).css("display", "block");
            }
            $("#lastshenglue" + dynamicalID).css('display', 'block');
        }

        if ((currentPage + 2) >= totalPage) {
            for (var i = totalPage - 4; i <= totalPage; i++) {
                $("#menu_id" + dynamicalID + i).css("display", "block");
            }
            $("#firstshenglue" + dynamicalID).css('display', 'block');
        }

        if (currentPage > 3 && currentPage + 2 < totalPage) {
            //$(".i_pageBtn >a:gt("+(currentPage-3)+"):lt("+(currentPage+1)+")").css('display','block');
            for (var i = currentPage - 2; i <= currentPage + 2; i++) {
                $("#menu_id" + dynamicalID + i).css("display", "block");
            }

            $("#firstshenglue" + dynamicalID + ","
                    + "#lastshenglue" + dynamicalID).css('display', 'block');
        }
    }

    if ($('#menu_id' + dynamicalID + totalPage).css('display') == 'block')
        $("#lastshenglue" + dynamicalID).css('display', 'none')

    if ($('#menu_id' + dynamicalID + "1").css('display') == 'block')
        $("#firstshenglue" + dynamicalID).css('display', 'none')


}

// TODO 不准使用关键字！ 并且 goto 释意不明确
function goto(num, currentPage, dynamicalID, url, urlChange, functionName, hide, jstogetChecked, data1, dataChange) {
    url = $("#url" + dynamicalID).val();

    var data = eval(data1);
    if (num == currentPage) {
        return;
    }
    data.startPageNo = num;

    if (null != urlChange && 'null' != urlChange && '' != urlChange && undefined != urlChange && "undefined" != urlChange) {
        url = urlChange;
    }
    if (null != dataChange && 'null' != dataChange && '' != dataChange && undefined != dataChange && "undefined" != dataChange) {
        data = dataChange;
        data.startPageNo = num;
    }

    pagesAjaxPost({
        "dynamical":dynamicalID, "url":url,
        "urlChange":urlChange, "functionName":functionName,
        "hide":hide, "jstogetChecked":jstogetChecked,
        "data":data, "dataChange": dataChange
    });
}

function checkRate(input) {
    var ex = /^(\d+)$/;
    if (ex.test(input)) {
        return true;
    }
    return false;
}

// TODO not necessary function, delete it,  use the JSON2 only
function JsonToStr(obj) {
    var dataString = JSON.stringify(obj);
    String.prototype.replaceAll = function (s1, s2) {
        return this.replace(new RegExp(s1, "gm"), s2);
    }
    dataString = dataString.replaceAll("\"", "\'");
    return dataString;
}

// TODO not necessary function, delete it,  use the JSON2 only, evel() that I give the garentine is not safe
function strToJson(str) {
    return eval('(' + str + ')');
}


function pagesAjaxPost( params ) {
    $.ajax({
        type:"POST",
        url:params["url"],
        data:params["data"],
        cache:false,
        dataType:"json",
        success:function( jsonData ){
            if( !jsonData )
                return false;

            eval(params["functionName"])(jsonData);
            if("setCheckedByPreOrNextPage" == params["jstogetChecked"]) {
                eval(params["jstogetChecked"] + "()");
            }
            initPages( jsonData, params["dynamical"], params["url"], params["urlChange"]
                    , params["functionName"], params["hide"], params["jstogetChecked"], params["data"]
                    , params["dataChange"] );
        }
    });
}
</script>

<!-- TODO to use the json data format to structure the data, delete 3 lines follow  -->
<input type="hidden" id="currentPage<%=request.getParameter("dynamical")%>">
<input type="hidden" id="data<%=request.getParameter("dynamical")%>">
<input type="hidden" id="url<%=request.getParameter("dynamical")%>">
<input type="hidden" id="functionName<%=request.getParameter("dynamical")%>">
<div class="i_pageBtn" id="i_pageBtn<%=request.getParameter("dynamical")%>" style="display:none">
    <a class="first" id="firstPage<%=request.getParameter("dynamical")%>">首页</a>
    <a class="lastPage" id="prePage<%=request.getParameter("dynamical")%>">上一页</a>
    <span class="ellipsis" id="firstshenglue<%=request.getParameter("dynamical")%>">...</span>
    <span class="ellipsis" id="lastshenglue<%=request.getParameter("dynamical")%>">...</span>
    <a class="nextPage" id="nextPage<%=request.getParameter("dynamical")%>">下一页</a>
    <a class="last" id="lastPage<%=request.getParameter("dynamical")%>">尾页</a>

    <div class="pageNum">
        共 <span id="totalRowS<%=request.getParameter("dynamical")%>" class="recordCount">11</span> 条记录
        共<span class="pageCount" id="totalPage<%=request.getParameter("dynamical")%>">0</span>页
        <span style='display: <%="minPage".equals(request.getParameter("pageType"))?"none":""%>'>,到第
            <input type="text" class="selectPage" kissfocus="on" id="getPage<%=request.getParameter("dynamical")%>">
            页
            <input class="pageSure" type="button" onfocus="this.blur();" value="跳转" id="button<%=request.getParameter("dynamical")%>">
        </span>
    </div>
</div>
