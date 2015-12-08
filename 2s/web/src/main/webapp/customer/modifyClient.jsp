<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="bcgogo" uri="http://www.bcgogo.com/taglibs/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <div class="select_supplier">
        <label class="rad" id="radExist">
            <input id="exist" type="radio" name="radio" checked="checked"/>
            选择现有供应商
        </label>
        &nbsp;&nbsp;
        <label class="rad" id="radAdd">
            <input id="new" type="radio" name="radio" />
            新增供应商
        </label>
    </div>
    <div class="height"></div>
    <input type="hidden" name="rowStart" id="rowStart" value="0">
    <input type="hidden" name="pageRows" id="pageRows" value="15">
    <input type="hidden" name="totalRows" id="totalRows" value="0">
    <input type="hidden" name="supplierId" id="supplierId">
    <input type="hidden" id="supplierShopId">
    <input type="hidden" id="addOrExist" />
    <!------------------------------------------现有供应商---------------------------------------------------------------->
    <div class="exist_suppliers">
        <label class="lblTitle">查询现有供应商</label> <!--identity-->
        <div class="divTit"><input type="text" class="txt" id="supplierInfoText" filtertype="identity"
                                   initialValue="供应商/联系人/手机"
                                   style="margin-right:5px;color: #999999;width: 190px;border:1px solid #7F9DB9; margin-top:3px; height:21px;"
                                   value="供应商/联系人/手机"/></div>
        <input type="button" value="查 询"  action-type="search-supplier" page="modifyClient" >
        <div class="clear"></div>
        <table cellpadding="0" cellspacing="0" class="tabRecord tabSupplier" id="supplierDatas">
            <col width="25">
            <col>
            <col width="180">
            <col width="200">
            <tr class="tabTitle">
                <td style="padding-left:10px;"></td>
                <td>供应商名称</td>
                <td>所在区域</td>
                <td>地址</td>
            </tr>
        </table>
        <div class="height"></div>
        <div class="i_pageBtn">
            <jsp:include page="/common/pageAJAX.jsp">
                <jsp:param name="url" value="supplier.do?method=searchSupplierDataAction"></jsp:param>
                <jsp:param name="jsHandleJson" value="initTr"></jsp:param>
                <jsp:param name="dynamical" value="supplierSuggest"></jsp:param>
                <jsp:param name="display" value="none"></jsp:param>
            </jsp:include>
        </div>
        <div class="height"></div>
        <div class="button btnSupplier" style="margin-left: 240px;margin-top: 25px;">
            <a class="btnSure" id="merge" style="padding-left: 0px;">下一步</a>
            <a class="btnSure" id="cancel" style="padding-left: 0px;">取 消</a>
        </div>
    </div>
    <!------------------------------------------新增供应商---------------------------------------------------------------->
    <form name="supplierForm" id="supplierFormAdd" action="unitlink.do?method=newSupplier" method="post">
        <input type="hidden" name="customerId" id="customerId3">
        <input type="hidden" name="birthdayString" id="birthdayString3">
        <input type="hidden" name="thirdCategoryIdStr" id="newThirdCategoryStr" />
        <input type="hidden" name="VehicleModelIdStr" id="newVehicleModelIdStr" />
        <input type="hidden" name="selectBrandModel" id="newSelectBrandModel" />
        <input type="hidden" name="customerKind" id="customerKind3">
        <input type="hidden" name="pageType" id="parentPageType2Add" />
        <div class="add_supplier">
            <label class="lblTitle">友情提示：新增供应商的资料与现有客户的一致，若修改则客户的资料也将被修改！</label>
            <div class="left_customer">
                <label class="lblTitle">基本信息</label>
                <div class="divTit" style="width:  325px;">
                    <span class="name"><span class="red_color">*</span>名称</span>
                    <input type="text" class="txt" style="width:248px;" name="name" id="name3"/>
                    <span id="name3_span" style="display: none;"></span>
                </div>
                <div class="divTit" style="width:  325px;">
                    <span class="name"><span class="red_color">*</span>所属区域</span>
                    <select id="select_province2" name="province" class="txt select">
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
                <div class="divTit" style="margin-left: 65px;width: 260px;">
                    <input id="input_address2" name="address" type="text" class="txt" initValue="详细地址"
                           style="width:248px;"/>
                    <span id="address2_span" style="display: none;"></span>
                </div>
                <!-- modified by zhuj 微型版和汽修版 -->
                <bcgogo:permission>
                    <bcgogo:if resourceType="logic"
                               permissions="WEB_VERSION_HAS_CUSTOMER_CONTACTS">
                        <!-- 拥有多联系人权限 -->
                    </bcgogo:if>
                    <bcgogo:else>
                        <div class="divTit">
                            <span class="name" name="contact">联系人</span>&nbsp;<input type="text" class="txt" id="contact3"
                                                                                     name="contact"/>
                        </div>
                        <div class="divTit">
                            <span class="name">手机</span>&nbsp;<input type="text" class="txt" id="mobile3" name="mobile"/>
                        </div>
                        <div class="divTit">
                            <span class="name">Email</span>&nbsp;<input type="text" class="txt" id="email3" name="email"/>
                        </div>
                        <div class="divTit">
                            <span class="name">QQ</span>&nbsp;<input type="text" class="txt" id="qq3" name="qq"/>
                        </div>
                    </bcgogo:else>
                </bcgogo:permission>


                <div class="divTit">
                    <span class="name" >座机</span>&nbsp;
                    <input type="text" maxlength="14" class="txt" id="landline3" name="landLine" style="width:76px;"/>
                    <input type="text" maxlength="14" class="txt" id="landlineSecond3" name="landLineSecond" style="width:76px;"/>
                    <input type="text" maxlength="14" class="txt" id="landlineThird3" name="landLineThird" style="width:75px;"/>
                </div>
                <div class="divTit">
                    <span class="name">传真</span>&nbsp;<input type="text" class="txt" id="fax3" name="fax" style="width:90px;"/>
                </div>
                <div class="divTit" style="width:  170px;">
                    <span class="name">简称</span>
                    <input type="text" class="txt" id="abbr3" name="abbr" style="width:90px"/>
                    <span id="abbr3_span" style="display: none;"></span>
                </div>
            </div>

            <!-- 联系人列表 -->
            <bcgogo:hasPermission permissions="WEB_VERSION_HAS_CUSTOMER_CONTACTS">
            <div class="right_customer go_customer">
                <label class="lblTitle">联系人信息<span class="icon_connacter">为主联系人</span></label>
                <table cellpadding="0" cellspacing="0" class="table_contact table_inputContact tb_go">
                    <colgroup>
                        <col width="62">
                        <col width="82">
                        <col >
                        <col width="90">
                        <col width="40">
                    </colgroup>
                    <tr>
                        <td>姓名</td>
                        <td>手机</td>
                        <td>Email</td>
                        <td>QQ</td>
                        <td></td>
                    </tr>
                    <tr class="single_contact">
                        <td><input type="text" class="txt" name="contacts3[0].name" id="contacts30.name" style="width:55px;" maxlength="11" /></td>
                        <td><input type="text" class="txt" name="contacts3[0].mobile" id="contacts30.mobile" style="width:82px;" maxlength="11"/></td>
                        <td><input type="text" class="txt" name="contacts3[0].email" id="contacts30.email" style="width:100px;" maxlength="50" /></td>
                        <td><input type="text" class="txt" name="contacts3[0].qq" id="contacts30.qq" maxlength="15" /></td>
                        <td>
                            <input type="hidden" name="contacts3[0].level" id="contacts30.level" value="0"/>
                            <a class="icon_connacter"></a><a class="close"></a>
                            <input type="hidden" name="contacts3[0].mainContact" id="contacts30.mainContact" value="1"/>
                        </td>
                    </tr>
                    <tr class="single_contact">
                        <td><input type="text" class="txt" name="contacts3[1].name" id="contacts31.name" style="width:55px;" maxlength="11" /></td>
                        <td><input type="text" class="txt" name="contacts3[1].mobile" id="contacts31.mobile" style="width:82px;" maxlength="11"/></td>
                        <td><input type="text" class="txt" name="contacts3[1].email" id="contacts31.email" style="width:100px;" maxlength="50" /></td>
                        <td><input type="text" class="txt" name="contacts3[1].qq" id="contacts31.qq" maxlength="15" /></td>
                        <td>
                            <input type="hidden" name="contacts3[1].level" id="contacts31.level" value="0"/>
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
                        <td><input type="text" class="txt" name="contacts3[2].name" id="contacts32.name" style="width:55px;" maxlength="11" /></td>
                        <td><input type="text" class="txt" name="contacts3[2].mobile" id="contacts32.mobile" style="width:82px;" maxlength="11"/></td>
                        <td><input type="text" class="txt" name="contacts3[2].email" id="contacts32.email" style="width:100px;" maxlength="50" /></td>
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
            </bcgogo:hasPermission>
            <div class="divTit" style="width:100%;"><span class="name">经营产品</span>&nbsp;<span id="newBusinessScopeSpan"></span></div>
            <div class="divTit" style="width:100%;"><span class="name">主营车型</span>&nbsp;<span id="newVehicleModelContentSpan"></span></div>
            <div class="height"></div>
            <label class="lblTitle">账户信息</label>
            <div class="divTit">
                <span class="name">开户行</span>&nbsp;<input type="text" class="txt" style="width:165px;" id="bank3" name="bank"/>
            </div>
            <div class="divTit">
                <span class="name">开户名</span>&nbsp;<input type="text" class="txt" style="width:165px;" id="accountName3" name="accountName"/>
            </div>
            <div class="divTit">
                <span class="name">账号</span>&nbsp;<input type="text" class="txt" style="width:170px;" id="account3" name="account"/>
            </div>
            <div class="divTit">
                <span class="name">结算方式</span>&nbsp;
                <select class="txt select" id="settlementType3" name="settlementTypeId">
                    <option value="">--请选择--</option>
                    <c:forEach items="${settlementTypeMap}" var="settlementType">
                        <option value="${settlementType.key}">${settlementType.value}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="divTit">
                <span class="name">发票类型</span>&nbsp;
                <select class="txt select" id="invoiceCategory3" name="invoiceCategoryId" style="width:90px;">
                    <option value="">--请选择--</option>
                    <c:forEach items="${invoiceCatagoryMap}" var="invoiceCategory">
                        <option value="${invoiceCategory.key}">${invoiceCategory.value}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="divTit" style="width:100%;">
                <span class="name" style="vertical-align:top;">备注</span>&nbsp;<textarea rows="2" cols="40" name="memo" id="memo3" class="textarea">${customerRecordDTO.memo}</textarea>
            </div>
            <div class="height"></div>
            <div class="button">
                <a class="btnSure" id="sureMerge_jy2">确 定</a>
                <a class="btnSure" id="cancel2">取 消</a>
            </div>
        </div>
    </form>
    <!------------------------------------------合并资料---------------------------------------------------------------->
    <form name="supplierForm" id="supplierForm" action="supplier.do?method=updateSupplier" method="post">
        <input type="hidden" name="supplierId" id="supplierId2">
        <input type="hidden" name="customerId" id="customerId2">
        <input type="hidden" name="id" id="id2">
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
                    <span class="name"><span class="red_color">*</span>名称</span>
                    <input type="text" class="txt" style="width:138px;" name="name" id="name2"/>
                    <span id="name2_span" style="display: none;"></span>
                </div>
                <div class="divTit" style="width:  325px;">
                    <span class="name"><span class="red_color">*</span>所属区域</span>
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
                    <input id="input_address1" name="address" type="text" class="txt"  initValue="详细地址" style="width:168px;"/>
                    <span id="address1_span" style="display: none;"></span>
                </div>

                <bcgogo:permission>
                    <bcgogo:if resourceType="logic"
                               permissions="WEB_VERSION_HAS_CUSTOMER_CONTACTS">
                        <!-- 拥有多联系人权限 -->
                    </bcgogo:if>
                    <bcgogo:else>
                        <div class="divTit" >
                            <span class="name" name="contact">联系人</span>&nbsp;<input type="text" class="txt" id="contact2"
                                                                                     name="contact" maxlength="11" />
                        </div>
                        <div class="divTit">
                            <span class="name">手机</span>&nbsp;<input type="text" class="txt" id="mobile2" name="mobile" maxlength="11" />
                        </div>
                        <div class="divTit">
                            <span class="name">Email</span>&nbsp;<input type="text" class="txt" id="email2" name="email" maxlength="50" />
                        </div>
                        <div class="divTit">
                            <span class="name">QQ</span>&nbsp;<input type="text" class="txt" id="qq2" name="qq" maxlength="15" />
                        </div>
                    </bcgogo:else>
                </bcgogo:permission>

                <div class="divTit">
                    <span class="name">座机</span>
                    <input type="text" class="txt" maxlength="14"  id="landline2" name="landLine" style="width:76px;"/>
                    <input type="text" class="txt" maxlength="14"  id="landlineSecond2" name="landLineSecond" style="width:76px;"/>
                    <input type="text" class="txt" maxlength="14"  id="landlineThird2" name="landLineThird" style="width:75px;"/>
                </div>
                <div class="divTit" style="width:  170px;">
                    <span class="name">传真</span>&nbsp;<input type="text" class="txt" id="fax2" name="fax" style="width:90px;"/>
                </div>
                <div class="divTit" style="width:  160px;">
                    <span class="name">生日</span>&nbsp;<input type="text" class="txt" style="width:80px;" id="birthdayString2" readonly="true" name="birthdayString"/>
                </div>
                <div class="divTit">
                    <span class="name">客户类型</span>&nbsp;
                    <select name="customerKind" id="customerKind2" class="txt select">
                        <c:forEach items="${customerTypeMap}" var="customerType" varStatus="status">
                            <option value="${customerType.key}">${customerType.value}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="divTit" style="width:  170px;">
                    <span class="name">简称</span>
                    <input type="text" class="txt" id="abbr2" name="abbr" style="width:90px"/>
                    <span id="abbr2_span" style="display: none;"></span>
                </div>

            </div>
            <!-- 联系人列表 -->
            <bcgogo:hasPermission permissions="WEB_VERSION_HAS_CUSTOMER_CONTACTS">
            <div class="right_customer go_customer">
                <label class="lblTitle">联系人信息<span class="red_color warning" style="display: none">最多只可保留三个联系人！</span></label>
                <table cellpadding="0" cellspacing="0" class="table_contact table_inputContact tb_go table_contact_gen">
                    <col width="50">
                    <col width="80">
                    <col width="80">
                    <col width="90">
                    <col width="40">
                    <tr>
                        <td>姓名</td>
                        <td>手机</td>
                        <td>Email</td>
                        <td>QQ</td>
                        <td></td>
                    </tr>
                </table>
            </div>
            </bcgogo:hasPermission>



            <div class="divTit" style="width:100%;"><span class="name">经营产品</span>&nbsp;<span id="updateBusinessScopeSpan"></span></div>

            <div class="divTit" style="width:100%;"><span class="name">主营车型</span>&nbsp;<span id="updateVehicleModelContentSpan"></span></div>
            <div class="height"></div>
            <label class="lblTitle">账户信息</label>
            <div class="divTit">
                <span class="name">开户行</span>&nbsp;<input type="text" class="txt" style="width:165px;" id="bank2" name="bank"/>
            </div>
            <div class="divTit">
                <span class="name">开户名</span>&nbsp;<input type="text" class="txt" style="width:165px;" id="accountName2" name="accountName"/>
            </div>
            <div class="divTit">
                <span class="name">账号</span>&nbsp;<input type="text" class="txt" style="width:170px;" id="account2" name="account"/>
            </div>
            <div class="divTit">
                <span class="name">结算方式</span>&nbsp;
                <select class="txt select" id="settlementType2" name="settlementTypeId">
                    <option value="">--请选择--</option>
                    <c:forEach items="${settlementTypeMap}" var="settlementType">
                       <option value="${settlementType.key}">${settlementType.value}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="divTit">
                <span class="name">发票类型</span>&nbsp;
                <select class="txt select" id="invoiceCategory2" name="invoiceCategoryId" style="width:90px;">
                    <option value="">--请选择--</option>
                    <c:forEach items="${invoiceCatagoryMap}" var="invoiceCategory">
                        <option value="${invoiceCategory.key}">${invoiceCategory.value}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="divTit" style="width:100%;">
                <span class="name" style="vertical-align:top;">备注</span>&nbsp;<textarea rows="2" cols="40" name="memo" id="memo2" class="txt textarea"></textarea>
            </div>
            <div class="height"></div>
            <div class="button">
                <a class="btnSure" id="sureMerge_jy">确 定</a>
                <a class="btnSure" id="prev">返回</a>
            </div>
        </div>
    </form>
