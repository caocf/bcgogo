<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>项目设置</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css">
    <link rel="stylesheet" type="text/css" href="styles/moreHistory<%=ConfigController.getBuildVersion()%>.css"/>
  <link rel="stylesheet" type="text/css" href="styles/returnStorage<%=ConfigController.getBuildVersion()%>.css">
  <link rel="stylesheet" type="text/css" href="styles/returnsTanTmp<%=ConfigController.getBuildVersion()%>.css">
  <link rel="stylesheet" type="text/css" href="styles/danjuCg<%=ConfigController.getBuildVersion()%>.css">
  <link rel="stylesheet" type="text/css" href="styles/setService<%=ConfigController.getBuildVersion()%>.css">
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/utils/tableUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/selectOptions<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/txnbase<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/setConstruction<%=ConfigController.getBuildVersion()%>.js"></script>
  <script type="text/javascript" src="js/setService<%=ConfigController.getBuildVersion()%>.js"></script>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>

<div class="i_main clear">
<div class="mainTitles">
    <bcgogo:permission>
        <bcgogo:if resourceType="logic" permissions="WEB.VERSION.DISABLE_VEHICLE_CONSTRUCTION">
           <%-- <jsp:include page="txnNavi.jsp">
                <jsp:param name="currPage" value="repair"/>
            </jsp:include>--%>
            <jsp:include page="vehicleConstructionNavi.jsp">
                <jsp:param name="currPage" value="setProject"/>
            </jsp:include>
        </bcgogo:if>
        <bcgogo:else>
            <jsp:include page="vehicleNavi.jsp">
                <jsp:param name="currPage" value="setProject"/>
            </jsp:include>
        </bcgogo:else>
    </bcgogo:permission>
</div>

<div class="i_mainRight" id="i_mainRight">
 <!------------------------------------------项目设置-------------------------------------------------------->
      <form:form commandName="categoryServiceSearchDTO" id="categoryServiceSearchForm" method="post"
                 action="${categoryServiceSearchDTO.url}" name="thisform">

    <div class="tuihuo_first">
      <span class="left_tuihuo"></span>
      <table>
        <col width="80"/>
        <col width="80"/>
        <col width="80"/>
        <col width="170"/>
        <col/>
        <tr>
          <td style="text-align:right;">施工内容：</td>
          <td>
                        <form:input path="serviceName" style="width:300px;"
                                value="${categoryServiceSearchDTO.serviceName}" autocomplete="off" cssClass="textbox"/>
                                <input type="hidden" id="serviceNameHidden" />
          </td>
          <td>营业分类：</td>
          <td>
                        <form:input path="categoryName" style="width:300px;"
                                value="${categoryServiceSearchDTO.categoryName}" autocomplete="off" cssClass="textbox"/>
                                <input type="hidden" id="categoryNameHidden" />

          </td>
                <td><input type="button" value="查询" class="buttonSmall" onclick="doSearch()"/></td>
        </tr>
      </table>
      <span class="right_tuihuo"></span>
    </div>

    <div class="clear"></div>
    <ul class="yinye_title clear">
      <li id="fencount">全部项目</li>
      <li id="first_cont">已分类项目</li>
      <li id="noCategory">未分类项目</li>
    </ul>

    <div class="clear"></div>
    <div class="tuihuo_tb clearfix" style="padding: 10px 15px;">

        <table class="clear" id="tb_tui" style="margin: 0;">
            <col/>
            <col width="423"/>
            <col width="100"/>
            <col width="100"/>
            <col width="100"/>
            <col width="100"/>
            <col width="150"/>
            <col/>


            <tr class="tab_title">
                <td class="tab_first"></td>
                <td style="text-align: left;">施工内容</td>
                <td>
                    <bcgogo:permission>
                        <bcgogo:if permissions="WEB.SET_THE_BUSINESS_CLASSIFICATION">
                            <input type="button" class="se_xiaoshou" value="营业分类" onfocus="this.blur();" id="setSale"/>
                        </bcgogo:if>
                        <bcgogo:else>营业分类</bcgogo:else>
                    </bcgogo:permission>
                </td>
                <td>工时</td>
                <td>金额/工时费</td>
                <td>
                    <bcgogo:permission>
                        <bcgogo:if permissions="WEB.SET_EMPLOYEE_COMMISSIONS">
                            <input type="button" class="se_xiaoshou" value="员工提成" onfocus="this.blur();" id="btnCommission"/>
                        </bcgogo:if>
                        <bcgogo:else>员工提成</bcgogo:else>
                    </bcgogo:permission>
                </td>

                <td><span style=" display:block; float:left; margin-left:30%;">操作</span><input
                        onclick="addConstruction()"
                        type="button" class="btnOpera3"
                        onfocus="this.blur();"
                        style=" display:block; float:left; margin:7px 0px 0px 5px; height:16px; width:16px;"/>
                </td>
                <td class="tab_last"></td>
            </tr>

            <c:forEach items="${categoryServiceSearchDTO.serviceDTOs}" var="service" varStatus="status">
                <tr id="serviceDTOs${status.index}.tr" class="table-row-original">
                    <td></td>
                    <td id="serviceDTOs${status.index}.nameTd" style="border-left:none; text-align: left;"
                        title="${service.name}" ondblclick="editService(this);">

                <span id="serviceDTOs${status.index}.nameSpan">
                    ${service.name}
                </span>

              <form:input path="serviceDTOs[${status.index}].name" style="display:none; height:20px"
                          onchange="changeInfo(this)"
                          value="${service.name}" class="serviceInput" maxlength="60"/>
            <form:hidden path="serviceDTOs[${status.index}].id" value="${service.id}"/>
        </td>

            <td id="serviceDTOs${status.index}.categoryTd" ondblclick="editService(this);">
          <form:hidden path="serviceDTOs[${status.index}].categoryId" value="${service.categoryId}"/>
                <span id="serviceDTOs${status.index}.categoryNameSpan">
                    ${service.categoryName}
                </span>
                            <form:input path="serviceDTOs[${status.index}].categoryName"
                                        class="categoryNameTd serviceInput"
                          style="display:none;height:20px"
                                        onchange="changeInfo(this)" onclick="showCategoryShow(${status.index})"
                                        readOnly="true"
                          value="${service.categoryName}" maxlength="16"/>
                            <div class="i_scroll_percentage categoryShowDiv" style="width:160px;"
                                 id="categoryShow${status.index}">
                <div style="text-align:left" class="i_note" id="categorySet${status.index}">设定营业类别</div>
                <div class="i_upBody clear" id="categoryRadioClear${status.index}">
                  <ul id="categoryUl${status.index}" class="clear">
                                        <c:forEach items="${categoryServiceSearchDTO.categoryDTOs}" var="category"
                                                   varStatus="categoryStatus">
                                            <li id="categoryLi${status.index}${categoryStatus.index}"><input
                                                    name="categoryRadio${status.index}"
                                                    id="categoryRadio${status.index}" type="radio"
                                                    style="float: left;margin-top: 5px"
                                 categoryName="${category.categoryName}" value="${category.id}"
                                                    class="radioCategoryShow${status.index}"/><input
                                                    value="${category.categoryName}"
                                                                                  type="hidden" style="width:145px;"/>
                        <label id="categoryLabel${status.index}"
                            style="width:145px;text-align: left;display: block;float:left">${category.categoryName}</label>
                      </li>
              </c:forEach>
                                        <li id="categoryInputLi${status.index}${categoryStatus.index}"><input
                                                name="categoryRadio${status.index}"
                                                id="categoryRadioShow${status.index}" type="radio" value=""
                                                class="radioCategoryShow${status.index}"/><input type="text"
                                                                                                 class="customCategory"
                                                                                                 id="categoryRadioText${status.index}"
                                                                                style="width:140px;"/>
                    </li>
              </ul>
                  <div class="more_his" id="categoryRadioMore${status.index}">
                                        <input id="saveCategory${status.index}" type="button" value="确认"
                                               onfocus="this.blur();"
                           onclick="changeCategory(${status.index});" class="btn"/>
                                        <input id="cancelCategory${status.index}" type="button" value="取消"
                                               onfocus="this.blur();"
                           onclick="cancleBtn(${status.index});" class="btn"/>
          </div>
                </div>
              </div>
        </td>

            <td id="serviceDTOs${status.index}.timeCost" ondblclick="editService(this);"></td>

            <td id="serviceDTOs${status.index}.priceTd" ondblclick="editService(this);">
                <span id="serviceDTOs${status.index}.priceSpan">
                    ${service.priceStr}
                </span>
                            <form:input path="serviceDTOs[${status.index}].price" onchange="changeInfo(this)"
                                        style="display:none;height:20px"
                          value="${service.priceStr}" class="price serviceInput" maxlength="10"/>
            <form:hidden path="serviceDTOs[${status.index}].light" value="${service.light}"/>
        </td>

            <td id="serviceDTOs${status.index}.percentageAmountTd" style="border-right:none;"
                ondblclick="editService(this);">
                <span id="serviceDTOs${status.index}.percentageAmountSpan">
                    ${service.percentageAmountStr}
                </span>
                        <form:input path="serviceDTOs[${status.index}].percentageAmount"
                                    style="display:none;height:20px"
                                    class="percentageAmount serviceInput" onchange="changeInfo(this)"
                                    value="${service.percentageAmountStr}" maxlength="10"/>
                    </td>
                    <td style="border-right:none;"><a id="serviceDTOs${status.index}.edit"
                                                      onclick="editService(this);"
                                                      style="width:75px;" href="#">编辑</a>
                        <a id="serviceDTOs${status.index}.save" style="width:75px;display: none;"
                           onclick="saveService(this);"
                           href="#">保存</a>&nbsp;|&nbsp;
                        <a href="#" id="serviceDTOs${status.index}.delete" style="width:75px;"
                           onclick="deleteService(this, '${service.id}');">删除</a>
                    </td>
                    <td></td>

                </tr>
            </c:forEach>
        </table>

      <div class="height"></div>
      <c:if test="${pager != null}">
        <input type="hidden" id="pageNo" name="pageNo" value="${pager.currentPage}"/>
        <input type="hidden" id="totalRows" name="totalRows" value="${pager.totalRows}"/>
        <form:hidden path="url" value="${categoryServiceSearchDTO.url}"/>
        <jsp:include page="/common/paging.jsp">
          <jsp:param name="url" value="${categoryServiceSearchDTO.url}"></jsp:param>
          <jsp:param name="submit" value="thisform"></jsp:param>
        </jsp:include>
      </c:if>

    </form:form>

  </div>
</div>
</div>

<div id="mask" style="display:block;position: absolute;"></div>

<div id="div_service" class="i_scroll_serviceName" style="display:none;width:300px;">
    <div class="Scroller-Container_service" id="Scroller-Container_ServiceName"></div>
</div>

<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:400px; top:400px; display:none;"
        allowtransparency="true" width="550px" height="300px" frameborder="0" src="" scrolling="no"></iframe>

<iframe id="iframe_PopupBox_setCategory" style="position:absolute;z-index:5; left:400px; top:400px; display:none;"
        allowtransparency="true" width="550px" height="300px" frameborder="0" src="" scrolling="no"></iframe>

<iframe id="iframe_PopupBox_setServiceCategory"
        style="position:absolute;z-index:5; left:400px; top:400px; display:none;" allowtransparency="true" width="550px"
        height="300px" frameborder="0" src="" scrolling="no"></iframe>

<div id="systemDialog"></div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>