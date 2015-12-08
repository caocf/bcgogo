<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 11-11-2
  Time: 下午6:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<html>
<head>
    <title>草稿箱</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid,"WEB.CUSTOMER_MANAGER.SMS_DRAFT");
    </script>
</head>
<body class="bodyMain">

<%@include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main clear">
    <div class="">
        <div class="i_searchTitle">超级管理</div>
        <ul>
            <li>
                <div class="searchLeft"></div>
                <div class="searchCenter">
                    <input type="text" value="车牌号" class="txt" id="vehicleNumber"/>
                    <input type="button" class="btn" onfocus="this.blur();"/>
                </div>
                <div class="searchRight"></div>
            </li>
            <li>
                <div class="searchLeft"></div>
                <div class="searchCenter">
                    <input type="text" value="车主姓名、手机号" id="input_search_Name"/>
                    <input type="button" class="btn" onfocus="this.blur();"/>
                </div>
                <div class="searchRight"></div>
            </li>
            <li>
                <div class="searchLeft"></div>
                <div class="searchCenter">
                    <input type="text" value="用品配件品名（简写缩写）" id="input_search_pName"/>
                    <input type="button" class="btn" onfocus="this.blur();"/>
                </div>
                <div class="searchRight"></div>
            </li>
        </ul>
        <div class="height"></div>
        <ul class="client_title">
            <li><a href="customer.do?method=customerdata">客户资料</a></li>
            <li><a href="customer.do?method=customerarrears">欠款提醒</a></li>
            <li><a class="client_hover" href="customer.do?method=smsinbox">短信管理</a></li>
            <li><a href="smsrecharge.do?method=smsrecharge">短信充值</a></li>
        </ul>
        <div class="height"></div>
        <div class="sms_title">
            <div class="sms_titleLeft"></div>
            <div class="sms_titleBody">
                <ul>
                    <li><a href="#">收件箱</a></li>
                    <li><a class="sms_hover" href="#">草稿箱</a></li>
                    <li><a href="#">已发送</a></li>
                    <li><a href="#">通讯录</a></li>
                    <li><a href="#">写短信</a></li>
                </ul>
            </div>
            <div class="sms_titleRight"></div>
        </div>
        <div class="sms_main">
            <div class="height"></div>
            <div class="sms_mainLeft">
                <div class="sms_leftOne"><a href="#">写 短 信</a></div>
                <div class="i_height"></div>
                <div><a href="#" class="sms_leftIcon">收 件 箱</a></div>
                <div class="i_height"></div>
                <div class="sms_leftTwo"><a href="#" class="sms_leftIcon1">草 稿 箱</a></div>
                <div class="i_height"></div>
                <div><a href="#" class="sms_leftIcon2">已 发 送</a></div>
                <div class="i_height"></div>
                <div><a href="#" class="sms_leftIcon3">通 讯 录</a></div>
                <div class="i_height"></div>
                <div><a href="#" class="sms_leftIcon4">垃 圾 箱</a></div>
            </div>
            <div class="sms_mainRight">
                <div class="sms_rightTitle">
                    <div class="sms_titleLeft"></div>
                    <div class="sms_titleCenter">
                        草 稿 箱（共<span>312</span>封）
                    </div>
                    <div class="sms_titleRight"></div>
                </div>
                <div class="sms_allSelect">
                    <div class="sms_allCheckbox"><label class="label_check c_on" for="checkbox-01"><input
                            type="checkbox" name="checkbox" id="checkbox-01" checked="" value="1"
                            onfocus="this.blur();"/></label></div>
                    <div class="sms_selectTitle"><a>全选</a><a>删除</a></div>
                    <div class="i_leftBtn">
                        <div class="i_leftCountHover">1</div>
                        <div class="i_leftCount">2</div>
                        <div class="i_leftCount">3</div>
                    </div>
                </div>
                <div class="sms_allNote">
                    <div class="sms_note">
                        <div class="sms_allCheckbox"><label class="label_check c_on" for="checkbox-02"><input
                                type="checkbox" name="checkbox" id="checkbox-02" checked="" value="1"
                                onfocus="this.blur();"/></label></div>
                        <div class="sms_selectTitle">+0512-66733331</div>
                        <div class="sms_noteContent">您好，感谢两天前到我公司进行服务....</div>
                        <div class="sns_noteTime">2011.11.01 20:58</div>
                    </div>
                    <div class="sms_note">
                        <div class="sms_allCheckbox"><label class="label_check c_on" for="checkbox-03"><input
                                type="checkbox" name="checkbox" id="checkbox-03" checked="" value="1"
                                onfocus="this.blur();"/></label></div>
                        <div class="sms_selectTitle">+0512-66733331</div>
                        <div class="sms_noteContent">您好，感谢两天前到我公司进行服务....</div>
                        <div class="sns_noteTime">2011.11.01 20:58</div>
                    </div>
                    <div class="sms_note">
                        <div class="sms_allCheckbox"><label class="label_check c_on" for="checkbox-04"><input
                                type="checkbox" name="checkbox" id="checkbox-04" checked="" value="1"
                                onfocus="this.blur();"/></label></div>
                        <div class="sms_selectTitle">+0512-66733331</div>
                        <div class="sms_noteContent">您好，感谢两天前到我公司进行服务....</div>
                        <div class="sns_noteTime">2011.11.01 20:58</div>
                    </div>
                    <div class="sms_note last">
                        <div class="sms_allCheckbox"><label class="label_check c_on" for="checkbox-05"><input
                                type="checkbox" name="checkbox" id="checkbox-05" checked="" value="1"
                                onfocus="this.blur();"/></label></div>
                        <div class="sms_selectTitle">+0512-66733331</div>
                        <div class="sms_noteContent">您好，感谢两天前到我公司进行服务....</div>
                        <div class="sns_noteTime">2011.11.01 20:58</div>
                    </div>
                </div>
                <div class="sms_allSelect">
                    <div class="sms_allCheckbox"><label class="label_check c_on" for="checkbox-06"><input
                            type="checkbox" name="checkbox" id="checkbox-06" checked="" value="1"
                            onfocus="this.blur();"/></label></div>
                    <div class="sms_selectTitle"><a>全选</a><a>删除</a></div>
                    <div class="i_leftBtn">
                        <div class="i_leftCountHover">1</div>
                        <div class="i_leftCount">2</div>
                        <div class="i_leftCount">3</div>
                    </div>
                </div>
            </div>
            <div class="height"></div>
        </div>
    </div>
</div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>