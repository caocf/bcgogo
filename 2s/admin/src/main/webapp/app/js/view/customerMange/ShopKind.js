Ext.define('Ext.view.customerMange.ShopKind', {
    extend:'Ext.form.ComboBox',
    alias:'widget.shopKind',
    editable:false,
    store:Ext.create('Ext.store.customerMange.ShopKind'),
    queryMode:'local',
    displayField:'label',
    valueField:'value',
    listeners : {
        render : function(combo) {
            if(!combo.getValue()){
//                combo.setValue("正式店")
                combo.setValue("OFFICIAL")
            }
        }
    }
});