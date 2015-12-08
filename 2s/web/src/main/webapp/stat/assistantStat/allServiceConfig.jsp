                                  <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>提成配置</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css">
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css">
    <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css">
    <link rel="stylesheet" type="text/css" href="styles/assistantStatConfig<%=ConfigController.getBuildVersion()%>.css"/>

    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/txnbase<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/setConstruction<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/setService<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/stat/assistantStat/allServiceConfig<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript">
    defaultStorage.setItem(storageKey.MenuUid,"WEB.STAT.AGENT_ACHIEVEMENTS.CONFIG");
  </script>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>

<div class="i_main clear">

  <input type="hidden" id="serviceIds" name="serviceIds" value=""/>
  <input type="hidden" id="pageType" name="pageType" value="setConstructions"/>

  <div class="mainTitles">
    <div class="titleWords">提成配置</div>

  </div>
  <div class="titBodys">
    <a class="hover_btn" href="assistantStat.do?method=redirectSalesManConfig">施工提成</a>
    <a class="normal_btn" href="assistantStat.do?method=searchSalesManConfig">员工部门</a>
    <a class="normal_btn" href="assistantStat.do?method=redirectProductConfig">销售提成</a>
  </div>

<div class="titBody">

    <div class="group_list">
      <span style="cursor:pointer;" id="totalNumSpan">项目总数&nbsp;<b class="blue_color">${totalNum}</b>&nbsp;</span>
        <span style="cursor:pointer;" id="totalConfigNumSpan">未配置提成&nbsp;<b class="blue_color"><span
            id="totalConfigNum">${totalShopAchievementConfig}</span></b>&nbsp;</span>

      <strong class="fr"><a href="#" onclick="achievementNotify()" class="yellow_color">进入业绩统计></a></strong>
    </div>
    <div class="clear i_height"></div>

    <div class="lineTop"></div>
    <div class="lineBody set_lineBody">
        <div class="divTit">施工项目&nbsp;
            <input type="text" class="txt txt_color" style="width:300px;" autocomplete="off" id="serviceName" />
        </div>
        <div class="divTit">营业分类&nbsp;
            <input type="text" class="txt txt_color" style="width:200px;" autocomplete="off" id="categoryName"/>
        </div>
        <div class="divTit">类型&nbsp;
            <select class="txt txt_color" id="categoryServiceType">
                <option value="">全部</option>
                <option value="HAS_CATEGORY_SERVICE">已分类</option>
                <option value="NO_CATEGORY_SERVICE">未分类</option>
            </select>
        </div>
        <div class="divTit button_conditon">
            <a class="button" onclick="doSearch()">查&nbsp;询</a>
        </div>
    </div>
    <div class="lineBottom"></div>
</div>

<div class="cuSearch">
    <div class="cartTop"></div>
    <div class="cartBody">
        <table class="tab_cuSearch" cellpadding="0" cellspacing="0" id="tb_construction">
            <col>
            <col width="150">
            <col width="100">
            <col width="100">
            <col width="150">
            <col width="80">
            <col width="80">
            <tr class="titleBg">
              <td style="padding-left:10px;">施工内容</td>

              <bcgogo:permission>
                <bcgogo:if permissions="WEB.SET_THE_BUSINESS_CLASSIFICATION">
                  <td><a class="btnTitle" onclick="setConstruction();">营业分类</a></td>
                </bcgogo:if>
                <bcgogo:else>
                  <td>营业分类</td>
                </bcgogo:else>
              </bcgogo:permission>


              <td>标准工时</td>
              <td>工时单价</td>
              <td>提成类型</td>
              <bcgogo:permission>
                <bcgogo:if permissions="WEB.SET_THE_BUSINESS_CLASSIFICATION">
                  <td><a class="btnTitle" id="setAchievement">提成</a></td>
                </bcgogo:if>
                <bcgogo:else>
                  <td>提成</td>
                </bcgogo:else>
              </bcgogo:permission>

              <td>操作</td>
            </tr>
            <tr class="space">
                <td colspan="7"></td>
            </tr>
            <tr class="titBody_Bg ">
                <td colspan="7">数据加载中...</td>
            </tr>
            <tr class="titBottom_Bg J_itemBottom">
                <td colspan="7"></td>
            </tr>
        </table>
        <div class="clear i_height"></div>
        <bcgogo:ajaxPaging url="category.do?method=getCategoryItemSearch" dynamical="setConstructions" postFn="initServiceItem"></bcgogo:ajaxPaging>
    </div>
    <div class="cartBottom"></div>
</div>
</div>


<div class="height"></div>
<div class="divTit di_an" id="button">
  <a class="button bigButton" href="#" onclick="achievementNotify()">进入业绩统计</a>
</div>

<div id="div_service" class="i_scroll_serviceName" style="display:none;width:300px;">
    <div class="Scroller-Container_service" id="Scroller-Container_ServiceName"></div>
</div>

<div id="systemDialog"></div>
<div id="addNewServiceDialog" class="alertMain" style="display: none">
        <div class="height"></div>
        <div class="divTit alert_divTit">施工内容&nbsp;<input type="text" class="txt" style="width:120px;" id="newServiceName"/></div>
        <div class="divTit alert_divTit">营业分类&nbsp;<input type="text" class="txt" style="width:115px;" id="newCategoryName"/></div>
        <div class="divTit alert_divTit">标准工时&nbsp;<input type="text" class="txt" id="newStandardHours"/></div>
        <div class="divTit alert_divTit">工时单价&nbsp;<input type="text" class="txt" style="width:120px;" id="newStandardUnitPrice"/></div>
        <div class="divTit alert_divTit">提成类型&nbsp;<select class="txt" style="width:121px;" id="newAchievementType">
                <option value="AMOUNT">按金额</option>
                <option value="RATIO">按比率</option>
            </select></div>
        <div class="divTit alert_divTit">提&nbsp;&nbsp;&nbsp;&nbsp;成&nbsp;<input type="text" class="txt" id="achievementAmount"/></div>
        <div class="height"></div>
        <div class="button"><a class="btnSure" id="saveNewService">确&nbsp;定</a><a class="btnSure" id="cancelSaveNewService">取&nbsp;消</a></div>
</div>



<div id="setAchievementDialog" class="alertMain" style="display: none">
  <div class="height"></div>
  <div class="divTit alert_divTit">提成类型&nbsp;<select class="txt" style="width:121px;" id="setAchievementType">
    <option value="AMOUNT">按金额</option>
    <option value="RATIO">按比率</option>
  </select></div>
  <div class="divTit alert_divTit">提&nbsp;&nbsp;&nbsp;&nbsp;成&nbsp;<input type="text" class="txt"
                                                                          id="setAchievementAmount"/></div>
  <div class="height"></div>
  <div class="button">
    <a class="btnSure" id="setAchievementBtn">确&nbsp;定</a>
    <a class="btnSure" id="setAchievementCancelBtn">取&nbsp;消</a>
  </div>
</div>



<iframe id="iframe_PopupBox_setCategory" style="position:absolute;z-index:5; left:400px; top:400px; display:none;"
        allowtransparency="true" width="550px" height="300px" frameborder="0" src="" scrolling="no"></iframe>

<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>