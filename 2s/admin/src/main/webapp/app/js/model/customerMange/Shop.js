/**
 * shop model
 */
Ext.define('Ext.model.customerMange.Shop', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'agent', type: 'string'},
//        {name: 'shopRelationInviteOriginShopId', type: 'string'},//邀请他的店铺Id
//        {name: 'shopRelationInviteOriginShopName', type: 'string'},//邀请他的店铺名
        {name: 'name', type: 'string'},    //店铺名
        {name: 'shortname', type: 'string'},    //店铺简称
        {name: 'legalRep', type: 'string'},   //法人
        {name: 'contact', type: 'string'},   //联系人
        {name: 'owner', type: 'string'},   //负责人/店主
        {name: 'mobile', type: 'string'},  //联系方式
        {name: 'storeManager', type: 'string'},   //管理员
        {name: 'storeManagerMobile', type: 'string'},   //管理员手机
        {name: 'qq', type: 'string'},   //qq
        {name: 'email', type: 'string'},   //email
        {name: 'landline', type: 'string'},   //landline
        {name: 'memo', type: 'string'},   //memo
        {name: 'url', type: 'string'},   //url

        {name: 'businessHours', type: 'string'},
        {name: 'established', type: 'string'}, //创立时间
        {name: 'licencePlate', type: 'string'}, //车牌默认前缀
        {name: 'area', type: 'string'}, //面积
        {name: 'address', type: 'string'},    //店铺地址
        {name: 'legalRep', type: 'string'},   //法人代表
        {name: 'softPrice', type: 'string'},   //价格
        {name: 'bargainPrice', type: 'string'},   //议价

        {name: 'usedSoftware', type: 'string'},       //使用过的软件
        {name: 'hasComputer', type: 'string'},       //是否有电脑
        {name: 'shopStatus', type: 'string'},        //状态
        {name: 'shopStatusValue', type: 'string'},        //状态
        {name: 'paymentStatus', type: 'string'},        //缴费状态
        {name: 'bargainStatus', type: 'string'},        //议价状态
        {name: 'shopState', type: 'string'},
        {name: 'networkType', type: 'string'},       //网络类型

        {name: 'areaId', type: 'string'},   //地区id
        {name: 'province', type: 'string'},   //地区 -省
        {name: 'city', type: 'string'},        //地区 -市
        {name: 'region', type: 'string'},     //地区  -县
        {name: 'areaName', type: 'string'},   //地区

        {name: 'trialStartTime', type: 'string'},   //试用起始时间
        {name: 'trialEndTime', type: 'string'},   //试用截止时间
        {name: 'usingEndTime', type: 'string'},   //使用结束时间
        {name: 'clueInputDate', type: 'string'},    //线索录入日期

        {name: 'relatedBusiness', type: 'string'}, //使用过的软件

        {name: 'feature', type: 'feature'}, //特色
        {name: 'features', type: 'array'}, //特色
        {name: 'otherFeature', type: 'string'}, //特色
        {name: 'otherVehicleBrand', type: 'string'}, //特色 -车型

        {name: 'businessScope', type: 'string'}, //经营范围
        {name: 'businessScopes', type: 'array'}, //经营范围
        {name: 'otherBusinessScope', type: 'string'}, //经营范围
        {name: 'majorProduct', type: 'string'},  //经营范围

        {name: 'relatedBusiness', type: 'string'}, //相关业务
        {name: 'relatedBusinesses', type: 'array'}, //相关业务
        {name: 'otherRelatedBusiness', type: 'string'}, //相关业务

        {name: 'operationMode', type: 'string'}, //经营方式
        {name: 'operationModes', type: 'array'}, //经营方式
        {name: 'otherOperationMode', type: 'string'}, //经营方式

        {name: 'shopKind', type: 'string'},
        {name: 'registerType', type: 'string'},

        {name: 'shopVersionId', type: 'string'},
        {name: 'shopVersionName', type: 'string'},

        {name: 'submitApplicationDate', type: 'string'},  //注册时间
        {name: 'registrationDate', type: 'string'},  //提交审核时间
        {name: 'managerUserNo', type: 'string'},      //管理员账号
        {name: 'managerId', type: 'string'},           //管理员id
        {name: 'followId', type: 'string'},           //跟进人
        {name: 'followName', type: 'string'},           //跟进人
        {name: 'agent', type: 'string'},           //销售人
        {name: 'agentMobile', type: 'string'},           //销售人
        {name: 'coordinateLat', type: 'double'},      //店铺纬度信息
        {name: 'coordinateLon', type: 'double'},         //店铺经度信息
        {name: 'locateStatus', type: 'string'},         //定位状态
        {name: 'agentId', type: 'string'},           //销售人
        {name: 'chargeType', type: 'string'},
        {name: 'adPricePerMonth', type: 'double'},//每个月广告费
        {name: 'adStartDateStr', type: 'string'},//广告开始投放时间
        {name: 'adEndDateStr', type: 'string'},//广告投放结束时间
        {name: 'productAdType', type: 'string'}//广告类型
    ],
    listeners: {
        exception: function (proxy, response, operation) {
            Ext.MessageBox.show({
                title: '错误异常',
                msg: operation.getError(),
                icon: Ext.MessageBox.ERROR,
                buttons: Ext.Msg.OK
            });
        }
    }

});