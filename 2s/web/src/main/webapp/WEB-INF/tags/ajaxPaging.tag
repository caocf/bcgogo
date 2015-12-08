<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="url" required="true" type="java.lang.String" %>
<%@ attribute name="dynamical" required="true" type="java.lang.String" %>
<%@ attribute name="postFn" type="java.lang.String" %>
<%@ attribute name="urlChange" type="java.lang.String" %>
<%@ attribute name="hide" type="java.lang.String" %>
<%@ attribute name="jsToGetChecked" type="java.lang.String" %>
<%@ attribute name="dataChange" type="java.lang.String" %>
<%@ attribute name="data" type="java.lang.String" %>
<%@ attribute name="display" type="java.lang.String" %>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/extension/json2/json2.js"></script>
<script type="text/javascript">
$(function () {
    /**前台传过来AJAX分页的URL(不能用&携带参数的URL)*/
    var url = '${pageScope.url}';
    /**中途需要改变的url（商品引导页中可能会用到，需要开发）*/
    var urlChange = '${pageScope.urlChange}';
    /**处理AJAX请求成功后返回的json数据的方法名*/
    var functionName = '${pageScope.postFn}';
    /**传过来的字符串，每次调用AJAX分页的时候必须传不同的值，用来合成动态ID*/
    var dynamical = '${pageScope.dynamical}';
    /**代办事项中的页面一开始数据要只显示前五条，所以只在加载的时候调用hideIt方法*/
    var hide = '${pageScope.hide}';
    /**处理上下翻页复合框中打钩的情况*/
    var jsToGetChecked = '${pageScope.jsToGetChecked}';
    /**默认请求传入的参数*/
    var data = {startPageNo:1, maxRows:10};      //todo by qxy 如果修改maxrows 同时需要到stockSearchControll.waitcoming 方法中修改。

    /**中途需要改变的data(需要开发*/
    var dataChange = '${pageScope.dataChange}';

    var inputValue = "";
    /**吐过前台有data传过来就替换默认的data*/
    <c:if test="${pageScope.data != null}">
        data = ${pageScope.data};
    </c:if>

    $("#url" + dynamical).val(url);

    $("#functionName"+dynamical).val(functionName);

    //如果true  加载
    if("none"!="${pageScope.display}"){
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
                initPage(jsonStr, dynamical, url, urlChange, functionName, hide, jsToGetChecked, data, dataChange);
            },
            error:function(jsonStr){
                GLOBAL.error("获取分页数据出错。");
                GLOBAL.error(jsonStr);
            }
        });
    }

    $(".j_sort").die("click").live("click", function () {
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
        pagingAjaxPost({
            "dynamical":dynamical, "url":url,
            "urlChange":urlChange, "functionName":functionName,
            "hide":hide, "jstogetChecked":jsToGetChecked,
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


        urlChange = $("#urlChange" + dynamical).val();
        if (!GLOBAL.Lang.isEmpty(urlChange)) {
            url = urlChange;
        }
        dataChange = strToJson($("#dataChange" + dynamical).val());
        if (!GLOBAL.Lang.isEmpty(dataChange)) {
            data = dataChange;
        }
        data.startPageNo = currentPage - 1;
        var functionName = $("#functionName"+dynamical).val();
        pagingAjaxPost({
            "dynamical":dynamical, "url":url,
            "urlChange":urlChange, "functionName":functionName,
            "hide":hide, "jstogetChecked":jsToGetChecked,
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
        urlChange = $("#urlChange" + dynamical).val();
        if (!GLOBAL.Lang.isEmpty(urlChange)) {
            url = urlChange;
        }
        dataChange = strToJson($("#dataChange" + dynamical).val());
        if (!GLOBAL.Lang.isEmpty(dataChange)) {
            data = dataChange;
        }
        data.startPageNo = currentPage + 1;
        var functionName = $("#functionName"+dynamical).val();
        pagingAjaxPost({
            "dynamical":dynamical, "url":url,
            "urlChange":urlChange, "functionName":functionName,
            "hide":hide, "jstogetChecked":jsToGetChecked,
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
        urlChange = $("#urlChange" + dynamical).val();
        if (!GLOBAL.Lang.isEmpty(urlChange)) {
            url = urlChange;
        }
        dataChange = strToJson($("#dataChange" + dynamical).val());
        if (!GLOBAL.Lang.isEmpty(dataChange)) {
            data = dataChange;
        }
        data.startPageNo = 1;
        var functionName = $("#functionName"+dynamical).val();
        pagingAjaxPost({
            "dynamical":dynamical, "url":url,
            "urlChange":urlChange, "functionName":functionName,
            "hide":hide, "jstogetChecked":jsToGetChecked,
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
        urlChange = $("#urlChange" + dynamical).val();
        if (!GLOBAL.Lang.isEmpty(urlChange)) {
            url = urlChange;
        }
        dataChange = strToJson($("#dataChange" + dynamical).val());
        if (!GLOBAL.Lang.isEmpty(dataChange)) {
            data = dataChange;
        }
        data.startPageNo = totalPage;
        var functionName = $("#functionName"+dynamical).val();
        pagingAjaxPost({
            "dynamical":dynamical, "url":url,
            "urlChange":urlChange, "functionName":functionName,
            "hide":hide, "jstogetChecked":jsToGetChecked,
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

        urlChange = $("#urlChange" + dynamical).val();
        if (!GLOBAL.Lang.isEmpty(urlChange)) {
            url = urlChange;
        }
        dataChange = strToJson($("#dataChange" + dynamical).val());
        if (!GLOBAL.Lang.isEmpty(dataChange)) {
            data = dataChange;
        }
        data.startPageNo = num;
        var functionName = $("#functionName"+dynamical).val();
        pagingAjaxPost({
            "dynamical":dynamical, "url":url,
            "urlChange":urlChange, "functionName":functionName,
            "hide":hide, "jstogetChecked":jsToGetChecked,
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
            if (!GLOBAL.Lang.isEmpty(urlChange)) {
                url = urlChange;
            }
            if (!GLOBAL.Lang.isEmpty(dataChange)) {
                data = dataChange;
                data.startPageNo = num;
            }
            var functionName = $("#functionName"+dynamical).val();
            pagingAjaxPost({
                "dynamical":dynamical, "url":url,
                "urlChange":urlChange, "functionName":functionName,
                "hide":hide, "jstogetChecked":jsToGetChecked,
                "data":data, "dataChange": dataChange
            });
        }
    });

});
</script>

<!-- TODO to use the json data format to structure the data, delete 3 lines follow  -->
<input type="hidden" id="currentPage${pageScope.dynamical}">
<input type="hidden" id="data${pageScope.dynamical}">
<input type="hidden" id="url${pageScope.dynamical}">
<input type="hidden" id="urlChange${pageScope.dynamical}">
<input type="hidden" id="dataChange${pageScope.dynamical}">
<input type="hidden" id="functionName${pageScope.dynamical}">
<div class="i_pageBtn" id="i_pageBtn${pageScope.dynamical}" style="display:${pageScope.display}">
    <a class="first" id="firstPage${pageScope.dynamical}">首页</a>
    <a class="lastPage" id="prePage${pageScope.dynamical}">上一页</a>
    <span class="ellipsis" id="firstshenglue${pageScope.dynamical}">...</span>
    <span class="ellipsis" id="lastshenglue${pageScope.dynamical}">...</span>
    <a class="nextPage" id="nextPage${pageScope.dynamical}">下一页</a>
    <a class="last" id="lastPage${pageScope.dynamical}">尾页</a>

    <div class="pageNum" id="totalRowS${pageScope.dynamical}">
        &nbsp;&nbsp;共 <span class="recordCount">0</span> 条记录&nbsp;&nbsp;
        <%--<div class="pageNum" id="totalPage${pageScope.dynamical}">--%>
        共
        <span class="pageCount" id="totalPage${pageScope.dynamical}">0</span>
        页，到第
        <input type="text" class="selectPage"
               id="getPage${pageScope.dynamical}">
        页
        <input class="pageSure" type="button" onfocus="this.blur();" value="跳转"
               id="button${pageScope.dynamical}">
    </div>
</div>
