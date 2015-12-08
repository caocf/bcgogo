package com.bcgogo.txn.model;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.RepealOrderDTO;
import org.hibernate.CallbackException;
import org.hibernate.Session;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: zhoudongming
 * Date: 12-6-18
 * Time: 上午10:21
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "repeal_order")
public class RepealOrder extends LongIdentifier {
    private Long shopId;
    private Long date;
    private String no;
    private Long deptId;
    private String dept;
    private Long executorId;
    private String executor;
    private Double total;
    private String memo;
    private Long editorId;
    private String editor;
    private Long editDate;
    private Long orderId;
    private String orderType;
    private OrderTypes orderTypeEnum;
    private Long repealDate;
    private Double settledAmount;
    private Long status;
    private OrderStatus statusEnum;
    private Long vestDate;
    private Double debt;

    @Column(name = "shop_id")
    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    @Column(name = "date")
    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    @Column(name = "no", length = 20)
    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    @Column(name = "dept_id")
    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    @Column(name = "dept", length = 20)
    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    @Column(name = "executor_id")
    public Long getExecutorId() {
        return executorId;
    }

    public void setExecutorId(Long executorId) {
        this.executorId = executorId;
    }

    @Column(name = "executor", length = 20)
    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }

    @Column(name = "total")
    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    @Column(name = "memo", length = 500)
    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Column(name = "editor_id")
    public Long getEditorId() {
        return editorId;
    }

    public void setEditorId(Long editorId) {
        this.editorId = editorId;
    }

    @Column(name = "editor", length = 20)
    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    @Column(name = "edit_date")
    public Long getEditDate() {
        return editDate;
    }

    public void setEditDate(Long editDate) {
        this.editDate = editDate;
    }

    @Column(name = "order_id")
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    @Column(name = "order_type", length = 20)
    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    @Column(name = "repeal_date")
    public Long getRepealDate() {
        return repealDate;
    }

    public void setRepealDate(Long repealDate) {
        this.repealDate = repealDate;
    }

    @Column(name = "settled_amount")
    public Double getSettledAmount() {
        return settledAmount;
    }

    public void setSettledAmount(Double settledAmount) {
        this.settledAmount = settledAmount;
    }

    @Column(name = "status")
    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    @Column(name = "vest_date")
    public Long getVestDate() {
        return vestDate;
    }

    public void setVestDate(Long vestDate) {
        this.vestDate = vestDate;
    }

    @Column(name = "debt")
    public Double getDebt() {
        return debt;
    }

    public void setDebt(Double debt) {
        this.debt = debt;
    }

		public RepealOrderDTO toDTO(){
			RepealOrderDTO repealOrderDTO = new RepealOrderDTO();

			repealOrderDTO.setId(this.getId());
			repealOrderDTO.setShopId(this.getShopId());
			repealOrderDTO.setDate(this.getDate());
			repealOrderDTO.setNo(this.getNo());
			repealOrderDTO.setDeptId(this.getDeptId());
			repealOrderDTO.setDept(this.getDept());
			repealOrderDTO.setExecutorId(this.getExecutorId());
			repealOrderDTO.setExecutor(this.getExecutor());
			repealOrderDTO.setTotal(this.getTotal());
			repealOrderDTO.setMemo(this.getMemo());
			repealOrderDTO.setEditorId(this.getEditorId());
			repealOrderDTO.setEditor(this.getEditor());
			repealOrderDTO.setEditDate(this.getEditDate());
			repealOrderDTO.setOrderId(this.getOrderId());
			repealOrderDTO.setOrderType(this.getOrderType());
			repealOrderDTO.setRepealDate(this.getRepealDate());
			repealOrderDTO.setSettledAmount(this.getSettledAmount());
			repealOrderDTO.setStatus(this.getStatus());
			repealOrderDTO.setVestDate(this.getVestDate());
			repealOrderDTO.setDebt(this.getDebt());

			return repealOrderDTO;

		}

  @Column(name="order_type_enum")
  @Enumerated(EnumType.STRING)
  public OrderTypes getOrderTypeEnum() {
    return orderTypeEnum;
  }

  public void setOrderTypeEnum(OrderTypes orderTypeEnum) {
    this.orderTypeEnum = orderTypeEnum;
  }

  @Column(name="status_enum")
  @Enumerated(EnumType.STRING)
  public OrderStatus getStatusEnum() {
    return statusEnum;
  }

  public void setStatusEnum(OrderStatus statusEnum) {
    this.statusEnum = statusEnum;
  }

}
