<%@ page import="com.bcgogo.common.Pager" %>
<%@ page import="com.bcgogo.user.dto.CustomerRecordDTO" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%
    @Deprecated
    //会员个性化
    boolean isMemberSwitchOn = WebUtil.checkMemberStatus(request);
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <title>客户管理_欠款提醒</title>

  <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/uncleUser<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
  <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/invoicing<%=ConfigController.getBuildVersion()%>.js"></script>
  <%--<script type="text/javascript" src="js/dragiframe<%=ConfigController.getBuildVersion()%>.js"></script>--%>
  <script type="text/javascript" src="js/jsScroller<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/jsScrollbar<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/detailsArrears<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/customer<%=ConfigController.getBuildVersion()%>.js"></script>

  <script type="text/javascript">

    function detailsArrears(customerId) {
      toReceivableSettle(customerId);
    }
    function prePage(nowPage) {
      if (nowPage != null && nowPage != "" && nowPage != "null") {
        if (nowPage * 1 == 1) {
                    nsDialog.jAlert("已经是第一页!");
        }
                else {
        window.location = "customer.do?method=customerarrears&pageNo=" + (nowPage * 1 - 1);
      }
    }
        }

    function nextPage(nowPage, pageCount) {
      if (nowPage == null || pageCount == null || nowPage == "null" || pageCount == "null") {
        return;
      }
      if (nowPage * 1 == pageCount * 1) {
                nsDialog.jAlert("已经是最后一页!");
      }
            else {
      window.location = "customer.do?method=customerarrears&pageNo=" + (nowPage * 1 + 1);
    }
        }
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

      var dates = date.split("-");
      var month = dates[1];
      var day = dates[2];
            window.location = encodeURI("sms.do?method=smswrite&mobile=" + $.trim(mobile) + "&type=" + type + "&money=" + arrears + "&licenceNo=" + licenceNo + "&month=" + month + "&day=" + day + "&name=" + name + "&customerId=" + customerIdStr);
    }

  </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<%@ include file="/sms/enterPhone.jsp" %>
<div class="i_main">
  <div class="i_search">
    <jsp:include page="customerNavi.jsp" >
      <jsp:param name="currPage" value="customerArrears" />
    </jsp:include>
    <div class="clear"></div>
  </div>


  <div class="i_mainRight">
    <div class="cus_current">欠款客户：<span>${pager.totalRows}</span>名，欠费总额：<span
        style="color: red "><%=request.getAttribute("totalReceivable")%></span>元
    </div>
    <div class="cus_title">
      <div class="cus_titleLeft"></div>
      <div class="cus_titleBody">
        <div style="float:left;width:50px;">No</div>
        <div style="float:left;width:265px;">客户名</div>
        <div style="float:left;width:265px;">联系人</div>
        <div style="float:left;width:125px;">联系方式</div>
        <div style="float:left;width:130px; ">欠款金额</div>
        <div style="float:left;width:120px; padding-left:10px;">预计还款日期</div>
        <div style="float:left;width:25px;">提醒</div>
      </div>
      <div class="cus_titleRight"></div>
    </div>
    <table class="cus_table clear table2" cellpadding="0" cellspacing="0" id="kucun">
      <col width="50"/>
      <col width="257"/>
      <col width="257"/>
      <col width="120"/>
      <col width="135"/>
      <col width="111"/>
            <col/>

      <%
        int pageStart = 0;
                Pager p = (Pager) request.getAttribute("pager");
          pageStart = p.getRowStart();
      %>

      <%
                List<CustomerRecordDTO> customerRecordDTOList = (List<CustomerRecordDTO>) request.getAttribute(
                        "arrearsCustomerRecordDTOList");
        if (customerRecordDTOList != null) {
          for (int m = 0; m < customerRecordDTOList.size(); m++) {
            CustomerRecordDTO customerRecordDTO = customerRecordDTOList.get(m);
      %>
      <%
        if (m % 2 == 0) {
      %>
      <tr>
          <%
              }else{
          %>
      <tr class="cus_tableBg">
        <%
          }
        %>

        <td style="border-left:none;"><%= m + 1 + pageStart %>
        </td>
        <td><a class="i_clickInfo2"
               href="unitlink.do?method=customer&customerId=<%=customerRecordDTO.getCustomerId()%>"><%=(customerRecordDTO.getName() == null ? "" : customerRecordDTO.getName())%>
        </a></td>
        <td><a class="i_clickInfo2"
               href="unitlink.do?method=customer&customerId=<%=customerRecordDTO.getCustomerId()%>"><%=(customerRecordDTO.getContact() == null ? "" : customerRecordDTO.getContact())%>
        </a></td>
        <td><a class="i_clickInfo2"
               href="unitlink.do?method=customer&customerId=<%=customerRecordDTO.getCustomerId()%>"><%=(customerRecordDTO.getMobile() == null ? "" : customerRecordDTO.getMobile())%>
        </a></td>
        <td class="qian_red">


          <a style="color:#FFFFFF"  href="javascript:detailsArrears('<%=customerRecordDTO.getCustomerId()%>')"
             class="num_cont">欠款结算</a>
          <%=customerRecordDTO.getTotalReceivable()%>
        </td>
        <td><%=customerRecordDTO.getRepayDateStr() %>
        </td>
        <td style="border-right:none;">
          <bcgogo:hasPermission permissions="WEB.CUSTOMER_MANAGER.SMS_SEND">
            <img src="images/duan.png" style="width:16px; height:11px;"
                                                    onclick="sendSms('<%=(customerRecordDTO.getMobile()==null?"":customerRecordDTO.getMobile())%>',
                                                            '3','<%=customerRecordDTO.getTotalReceivable()%>',
                                                            '<%=(customerRecordDTO.getLicenceNo()==null?"":customerRecordDTO.getLicenceNo())%>',
                                                            '<%=customerRecordDTO.getRepayDateStr() %>',
                                                            '<%=(customerRecordDTO.getName()==null?"":customerRecordDTO.getName())%>', '<%=customerRecordDTO.getCustomerId() %>')"/>
          </bcgogo:hasPermission>
        </td>

        <!-- mobile,type,arrears,licenceNo,date,name -->

      </tr>
      <%
          }
        }
      %>

    </table>
    <div class="height"></div>

              <!--分页开始 -->
        <jsp:include page="/common/paging.jsp">
            <jsp:param name="url" value="customer.do?method=customerarrears"></jsp:param>
        </jsp:include>
        <!--分页结束-->
  </div>
</div>
</div>
</div>
<iframe id="iframe_PopupBox_1"
        style="position:absolute;z-index:4; left:200px; top:200px; display:none;background:#FFFFFF;"
        allowtransparency="true" width="1000px" height="650px" frameborder="0" src=""></iframe>
<iframe id="iframe_PopupBox_2"
        style="position:absolute;z-index:6;top:210px;left:87px;display:none;  background: none repeat scroll;"
        allowtransparency="true" width="1000px" height="650px" frameborder="0" src="" scrolling="no"></iframe>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>

<script type="text/javascript" src="js/txnbase<%=ConfigController.getBuildVersion()%>.js"></script>

