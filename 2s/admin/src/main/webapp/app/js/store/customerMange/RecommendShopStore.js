Ext.define('Ext.store.customerMange.RecommendShopStore', {
    extend:'Ext.data.TreeStore',
    model:"Ext.model.customerMange.RecommendShop",
    folderSort:true,
    sorters: [{
        property: 'sort',
        direction: 'ASC'
    }],
    fields: [
        { name: 'id', type: 'string'},
        { name: 'name', type: 'string'},
        { name: 'text', type: 'string' },
        { name: 'value', type: 'string' },
        { name: 'parentId', type: 'string'},
        { name: 'hasThisNode', type: 'boolean'},
        { name: 'leaf', type: 'boolean' },
        { name: 'sort', type: 'int' },
        { name: 'iconCls', type: 'string'},
        { name: 'type', type: 'string' }
    ],
    root:{
        value: '统购平台',
        name: 'BCGOGO',
        leaf: false,
        iconCls: 'icon-user-set',
        id: -1,
        expanded: true
    },
    proxy:{
        type:'ajax',
        api:{
            read:'shopAd.do?method=getShopRecommend'
        },
        reader:{
            type:'json'
        }
    }

});