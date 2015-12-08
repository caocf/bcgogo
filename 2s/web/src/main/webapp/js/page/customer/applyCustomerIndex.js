$().ready(function() {
    var mapMark = new App.Module.MapMark();

    // 这是最简单的调用样例， 如果需要查看详细的调用文档， 请看 bcgogo-mapMark.js 这个文件的注释，里面有详细说明
    // 除了 initDefault() , 还有更自由的函数 init 可以选用，具体的用法也请看 bcgogo-mapMark.js 的注释
    mapMark.initDefault({
        "width":328,
        "selector":"#mapDiv",
        "onSelect":function(event, itemData)  {
            window.location.href="apply.do?method=getApplyCustomersPage&provinceNo="+itemData.provinceNo;
        }
    });

    if ($("#recommendCustomerDiv").length > 0) {
        APP_BCGOGO.Net.asyncPost({
            url: "supplyDemand.do?method=getRecommendShopByShopId",
            dataType: "json",
            success: function (json) {
                initRecommendCustomer(json);
            }
        });

        function initRecommendCustomer(json) {
            var size = 5;

            if (json == null || json.data == null ||json.data.length<=0 ) {
                $(".J_customerRecommendShow").hide();
                return;
            }
            var length = json.data.length;
            var liSize = length < size ? 1 : parseInt((length / size));
            var str = '<ul class="JScrollGroup">';
            for (var liIndex = 0; liIndex < liSize; liIndex ++) {
                str += '<li id="li_' + liIndex + '" class="JScrollItem"></li>';
            }
            str += '</ul>';
            $("#recommendCustomerDiv").html(str);

            for (var liIndex = 0; liIndex < liSize; liIndex ++) {
                var liHtml = '';
                liHtml += '<div class="cuSearch look_customer" style=" width: 664px;height: 220px">';
                liHtml += '    <span class="cart_title lookCustomer_title"></span>     ';

                liHtml += '    <div class="cartBody lineBody newBody" style=" width: 664px;height: 220px">';
                liHtml += '        <table class="tab_cuSearch tabSales tab_cart" style=" width: 664px;" cellpadding="0" cellspacing="0">';
                liHtml += '            <col width="300">';
                liHtml += '                <col>';

                for (var index = 0; index < size; index ++) {
                    var s = liIndex * size + index;
                    if (s >= length) {
                        continue;
                    }
                    var shop = json.data[s];
                    var idStr = shop.idStr;
                    var name = shop.name;
                    var areaName = shop.areaName;
                    var businessScopeStr = G.Lang.isEmpty(shop.businessScopeStr) ? "暂无信息" : shop.businessScopeStr;
                    var shortBusinessScope = businessScopeStr.length > 55 ? businessScopeStr.substring(0, 55) + '...' : businessScopeStr;
                    var tr = '<tr>';
                    tr += '    <td style="padding-left:50px;height: 38px;">';
//                    tr +=          '<a class="blue_color" href="#" onclick="window.open(\'supplier.do?method=redirectSupplierComment&paramShopId=' + idStr + '\')">'+name+'</a>';
                    tr +=          '<a class="blue_color" target="_blank" href="shopMsgDetail.do?method=renderShopMsgDetail&paramShopId=' + idStr + '">'+name+'</a>';
                    tr += '        <br><span class="gray_color">（'+areaName+'）</span>';
                    tr += '    </td>';
                    tr += '    <td style="height: 38px;" title="' + businessScopeStr + '">经营产品：<span>' + shortBusinessScope + '</span></td>';
                    tr += ' </tr>';

                    liHtml += tr;
                }

                liHtml += '                </table>';
                liHtml += '            </div>';
                liHtml += '        </div>';

                $("#li_" + liIndex).html(liHtml);
            }
            $(".J_customerRecommendShow").show();


            var scrollFlowVertical = new App.Module.ScrollFlowVertical();
            scrollFlowVertical.init({
                "selector": "#recommendCustomerDiv",
                "width": 664,
                "height": length <= size ? ((length * 38) + 30) : 220,
                "background": "#fff",
                "onNextComplete": function () {

                },
                "onPrevComplete": function () {

                }
            }).startAutoScroll();
            window.scrollFlowVertical = scrollFlowVertical;
        }
    }

    $(".J_showMoreProductCategory").live("click",function(){
       $(this).closest("span").find(".J_showProductCategoryAll").show();
       $(this).removeClass("icon_more").removeClass("J_showMoreProductCategory").addClass("icon_moreUp").addClass("J_hideMoreProductCategory").text("收拢");
    });
    $(".J_hideMoreProductCategory").live("click",function(){
        $(this).closest("span").find(".J_showProductCategoryAll").hide();
        $(this).removeClass("icon_moreUp").removeClass("J_hideMoreProductCategory").addClass("icon_more").addClass("J_showMoreProductCategory").text("更多");
    });
});
