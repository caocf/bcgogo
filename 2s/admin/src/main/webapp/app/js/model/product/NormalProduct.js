Ext.define('Ext.model.product.NormalProduct', {
    extend:'Ext.data.Model',
    fields:[
        { name:'id',type:"string"},
        { name:'productName',type:"string"},
        { name:'commodityCode',type:"string"},
        { name:'productCategoryName',type:'string'},
        { name:'productFirstCategoryName',type:'string'},
        { name:'productFirstCategoryId',type:'string'},
        { name:'productSecondCategoryId',type:'string'},
        { name:'productSecondCategoryName',type:'string'},
        { name:'productCategoryId',type:"string" },
        { name:'unit',type:"string"},
        { name:'bindingShopProductCount',type:"string"},
        { name:'brand',type:"string"},
        { name:'model',type:"string"},
        { name:'spec',type:"string"},
        { name:'vehicleBrandModelInfo',type:"string"},
        { name:'selectBrandModel',type:"string"},
        { name:'vehicleModel',type:"string"},
        { name:'vehicleBrand',type:"string"},
        { name:'vehicleBrandId',type:"string"},
        { name:'vehicleModelIds',type:"string"},
        { name:'vehicleModelId',type:"string"}
    ],
    proxy:{
        type:'ajax',
        api:{
            read:'productManage.do?method=getNormalProducts'
        },
        reader:{
            root:'result',
            type:'json',
            totalProperty:"totalRows"
        }
    }

});