<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>
<html>
<head>
    <meta http-equiv='Content-Type' content='text/html; charset=iso-8859-1'>
    <title>Ux.grid.plugin.HasManyExpander</title>
<head>
    <title>统购后台CRM管理系统</title>
    <%
        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
    %>
    <%--Ext base css--%>
    <link rel="stylesheet" type="text/css" href="<%=basePath%>app/ext4.1/resources/css/ext-all-debug.css"/>
    <%--Ext ux css--%>
    <link rel="stylesheet" type="text/css" href="<%=basePath%>app/js/ux/css/CheckHeader.css"/>

    <%--<script type="text/javascript" src="<%=basePath%>app/ext4.1/ext-all.js"></script>--%>
    <script type="text/javascript" src="<%=basePath%>app/ext4.1/ext-all-debug.js"></script>
    <script type="text/javascript" src="<%=basePath%>app/ext4.1/locale/ext-lang-zh_CN.js"></script>

    <script type="text/javascript" src="<%=basePath%>app/js/utils/ValidateUtils.js"></script>
</head>
    
    <script type='text/javascript'>
    Ext.Loader.setConfig({
        enabled: true,
        paths   : {
            'Ext.ux' : 'app/js/ux'
        }
    });
        Ext.require([
            'Ext.grid.Panel',
            'Ext.data.Store',
            'Ext.data.writer.Writer', //bug in Ext JS 4.1.0 RC3, proxy should require this, bug already opened
            'Ext.ux.grid.plugin.AssociationRowExpander'
        ]);

        Ext.define('A.Company', {
            extend     : 'Ext.data.Model',
            idProperty : 'companyId',
            fields     : [
                'receiptNo'
            ],

            proxy    : {
                type   : 'ajax',
                url    : 'app/dummyData/test.json',
                reader : {
                    type : 'json',
                    root : 'data'
                }
            },

            hasMany    : {
                model : 'A.History',
                name  : 'bcgogoReceivableOrderItemDTOList'
            }
        });

        Ext.define('A.History', {
            extend : 'Ext.data.Model',
            fields : [
                'productName'
            ]
            ,

            proxy : {
                type   : 'ajax',
                url    : 'app/dummyData/belongsTo.json',
                reader : {
                    type : 'json',
                    root : 'data'
                }
            },

            belongsTo : {
                model      : 'Company',
                name       : 'companies',
                foreignKey : 'companyId'
            }
            ,

            hasOne : {
                model      : 'Company',
                name       : 'companies',
                foreignKey : 'companyId',
                getterName : 'getCompanyOne'
            }
        });

        Ext.onReady(function() {
            var grid1 = new Ext.grid.Panel({
                store    : new Ext.data.Store({
                    model    : 'A.Company',
                    autoLoad : true,
                    proxy    : {
                        type   : 'ajax',
                        url    : 'app/dummyData/test.json',
                        reader : {
                            type : 'json',
                            root : 'data'
                        }
                    }
                }),
                renderTo : 'hasManyGrid',
                width    : 600,
                height   : 300,
                columns  : [
                    { text : 'Company',      dataIndex : 'receiptNo',        flex : 1                                     }
                ],
                plugins : [
                    {
                        ptype      : 'associationrowexpander',
                        getterName : 'bcgogoReceivableOrderItemDTOList',
                        gridConfig : {
                            height  : 100,
                            title   : 'History',
                            columns : [
                                {
                                    header    : 'Text',
                                    dataIndex : 'productName',
                                    flex      : 1
                                }
                            ]
                        }
                    }
                ]
            });
            console.log(grid1.store);
            var grid2 = new Ext.grid.Panel({
                store    : new Ext.data.Store({
                    model    : 'Company',
                    autoLoad : true,
                    proxy    : {
                        type   : 'ajax',
                        url    : 'app/dummyData/hasMany.json',
                        reader : {
                            type : 'json',
                            root : 'data'
                        }
                    }
                }),
                renderTo : 'hasManyView',
                width    : 600,
                height   : 300,
                columns  : [
                    { text : 'Company',      dataIndex : 'company',        flex : 1                                     },
                    { text : 'Price',        dataIndex : 'price',      renderer : Ext.util.Format.usMoney               },
                    { text : 'Change',       dataIndex : 'change'                                                       },
                    { text : '% Change',     dataIndex : 'pctChange'                                                    },
                    { text : 'Last Updated', dataIndex : 'lastChange', renderer : Ext.util.Format.dateRenderer('m/d/Y') }
                ],
                plugins : [
                    {
                        ptype      : 'associationrowexpander',
                        getterName : 'history',
                        viewConfig : {
                            itemSelector : 'div.history-text',
                            emptyText    : 'There is no history',
                            tpl          : new Ext.XTemplate(
                                '<div><b>History</b></div>',
                                '<tpl for=".">',
                                    '<div class="history-text">{text} ({date:date("n/j g:ia")})</div>',
                                '</tpl>'
                            )
                        }
                    }
                ]
            });

            var grid3 = new Ext.grid.Panel({
                store    : new Ext.data.Store({
                    model    : 'History',
                    autoLoad : true
                }),
                renderTo : 'belongsTo',
                width    : 600,
                height   : 300,
                columns  : [
                    { text : 'Text', dataIndex : 'text',     flex : 1                                     },
                    { text : 'Date', dataIndex : 'date', renderer : Ext.util.Format.dateRenderer('m/d/Y') }
                ],
                plugins : [
                    {
                        ptype      : 'associationrowexpander',
                        type       : 'belongsTo',
                        getterName : 'getCompany',
                        rowBodyTpl : new Ext.XTemplate(
                                         '<div><b>Company Details:</b></div>',
                                         '<div>{company} - {[this.colorVal(values.price, true)]}</div>',
                                         '<div>{lastChange:date("n/j g:ia")}</div>',
                                         '<div>{[this.colorVal(values.change, true)]} {[this.colorVal(values.pctChange, false)]}</div>',
                                         {
                                             colorVal : function(value, money) {
                                                 var color = value === 0 ? '000' : (value > 0 ? '093' : 'F00');

                                                 if (money) {
                                                     value = Ext.util.Format.usMoney(value);
                                                 } else {
                                                     value += '%';
                                                 }

                                                 return '<span style="color: #' + color + ';">' + value + '</span>';
                                             }
                                         }
                                     )
                    }
                ]
            });

            var grid4 = new Ext.grid.Panel({
                store    : new Ext.data.Store({
                    model    : 'History',
                    autoLoad : true
                }),
                renderTo : 'hasOne',
                width    : 600,
                height   : 300,
                columns  : [
                    { text : 'Text', dataIndex : 'text',     flex : 1                                     },
                    { text : 'Date', dataIndex : 'date', renderer : Ext.util.Format.dateRenderer('m/d/Y') }
                ],
                plugins : [
                    {
                        ptype      : 'associationrowexpander',
                        type       : 'hasOne',
                        getterName : 'getCompanyOne',
                        rowBodyTpl : new Ext.XTemplate(
                                         '<div><b>Company Details:</b></div>',
                                         '<div>{company} - {[this.colorVal(values.price, true)]}</div>',
                                         '<div>{lastChange:date("n/j g:ia")}</div>',
                                         '<div>{[this.colorVal(values.change, true)]} {[this.colorVal(values.pctChange, false)]}</div>',
                                         {
                                             colorVal : function(value, money) {
                                                 var color = value === 0 ? '000' : (value > 0 ? '093' : 'F00');

                                                 if (money) {
                                                     value = Ext.util.Format.usMoney(value);
                                                 } else {
                                                     value += '%';
                                                 }

                                                 return '<span style="color: #' + color + ';">' + value + '</span>';
                                             }
                                         }
                                     )
                    }
                ]
            });
        });
    </script>
    <style type="text/css">
        body {
            padding : 50px;
        }
        .x-grid-rowbody-loading {
            background   : url(images/loading.gif) no-repeat;
            height       : 16px;
            padding-left : 20px;
        }
        #hasManyGrid {
            position : absolute;
            top      : 50px;
            left     : 50px;
        }
        #hasManyView {
            position : absolute;
            top      : 50px;
            left     : 700px;
        }
        #belongsTo {
            position : absolute;
            top      : 400px;
            left     : 50px;
        }
        #hasOne {
            position : absolute;
            top      : 400px;
            left     : 700px;
        }
    </style>
</head>
<body>
<div id="hasManyGrid"><b>hasMany</b> association with an inner Ext.grid.Panel</div>
<div id="hasManyView"><b>hasMany</b> association with an inner Ext.view.View</div>
<div id="belongsTo"><b>belongsTo</b> association using an Ext.XTemplate</div>
<div id="hasOne"><b>hasOne</b> association using an Ext.XTemplate</div>
</body>
</html>