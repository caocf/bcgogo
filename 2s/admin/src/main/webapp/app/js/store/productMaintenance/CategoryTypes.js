Ext.define('Ext.store.productMaintenance.CategoryTypes', {
    extend:'Ext.data.Store',
    fields:['label', 'value'],
    data:[
        {"value":"THIRD_CATEGORY", "label":"三级分类(品名)"},
        {"value":"FIRST_CATEGORY", "label":"一级分类(系统类别)"},
        {"value":"SECOND_CATEGORY", "label":"二级分类(种类)"}
    ]
});