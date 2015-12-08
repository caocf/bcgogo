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
<%@include file="/WEB-INF/views/style-thirdpartLibs.jsp" %>

<%-- scripts --%>
<%@include file="/WEB-INF/views/script-thirdpartLibs.jsp" %>
<script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.js"></script>
<script type="text/javascript" src="js/uploadPreview.js"></script>
<%@include file="/WEB-INF/views/script-common.jsp" %>
<script type="text/javascript" src="js/searchDefault.js"></script>

<script type="text/javascript">
$(document).ready(function () {
    var shopName = $("#shopName")[0];
    $("#shopName").live("keyup", function (e) {
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
                        error:function (XMLHttpRequest, error, errorThrown) {
                            $("#div_shopName").css({'display':'none'});
                        },
                        success:function (jsonStr) {
                            ajaxStyleShopName(shopName, jsonStr);
                        }
                    }
            );
        }
    });

    $("#shopName").bind("blur", function () {
        if (isout) {
            $("#div_shopName").css({'display':'none'});
        }
        if ($(this).val() == "") {
            $("#shopName").val("默认模板");
            $("#uploadShopName").text("默认模板");
            $("#shopId").val(-1);
            $("#uploadShopId").val(-1);
        }
    });

    $("#templateName").live("blur", function () {
        if ($("#templateName").val() == "") {
            alert("模板名称不能为空");
            return false;
        }
        if($("#displayName").val()==""){
            $("#displayName").val($(this).val());
        }
        var name = $("#templateName").val();
        var shopId = $("#uploadShopId").val();
        var isRepeate = false;

        $.ajax({
            type:"POST",
            url:"print.do?method=getTemplateNameByName",
            async:false,
            data:{
                name:name,
                shopId: shopId,
                tsLog:10000000000 * (1 + Math.random())
            },
            cache:false,
            dataType:"json",
            success:function (data) {
                var tmp = data[0]['resu'];
                if (tmp == "error") {
                    isRepeate = true;
                }
            }
        });
        if(isRepeate){
            alert("此模板名称已被占用，请重新输入");
        }
    });

    $("#thisform #orderType").bind("change", function(){
        if($(this).val()=="SALE" || $(this).val() == "REPAIR"){
            $("#hint").show();
        }else{
            $("#hint").hide();
        }
    })
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

//function checkTemplateName() {
//    var name = $("#templateName").val();
//    var shopId = $("#uploadShopId").val();
//    $.ajax({
//        type:"POST",
//        url:"print.do?method=getTemplateNameByName",
//        async:false,
//        data:{
//            name:name,
//            shopId: shopId,
//            tsLog:10000000000 * (1 + Math.random())
//        },
//        cache:false,
//        dataType:"json",
//        success:function (data) {
//            var tmp = data[0]['resu'];
//            if (tmp == "error") {
//                alert("此模板名称已被占用，请重新输入");
//                $("#templateName").focus();
//            }
//        }
//    });
//}


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
            'display':'block', 'position':'absolute',
            'left':x + 'px',
            'top':y + offsetHeight + 8 + 'px'
        });
        $("#Scroller-Container_shopName").html("");

        for (var i = 0; i < (jsonStr.length > 10 ? 10 : jsonStr.length); i++) {
            var id = jsonStr[i].id;
            var a = $("<a id=" + id + "></a>");
            a.html(jsonStr[i].name + "   " + jsonStr[i].mobile + "<br>");
            $(a).bind("mouseover", function () {
                isout = false;
                $("#Scroller-Container_shopName > a").removeAttr("class");
                $(this).attr("class", "hover");
                selectValue = jsonStr[$("#Scroller-Container_shopName > a").index($(this)[0])].name;// $(this).html();
                selectItemNum = parseInt(this.id.substring(10));
            });

            $(a).bind("mouseout", function (event) {
                isout = true;
                selectValue = "";
            });

            $(a).click(function () {
                var sty = this.id;
                $(domObject).val(selectValue = jsonStr[$("#Scroller-Container_shopName > a").index($(this)[0])].name); //取的第一字符串
                $("#uploadShopName").text($(domObject).val());
                $("#shopId").val(sty);
                $("#uploadShopId").val(sty);
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
    if ($("#templateName").val() == "") {
        alert("模板名称不能为空");
        return false;
    }
    if($("#displayName").val() == ""){
        alert("显示名称不能为空");
        return false;
    }

    var name = $("#templateName").val();
    var ischeck = false;
    var shopId = $("#uploadShopId").val();
    $.ajax({
        type:"POST",
        url:"print.do?method=getTemplateNameByName",
        async:false,
        data:{
            name:name,
            shopId:shopId,
            tsLog:10000000000 * (1 + Math.random())
        },
        cache:false,
        dataType:"json",
        success:function (data) {
            var tmp = data[0]['resu'];
            if (tmp == "error") {
                ischeck = true;
            }
        }
    });

    if (ischeck) {
        alert("此模板名称已被占用，请重新输入");
        return false;
    }

    var isJsp = $("#printFile").val().split(".")[1];

    if (isJsp != 'html' && isJsp != 'htm' && isJsp != 'HTML') {
        alert("请选择html结尾的文件");
        return false;
    }

    $("#thisform").ajaxSubmit(function (data) {
        if (-1 != data.indexOf("pre")) {
            data = data.substring(5, data.length - 6);
        }
        var jsonObj = JSON.parse(data);
        if ("notRegister" == jsonObj.resu) {
            alert("此店面没有注册");
        }
        else if ("success" == jsonObj.resu) {
            alert("上传成功");
            $("#searchForm").submit();
        }
        else if ("error" == jsonObj.resu) {
            alert("上传失败");
        }
    });
}

function deleteTemplate(shopPrintTemplateId){
    if(confirm("确认删除？")){
        $.ajax({
            type:"POST",
            url:"print.do?method=deleteTemplateByShopPrintTemplateId",
            async:false,
            data:{
                id:shopPrintTemplateId
            },
            cache:false,
            dataType:"json",
            success:function (data) {
                if(data.success){
                    alert("删除成功！");
                    $("#searchForm").submit();
                }else{
                    alert(data.msg);
                }
            }
        });
    }
}

function modifyName(shopPrintTemplateId, currName){
    var displayName = prompt('请输入新的显示名称', currName);
    if(displayName == null || $.trim(displayName)==''){
        return;
    }
    $.ajax({
        type:"POST",
        url:"print.do?method=updateDisplayNameByShopPrintTemplateId",
        async:false,
        data:{
            id:shopPrintTemplateId,
            displayName: displayName
        },
        cache:false,
        dataType:"json",
        success:function (data) {
            if (data.success) {
                alert("更新成功！");
                $("#searchForm").submit();
            } else {
                alert(data.msg);
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
            你好，<span>${sessionScope.userName}</span>|<a href="j_spring_security_logout">退出</a></div>
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
                        <div class="titleHover"><a href="print.do?method=toLeadPage">模板管理</a></div>
                        <div class="title"><a href="print.do?method=chooseOldTemplate">模板关联</a></div>
                    </div>
                </div>
                <div class="rightTime">
                    <div class="timeLeft"></div>
                    <div class="timeBody">
                        <div class="pen"></div>
                        模板管理
                    </div>
                    <div class="timeRight"></div>
                </div>
                <div class="fileInfo">
                    <form:form action="print.do?method=searchPrintTemplateByShopAndType" method="post"
                               commandName="printTemplateDTO"
                               id="searchForm" name="searchForm">
                        <label>店面搜索</label>
                        <input style="margin-left: 0px;" autocomplete="off" type="text" id="shopName"
                               name="shopName" value="${shopName}"/>&nbsp;<img src="images/star.jpg"/>
                        <input type="hidden" id="shopId" name="shopId" value="${shopId}">

                        <label>模板类型</label>
                        <form:select path="orderType">
                            <form:options items="${printTemplateMap}"/>
                        </form:select>
                        <input type="submit" value="搜索"/>
                        <c:if test="${!empty printTemplates}">
                            <table style="width:800px;">
                                <thead>
                                <tr>
                                    <th>序号</th>
                                    <th>模板名称（唯一）</th>
                                    <th>显示名称</th>
                                    <th>是否默认模板</th>
                                    <th>操作</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${printTemplates}" var="printTemplate" varStatus="status">
                                    <tr>
                                        <td>${status.index+1}</td>
                                        <td>${printTemplate.name}</td>
                                        <td>${printTemplate.displayName}</td>
                                        <td><c:if test="${printTemplate.shopId==-1}">是</c:if>
                                        <c:if test="${printTemplate.shopId!=-1}">否</c:if>
                                        </td>
                                        <td>
                                            <c:if test="${printTemplate.shopId!=-1}">
                                                <a href="javascript:deleteTemplate('${printTemplate.shopPrintTemplateId}')">删除</a>
                                            </c:if>
                                            <a href="javascript:modifyName('${printTemplate.shopPrintTemplateId}', '${printTemplate.displayName}')">修改名称</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </c:if>
                    </form:form>
                </div>
                <br><br>

                <div class="rightTime">
                    <div class="timeLeft"></div>
                    <div class="timeBody">
                        <div class="pen"></div>
                        上传新模板
                    </div>
                    <div class="timeRight"></div>
                </div>
                <div class="fileInfo" style="line-height:25px;">
                    <form:form action="print.do?method=savePrintTemplate" method="post" commandName="print"
                               id="thisform" name="thisform" enctype="multipart/form-data">
                        <label>当前店面:</label>  <span id="uploadShopName">${shopName}</span><br/><br/>

                        <input type="hidden" id="uploadShopId" name="uploadShopId" value="${shopId}" />
                        <input type="hidden" id="uploadShopName" name="uploadShopName" value="${shopName}" />
                        <label>模板类型</label>
                        <form:select path="orderType" >
                            <form:options items="${printTemplateMap}"/>
                        </form:select>&nbsp;&nbsp;<span style="color:red;display:none" id="hint">注：各店铺的销售单、施工单支持多模板，上传新模板后请及时删除废弃模板。</span><br>

                        <label>模板名称(唯一)</label>
                        <input style="margin-left: 0px;" autocomplete="off" type="text" id="templateName"
                               name="templateName"/>&nbsp;<img src="images/star.jpg"/><br/>

                        <label>显示名称</label>
                        <input style="margin-left: 0px;" autocomplete="off" type="text" id="displayName"
                               name="displayName"/>&nbsp;<img src="images/star.jpg"/><br/>
                        
                        <br>
                        <input style="margin-left: 0px" type="file" id="printFile" name="printFile"/>
                        <input type="test" style="display:none">
                        <input style="margin-left: 15px" type="button" value="上传文件" onclick="thisformsubmit()"/><br><br>
                        <%--<input type="hidden" id="shopId" name="shopId">--%>
                    </form:form>
                </div>
            </div>
            <!--内容结束-->
        </div>
        <!--内容结束-->
    </div>
    <!--右侧内容结束-->
</div>
<!-- 店面 -->
<div id="div_shopName" class="i_scroll" style="display:none;width:250px;">

    <div class="Scroller-ContainerShopName" id="Scroller-Container_shopName">
    </div>

</div>
</body>
</html>