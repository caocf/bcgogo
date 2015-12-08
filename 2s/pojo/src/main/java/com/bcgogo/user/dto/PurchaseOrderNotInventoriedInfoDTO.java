package com.bcgogo.user.dto;

import com.bcgogo.search.dto.ItemIndexDTO;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-2-18
 * Time: 下午2:16
 * To change this template use File | Settings | File Templates.
 */
public class PurchaseOrderNotInventoriedInfoDTO implements Serializable {

    private int purchaseOrderNotInventoriedSize;
    private float productAmount;
    private double  purchaseOrderNotInventoriedTotalMoney;
    private List<ItemIndexDTO> purchaseOrderNotInventoriedList;

    public int getPurchaseOrderNotInventoriedSize() {
        return purchaseOrderNotInventoriedSize;
    }

    public float getProductAmount() {
        return productAmount;
    }

    public double getPurchaseOrderNotInventoriedTotalMoney() {
        return purchaseOrderNotInventoriedTotalMoney;
    }

    public void setPurchaseOrderNotInventoriedSize(int purchaseOrderNotInventoriedSize) {
        this.purchaseOrderNotInventoriedSize = purchaseOrderNotInventoriedSize;
    }

    public void setProductAmount(float productAmount) {
        this.productAmount = productAmount;
    }

    public void setPurchaseOrderNotInventoriedTotalMoney(double purchaseOrderNotInventoriedTotalMoney) {
        this.purchaseOrderNotInventoriedTotalMoney = purchaseOrderNotInventoriedTotalMoney;
    }

    public List<ItemIndexDTO> getPurchaseOrderNotInventoriedList() {
        return purchaseOrderNotInventoriedList;
    }

    public void setPurchaseOrderNotInventoriedList(List<ItemIndexDTO> purchaseOrderNotInventoriedList) {
        this.purchaseOrderNotInventoriedList = purchaseOrderNotInventoriedList;
    }
}
