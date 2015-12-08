<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/includes.jsp" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>商品详情</title>
    <link rel="stylesheet" type="text/css" href="styles/style<%=ConfigController.getBuildVersion()%>.css"/>
    <link rel="stylesheet" type="text/css" href="styles/salesSlip<%=ConfigController.getBuildVersion()%>.css"/>
    <%@include file="/WEB-INF/views/header_script.jsp" %>

    <script type="text/javascript" src="js/page/autoaccessoryonline/bcgogoProduct<%=ConfigController.getBuildVersion()%>.js"></script>
    <script type="text/javascript" src="js/dataUtil<%=ConfigController.getBuildVersion()%>.js"></script>

    <script type="text/javascript">
        defaultStorage.setItem(storageKey.MenuUid, "WEB.SYSTEM_SETTINGS.BCGOGO_RECEIVABLE");
        defaultStorage.setItem(storageKey.MenuCurrentItem,"商品详情");
    </script>
</head>
<body class="bodyMain">
<%@ include file="/WEB-INF/views/header_html.jsp" %>

<%@ include file="/common/messagePrompt.jsp" %>
<div class="i_main clear">
    <div class="mainTitles parts-quoted-price">
        <div class="titleWords">商品详情</div>
        <div class="cl"></div>
    </div>
    <div class="titBody">
        <div class="accessories-container">
            <div class="product-details">
                <div class="details-img fl">
                    <div class="img-original">
                        <c:forEach items="${bcgogoProductDTO.propertyDTOList}" var="propertyDTO" varStatus="status">
                            <img id="bigImageSwitch_${status.index+1}" class="J_bigImageSwitch" src="${propertyDTO.imageCenterDTO.bcgogoProductInfoBigImageURL}" style="display: ${status.first?'':'none'}"/>
                        </c:forEach>
                    </div>
                    <div class="group-img-thumbnails">
                        <c:forEach items="${bcgogoProductDTO.propertyDTOList}" var="propertyDTO" varStatus="status">
                            <div class="J_smallImageSwitch img-thumbnails-item fl ${status.first?'actived':''} ${status.last?'item-last':''}" id="smallImage_${propertyDTO.id}" style="cursor: pointer" data-index="${status.index+1}">
                                <div class="item-arrow"></div>
                                <img src="${propertyDTO.imageCenterDTO.bcgogoProductInfoSmallImageURL}"/>
                            </div>
                        </c:forEach>
                        <div class="cl"></div>
                    </div>
                </div>
                <div class="details-right">
                    <form id="bcgogoProductForm" action="bcgogoReceivable.do?method=confirmBcgogoReceivableOrder" method="post"></form>

                    <input type="hidden" id="productId" value="${bcgogoProductDTO.id}">
                    <input type="hidden" id="productScene" value="${bcgogoProductDTO.productScene}">
                    <div class="font14"><strong>${bcgogoProductDTO.text}</strong></div>
                    <div class="product-line">
                        <div class="line-03">价格：</div>
                        <div class="line-02">&yen; <span id="bcgogoProductPrice">${bcgogoProductDTO.defaultPrice}</span></div>
                        <div class="cl"></div>
                        <div class="i_height clear"></div>
                    </div>
                    <div class="product-line">
                        <div class="line-03">性质：</div>
                        <div class="line-02">
                            <c:choose>
                                <c:when test="${fn:length(bcgogoProductDTO.propertyKindMap)==1}">
                                    <c:forEach items="${bcgogoProductDTO.propertyKindMap}" var="propertyKindMap">
                                        <c:set var="currentPropertyKind" value="${propertyKindMap.key}"/>
                                        <div class="J_selectKind card-select" data-product-property-ids="${propertyKindMap.value}" style="cursor: pointer">${propertyKindMap.key}</div>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach items="${bcgogoProductDTO.propertyKinds}" var="propertyKind">
                                        <div class="J_selectKind card-default" data-product-property-ids="${propertyKindMap.value}" style="cursor: pointer">${propertyKindMap.key}</div>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>

                        </div>
                        <div class="i_height clear"></div>
                    </div>
                    <c:choose>
                        <c:when test="${bcgogoProductDTO.productScene eq 'MULTI_BUY'}">
                            <div class="product-line">
                                <div class="line-03">类型：</div>
                                <div class="line-02">
                                    <c:forEach items="${bcgogoProductDTO.propertyDTOList}" var="propertyDTO" varStatus="status">
                                        <c:choose>
                                            <c:when test="${empty currenPropertyKind}">
                                                <div class="J_TypeAmountLine" style="float:left;width: 420px">
                                                    <div class="J_selectType card-default" data-product-property-id="${propertyDTO.id}" data-product-property-price="${propertyDTO.price}" style="cursor: pointer;margin: 0 6px 0 0">${propertyDTO.type}</div>
                                                    <div>
                                                        <span class="fl">购买数量：</span>
                                                        <div class="card-number J_ProductAmountDiv" style="padding:0;margin: 0">
                                                            <div class="J_subtractBtn" style="cursor: pointer;float: left;width: 10px;text-align: center;-moz-user-select:none;">-</div>
                                                            <div style="float: left"><input name="amount" type="text" data-product-property-id="${propertyDTO.id}" class="J_ModifyAmount" maxlength="5" value="0" autocomplete="off"/></div>
                                                            <div class="J_addBtn" style="cursor: pointer;float: left;width: 10px;text-align: center;-moz-user-select:none;">+</div>
                                                        </div>
                                                        <span class="fl">${bcgogoProductDTO.unit}&nbsp;</span>
                                                    </div>
                                                </div>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="J_TypeAmountLine" style="float:left;width: 420px">
                                                    <div class="J_selectType ${currentPropertyKind==propertyDTO.kind?'card-default':'card-disable'}" data-product-property-price="${propertyDTO.price}" style="cursor: pointer;margin: 0 6px 0 0">${propertyDTO.type}</div>
                                                    <div>
                                                        <span class="fl">购买数量：</span>
                                                        <div class="card-number J_ProductAmountDiv" style="padding:0;margin: 0">
                                                            <div class="J_subtractBtn" style="cursor: pointer;float: left;width: 10px;text-align: center;-moz-user-select:none;">-</div>
                                                            <div style="float: left"><input name="amount" type="text" data-product-property-id="${propertyDTO.id}" class="J_ModifyAmount" maxlength="5" value="0" autocomplete="off"/></div>
                                                            <div class="J_addBtn" style="cursor: pointer;float: left;width: 10px;text-align: center;-moz-user-select:none;">+</div>
                                                        </div>
                                                        <span class="fl">${bcgogoProductDTO.unit}&nbsp;</span>
                                                    </div>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </div>
                                <div class="i_height clear"></div>
                            </div>
                            <div class="card-buy">
                                <input id="createBcgogoOrderBtn" style="cursor: pointer" type="button" class="card-btn" />
                                <a class="icon_QQ" id="bcgogoQQ"></a>
                                <div class="clear"></div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="product-line">
                                <div class="line-03">类型：</div>
                                <div class="line-02">
                                    <c:forEach items="${bcgogoProductDTO.propertyDTOList}" var="propertyDTO">
                                        <c:choose>
                                            <c:when test="${empty currenPropertyKind}">
                                                <div style="float: left" class="J_selectType card-default" data-product-property-id="${propertyDTO.id}" data-product-property-price="${propertyDTO.price}" style="cursor: pointer">${propertyDTO.type}</div>
                                            </c:when>
                                            <c:otherwise>
                                                <div style="float: left" class="J_selectType ${currentPropertyKind==propertyDTO.kind?'card-default':'card-disable'}" data-product-property-price="${propertyDTO.price}" style="cursor: pointer">${propertyDTO.type}</div>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </div>
                                <div class="i_height clear"></div>
                            </div>
                            <div class="clear"></div>
                            <div class="card-buy">
                                <span class="fl">购买数量：</span>
                                <div class="card-number J_ProductAmountDiv" style="padding:0">
                                    <div class="J_subtractBtn" style="cursor: pointer;float: left;width: 10px;text-align: center;-moz-user-select:none;">-</div>
                                    <div style="float: left"><input name="amount" data-product-property-id="${propertyDTO.id}" type="text" class="J_ModifyAmount" maxlength="5" value="1" autocomplete="off"/></div>
                                    <div class="J_addBtn" style="cursor: pointer;float: left;width: 10px;text-align: center;-moz-user-select:none;">+</div>
                                </div>
                                <span class="fl">${bcgogoProductDTO.unit}&nbsp;</span>
                                <c:if test="${bcgogoProductDTO.productScene eq 'ATTACH_BUY'}">
                                    <span class="fl font12-normal"> <a class=" red_color">提示：此处数量填写1份,表示购买1000张卡!</a></span>
                                </c:if>
                                <div class="clear"></div>

                                <c:if test="${bcgogoProductDTO.productScene eq 'ATTACH_BUY'}">
                                    <input type="checkbox" name="checkbox" id="skj_checkbox" autocomplete="off"/>
                                    <label for="skj_checkbox"><span class="font12-normal">购买</span></label>
                                    <strong class="font12-normal"><a class="blue_color" href="bcgogoProduct.do?method=bcgogoProductDetail&bcgogoProductId=${attachedBcgogoProductDTO.id}" target="_blank">${attachedBcgogoProductDTO.name} ${attachedBcgogoProductPropertyDTO.kind}【${attachedBcgogoProductPropertyDTO.type}】</a> 单价：${attachedBcgogoProductPropertyDTO.price}元</strong><!--写死！无奈！悲剧！-->
                                    <input type="text" style="width: 50px" name="skjAmount" data-product-property-id="${attachedBcgogoProductPropertyDTO.id}" data-product-id="${attachedBcgogoProductDTO.id}" id="skjAmount" class="txt" maxlength="5" value="0" autocomplete="off"/>
                                    <span class="font12-normal">台</span>
                                    <div class="clear"></div>
                                </c:if>

                                <input id="createBcgogoOrderBtn" style="cursor: pointer" type="button" class="card-btn" />
                                <a class="icon_QQ" id="bcgogoQQ"></a>
                                <div class="clear"></div>
                            </div>
                        </c:otherwise>
                    </c:choose>

                    <div class="clear"></div>
                </div>
                <div class="clear"></div>
            </div>
            <div class="accessoriesLeft">
                <div class="title">商品介绍</div>
                <div class="content card-content">
                    ${bcgogoProductDTO.description}
                    <div class="clear"></div>
                </div>
                <div class="clear"></div>
            </div>

        </div>
        <div class="accessories-right">
            <div class="accessoriesRight">
                <div class="title">其他可购产品一览</div>
                <div class="clear"></div>
                <div class="content">
                    <ul class="ul-03">
                        <c:forEach items="${bcgogoProductDTOList}" var="otherBcgogoProductDTO">
                            <c:if test="${otherBcgogoProductDTO.id != bcgogoProductDTO.id}">
                                <li>
                                    <div class="goodList_pic">
                                        <div class="goodList_pic_main">
                                            <a href="bcgogoProduct.do?method=bcgogoProductDetail&bcgogoProductId=${otherBcgogoProductDTO.id}"><img style="width: 80px;height: 80px;" src="${otherBcgogoProductDTO.imageCenterDTO.bcgogoProductListSmallImageURL}"/></a>
                                        </div>
                                    </div>
                                    <div class="goodList_info accessoriesList_info">
                                        <p><a href="bcgogoProduct.do?method=bcgogoProductDetail&bcgogoProductId=${otherBcgogoProductDTO.id}" class="blue_color">${otherBcgogoProductDTO.name}</a></p>
                                    </div>
                                    <div class="clear"></div>
                                </li>
                            </c:if>
                        </c:forEach>
                        <div class="clear"></div>
                    </ul>
                </div>
            </div>
        </div>
        <div class="height"></div>
    </div>
</div>
<div id="mask" style="display:block;position: absolute;"></div>
<%@include file="/WEB-INF/views/footer_html.jsp" %>
</body>
</html>