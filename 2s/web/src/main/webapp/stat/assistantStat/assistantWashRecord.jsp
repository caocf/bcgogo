<%@ include file="/WEB-INF/views/includes.jsp" %>
<%--
  User: liuWei
  Date: 12-4-16
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>
        员工洗车业绩统计
    </title>


    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/head<%=ConfigController.getBuildVersion()%>.css"/>

    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>

    <%@include file="/WEB-INF/views/header_script.jsp" %>

    <script type="text/javascript"
            src="js/stat/assistantStat/assistantRecord<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid,"WEB.STAT.AGENT_ACHIEVEMENTS.STAT");

        <bcgogo:permissionParam resourceType="logic" permissions="WEB.VERSION.VEHICLE_CONSTRUCTION,WEB.VERSION.MEMBER_STORED_VALUE">
        APP_BCGOGO.Permission.Version.VehicleConstruction =${WEB_VERSION_VEHICLE_CONSTRUCTION};
        APP_BCGOGO.Permission.Version.MemberStoredValue =${WEB_VERSION_MEMBER_STORED_VALUE};
        </bcgogo:permissionParam>
        $().ready(function(){
            $("#printButton").live("click",function(){
                var url;
                APP_BCGOGO.Net.syncGet({
                    url:"print.do?method=getTemplates",
                    data:{
                        "orderType":"ASSISTENT_WASH_STAT",
                        "now":new Date()
                    },
                    dataType:"json",
                    success:function(result) {
                        if(result && result.length >1){
                            var selects = "<div style='margin:10px;font-size:15px;line-height: 22px;'>" +
                                    "<div style='margin-bottom:5px;'>请选择打印模板：</div>";
                            for(var i = 0; i<result.length; i++){
                                var radioId = "selectTemplate" + i;
                                selects += "<input type='radio' id='"+radioId+"' name='selectTemplate' value='"+result[i].idStr+"'";
                                if(i==0){
                                    selects += " checked='checked'";
                                }
                                selects += " />" +"<label for='"+radioId+"'>"+result[i].displayName +"</label><br/>";
                            }
                            selects += "</div>";
                            nsDialog.jConfirm(selects, "请选择打印模板", function (returnVal) {
                                if (returnVal) {
                                    printPageContent($("input:radio[name='selectTemplate']:checked").val());
                                }
                            });
                        }else{
                            printPageContent();
                        }
                    }
                });
            });

            function printPageContent(templateId){
                var data="";
                data+="&startTimeStr="+$("#startTime").val();
                data+="&endTimeStr="+$("#endTime").val();
                data+="&achievementStatTypeStr="+$("#achievementStatTypeStr").val();
                data+="&assistantOrDepartmentIdStr="+$("#assistantOrDepartmentId").val();
                data+="&orderTypeStr=washBeauty";
                data+="&startPageNo="+$("#currentPagedynamicalAssistantWash").val();
                data+="&maxRows=25";
                data+="&achievementOrderTypeStr="+$("#achievementOrderTypeStrHidden").val();
                data+="&achievementCalculateWayStr="+$("#achievementCalculateWayHidden").val();
                data+="&serviceIdStr="+$("#serviceIdStrHidden").val() ;
                data += "&orderType="+$("#orderType").val();
                window.showModalDialog("assistantStat.do?method=printAssistantService&"+data+"&templateId=" + templateId + "&now=" + new Date(), '', "dialogWidth=1024px;dialogHeight=768px");
                return;
            }

        });
    </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>


<div class="i_main clear">
    <input id="recordType" type="hidden" name="recordType" value="assistantWashRecord"/>
    <input id="startTime" type="hidden" name="startTime" value="${startTime}"/>
    <input id="endTime" type="hidden" name="endTime" value="${endTime}"/>
    <input id="orderType" type="hidden" name="orderType" value="${orderType}"/>
    <input id="achievementStatTypeStr" name="achievementStatTypeStr" type="hidden" value="${achievementStatTypeStr}"/>
    <input id="assistantOrDepartmentId" name="assistantOrDepartmentId" type="hidden" value="${assistantOrDepartmentId}"/>
    <input type="hidden" id="startPageNoHiddenHidden"  name="startPageNoHiddenHidden" value="${startPageNoHiddenHidden}" />


    <div class="mainTitles">
        <div class="titleWords">洗车美容业绩</div>
    </div>
    <%@include file="/stat/assistantStat/assistantRecord.jsp" %>
    <div class="cuSearch">
        <div class="cartTop"></div>
        <div class="cartBody">
            <table class="tab_cuSearch" cellpadding="0" cellspacing="0" id="assistantServiceTable">
                <col/>
                <col/>
                <col width="90">
                <col width="80">
                <col width="80">
                <col width="55px">

                <col width="80">
                <col width="55px">
                <col/>
                <col width="80px">
                <tr class="titleBg">
                    <td style="padding-left:10px;">员工</td>
                    <td>部门</td>
                    <td>日期</td>
                    <td>车辆</td>
                    <td>客户</td>
                    <td>内容</td>
                    <td>金额</td>
                    <td>提成</td>
                    <td>提成计算方法</td>
                    <td>单据</td>
                </tr>
            </table>
            <div class="clear i_height"></div>

            <jsp:include page="/common/pageAJAX.jsp">
                <jsp:param name="url" value="assistantStat.do?method=getAssistantServiceByPage"></jsp:param>
                <jsp:param name="jsHandleJson" value="initAssistantWash"></jsp:param>
                <jsp:param name="dynamical" value="dynamicalAssistantWash"></jsp:param>
                <jsp:param name="data"
                           value="{assistantOrDepartmentIdStr:$('#assistantOrDepartmentId').val(),achievementStatTypeStr:$('#achievementStatTypeStr').val(),
                   startTimeStr:$('#startTime').val(),endTimeStr:$('#endTime').val(),orderType:'washBeauty',startPageNo:1,maxRows:25}"></jsp:param>
                <jsp:param name="display" value="none"></jsp:param>
            </jsp:include>
        </div>
        <div class="cartBottom"></div>
    </div>
    <div class="cartBottom"></div>
</div>
<div class="height"></div>
<div class="divTit" id="button" style=" margin:0 auto;  width:200px;">
    <a class="button" id="printButton" style="display:none;">打&nbsp;印</a>
    <a class="button" id="backButton">返&nbsp;回</a>
</div>
<div class="height"></div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>