Ext.define('Ext.model.productMaintenance.ProductCategory', {
    extend:'Ext.data.Model',
    fields:[
        { name:'id', type:'string'},
        { name:'text' },
        { name:'name', type:'string'},
        { name:'parentId', type:'string'},
        { name:'leaf', type:'boolean' },
        { name:'sort', type:'int' },
        { name:'iconCls', type:'string'},
        { name:'type', type:'string' },

        { name:'firstCategoryId', type:'string'},
        { name:'secondCategoryId', type:'string'},
        { name:'thirdCategoryId', type:'string'},
        { name:'firstCategoryName', type:'string'},
        { name:'secondCategoryName', type:'string'},
        { name:'thirdCategoryName', type:'string'}

    ],
    proxy:{
        type:'ajax',
        api:{
            create:'productCategory.do?method=updateProductCategory',
            read:'productCategory.do?method=getProductCategory'
        },
        reader:{
            type:'json'
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
});
