Ext.define('Ext.model.customerMange.RecommendShop', {
    extend:'Ext.data.Model',
    fields:[
        { name: 'id', type: 'string'},
        { name: 'name', type: 'string'},
        { name: 'text', type: 'string' },
        { name: 'value', type: 'string' },
        { name: 'parentId', type: 'string'},
        { name: 'hasThisNode', type: 'boolean'},
        { name: 'leaf', type: 'boolean' },
        { name: 'sort', type: 'int' },
        { name: 'iconCls', type: 'string'},
        { name: 'type', type: 'string' },
        { name: 'imgUrl', type: 'string' }
    ]
});