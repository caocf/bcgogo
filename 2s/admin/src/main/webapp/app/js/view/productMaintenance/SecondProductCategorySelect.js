Ext.define('Ext.view.productMaintenance.SecondProductCategorySelect', {
    extend:'Ext.form.ComboBox',
    alias:'widget.secondProductCategorySelect',
    emptyText:'',
    store:Ext.create('Ext.store.productMaintenance.SecondProductCategories'),
    displayField:'name',
    valueField:'name',
    remoteFilter:false, //ajax过滤开关
    queryMode:'remote', //远程过滤
    queryParam:'name', //过滤字
    queryDelay:500, //延迟
    autoLoad:false,
    minChars:1, //The minimum number of characters the user must type before autocomplete defaults to 4 if queryMode = 'remote'
    enableKeyEvents:true


});