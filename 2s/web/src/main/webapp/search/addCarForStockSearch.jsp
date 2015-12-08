<%@ page import="com.bcgogo.config.ConfigController" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%--
  Created by IntelliJ IDEA.
  User: wjl
  Date: 11-9-30
  Time: 上午9:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>新增车型</title>
    <%
        String webapp = request.getContextPath();
    %>
</head>
<link rel="stylesheet" type="text/css" href="<%=webapp%>/styles/style<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="<%=webapp%>/styles/up<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css"
      href="<%=webapp%>/styles/moreUserinfo<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="<%=webapp%>/styles/cuDetail<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="<%=webapp%>/styles/addCar<%=ConfigController.getBuildVersion()%>.css"/>
<script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="js/addCarSolr<%=ConfigController.getBuildVersion()%>.js"></script>
<%--<script type="text/javascript" src="js/dragiframe<%=ConfigController.getBuildVersion()%>.js"></script>--%>
<style type="text/css">
    .Scroller-Container a {
        height: 20px;
        line-height: 20px;
        padding-left: 5px;
        color: white;
        white-space: nowrap;
    }
</style>
<script type="text/javascript">
     $(function() {
        jQuery(".addcaritem:last img").removeAttr("style");
        window.parent.document.getElementById("iframe_PopupBox_2").style.display = "block";
        document.getElementById("div_close").onclick = closeWindow;
        document.getElementById("button_02").onclick = closeWindow;
//        window.parent.addHandle(document.getElementById('div_drag'), window);
          s
        jQuery(document).click(function(e) {
            var e = e || event;
            var target = e.srcElement || e.target;
            if (target.id != "div_brand") {
                $("#div_brand")[0].style.display = "none";
            }
        });
    });
    function closeWindow() {
        window.parent.document.getElementById("mask").style.display = "none";
        if (window.parent.document.getElementById("iframe_PopupBox_2") != null) {
            window.parent.document.getElementById("iframe_PopupBox_2").style.display = "none";
            window.parent.document.getElementById("iframe_PopupBox_2").src = "";
        }
        if (window.parent.document.getElementById("iframe_PopupBox") != null) {
            window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
            window.parent.document.getElementById("iframe_PopupBox").src = "";
        }
        try {
            $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
        } catch(e) {
            ;
    }
    }
    function addrow() {
        var trnum = jQuery(".addcaritem").size();
        var pvBrand = jQuery("#" + (trnum - 1) + "_pvBrand").val();
        var pvModel = jQuery("#" + (trnum - 1) + "_pvModel").val();
        var pvYear = jQuery("#" + (trnum - 1) + "_pvYear").val();
        var pvEngine = jQuery("#" + (trnum - 1) + "_pvEngine").val();
        if (pvBrand == "" || pvModel == "") {
            return;
        }
        var trObj = jQuery(".addcaritem:last").clone(true);
        jQuery(trObj).find("td").first().html(trnum + 1);
        jQuery(trObj).find("input").each(function() {
            var inputid = jQuery(this).attr("id");
            this.id = (trnum) + "_" + inputid.split("_")[1];
        });
        jQuery(".addcaritem:last img:eq(0)").css({'display':'none'});
        jQuery(".addcaritem:last").after(trObj);
        jQuery(".addcaritem:last input").val("");
    }
    function addVehicleForThisProduct() {
        var trnum = jQuery(".addcaritem").size();
        var pvBrand = jQuery("#" + (trnum - 1) + "_pvBrand").val();
        var pvModel = jQuery("#" + (trnum - 1) + "_pvModel").val();
        var pvYear = jQuery("#" + (trnum - 1) + "_pvYear").val();
        var pvEngine = jQuery("#" + (trnum - 1) + "_pvEngine").val();
        if (pvBrand == "" || pvModel == "") {
            alert("请为最后一行输入完整的车型信息!");
            return;
        }
        var isAdd = confirm("是否确定为此商品添加所选车型!");
        if (!isAdd) {
            return;
        }
        var trnum = jQuery(".addcaritem").size();
        var o = "";
        for (var e = 0; e < trnum; e++) {
            if (jQuery("#" + e + "_pvBrand").val() == "" || jQuery("#" + e + "_pvModel").val() == "") {
                continue;
            }
            o = o + jQuery("#" + e + "_productId").val() + "," + jQuery("#" + e + "_pvBrand").val() + "," +
                    jQuery("#" + e + "_pvModel").val() + "," + jQuery("#" + e + "_pvYear").val() + "," +
                    jQuery("#" + e + "_pvEngine").val() + ";";
        }
        jQuery.ajax({
                    type:"post",
                    url:"searchInventoryIndex.do?method=addVehicleForThisProduct",
                    async:true,
                    data:{
                        idvalues:o
                    },
                    cache:false,
                    dataType:"json",
                    success:function(jsonStr) {
                        closeWindow();
                    }
                }
        );
    }
</script>
<body>
<div id="div_show" class="i_searchBrand">
    <div class="i_arrow"></div>
    <div class="i_upCenter">
        <div class="i_note" id="div_drag">新增车型</div>
        <div class="i_close" id="div_close"></div>
    </div>
    <div class="i_upRight"></div>
    <div class="i_upBody clear">
        <table cellpadding="0" cellspacing="0" class="table2">
            <col width="40">
            <col width="95">
            <col width="80">
            <col/>
            <col width="75">
            <col width="45">
            <tr class="table_title">
                <td style="border-left:none;">No</td>
                <td>车辆品牌<input type="button" class="tab_arrow" onfocus="this.blur();"/></td>
                <td>车型<input type="button" class="tab_arrow" onfocus="this.blur();"/></td>
                <td>年代<input type="button" class="tab_arrow" onfocus="this.blur();"/></td>
                <td>排量<input type="button" class="tab_arrow" onfocus="this.blur();"/></td>
                <td style="border-right:none;">操作</td>
            </tr>
            <c:if test="${pvDTOList != null}">
                <c:forEach items="${pvDTOList}" var="productVehicleDTO" varStatus="status">
                    <tr class="addcaritem">
                        <td style="border-left:none;">${status.index+1}</td>
                        <td class="font"><input id="${status.index}_pvBrand" type="text"
                                                value="${productVehicleDTO.pvBrand}"
                                                onkeyup="searchVehicleWithAjax(this,'brand',parseInt(this.id.split('_')[0]),'notClick')"
                                                onclick="searchVehicleWithAjax(this,'brand',parseInt(this.id.split('_')[0]),'click')"/>
                        </td>

                        <td><input type="text" id="${status.index}_pvModel" value="${productVehicleDTO.pvModel}"
                                   onkeyup="searchVehicleWithAjax(this,'model',parseInt(this.id.split('_')[0]),'notClick')"
                                   onclick="searchVehicleWithAjax(this,'model',parseInt(this.id.split('_')[0]),'click')"/>
                        </td>
                        <td><input type="text" id="${status.index}_pvYear" value="${productVehicleDTO.pvYear}"
                                   onkeyup="searchVehicleWithAjax(this,'year',parseInt(this.id.split('_')[0]),'notClick')"
                                   onclick="searchVehicleWithAjax(this,'year',parseInt(this.id.split('_')[0]),'click')"/>
                        </td>
                        <td><input type="text" id="${status.index}_pvEngine" value="${productVehicleDTO.pvEngine}"
                                   onkeyup="searchVehicleWithAjax(this,'engine',parseInt(this.id.split('_')[0]),'notClick')"
                                   onclick="searchVehicleWithAjax(this,'engine',parseInt(this.id.split('_')[0]),'click')"/>
                        </td>
                        <td style="border-right:none;"><img src="<%=webapp%>/images/opera2.jpg" style="display: none"
                                                            onclick="addrow()"/>
                        </td>
                        <td style="display: none"><input id="${status.index}_productId" type="hidden"
                                                         value="${productVehicleDTO.productId}"/></td>
                        <td style="display: none"><input id="${status.index}_brandId" type="hidden"
                                                         value="${productVehicleDTO.brandId}"/></td>
                        <td style="display: none"><input id="${status.index}_modelId" type="hidden"
                                                         value="${productVehicleDTO.modelId}"/></td>
                        <td style="display: none"><input id="${status.index}_yearId" type="hidden"
                                                         value="${productVehicleDTO.yearId}"/></td>
                        <td style="display: none"><input id="${status.index}_engineId" type="hidden"
                                                         value="${productVehicleDTO.engineId}"/></td>
                    </tr>
                </c:forEach>
            </c:if>
        </table>
        <div class="more_his">
            <input type="button" value="确认" onfocus="this.blur();" class="btn" onclick="addVehicleForThisProduct()"/>
            <input type="button" value="取消" onfocus="this.blur();" class="btn" id="button_02"/>
        </div>
    </div>
    <div class="i_upBottom clear">
        <div class="i_upBottomLeft"></div>
        <div class="i_upBottomCenter"></div>
        <div class="i_upBottomRight"></div>
    </div>
</div>
<div id="div_brand" class="i_scroll" style="display:none;height:230px">
    <div class="Container" style="height:230px;">
        <div id="Scroller-1" style="height:225px;">
            <div class="Scroller-Container" id="Scroller-Container_id">
            </div>
        </div>
    </div>

</div>
<iframe id="iframe_AddCarPopupBox" style="position:absolute;z-index:7; left:0px; top:140px; display:none;"
        allowtransparency="true" width="930px" height="450px" frameborder="0" src=""></iframe>
</body>
</html>