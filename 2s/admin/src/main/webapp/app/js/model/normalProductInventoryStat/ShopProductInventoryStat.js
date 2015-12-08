/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-12-29
 * Time: 上午11:43
 * To change this template use File | Settings | File Templates.
 */
Ext.define('Ext.model.normalProductInventoryStat.ShopProductInventoryStat', {
    extend:'Ext.data.Model',
    fields:[
        { name:'id',type:"string"},
        { name:'commodityCode',type:"string"},
        { name:'nameAndBrand',type:"string"},
        { name:'specAndModel',type:'string'},
        { name:'productVehicleBrand',type:"string" },
        { name:'unit',type:"string"},
        { name:'normalProductId',type:"string"},
        { name:'amount',type:"string"},
        { name:'total',type:"string"},
        { name:'normalProductStatType',type:"string"},
        { name:'topPrice',type:"string"},
        { name:'bottomPrice',type:"string"},
        { name:'priceStr',type:"string"},
        { name:'times',type:"string"},
        { name:'averagePrice',type:"string"},
        { name:'inventoryAmount',type:"string"},
        { name:'lastInventoryDate',type:"string"},
        { name:'shopName',type:"string"},
        { name:'shopVersion',type:"string"},
        { name: 'bottomPriceStr', type: "string"},
        { name: 'topPriceStr', type: "string"}


    ],
    proxy:{
        type:'ajax',
        api:{
            read:'shopProductInventory.do?method=getShopProductInventoryStatByCondition'
        },
        reader:{
            type:'json',
            root:"result",
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
});

