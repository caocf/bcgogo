Bcgogo = {
    MessageEventListenerFlag: false,
    AddressCoordinateWindow: {
        coordinateLon: null,
        coordinateLat: null
    }
};
Ext.define('Ext.view.customerMange.AddressCoordinateWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.addresscoordinatewindow',
    layout: 'fit',
    iconCls: 'icon-locate',
    collapsible: true,
    requires: [
        "Ext.view.customerMange.RegionSelect",
        "Ext.view.customerMange.CitySelect",
        "Ext.view.customerMange.ProvinceSelect"
    ],

    initComponent: function () {
        var me = this;
        me.commonUtils = Ext.create("Ext.utils.Common");
        Ext.apply(me, {
            items: {
                xtype: 'form',
                width: 700,
                height: 500,
                frame: false,
                border: false,
                buttons: [
                    {
                        text: '保存',
                        action: 'save',
                        scope: me
//                        handler: me.updateAddressCoordinate
                    },
                    {
                        text: '取消',
                        tooltip: "取消",
                        handler: function () {
                            me.close();
                        }
                    }
                ],
                layout: 'anchor',
                items: [
                    {
                        xtype: 'fieldset',
                        layout: 'anchor',
                        border: false,
                        items: [
                            {
                                xtype: 'fieldset',
                                height: 60, //图片高度
                                layout: 'anchor',
                                name: "area",
                                padding: 0,
                                border: false,
                                items: [
                                    {
                                        xtype: 'fieldset',
                                        layout: 'hbox',
                                        anchor: '100%',
                                        padding: 0,
                                        defaults: {
                                            anchor: "100%",
                                            margin: "0 5 5 0"
                                        },
                                        border: false,
                                        items: [
                                            {
                                                fieldLabel: '所在区域',
                                                labelWidth: 70,
                                                width: 180,
                                                name: 'province',
                                                allowBlank: false,
                                                xtype: "provinceSelect",
                                                listeners: {
                                                    scope: me,
                                                    select: me.provinceSelectAction,
                                                    beforequery: me.provinceBeforeQuery,
                                                    beforerender: me.provinceBeforeRender
                                                }
                                            },
                                            {
                                                width: 100,
                                                name: 'city',
                                                allowBlank: false,
                                                xtype: "citySelect",
                                                listeners: {
                                                    scope: me,
                                                    select: me.citySelectAction,
                                                    beforequery: me.cityBeforeQuery,
                                                    beforerender: me.cityBeforeRender
                                                }
                                            },
                                            {
                                                width: 100,
                                                xtype: "regionSelect",
                                                name: 'region',
                                                listeners: {
                                                    scope: me,
                                                    select: me.regionSelectAction,
                                                    beforequery: me.regionBeforeQuery,
                                                    beforerender: me.regionBeforeRender
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        xtype: 'fieldset',
                                        layout: 'hbox',
                                        anchor: '100%',
                                        padding: 0,
                                        border: false,
                                        defaults: {
                                            anchor: "100%"
                                        },
                                        items: [
                                            {
                                                xtype: 'textfield',
                                                name: 'address',
                                                fieldLabel: '详细地址',
                                                labelWidth: 70,
                                                width: 410,
                                                enforceMaxLength: true,
                                                maxLength: 100
                                            },
                                            {
                                                text: '查找',
                                                xtype: "button",
                                                width: 60,
                                                margin: "0 0 0 10",
                                                listeners: {
                                                    scope: me,
                                                    click: me.getAddressCoordinate
                                                }
                                            }
                                        ]
                                    }

                                ]
                            },
                            {
                                xtype: 'component',
                                name: "addressCoordinate",
                                width: 700, //图片宽度
                                height: 350, //图片高度
                                html: '<iframe width="100%" height="100%" frameborder="0" src="" scrolling="no" frameborder="0"></iframe>'
                            },
                            {
                                hidden: true,
                                name: 'coordinateLat',
                                xtype: "hiddenfield"
                            },
                            {
                                hidden: true,
                                name: 'coordinateLon',
                                xtype: "hiddenfield"
                            },
                            {
                                hidden: true,
                                name: 'id',
                                xtype: "hiddenfield"
                            }
                        ]
                    }
                ]
            }
        });
        me.callParent();
    },

    updateAddressCoordinate: function (callback) {
        var me = this, form = me.down('form').getForm(), lat, lng;
        me.mask('正在保存 . . .');
        lng = form.findField('coordinateLon').getValue() || Bcgogo.AddressCoordinateWindow.coordinateLon;
        lat = form.findField('coordinateLat').getValue() || Bcgogo.AddressCoordinateWindow.coordinateLat;
        if (!lng || !lat) {
            Ext.Msg.alert('返回结果', "请在地图中确认！", function () {
                me.unmask();
            });
            return;
        }
        var params = form.getValues();
        params['coordinateLon'] = lng;
        params['coordinateLat'] = lat;
        me.commonUtils.ajax({
            url: 'shopManage.do?method=updateShopAddressCoordinate',
            params: params,
            success: function (result) {
                if (result) {
                    Ext.Msg.alert('返回结果', result['msg'], function () {
                        if (callback instanceof Function) {
                            callback();
                        }
                        me.close();
                        me.clearAddressCoordinateWindowParams();
                    });
                }
            }
        });
    },
    clearAddressCoordinateWindowParams: function () {
        Bcgogo.AddressCoordinateWindow.coordinateLon = null;
        Bcgogo.AddressCoordinateWindow.coordinateLat = null;
    },

    showAddressCoordinateWindow: function (rec) {
        this.setTitle(rec.get("name") + " 地图坐标确认");
        this.down("form").loadRecord(rec);
        this.down("[name=address]").setValue(rec.get("address").split(rec.get("areaName"))[1]);
        this.show();
        var form = this.down("form"),
            me = this;
        var shop = (rec['data']);
        if (!shop)return;
        if (shop['coordinateLon']) {
            me.onBaiduMapCenterAndZoom(shop['coordinateLon'], shop['coordinateLat']);
        } else {
            me.onBaiduMapSearch();
        }
    },

    getAddressCoordinate: function () {
        var me = this, form = this.down('form').getForm();
        me.onBaiduMapSearch();
    },

    setCoordinateResult: function (lng, lat, form) {
        var me = this, form = form || me.down('form').getForm();
        form.findField('coordinateLon').setValue(lng);
        form.findField('coordinateLat').setValue(lat);
        Bcgogo.AddressCoordinateWindow.coordinateLon = lng;
        Bcgogo.AddressCoordinateWindow.coordinateLat = lat;
    },

    addCoordinateResultEventListener: function () {
        var me = this, form = me.down('form').getForm();
        if (!Bcgogo.MessageEventListenerFlag) {
            window.addEventListener("message", function (event) {
                if ("coordinate" === event.data['type']) {
                    me.setCoordinateResult(event.data['lng'], event.data['lat'], form);
                }/* else if ("noresult" === event.data['type'] && (!event.data.lat || !event.data.lng)) {
                    Ext.Msg.alert('返回结果', "地图中未找到,请手动在地图中标示！");
                }*/
            }, false);
            Bcgogo.MessageEventListenerFlag = true;
        }
    },

    onBaiduMapCenterAndZoom: function (lng, lat) {
        var me = this;
        me.setCoordinateResult(null, null);
        if (!lng || !lat) return;
        var iframe = me.down("form").down('[name=addressCoordinate]').el.dom.childNodes[0];
        iframe.src = "api/proxy/baidu/map/shop/" + lat + "/" + lng + "/NULL/NULL/NULL?origin="+encodeURIComponent(window.location.origin);
        me.setAddress();
        me.addCoordinateResultEventListener();
    },

    onBaiduMapSearch: function () {
        var me = this;
        me.setCoordinateResult(null, null);
        var iframe = me.down("form").down('[name=addressCoordinate]').el.dom.childNodes[0];
        me.setAddress();
        var area = me.city;
        if (!area) {
            Ext.Msg.alert('返回结果', "请选择城市！");
            return;
        }
        if (area == "市辖区" || area == "县") {
            area = me.province;
        }
        iframe.src = "api/proxy/baidu/map/shop/NULL/NULL/" + encodeURI(area) + "/" + (me.region ? encodeURI(me.region) : "NULL") + "/" + (me.addressDetail ? encodeURI(me.addressDetail) : "NULL")+"?origin="+encodeURIComponent(window.location.origin);
        me.addCoordinateResultEventListener();
    },
    setAddress: function () {
        var me = this;
        me.province = Ext.String.trim(me.down("provinceSelect").getRawValue());
        me.city = Ext.String.trim(me.down("citySelect").getRawValue());
        me.region = Ext.String.trim(me.down("regionSelect").getRawValue());
        me.addressDetail = Ext.String.trim(me.down("[name=address]").getValue());
    },
    provinceSelectAction: function (combo, records) {
        var win = this, form = win.down('form').getForm(),
            regionSelect = win.down("regionSelect"),
            citySelect = win.down("citySelect");
        citySelect.setRawValue("");
        citySelect.setValue(null);
        regionSelect.setRawValue(null);
        regionSelect.setValue(null);
        citySelect.setProvince(records[0]);
//        var address = records[0].get("name");
//        form.findField('address').setValue(address);
        win.setAddress();
    },

    provinceBeforeQuery: function (queryEvent) {
        queryEvent.combo.store.proxy.extraParams = {
            parentNo: "1"
        };
    },

    provinceBeforeRender: function (combo) {
        var win = this;
        combo.store.proxy.extraParams = {
            parentNo: "1"
        };
        combo.store.load();
    },

    citySelectAction: function (combo, records) {
        var win = this, form = win.down('form').getForm(),
            regionSelect = win.down("regionSelect");
        regionSelect.setRawValue(null);
        regionSelect.setValue(null);
        regionSelect.setCity(records[0]);
//        var address = win.down("provinceSelect").getRawValue() + records[0].get("name");
//        form.findField('address').setValue(address);
        win.setAddress();
    },

    cityBeforeRender: function (combo) {
        var win = this, form = win.down("form");
        if (form.getRecord() && form.getRecord().get("province")) {
            combo.store.load({params: {parentNo: form.getRecord().get("province")}});
        }
    },

    cityBeforeQuery: function (queryEvent) {
        var win = this, form = win.down('form').getForm(),
            parentNo, rec = form.getRecord();
        if (!queryEvent.combo.getProvince()) {
            parentNo = rec.get("province");
            if (!parentNo) {
                return false;
            }
        } else {
            parentNo = queryEvent.combo.getProvince().get("no");
        }
        queryEvent.combo.store.proxy.extraParams = {
            parentNo: parentNo
        };
        queryEvent.combo.store.load();
    },

    regionSelectAction: function (combo, records) {
        var form = this.down('form').getForm();
//        var address = this.down("provinceSelect").getRawValue() + this.down("citySelect").getRawValue() + records[0].get("name")
//        form.findField('address').setValue(address);
        this.setAddress();
    },

    regionBeforeQuery: function (queryEvent) {
        var form = this.down("form"), parentNo,
            rec = form.getRecord();
        if (queryEvent.combo.getCity()) {
            parentNo = queryEvent.combo.getCity().get("no");
        }
        if (!parentNo) {
            parentNo = rec.get("city");
            if (!parentNo)  return false;
        }
        queryEvent.combo.store.proxy.extraParams = {
            parentNo: parentNo
        };
        queryEvent.combo.store.load();
    },

    regionBeforeRender: function (combo) {
        var form = this.down("form");
        if (form.getRecord().get("city")) {
            combo.store.load({params: {parentNo: form.getRecord().get("city")}});
        }
    }
});