Ext.define('Ext.model.product.ShopProduct', {
    extend:'Ext.data.Model',
    fields:[
        { name:'id',type:"string"},
        { name:'name',type:"string"},
        { name:'commodityCode',type:"string"},
        { name:'unit',type:"string"},
        { name:'brand',type:"string"},
        { name:'model',type:"string"},
        { name:'spec',type:"string"},
        { name:'vehicleModel',type:"string"},
        { name:'vehicleBrand',type:"string"},
        { name:'shopName' ,type:"string"},
        { name:'relevanceStatus' ,type:"string"},
        { name:'productVehicleBrand' ,type:"string"},
        { name:'productVehicleModel' ,type:"string"},
        { name:'sellUnit' ,type:"string"},
        { name:'storageUnit' ,type:"string"},
        { name:'normalProductId',type:"string"},
        { name:'normalProductName',type:"string"},
        { name:'normalCommodityCode',type:"string"},
        { name:'normalBrand',type:"string"},
        { name:'normalModel',type:"string"},
        { name:'normalSpec',type:"string"},
        { name:'normalVehicleBrand',type:"string"},
        { name:'normalVehicleModel',type:"string"},
        { name:'inventoryAveragePrice',type:"string"},
        { name:'normalVehicleBrandModelInfo',type:"string"},
        { name:'productModifyFieldsList',type:"string"},
        { name:'hideExpander',type:"boolean",defaultValue: false, convert: null},
        { name:'normalUnit',type:"string"}
    ],
    belongsTo:"Ext.model.product.NormalProduct",
    proxy:{
        type:'ajax',
        api:{
            read:'productManage.do?method=getShopProducts'
        },
        reader:{
            root:'result',
            type:'json',
            totalProperty:"totalRows"
        }
    }

});