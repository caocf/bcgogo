package com.bcgogo.config.model;

import com.bcgogo.config.dto.ExportRecordDTO;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: zoujianhong
 * Date: 13-6-17
 * Time: 下午3:45
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "export_record")
public class ExportRecord extends LongIdentifier {

    private Long shopId;
    private String userNo;
    private String status;
    private String description;
    private String scene;
    public ExportRecord() {
    }

    public ExportRecord(ExportRecordDTO exportRecordDTO) {
        if(exportRecordDTO == null){
            return ;
        }
        setId(exportRecordDTO.getId());
        setScene(exportRecordDTO.getScene());
        setDescription(exportRecordDTO.getDescription());
        setShopId(exportRecordDTO.getShopId());
        setStatus(exportRecordDTO.getStatus());
        setUserNo(exportRecordDTO.getUserNo());
    }

    @Column(name = "shop_id")
    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    @Column(name = "user_no", length = 100)
    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    @Column(name = "status", length = 20)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "description", length = 4000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    @Column(name = "scene")
    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }


    public ExportRecordDTO toDTO() {
        ExportRecordDTO exportRecordDTO = new ExportRecordDTO();
        exportRecordDTO.setId(this.getId());
        exportRecordDTO.setShopId(this.shopId);
        exportRecordDTO.setDescription(this.description);
        exportRecordDTO.setStatus(this.status);
        exportRecordDTO.setScene(this.scene);
        return exportRecordDTO;
    }
}
