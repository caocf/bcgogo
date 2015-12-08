/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 13-1-3
 * Time: 上午10:47
 * To change this template use File | Settings | File Templates.
 */

Ext.define('Ext.model.normalProductInventoryStat.NormalProductStatDetail', {
    extend:'Ext.data.Model',
    fields:[
        { name:'id',type:"string"},
        { name:'shopName',type:"string"},
        { name:'shopVersion',type:"string"},
        { name:'times',type:'string'},
        { name:'amount',type:"string" },
        { name:'total',type:"string"},
        { name:'topPrice',type:"string"},
        { name:'commodityCode',type:"string"},
        { name:'bottomPrice',type:"string"},
        { name:'areaInfo',type:"string"},
        { name:'unit',type:"string"},
        { name:'averagePrice',type:"string"},
        { name:'inventoryAmount',type:"string"},
        { name:'lastInventoryDate',type:"string"}
    ],
    proxy:{
        type:'ajax',
        api:{
            read:'normalProductStat.do?method=getNormalProductStatDetailByNormalProductId'
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
});
