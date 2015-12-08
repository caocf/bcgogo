/**
 * @author zhangjuntao
 * 客户管理-线索管理 view 入口
 */
Ext.define('Ext.view.customerMange.customersClues.CustomerCluesList', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.customerMangeCustomerCluesList',
    autoScroll: true,
    columnLines: true,
    stripeRows: true, //每列是否是斑马线分开
//    enableKeyNav:true,          //允许键盘操作，即上下左右移动选中点
    forceFit: true, //自动填充，即让所有列填充满gird宽度
    multiSelect: true, //可以多选
    autoHeight: true,
    requires: [
        "Ext.view.customerMange.RegionSelect",
        "Ext.view.customerMange.CitySelect",
        "Ext.view.customerMange.ProvinceSelect"
    ],
    initComponent: function () {
        var me = this, container,
            store = Ext.create('Ext.store.customerMange.Shops');
        container = Ext.widget({
            xtype: "container",
            border: false,
            layout: {
                overflowHandler: 'Menu'
            },
            items: [
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            xtype: "textfield",
                            emptyText: "店铺名",
                            width: 100,
                            name: 'searchName'
                        },
                        {
                            xtype: "textfield",
                            emptyText: "联系人",
                            width: 100,
                            name: 'searchContact'
                        },
                        {
                            fieldLabel: '所在区域',
                            labelWidth: 55,
                            width: 135,
                            xtype: "provinceSelect",
                            name: 'province'
                        },
                        {
                            width: 80,
                            xtype: "citySelect",
                            name: 'city'
                        },
                        {
                            width: 80,
                            xtype: "regionSelect",
                            name: 'region'
                        },
                        { xtype: 'tbspacer', width: 10 },
                        {
                            xtype: 'checkboxgroup',
                            fieldLabel: '意向情况',
                            columns: 3,
                            id: 'shopStatuses',
                            labelWidth: 55,
                            width: 230,
                            items: [
                                { boxLabel: '无意向', name: 'shopStatuses', inputValue: 'NO_INTENTION' },
                                { boxLabel: '潜在', name: 'shopStatuses', inputValue: 'LATENT' },
                                { boxLabel: '意向', name: 'shopStatuses', inputValue: 'INTENTION' }
                            ]
                        },
                        {
                            xtype: "textfield",
                            emptyText: "跟进人",
                            width: 100,
                            name: 'searchFollowName'
                        },
                        '->',
                        {
                            text: "查询",
                            xtype: 'button',
                            action: 'search',
                            iconCls: "icon-search",
                            tooltip: "根据条件查询用户信息",
                            scope: this
                        },
                        {
                            text: "重置",
                            xtype: 'button',
                            action: 'reset',
                            iconCls: "icon-reset",
                            scope: this
                        }
                    ]
                },
                {
                    xtype: 'toolbar',
                    items: [
                        '->',
                        {
                            text: '新增线索',
                            xtype: 'button',
                            action: 'add',
                            scope: this,
                            iconCls: 'icon-add'
                        },
                        '-',
                        {
                            text: '编辑线索',
                            xtype: 'button',
                            iconCls: 'icon-edit',
                            action: 'edit',
                            scope: this,
                            disabled: true
                        },
                        '-',
                        {
                            text: '注册',
                            xtype: 'button',
                            iconCls: 'icon-user-set',
                            action: 'register',
                            scope: this,
                            disabled: true
                        },
                        '-',
                        {
                            text: '转出',
                            xtype: 'button',
                            iconCls: 'icon-add-p',
                            action: 'rollOut',
                            scope: this,
                            disabled: true
                        },
                        '-',
                        {
                            text: '删除线索',
                            xtype: 'button',
                            action: 'delete',
                            scope: this,
                            iconCls: 'icon-del',
                            disabled: true
                        }
                    ]
                }
            ]
        });
        Ext.apply(me, {
            tbar: container,
            dockedItems: {
                dock: 'bottom',
                xtype: 'pagingtoolbar',
                store: store,
                displayInfo: true
            },
            store: store,
            columns: [
                {
                    header: 'No.',
                    xtype: 'rownumberer',
                    sortable: false,
                    width: 25
                },
                {
                    header: '店铺名',
                    dataIndex: 'name'
                },
                {
                    header: '联系人',
                    dataIndex: 'owner'
                },
                {
                    header: '联系方式',
                    dataIndex: 'mobile'
                },
                {
                    header: '所属区域',
                    dataIndex: 'areaName',
                    renderer: function (value, metadata) {
                        metadata.tdAttr = 'data-qtip="' + value + '"';
                        return value;
                    }
                },
                {
                    header: '使用软件',
                    dataIndex: 'usedSoftware',
                    renderer: function (val, style, rec, index) {
                        return  rec.get("usedSoftware") ? rec.get("usedSoftware") : "无";
                    }
                },
                {
                    header: '意向情况',
                    dataIndex: 'shopStatusValue'
                },
                {
                    header: '跟进人',
                    dataIndex: 'followName'
                },
                {
                    header: '录入时间',
                    dataIndex: 'clueInputDate'
                }
                /* {
                 header:'最近拜访时间',
                 dataIndex:'operateTime',
                 renderer:function (val, style, rec, index) {
                 return val ? Ext.Date.format(new Date(Number(val)), 'Y-m-d') : "";
                 }
                 }*/
            ]
        });
        this.callParent(arguments);
    }

});