package com.bcgogo.search.dto;

import com.bcgogo.common.Pager;
import com.bcgogo.txn.dto.RepairOrderDTO;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wenjun
 * Date: 13-10-16
 * Time: 上午9:26
 * To change this template use File | Settings | File Templates.
 */
public class CarConstructionInvoiceSearchResultListDTO {
  private List<RepairOrderDTO> repairOrderDTOList;
  private Pager pager;
  private int numFound;
  private int total;
  private int dispatchTotal; //施工中统计
  private int lackTotal; //缺料待修统计
  private int incomingTotal; //来料待修统计
  private int waitOutStorageTotal; //待领料统计
  private int outStorageTotal; //领料待修统计
  private int normalTotal; //正常施工统计
  private int pendingTotal; //待交付统计

  private double dispatchFee; //施工中应收总额
  private double pendingFee; //待交付应收总额

  public List<RepairOrderDTO> getRepairOrderDTOList() {
    return repairOrderDTOList;
  }

  public void setRepairOrderDTOList(List<RepairOrderDTO> repairOrderDTOList) {
    this.repairOrderDTOList = repairOrderDTOList;
  }

  public int getNumFound() {
    return numFound;
  }

  public void setNumFound(int numFound) {
    this.numFound = numFound;
  }

  public int getLackTotal() {
    return lackTotal;
  }

  public void setLackTotal(int lackTotal) {
    this.lackTotal = lackTotal;
  }

  public int getIncomingTotal() {
    return incomingTotal;
  }

  public void setIncomingTotal(int incomingTotal) {
    this.incomingTotal = incomingTotal;
  }

  public int getWaitOutStorageTotal() {
    return waitOutStorageTotal;
  }

  public void setWaitOutStorageTotal(int waitOutStorageTotal) {
    this.waitOutStorageTotal = waitOutStorageTotal;
  }

  public int getOutStorageTotal() {
    return outStorageTotal;
  }

  public void setOutStorageTotal(int outStorageTotal) {
    this.outStorageTotal = outStorageTotal;
  }

  public int getPendingTotal() {
    return pendingTotal;
  }

  public void setPendingTotal(int pendingTotal) {
    this.pendingTotal = pendingTotal;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public Pager getPager() {
    return pager;
  }

  public void setPager(Pager pager) {
    this.pager = pager;
  }

  public int getNormalTotal() {
    return normalTotal;
  }

  public void setNormalTotal(int normalTotal) {
    this.normalTotal = normalTotal;
  }

  public int getDispatchTotal() {
    return dispatchTotal;
  }

  public void setDispatchTotal(int dispatchTotal) {
    this.dispatchTotal = dispatchTotal;
  }

  public double getDispatchFee() {
    return dispatchFee;
  }

  public void setDispatchFee(double dispatchFee) {
    this.dispatchFee = dispatchFee;
  }

  public double getPendingFee() {
    return pendingFee;
  }

  public void setPendingFee(double pendingFee) {
    this.pendingFee = pendingFee;
  }
}
