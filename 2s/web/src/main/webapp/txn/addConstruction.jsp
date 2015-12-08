<%@ page import="com.bcgogo.config.ConfigController" %>
<%--
  Created by IntelliJ IDEA.
  User: zhoudongming
  Date: 12-7-13
  Time: 下午2:34
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>新增施工内容</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css">
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moneyDetail<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/memberSet<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" href="js/extension/jquery/plugin/jquery-tooltip/jquery.tooltip.css"/>
    <link rel="stylesheet" href="js/extension/jquery/plugin/tipsy-master/src/stylesheets/tipsy.css"/>
    <link rel="stylesheet" href="js/components/themes/bcgogo-droplist<%=ConfigController.getBuildVersion()%>.css"/>

    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-tooltip/jquery.tooltip<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/tipsy-master/src/javascripts/jquery.tipsy<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/mask<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/txnbase<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/setConstruction<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-highlightcomplete<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript"
            src="js/components/ui/bcgogo-droplist<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript"
            src="js/components/ui/bcgogo-droplist-lite<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        jQuery(document).ready(function() {
            jQuery("#div_close,#cancelBtn").click(function() {
                window.parent.document.getElementById("mask").style.display = "none";
                window.parent.document.getElementById("iframe_PopupBox").style.display = "none";
                window.parent.document.getElementById("iframe_PopupBox").src = "";

                try {
                    $(window.parent.document.body).find("input[type='button']").eq(0).focus().blur();
                } catch(e) {
                    ;
                }
            });

            jQuery("#cancelBtn").bind("click", function(e) {
                jQuery("#categoryName").val("");
                jQuery("#serviceName").val("");
                jQuery("#price").val("");
                jQuery("#percentageAmount").val("");
            });
            jQuery("#saveBtn").bind("click", function(e) {
                if (jQuery("#serviceName").val() == "") {
                    nsDialog.jAlert("施工内容不能为空");
                    return false;
                }
                var serviceExist = false;
                var url = "category.do?method=checkServiceNameRepeat";
                APP_BCGOGO.Net.syncAjax({url:url,dataType:"json",data:{serviceName:jQuery("#serviceName").val(),insert:"add"},success:function(data) {
                    if ("error" == data.resu) {
                        nsDialog.jAlert("服务名已存在");
                        serviceExist = true;
                    }else if("inUse" == data.resu){
                        nsDialog.jAlert("服务正在被进行中的单据使用，无法修改");
                        serviceExist = true;
                    }
                }});

                if (serviceExist)
                    return false;

                if (isNaN(jQuery("#price").val()) || jQuery("#price").val() < 0) {
                    return false;
                }
                if (isNaN(jQuery("#percentageAmount").val()) || jQuery("#percentageAmount").val() < 0) {
                    return false;
                }
                var formName = document.getElementById("thisform");
                formName.action = "category.do?method=addNewService";
                formName.target = "_parent";
                formName.submit();
            });

            $("#price,#percentageAmount").keyup(function() {
                if (jQuery(this).val() != APP_BCGOGO.StringFilter.inputtingPriceFilter(this.value, 2)) {
                    jQuery(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter(this.value, 2));
                }
            });

            $("#categoryName").live("keyup", function() {
                $(this).val(APP_BCGOGO.StringFilter.inputtingProductNameFilter(this.value));
            });

            jQuery(document).click(function(e) {
                var e = e || event;
                var target = e.srcElement || e.target;
                if (target.id != "categoryDisplay" && target.id != "categoryName") {
                    jQuery("#categoryDisplay").hide();
                }
            });
        });


    </script>


</head>
<body>
<div class="i_searchBrand i_searchBrand-washBeautyAccount">
    <div class="i_arrow"></div>
    <div class="i_upLeft"></div>
    <div class="i_upCenter">
        <div class="i_note" id="div_drag">新增施工内容</div>
        <div class="i_close" id="div_close"></div>
    </div>
    <div class="i_upRight"></div>
    <form:form action="" id="thisform" commandName="categoryServiceSearchDTO" name="addConstructionForm" method="post">
        <div style="display:none">
            <select name="allServiceDTOs">
                <c:forEach items="${categoryServiceSearchDTO.serviceDTOs}" var="service">
                    <option value="${service.name}"></option>
                </c:forEach>
            </select>
        </div>
        <div class="i_upBody">
            <table cellpadding="0" cellspacing="0" class="table2">
                <col/>
                <col width="100"/>
                <col width="40"/>
                <col width="75"/>
                <col width="60"/>
                <tr>
                    <td>施工内容</td>
                    <td>营业归类</td>
                    <td>工时</td>
                    <td>金额/工时费</td>
                    <td>员工提成</td>
                </tr>
                <tr>
                    <td><form:input class="table_input j_no_suggestion" path="serviceName" onblur="setCategory(this)"
                                    value="${categoryServiceSearchDTO.serviceName}" maxlength="60"
                                    autocomplete="off"/></td>
                    <td><form:input class="table_input" path="categoryName"
                                    value="${categoryServiceSearchDTO.categoryName}" maxlength="16" autocomplete="off"/>
                    </td>
                    <td></td>
                    <td><form:input class="table_input" path="price" value="${categoryServiceSearchDTO.price}"
                                    maxlength="10" autocomplete="off"/></td>
                    <td><form:input class="table_input" path="percentageAmount"
                                    value="${categoryServiceSearchDTO.percentageAmount}" maxlength="10"
                                    autocomplete="off"/></td>
                </tr>
            </table>
            <div class="clear height"></div>
            <div class="btnInput">
                <input id="saveBtn" type="button" onfocus="this.blur();" value="确认">
                <input id="cancelBtn" type="button" onfocus="this.blur();" value="取消">
            </div>
        </div>
    </form:form>
    <div class="i_upBottom">
        <div class="i_upBottomLeft"></div>
        <div class="i_upBottomCenter"></div>
        <div class="i_upBottomRight"></div>
    </div>
</div>
</body>
</html>