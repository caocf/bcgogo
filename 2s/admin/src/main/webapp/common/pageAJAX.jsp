<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

 <script type="text/javascript" src="js/extension/json2/json2.min.js"></script>
<script type="text/javascript">


jQuery(function() {
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
    var data = {startPageNo:1,maxRows:10};


    /**中途需要改变的data(需要开发*/
    var dataChange = <%=request.getParameter("dataChagne")%>;

    var inputValue = "";
    /**吐过前台有data传过来就替换默认的data*/
    if(null != <%=request.getParameter("data")%> && 'null' != <%=request.getParameter("data")%>)
    {
        data = eval(<%=request.getParameter("data")%>);
    }

    jQuery("#url"+dynamical).val(url);
    jQuery("#data"+dynamical).val(JsonToStr(data));

    jQuery.ajax({
        type:"POST",
        url:url,
        data:data,
        cache:false,
        dataType:"json",
        success:function(jsonStr) {
            eval(functionName)(jsonStr);
            /**只是代办事项中会遇到*/
            if('hideIt' == hide)
            {
                eval(hide+"()");
            }
            initfenye(jsonStr,dynamical,url,urlChange,functionName,hide,jstogetChecked,data,dataChange);
        }
    });
    /**上一页按钮事件*/
    jQuery("#prePage"+dynamical).live('click',function(){
       data = strToJson(jQuery("#data"+dynamical).val());
        var currentPage = jQuery("#currentPage"+dynamical).val() * 1;
        /**当前页已经是第一页就返回*/
        if(currentPage==1)
        {
            return;
        }

        data.startPageNo = currentPage-1;

        if(null!=urlChange &&  'null' != urlChange)
        {
            url = urlChange;
        }
        if(null!=dataChange &&  'null' != dataChange)
        {
            data = dataChange;

            data.startPageNo = num;
        }
        jQuery.ajax({
            type:"POST",
            url:url,
            data:data,
            cache:false,
            dataType:"json",
            success:function(jsonStr) {
                eval(functionName)(jsonStr);
                if('setCheckedByPreOrNextPage' == jstogetChecked)
                {
                    eval(jstogetChecked+"()");
                }
                initfenye(jsonStr,dynamical,url,urlChange,functionName,hide,jstogetChecked,data,dataChange);
            }
        });

    });

    /**下一页按钮事件*/
    jQuery("#nextPage"+dynamical).live('click',function(){
        data = strToJson(jQuery("#data"+dynamical).val());
        var currentPage = jQuery("#currentPage"+dynamical).val() * 1;

        var totalPage = jQuery("#totalPage"+dynamical).html() * 1;
        /**当前页已经是最后一页就返回*/
        if(currentPage==totalPage)
        {
            return;
        }
        data.startPageNo = currentPage+1;
        if(null!=urlChange &&  'null' != urlChange)
        {
            url = urlChange;
        }
        if(null!=dataChange &&  'null' != dataChange)
        {
            data = dataChange;
            data.startPageNo = num;
        }
        jQuery.ajax({
            type:"POST",
            url:url,
            data:data,
            cache:false,
            dataType:"json",
            success:function(jsonStr) {
                eval(functionName)(jsonStr);
                if('setCheckedByPreOrNextPage' == jstogetChecked)
                {
                    eval(jstogetChecked+"()");
                }

                initfenye(jsonStr,dynamical,url,urlChange,functionName,hide,jstogetChecked,data,dataChange);
            }
        });

    });

    /**首页事件*/
    jQuery("#firstPage"+dynamical).live('click',function(){
        data = strToJson(jQuery("#data"+dynamical).val());
        var currentPage = jQuery("#currentPage"+dynamical).val() * 1;
        /**当前页已经是第一页就返回*/
        if(currentPage==1)
        {
            return;
        }

        data.startPageNo = 1;
        if(null!=urlChange &&  'null' != urlChange)
        {
            url = urlChange;
        }
        if(null!=dataChange &&  'null' != dataChange)
        {
            data = dataChange;
            data.startPageNo = num;
        }
        jQuery.ajax({
            type:"POST",
            url:url,
            data:data,
            cache:false,
            dataType:"json",
            success:function(jsonStr) {
                eval(functionName)(jsonStr);
                if('setCheckedByPreOrNextPage' == jstogetChecked)
                {
                    eval(jstogetChecked+"()");
                }
                initfenye(jsonStr,dynamical,url,urlChange,functionName,hide,jstogetChecked,data,dataChange);
            }
        });

    });
    /**尾页事件*/
    jQuery("#lastPage"+dynamical).live('click',function(){
        data = strToJson(jQuery("#data"+dynamical).val());
        var currentPage = jQuery("#currentPage"+dynamical).val() * 1;
        var totalPage = jQuery("#totalPage"+dynamical).html() * 1;
        /**当前页已经是最后一页就返回*/
        if(currentPage==totalPage)
        {
            return;
        }

        data.startPageNo = totalPage;
        if(null!=urlChange &&  'null' != urlChange)
        {
            url = urlChange;
        }
        if(null!=dataChange && 'null' != dataChange)
        {
            data = dataChange;
            data.startPageNo = num;
        }
        jQuery.ajax({
            type:"POST",
            url:url,
            data:data,
            cache:false,
            dataType:"json",
            success:function(jsonStr) {
                eval(functionName)(jsonStr);
                if('setCheckedByPreOrNextPage' == jstogetChecked)
                {
                    eval(jstogetChecked+"()");
                }
                initfenye(jsonStr,dynamical,url,urlChange,functionName,hide,jstogetChecked,data,dataChange);
            }
        });

    });

    /**点击跳转按钮跳页事件*/
    jQuery("#button"+dynamical).live('click',function(){
        data = strToJson(jQuery("#data"+dynamical).val());
        var currentPage = jQuery("#currentPage"+dynamical).val() * 1;
        var totalPage = jQuery("#totalPage"+dynamical).html() * 1;
        /**输入框中输入页码*/

        var num = jQuery("#getPage"+dynamical).val()*1;

        if(isNaN(num))
        {
            jQuery("#getPage"+dynamical).val(currentPage);
            return;
        }
        /**输入页码小于1把页码置1*/
        if(num < 1)
        {
            num = 1;
            jQuery("#getPage"+dynamical).val(num);
        }
        /**输入页码大于总页码把页码置为总页码*/
        if(num > totalPage)
        {
            num = totalPage;
            jQuery("#getPage"+dynamical).val(num)
        }
        /**输入页码等于当前页就返回*/
        if(num == currentPage)
        {
            return;
        }

        data.startPageNo = num;
        if(null!=urlChange &&  'null' != urlChange)
        {
            url = urlChange;
        }
        if(null!=dataChange &&  'null' != dataChange)
        {
            data = dataChange;
            data.startPageNo = num;
        }

        jQuery.ajax({
            type:"POST",
            url:url,
            data:data,
            cache:false,
            dataType:"json",
            success:function(jsonStr) {
                eval(functionName)(jsonStr);
                if('setCheckedByPreOrNextPage' == jstogetChecked)
                {
                    eval(jstogetChecked+"()");
                }
                initfenye(jsonStr,dynamical,url,urlChange,functionName,hide,jstogetChecked,data,dataChange);
            }
        });

    });

    document.getElementById("getPage"+dynamical).onkeyup=function(evt){
        evt = evt || event;
        var k=window.event?evt.keyCode:evt.which
        if(this.value == '0'){
            this.value='';
            return;
        }
        if(k==108||k==13){
           return;
        }else if((k>=96&&k<=105)||(k>=48&&k<=57)){
            if (this.value.substring(0, 1) == '0') {
                this.value = inputValue;
                return;
            }
            inputValue = this.value;
        }else if(k==8){
            inputValue = this.value;
        }else{
            this.value=inputValue;
        }
    }
    /**输入框中输入页码直接回车事件，和跳转按钮事件一样*/
    jQuery("#getPage"+dynamical).live('keypress',function(e){
        e = e || event;
        var k = window.event?e.keyCode:e.which
        if(k== 13 || k==108)
        {
           data = strToJson(jQuery("#data"+dynamical).val());
            var currentPage = jQuery("#currentPage"+dynamical).val() * 1;
            var totalPage = jQuery("#totalPage"+dynamical).html() * 1;
            var num = jQuery("#getPage"+dynamical).val()*1;
            if(isNaN(num))
            {
                jQuery("#getPage"+dynamical).val(currentPage);
                return;
            }
            if(num < 1)
            {
                num = 1;
                jQuery("#getPage"+dynamical).val(num);
            }

            if(num > totalPage)
            {
                num = totalPage;
                jQuery("#getPage"+dynamical).val(num)
            }

            if(num == currentPage)
            {
                return;
            }
            data.startPageNo = num;
            if(null!=urlChange && 'null' != urlChange)
            {
                url = urlChange;
            }
            if(null!=dataChange &&  'null' != dataChange)
            {
                data = dataChange;
                data.startPageNo = num;
            }
            jQuery.ajax({
                type:"POST",
                url:url,
                data:data,
                cache:false,
                dataType:"json",
                success:function(jsonStr) {
                    eval(functionName)(jsonStr);
                    if('setCheckedByPreOrNextPage' == jstogetChecked)
                    {
                        eval(jstogetChecked+"()");
                    }
                    initfenye(jsonStr,dynamical,url,urlChange,functionName,hide,jstogetChecked,data,dataChange);
                }
            });
        }


    });

});
/**动态初始化组件*/
function initfenye(jsonStr,dynamicalID,url,urlChange,functionName,hide,jstogetChecked,data,dataChange)
{
    url = jQuery("#url"+dynamicalID).val();
  /***/
    if((jsonStr[jsonStr.length-1].totalRows)*1 <1)
    {
       jQuery("#i_pageBtn"+dynamicalID).hide();
       return;
    }else{
       jQuery("#i_pageBtn"+dynamicalID).show();
    }

    jQuery("#totalRowS"+dynamicalID+" span").html(jsonStr[jsonStr.length-1].totalRows);
    jQuery("#totalPage"+dynamicalID).html(jsonStr[jsonStr.length-1].totalPage);
    jQuery("#getPage"+dynamicalID).val(jsonStr[jsonStr.length-1].currentPage);
    jQuery("#currentPage"+dynamicalID).val(jsonStr[jsonStr.length-1].currentPage);

    var totalPage = jsonStr[jsonStr.length-1].totalPage * 1;
    var currentPage = jsonStr[jsonStr.length-1].currentPage * 1;

    if(currentPage==totalPage)
    {
        jQuery("#nextPage"+dynamicalID).css("background","#F5F4EA");
        jQuery("#nextPage"+dynamicalID).css("color","#ACA899");
        jQuery("#lastPage"+dynamicalID).css("background","#F5F4EA")
        jQuery("#lastPage"+dynamicalID).css("color","#ACA899");
    }
    else
    {
        jQuery("#nextPage"+dynamicalID).css("background","#FFFFFF");
        jQuery("#nextPage"+dynamicalID).css("color","#2953A6");
        jQuery("#lastPage"+dynamicalID).css("background","#FFFFFF");
        jQuery("#lastPage"+dynamicalID).css("color","#2953A6");
    }

    if(currentPage ==1)
    {
        jQuery("#prePage"+dynamicalID).css("background","#F5F4EA");
        jQuery("#prePage"+dynamicalID).css("color","#ACA899");
        jQuery("#firstPage"+dynamicalID).css("background","#F5F4EA")
        jQuery("#firstPage"+dynamicalID).css("color","#ACA899");
    }
    else
    {
        jQuery("#prePage"+dynamicalID).css("background","#FFFFFF");
        jQuery("#prePage"+dynamicalID).css("color","#2953A6");
        jQuery("#firstPage"+dynamicalID).css("background","#FFFFFF");
        jQuery("#firstPage"+dynamicalID).css("color","#2953A6");
    }

    var tr = ' ';

    jQuery(jQuery("#firstshenglue"+dynamicalID).nextUntil("#lastshenglue"+dynamicalID)).remove();

    var dataString =JsonToStr(data);
    jQuery("#data"+dynamicalID).val(dataString);
    for(var i=1;i<=totalPage;i++)
    {
        tr = tr + '<a class="first" id="menu_id'+dynamicalID+i+'" style="display:none" onclick = "goto('+i+','+currentPage+',\''+dynamicalID+'\',\''+url+'\',\''+urlChange+'\',\''+functionName+'\',\''+hide+'\',\''+jstogetChecked+'\','+dataString+',\''+dataChange+'\')">'+i+'</a>';
    }
    jQuery("#firstshenglue"+dynamicalID).after(tr);
    jQuery('#menu_id'+dynamicalID+currentPage).css('background-color','#2953A6');
    jQuery('#menu_id'+dynamicalID+currentPage).css('color','#FFFFFF');
    jQuery('#menu_id'+dynamicalID+currentPage).css('border','1px solid #2953A6');
    if(totalPage<=5)
    {
        for(var i = 1;i<=totalPage;i++)
        {
            document.getElementById("menu_id"+dynamicalID+i).style.display = "block"
        }
    }
    else
    {

        if(currentPage-2<=1)
        {
            for(var i = 1;i<=5;i++)
            {
                document.getElementById("menu_id"+dynamicalID+i).style.display = "block";
            }
            jQuery("#lastshenglue"+dynamicalID).css('display','block');
        }

        if((currentPage+2)>=totalPage)
        {
            for(var i = totalPage-4;i<=totalPage;i++)
            {
                document.getElementById("menu_id"+dynamicalID+i).style.display = "block";
            }
            jQuery("#firstshenglue"+dynamicalID).css('display','block');
        }

        if(currentPage>3 && currentPage+2<totalPage)
        {
            //jQuery(".i_pageBtn >a:gt("+(currentPage-3)+"):lt("+(currentPage+1)+")").css('display','block');
            for(var i = currentPage-2;i<=currentPage+2;i++)
            {
                document.getElementById("menu_id"+dynamicalID+i).style.display = "block"
            }
            jQuery("#firstshenglue"+dynamicalID).css('display','block');
            jQuery("#lastshenglue"+dynamicalID).css('display','block');
        }

    }

    if(jQuery('#menu_id'+dynamicalID+totalPage).css('display') == 'block')
    {
        jQuery("#lastshenglue"+dynamicalID).css('display','none')
    }

    if(jQuery('#menu_id'+dynamicalID+"1").css('display') == 'block')
    {
        jQuery("#firstshenglue"+dynamicalID).css('display','none')
    }
}

function goto(num,currentPage,dynamicalID,url,urlChange,functionName,hide,jstogetChecked,data1,dataChange)
{
    url = jQuery("#url"+dynamicalID).val();

    var data = eval(data1);
    if(num == currentPage)
    {
        return;
    }
    data.startPageNo = num;
    if(null!=urlChange && 'null' != urlChange&& ''!=urlChange)
    {
        url = urlChange;
    }
    if(null!=dataChange && 'null' != dataChange&& ''!=urlChange)
    {
        data = dataChange;
        data.startPageNo = num;
    }
    jQuery.ajax({
        type:"POST",
        url:url,
        data:data,
        cache:false,
        dataType:"json",
        success:function(jsonStr) {
            eval(functionName)(jsonStr);
            if('setCheckedByPreOrNextPage' == jstogetChecked)
            {
                eval(jstogetChecked+"()");
            }
            initfenye(jsonStr,dynamicalID,url,urlChange,functionName,hide,jstogetChecked,data,dataChange);
        }
    });

}

function checkRate(input)
{
    var ex = /^(\d+)$/;
    if (ex.test(input)) {
      return true;
    }
    return false;
}

function JsonToStr(obj)
{
    var dataString = JSON.stringify(obj);
    String.prototype.replaceAll = function(s1,s2){
        return this.replace(new RegExp(s1,"gm"),s2);
    }
    dataString = dataString.replaceAll("\"","\'");
    return dataString;
}

function strToJson(str)
{
    return eval('('+str+')');
}

</script>

<input type = "hidden" id="currentPage<%=request.getParameter("dynamical")%>">
<input type = "hidden" id="data<%=request.getParameter("dynamical")%>">
<input type = "hidden" id="url<%=request.getParameter("dynamical")%>">
<div class="i_pageBtn" id="i_pageBtn<%=request.getParameter("dynamical")%>">
    <a class="first" id="firstPage<%=request.getParameter("dynamical")%>">首页</a>
    <a class="lastPage" id = "prePage<%=request.getParameter("dynamical")%>"><< 上一页</a>
    <span class="ellipsis" id = "firstshenglue<%=request.getParameter("dynamical")%>">...</span>
    <span class="ellipsis" id = "lastshenglue<%=request.getParameter("dynamical")%>">...</span>
    <a class="nextPage" id = "nextPage<%=request.getParameter("dynamical")%>">下一页 >></a>
    <a id="lastPage<%=request.getParameter("dynamical")%>">尾页</a>
    <div class="pageNum" id="totalRowS<%=request.getParameter("dynamical")%>">
        &nbsp;&nbsp;共 <span>11</span> 条记录&nbsp;&nbsp;
    <%--<div class="pageNum" id="totalPage<%=request.getParameter("dynamical")%>">--%>
        共
        <span id="totalPage<%=request.getParameter("dynamical")%>">12</span>
        页，到第
        <input type="text" style="width:30px; margin:0px 2px 0px 2px;"  id = "getPage<%=request.getParameter("dynamical")%>">
        页
        <input class="pageSure" type="button" onfocus="this.blur();" value="跳转" id ="button<%=request.getParameter("dynamical")%>">
    </div>
</div>
