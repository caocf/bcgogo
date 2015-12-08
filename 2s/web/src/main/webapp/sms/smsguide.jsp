<%--
  Created by IntelliJ IDEA.
  User: MZDong
  Date: 11-11-2
  Time: 下午6:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<head>
    <title>客户管理——引导页</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>

    <script type="text/javascript" src="js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="js/base<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/application<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript">
         $().ready(function() {
            $("#datetime").datetimepicker({
                "numberOfMonths" : 1,
                "showButtonPanel": true,
                "changeYear":true,
                "changeMonth":true
            });
        });
    </script>
</head>
<body class="bodyMain">

<%@include file="/WEB-INF/views/header_html.jsp" %>
<div class="s_main">
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
        <div class="clear"></div>
    </div>
    <!--导航-->
    <ul class="admin_title">
        <li><a href="customer.do?method=customerdata">客户资料</a></li>
        <li><a href="customer.do?method=customerarrears">欠款提醒</a></li>
        <li><a href="customer.do?method=smsinbox">短信管理</a></li>
        <li><a href="smsrecharge.do?method=smsrecharge">短信充值</a></li>
    </ul>
    <!--图片展示-->
    <div class="pic_show">
        <span class="left_pic"></span>

        <div class="center_pic"></div>
        <span class="right_pic"></span>

        <div class="clear"></div>
    </div>
    <!--欠款总额-->
    <div class="balance_count">
        <span class="left_num"></span>

        <div class="center_num">
            <input href="#" class="close_sum"/>

            <div class="sum_all">客户欠款总额<label>520,000</label>¥</div>
            <a href="#" class="more_sum">点击详细</a>

        </div>
        <span class="right_num"></span>

        <div class="clear"></div>
    </div>
    <!--欠款信息-->
    <div class="guide_info">
        <!--客户总信息-->
        <div class="custom">
            <div class="custom_top clear"><span class="top_left"></span><span class="top_right"></span>

                <div class="clear"></div>
            </div>
            <div class="custom_content">
                <h5>系统客户数52,000 本月新增1,800名客户</h5>

                <div class="search_num">
                    <div class="searchLeft"></div>
                    <div class="searchCenter">
                        <input type="text" value="用户名/车牌号/手机号" class="txt" id="input_search_num"/>
                    </div>
                    <div class="searchRight"><input type="button" class="btn" onfocus="this.blur();"/></div>
                    <div class="clear"></div>
                </div>
                <!--客户信息-->
                <dl class="customer">
                    <dt>
                    <div class="cust_left"></div>
                    <div class="cust_title"><label class="title_name">客户名</label><label
                            class="title_sum">消费总额</label><label class="title_time">最近消费时间
                    </label></div>
                    </dt>
                    <dd><label class="num">01</label><label class="cust_name">张建</label><label
                            class="cust_sum">129.526</label><label class="cust_time">2011.10.29</label></dd>
                    <dd><label class="num">02</label><label class="cust_name">苗海伟</label><label
                            class="cust_sum">126,526</label><label class="cust_time">2011.10.27</label></dd>
                    <dd><label class="num">03</label><label class="cust_name">张建</label><label
                            class="cust_sum">129.526</label><label class="cust_time">2011.10.25</label></dd>
                    <dd><label class="num">04</label><label class="cust_name">苗海伟</label><label
                            class="cust_sum">129.526</label><label class="cust_time">2011.10.21</label></dd>
                    <dd><label class="num">05</label><label class="cust_name">张建</label><label
                            class="cust_sum">129.526</label><label class="cust_time">2011.09.19</label></dd>
                </dl>
                <dl class="customer">
                    <dt>
                    <div class="cust_left"></div>
                    <div class="cust_title"><label class="title_name">客户名</label><label
                            class="title_sum">消费总额</label><label class="title_time">最近消费时间
                    </label></div>
                    </dt>
                    <dd><label class="num">01</label><label class="cust_name">张建</label><label
                            class="cust_sum">129.526</label><label class="cust_time">2011.10.29</label></dd>
                    <dd><label class="num">02</label><label class="cust_name">苗海伟</label><label
                            class="cust_sum">126,526</label><label class="cust_time">2011.10.27</label></dd>
                    <dd><label class="num">03</label><label class="cust_name">张建</label><label
                            class="cust_sum">129.526</label><label class="cust_time">2011.10.25</label></dd>
                    <dd><label class="num">04</label><label class="cust_name">苗海伟</label><label
                            class="cust_sum">129.526</label><label class="cust_time">2011.10.21</label></dd>
                    <dd><label class="num">05</label><label class="cust_name">张建</label><label
                            class="cust_sum">129.526</label><label class="cust_time">2011.09.19</label></dd>
                </dl>
                <div class="addClient"><input type="button" class="addUser" value="新增客户"/></div>
                <div class="clear"></div>
            </div>
            <div class="custom_bottom"><span class="bottom_left"></span><span class="bottom_right"></span></div>
        </div>
        <!--快捷短信-->
        <div class="shortcut">
            <div class="shortcut_top"><span class="top_left"></span><span class="top_right"></span></div>
            <div class="shortcut_content">
                <div class="shortcut_title"><strong>快捷短信</strong><img src="images/line.png"
                                                                      style="width:730px; height:2px; display:block;">
                </div>
                <!--短信编辑-->
                <div class="text_edit">
                    <!--短信内容-->
                    <div class="text_cont">
                        <table>
                            <tr>
                                <td><label class="text_lab lis_pho">接收手机</label></td>
                                <td class="txt_edit">
                                    <textarea class="phoNumber"></textarea><br/>
                                    <label class="txt_lbl">共添加了0个手机号码;最多只能输入100个手机号码</label>
                                    <input type="button" class="add_Contacts" value="添加联系人"/>
                                </td>
                            </tr>
                            <tr>
                                <td><label class="text_lab send_cont">发送内容</label></td>
                                <td class="txt_ti">
                                    <textarea row="1" class="phoContent"></textarea><br/>
                                    <label class="txt_lbl txt_char">共输入了0个汉字</label>
                                    <label class="txt_lbl">短信内容最多只能输入200个汉字</label>
                                </td>
                            </tr>
                            <tr>
                                <td><label class="text_lab send_time">发送时间</label></td>
                                <td>
                                    <div class="i_searchTime"><span
                                            id="datetime">起始时间</span></div>
                                    <div class="i_searchTime" style="display:none;"><input type="text"/></div>
                                </td>
                            </tr>

                        </table>
                    </div>
                    <!--短信类型-->
                    <ul class="text_type">
                        <li><input type="radio" name="textType" class="txt_type"/>节日祝福</li>
                        <li><input type="radio" name="textType" class="txt_type"/>保养提醒</li>
                        <li><input type="radio" name="textType" class="txt_type"/>售后调查</li>
                        <li><input type="radio" name="textType" class="txt_type"/>违章提醒</li>
                        <li><input type="radio" name="textType" class="txt_type"/>保险提醒</li>
                        <li><input type="radio" name="textType" class="txt_type"/>应收款提醒</li>
                        <li><input type="radio" name="textType" class="txt_type"/>验车提醒</li>
                        <li><input type="radio" name="textType" class="txt_type"/>施工状态提醒</li>
                        <li><input type="radio" name="textType" class="txt_type"/>生日提醒</li>
                    </ul>
                </div>
                <!--短信编辑结束-->
                <div class="shortcut_send clear">
                    <img src="images/line.png" style="width:730px; height:2px; display:block;">
                    <input type="button" class="affirm_send" value="确认发送"/>
                    <input type="button" class="delayed" value="延时发送"/>
                </div>
            </div>
            <div class="shortcut_bottom"><span class="bottom_left"></span><span class="bottom_right"></span></div>
            <div class="clear"></div>
        </div>
        <div class="clear"></div>
    </div>
    <!--欠款信息结束-->
</div>
<!--弹出框-->
<div id="mask" style="display:block;position: absolute;">
</div>
<iframe id="iframe_PopupBox" style="position:absolute;z-index:5; left:400px; top:400px; display:none;"
        allowtransparency="true" width="630px" height="450px" frameborder="0" src=""></iframe>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>