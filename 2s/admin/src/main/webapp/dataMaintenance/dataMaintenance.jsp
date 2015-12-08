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
    <link rel="stylesheet" type="text/css" href="styles/dataMaintenance.css"/>
    <%@include file="/WEB-INF/views/style-thirdpartLibs.jsp"%>
    <style type="text/css">
        div#users-contain { width: 350px; margin: 20px 0; }
		div#users-contain table { margin: 1em 0; border-collapse: collapse; width: 100%; }
		div#users-contain table td, div#users-contain table th { border: 1px solid #eee; padding: .6em 10px; text-align: left; }
        fieldset { padding:0; border:0; margin-top:25px; }
    </style>

    <%-- scripts --%>
    <%@include file="/WEB-INF/views/script-thirdpartLibs.jsp"%>
    <script type="text/javascript" src="js/extension/jquery/plugin/jquery.validate.js"></script>
    <%@include file="/WEB-INF/views/script-common.jsp"%>
    <script type="text/javascript" src="js/txnbase_2.js"></script>
    <script type="text/javascript" src="js/stores_2.js"></script>
    <!--时间-->
    <script type="text/javascript" src="js/dataMaintenance.js"></script>
    <script type="text/javascript" src="js/bcgogo.js"></script>
    <script type="text/javascript" src="js/mask.js"></script>
    <script type="text/javascript" src="js/page/dataMaintenance/permission.js"></script>

    <script type="text/javascript">
        var labelValue;
        $(function() {
            $(".fileInfo input:eq(0)").attr("checked", true);
            labelValue = $(".fileInfo input:eq(0)").next().html();
            $(".fileInfo input[type='radio']").live("click", function() {
                labelValue = $(this).next().html();
            });
        });
        function thisformsubmit() {
            if (!thisform.productFile.value) {
                alert("请选择需要上传的文件!");
            } else {
                var filesuffix = thisform.productFile.value.split(".")[1];
                if (filesuffix != 'csv') {
                    alert("请选择CSV文件!");
                }
                else {
                    if (confirm("您选择的是:" + labelValue + ",确认上传?")) {
                        thisform.submit();
                    } else {
                        return;
                    }
                }
            }
        }
    </script>
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
        <%@include file="/WEB-INF/views/left.jsp" %>
        <!--左侧列表结束-->
        <!--右侧内容-->
        <div class="bodyRight">
            <!--搜索-->
            <div class="rightText">
                <div class="textLeft"></div>
                <div class="textBody"><input type="text" value="店铺名" id="txt_shopName"/></div>
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
                <div class="rightTitle">
                    <div class="rightLeft"></div>
                    <div class="rightBody">
                        <div class="titleHover" id="dataUpload">数据上传</div>
                        <div class="title" id="smsControl">短信模板</div>
                        <div class="title" id="systemConfig">系统配置</div>
                        <div class="title" id="solrThesaurus">SOLR词库</div>
                        <div class="title" id="reindexSolr">索引重建</div>
                        <div class="title" id="smsYiMei">亿美操作</div>
                        <%--<div class="title" id="permission">权限配置</div>--%>
                    </div>
                </div>
                <div class="fileInfo">

                    <form:form action="dataMaintenance.do?method=insertproductdata" method="post" name="thisform"
                               commandName="command" enctype="multipart/form-data" id="dataUpload_form">
                        <input type="radio" name="productFileType" value="0"/>
                        <label>上传车辆信息</label><br><br>
                        <input type="radio" name="productFileType" value="1"/>
                        <label>上传商品信息</label><br><br>
                        <input type="radio" name="productFileType" value="2"/>
                        <label>上传产品车型对应信息</label><br><br>
                        <input type="radio" name="productFileType" value="3"/>
                        <label>上传车牌前缀信息</label><br><br>
                        <input type="radio" name="productFileType" value="4"/>
                        <label>上传地区信息</label><br><br>
                        <input type="file" name="productFile"/>
                        <input style="margin-left: 30px" type="button" value="上传文件" onclick="thisformsubmit()"/><br><br>
                    </form:form>

                    <form action="dataMaintenance.do?method=updateSolrThesaurus" method="post" name="solrThesaurusForm"
                          enctype="multipart/form-data" id="solrThesaurus_form" style="display:none">
                        <label>新词库文件</label> <input id="sfFile1" type="file" name="stFile" class="required"/>
                        <label>原词库文件</label> <input id="sfFile2" type="file" name="stFile" class="required"/>
                        <input type="button" onclick="dataMT.submitFile(solrThesaurusForm)" value="提交"/>
                    </form>

                    <div id="smsYiMei_form" style="display:none">
                        <label>选择命令：</label>
                        <select id="yiMei_options">
                            <optgroup label="查询余额">
                                <option value="balance_industry">行业通道查询</option>
                                <option value="balance_marketing">营销通道查询</option>
                            </optgroup>
                            <optgroup label="注册">
                                <option value="register_industry" title="第一次或注销后后注册">行业通道注册</option>
                                <option value="register_marketing" title="第一次或注销后后注册">营销通道注册</option>
                            </optgroup>
                            <optgroup label="注销">
                                <option value="logout_industry">行业通道注销</option>
                                <option value="logout_marketing">营销通道注销</option>
                            </optgroup>
                        </select>
                        <span style="display:none;margin-left: 5%">
                            <label>搜索用户帐号：</label>
                            <label id="yimei_show"></label>
                        </span>
                        <input type="button" id="yiMei_option" value="确定"/>
                    </div>

                    <div id="reindexSolr_form" style="display:none">
                        <label>选择命令：</label>
                        <select id="reindexSolr_content">
                            <optgroup label="清空SOLR">
                                <option value="clearAllProduct">清空所有产品</option>
                                <option value="clearAllVehicle" title="清空后需要重现导入数据">清空所有车型</option>
                                <option value="clearAllOrder" title="清空后需要重现导入数据">清空所有单据</option>
                                <option value="clearAllCustomerAndSupplier" title="清空后需要重现导入数据">清空所有客户与供应商</option>
                            </optgroup>
                            <optgroup label="重建产品索引">
                                <option value="reindexCommonProduct">标准商品</option>
                                <option value="reindexLocalProduct" title="包含库存等信息" class="local_product">本店面商品和库存</option>
                                <option value="reindexAllLocalProduct" title="包含库存等信息">所有店面商品和库存</option>
                            </optgroup>
                            <optgroup label="重建车型首字母索引">
                                <option value="reindexFLVehicle" title="必须保证SOLR中有车型">车型首字母</option>
                            </optgroup>
                            <optgroup label="重建单据索引">
                                <option value="reindexOrder_All">所有单据</option>
                                <option value="reindexOrder_DS" class="local_product">本店面单据</option>
                            </optgroup>
                            <optgroup label="重建客户供应商索引">
                                <option value="reindexCustomerAndSupplier_All">所有客户供应商</option>
                                <option value="reindexCustomer_All">所有客户</option>
                                <option value="reindexSupplier_All">所有供应商</option>
                                <option value="reindexSupplier_shop" class="local_product">本店供应商</option>
                                <option value="reindexCustomer_shop" class="local_product">本店客户</option>
                            </optgroup>
                            <optgroup label="重建施工项目索引">
                                <option value="reindexRepairService_All">所有施工项目</option>
                                <option value="reindexRepairService_shop" class="local_product">本店面施工项目</option>
                            </optgroup>
                            <optgroup label="重建车辆索引">
                                <option value="reindexVehicleLicenceNo_All">所有车辆</option>
                                <option value="reindexVehicleLicenceNo_shop" class="local_product">本店面车辆</option>
                            </optgroup>
                            <optgroup label="重建经营产品索引">
                                <option value="reindexProductCategory">经营产品</option>
                            </optgroup>
                            <optgroup label="重建店铺索引">
                                <option value="reindexShop_All">所有店铺</option>
                                <option value="reindexShop_shop" class="local_product">本店</option>
                            </optgroup>
                        </select>
                        <span style="display:none;margin-left: 5%" id="bcgogoShop_span">
                            <label>搜索用户帐号：</label>
                            <input type="text" id="bcgogoShop" name="bcgogoShop"
                                   onkeyup="dataMT.LazzySearcher.lazzySearch(this)"/>
                        </span>

                          <span style="display:none;margin-left: 5%" id="bcgogoOrderType_span">
                            <label>单据类型：</label>
                            <select id="orderType">
                                <option value="">----所有单据----</option>
                                <option value="REPAIR">施工单</option>
                                <option value="INVENTORY">入库单</option>
                                <option value="PURCHASE">采购单</option>
                                <option value="RETURN">入库退货单</option>
                                <option value="SALE">销售单</option>
                                <option value="WASH_BEAUTY">洗车单</option>
                                <option value="MEMBER_RETURN_CARD">会员退卡</option>
                                <option value="MEMBER_BUY_CARD">会员购卡</option>
                                <option value="SALE_RETURN">销售退货单</option>
                                <option value="CUSTOMER_STATEMENT_ACCOUNT">客户对账单</option>
                                <option value="SUPPLIER_STATEMENT_ACCOUNT">供应商对账单</option>
                                <option value="INVENTORY_CHECK">库存盘点单</option>
                                <option value="ALLOCATE_RECORD">仓库调拨单</option>
                                <option value="INNER_PICKING">内部领料单</option>
                                <option value="INNER_RETURN">内部退料</option>
                                <option value="BORROW_ORDER">借调单</option>
                                <option value="RETURN_ORDER">借调归还单</option>
                                <option value="PRE_BUY_ORDER">求购单</option>
                                <option value="QUOTED_PRE_BUY_ORDER">求购报价单</option>
                            </select>
                        </span>
                        <input type="button" onclick="dataMT.reindexAction.reindexByCommand($('#reindexSolr_content').val())"
                               value="确认重建" id="reindexButton" style="margin-left: 10%"/>
                    </div>

                    <div id="smsControl_form" style="display:none" class="messageTemplate_form">
                        <div>
                            <label style="float:left;">短信模板查询 </label>

                            <div style="float:left;"><input id="msgTemplate_type" type="text" tabindex="6" autocomplete="off"
                                                            onfocus="if(this.value=='类型'){this.value=''}"
                                                            onblur="if(this.value==''){this.value='类型'}" value="类型"/>
                            </div>

                            <div style="float:left;">
                                <input type="button" id="msgTemplateSearchBtn" onfocus="this.blur();" value="查询"/>
                            </div>
                        </div>
                        <div class="clear"></div>
                        <div class="addMsgTemplateBtn_div">
                            <input type="button" id="addMsgTemplateBtn" onfocus="this.blur();" value="新增" style="float:right"/>
                        </div>
                      <div>
                        <table cellpadding="0" cellspacing="0" class="table_msgTemplate" id="table_msgTemplate" width="830px">
                          <col width="50">
                          <col width="80">
                          <col width="100">
                          <col width="100">
                          <col width="100">
                          <col width="200">
                          <col width="100">
                          <%--<col width="90">--%>
                          <tr class="dm_table_title">
                            <td style="border-left:none;">编号</td>
                            <td>模板名</td>
                            <td>类型</td>
                            <td>场景</td>
                            <td>必要性</td>
                            <td>内容</td>
                            <td>操作</td>
                          </tr>
                        </table>
                      </div>

                        <div class="simplePageAJAX">
                            <jsp:include page="/common/simplePageAJAX.jsp">
                                <jsp:param name="url" value="dataMaintenance.do?method=searchMessageTemplate"></jsp:param>
                                <jsp:param name="data" value="{pageNo:1,shopId:-1,type:''}"></jsp:param>
                                <jsp:param name="jsHandleJson" value="initMsgTemplateTable"></jsp:param>
                                <jsp:param name="dynamicalID" value="dynamical1"></jsp:param>
                            </jsp:include>
                        </div>

                    </div>

                    <div id="systemConfig_form" style="display:none" class="systemConfig_form">
                        <div>
                            <label style="float:left;">配置查询 </label>

                            <div style="float:left;"><input id="config_key" type="text" tabindex="6" autocomplete="off"
                                                            onfocus="if(this.value=='key'){this.value=''}"
                                                            onblur="if(this.value==''){this.value='key'}" value="key"/>
                            </div>
                            <div style="float:left;"><input id="config_value" type="text" tabindex="6" autocomplete="off"
                                                            onfocus="if(this.value=='value'){this.value=''}"
                                                            onblur="if(this.value==''){this.value='value'}" value="value"/>
                            </div>
                            <div style="float:left;">
                                <input type="button" id="configSearchBtn" onfocus="this.blur();" value="查询"/>
                            </div>
                        </div>
                        <div class="clear"></div>
                        <div class="addConfigBtn_div">
                            <input type="button" id="addConfigBtn" onfocus="this.blur();" value="新增"/>
                        </div>

                        <div>
                            <table cellpadding="0" cellspacing="0" class="config_table" id="table_config" width="830px">
                                <col width="120">
                                <col width="170">
                                <col width="220">
                                <col width="220">
                                <col width="100">
                                <%--<col width="90">--%>
                                <tr class="dm_table_title">
                                    <td style="border-left:none;">编号</td>
                                    <td>键</td>
                                    <td>值</td>
                                    <td>描述</td>
                                    <td>操作</td>
                                </tr>
                            </table>
                        </div>

                        <div class="simplePageAJAX">
                            <jsp:include page="/common/simplePageAJAX.jsp">
                                <jsp:param name="url" value="dataMaintenance.do?method=searchConfig"></jsp:param>
                                <jsp:param name="data" value="{pageNo:1,shopId:-1,name:'',value:''}"></jsp:param>
                                <jsp:param name="jsHandleJson" value="initConfigTable"></jsp:param>
                                <jsp:param name="dynamicalID" value="dynamical2"></jsp:param>
                            </jsp:include>
                        </div>

                    </div>

                     <%--权限配置--%>
                    <div id="permission_form" style="display:none">
                        <%@include file="permission.jsp" %>
                </div>
                </div>
                <!--内容结束-->
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
<ul class="suggestionMain" style="list-style: none;color:#000000;">
</ul>
<div id="mask" style="display:block;position: absolute;">
</div>
<iframe name="iframe_PopupBox" id="iframe_PopupBox" style="position:absolute;z-index:5; left:400px; top:200px; display:none;"  allowtransparency="true" width="1000px" height="1500px" frameborder="0" src=""></iframe>
</body>
</html>