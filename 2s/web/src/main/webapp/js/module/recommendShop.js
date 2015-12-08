/**
 * @author:ndong
 * @date:2014-8-20
 */
;
(function(){

    APP_BCGOGO.namespace("Module.recommendShop");

    var self,
        // Class Names
        C = {
            shrink:"ui-bcgogo-recommendShop-shrink",
            comp:"ui-bcgogo-recommendShop",
            container: "ui-bcgogo-recommendShop-container",
            item:"ui-bcgogo-recommendShop-container-item",
            sBtn:"ui-bcgogo-recommendShop-series-Btn",
            bBtn:"ui-bcgogo-recommendShop-brand-series-Btn",
            bLastBtn:"ui-bcgogo-recommendShop-brand-last-Btn",
            cLastBtn:"ui-bcgogo-recommendShop-content-last-Btn",
            searchBtn:"ui-bcgogo-recommendShop-search-btn",
            closeBtn:"ui-bcgogo-recommendShop-close-btn"
        },
        //template
        T={
            shrink:'<div class="r_commodities_relative '+C.shrink+'"><div class="tip-recommend-shop J_show_ad"> </div></div>',
            header:'<div class="title_top"><div class="title_off '+C.closeBtn+'"></div>推荐商户</div> ',
           search:'<div class="search"><input placeholder="主营车型" type="text" class="s_input standardVehicleBrand" /><div class="search_retrieve_btn '+C.searchBtn+'"></div></div> ',
            container1: '<div class="container1 '+C.item+'"><div class="shop_home"></div><div class="nano"><div class="nano-content"><ul></ul></div></div></div> ',
            container2: '<div class="container2 '+C.item+'"><div class="turn_back '+C.bLastBtn+'">上一级</div><div class="nano"><div class="nano-content"><ul></ul></div></div></div> ',
            container3: '<div class="container3 '+C.item+'"><div class="turn_back '+C.cLastBtn+'">上一级</div><div class="nano"><div class="nano-content"><ul></ul></div></div></div> '
        },
        options={
            s_url:"apply.do?method=getApplySuppliersPage",
            b_content_url:"recommendShop.do?method=getRecommendShopBySeries",
            shop_detail_url:"shopMsgDetail.do?method=renderShopMsgDetail&paramShopId="
        },
        _$=jQuery_1_10_2; //引用到jquery-1.10.2


    APP_BCGOGO.Module.recommendShop = {
        _$frame:null,
        _data:null,
        _data_map:null,
        _left:70,
        _top:155,
        _width:163,
        _height:450,
        _c_height:380,
        _title:null,
        _draw:function (data) {
            if (!data) {
                return;
            }
            self._data = data;
            var rootNode=data;
            self._data_map={};
            var nodes=rootNode.children;
            var nodeStr="";
            //生成一级系列元素
            for(var i=0;i<nodes.length;i++){
                var node=nodes[i];
                nodeStr+='<li class="li_shop_r '+C.sBtn+'" sName="'+node.text+'"><a>'+node.text+'</a><span>'+node.childSize+'</span></li>';
                self._data_map[node.text]=node.children;
            }
            $(".container1 ul",self._$frame).append(nodeStr);
            //设置scroll样式
            _$(".nano").nanoScroller({alwaysVisible: true});
        },
        _initView:function() {
            //缩小后的组件
            var $shrink=$(T.shrink);
            $shrink.hide();
            $shrink.appendTo(window.document.body);
            //组装组件
            self._$frame=$('<div class="r_commodities_absolute '+C.comp+'" ">'
                +T.header+
                '<div class="content"> '+T.search+'<div class="clear"></div></div>'+
                '<div class="'+C.container+'"></div>'+
                '</div>');

            self._$frame.appendTo(window.document.body);

            //设置组件整体大小
            $("."+C.comp)
                .css("left",self._left)
                .css("top",self._top)
                .height(self._height)
                .width(self._width);
            //设置滑动组件内容
            $("."+C.container,self._$frame)
                .height(self._c_height)
                .width(self._width*3)
                .append(T.container1).append(T.container2).append(T.container3);
            //设置每个显示tab大小
            $("."+C.item,self._$frame)
                .height(self._c_height)
                .width(self._width);

            //bind event
            $("."+C.sBtn).live("click",self._sBtnClick);
            $("."+C.bBtn).live("click",self._bBtnClick);
            $("."+C.bLastBtn).live("click",self._bLastBtnClick);
            $("."+C.cLastBtn).live("click",self._cLastBtnClick);
            $("."+C.searchBtn).live("click",self._searchBtnClick);
            $("."+C.closeBtn).live("click",self._closeBtnClick);
            $("."+C.shrink).bind("click",self._shrinkBtnClick);

        },
        init:function () {
            self= APP_BCGOGO.Module.recommendShop;
            self._initView();
        },
        _hide:function(){
            $("."+C.comp).hide();
        },
        //可以适当的加效果
        _show:function(){
            self._$frame.show();
        },
        //event bind
        _closeBtnClick:function(){
            self._hide();
            $("."+C.shrink).show();
        },
        _shrinkBtnClick:function(){
            self._show();
            $("."+C.shrink).hide();
        },
        _sBtnClick:function(){    //第一个tab内元素点击
            $(".container2 ul",self._$frame).html("");
            var nodes=self._data_map[$(this).attr("sName")];
            if(!G.isEmpty(nodes)){
                var nodeStr="";
                for(var i=0;i<nodes.length;i++){
                    var node=nodes[i];
                    var imageUrl=node.value;
                    nodeStr+='<li style="cursor: pointer" class="li_shop_i '+C.bBtn+'" bId="'+node.idStr+'">' +
                        '<div class="img"><img  src="'+imageUrl+'"></div><div><b>'+node.text+'</b>('+node.childSize+')</div>' +
                        '</li>';
                }
                $(".container2 ul",self._$frame).append(nodeStr);
            }
            //绑定滑动事件
            $("."+C.container).stop(true, true).animate({
                "left":self._width * -1
            }, 200, "swing");
            //绑定滚动条样式的组件
            _$(".nano").nanoScroller({alwaysVisible: true});
        },
        _bLastBtnClick:function(){
            $("."+C.container).animate({
                "left":0
            }, 200, "swing");
        },
        _cLastBtnClick:function(){
            $("."+C.container).stop(true, true).animate({
                "left":self._width * -1
            }, 200, "swing");
        },

        _bBtnClick:function(){
            var bId=$(this).attr("bId");
            APP_BCGOGO.Net.asyncGet({
                url: options.b_content_url,
                data: {
                    parentId:bId,
                    "now": new Date()
                },
                dataType: "json",
                success: function (nodes) {
                    $(".container3 ul",self._$frame).html("");
                    if(!G.isEmpty(nodes)){
                        var nodeStr="";
                        for(var i=0;i<nodes.length;i++){
                            var shop=nodes[i];
                            var phone=G.isEmpty(shop.landline)?shop.mobile:shop.landline;
                            var qq=shop.qq;
                            nodeStr+='<li class="li_shop_d"><div class="m_home">' +
                                '<a class="r-shop-name" target="_blank" href="'+options.shop_detail_url+shop.idStr+'">'+shop.name+'</a>'+
                                '<div class="j_qq" qq="'+qq+'"></div></div>'+
                                '<div class="m_phone">'+phone+'</div>' +
                                '<div class="m_address">'+shop.address+'</div>' +
                                '</li>';

                        }
                        $(".container3 ul",self._$frame).append(nodeStr);
                        $(".j_qq",".container3 ul").each(function(){
                            $(this).multiQQInvoker({
                                QQ:$(this).attr("qq")
                            });
                        });
                    }
                    $("."+C.container).stop(true, true).animate({
                        "left":self._width * -2
                    }, 200, "swing");

                    //设置scroll样式
                    try{
                        _$(".nano").nanoScroller({alwaysVisible: true});
                    }catch(e){
                        console.info(e);
                    }
                }
            });

        },
       _searchBtnClick:function(){
            var standardVehicleBrand=$(this).closest(".search").find(".standardVehicleBrand").val();
            window.location.href=options.s_url+"&standardVehicleBrand="+standardVehicleBrand;
        },
        //public  interface
        show: function (p) {
            self._show();
            self._draw(p.data || {});


        }
    }
})();

$(function(){
    var recommendShop = APP_BCGOGO.Module.recommendShop;
    recommendShop.init();
});