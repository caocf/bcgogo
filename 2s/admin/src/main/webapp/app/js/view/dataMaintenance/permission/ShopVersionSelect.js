Ext.define('Ext.view.dataMaintenance.permission.ShopVersionSelect', {
    extend:'Ext.form.ComboBox',
    alias:'widget.permissionShopVersionSelect',
    editable:false,
    store:Ext.create('Ext.store.dataMaintenance.ShopVersions',{
        proxy:{
            type:'ajax',
            api:{
                read:'shopVersion.do?method=getCommonShopVersion',
                update:'shopVersion.do?method=saveOrUpdateShopVersion'
            },
            reader:{
                type:'json',
                root:"results"
            }
        }
    }),
    displayField:'name',
    remoteFilter:true, //ajax过滤开关
    queryMode:'remote', //远程过滤
    queryParam:'name', //过滤字
    queryDelay:500, //延迟
    minChars:1, //The minimum number of characters the user must type before autocomplete defaults to 4 if queryMode = 'remote'
    enableKeyEvents:true
});
