<%@ page import="com.bcgogo.config.ConfigController" %>
<%--
  Created by IntelliJ IDEA.
  User: lw
  Date: 12-10-16
  Time: 下午2:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <title>设定营业分类</title>
  <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css">
  <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/moneyDetail<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/memberSet<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
  <link rel="stylesheet" href="js/extension/jquery/plugin/jquery-tooltip/jquery.tooltip.css"/>
  <link rel="stylesheet" href="js/extension/jquery/plugin/tipsy-master/src/stylesheets/tipsy.css"/>
  <link rel="stylesheet"
        href="js/components/themes/bcgogo-detailsListPanel<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" href="js/components/themes/bcgogo-droplist<%=ConfigController.getBuildVersion()%>.css"/>

  <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
  <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
  <script type="text/javascript"
          src="js/extension/jquery/plugin/jquery-tooltip/jquery.tooltip<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript"
          src="js/extension/jquery/plugin/tipsy-master/src/javascripts/jquery.tipsy<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/mask<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/txnbase<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/setConstruction<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript"
          src="js/components/ui/bcgogo-droplist<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript"
          src="js/components/ui/bcgogo-droplist-lite<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript"
          src="js/components/ui/bcgogo-highlightcomplete<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/module/ajaxQuery<%=ConfigController.getBuildVersion()%>.js"></script>

  <script type="text/javascript">
    jQuery(document).ready(function() {
      jQuery("#div_close,#cancelBtn").click(function() {
        window.parent.document.getElementById("mask").style.display = "none";
        window.parent.document.getElementById("iframe_PopupBox_setCategory").style.display = "none";
        window.parent.document.getElementById("iframe_PopupBox_setCategory").src = "";

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
        if (jQuery("#categoryName").val() == "") {
          if (!confirm("确定批量修改此页所有施工项目的营业分类为空")) {
            return;
          }
        }


        if ($("#serviceIds", window.parent.document).val() == "" || $("#serviceIds", window.parent.document).val() == null) {
          return;
        }
        var bcgogoAjaxQuery = APP_BCGOGO.Module.wjl.ajaxQuery;

        var data = {
          ids: $("#serviceIds", window.parent.document).val(),
          categoryName:  $("#categoryName").val()
        }
        var url = "category.do?method=batchUpdateServiceCategory";

        bcgogoAjaxQuery.setUrlData(url, data);
        bcgogoAjaxQuery.ajaxQuery(function (jsonStr) {
          if (jsonStr && jsonStr.success) {
            $(".categoryNameSpan", window.parent.document).text($("#categoryName").val());
          }
          $("#div_close").click();
        });


      });

      $("#price,#percentageAmount").keyup(function() {
        if (jQuery(this).val() != APP_BCGOGO.StringFilter.inputtingPriceFilter(this.value, 2)) {
          jQuery(this).val(APP_BCGOGO.StringFilter.inputtingPriceFilter(this.value, 2));
        }
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
    <div class="i_note" id="div_drag">设定营业分类</div>
    <div class="i_close" id="div_close"></div>
  </div>
  <div class="i_upRight"></div>
  <div style="display:none">
    <select name="allServiceDTOs">
      <c:forEach items="${categoryServiceSearchDTO.serviceDTOs}" var="service">
        <option value="${service.name}"></option>
      </c:forEach>
    </select>
  </div>
  <div class="i_upBody">
    <table cellpadding="0" cellspacing="0" class="table2">
      <col width="40"/>
      <col width="150"/>
      <tr>
        <td>营业分类:</td>
        <td><input class="table_input" id="categoryName" maxlength="16" autocomplete="off"/>
          <input type="hidden" id="categoryId"/>

          <div id="categoryDisplay" class="i_scroll_category"
               style="height:200px;overflow-x:hidden;overflow-y:scroll;">
            <c:forEach items="${categoryServiceSearchDTO.categoryDTOs}" var="category"
                       varStatus="cateStatus">

              <div class="i_service_item" onmouseover="divMouseOver(this)"
                   onmouseout="divMouseOut(this)"
                   onclick="doCategoryValueSet(this,'${category.id}')">${category.categoryName}
              </div>
            </c:forEach>
          </div>
        </td>

      </tr>
    </table>
    <div class="clear height"></div>
    <div class="btnInput">
      <input id="saveBtn" type="button" onfocus="this.blur();" value="确认">
      <input id="cancelBtn" type="button" onfocus="this.blur();" value="取消">
    </div>
  </div>
  <div class="i_upBottom">
    <div class="i_upBottomLeft"></div>
    <div class="i_upBottomCenter"></div>
    <div class="i_upBottomRight"></div>
  </div>
</div>
</body>
</html>