Ext.define('Ext.store.dataMaintenance.ShopVersions', {
    extend:'Ext.data.Store',
//    autoSync: true,
    fields:[
        {name:'id', type:'string'},
        {name:'name', type:'string'},
        {name:'value', type:'string'}
    ],
    proxy:{
        type:'ajax',
        api:{
            read:'shopVersion.do?method=getAllShopVersion',
            update:'shopVersion.do?method=saveOrUpdateShopVersion'
        },
        reader:{
            type:'json',
            root:"results"
        }
    }
});