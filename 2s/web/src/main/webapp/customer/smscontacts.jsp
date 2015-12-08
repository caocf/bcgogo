<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 11-11-2
  Time: 下午5:59
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ include file="/WEB-INF/views/includes.jsp" %>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>客户管理——短信管理——通讯录</title>

    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>
    <script type="text/javascript" src="js/invoicing<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/script<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/txnbase<%=ConfigController.getBuildVersion()%>.js"></script>
    <%--<script type="text/javascript" src="js/dragiframe<%=ConfigController.getBuildVersion()%>.js"></script>--%>
    <script type="text/javascript" src="js/jsScroller<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/jsScrollbar<%=ConfigController.getBuildVersion()%>.js"></script>

</head>
<body class="bodyMain">

<%@include file="/WEB-INF/views/header_html.jsp" %>
<div class="i_main">
    <div class="i_search">
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
                    <li><a href="#">草稿箱</a></li>
                    <li><a href="#">已发送</a></li>
                    <li><a class="sms_hover" href="#">通讯录</a></li>
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
                <div><a href="#" class="sms_leftIcon1">草 稿 箱</a></div>
                <div class="i_height"></div>
                <div><a href="#" class="sms_leftIcon2">已 发 送</a></div>
                <div class="i_height"></div>
                <div class="sms_leftTwo"><a href="#" class="sms_leftIcon3">通 讯 录</a></div>
                <div class="i_height"></div>
                <div><a href="#" class="sms_leftIcon4">垃 圾 箱</a></div>
            </div>
            <div class="sms_mainRight">
                <div class="sms_rightTitle">
                    <div class="sms_titleLeft"></div>
                    <div class="sms_titleCenter">
                        通 讯 录（共<span>31,000</span>名联系人）
                    </div>
                    <div class="sms_titleRight"></div>
                </div>
                <div class="sms_Select">
                    <div class="sms_allCheckbox"><label class="label_check c_on" for="checkbox-01"><input
                            type="checkbox" name="checkbox" id="checkbox-01" checked="" value="1"
                            onfocus="this.blur();"/></label></div>
                    <div class="sms_selectTitles"><a>新建联系人</a><a>删除联系人</a><a class="sms_allow">移动到组</a></div>
                    <div class="i_leftBtn">
                        <div class="i_leftCountHover">1</div>
                        <div class="i_leftCount">2</div>
                        <div class="i_leftCount">3</div>
                    </div>
                </div>
                <div class="sms_allContacts">
                    <div class="sms_note">
                        <div class="sms_allCheckbox"><label class="label_check c_on" for="checkbox-02"><input
                                type="checkbox" name="checkbox" id="checkbox-02" checked="" value="1"
                                onfocus="this.blur();"/></label></div>
                        <div class="sms_selectTitle">陈紫杰</div>
                        <div class="sms_noteContent">0512-66733331/13088888888</div>
                        <div class="sns_noteTime">苏E88888</div>
                    </div>
                    <div class="sms_note">
                        <div class="sms_allCheckbox"><label class="label_check c_on" for="checkbox-03"><input
                                type="checkbox" name="checkbox" id="checkbox-03" checked="" value="1"
                                onfocus="this.blur();"/></label></div>
                        <div class="sms_selectTitle">陈紫杰</div>
                        <div class="sms_noteContent">0512-66733331/13088888888</div>
                        <div class="sns_noteTime">苏E88888</div>
                    </div>
                    <div class="sms_note">
                        <div class="sms_allCheckbox"><label class="label_check c_on" for="checkbox-04"><input
                                type="checkbox" name="checkbox" id="checkbox-04" checked="" value="1"
                                onfocus="this.blur();"/></label></div>
                        <div class="sms_selectTitle">陈紫杰</div>
                        <div class="sms_noteContent">0512-66733331/13088888888</div>
                        <div class="sns_noteTime">苏E88888</div>
                    </div>
                    <div class="sms_note">
                        <div class="sms_allCheckbox"><label class="label_check c_on" for="checkbox-05"><input
                                type="checkbox" name="checkbox" id="checkbox-05" checked="" value="1"
                                onfocus="this.blur();"/></label></div>
                        <div class="sms_selectTitle">陈紫杰</div>
                        <div class="sms_noteContent">0512-66733331/13088888888</div>
                        <div class="sns_noteTime">苏E88888</div>
                    </div>
                    <div class="sms_note">
                        <div class="sms_allCheckbox"><label class="label_check c_on" for="checkbox-06"><input
                                type="checkbox" name="checkbox" id="checkbox-06" checked="" value="1"
                                onfocus="this.blur();"/></label></div>
                        <div class="sms_selectTitle">陈紫杰</div>
                        <div class="sms_noteContent">0512-66733331/13088888888</div>
                        <div class="sns_noteTime">苏E88888</div>
                    </div>
                    <div class="sms_note last">
                        <div class="sms_allCheckbox"><label class="label_check c_on" for="checkbox-07"><input
                                type="checkbox" name="checkbox" id="checkbox-07" checked="" value="1"
                                onfocus="this.blur();"/></label></div>
                        <div class="sms_selectTitle">陈紫杰</div>
                        <div class="sms_noteContent">0512-66733331/13088888888</div>
                        <div class="sns_noteTime">苏E88888</div>
                    </div>
                </div>
                <div class="sms_group">
                    <div class="sms_groupNew">分组<span>新建联系组</span></div>
                    <div class="sms_border"></div>
                    <div class="sms_groupTitle">所有<span>（105）</span></div>
                    <div class="sms_groupTitle">常用联系人<span>（105）</span></div>
                    <div class="sms_groupTitle">未分组<span>（105）</span></div>
                    <div class="sms_solidborder"></div>
                    <div class="sms_groupTitle">丰田<span>（105）</span></div>
                    <div class="sms_groupTitle">别克<span>（105）</span></div>
                    <div class="sms_groupTitle">供应商<span>（105）</span></div>
                </div>
                <div class="sms_Select space">
                    <div class="sms_allCheckbox"><label class="label_check c_on" for="checkbox-08"><input
                            type="checkbox" name="checkbox" id="checkbox-08" checked="" value="1"
                            onfocus="this.blur();"/></label></div>
                    <div class="sms_selectTitles"><a>新建联系人</a><a>删除联系人</a><a class="sms_allow">移动到组</a></div>
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