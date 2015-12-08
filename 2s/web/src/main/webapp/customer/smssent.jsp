<%@ page import="com.bcgogo.notification.dto.OutBoxDTO" %>
<%@ page import="java.util.List" %>
<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 11-11-2
  Time: 下午6:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>客户管理——短信管理——已发送</title>

    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/customer<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript" src="js/invoicing<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/txnbase<%=ConfigController.getBuildVersion()%>.js"></script>
    <%--<script type="text/javascript" src="js/dragiframe<%=ConfigController.getBuildVersion()%>.js"></script>--%>

    <script type="text/javascript" src="js/jsScroller<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/jsScrollbar<%=ConfigController.getBuildVersion()%>.js"></script>


</head>
<body class="bodyMain">

<%@include file="/WEB-INF/views/header_html.jsp" %>


<div class="title">    <!--
  <div class="title_left">
  </div>
  <div class="title_center"></div>
  <div class="title_right"></div>    -->
    <div class="title_label">
        <ul>        <!--
      <li class="labelhover">
        <div class="label_left"></div>
        <div class="label_bg">
          <a>车辆 苏E88888</a>
          <input type="button" onfocus="this.blur();"/>
        </div>
        <div class="label_right"></div>
      </li>
      <li class="labelnormal">
        <div class="label_left"></div>
        <div class="label_bg">
          <a>车辆 苏E88888</a>
          <input type="button" onfocus="this.blur();"/>
        </div>
        <div class="label_right"></div>
      </li>      -->
        </ul>
    </div>
</div>

<div class="i_main">
    <div class="i_search">
        <div class="i_searchTitle">客户管理</div>
        <div class="i_mainTitle">
            <a href="customer.do?method=customerdata">客户资料</a>
            <a href="customer.do?method=customerarrears">欠款提醒</a>
            <a id="shoppingSell" class="title_hover">短信管理</a>
            <a href="smsrecharge.do?method=smsrecharge">短信充值</a>
        </div>


        <div class="sms_title">
            <div class="sms_titleLeft"></div>
            <div class="sms_titleBody">
                <ul>
                    <li><a href="customer.do?method=smswrite">写短信</a></li>
                    <li><a class="sms_hover" href="#">已发送</a></li>
                    <li><a href="customer.do?method=smssend">待发送</a></li>
                    <li><a href="customer.do?method=smsinbox">收件箱</a></li>
                </ul>
            </div>
            <div class="sms_titleRight"></div>
        </div>
        <div class="sms_main">
            <div class="height"></div>
            <div class="sms_mainLeft">
                <div class="sms_leftOne"><a href="customer.do?method=smswrite" class="sms_leftIcon1">写 短 信</a></div>
                <div class="i_height"></div>
                <div class="sms_leftTwo"><a href="#" class="sms_leftIcon2">已 发 送</a></div>
                <div class="i_height"></div>
                <div><a href="customer.do?method=smssend" class="sms_leftIcon3">待 发 送</a></div>
                <div class="i_height"></div>
                <div><a href="customer.do?method=smsinbox" class="sms_leftIcon4">收 件 箱</a></div>
            </div>
            <div class="sms_mainRight">
                <div class="sms_rightTitle">
                    <div class="sms_titleLeft"></div>
                    <div class="sms_titleCenter">
                        已 发 送（共<span><%=request.getAttribute("sentSmsNumber")%></span>封）
                    </div>
                    <div class="sms_titleRight"></div>
                </div>
                <div class="sms_allSelect">
                    <div class="i_leftBtn">     <!--
            <div class="i_leftCountHover">1</div>
            <div class="i_leftCount">2</div>
            <div class="i_leftCount">3</div>    -->

                        <%
                            Integer pageCount = (Integer) request.getAttribute("pageCount");
                            Integer pageNo = (Integer) request.getAttribute("pageNo");
                            if (pageCount != null && pageNo != null) {
                                for (int m = 1; m <= pageCount; m++) {
                                    if (m == pageNo) {
                        %>
                        <div class="i_leftCountHover"><a style="text-decoration:none;color:#000000"
                                                         href="sms.do?method=smssent&pageNo=<%=(m)%>"><%=(m)%>
                        </a></div>
                        <%
                        } else {
                        %>
                        <div class="i_leftCount"><a style="text-decoration:none;color:#000000"
                                                    href="sms.do?method=smssent&pageNo=<%=(m)%>"><%=(m)%>
                        </a></div>
                        <%
                                    }
                                }
                            }
                        %>

                    </div>
                </div>
                <div class="sms_table">
                    <table cellpadding="0" cellspacing="0" class="sms_allNote">
                        <col width="30">
                        <col width="70">
                        <col width="70">
                        <col width="100">
                        <col width="80">
                        <col/>
                        <col width="120">
                        <tr class="sms_tr">
                            <td>&nbsp;</td>
                            <td>车牌</td>
                            <td>客户名</td>
                            <td>手机号</td>
                            <td>分类</td>
                            <td>内容</td>
                            <td>时间</td>
                        </tr>


                        <%
                            List<OutBoxDTO> outBoxDTOList = (List<OutBoxDTO>) request.getAttribute("outBoxDTOList");
                            if (outBoxDTOList != null) {
                                for (OutBoxDTO outBoxDTO : outBoxDTOList) {
                        %>
                        <tr>
                            <td>&nbsp;</td>
                            <td><%=(outBoxDTO.getLicenceNo() == null ? "" : outBoxDTO.getLicenceNo())%>
                            </td>
                            <td><%=(outBoxDTO.getName() == null ? "" : outBoxDTO.getName())%>
                            </td>
                            <td><%=outBoxDTO.getSendMobile()%>
                            </td>

                            <td>
                                <%=outBoxDTO.getSmsType()  %>
                            </td>

                            <td style="text-align:left;">
                                <%=outBoxDTO.getContent()%>
                            </td>
                            <td style="color:#CCCCCC;"><%=outBoxDTO.getSendTimeStr() %>
                            </td>
                        </tr>
                        <%
                                }
                            }
                        %>

                    </table>
                </div>
                <div class="sms_allSelect">
                    <div class="i_leftBtn">     <!--
              <div class="i_leftCountHover">1</div>
              <div class="i_leftCount">2</div>
              <div class="i_leftCount">3</div>     -->

                        <%
                            // Integer pageCount = (Integer)request.getAttribute("pageCount");
                            // Integer pageNo = (Integer)request.getAttribute("pageNo");
                            if (pageCount != null && pageNo != null) {
                                for (int m = 1; m <= pageCount; m++) {
                                    if (m == pageNo) {
                        %>
                        <div class="i_leftCountHover"><a style="text-decoration:none;color:#000000"
                                                         href="sms.do?method=smssent&pageNo=<%=(m)%>"><%=(m)%>
                        </a></div>
                        <%
                        } else {
                        %>
                        <div class="i_leftCount"><a style="text-decoration:none;color:#000000"
                                                    href="sms.do?method=smssent&pageNo=<%=(m)%>"><%=(m)%>
                        </a></div>
                        <%
                                    }
                                }
                            }
                        %>

                    </div>
                </div>
                <div class="height"></div>
            </div>
        </div>
    </div>
    <%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
