<%@ page import="com.bcgogo.common.Pager" %>
<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- 分页头 -->
<script language="javascript">
    jQuery(function(){
        var url = '<%=request.getParameter("url")%>'+'&pageNo';
        var dynamical = '<%=null==request.getParameter("dynamical")?"null":request.getParameter("dynamical")%>';
        var pager = '<%=request.getParameter("pager")%>';
        var submit = '<%=request.getParameter("submit")%>';
        var inputValue = "";
        if(null != pager && 'null' != pager)
        {
            if(null != <%=request.getParameter("isFirstFenYe")%>)
            {
                var pPageNo =  <%=request.getAttribute("pPageNo")%>;
                url = '<%=request.getParameter("url")%>'+'&pNo='+pPageNo+'&iNo';
            }
            else
            {
                var ihPageNo =  <%=session.getAttribute("ihPageNo")%>;
                url = '<%=request.getParameter("url")%>'+'&iNo='+ihPageNo+'&pNo';
            }
        }
        <%
            /**获取后台传过来的Pager分页对象*/
            Pager pager = (Pager)request.getAttribute(null==request.getParameter("pager")?"pager":request.getParameter("pager"));
        %>
        var i = <%= pager.getCurrentPage()%>
        /**总页数*/
        var totalPage = <%=pager.getTotalPage()%>;
        /**当前页*/
        var currentPage = <%=pager.getCurrentPage()%>;
       /** 总记录数*/
        var totalRows = <%=pager.getTotalRows()%>;
        if(totalRows<1)
        {
            jQuery("#i_pageBtn"+dynamical).hide();
            return;
        }
        var tr='';
        for(var i=1;i<=totalPage;i++)
        {
            tr = tr + '<a class="first" id="menu_id'+i+dynamical+'" style="display:none" onclick = "go('+i+','+currentPage+',\''+url+'\',\''+submit+'\')">'+i+'</a>';
        }
        jQuery("#firstshenglue"+dynamical).after(tr);


        /**每页的按钮小于5时候全部显示，大于5的时候只显示5个*/
        if(totalPage<=5)
        {
            for(var i = 1;i<=totalPage;i++)
            {
                document.getElementById("menu_id"+i+dynamical).style.display = "block"
            }
        }
        else
        {
            /**显示最前面5个按钮*/
            if(currentPage-2<=1)
            {
                for(var i = 1;i<=5;i++)
                {
                    document.getElementById("menu_id"+i+dynamical).style.display = "block"
                }
            }
            /**显示最后面5个按钮*/
            if((currentPage+2)>=totalPage)
            {
                for(var i = totalPage-4;i<=totalPage;i++)
                {
                    document.getElementById("menu_id"+i+dynamical).style.display = "block";
                }
            }
            /**显示中间5个按钮*/
            if(currentPage>3 && currentPage+2<totalPage)
            {
                for(var i = currentPage-2;i<=currentPage+2;i++)
                {
                     document.getElementById("menu_id"+i+dynamical).style.display = "block"
                }
            }

        }
        /**如果是当前页就把页码变色*/
        jQuery("#menu_id"+currentPage+dynamical).css('background-color','#2953A6');
        jQuery("#menu_id"+currentPage+dynamical).css('color','#FFFFFF');
        jQuery("#menu_id"+currentPage+dynamical).css('border','1px solid #2953A6');

        jQuery("#getPage"+dynamical).val(currentPage);
        jQuery("#totalRows"+dynamical).html(totalRows);
        jQuery("#totalPage"+dynamical).html(totalPage);
        /**如果最后面的页码为最后一页，使后面的省略号隐藏*/
        if(jQuery('#menu_id'+totalPage+dynamical).css('display') == 'block')
        {
            jQuery("#lastshenglue"+dynamical).css('display','none')
        }
        /**如果最前面的页码为1，使前面的省略号隐藏*/
        if(jQuery('#menu_id1'+dynamical).css('display') == 'block')
        {
            jQuery("#firstshenglue"+dynamical).css('display','none')
        }
        //实现按钮变灰不可点击效果
        if(currentPage == totalPage)
        {
            jQuery("#nextPage"+dynamical).css("background","#F5F4EA");
            jQuery("#nextPage"+dynamical).css("color","#ACA899");
            jQuery("#nextPage"+dynamical).next().css("background","#F5F4EA")
            jQuery("#nextPage"+dynamical).next().css("color","#ACA899");

        }
        if(currentPage==1)
        {
            jQuery("#firstPage"+dynamical).css("background","#F5F4EA");
            jQuery("#firstPage"+dynamical).css("color","#ACA899");
            jQuery("#firstPage"+dynamical).next().css("background","#F5F4EA")
            jQuery("#firstPage"+dynamical).next().css("color","#ACA899");
        }

        jQuery("#firstPage"+dynamical).live('click',function(){
            if(null != submit && 'null'!=submit)
            {
                if(1 == currentPage)
                {
                    return;
                }
                document.thisform.pageNo.value = 1;
                document.thisform.submit();
                return;
            }

           go(1,currentPage,url,submit);
        });

        jQuery("#lastPage"+dynamical).live('click',function(){
            if(null != submit && 'null'!=submit)
            {
                if(currentPage == totalPage)
                {
                    return;
                }
                document.thisform.pageNo.value = totalPage;
                document.thisform.submit();
                return;
            }

            go(totalPage,currentPage,url,submit);
        });

        jQuery("#nextPage"+dynamical).live('click',function(){
            if(null != submit && 'null'!=submit)
            {
                if(currentPage>=totalPage)
                {
                    return;
                }
                document.thisform.pageNo.value = currentPage + 1;
                document.thisform.submit();
                return;
            }
            nextPage(currentPage,totalPage,url);
        });

        jQuery("#prePage"+dynamical).live('click',function(){
             if(null != submit && 'null'!=submit)
            {
                if(currentPage<=1)
                {
                    return;
                }
                document.thisform.pageNo.value = currentPage - 1;
                document.thisform.submit();
                return;
            }
            prePage(currentPage,url);
        });

        jQuery("#button"+dynamical).live('click',function(){
            var num = jQuery("#getPage"+dynamical).val() * 1;
            if(isNaN(num))
            {
                jQuery("#getPage"+dynamical).val(currentPage);
                return;
            }
            if(jQuery("#getPage"+dynamical).val() * 1<1)
            {
                num = 1;
                jQuery("#getPage"+dynamical).val(1);
                if(currentPage*1 == 1)
                {
                    return;
                }
            }

            if(jQuery("#getPage"+dynamical).val() * 1>totalPage)
            {
                num = totalPage;
                jQuery("#getPage"+dynamical).val(totalPage);
                if(currentPage*1 == totalPage)
                {
                    return;
                }
            }

            if(jQuery("#getPage"+dynamical).val() * 1 == currentPage)
            {
                return;
            }

            if(null != submit && 'null'!=submit)
            {
                document.thisform.pageNo.value = num;
                document.thisform.submit();
                return;
            }

            window.location =url+"=" + num;
        });

        jQuery("#getPage"+dynamical).get(0).onkeyup=function(evt){
            evt = evt || event;
            var k=window.event?evt.keyCode:evt.which;
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


        jQuery("#getPage"+dynamical).live('keypress',function(e){
            var e = e || window.event;
            var k=window.event?e.keyCode:e.which;
            if(k == 13 || k== 108)
            {
                var num = jQuery("#getPage"+dynamical).val() * 1;
                if(isNaN(num))
                {
                    jQuery("#getPage"+dynamical).val(currentPage);
                    return;
                }
                if(jQuery("#getPage"+dynamical).val() * 1<1)
                {
                    num = 1;
                    jQuery("#getPage"+dynamical).val(1);
                    if(currentPage*1 == 1)
                    {
                        return;
                    }
                }

                if(jQuery("#getPage"+dynamical).val() * 1>totalPage)
                {
                    num = totalPage;
                    jQuery("#getPage"+dynamical).val(totalPage);
                    if(currentPage*1 == totalPage)
                    {
                        return;
                    }
                }

                if(jQuery("#getPage"+dynamical).val() * 1 == currentPage)
                {
                    return;
                }
                if(null != submit && 'null'!=submit)
                {
                    document.thisform.pageNo.value = num;
                    document.thisform.submit();
                    return;
                }
                window.location =url+"=" + num;
            }
        });


    });
    /**点击上页触发的事件*/
    function prePage(nowPage,url) {
      if (nowPage != null) {
        if (nowPage * 1 == 1) {
          //alert("已经是第一页!");
          return;
        }
        window.location = url+ "=" + (nowPage * 1 - 1);
      }
    }
    /**点击下页触发的事件*/
    function nextPage(nowPage, totalPage,url) {
      if (nowPage * 1 == totalPage * 1) {
        //alert("已经是最后一页!");
        return;
      }
      window.location =url+"=" +  (nowPage * 1 + 1);
    }
    /**点击页码时触发的事件*/
    function go(num,nowPage,url,submit)
    {
        if(num * 1 == nowPage * 1)
        {
            return;
        }
        if(null != submit && 'null'!=submit)
        {
            document.thisform.pageNo.value = num;
            document.thisform.submit();
            return;
        }
        window.location =url+"=" + num * 1;
    }


</script>


<input type="hidden" id="getURL<%=null==request.getParameter("dynamical")?"null":request.getParameter("dynamical")%>">
    <div class="i_pageBtn" id="i_pageBtn<%=null==request.getParameter("dynamical")?"null":request.getParameter("dynamical")%>">
        <%--<div class="pageNum">共 <span>${pager.totalRows}</span> 条记录&nbsp;</div>--%>
        <a id="firstPage<%=null==request.getParameter("dynamical")?"null":request.getParameter("dynamical")%>" class="first">首&nbsp;页</a>
        <a class="lastPage" id="prePage<%=null==request.getParameter("dynamical")?"null":request.getParameter("dynamical")%>" ><< 上一页</a>
        <span class="ellipsis" id = "firstshenglue<%=null==request.getParameter("dynamical")?"null":request.getParameter("dynamical")%>">...</span>

        <span class="ellipsis" id = "lastshenglue<%=null==request.getParameter("dynamical")?"null":request.getParameter("dynamical")%>">...</span>
        <a class="nextPage" id="nextPage<%=null==request.getParameter("dynamical")?"null":request.getParameter("dynamical")%>" >下一页 >></a>
        <a id="lastPage<%=null==request.getParameter("dynamical")?"null":request.getParameter("dynamical")%>">尾&nbsp;页</a>
        <div class="pageNum">&nbsp;&nbsp;
            共 <span id = "totalRows<%=null==request.getParameter("dynamical")?"null":request.getParameter("dynamical")%>">0</span> 条记录&nbsp;&nbsp;
            共
            <span id="totalPage<%=null==request.getParameter("dynamical")?"null":request.getParameter("dynamical")%>">0</span>
            页，到第
            <input type="text" style="width:30px; margin:0px 2px 0px 2px;" value="${pager.currentPage}" id = "getPage<%=null==request.getParameter("dynamical")?"null":request.getParameter("dynamical")%>" >
            页
            <input class="pageSure" type="button" onfocus="this.blur();" value="跳转" id ="button<%=null==request.getParameter("dynamical")?"null":request.getParameter("dynamical")%>">
        </div>
    </div>
