/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 14-1-2
 * Time: 下午2:37
 * To change this template use File | Settings | File Templates.
 */
Ext.define('Ext.model.sales.SmsPreferentialPolicy', {
    extend:'Ext.data.Model',
    fields:[
        { name:'id',type:"string"},
        { name:'rechargeAmount', type:"string"},
        { name:'presentAmount', type:"string"}
    ]
});
