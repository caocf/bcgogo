package com.bcgogo.api;

/**
 * Created by XinyuQiu on 14-7-10.
 */
public class ObdOperationPermissionDTO {
  private boolean isAdmin = false;
  private boolean isEdit = false;
  private boolean isDelete = false;
  private boolean isPackage = false;
  private boolean isLog = false;
  private boolean isSplit = false;
  private boolean isOutStorage = false;
  private boolean isSell = false;
  private boolean isReturn = false;
  private boolean isImport = false;

  public boolean isAdmin() {
    return isAdmin;
  }

  public void setAdmin(boolean isAdmin) {
    this.isAdmin = isAdmin;
  }

  public boolean isEdit() {
    return isEdit;
  }

  public void setEdit(boolean isEdit) {
    this.isEdit = isEdit;
  }

  public boolean isDelete() {
    return isDelete;
  }

  public void setDelete(boolean isDelete) {
    this.isDelete = isDelete;
  }

  public boolean isPackage() {
    return isPackage;
  }

  public void setPackage(boolean isPackage) {
    this.isPackage = isPackage;
  }

  public boolean isLog() {
    return isLog;
  }

  public void setLog(boolean isLog) {
    this.isLog = isLog;
  }

  public boolean isSplit() {
    return isSplit;
  }

  public void setSplit(boolean isSplit) {
    this.isSplit = isSplit;
  }

  public boolean isOutStorage() {
    return isOutStorage;
  }

  public void setOutStorage(boolean isOutStorage) {
    this.isOutStorage = isOutStorage;
  }

  public boolean isSell() {
    return isSell;
  }

  public void setSell(boolean isSell) {
    this.isSell = isSell;
  }

  public boolean isReturn() {
    return isReturn;
  }

  public void setReturn(boolean isReturn) {
    this.isReturn = isReturn;
  }

  public boolean isImport() {
    return isImport;
  }

  public void setImport(boolean isImport) {
    this.isImport = isImport;
  }
}
