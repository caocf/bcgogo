<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>后台管理系统——店面管理</title>

    <%-- styles --%>
    <link rel="stylesheet" type="text/css" href="styles/backstage.css"/>
    <link rel="stylesheet" type="text/css" href="styles/backAgent.css"/>
    <link rel="stylesheet" type="text/css" href="styles/storesMange.css"/>
    <%@include file="/WEB-INF/views/style-thirdpartLibs.jsp"%>

    <%-- scripts --%>
    <%@include file="/WEB-INF/views/script-thirdpartLibs.jsp"%>
    <script type="text/javascript" src="js/extension/My97DatePicker/WdatePicker.js"></script>
    <%@include file="/WEB-INF/views/script-common.jsp"%>
    <script type="text/javascript" src="js/txnbase_2.js"></script>
    <script type="text/javascript" src="js/stores_2.js"></script>
    <script type="text/javascript" src="js/searchDefault.js"></script>
</head>
<body>
<div class="main">
    <!--头部-->
    <div class="top">
        <div class="top_left">
            <div class="top_name">统购后台管理系统</div>
            <div class="top_image"></div>
            你好，<span>张三</span>|<a href="#">退出</a></div>
        <div class="top_right"><span>2011.11.23 14:01 星期三</span></div>
    </div>
    <!--头部结束-->
    <div class="body">
        <!--左侧列表-->
        <div class="bodyLeft">
            <ul class="leftTitle">
                <li><a href="#" class="left_register">注册</a>
                    <input type="button" class="btnNum" value="13"/>
                </li>
                <li class="left_hover"><a href="#" class="left_shopping">商品</a><input type="button" class="btnNum" value="10"/></li>
                <li><a href="#" class="left_vehicle">车辆</a><input type="button" class="btnNum" value="8"/></li>
                <li><a href="#" class="left_recruit">招聘</a><input type="button" class="btnNum" value="5"/></li>
                <li><a href="#" class="left_recharge">充值</a><input type="button" class="btnNum" value="28"/></li>
                <li><a href="#" class="left_agaent">代理商</a><input type="button" class="btnNum" value="25"/></li>
                <li><a href="#" class="left_manage">后台管理</a><input type="button" class="btnNum" value="28"/></li>
                <li><a href="dataMaintenance.do?method=createDM" class="left_datamaintain">数据维护</a><input type="button"
                                                                                                          class="btnNum"
                                                                                                          value="5"/>
              <li><a href="shopConfig.do?method=shopIndividuation" class="left_shopConfig">店铺设置</a><input type="button" class="btnNum" value="0"/></li>

              <li><a href="print.do?method=toLeadPage" class="left_print">打印模板</a><input type="button" class="btnNum" value="0"/></li>
                </li>
            </ul>
        </div>
        <!--左侧列表结束-->
        <!--右侧内容-->
        <div class="bodyRight">
            <!--搜索-->
            <div class="rightText">
                <div class="textLeft"></div>
                <div class="textBody"><input type="text" value="店铺名" id="txt_shopName"/>
                <input type="hidden" value="" id="shopId"/>
                </div>
                <div class="textRight"></div>
            </div>
            <div class="rightText">
                <div class="textLeft"></div>
                <div class="textBody"><input type="text" value="店主" id="txt_shopOwner"/></div>
                <div class="textRight"></div>
            </div>
            <div class="rightText">
                <div class="textLeft"></div>
                <div class="textAddressbody"><input type="text" value="地址" id="txt_address"/></div>
                <div class="textRight"></div>
            </div>
            <div class="rightText">
                <div class="textLeft"></div>
                <div class="textBody"><input type="text" value="手机/电话" id="txt_phone"/></div>
                <div class="textRight"></div>
            </div>
            <input type="button" class="rightSearch" value="搜 索"/>
            <!--搜索结束-->
            <!--内容-->
            <div class="rightMain clear">
                <!--店面基本信息-->
                <!--代理商-->
                <div class="rightTime">
                    <div class="timeLeft"></div>
                    <div class="timeBody">
                        <div class="pen"></div>
                        店面基本信息
                    </div>
                    <div class="timeRight"></div>
                </div>
                <!--代理商结束-->
                <!--table-->
                <table cellpadding="0" cellspacing="0" id="agent_tb" class="clear">
                    <col width="73" style="*width:53px;">
                    <col width="153">
                    <col width="68">
                    <col width="188">
                    <col width="68">
                    <col width="297">
                    <tr>
                        <td class="stroes_first">店铺名</td>
                        <td>统瀛车时代</td>
                        <td>地 址</td>
                        <td>苏州相城区元和路205#</td>
                        <td>主营项目</td>
                        <td>汽车保养 钣金喷漆 汽车中修</td>
                    </tr>
                    <tr>
                        <td class="stroes_first">店 主</td>
                        <td> 陈子杰</td>
                        <td>营业时间</td>
                        <td>08:00~21:00</td>
                        <td>成立时间</td>
                        <td>2009.10.15</td>
                    </tr>
                    <tr>
                        <td class="stroes_first">手机号</td>
                        <td> 13888888888</td>
                        <td>签约业务员</td>
                        <td>张剑(000007)</td>
                        <td>店面人员</td>
                        <td>28名
                            <div class="shuaxin">刷新库存数据</div>

                        </td>
                    </tr>
                </table>
                <!--table结束-->
                <!--店面基本信息息结束-->
                <!--历史记录-->
                <!--代理商-->
                <div class="rightTime  salesman clear">
                    <div class="timeLeft"></div>
                    <div class="timeBody">
                        <label>历史记录</label>
                        <!--时间-->
                        <div class="i_searchTime">
                            <div class="textLeft"></div>
                            <div class="textBody">
                                <input type="text" id="datetime1" value="2011年10月10日"/>
                                <img src="../images/datePicker.jpg" onclick="WdatePicker({el:'datetime1'})"/>
                            </div>
                            <div class="textRight"></div>
                        </div>
                        <!--时间-->
                        <div class="i_searchTime">
                            <div class="textLeft"></div>
                            <div class="textBody">
                                <input type="text" id="datetime2" value="2011年10月18日"/>
                                <img src="../images/datePicker.jpg"
                                     style="width:20px; height:20px; position:absolute; right:2px; top:2px;"
                                     onclick="WdatePicker({el:'datetime2'})"/>
                            </div>
                            <div class="textRight"></div>
                        </div>
                        <!--充值-->
                        <div class="i_searchTime recharges select" id="select">

                            <div class="textLeft"></div>
                            <div class="textBody">
                                <input type="text" id="datetime" class="valt" value="充值"/>
                            </div>
                            <div class="textRight"></div>
                            <img src="../images/search_xia.png"
                                 style="display:block;position:absolute;top:10px;right:10px;"/>

                            <div class="option" id="option">
                                <ul>
                                    <li tip="30">30</li>
                                    <li tip="50">50</li>
                                    <li tip="100">100</li>
                                </ul>
                            </div>
                        </div>
                        <!--其他-->
                        <div class="other">
                            <div class="textLeft"></div>
                            <div class="textBody">
                                <input type="text" id="datetime3" value="其他"/>
                            </div>
                            <div class="textRight"></div>
                        </div>
                        <input type="button" class="rightSearch search_or" value="搜 索"/>
                    </div>
                    <div class="timeRight"></div>
                </div>
                <!--代理商结束-->
                <!--table-->
                <table cellpadding="0" cellspacing="0" id="histroy_tb" class="clear">
                    <col width="70">
                    <col width="185">
                    <col width="155">
                    <col width="305">
                    <col width="155">
                    <col width="145">
                    <thead>
                    <tr>
                        <th>NO</th>
                        <th>时间</th>
                        <th>项目</th>
                        <th>内容</th>
                        <th>处理人</th>
                        <th>处理结果</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>1</td>
                        <td> 2011年10月10日</td>
                        <td>充值</td>
                        <td>余额20元，充值100元</td>
                        <td>王五</td>
                        <td>完成</td>
                    </tr>
                    <tr class="agent_bg">
                        <td>2</td>
                        <td>2011年10月18日</td>
                        <td>充值</td>
                        <td>余额20元，充值100元</td>
                        <td>王五</td>
                        <td>完成</td>
                    </tr>
                    </tbody>
                </table>
                <!--table结束-->
                <!--历史记录结束-->
            </div>
            <!--内容结束-->
            <!--圆角-->
            <div class="bottom_crile clear">
                <div class="crile"></div>
                <div class="bottom_x"></div>
                <div style="clear:both;"></div>
            </div>
            <!--圆角结束-->
        </div>
        <!--右侧内容结束-->
    </div>
</div>
</body>
</html>