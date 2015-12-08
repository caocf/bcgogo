<%--
  Created by IntelliJ IDEA.
  User: ndong
  Date: 2015-1-6
  Time: 11:44
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="com.bcgogo.config.ConfigController" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>车险计算器</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=640,target-densitydpi=320,user-scalable=no,maximum-scale=1.0">
    <meta name="format-detection" content="telephone=no">
    <link rel="stylesheet" type="text/css"
          href="/web/styles/mobile/v-insurance<%=ConfigController.getBuildVersion()%>.css">
    <script type="text/javascript" src="/web/js/extension/jquery/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="/web/js/mobile/mBase<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="/web/js/mobile/weChat<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="/web/js/wx/v_insurance<%=ConfigController.getBuildVersion()%>.js"></script>

</head>
<body>

<section style="margin:0px;padding:0px;display:block;overflow: hidden;" id="priceContentDiv" class="main">
    <!-- 内容区域 -->
    <section style="height:auto;padding-bottom:60px;" id="insureContent" class="insure_content">
        <div class="insure_simple_info_wp">
            <span class="fz_26">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                     车险计算器是统购车业为车友精心打造的计算工具,是车险保费报价的开放式算费平台。<br/>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                汽车保险作为一个相对专业的服务产品，每位车主都曾经历过混乱的保险报价和电话骚扰，
                每个车主都在期待一个既方便、又安全，同时还能避免干扰的保险服务方式,欢迎大家使用，让我们的生活更精彩！
            </span>
        </div>
        <!-- 基本信息 -->
        <div class="pd_left biz_Flag">
            <div class="biz_begin_date com_item">
                <span class="c_ff6632">请填写车辆购置价格</span>
            </div>
        </div>
        <section id="bizList" class="biz_sec_wp pd_left biz_Flag">
            <p class="com_quote_item b-item">
                <span class="l_a">车辆购置价格</span>
                <span class="l_b">
                    <span><input id="txtMoney" type="number" class="i-price"/></span>&nbsp;元
                </span>
            </p>
        </section>
        <section class="c_bg"></section>
        <!-- 交强险 -->
        <div class="pd_left biz_Flag">
            <div class="biz_begin_date com_item">
                <span class="com_item_l  c_ff6632">强制保险</span>
            </div>
        </div>
        <ul class="tiplist biz_Flag">
            <li class="l_1">投保</li>
            <li class="l_2">险种</li>
            <li class="l_3">保额</li>
            <li class="l_4">保费</li>
        </ul>
        <section id="bizList" class="biz_sec_wp pd_left biz_Flag">
            <p id="i_compulsory" class="com_quote_item">
                <span class="insurance_chk"><a class="compulsory_cbx_select"></a></span>
                <span class="com_quote_item_l">交强险<a d-type="compulsory" class="t-help"></a></span>
                <span class="com_quote_item_r">
                    <span class="com_quote_item_r_l com_list_table_rcell select-area">
                        <select class="insurance_opt">
                            <option name="6sit" value="950">6座以下</option>
                            <option value="1100">6座及以上</option>
                        </select>
                    </span>
                    <span class="com_quote_item_r_r">¥<span class="insurance_price">0</span></span>
                </span>
            </p>
        </section>
        <section class="c_bg"></section>
        <div class="pd_left biz_Flag">
            <div id="bizBeginDate" class="biz_begin_date com_item">
                <span id="changeAllBiz" class="com_item_l  c_ff6632">商业保险</span>
            </div>
        </div>
        <ul class="tiplist biz_Flag">
            <li class="l_1">投保</li>
            <li class="l_2">险种</li>
            <li class="l_3">保额</li>
            <li class="l_4">保费</li>
        </ul>
        <section style="height:auto" id="quoteContentList" class="quote_content_list">
            <!-- 商业险 -->
            <section id="bizList" class="biz_sec_wp pd_left biz_Flag">
                <p id="i_tpl" class="com_quote_item">
                    <span class="insurance_chk"><a class="item_chk item_selected"></a></span>
                    <span class="com_quote_item_l">第三者责任险<a d-type="tpl" class="t-help"></a></span>
                    <span class="com_quote_item_r">
                        <span class="com_quote_item_r_l com_list_table_rcell select-area">
                            <select class="insurance_opt">
                                <option aValue="801" bValue="685">5万</option>
                                <option aValue="971" bValue="831">10万</option>
                                <option aValue="1120" bValue="958" selected="selected">20万</option>
                                <option aValue="1293" bValue="1106">50万</option>
                                <option aValue="1412" bValue="1208">100万</option>
                            </select>
                        </span>
                        <span class="com_quote_item_r_r">¥<span class="insurance_price">0</span></span>
                    </span>
                </p>
                <p id="i_car_damage" class="com_quote_item no_bd">
                    <span class="insurance_chk"><a class="item_chk item_selected"></a></span>
                    <span class="com_quote_item_l">车辆损失险 <a d-type="carDamage" class="t-help"></a></span>
                    <span class="com_quote_item_r">
                        <span class="com_quote_item_r_r">¥<span class="insurance_price">0</span></span>
                    </span>
                </p>

                <p id="i_abatement" class="com_quote_item no_bd">
                    <span class="insurance_chk"><a class="item_chk item_selected"></a></span>
                    <span class="com_quote_item_l">不计免赔特约险 <a d-type="abatement" class="t-help"></a></span>
                    <span class="com_quote_item_r">
                        <span class="com_quote_item_r_r">¥<span class="insurance_price">0</span></span>
                    </span>
                </p>

                <p id="i_carTheft" class="com_quote_item">
                    <span class="insurance_chk"><a class="item_chk item_selected"></a></span>
                    <span class="com_quote_item_l">全车盗抢险<a d-type="carTheft" class="t-help"></a></span>
                            <span class="com_quote_item_r">
                                <span class="com_quote_item_r_r">¥<span class="insurance_price">0</span></span>
                            </span>
                </p>

                <p id="i_limitOfDriver" class="com_quote_item">
                    <span class="insurance_chk"><a class="item_chk item_selected"></a></span>
                    <span class="com_quote_item_l">司机座位责任险<a d-type="limitOfDriver" class="t-help"></a></span>
		                	<span class="com_quote_item_r">
                                <span class="com_quote_item_r_l com_list_table_rcell select-area">
                                    <select class="insurance_opt">
                                        <option value="10000">1万</option>
                                        <option value="20000">2万</option>
                                        <option value="30000">3万</option>
                                        <option value="40000">4万</option>
                                        <option value="50000">5万</option>
                                    </select>
                                </span>
                                <span class="com_quote_item_r_r">¥<span class="insurance_price">0</span></span>
		                	</span>
                </p>
                <p id="i_limitOfPassenger" class="com_quote_item">
                    <span class="insurance_chk"><a class="item_chk item_selected"></a></span>
                    <span class="com_quote_item_l">乘客座位责任险<a d-type="limitOfPassenger" class="t-help"></a></span>
                    <span class="com_quote_item_r">
                        <span class="com_quote_item_r_l com_list_table_rcell select-area">
                            <select class="insurance_opt">
                                <option value="10000">1万</option>
                                <option value="20000">2万</option>
                                <option value="30000">3万</option>
                                <option value="40000">4万</option>
                                <option value="50000">5万</option>
                            </select>
                        </span>
                        <span class="com_quote_item_r_r">¥<span class="insurance_price">0</span></span>
                    </span>
                </p>
                <p id="i_breakageOfGlass" class="com_quote_item">
                    <span class="insurance_chk"><a class="item_chk item_selected"></a></span>
                    <span class="com_quote_item_l">玻璃单独破碎险<a d-type="breakageOfGlass" class="t-help"></a></span>
                    <span class="com_quote_item_m"></span>
                    <span class="com_quote_item_r">
                        <span class="com_quote_item_r_l com_list_table_rcell select-area">
                            <select class="insurance_opt">
                                <option>国产</option>
                                <option value="import">进口</option>
                            </select>
                        </span>
                        <span class="com_quote_item_r_r">¥<span class="insurance_price">0</span></span>
                    </span>
                </p>
                <p id="i_carDamageDW" class="com_quote_item">
                    <span class="insurance_chk"><a class="item_chk item_selected"></a></span>
                    <span class="com_quote_item_l">车身划痕损失险<a d-type="carDamageDW" class="t-help"></a></span>
                    <span class="com_quote_item_r">
                        <span class="com_quote_item_r_l com_list_table_rcell select-area">
                            <select class="insurance_opt">
                                <%--aValue:购置价格30w以下,bVaue:50w以上,cValue:30w-50w--%>
                                <option aValue="400" bValue="850" cValue="585">2千</option>
                                <option aValue="570" bValue="1100" cValue="900">5千</option>
                                <option aValue="760" bValue="1500" cValue="1170">1万</option>
                                <option aValue="1140" bValue="2250" cValue="1780">2万</option>
                            </select>
                        </span>
                        <span class="com_quote_item_r_r">¥<span class="insurance_price">0</span></span>
                    </span>
                </p>
                <p id="i_selfignite" class="com_quote_item">
                    <span class="insurance_chk"><a class="item_chk item_selected"></a></span>
                    <span class="com_quote_item_l">自燃损失险<a d-type="selfignite" class="t-help"></a></span>
                    <span class="com_quote_item_r">
                        <span class="com_quote_item_r_r">¥<span class="insurance_price">0</span></span>
                    </span>
                </p>
            </section>
            <section class="c_bg"></section>
        </section>
    </section>

    <!-- 底部固定菜单 -->
    <section style="display: block" id="dock_oper_panel">
        <%--<div style="display:none" id="insureTotalPremium" class="insure_type_total_premium_wp">--%>
        <%--<div class="insure_total">保险合计</div>--%>
        <%--<div id="bizSumEL" class="biz_total_premium_wp com_item biz_Flag">--%>
        <%--<span class="com_quote_item_l biz_total_txt cbx_unck">商业险</span><span style="color:#FF6632;"--%>
        <%--class="com_item_r">¥<span--%>
        <%--id="bizQuote"></span></span>--%>
        <%--</div>--%>
        <%--<div id="forceSumEL" class="force_total_premium_wp com_item">--%>
        <%--<span class="com_quote_item_l force_total_txt cbx_unck">交强险+车船税</span><span style="color:#FF6632"--%>
        <%--id="forceQuote"--%>
        <%--class="force_car_tax_total com_item_r"></span>--%>
        <%--</div>--%>
        <%--<div id="accidentSumEL" class="biz_total_premium_wp com_item biz_Flag">--%>
        <%--<span class="com_quote_item_l biz_total_txt cbx_unck">意外险</span><span style="color:#FF6632;"--%>
        <%--class="com_item_r">¥<span--%>
        <%--id="accidentQuote">0</span></span>--%>
        <%--</div>--%>
        <%--</div>--%>
        <div class="dock_oper_panel_bottom_wp">
            <div id="orderDiv" class="order_total_wp">
                市场指导价
                <span style="color:#ff6632">¥<span id="total">0</span></span>
            </div>
        </div>
    </section>
    <section style="display: block" class="help-swing">
        <div class="d-close-btn">
            <a id="closeBtn" class="close-btn">关闭</a>
        </div>
        <div class="txt txt_compulsory">
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            家用6座以下950元/年，家用6座及以上1100元/年
           <br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;机动车交通事故责任强制保险是我国首个由国家法律规定实行的强制保险制度。
            《机动车交通事故责任强制保险条例》规定：交强险是由保险公司对被保险机动车发生道路交通事故造成受害人(不包括本车人员和被保险人)的人身伤亡、财产损失，在责任限额内予以赔偿的强制性责任保险。
            交强险责任限额分为死亡伤残赔偿限额50000元、医疗费用赔偿限额8000元、财产损失赔偿限额2000元以及被保险人在道路交通事故中无责任的赔偿限额。其中无责任的赔偿限额分别按照以上三项限额的20％计算。<br/>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;责任限额是指被保险机动车发生道路交通事故，保险公司对每次保险事故所有受害人的人身伤亡和财产损失所承担的最高赔偿金额。
        </div>
        <div class="txt txt_tpl">
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;第三者责任险是指被保险人或其允许的驾驶人员在使用保险车辆过程中发生意外事故，致使第三者遭受人身伤亡或财产直接损毁，
            依法应当由被保险人承担的经济责任，保险公司负责赔偿。同时，若经保险公司书面同意，被保险人因此发生仲裁或诉讼费用的，保险公司在责任限额以外赔偿，但最高不超过责任限额的30％。
            因为交强险在对第三者的财产损失和医疗费用部分赔偿较低，可考虑购买第三者责任险作为交强险的补充。
        </div>
        <div class="txt txt_carDamage">
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;车辆购置价格*1.2%<br/>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 车辆损失险是车辆保险中用途最广泛的险种，它负责赔偿由于自然灾害和意外事故造成的自己车辆的损失。
            无论是小剐小蹭，还是损坏严重，都可以由保险公司来支付修理费用。<br/>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 被保险人或其允许的合格驾驶员在使用保险车辆过程中，因下列原因造成保险车辆的损失，
            保险公司负责赔偿：1．碰撞、倾覆；2．火灾、爆炸；3．外界物体倒塌、空中运行物体坠落、保险车辆行驶中平行坠落；4．雷击、暴风、龙卷风、暴雨、洪水、海啸、地陷、冰陷、崖崩、雪崩、雹灾、泥石流、滑坡；5.
            载运保险车辆的渡船遭受自然灾害（只限于有驾驶员随车照料者）。<br/>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 发生保险事故时，被保险人或其允许的合格驾驶员对保险车辆采取施救、保护措施所支出的合理费用，
            保险公司负责赔偿。但此项费用的最高赔偿金额以责任限额为限。
        </div>
        <div class="txt txt_abatement">
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; (车辆损失险+第三者责任险)×20%<br/>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 负责赔偿在车损险和第三者责任险中应由被保险人自己承担的免赔金额，即100%赔付。<br/>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 不计免赔特约险为附加险，必须在投保车损险和第三者责任险之后方可投保该险种。
        </div>
        <div class="txt txt_carTheft">
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;车辆购置价格*1%<br/>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;指保险车辆全车被盗窃、被抢劫、被抢夺，经县级以上公安刑侦部门立案侦查证实满一定时间没有下落的，由保险公司在保险金额内予以赔偿。
            如果是车辆的某些零部件被盗抢，如轮胎被盗抢、车内财产被盗抢、后备箱内的物品丢失，保险公司均不负责赔偿。 但是，对于车辆被盗抢期间内，保险车辆上零部件的损坏、丢失，保险公司一般负责赔偿。<br/>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;全车盗抢险为附加险，必须在投保车辆损失险之后方可投保该险种。
        </div>
        <div class="txt txt_limitOfDriver">
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 司机责任险=保额*费率<br>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;统称为车上责任险，包括司机座位和乘客座位，主要是指在发生意外情况下，保险公司对司机座位的人员和乘客的人身安全进行赔偿。<br/>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;严格来说，司机责任险并不是一个独立的险种，而是商业车险中车上人员责任险的一部分，除此之外，车主还可以为乘客座位投保，一般选择的赔偿限额为1-5万元
        </div>
        <div class="txt txt_limitOfPassenger">
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 乘客责任险=保额*费率<br/>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;被保险人允许的合格驾驶员在使用保险车辆过程中发生保险事故，致使车内乘客人身伤亡，依法应由被保险人承担的赔偿责任，保险人依照保险合同的约定给予赔偿。
        </div>
        <div class="txt txt_breakageOfGlass">
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            负责赔偿保险车辆在使用过程中，发生本车玻璃发生单独破碎的保险公司按照保险合同进行赔偿。玻璃单独破碎险中的玻璃是指风档玻璃和车窗玻璃，如果车灯、车镜玻璃破碎及车辆维修过程中造成的破碎，保险公司不承担赔偿责任。<br/>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 玻璃单独破碎险为附加险，必须在投保车辆损失险之后方可投保该险种。
        </div>
        <div class="txt txt_carDamageDW">
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;无明显碰撞痕迹的车身划痕损失，保险公司负责赔偿。 <br/>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;车身划痕险为附加险，必须在投保车辆损失险之后方可投保该险种。
        </div>
        <div class="txt txt_selfignite">
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;车辆购置价格*0.15% <br/>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 负责赔偿因本车电器、线路、供油系统发生故障及运载货物自身原因起火造成车辆本身的损失。当车辆发生部分损失，
            按照实际修复费用赔偿修理费。如果车辆自燃造成整体烧毁或已经失去修理价值，则按照出险时车辆的实际价值赔偿，但不超过责任限额。
        </div>
    </section>
</section>
   <div id="helpMask" class="help-mask"></div>
</body>
</html>
