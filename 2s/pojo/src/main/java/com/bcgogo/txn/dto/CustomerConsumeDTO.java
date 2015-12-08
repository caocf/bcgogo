package com.bcgogo.txn.dto;

import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderDTO;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-2-20
 * Time: 下午2:16
 * To change this template use File | Settings | File Templates.
 */
public class CustomerConsumeDTO implements Serializable {
    private int itemCount;
    private List<ItemIndexDTO> itemIndexDTOList;
    private int orderCount;
    private List<OrderDTO> orderDTOList;

    public int getItemCount() {
        return itemCount;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public List<OrderDTO> getOrderDTOList() {
        return orderDTOList;
    }

    public List<ItemIndexDTO> getItemIndexDTOList() {
        return itemIndexDTOList;
    }

    public void setItemIndexDTOList(List<ItemIndexDTO> itemIndexDTOList) {
        this.itemIndexDTOList = itemIndexDTOList;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public void setOrderDTOList(List<OrderDTO> orderDTOList) {
        this.orderDTOList = orderDTOList;
    }
}
