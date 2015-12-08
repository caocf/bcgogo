<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 11-11-2
  Time: 下午6:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ include file="/WEB-INF/views/includes.jsp" %>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>客户管理——短信管理——写短信</title>

    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/messGuide<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/userGuid<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/SMSwrite<%=ConfigController.getBuildVersion()%>.css"/>

    <%@include file="/WEB-INF/views/header_script.jsp" %>

    <script type="text/javascript" src="js/invoicing<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/txnbase<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript" src="js/jsScroller<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/jsScrollbar<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript" src="js/customer<%=ConfigController.getBuildVersion()%>.js"></script>
     <script type="text/javascript">
         $().ready(function() {
            $("#sendTime")
                    .datetimepicker({
                "numberOfMonths" : 1,
                "showButtonPanel": true,
                        "changeYear":true,
                        "changeMonth":true,
                        "yearSuffix":"",
                        "yearRange":"c-100:c+100"
                    })
                    .bind("click", function(event) {
                        $(this).blur();
            });

        });
    </script>

</head>
<body class="bodyMain">

<%@include file="/WEB-INF/views/header_html.jsp" %>


<div class="title">    <!--
  <div class="title_left">
  </div>
  <div class="title_center"></div>
  <div class="title_right"></div>   -->
    <div class="title_label">
        <ul>         <!--
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
      </li>       -->
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
                    <li><a class="sms_hover" href="#">写短信</a></li>
                    <li><a href="customer.do?method=smssent">已发送</a></li>
                    <li><a href="customer.do?method=smssend">待发送</a></li>
                    <li><a href="customer.do?method=smsinbox">收件箱</a></li>
                </ul>
            </div>
            <div class="sms_titleRight"></div>
        </div>
        <div class="sms_main">
            <div class="height"></div>
            <div class="sms_mainLeft">
                <div class="sms_leftOne"><a href="#" class="sms_leftIcon1">写 短 信</a></div>
                <div class="i_height"></div>
                <div><a href="customer.do?method=smssent" class="sms_leftIcon2">已 发 送</a></div>
                <div class="i_height"></div>
                <div><a href="customer.do?method=smssend" class="sms_leftIcon3">待 发 送</a></div>
                <div class="i_height"></div>
                <div><a href="customer.do?method=smsinbox" class="sms_leftIcon4">收 件 箱</a></div>
            </div>
            <div class="sms_mainRight">
                <!--头部-->
                <div class="sms_rightTitle">
                    <div class="sms_titleLeft"></div>
                    <div class="sms_titleCenter">
                        写 短 信
                    </div>
                    <div class="sms_titleRight"></div>
                </div>
                <!--内容-->
                <div class="write clear">

                    <!--短信内容-->
                    <div class="text_cont">
                        <table>
                            <tr>
                                <td><label class="text_lab lis_pho">接收手机</label></td>
                                <td class="txt_edit">
                                    <textarea class="phoNumber" name="phoneNumbers" id="phoneNumbers"></textarea><br/>

                                    <label class="txt_lbl">共添加了<span id="phoneAmount"></span>个手机号码;</label>
                                    <input type="button" class="add_Contacts" id="addUserBtn" value="添加联系人"/>
                                </td>
                            </tr>
                            <tr>
                                <td><label class="text_lab send_cont">发送内容</label></td>
                                <td class="txt_ti">
                                    <div class="txt_bg">
                                        <span class="txt_top"></span>
                                        <textarea name="smsContent" id="smsContent" class="phoContent"
                                                  row="1"></textarea>
                                        <span class="txt_bottom"></span>
                                    </div>
                                    <label class="txt_lbl txt_char">共输入了<span id="contentLength"></span>个汉字</label>
                                    <label class="txt_lbl">短信内容最多只能输入200个汉字，每70个汉字为一条，依此类推</label>
                                </td>
                            </tr>
                            <tr>
                                <td><label class="text_lab send_time">发送时间</label></td>
                                <td>

                                    <div class="i_searchTime" id="sendTimeDiv">
                                        <input id="sendTime" readonly="true" type="text"
                                               style="margin-left: 10px;width: 175px;background-color: #f5f5f5"/>
                                    </div>
                                    <div class="i_searchTime" style="display:none;"></div>
                                    <label class="txt_lbl">短信将于<span id="sendYear"></span>年<span id="sendMonth"></span>月<span
                                            id="sendDate"></span>日00:00准时发送</label>
                                </td>
                            </tr>

                        </table>
                        <div class="shortcut_send clear">
                            <input type="button" id="nowSendBtn" class="affirm_send" value="立即发送"/>
                            <input type="button" id="delaySendBtn" class="delayed" value="延时发送"/>
                        </div>

                        <!--短信类型-->

                    </div>
                    <!--选择客户-->
                    <div class="custom">
                        <div class="custom_top clear"><span class="top_left"></span><span class="top_right"></span>

                            <div class="clear"></div>
                        </div>
                        <div class="custom_content">
                            <h5>选择客户</h5>

                            <!--客户信息-->
                            <dl class="customer user_info" id="num1">
                                <dt>
                                <div class="cust_left"></div>
                                <div class="cust_title"><label class="title_name">&nbsp;</label><label
                                        class="title_sum">数量</label><label class="title_time">客户类型
                                </label></div>
                                </dt>
                                <dd>
                                    <!--<input type="radio" name="info" class="num"/><a href="#" class="cust_sum">1330</a><label class="cust_time">联系电话齐全(节日祝福)</label>--></dd>
                                <dd>
                                    <!--<input type="radio" name="info" class="num"/><a href="#" class="cust_sum">1330</a><label class="cust_time">联系电话齐全(促销活动)</label>--></dd>
                                <dd><!--<input type="radio" name="info" class="num"/><a href="#" class="cust_sum">45</a><label class="cust_time">
               	    <div class="sms_time" id="input_operate"><div class="sms_timeArrow"></div><span id="span_operate">三个月</span></div>
                    保险到期</label>--></dd>
                                <dd><!--<input type="radio" name="info" class="num"/><a href="#" class="cust_sum">12</a><label class="cust_time">
                  	<!--<div class="register_position" id="input_operate">
                        <div class="register_icon"><input type="button" onfocus="this.blur();" /></div>
                        <span id="span_operate">三个月</span>
                    </div>
                    <div id="div_operate" class="selectMode"  style="display:none;">
                    <a>六个月</a>
                    <a>12个月</a>
                  </div> -->       <!--
                  <div class="sms_time" id="input_inspection"><div class="sms_timeArrow"></div><span id="span_inspection">三个月</span></div>
                  验车到期</label>--></dd>
                                <dd>
                                    <!--<input type="radio" name="info" class="num"/><a href="#" class="cust_sum">5</a><label class="cust_time">近期违章(短信通知)</label>--></dd>
                                <dd><!--<input type="radio" name="info" class="num"/><a href="#" class="cust_sum">6</a><label class="cust_time">
                <!--<div class="register_main" id="input_operate">
                        <div class="register_icon"><input type="button" onfocus="this.blur();" /></div>
                        <span id="span_operate">三个月</span>
                    </div>
                    <div id="div_operate" class="selectMode"  style="display:none;">
                    <a>六个月</a>
                    <a>12个月</a>
                  </div> -->    <!--
                  <div class="sms_time" id="input_birthday"><div class="sms_timeArrow"></div><span id="span_birthday">三个月</span></div>
                内过生日</label>--></dd>
                                <dd><!--<input type="radio" name="info" class="num" /><a href="#" class="cust_sum">15</a><label class="cust_time">
                 	<label class="cust_time">会员卡余额不足</label>
              <!--   	<div class="register_operate aa_show" id="input_operate">
                        <div class="register_icon aa_show"><input type="button" onfocus="this.blur();" /></div>
                        <span id="span_operate">100</span>
                    </div>
                    <div id="div_operate" class="selectMode"  style="display:none;">
                    <a>六个月</a>
                    <a>12个月</a>
                  </div> -->
                                    <!--
                                        <label class="cust_time"><div class="sms_money" id="input_money"><div class="sms_timeArrow"></div><span id="span_money">100</span></div>元</label>
                                    </label>--></dd>
                            </dl>
                            <div class="clear"></div>
                        </div>
                        <div class="custom_bottom"><span class="bottom_left"></span><span class="bottom_right"></span>
                        </div>
                    </div>
                    <!--选择客户结束-->
                    <div class="clear"></div>
                </div>
            </div>
            <div class="height"></div>
        </div>
    </div>
</div>

<div id="div_operate" class="selectTime" style="display:none;">
    <a>半个月</a>
    <a>1个月</a>
    <a>2个月</a>
    <a>3个月</a>
</div>

<div id="div_money" class="selectMoney" style="display:none;">
    <a>10</a>
    <a>20</a>
    <a>50</a>
    <a>100</a>
</div>

<div id="mask" style="display:block;position: absolute;">
</div>
<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:400px; top:400px; display:none;"
        allowtransparency="true" width="630px" height="450px" frameborder="0" src=""></iframe>

<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>
