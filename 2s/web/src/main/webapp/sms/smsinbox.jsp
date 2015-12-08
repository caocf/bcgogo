<%@ page import="com.bcgogo.notification.dto.InBoxDTO" %>
<%@ page import="java.util.List" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<%
    //会员个性化
    boolean isMemberSwitchOn = WebUtil.checkMemberStatus(request);
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>客户管理——短信管理——收件箱</title>

    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/customer<%=ConfigController.getBuildVersion()%>.js"></script>


    <script type="text/javascript" src="js/invoicing<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/txnbase<%=ConfigController.getBuildVersion()%>.js"></script>
    <%--<script type="text/javascript" src="js/dragiframe<%=ConfigController.getBuildVersion()%>.js"></script>--%>

    <script type="text/javascript" src="js/jsScroller<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/jsScrollbar<%=ConfigController.getBuildVersion()%>.js"></script>

        <script type="text/javascript">
        //       function prePage(nowPage){
        //              if(nowPage*1==1){
        //                  alert("已经是第一页!");
        //                  return;
        //              }
        //              window.location = "sms.do?method=smsinbox&pageNo="+(nowPage*1-1);
        //       }
        //
        //       function nextPage(nowPage,pageCount){
        //          if(nowPage*1==pageCount*1){
        //              alert("已经是最后一页!");
        //              return;
        //          }
        //          window.location = "sms.do?method=smsinbox&pageNo="+(nowPage*1+1);
        //       }
    </script>
</head>
<body class="bodyMain">

<%@include file="/WEB-INF/views/header_html.jsp" %>

<div class="title">     <!--
  <div class="title_left">
  </div>
  <div class="title_center"></div>
  <div class="title_right"></div>    -->
  <div class="title_label">
    <ul>             <!--
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
      </li>        -->
    </ul>
  </div>
</div>
<div class="i_main clear">

<%--  <div class="">
    <jsp:include page="../customer/customerNavi.jsp" >
      <jsp:param name="currPage" value="smsManage" />
    </jsp:include>
    <div class="i_main clear">
  </div>--%>

   <jsp:include page="smsNavi.jsp">
       <jsp:param name="catalogue" value="smsManage" />
     <jsp:param name="currPage" value="smsInbox" />
   </jsp:include>

      <div class="sms_mainRight">
        <div class="sms_rightTitle">
          <div class="sms_titleLeft"></div>
          <div class="sms_titleCenter">
            收 件 箱（共<span>${pager.totalRows}</span>封）
          </div>
          <div class="sms_titleRight"></div>
        </div>
        <div class="sms_allSelect">
          <div class="i_leftBtn">      <!--
            <div class="i_leftCountHover">1</div>
            <div class="i_leftCount">2</div>
            <div class="i_leftCount">3</div>  -->
          </div>
        </div>
        <div class="sms_table">
           <table cellpadding="0" cellspacing="0" class="sms_allNote">
           <col width="30">
           <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
               <col width="70">
           </bcgogo:hasPermission>
           <col width="70">
           <col width="100">
           <col width="80">
                        <col/>
           <col width="120">
           <tr class="sms_tr">
             <td>&nbsp;</td>
             <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
               <td>车牌</td>
             </bcgogo:hasPermission>
             <td>客户名</td>
             <td>手机号</td>
             <td>分类</td>
             <td>内容</td>
             <td>时间</td>
           </tr>


           <%
                            List<InBoxDTO> inBoxDTOList = (List<InBoxDTO>) request.getAttribute("inBoxDTOList");
                            if (inBoxDTOList != null) {
                                for (InBoxDTO inBoxDTO : inBoxDTOList) {
           %>
               <tr>
             <td>&nbsp;</td>
                   <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION">
                                <td><%=(inBoxDTO.getLicenceNo() == null ? "" : inBoxDTO.getLicenceNo())%></td>
                   </bcgogo:hasPermission>
                            <td><%=(inBoxDTO.getName() == null ? "" : inBoxDTO.getName())%>
                            </td>
                            <td><%=inBoxDTO.getSendMobile()%>
                            </td>
             <td>----</td>
             <td style="text-align:left;">
                  <%=inBoxDTO.getContent()%>
             </td>
                            <td style="color:#CCCCCC;"><%=inBoxDTO.getSendTime() %>
                            </td>
           </tr>
           <%
                    }
                }
           %>

           </table>
         </div>
         <div class="sms_allSelect">
                <jsp:include page="/common/paging.jsp">
                    <jsp:param name="url" value="method=smsinbox"></jsp:param>
                </jsp:include>
         </div>
     <div class="height"></div>
    </div>
  </div>
    </div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
