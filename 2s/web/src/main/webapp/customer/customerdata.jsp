<%@ page import="com.bcgogo.user.service.IUserService" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%
  //手机号部分隐藏开关
  boolean mobileHiddenTag = ServiceManager.getService(IUserService.class).isMobileSwitchOn((Long) request.getSession().getAttribute("shopId"));
  //当前用户的用户组名称
  String userGroup = (String) request.getSession().getAttribute("userGroupName");
%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <title>客户管理_客户资料</title>

  <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/uncleUser<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/customData<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/carSearch<%=ConfigController.getBuildVersion()%>.css"/>
  	<link rel="stylesheet" type="text/css" href="styles/mergeCustomer<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
<link rel="stylesheet" type="text/css" href="styles/yinshouyinfu<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css"
        href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
  <link rel="stylesheet" type="text/css"
        href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addCheck<%=ConfigController.getBuildVersion()%>.css"/>
  <%@include file="/WEB-INF/views/header_script.jsp" %>
  <script type="text/javascript">
    <bcgogo:permissionParam permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_ARREARS">
    APP_BCGOGO.Permission.CustomerManager.CustomerArrears=${WEB_CUSTOMER_MANAGER_CUSTOMER_ARREARS};
    </bcgogo:permissionParam>
    <bcgogo:permissionParam resourceType="logic" permissions="WEB.VERSION.RELATION_CUSTOMER">
    APP_BCGOGO.Permission.Version.RelationCustomer=${WEB_VERSION_RELATION_CUSTOMER};
    </bcgogo:permissionParam>
    <bcgogo:permissionParam permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_UPDATE">
    APP_BCGOGO.Permission.CustomerManager.UpdateCustomer=${WEB_CUSTOMER_MANAGER_CUSTOMER_UPDATE};
    </bcgogo:permissionParam>
  </script>
  <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript"
          src="js/page/customer/customerData<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/detailsArrears<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/customer<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/customerOrSupplier/mergeCustomer<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/customerOrSupplier/myCustomer<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript">
      APP_BCGOGO.UserGuide.currentPageIncludeGuideStep = "CONTRACT_CUSTOMER_GUIDE_CUSTOMER_DATA,CONTRACT_CUSTOMER_GUIDE_RECOMMEND_CUSTOMER,CONTRACT_CUSTOMER_GUIDE_SINGLE_APPLY";
      APP_BCGOGO.UserGuide.currentPage = "customerData";
      var fromUserGuideStep = '${param.fromUserGuideStep}';
    <c:choose>
    <c:when test="${pageTip=='data'}">
    window.parent.location = "customer.do?method=customerdata";
    </c:when>
    </c:choose>

    //author:zhangjuntao
    var objEnterPhoneType, objEnterPhoneArrears, objEnterPhoneLicenceNo, objEnterPhoneDate, objEnterPhoneName, objEnterPhoneCustomerIdStr;
    function enterPhoneSendSms(objEnterPhoneMobile) {
      sendSms(objEnterPhoneMobile, objEnterPhoneType, objEnterPhoneArrears, objEnterPhoneLicenceNo,
          objEnterPhoneDate, objEnterPhoneName, objEnterPhoneCustomerIdStr);
    }

    function sendSms(mobile, type, arrears, licenceNo, date, name, customerIdStr) {        // type <!-- 0 保险  1 验车  2 生日-->
      if (mobile == null || $.trim(mobile) == "") {
        $("#enterPhoneCustomerId").val(customerIdStr);
        Mask.Login();
        $("#enterPhoneSetLocation").fadeIn("slow");
        objEnterPhoneType = type;
        objEnterPhoneArrears = arrears;
        objEnterPhoneLicenceNo = licenceNo;
        objEnterPhoneDate = date;
        objEnterPhoneName = name;
        objEnterPhoneCustomerIdStr = customerIdStr;
        return;
      }

      if (arrears == 0.0) {
        window.location = encodeURI("sms.do?method=smswrite&mobile=" + $.trim(mobile)+"&customerId=" + customerIdStr);
      }else {
        var dates = date.split("-");
        var month = dates[1];
        var day = dates[2];
        window.location = encodeURI("sms.do?method=smswrite&mobile=" + $.trim(mobile) + "&type=" + type + "&money=" + arrears + "&licenceNo=" + licenceNo + "&month=" + month + "&day=" + day + "&name=" + name + "&customerId=" + customerIdStr);
      }
    }

    $().ready(function () {
        // 权限设定，手机号码部分隐藏
        var mobileHiddenTag = <%=mobileHiddenTag%>;
        var userGroup = "<%=userGroup%>";
        if (mobileHiddenTag == true && userGroup != "BCGOGO管理员" && userGroup != "老板/财务") {
            APP_BCGOGO.Permission.isMobileHidden = true;
        }else {
            APP_BCGOGO.Permission.isMobileHidden = false;
        }
        $("#customerSearchBtn").click();

    });

  </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<bcgogo:permissionParam permissions="WEB.CUSTOMER_MANAGER.SMS_SEND">
  <input type="hidden" id="smsSendPermission" value="${WEB_CUSTOMER_MANAGER_SMS_SEND}"/>
</bcgogo:permissionParam>
<input type="hidden" id="quanxian" value="${userGroupName}">
<%--存放选择客户的id--%>
<div id="selectedIdArray">
</div>

<div class="i_main clear">
<div class="i_main clear">
    <div class="mainTitles">
        <jsp:include page="customerNavi.jsp">
            <jsp:param name="currPage" value="customerData"/>
        </jsp:include>
        <%--<jsp:include page="customerDataNavi.jsp">--%>
            <%--<jsp:param name="currPage" value="customerData"/>--%>
        <%--</jsp:include>--%>
    </div>
  <div class="i_mainRight">
    <div class="cus_condition">
      <div class="cus_left"></div>
      <div class="cus_Bg" style="height:28px">
        <input type="text" class="txt" id="customerInfoText" pagetype="customerdata"
               style="color: #666666;width:220px; height:20px;" initialValue="客户/联系人/手机/车牌号/会员号"
               value="客户/联系人/手机/车牌号/会员号"/>
        <input type="hidden" value="${customerIds}" id="customerIds">
        <input class="cus_btnSearch" id="customerSearchBtn" type="button" onfocus="this.blur();" value="搜索">
      </div>
      <%--<div class="cus_right"></div>--%>
      <div class="cus_member" id="conditions" style="display: none">
      </div>
      <div class="cus_search">
        <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
        <div class="cus_line type" id="memberTypes">
          <label>按会员类型选择：</label>
          <a class="hoverText_deepyellow">非会员</a>
          <a class="hoverText_deepyellow" multvalue="${cardNames}" id="allMember">会员</a>
          <c:forEach items="${memberCardTypes}" var="card" varStatus="status">
            <c:choose>
              <c:when test="${status.index>3}">
                <a class="hoverText_deepyellow moreCardName" style="display: none;">${card}</a>
              </c:when>
              <c:otherwise>
                <a class="hoverText_deepyellow">${card}</a>
              </c:otherwise>
            </c:choose>
          </c:forEach>
          <span class="moreCard blue_col" id="showMoreCard">更多</span>
        </div>
        </bcgogo:hasPermission>
        <div class="cus_line" id="totalAmount">
          <label>按累计金额选择：</label>
          <a class="hoverText_deepyellow">一千以下</a>
          <a class="hoverText_deepyellow">一千~一万</a>
          <a class="hoverText_deepyellow">一万以上</a>
          <input type="text" class="txt textbox" id="totalAmountStart"/>
          ~
          <input type="text" class="txt textbox" id="totalAmountEnd"/>
          <a class="cusSure hoverText_lightyellow">确认</a>
        </div>
        <div class="cus_line" id="totalDebt">
          <label>按欠款金额选择：</label>
          <a class="hoverText_deepyellow">无应收</a>
          <a class="hoverText_deepyellow">有应收</a>
          <a class="hoverText_deepyellow">一千以下</a>
          <a class="hoverText_deepyellow">一千~一万</a>
          <a class="hoverText_deepyellow">一万以上</a>
          <input type="text" class="txt textbox" id="totalDebtStart"/>
          ~
          <input type="text" class="txt textbox"
                 id="totalDebtEnd"/>
          <a class="cusSure hoverText_lightyellow">确认</a>
        </div>
        <div class="cus_line" id="lastExpenseTime">
          <label>按消费时间选择：</label>
          <a class="hoverText_deepyellow">昨天</a>
          <a class="hoverText_deepyellow">今天</a>
          <a class="hoverText_deepyellow">最近一周</a>
          <a class="hoverText_deepyellow">最近一月</a>
          <a class="hoverText_deepyellow">最近一年</a>
          <input id="lastExpenseTimeStart" type="text" style="width:80px;"
                 readonly="readonly" class="textbox" />
          ~
          <input id="lastExpenseTimeEnd" type="text" style="width:80px;"
                 readonly="readonly"class="textbox" />
          <a class="cusSure hoverText_lightyellow">确认</a>
        </div>
      </div>
      <div class="cus_bottomLeft"></div>
      <div class="cus_bottomRight"></div>
    </div>
  </div>

</div>
<div class="i_mainRight">
  <div class="cusMsg">
    <input id="filterType" type="hidden">
    <div style="float:left;">客户：共有：<span id="totalNum" class="blue_col">0</span>名
      <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.MEMBERSHIP_PACKAGE_MANAGER">
        <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;其中会员：<span id="memberNum" class="blue_col">0</span>名
        </bcgogo:hasPermission>
      </bcgogo:hasPermission>
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;今日新增：<span id="todayCustomer" class="blue_col">0</span>名
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;手机客户：<span
          id="mobileNum" class="blue_col">0</span>名<div class="cusLetter" title="群发短信" id="sendMulSms"></div>
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;应收款：¥<span
          id="totalDebtStat">0</span>
        <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.RELATION_CUSTOMER">
        &nbsp;&nbsp;&nbsp;关联客户:<span id="relatedNum" class="blue_col">0</span>名&nbsp;&nbsp;
        </bcgogo:hasPermission>
    </div>
    <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MODIFY">
      <input type="button" class="add_user" id="input_addUser" value="新增客户" onfocus="this.blur();"/>
    </bcgogo:hasPermission>
      <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.RELATION_CUSTOMER">
          <input class="add_user hoverReminder" id="sentInvitationCodePromotionalSms" type="button" value="推荐使用软件"
                 style="margin-right: 10px;">
      <div class="tixing alert" style="left: 820px; margin-top:28px; display: none;">
        <div class="ti_top"></div>
        <div class="ti_body alertBody" style="color: #FF5E04;">
          <div>您可推荐未使用一发软件的供应商使用:</div>
          <div>1、成功推荐1家，即可获得200元短信返利！</div>
          <div>2、一站式比价采购，节省成本！</div>
          <div>3、海量供应商供您选择！</div>
        </div>
        <div class="ti_bottom"></div>
      </div>
      </bcgogo:hasPermission>
  </div>
  <div class="cus_title">
    <input type="hidden" name="rowStart" id="rowStart" value="0">
    <input type="hidden" name="pageRows" id="pageRows" value="15">
    <input type="hidden" name="totalRows" id="totalRows" value="0">
    <input type="hidden" name="sortStatus" id="sortStatus" value="">
     <input type="hidden" name="sortStr" id="sortStr" value="">

    <table class="cus_table clear table2" cellpadding="0" cellspacing="0" id="customerDatas"></table>
    <div class="height"></div>
    <div style="float:left">
      <jsp:include page="/common/pageAJAXForSolr.jsp">
        <jsp:param name="dynamical" value="customerSuggest"></jsp:param>
        <jsp:param name="buttonId" value="searchCustomerDataAction"></jsp:param>
      </jsp:include>
    </div>
  </div>
  <div class="height"></div>
  <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.CUSTOMER_MERGE">
    <div style="padding-top: 10px">
      <input type="button" class="buttonBig add_user" id="mergeCustomerBtn" value="合并客户" onfocus="this.blur();"/>
    </div>
  </bcgogo:hasPermission>
</div>
</div>
<c:if test="${shopIdTip!=null && shopIdTip=='lostShopId'}">
  <script type="text/javascript">
    nsDialog.jAlert("店铺Id缺失!");
  </script>
</c:if>

<c:if test="${nameLicenceNoTip!=null && nameLicenceNoTip=='lostNameLicenceNo'}">
  <script type="text/javascript">
    nsDialog.jAlert("客户名必须填写!");
  </script>
</c:if>

<div id="mask" style="display:block;position: absolute;"></div>
<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:0px;top:0px; display:none;height:700px;"
        allowtransparency="true" width="100%" height="100%" frameborder="0" src="" scrolling="no"></iframe>

<iframe id="iframe_PopupBox_1" style="position:absolute;z-index:6; display:none;"
        allowtransparency="true" width="730px" height="450px" frameborder="0" src="" scrolling="no"></iframe>

<iframe id="iframe_PopupBox_2"
        style="position:absolute;z-index:6;top:210px;left:87px;display:none;  background: none repeat scroll;"
        allowtransparency="true" width="1000px" height="650px" frameborder="0" src="" scrolling="no"></iframe>
<%@ include file="/sms/enterPhone.jsp" %>

<div id="deleteCustomer_dialog">
  <div id="deleteReceiptNo"></div>
</div>

<!-- 合并普通客户弹出框-->
<div id="mergeCustomerDetail" style="display: none;">
  <jsp:include page="merge/mergeCustomerDetail.jsp"></jsp:include>
</div>

<!-- 合并关联客户弹出框-->
<div id="mergeRelatedCustomerDetail" style="display: none;">
  <jsp:include page="merge/mergeRelatedCustomerDetail.jsp"></jsp:include>
</div>
<div pop-window-name="input-mobile" style="display: none;">
    <div style="margin-left: 10px;margin-top: 10px">
        <label>手机号：</label>
        <input type="text" pop-window-input-name="mobile" maxlength="11" style="width:125px;height: 20px">
    </div>
</div>
<div id="cancelShopRelationDialog" style="display: none;">
    该用户是您的关联客户，取消关联后您将无法在线处理订单，是否确认取消？若确认，请填写取消理由。
  <textarea id="cancel_msg" init_word="取消关联理由" maxLength=70 style="width:270px;height: 63px;margin-top: 7px;"
            class="gray_color">取消关联理由</textarea>
</div>
<div class="alert" id="payableReceivableAlert" style="display: none;">
    点击后对账
</div>
<div id="multi_alert" class="tixing alert" style="left: 820px; margin-top: 0px; display: none;">
    <div class="ti_top"></div>
    <div class="ti_body" style="color: #FF5E04;">
        <div>您可推荐未使用一发软件的客户使用，推荐成功使用，您可拥有:</div>
        <div>1、200元短信返利！</div>
        <div>2、追踪客户商品销售情况！</div>
        <div>3、在线处理订单，简单快捷！</div>
        <div>4、促销商品消息，营销便利！</div>
        <div>5、海量客户！</div>
    </div>
    <div class="ti_bottom"></div>
</div>
<div id="single_alert" class="tixing alert" style="left: 820px; margin-top: 0px; display: none;">
    <div class="ti_top tiTop"></div>
    <div class="ti_body" style="color: #FF5E04;">
        <div>您可推荐未使用一发软件的客户使用，推荐成功，您可拥有:</div>
        <div>1、200元短信返利！</div>
        <div>2、追踪客户商品销售情况！</div>
        <div>3、在线处理订单，简单快捷！</div>
        <div>4、促销商品消息，营销便利！</div>
        <div>5、海量客户！</div>
    </div>
    <div class="ti_bottom"></div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>