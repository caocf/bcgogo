package com.bcgogo.config.dto;

import com.bcgogo.txn.dto.assistantStat.AssistantStatSearchDTO;
import com.bcgogo.utils.StringUtil;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zoujianhong
 * Date: 13-6-17
 * Time: 下午3:50
 * To change this template use File | Settings | File Templates.
 */
public class ExportRecordDTO {

    private Long id;
    private Long shopId;
    private String userNo;
    private String status;
    private String description;
    private String scene;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

}
