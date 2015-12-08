package com.bcgogo.enums;

/**
 * Created by LiTao on 2015/11/5.
 */
public enum IncomeType {
    INCOME("收入"),
    EXPENSES("支出");

    private String name;

    IncomeType(String name) {
        this.name=name;
    }
    public String getName() {
        return name;
    }

}
