<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>后台管理—导入模板</title>

    <%-- styles --%>
    <link rel="stylesheet" type="text/css" href="styles/backstage.css"/>
    <link rel="stylesheet" type="text/css" href="styles/backAgent.css"/>
    <link rel="stylesheet" type="text/css" href="styles/storesMange.css"/>
    <link rel="stylesheet" type="text/css" href="styles/style.css"/>
    <%@include file="/WEB-INF/views/style-thirdpartLibs.jsp"%>

    <%-- scripts --%>
    <%@include file="/WEB-INF/views/script-thirdpartLibs.jsp"%>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
    <script type="text/javascript" src="js/uploadPreview.js"></script>
    <%@include file="/WEB-INF/views/script-common.jsp"%>
    <script type="text/javascript" src="js/searchDefault.js"></script>

    <script type="text/javascript">
        $(document).ready(function() {
            var shopName = document.getElementById("shopName");
            $("#shopName").live("keyup", function(e) {
                if (!checkKeyUp(this, e)) {
                    return;
                }

                if (shopName.value == '' || shopName.value == null) {
                    $("#div_shopName").css({'display':'none'});
                }
                else {
                    shopName.value = shopName.value.replace(/[\ |\\]/g, "");
                    $.ajax({
                                type:"POST",
                                url:"print.do?method=getShopNameByName",
                                async:true,
                                data:{
                                    name:shopName.value,
                                    now:new Date()
                                },
                                cache:false,
                                dataType:"json",
                                error:function(XMLHttpRequest, error, errorThrown) {
                                    $("#div_shopName").css({'display':'none'});
                                },
                                success:function(jsonStr) {
                                    ajaxStyleShopName(shopName, jsonStr);
                                }
                            }
                    );
                }
            });

            $("#shopName").bind("blur",function(){
                if(isout)
                {
                    $("#div_shopName").css({'display':'none'});
                }
            });

            $("#templateName").bind("change", function(){
                $("#displayName").val($(this).find("option:selected").text());
            });
        });
        var isout = true;


        var lastvalue;
        function checkKeyUp(domObj, domEvent) {
            var e = domEvent || event;
            var eventKeyCode = e.which || e.keyCode;
            if (eventKeyCode == 38 || eventKeyCode == 40) {
                return false;
            } else {
                var domvalue = domObj.value;
                if (domvalue != lastvalue) {
                    lastvalue = domvalue;
                    return true;
                } else {
                    return false;
                }
            }
        }

        function ajaxStyleShopName(domObject, jsonStr) {
            var offset = $(domObject).offset();
            var offsetHeight = $(domObject).height();
            var offsetWidth = $(domObject).width();
            domTitle = domObject.name;
            var x = getX(domObject);
            var y = getY(domObject);
            selectmore = jsonStr.length;
            if (selectmore <= 0) {
                $("#div_shopName").css({'display':'none'});
            }
            else {
                $("#div_shopName").css({
                    'display':'block','position':'absolute',
                    'left':x + 'px',
                    'top':y + offsetHeight + 8 + 'px'
                });
                $("#Scroller-Container_shopName").html("");
                var id = "";
                for (var i = 0; i < (jsonStr.length>10?10:jsonStr.length); i++) {
                    id = jsonStr[i].id;
                    var a = $("<a id="+id+"></a>");
                    a.html(jsonStr[i].name + "   " + jsonStr[i].mobile + "<br>");

                    $(a).bind("mouseover", function() {
                        isout = false;
                        $("#Scroller-Container_shopName > a").removeAttr("class");
                        $(this).attr("class", "hover");
                        selectValue = jsonStr[$("#Scroller-Container_shopName > a").index($(this)[0])].name;// $(this).html();
                        selectItemNum = parseInt(this.id.substring(10));
                    });

                    $(a).bind("mouseout", function(event) {
                        isout = true;
                        selectValue="";
                    });

                    $(a).click(function() {
                        var sty = this.id;
                        $(domObject).val(selectValue = jsonStr[$("#Scroller-Container_shopName > a").index($(this)[0])].name); //取的第一字符串
                        $("#shopId").val(sty);
                        selectItemNum = -1;
                        $("#div_shopName").css({'display':'none'});
                    });

                    $("#Scroller-Container_shopName").append(a);
                }
            }
        }



        function getX(elem) {
            var x = 0;
            while (elem) {
                x = x + elem.offsetLeft;
                elem = elem.offsetParent;
            }
            return x;
        }
        function getY(elem) {
            var y = 0;
            while (elem) {
                y = y + elem.offsetTop;
                elem = elem.offsetParent;
            }
            return y;
        }
        function thisformsubmit() {
            if($.trim($("#displayName").val()) ==''){
                alert("显示名称不能为空！");
                return;
            }

            $("#thisform").ajaxSubmit(function(data) {
                $("#shopId").val("");
                if (-1 != data.indexOf("pre")) {
                    data = data.substring(5, data.length - 6);
                }
                alert(data);
            });
        }
     $(document).ready(function(){
        $.ajax({
            type:"POST",
            url:"print.do?method=getTemplateNameByType",
            async:true,
            data:{
                type:$("#orderType").val(),
                now:new Date()
            },
            cache:false,
            dataType:"json",
            success:function(jsonStr) {
                if(jsonStr.length>0)
                {
                    var html="";
                    for(var i=0;i<jsonStr.length;i++)
                    {
                        html = html + '<option width = "25" value="'+jsonStr[i].idStr+'">'+jsonStr[i].name+'</option>';
                    }
                    $("#templateName").append(html);
                }
            }
        });
     });

     function changeTemplateNames()
     {
         $.ajax({
             type:"POST",
             url:"print.do?method=getTemplateNameByType",
             async:true,
             data:{
                 type:$("#orderType").val(),
                  now:new Date()
             },
             cache:false,
             dataType:"json",
             success:function(jsonStr) {
                 if(jsonStr.length>0)
                 {
                     var html="";
                     for(var i=0;i<jsonStr.length;i++)
                     {
                         html = html + '<option width = "25" value="'+jsonStr[i].idStr+'">'+jsonStr[i].name+'</option>';
                     }
                     $("#templateName").html("");
                     $("#templateName").append(html);
                     $("#templateName").change();
                 }
                 else
                 {
                     $("#templateName").html("");
                     $("#templateName").change();
                 }
             }
         });
     }


    </script>
</head>
<body>
<div class="main">
    <!--头部-->
    <div class="top">
        <div class="top_left">
            <div class="top_name">统购后台管理系统</div>
            <div class="top_image"></div>
            你好，<span>张三</span>|<a href="j_spring_security_logout">退出</a></div>
        <div class="top_right"><span>2011.11.23 14:01 星期三</span></div>
    </div>
    <!--头部结束-->
    <div class="body">
        <!--左侧列表-->
        <%@include file="/WEB-INF/views/left.jsp" %>
        <!--左侧列表结束-->
        <!--右侧内容-->
        <div class="bodyRight">
            <!--搜索-->
            <div class="rightText">
                <div class="textLeft"></div>
                <div class="textBody"><input type="text" value="店铺名" id="txt_shopName"/></div>
                <div class="textRight"></div>
            </div>
            <div class="rightText">
                <div class="textLeft"></div>
                <div class="textBody"><input type="text" value="店主" id="txt_shopOwner"/></div>
                <div class="textRight"></div>
            </div>
            <div class="rightText">
                <div class="textLeft"></div>
                <div class="textAddressbody"><input type="text" value="地址" id="txt_address"/></div>
                <div class="textRight"></div>
            </div>
            <div class="rightText">
                <div class="textLeft"></div>
                <div class="textBody"><input type="text" value="手机/电话" id="txt_phone"/></div>
                <div class="textRight"></div>
            </div>
            <input type="button" class="rightSearch" value="搜 索"/>
            <!--搜索结束-->
            <!--内容-->
            <div class="rightMain clear">
                <div class="rightTitle">
                    <div class="rightLeft"></div>
                    <div class="rightBody">
                        <div class="title"><a href="print.do?method=toLeadPage">模板管理</a></div>
                        <div class="titleHover"><a href="">模板关联</a></div>
                    </div>
                </div>
                <div class="fileInfo">
                    <form:form action="print.do?method=ShopRelevanceTemplate" method="post" commandName="print"
                               id="thisform" name="thisform">

                        <label>上传店面名称</label><br>

                        <input style="margin-left: 0px;" autocomplete="off" type="text" id="shopName"
                               name="shopName"/>&nbsp;<img src="images/star.jpg"/>
                        <br><br>
                        <label>选择模板类型</label><br>

                        <form:select path="orderType" onchange="changeTemplateNames()">
                            <form:options items="${printTemplateMap}"/>
                        </form:select>
                        <br> <br>
                        <label>选择模板名称</label><br>
                        <select id="templateName" style="width:150px;" name="templateId">

                        </select>
                        <br> <br>
                        <label>输入显示名称</label><br>
                        <input type="text" id="displayName" name="displayName" />&nbsp;<img src="images/star.jpg"/>
                        <br/><br/>
                        <input type="text" style="display:none">
                        <input style="margin-left: 15px" type="button" value="建立关联" onclick="thisformsubmit()"/><br><br>
                        <input type="hidden" id="shopId" name="shopId">
                    </form:form>
                </div>
                <!--内容结束-->
            </div>
            <!--内容结束-->
            <!--圆角-->
            <div class="bottom_crile clear">
                <div class="crile"></div>
                <div class="bottom_x"></div>
                <div style="clear:both;"></div>
            </div>
            <!--圆角结束-->
        </div>
        <!--右侧内容结束-->
    </div>
</div>
<!-- 店面 -->
<div id="div_shopName" class="i_scroll" style="display:none;width:250px;">

    <div class="Scroller-ContainerShopName" id="Scroller-Container_shopName">
    </div>

</div>
</body>
</html>