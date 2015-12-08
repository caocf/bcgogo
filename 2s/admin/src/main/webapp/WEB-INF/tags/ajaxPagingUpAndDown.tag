<?xml version="1.0" encoding="UTF-8" ?>
<%@ tag pageEncoding="UTF-8" %>
<%@ attribute name="url" required="true" type="java.lang.String" %>
<%@ attribute name="dynamical" required="true" type="java.lang.String" %>
<%@ attribute name="postFn" type="java.lang.String" %>
<%@ attribute name="urlChange" type="java.lang.String" %>
<%@ attribute name="hide" type="java.lang.String" %>
<%@ attribute name="jsToGetChecked" type="java.lang.String" %>
<%@ attribute name="dataChange" type="java.lang.String" %>
<%@ attribute name="data" type="java.lang.String" %>
<%@ attribute name="display" type="java.lang.String" %>
<%@ attribute name="getDataFunction" type="java.lang.String" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/extension/json2/json2.js"></script>
<script type="text/javascript">
var pagingAjaxPostForUpAndDownFunction = [];
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
    var data = {currentPage:1, pageSize:10};

    /**中途需要改变的data(需要开发*/
    var dataChange = '${pageScope.dataChange}';

    /**获取查询条件的方法*/
    var getDataFunction = '${pageScope.getDataFunction}';

    var inputValue = "";
    /**吐过前台有data传过来就替换默认的data 前台加data 要这么加 ：data="{\\\"currentPage\\\":1,\\\"pageSize\\\":1}"*/
    if (null != '${pageScope.data}' && 'null' != '${pageScope.data}' && '' != '${pageScope.data}') {
        data = JSON.parse("${pageScope.data}");
    }else if(getDataFunction){
        data = eval(getDataFunction)();
    }
    pagingAjaxPostForUpAndDownFunction[dynamical] = [];
    $("#url" + dynamical).val(url);
    $("#data" + dynamical).val(JsonToStr(data));
    $("#functionName"+dynamical).val(functionName);

    //如果true  加载
    if("none"!="${pageScope.display}"){
        $.ajax({
            type:"POST",
            url:url,
            data:data,
            cache:false,
            dataType:"json",
            success:function (jsonStr) {
                if( !jsonStr ) return false;

                eval(functionName)(jsonStr);
                initUpAndDownPage(jsonStr, dynamical, url, functionName, data);
//                initPage(jsonStr, dynamical, url, urlChange, functionName, hide, jsToGetChecked, data, dataChange);
            },
            error:function(jsonStr){
                GLOBAL.error("获取分页数据出错。");
                GLOBAL.error(jsonStr);
            }
        });
    }

    $(".j_sort").live("click", function () {
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
        pagingAjaxPostForUpAndDown({"dynamical":dynamical, "url":url, "functionName":functionName,"data":data});

    });

    /**上一页按钮事件*/
    $("#prePage" + dynamical).live('click', function () {
        data = strToJson($("#data" + dynamical).val());
        var currentPage = $("#currentPage" + dynamical).val() * 1;
        /**当前页已经是第一页就返回*/
        if (currentPage == 1) {
            return;
        }
        if (getDataFunction) {
            data = eval(getDataFunction)();
        }
        data.currentPage = currentPage - 1;
        if (!GLOBAL.Lang.isEmpty(urlChange)) {
            url = urlChange;
        }
        if (!GLOBAL.Lang.isEmpty(dataChange)) {
            data = dataChange;
            data.currentPage = num;
        }

        var functionName = $("#functionName"+dynamical).val();
        pagingAjaxPostForUpAndDown({"dynamical":dynamical, "url":url, "functionName":functionName,"data":data});
    });

    /**下一页按钮事件*/
    $("#nextPage" + dynamical).live('click', function () {
        data = strToJson($("#data" + dynamical).val());
        var currentPage = $("#currentPage" + dynamical).val() * 1;

        var totalPage = $("#totalPage" + dynamical).val() * 1;
        /**当前页已经是最后一页就返回*/
        if (currentPage >= totalPage) {
            return;
        }
        if (getDataFunction) {
            data = eval(getDataFunction)();
        }
        data.currentPage = currentPage + 1;
        if (!GLOBAL.Lang.isEmpty(urlChange)) {
            url = urlChange;
        }
        if (!GLOBAL.Lang.isEmpty(dataChange)) {
            data = dataChange;
            data.currentPage = num;
        }
        var functionName = $("#functionName"+dynamical).val();
        pagingAjaxPostForUpAndDown({"dynamical":dynamical, "url":url, "functionName":functionName,"data":data});
    });
    pagingAjaxPostForUpAndDownFunction[dynamical]["flush"] = function () {
        data = strToJson($("#data" + dynamical).val());
        var functionName = $("#functionName" + dynamical).val();
        pagingAjaxPostForUpAndDown({"dynamical": dynamical, "url": url, "functionName": functionName, "data": data});
    }
});

/**动态初始化组件*/
function initUpAndDownPage(jsonStr, dynamicalID, url, functionName, data) {
    url = $("#url" + dynamicalID).val();
    /***/
    var pager = jsonStr.pager;
    if (pager) {
         $("#i_pageBtn" + dynamicalID).show();

        $("#functionName" + dynamicalID).val(functionName);

        $("#totalPage" + dynamicalID).val(pager.totalPage);
        $("#currentPage" + dynamicalID).val(pager.currentPage);

        var totalPage = pager.totalPage * 1;
        var currentPage = pager.currentPage * 1;
        if (currentPage >= totalPage) {
            $("#nextPage" + dynamicalID).removeClass('wordButton');
        } else {
            $("#nextPage" + dynamicalID).addClass('wordButton');
        }
        if (currentPage == 1) {
            $("#prePage" + dynamicalID).removeClass('wordButton');
        } else {
            $("#prePage" + dynamicalID).addClass('wordButton');
        }
    }

    var dataString = JsonToStr(data);
    $("#data" + dynamicalID).val(dataString);

}

function pagingAjaxPostForUpAndDown( params ) {
    $.ajax({
        type:"POST",
        url:params["url"],
        data:params["data"],
        cache:false,
        dataType:"json",
        success: function (jsonData) {
            if (!jsonData)
                return false;
            eval(params["functionName"])(jsonData);
            initUpAndDownPage(jsonData, params["dynamical"], params["url"], params["functionName"], params["data"])
        }
    });

}
</script>

<!-- TODO to use the json data format to structure the data, delete 3 lines follow  -->
<input type="hidden" id="currentPage${pageScope.dynamical}">
<input type="hidden"id="totalPage${pageScope.dynamical}">
<input type="hidden" id="data${pageScope.dynamical}">
<input type="hidden" id="url${pageScope.dynamical}">
<input type="hidden" id="functionName${pageScope.dynamical}">
<div class="i_pageBtn" id="i_pageBtn${pageScope.dynamical}" style="display:${pageScope.display}">
    <a class="lastPage" id="prePage${pageScope.dynamical}">上一页</a>
    <a class="nextPage" id="nextPage${pageScope.dynamical}">下一页</a>
</div>
