package com.bcgogo.notification.dto;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: lijie
 * Date: 11-11-10
 * Time: 下午3:50
 * To change this template use File | Settings | File Templates.
 */
public class TemplateDTO implements Serializable {
    private Long id;
    private Long type;
    private String content;
    private Long status;
    private Long lastEditorId;
    private String lastEditorName;
    private String memo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getLastEditorId() {
        return lastEditorId;
    }

    public void setLastEditorId(Long lastEditorId) {
        this.lastEditorId = lastEditorId;
    }

    public String getLastEditorName() {
        return lastEditorName;
    }

    public void setLastEditorName(String lastEditorName) {
        this.lastEditorName = lastEditorName;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
