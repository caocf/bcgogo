package com.bcgogo.remind.dto;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 13-6-7
 * Time: 上午10:07
 */
public class TitlePromptDTO {
  private Integer todoOrderAmount;
  private Integer todoSalesOrdersAmount;
  private Integer todoSalesReturnOrdersAmount;
  private Integer todoPurchaseOrdersAmount;
  private Integer todoPurchaseReturnOrdersAmount;
  private Integer todoRemindAmount;
  private Integer todoRepairRemindAmount;
  private Integer todoArrearRemindAmount;
  private Integer todoTxnRemindAmount;
  private Integer todoCustomerServiceRemindAmount;
  private Integer todoTxnRemindAmountNavi;
  private Integer todoCustomerAmountNavi;
  private Integer todoImpactRemindAmountNavi;

  public TitlePromptDTO() {
  }

  public TitlePromptDTO(Integer todoOrderAmount, Integer todoSalesOrdersAmount, Integer todoSalesReturnOrdersAmount, Integer todoPurchaseOrdersAmount, Integer todoPurchaseReturnOrdersAmount, Integer todoRemindAmount, Integer todoRepairRemindAmount, Integer todoArrearRemindAmount, Integer todoTxnRemindAmount, Integer todoCustomerServiceRemindAmount, Integer todoTxnRemindAmountNavi, Integer todoCustomerAmountNavi,Integer todoImpactRemindAmountNavi) {
    this.todoOrderAmount = todoOrderAmount;
    this.todoSalesOrdersAmount = todoSalesOrdersAmount;
    this.todoSalesReturnOrdersAmount = todoSalesReturnOrdersAmount;
    this.todoPurchaseOrdersAmount = todoPurchaseOrdersAmount;
    this.todoPurchaseReturnOrdersAmount = todoPurchaseReturnOrdersAmount;
    this.todoRemindAmount = todoRemindAmount;
    this.todoRepairRemindAmount = todoRepairRemindAmount;
    this.todoArrearRemindAmount = todoArrearRemindAmount;
    this.todoTxnRemindAmount = todoTxnRemindAmount;
    this.todoCustomerServiceRemindAmount = todoCustomerServiceRemindAmount;
    this.todoTxnRemindAmountNavi = todoTxnRemindAmountNavi;
    this.todoCustomerAmountNavi = todoCustomerAmountNavi;
    this.todoImpactRemindAmountNavi = todoImpactRemindAmountNavi;
  }

  public Integer getTodoImpactRemindAmountNavi() {
    return todoImpactRemindAmountNavi;
  }

  public void setTodoImpactRemindAmountNavi(Integer todoImpactRemindAmountNavi) {
    this.todoImpactRemindAmountNavi = todoImpactRemindAmountNavi;
  }

  public Integer getTodoOrderAmount() {
    return todoOrderAmount;
  }

  public void setTodoOrderAmount(Integer todoOrderAmount) {
    this.todoOrderAmount = todoOrderAmount;
  }

  public Integer getTodoSalesOrdersAmount() {
    return todoSalesOrdersAmount;
  }

  public void setTodoSalesOrdersAmount(Integer todoSalesOrdersAmount) {
    this.todoSalesOrdersAmount = todoSalesOrdersAmount;
  }

  public Integer getTodoSalesReturnOrdersAmount() {
    return todoSalesReturnOrdersAmount;
  }

  public void setTodoSalesReturnOrdersAmount(Integer todoSalesReturnOrdersAmount) {
    this.todoSalesReturnOrdersAmount = todoSalesReturnOrdersAmount;
  }

  public Integer getTodoPurchaseOrdersAmount() {
    return todoPurchaseOrdersAmount;
  }

  public void setTodoPurchaseOrdersAmount(Integer todoPurchaseOrdersAmount) {
    this.todoPurchaseOrdersAmount = todoPurchaseOrdersAmount;
  }

  public Integer getTodoPurchaseReturnOrdersAmount() {
    return todoPurchaseReturnOrdersAmount;
  }

  public void setTodoPurchaseReturnOrdersAmount(Integer todoPurchaseReturnOrdersAmount) {
    this.todoPurchaseReturnOrdersAmount = todoPurchaseReturnOrdersAmount;
  }

  public Integer getTodoRemindAmount() {
    return todoRemindAmount;
  }

  public void setTodoRemindAmount(Integer todoRemindAmount) {
    this.todoRemindAmount = todoRemindAmount;
  }

  public Integer getTodoRepairRemindAmount() {
    return todoRepairRemindAmount;
  }

  public void setTodoRepairRemindAmount(Integer todoRepairRemindAmount) {
    this.todoRepairRemindAmount = todoRepairRemindAmount;
  }

  public Integer getTodoArrearRemindAmount() {
    return todoArrearRemindAmount;
  }

  public void setTodoArrearRemindAmount(Integer todoArrearRemindAmount) {
    this.todoArrearRemindAmount = todoArrearRemindAmount;
  }

  public Integer getTodoTxnRemindAmount() {
    return todoTxnRemindAmount;
  }

  public void setTodoTxnRemindAmount(Integer todoTxnRemindAmount) {
    this.todoTxnRemindAmount = todoTxnRemindAmount;
  }

  public Integer getTodoCustomerServiceRemindAmount() {
    return todoCustomerServiceRemindAmount;
  }

  public void setTodoCustomerServiceRemindAmount(Integer todoCustomerServiceRemindAmount) {
    this.todoCustomerServiceRemindAmount = todoCustomerServiceRemindAmount;
  }

  public Integer getTodoTxnRemindAmountNavi() {
    return todoTxnRemindAmountNavi;
  }

  public void setTodoTxnRemindAmountNavi(Integer todoTxnRemindAmountNavi) {
    this.todoTxnRemindAmountNavi = todoTxnRemindAmountNavi;
  }

  public Integer getTodoCustomerAmountNavi() {
    return todoCustomerAmountNavi;
  }

  public void setTodoCustomerAmountNavi(Integer todoCustomerAmountNavi) {
    this.todoCustomerAmountNavi = todoCustomerAmountNavi;
  }

}
