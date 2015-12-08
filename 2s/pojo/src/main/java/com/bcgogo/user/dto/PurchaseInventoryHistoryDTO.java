package com.bcgogo.user.dto;

import com.bcgogo.search.dto.ItemIndexDTO;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-2-17
 * Time: 下午5:39
 * To change this template use File | Settings | File Templates.
 */
public class PurchaseInventoryHistoryDTO implements Serializable {

     private String inventoryDate;

     private double inventoryTotalMoney;

     private int inventoryHistorySize;

     private List<ItemIndexDTO> itemIndexDTOList;

    public List<ItemIndexDTO> getItemIndexDTOList() {
        return itemIndexDTOList;
    }

    public void setItemIndexDTOList(List<ItemIndexDTO> itemIndexDTOList) {
        this.itemIndexDTOList = itemIndexDTOList;
    }

    public double getInventoryTotalMoney() {
        return inventoryTotalMoney;
    }

    public int getInventoryHistorySize() {
        return inventoryHistorySize;
    }

    public void setInventoryTotalMoney(double inventoryTotalMoney) {
        this.inventoryTotalMoney = inventoryTotalMoney;
    }

    public void setInventoryHistorySize(int inventoryHistorySize) {
        this.inventoryHistorySize = inventoryHistorySize;
    }

    public void setInventoryDate(String inventoryDate) {
        this.inventoryDate = inventoryDate;
    }

    public String getInventoryDate() {
        return inventoryDate;
    }
}
