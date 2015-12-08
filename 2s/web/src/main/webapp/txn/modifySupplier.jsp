<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags" %>
<div class="select_supplier">
    <label class="rad" id="radExist">
        <input type="radio" id="exist" name="radio" checked="checked"/>
        选择现有客户
    </label>
    &nbsp;&nbsp;
    <label class="rad" id="radAdd">
        <input type="radio" id="new" name="radio" />
        新增客户
    </label>
</div>
<div class="height"></div>
<input type="hidden" name="rowStart" id="rowStart" value="1">
<input type="hidden" name="pageRows" id="pageRows" value="10">
<input type="hidden" name="totalRows" id="totalRows" value="0">
<input type="hidden" name="customerId" id="customerId">
<input type="hidden" id="addOrExist" />
<!------------------------------------------现有供应商---------------------------------------------------------------->
<div class="exist_suppliers">
    <label class="lblTitle">查询现有客户</label>
    <div class="divTit"><input type="text" class="txt" id="customerInfoText" filtertype="identity" pagetype="customerdata"  initialValue="客户/联系人/手机" value="客户/联系人/手机"  style="width:200px;color: #999999;" /></div>
    <input type="button" value="查 询"  action-type="search-customer" page="modifySupplier" >
    <div class="clear"></div>
    <table cellpadding="0" cellspacing="0" class="tabRecord tabSupplier" id="customerDatas">
        <col width="25">
        <col>
        <col width="180">
        <col width="200">
        <tr class="tabTitle">
            <td style="padding-left:10px;"></td>
            <td>客户名称</td>
            <td>所在区域</td>
            <td>地址</td>
        </tr>
    </table>
    <div class="height"></div>
    <div class="i_pageBtn">
        <jsp:include page="/common/pageAJAX.jsp">
            <jsp:param name="url" value="customer.do?method=searchCustomerDataAction"></jsp:param>
            <jsp:param name="jsHandleJson" value="initTr"></jsp:param>
            <jsp:param name="dynamical" value="customerSuggest"></jsp:param>
            <jsp:param name="display" value="none"></jsp:param>
        </jsp:include>
    </div>
    <div class="height"></div>
    <div class="button btnSupplier" style="margin-left: 240px;">
        <a class="btnSure" id="merge" style="padding-left: 0px;">下一步</a>
        <a class="btnSure" id="cancel" style="padding-left: 0px;">取 消</a>
    </div>
</div>
<!------------------------------------------新增客户---------------------------------------------------------------->
<form id="customerFormAdd" action="customer.do?method=saveOrUpdateCustomer" method="post">
    <input type="hidden" name="supplierId" id="supplierId3" />
    <input type="hidden" name="thirdCategoryIdStr" id="newThirdCategoryStr"/>
    <input type="hidden" name="VehicleModelIdStr" id="newVehicleModelIdStr" />
    <input type="hidden" name="selectBrandModel" id="newSelectBrandModel" />
    <input type="hidden" name="pageType" id="parentPageType2Add" />
<div class="add_supplier">
    <label class="lblTitle">友情提示：新增客户的资料与现有供应商的一致，若修改则供应商的资料也将被修改！</label>
    <div class="left_customer" <c:if test="${!wholesalerVersion}">style="width:800px;"</c:if>>
        <label class="lblTitle">基本信息</label>
        <div class="divTit" style="width:  325px;">
            <span class="name"><span class="red_color">*</span>名称</span>&nbsp;
            <input type="text" class="txt" style="width:248px;" name="name" id="name3"/>
            <span id="name3_span" style="display: none;"></span>
        </div>
        <div class="divTit" style="width:  325px;">
            <span class="name "><span class="red_color">*</span>所属区域</span>&nbsp;
            <select id="select_province2" name="province" style="margin-left: -5px;" class="txt select">
                <option value="">所有省</option>
            </select>
            <select id="select_city2" name="city" class="txt select">
                <option value="">所有市</option>
            </select>
            <select id="select_township2" name="region" class="txt select">
                <option value="">所有区</option>
            </select>
            <span id="areaInfo2_span" style="display: none;"></span>
        </div>
        <bcgogo:permission>
            <bcgogo:if resourceType="logic"
                       permissions="WEB_VERSION_HAS_CUSTOMER_CONTACTS">
                <!-- 拥有多联系人权限 -->
            </bcgogo:if>
            <bcgogo:else>
                <div class="divTit">
                    <span class="name" name="contact">联系人</span>&nbsp;<input type="text" class="txt" id="contact3" maxlength="11"
                                                                             name="contact"/>
                </div>
                <div class="divTit">
                    <span class="name">手机</span>&nbsp;<input type="text" class="txt" id="mobile3" name="mobile" maxlength="11" />
                </div>
                <div class="divTit">
                    <span class="name">Email</span>&nbsp;<input type="text" class="txt" id="email3" name="email" maxlength="50" />
                </div>
                <div class="divTit">
                    <span class="name">QQ</span>&nbsp;<input type="text" class="txt" id="qq3" name="qq" maxlength="15" />
                </div>
            </bcgogo:else>
        </bcgogo:permission>
        <div class="divTit" style="margin-left: 65px;width: 260px;">
            <input id="input_address2" name="address" style="width:245px;margin-bottom: 7px;" type="text" class="txt"  initValue="详细地址"/>
            <span id="address2_span" style="display: none;"></span>
        </div>

        <div class="divTit">
            <span class="name">座机</span>&nbsp;
            <input type="text" class="txt" id="landline3" name="landLine" maxlength="14" style="width:76px;"/>
            <input type="text" class="txt" id="landlineSecond3" name="landLineSecond" maxlength="14" style="width:76px;"/>
            <input type="text" class="txt" id="landlineThird3" name="landLineThird" maxlength="14" style="width:76px;"/>
        </div>
        <div class="divTit" style="width:  150px;">
            <span class="name" style="width:38px;">传真</span>&nbsp;<input type="text" class="txt" id="fax3" name="fax"/>
        </div>
        <div class="divTit" style="width:  170px;">
            <span class="name">生日</span>&nbsp;<input type="text" class="txt" readonly="true" id="birthdayString2" name="birthdayString"/>
        </div>
        <div class="divTit" style="width:  170px;">
            <span class="name">客户类型</span>&nbsp;
            <select name="customerKind" id="customerKind2" class="txt select" style="margin-left: -5px;">
                <c:forEach items="${categoryList}" var="customerType" varStatus="status">
                    <option value="${customerType.key}">${customerType.value}</option>
                </c:forEach>
            </select>
        </div>
        <div class="divTit" style="width:  150px;">
            <span class="name" <c:if test="${wholesalerVersion}">style="width:38px;"</c:if>>简称</span>&nbsp;<input type="text" class="txt" id="abbr3" name="shortName"/>
            <span id="abbr3_span" style="display: none;"></span>
        </div>
    </div>
    <!-- add by zhuj -->
    <div class="right_customer go_customer">
        <label class="lblTitle">联系人信息<span class="icon_connacter">为主联系人</span></label>
        <table cellpadding="0" cellspacing="0" class="table_contact table_inputContact tb_go">
            <col width="55">
            <col width="80">
            <col>
            <col width="90">
            <col width="37">
            <tr class="title_top">
                <td style="padding-left:5px;">姓名</td>
                <td>手机</td>
                <td>Email</td>
                <td>QQ</td>
                <td></td>
            </tr>
            <tr class="single_contact">
                <td><input type="text" class="txt" name="contacts3[0].name" id="contacts30.name" maxlength="11" /></td>
                <td><input type="text" class="txt" maxlength="11"  name="contacts3[0].mobile" id="contacts30.mobile" style="width:73px;"/></td>
                <td><input type="text" class="txt" name="contacts3[0].email" id="contacts30.email" style="width:120px;" maxlength="50" /></td>
                <td><input type="text" class="txt" name="contacts3[0].qq" id="contacts30.qq" maxlength="15" /></td>
                <td>
                    <input type="hidden" name="contacts3[0].level" id="contacts30.level" value="0"/>
                    <a class="icon_connacter"></a><a class="close"></a>
                    <input type="hidden" name="contacts3[0].mainContact" id="contacts30.mainContact" value="1"/>
                </td>
            </tr>
            <tr class="single_contact">
                <td><input type="text" class="txt" name="contacts3[1].name" id="contacts31.name" maxlength="11" /></td>
                <td><input type="text" class="txt" maxlength="11" name="contacts3[1].mobile" id="contacts31.mobile" style="width:73px;"/></td>
                <td><input type="text" class="txt" name="contacts3[1].email" id="contacts31.email" style="width:120px;" maxlength="50" /></td>
                <td><input type="text" class="txt" name="contacts3[1].qq" id="contacts31.qq" maxlength="15" /></td>
                <td>
                    <input type="hidden" name="contacts3[1].level" id="contacts31.level" value="1"/>
                    <a class="icon_grayconnacter hover"></a><a class="close"></a>

                    <div class="alert">
                        <span class="arrowTop"></span>

                        <div class="alertAll">
                            <div class="alertLeft"></div>
                            <div class="alertBody">点击设为主联系人</div>
                            <div class="alertRight"></div>
                        </div>
                    </div>
                    <input type="hidden" name="contacts3[1].mainContact" id="contacts31.mainContact" value="0"/>
                </td>
            </tr>
            <tr class="single_contact">
                <td><input type="text" class="txt" name="contacts3[2].name" id="contacts32.name" maxlength="11" /></td>
                <td><input type="text" class="txt" maxlength="11"  name="contacts3[2].mobile" id="contacts32.mobile" style="width:73px;"/></td>
                <td><input type="text" class="txt" name="contacts3[2].email" id="contacts32.email" style="width:120px;" maxlength="50" /></td>
                <td><input type="text" class="txt" name="contacts3[2].qq" id="contacts32.qq" maxlength="15" /></td>
                <td>
                    <input type="hidden" name="contacts3[2].level" id="contacts32.level" value="2"/>
                    <a class="icon_grayconnacter hover"></a><a class="close"></a>
                    <div class="alert">
                        <span class="arrowTop"></span>
                        <div class="alertAll">
                            <div class="alertLeft"></div>
                            <div class="alertBody">点击设为主联系人</div>
                            <div class="alertRight"></div>
                        </div>
                    </div>
                    <input type="hidden" name="contacts3[2].mainContact" id="contacts32.mainContact" value="0"/>
                </td>
            </tr>
        </table>
    </div>
    <div class="divTit" style="width:100%;"><span class="name">经营产品</span>&nbsp;<span id="newBusinessScopeSpan"></span></div>
    <div class="divTit" style="width:100%;"><span class="name">主营车型</span>&nbsp;<span id="newVehicleModelContentSpan"></span></div>

    <div class="height"></div>
    <label class="lblTitle">账户信息</label>
    <div class="divTit">
        <span class="name">开户行</span>&nbsp;<input type="text" class="txt" style="width:165px;" id="bank3" name="bank"/>
    </div>
    <div class="divTit">
        <span class="name">开户名</span>&nbsp;<input type="text" class="txt" style="width:165px;" id="accountName3" name="bankAccountName"/>
    </div>
    <div class="divTit">
        <span class="name">账号</span>&nbsp;<input type="text" class="txt" style="width:170px;" id="account3" name="account"/>
    </div>
    <div class="divTit">
        <span class="name">结算方式</span>&nbsp;
        <select class="txt select" id="settlementType3" name="settlementType">
            <option value="">--请选择--</option>
            <c:forEach items="${settlementTypeList}" var="item">
                <option value="${item.key}">${item.value}</option>
            </c:forEach>
        </select>
    </div>
    <div class="divTit">
        <span class="name">发票类型</span>&nbsp;
        <select class="txt select" id="invoiceCategory3" name="invoiceCategory" style="width:90px;">
            <option value="">--请选择--</option>
            <c:forEach items="${invoiceCategoryList}" var="item">
                <option value="${item.key}">${item.value}</option>
            </c:forEach>
        </select>
    </div>
    <div class="divTit" style="width:100%;">
        <span class="name" style="vertical-align:top;">备注</span>&nbsp;<textarea rows="2" cols="40" name="memo" id="memo3" class="txt textarea"></textarea>
    </div>
    <div class="height"></div>
    <div class="button">
        <a class="btnSure" id="sureMerge_jy2">确 定</a>
        <a class="btnSure" id="cancel2" >取 消</a>
    </div>
</div>
</form>
<!------------------------------------------合并资料---------------------------------------------------------------->
<form id="customerForm" action="customer.do?method=saveOrUpdateCustomer" method="post">
    <input type="hidden" name="supplierId" id="supplierId2" />
    <input type="hidden" name="customerId" id="customerId2" />
    <input type="hidden" name="id" id="id2" />
    <input type="hidden" name="thirdCategoryIdStr" id="updateThirdCategoryStr" />
    <input type="hidden" name="VehicleModelIdStr" id="updateVehicleModelIdStr" />
    <input type="hidden" name="selectBrandModel" id="updateSelectBrandModel" />
    <input type="hidden" name="pageType" id="parentPageType2Merge" />
    <input type="hidden" name="mergeContact" id="mergeContact" />
<div class="add_supplier" id="mergeInfo">
    <label class="lblTitle">友情提示：该客户与供应商为同一客户，请您修改统一的资料，确认后客户与供应商的资料将一样！</label>
    <div class="left_customer">
    <label class="lblTitle">基本信息</label>
    <div class="divTit" style="width:  325px;">
        <span class="name"><span class="red_color">*</span>名称</span>&nbsp;<input type="text" class="txt" style="width:138px;" name="name" id="name2"/>
        <span id="name2_span" style="display: none;"></span>
    </div>
    <div class="divTit" style="width:  325px;">
        <span class="name"><span class="red_color">*</span>所属区域</span>&nbsp;
        <select id="select_province1" name="province" class="txt select">
            <option value="">所有省</option>
        </select>
        <select id="select_city1" name="city" class="txt select">
            <option value="">所有市</option>
        </select>
        <select id="select_township1" name="region" class="txt select">
            <option value="">所有区</option>
        </select>
        <span id="areaInfo1_span" style="display: none;"></span>
    </div>
    <div class="divTit" style="margin-left: 65px;width: 260px;">
        <input id="input_address1" name="address" type="text" class="txt"  initValue="详细地址"
               style="width:168px;"/>
        <span id="address1_span" style="display: none;"></span>
    </div>


    <div class="divTit">
        <span class="name">座机</span>&nbsp;
        <input type="text" maxlength="14" class="txt" id="landline2" name="landLine" style="width:76px;"/>
        <input type="text" maxlength="14" class="txt" id="landlineSecond2" name="landLineSecond" style="width:76px;"/>
        <input type="text" maxlength="14" class="txt" id="landlineThird2" name="landLineThird" style="width:76px;"/>
    </div>
    <div class="divTit" style="width:  150px;">
        <span class="name">传真</span>&nbsp;<input type="text" class="txt" id="fax2" name="fax" style="width: 80px;"/>
    </div>
        <div class="divTit" style="width:  170px;">
            <span class="name">生日</span>&nbsp;<input type="text" class="txt" readonly="true"
                                                     id="birthdayString3" name="birthdayString"/>
        </div>
        <div class="divTit" style="width:  170px;">
            <span class="name">客户类型</span>&nbsp;
            <select name="customerKind" id="customerKind3" class="txt select">
                <c:forEach items="${categoryList}" var="customerType" varStatus="status">
                    <option value="${customerType.key}">${customerType.value}</option>
                </c:forEach>
            </select>
        </div>
        <div class="divTit" style="width:  150px;">
            <span class="name">简称</span>&nbsp;<input type="text" class="txt" id="abbr2" name="shortName" style="width: 80px;"/>
            <span id="abbr2_span" style="display: none;"></span>
        </div>
    </div>
    <!-- add by zhuj -->
    <div class="right_customer go_customer">
        <label class="lblTitle">联系人信息<span class="red_color warning" style="display: none">最多只可保留三个联系人！</span></label>
        <table cellpadding="0" cellspacing="0" class="table_contact table_inputContact tb_go table_contact_gen">
            <col width="55">
            <col width="80">
            <col>
            <col width="90">
            <col width="37">
            <tr class="title_top">
                <td style="padding-left:5px;">姓名</td>
                <td>手机</td>
                <td>Email</td>
                <td>QQ</td>
                <td></td>
            </tr>
            <%--<tr class="single_contact">
                <td><input type="text" class="txt" name="contacts2[0].name" id="contacts20.name"/></td>
                <td><input type="text" class="txt" name="contacts2[0].mobile" id="contacts20.mobile" style="width:73px;"/></td>
                <td><input type="text" class="txt" name="contacts2[0].email" id="contacts20.email" style="width:120px;"/></td>
                <td><input type="text" class="txt" name="contacts2[0].qq" id="contacts20.qq"/></td>
                <input type="hidden" name="contacts2[0].level" id="contacts20.level" value="0"/>
                <td><a class="icon_connacter"></a><a class="close"></a></td>
                <input type="hidden" name="contacts2[0].mainContact" id="contacts20.mainContact" value="1"/>
            </tr>
            <tr class="single_contact">
                <td><input type="text" class="txt" name="contacts2[1].name" id="contacts21.name"/></td>
                <td><input type="text" class="txt" name="contacts2[1].mobile" id="contacts21.mobile" style="width:73px;"/></td>
                <td><input type="text" class="txt" name="contacts2[1].email" id="contacts21.email" style="width:120px;"/></td>
                <td><input type="text" class="txt" name="contacts2[1].qq" id="contacts21.qq"/></td>
                <input type="hidden" name="contacts2[1].level" id="contacts21.level" value="1"/>
                <td>
                    <a class="icon_grayconnacter hover"></a><a class="close"></a>
                    <div class="alert">
                        <span class="arrowTop"></span>
                        <div class="alertAll">
                            <div class="alertLeft"></div>
                            <div class="alertBody">点击设为主联系人</div>
                            <div class="alertRight"></div>
                        </div>
                    </div>
                </td>
                <input type="hidden" name="contacts2[1].mainContact" id="contacts21.mainContact" value="0"/>
            </tr>
            <tr class="single_contact">
                <td><input type="text" class="txt" name="contacts2[2].name" id="contacts22.name"/></td>
                <td><input type="text" class="txt" name="contacts2[2].mobile" id="contacts22.mobile" style="width:73px;"/></td>
                <td><input type="text" class="txt" name="contacts2[2].email" id="contacts22.email" style="width:120px;"/></td>
                <td><input type="text" class="txt" name="contacts2[2].qq" id="contacts22.qq"/></td>
                <input type="hidden" name="contacts2[2].level" id="contacts22.level" value="2"/>
                <td>
                    <a class="icon_grayconnacter hover"></a><a class="close"></a>
                    <div class="alert">
                        <span class="arrowTop"></span>
                        <div class="alertAll">
                            <div class="alertLeft"></div>
                            <div class="alertBody">点击设为主联系人</div>
                            <div class="alertRight"></div>
                        </div>
                    </div>
                </td>
                <input type="hidden" name="contacts2[2].mainContact" id="contacts22.mainContact" value="0"/>
            </tr>--%>
        </table>
    </div>

    <div class="divTit" style="width:100%;"><span class="name">经营产品</span>&nbsp;<span id="updateBusinessScopeSpan"></span></div>
    <div class="divTit" style="width:100%;"><span class="name">主营车型</span>&nbsp;<span id="updateVehicleModelContentSpan"></span></div>

    <div class="height"></div>
    <label class="lblTitle">账户信息</label>
    <div class="divTit">
        <span class="name">开户行</span>&nbsp;<input type="text" class="txt" style="width:165px;" id="bank2" name="bank"/>
    </div>
    <div class="divTit">
        <span class="name">开户名</span>&nbsp;<input type="text" class="txt" style="width:165px;" id="accountName2" name="bankAccountName"/>
    </div>
    <div class="divTit">
        <span class="name">账号</span>&nbsp;<input type="text" class="txt" style="width:170px;" id="account2" name="account"/>
    </div>
    <div class="divTit">
        <span class="name">结算方式</span>&nbsp;
        <select class="txt select" id="settlementType2" name="settlementType">
            <option value="">--请选择--</option>
            <c:forEach items="${settlementTypeList}" var="item">
                <option value="${item.key}">${item.value}</option>
            </c:forEach>
        </select>
    </div>
    <div class="divTit">
        <span class="name">发票类型</span>&nbsp;
        <select class="txt select" id="invoiceCategory2" name="invoiceCategory" style="width:90px;">
            <option value="">--请选择--</option>
            <c:forEach items="${invoiceCategoryList}" var="item">
                <option value="${item.key}">${item.value}</option>
            </c:forEach>
        </select>
    </div>
    <div class="divTit" style="width:100%;">
        <span class="name" style="vertical-align:top;">备注</span>&nbsp;<textarea rows="2" cols="40" name="memo" id="memo2" class="txt textarea"></textarea>
    </div>
    <div class="height"></div>
    <div class="button">
        <a class="btnSure" id="sureMerge_jy" >确 定</a>
        <a class="btnSure" id="prev" >返回</a>
    </div>
</div>
</form>
