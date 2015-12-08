<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script type="text/javascript" src="js/extension/json2/json2.min.js"></script>
<script type="text/javascript">
    var pagingDataProvider = null;
    function getPagingDataProvider(){
        if(pagingDataProvider==null){
            pagingDataProvider={};
            return  pagingDataProvider;
        }
        return  pagingDataProvider;
    }
    jQuery(function() {
        pagingDateProvider= getPagingDataProvider();
        var url = '<%=request.getParameter("url")%>';
        var functionName = '<%=request.getParameter("jsHandleJson")%>';
        var dynamicalID = '<%=request.getParameter("dynamicalID")%>';
        var data=<%=request.getParameter("data")%>;
        pagingDataProvider[dynamicalID]= data;
        jQuery.ajax({
            type:"POST",
            url:url,
            data:pagingDataProvider[dynamicalID],
            cache:false,
            dataType:"json",
            success:function(jsonStr) {
                eval(functionName)(jsonStr);
                refreshPage(jsonStr,dynamicalID,functionName);
            }
        });

    jQuery("#lastPage"+dynamicalID).live('click',function(){
        pagingDataProvider[dynamicalID].pageNo= pagingDataProvider[dynamicalID].pageNo-1;
        jQuery.ajax({
            type:"POST",
            url:url,
            data:pagingDataProvider[dynamicalID],
            cache:false,
            dataType:"json",
            success:function(jsonStr) {
                eval(functionName)(jsonStr);
                refreshPage(jsonStr,dynamicalID,functionName);
            }
        });
    });
    jQuery("#nextPage"+dynamicalID).live('click',function(){
        pagingDataProvider[dynamicalID].pageNo= pagingDataProvider[dynamicalID].pageNo+1;
        jQuery.ajax({
            type:"POST",
            url:url,
            data: pagingDataProvider[dynamicalID],
            cache:false,
            dataType:"json",
            success:function(jsonStr) {
                window[functionName](jsonStr);
                eval(functionName)(jsonStr);
                refreshPage(jsonStr,dynamicalID,functionName);
            }
        });
    });
});
function refreshPage(jsonStr,dynamicalID,functionName){
    var isLastPage = jsonStr[jsonStr.length-1].isLastPage;
    var  currentPage= jsonStr[jsonStr.length-1].currentPage;
    jQuery("#currentPage"+dynamicalID).val(currentPage);
     jQuery("#onlin_his"+dynamicalID).html(currentPage);

    var dom_lastPage= jQuery("#lastPage"+dynamicalID);
    var dom_nextPage= jQuery("#nextPage"+dynamicalID);
    jQuery("#currentPage"+dynamicalID).css('display', 'block');
    if (isLastPage == true && currentPage == 1) {
        dom_lastPage.css('display', 'none');
        dom_nextPage.css('display', 'none');
    }
    else if (isLastPage == true && currentPage > 1) {
        dom_lastPage.css('display', 'block');
        dom_nextPage.css('display', 'none');
    }
    else if (isLastPage == false && currentPage == 1) {
        dom_lastPage.css('display', 'none');
        dom_nextPage.css('display', 'block');
    }
    else if (isLastPage == false && currentPage > 1) {
        dom_lastPage.css('display', 'block');
        dom_nextPage.css('display', 'block');
    }

}





</script>

<div class="i_leftBtn i_bottom" id="i_pageBar<%=request.getParameter("dynamicalID")%>">
    <div class="lastPage"id="lastPage<%=request.getParameter("dynamicalID")%>">上一页</div>
    <div class="onlin_his" id="onlin_his<%=request.getParameter("dynamicalID")%>">1</div>
    <div class="nextPage" id="nextPage<%=request.getParameter("dynamicalID")%>">下一页</div>
</div>