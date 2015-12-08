Ext.define('Ext.model.productMaintenance.ProductCategoryDetail', {
    extend:'Ext.data.Model',
    fields:[
        {name:'id', type:'string'},
        {name:'firstCategoryName', type:'string'},
        {name:'firstCategoryId', type:'string'},
        {name:'secondCategoryName', type:'string'},
        {name:'secondCategoryId', type:'string'},
        {name:'thirdCategoryName', type:'string'},
        {name:'thirdCategoryId', type:'string'},

        {name:'name', type:'string'},
        {name:'type', type:'string'}
    ],
    proxy:{
        type:'ajax',
        api:{
            read:'productCategory.do?method=getProductCategoryByNameOrId'
        },
        reader:{
            type:'json',
            root:"results",
            totalProperty:"totalRows"
        }
    },
    listeners:{
        exception:function (proxy, response, operation) {
            Ext.MessageBox.show({
                title:'错误异常',
                msg:operation.getError(),
                icon:Ext.MessageBox.ERROR,
                buttons:Ext.Msg.OK
            });
        }
    }

})