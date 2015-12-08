Ext.define("Ext.model.TreeMenu", { // 定义树节点数据模型
    extend:"Ext.data.Model",
    fields:[
        {name:"id", type:"string"},
        {name:"text", type:"string"},
        {name:"iconCls", type:"string"},
        {name:"leaf", type:"boolean"},
        {name:'type', type:"string"},
        {name:'component', type:"string"}
    ]
});