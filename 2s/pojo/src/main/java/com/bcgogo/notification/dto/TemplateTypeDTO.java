package com.bcgogo.notification.dto;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: lijie
 * Date: 11-11-10
 * Time: 下午3:53
 * To change this template use File | Settings | File Templates.
 */
public class TemplateTypeDTO implements Serializable {
    private Long id;
    private String name;
    private Long status;
    private String memo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
