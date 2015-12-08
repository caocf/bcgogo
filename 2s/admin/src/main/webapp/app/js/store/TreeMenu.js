Ext.define('Ext.store.TreeMenu', {
    extend:'Ext.data.TreeStore',
//    defaultRootId:id, // 默认的根节点id
    model:"Ext.model.TreeMenu",
    proxy:{
        type:"ajax", // 获取方式
        url:"view.do?method=getTreeMenuByParentId" // 获取树节点的地址
    },
    clearOnLoad:true,
    nodeParam:"id"  // 设置传递给后台的参数名,值是树节点的id属性
});
