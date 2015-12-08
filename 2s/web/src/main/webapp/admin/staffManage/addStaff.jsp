<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>新增/修改员工信息</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/up<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/accept<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/addWoker<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/employerInfo<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery-ui-1.8.21.custom.css"/>
    <link rel="stylesheet" type="text/css" href="js/extension/jquery/plugin/jquery-ui/themes/redmond/jquery.ui.timepicker-addon.css"/>
    <link rel="stylesheet" href="js/extension/jquery/plugin/jquery-tooltip/jquery.tooltip.css"/>
    <link rel="stylesheet" href="js/extension/jquery/plugin/tipsy-master/src/stylesheets/tipsy.css"/>
    <link rel="stylesheet" href="js/components/themes/bcgogo-detailsListPanel<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" href="js/components/themes/bcgogo-droplist<%=ConfigController.getBuildVersion()%>.css"/>

    <style type="text/css">
        #ui-datepicker-div, .ui-datepicker {
            font-size: 90%;
        }
        .tabEmp tr td span {
            color: #ff0000;
            font-weight: bold;
        }
    </style>
    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.form.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery-ui-1.8.21.custom.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/jquery.ui.timepicker-addon.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-ui/ui/i18n/jquery.ui.datepicker-zh-CN.min.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery-tooltip/jquery.tooltip<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/extension/jquery/plugin/tipsy-master/src/javascripts/jquery.tipsy<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-noticeDialog<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/module/bcgogo-IDCardValidate<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/components/ui/bcgogo-droplist<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/member<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/salesManInfo<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/addStaff<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/admin/department<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/utils/stringUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/page/admin/userGroupSuggestion<%=ConfigController.getBuildVersion()%>.js"></script>
</head>
<body>

<div class="i_searchBrand">
    <form:form commandName="salesManDTO" id="salesManForm" action="staffManage.do?method=checkAndSaveSalesManInfo" method="post">
        <form:hidden path="shopId" value="${salesManDTO.shopId}"/>
        <form:hidden path="id" value="${salesManDTO.id}"/>
        <form:hidden path="userType" value="${salesManDTO.userType}"/>
        <div class="i_arrow"></div>
        <div class="i_upLeft"></div>
        <div class="i_upCenter">
            <div class="i_note" id="div_drag">新增/修改员工信息</div>
            <div class="i_close" id="div_close"></div>
        </div>
        <div class="i_upRight"></div>
        <div class="i_upBody">
          <div class="title_line clear"><label>请填写基本信息：</label> <span class="line_woker"></span></div>
            <div class="height clear"></div>
            <table cellpadding="0" cellspacing="0" class="tabEmp">
                <col width="280">
                <col/>
                <tr>
                    <td>
                        姓&nbsp;&nbsp;&nbsp;&nbsp;名&nbsp;<span>*</span>&nbsp;
                        <form:input path="name" value="${salesManDTO.name}" maxlength="6" class="textbox"/>
                    </td>
                    <td>性&nbsp;&nbsp;&nbsp;&nbsp;别&nbsp;&nbsp;&nbsp; <form:radiobutton path="sexStr" value="MALE"/>男
                        <form:radiobutton path="sexStr" value="FEMALE"/>女
                    </td>
                </tr>
                <tr>
                    <td>
                        手机号码&nbsp;<span style="color: #ff0000;">*</span>&nbsp;
                        <form:input id="mobile" path="mobile" value="${salesManDTO.mobile}" maxlength="11" class="textbox"/>
                    </td>
                    <td>
                        工&nbsp;&nbsp;&nbsp;&nbsp;号&nbsp;&nbsp;&nbsp;
                        <form:input path="salesManCode" value="${salesManDTO.salesManCode}" maxlength="10" class="textbox"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        部&nbsp;&nbsp;&nbsp;&nbsp;门&nbsp;<span>*</span>&nbsp;
                        <form:input path="departmentName" value="${salesManDTO.departmentName}" class="textbox" />
                        <%--<select id="departmentName2" name="departmentName"  value="${salesManDTO.departmentName}" style="width:135px; height:22px;">--%>
                          <%--<c:if test="${salesManDTO.departmentName != null}" var="test">--%>
                              <%--<option value="${salesManDTO.departmentName}" id="first">${salesManDTO.departmentName}</option>--%>
                          <%--</c:if>--%>
                          <%--<c:if test="${!test}">--%>
                              <%--<option value="" id="first">请选择</option>--%>
                          <%--</c:if>--%>
                        <%--</select>--%>
                        <form:hidden path="departmentId" value="${salesManDTO.departmentId}"/>

                    </td>
                    <td>津&nbsp;&nbsp;&nbsp;&nbsp;贴&nbsp;&nbsp;&nbsp;
                        <form:input path="allowance" value="${salesManDTO.allowance}" maxlength="7" class="textbox"/></td>

                </tr>
                <tr>
                    <td>
                        基本工资&nbsp;&nbsp;&nbsp;
                        <form:input path="salary" value="${salesManDTO.salary}" maxlength="7" class="textbox"/></td>
                    <td>
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;QQ&nbsp;&nbsp;&nbsp;
                        <form:input path="qq" value="${salesManDTO.qq}" maxlength="30" class="textbox"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        身份证号&nbsp;&nbsp;&nbsp;
                        <form:input path="identityCard" value="${salesManDTO.identityCard}" maxlength="18" class="textbox"/>
                    </td>
                    <td>&nbsp;&nbsp;&nbsp;Email&nbsp;&nbsp;&nbsp;
                        <form:input path="email" value="${salesManDTO.email}" maxlength="20" class="textbox"/></td>
                    </td>
                </tr>

                <tr>
                    <td>状&nbsp;&nbsp;&nbsp;&nbsp;态&nbsp;&nbsp;&nbsp; <form:radiobutton path="status" value="INSERVICE"/>在职
                        <form:radiobutton path="status" value="DEMISSION"/>离职
                        <form:radiobutton path="status" value="ONTRIAL"/>试用
                    </td>
                    <td>
                        入职日期&nbsp;&nbsp;&nbsp;
                        <form:input path="careerDateStr" name="careerDateStr" value="${salesManDTO.careerDateStr}" class="textbox"/>
                    </td>

                </tr>
                </table>
                <div class="height"></div>
                <div class="title_line clear"><label>请填写账户信息：</label> <span class="line_woker"></span></div>
                <div class="height clear"></div>
                <table cellpadding="0" cellspacing="0" class="tabEmp">
                <tr>

                        <td>账&nbsp;户&nbsp;名&nbsp;&nbsp;&nbsp;
                            <form:input id="userNo" path="userNo" value="${salesManDTO.userNo}" maxlength="20" class="textbox"/>
                        </td>
                        <td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;职位权限&nbsp;&nbsp;&nbsp;
                        <select id="userGroupName2" name="userGroupName" value="${salesManDTO.userGroupName}" style="width:135px; height:22px;">
                          <c:if test="${salesManDTO.userGroupName != null}" var="test">
                              <option value="${salesManDTO.userGroupName}" id="first">${salesManDTO.userGroupName}</option>
                          </c:if>
                          <c:if test="${!test}">
                              <option value="" id="first">请选择</option>
                          </c:if>
                        </select>
                        <form:hidden path="userGroupId" value="${salesManDTO.userGroupId}"/>

                        </td>

                </tr>
                <tr>
                    <td colspan="2">备&nbsp;&nbsp;&nbsp;注&nbsp;&nbsp;&nbsp;&nbsp;
                        <textarea rows="2" cols="40" style="width: 78%;" name="memo" maxlength="100" id="memo" value="${salesManDTO.memo}" class="textarea">${salesManDTO.memo}</textarea>
                    </td>
                </tr>
            </table>


            <div class="height"></div>
            <div class="title_line clear"><label>员工业绩设置提成：</label> <span
                class="line_woker"></span></div>
            <div class="height clear"></div>
            <table cellpadding="0" cellspacing="0" id="add_info">
              <col width="120px;"/>
              <col/>
              <col width="130px;"/>
              <col/>
              <tr>
                <bcgogo:hasPermission permissions="WEB.VEHICLE_CONSTRUCTION.WASH_BEAUTY.BASE">
                  <td class="right">洗车（元/次）</td>
                  <td><input type="text" id="washBeautyAchievement" name="washBeautyAchievement" class="textbox" style="width:110px;"/>
                  </td>
                </bcgogo:hasPermission>

                <bcgogo:hasPermission resourceType="menu" permissions="WEB.VEHICLE_CONSTRUCTION.BASE">
                  <td class="right">施工（百分比%）</td>
                  <td><input type="text" id="serviceAchievement" name="serviceAchievement" class="textbox" style="width:110px;"/></td>
                </bcgogo:hasPermission>
              </tr>
              <bcgogo:hasPermission permissions="WEB.TXN.SALE_MANAGE.SALE" resourceType="menu">
                <tr>
                  <td class="right">销售（百分比%）</td>
                  <td><input type="text" id="salesAchievement" name="salesAchievement" class="textbox"
                             style="width:110px;"/></td>
                  <td class="right">销售利润（百分比%）</td>
                  <td><input type="text" id="salesProfitAchievement" name="salesProfitAchievement" class="textbox"
                             style="width:110px;"/></td>
                </tr>
              </bcgogo:hasPermission>


              <bcgogo:hasPermission resourceType="logic" permissions="WEB.VERSION.MEMBER_STORED_VALUE">
                <tr>
                  <td class="right">会员购卡</td>
                  <td colspan="3"><select id="memberNewType" name="memberNewType" style="width:105px; height:22px; float:left; margin-right:10px;">
                    <option value="CARD_AMOUNT">按销售量</option>
                    <option value="CARD_TOTAL">按销售额</option>
                  </select>
                    <input type="text" id="memberNewAchievement" name="memberNewAchievement" class="textbox" style="width:120px;"/></td>
                </tr>
                <tr>
                  <td class="right">会员续卡</td>
                  <td colspan="3"><select id="memberRenewType" name="memberRenewType" style="width:105px; height:22px; float:left; margin-right:10px;">
                    <option value="CARD_AMOUNT">按销售量</option>
                    <option value="CARD_TOTAL">按销售额</option>
                  </select>
                    <input id="memberReNewAchievement" name="memberReNewAchievement" type="text" class="textbox" style="width:120px;"/></td>
                </tr>
              </bcgogo:hasPermission>


            </table>
            <div class="height"></div>


            <div class="btnClick">
                <input type="button" id="submitBtn"  onfocus="this.blur();" value="确认">
                <input type="button" id="cancelBtn" onfocus="this.blur();" value="取消">
            </div>
        </div>
        <div class="i_upBottom">
            <div class="i_upBottomLeft"></div>
            <div class="i_upBottomCenter"></div>
            <div class="i_upBottomRight"></div>
        </div>
    </form:form>
</div>
</body>
</html>