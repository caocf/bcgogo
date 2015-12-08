package com.bcgogo.wx.menu;

/**
 * 多级菜单（父按钮)
 * User: ndong
 * Date: 14-8-19
 * Time: 上午2:47
 * To change this template use File | Settings | File Templates.
 */
public class ComplexButton extends Button {

    private Button[] sub_button;

    public Button[] getSub_button() {
        return sub_button;
    }

    public void setSub_button(Button[] sub_button) {
        this.sub_button = sub_button;
    }
}
