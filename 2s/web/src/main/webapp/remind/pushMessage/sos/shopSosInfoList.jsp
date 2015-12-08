<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>救援查询</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/todo_new<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/prompt_box<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
	<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=nh73VgKTDOS1LnxhSPvpz9DM"></script>

	<script type="text/javascript" src="js/page/remind/message/sos/shopSosInfoList<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/exportExcel<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.SCHEDULE.SHOP_SOS_INFO.BASE");
    </script>
</head>
<body class="bodyMain">
<%@include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
    <div class="mainTitles">
        <div class="titleWords">救援提醒</div>
    </div>
    <div class="clear"></div>
    <div id="todo_content">
        <div style="position: relative;z-index: 10;display: none;color: #000000;" id="send_sms_and_client_msg">
            <div class="prompt_box" style="width:430px; margin: 0 auto; position: fixed; top: 100px;left:250px;">
                <div class="title" style="padding-left: 10px;width: 420px;"><div class="turn_off" id="close_prompt_box"></div>发送售后回访信息</div>
                <div class="content">
                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td  colspan="3"  height="5" style="line-height:normal">&nbsp;</td>
                        </tr>
                        <tr>
                            <td valign="top" align="right">发送内容：</td>
                            <td>
                                <div class="prompt_textarea" style="width:220px;height: 111px">
                                    <div class="txt J_prompt_textarea" style="width: 211px;border: 0 none;height: 77px;"></div>
                                    <div class="bottom" style="width: 215px;padding: 0 15px 0 0;margin-left:-5px;margin-top: 7px; ">共<strong class="red_color J_message_size">10</strong>个字/将分为<span class="orange_color J_message_count">2条</span>短信发出</div>
                                </div>

                            </td>
                            <td  valign="top" style="text-align:left; color:#aaa"></td>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td><div class="fl"><input name="sendSms" checked type="checkbox"/>&nbsp发送手机短信</div>
                            </td>
                            <td>&nbsp;</td>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td colspan="2"><div class="fl"><input name="sendApp" type="checkbox" checked />&nbsp发送手机客户端信息</div><span class="gray_color">(仅对已装手机客户端的手机号有效)</span>
                            </td>
                        </tr>
                    </table>
                    <div class="clear"></div>
                    <div class="wid275">
                        <div class="addressList"> <a id="sendSmsAppAction">发 送</a> <a id="cancel_prompt_box">取 消</a>
                        </div>
                    </div>
                    <div class="clear"></div>
                </div>
            </div>
        </div>
        <div class="lay-main lay-map" style="z-index: 4; position: absolute;display:none;padding-top: 200px;padding-left:150px;" id="map_container_iframe_div">
            <div class="hd">
                <a action="close_map_container_iframe" class="close" style="right: 1px;top: 165px;"></a>
                <div class="lay-con" style="width: 710px;">
                    <div class="map">
                        <div class="map-cont" style=" width:700px; height:380px; overflow: hidden;">
                            <%--<iframe src="" id="map_container_iframe" style="width: 700px;height: 550px;" scrolling="no" frameborder="0" allowtransparency="true"></iframe>--%>

                        </div>
                        <div class="padding10">
                            <input name="" type="button" action="close_map_container_iframe"  class="query-btn" value="关闭"/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div>
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="table_search">
                <colgroup>
                    <col  width="80"/>
                    <col />
                    <col  width="80"/>
                    <col  width="200"/>
                    <col  width="80"/>
                    <col  width="290"/>
                </colgroup>
                <tr>
                    <th>日期：</th>
                    <td colspan="3">
                    <div area-type="time" class="divTit">
                    <a class="btnList" data-type="yestoday">昨天</a>&nbsp;
                    <a class="btnList" data-type="today">今天</a>&nbsp;
                    <a class="btnList" data-type="lastweek">最近一周</a>&nbsp;
                    <a class="btnList" data-type="lastmonth">最近一月</a>&nbsp;
                    <a class="btnList" data-type="lastyear">最近一年</a>
                    <input autocomplete="off" type="text" class="txt" id="timeStart" readonly="readonly" style="width: 70px"/>
                    至
                    <input autocomplete="off" type="text" class="txt" id="timeEnd" readonly="readonly" style="width: 70px"/>
                    </div>
                    </td>

                    <th>状态：</th>
                    <td>
                        <label class="rad"><input autocomplete="off" type="checkbox" ${isUntreated == 'YES'?"checked='checked'":"" } name="isUntreated"/>&nbsp;未处理</label>
                        <%--<label class="rad"><input autocomplete="off" type="checkbox" ${isSendMessage == 'YES'?"checked='checked'":"" } name="isSendMessage"/>&nbsp;已发消息</label>--%>
                        <%--<label class="rad"><input autocomplete="off" type="checkbox" ${isCreateAppointOrder == 'YES'?"checked='checked'":"" } name="isCreateAppointOrder"/>&nbsp;已预约</label>--%>
                        <label class="rad"><input autocomplete="off" type="checkbox" ${isDeleted == 'YES'?"checked='checked'":"" } name="isDeleted"/>&nbsp;已处理</label>
                    </td>
                    <%--<th>距保养里程：</th>--%>
                    <%--<td>--%>
                        <%--<select id="mileageType" class="txt" name="mileageType" style="width: 120px" autocomplete="off">--%>
                            <%--<option value="">--全部--</option>--%>
                            <%--<c:forEach items="${mileageTypes}" var="mileageType">--%>
                                <%--<option value="${mileageType}">${mileageType.value}</option>--%>
                            <%--</c:forEach>--%>

                        <%--</select>--%>
                    <%--</td>--%>
                    <%--<th>车牌号：</th>--%>
                    <%--<td><input type="text" class="txt" name="vehicleNo" value="${vehicleNo}" autocomplete="off"/></td>--%>
                </tr>
                <tr>
                    <%--<th>车牌号：</th>--%>
                    <%--<td><input type="text" class="txt" name="vehicleNo" value="${vehicleNo}" autocomplete="off"/></td>--%>
                    <%--<th>手机号：</th>--%>
                    <%--<td><input type="text" class="txt" name="mobile" value="" autocomplete="off"/></td>--%>
                    <%--<th>类型：</th>--%>
                    <%--<td>--%>
                    <%--<select id="faultAlertType" class="txt" name="faultAlertType" style="width: 120px" autocomplete="off">--%>
                    <%--<option value="">--全部--</option>--%>
                    <%--<c:forEach items="${faultAlertTypes}" var="faultAlertType">--%>
                    <%--<option value="${faultAlertType}">${faultAlertType.value}</option>--%>
                    <%--</c:forEach>--%>

                    <%--</select>--%>
                    <%--</td>--%>
                </tr>
            </table>
            <div class="search_div">
                <div class="search_btn" id="searchAction">查 询</div>
                <%--<div class="empty_btn" id="resetAction">清空条件</div>--%>
                <div class="clear"></div>
            </div>
            <div class="clear height"></div>
        </div>
        <div class="score_list">
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="score_table2" id="shopFaultInfoTables">
                <tr>
                    <th style="width: 25px">
                        <input autocomplete="off" type="checkbox" ation-type="select-all"/>
                    </th>
                    <th style="width: 120px">求救时间</th>
                    <th style="width: 160px">车辆信息</th>
                    <th style="width: 160px">车主信息</th>
                    <th style="width: 160px">客户信息</th>
                    <th style="width: 140px">车辆位置</th>
                    <th style="width: 60px">状态</th>
                    <th style="width: 100px">操作</th>
                </tr>
            </table>
            <div class="all_toLeft" id="multiOperationContainer">
                <span class="fl" style="margin-right:10px;"><span style="float:left; margin:6px 5px 0 0px;"><input autocomplete="off" type="checkbox" ation-type="select-all"/></span>全选</span>
                <div class="search_btn" id="deleteAction">删 除</div>
                <%--<div class="search_btn" id="generateAppointOrder">生成预约</div>--%>
                <%--<div class="search_btn" id="exportFaultInfo">导出</div>--%>
                <img id="exportFaultInfoCover" style=" width: 21px;margin-left: 10px;margin-top: 3px; display: none;" title="正在导出" alt="正在导出" src="images/loadinglit.gif">
                <div class="clear"></div>
            </div>
            <div style="float: right;margin: 12px 0 10px 10px;">
                <bcgogo:ajaxPaging
                        url="shopSosInfo.do?method=searchShopSosInfoList"
                        data="{startPageNo:'1',maxRows:10,ids:'${shopFaultInfoId}',isUntreated:'${isUntreated}',isSendMessage:'${isSendMessage}',
                isCreateAppointOrder:'${isCreateAppointOrder}',isDeleted:'${isDeleted}',vehicleNo:'${vehicleNo}'}"
                        postFn="drawShopFaultInfoTable"
                        dynamical="_shopFaultInfo" display="true"/>
            </div>

            <div class="clear"></div>
            <input autocomplete="off" type="hidden" name="startPageNo" id="sfi_startPageNo" value="1"/>
            <input autocomplete="off" type="hidden" name="maxRows" id="sfi_maxRows" value="10" />

            <%--<div class="page_box">--%>
            <%--<div class="fl"><span class="left"><a href="#"> < </a></span> <a href="#" class="hover">1</a> <a href="#">2</a> <a href="#">3</a> <a href="#">4</a> <a href="#">5</a>--%>
            <%--<span class="left"><a href="#"> > </a></span> </div>--%>
            <%--<div class="fl"><span class="fl">跳转到</span><input name="" type="text"  class="input"/><span class="left"><a href="#"> Go </a></span></div>--%>
            <%--</div>--%>
        </div>
    </div>
</div>
<div id="mask" style="display:block;position: absolute;"> </div>
<div id="closemap" style="cursor:pointer;color:black;background-color:yellow;font-weigth:bold;margin-left:905px;
                                display:none;width:30px;z-index:9999;position:relative;top:-490px;text-align:center;" title="关闭">关闭</div>
<div id="allmap" style="width:700px;height:380px;margin:-490px auto 0 auto;
                            display:none;border:5px solid green;z-index:9999;"></div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>

</body>
</html>
