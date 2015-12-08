package com.bcgogo.txn.bcgogoListener.test;

import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-4-10
 * Time: 下午5:11
 * To change this template use File | Settings | File Templates.
 */
public class Test {
  public static void main(String[] args) {

    Hashtable<String, String> a = new Hashtable();
    a.put("a","a");
    a.put("c","c");
    a.put("a","b");
      System.out.println(a);

//    SalesOrderDTO salesOrderDTO= new SalesOrderDTO();
//    salesOrderDTO.setTotal(1234L);
//
//		WashOrderDTO washOrderDTO = new WashOrderDTO();
//    washOrderDTO.setCashNum(3456L);
//
//
//    SaleOrderSavedEvent saleOrderSavedEvent = new SaleOrderSavedEvent(salesOrderDTO);
//    WashOrderSavedEvent washOrderSavedEvent = new WashOrderSavedEvent(washOrderDTO);
//
//    BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
//    BcgogoEventPublisher bcgogoEventPublisher2 = new BcgogoEventPublisher();
//
//		bcgogoEventPublisher.publisherWashOrderSaved(washOrderSavedEvent);
//		bcgogoEventPublisher2.publisherSaleOrderSaved(saleOrderSavedEvent);
//     ITxnService txnService = ServiceManager.getService(TxnService.class);
//
//    WashOrderDTO washOrderDTO  = new WashOrderDTO();
//    washOrderDTO.setShopId(10000010001790080L);
//    washOrderDTO.setCreationDate("2012-04-24 09:42");
//    washOrderDTO.setCustomerId(10000010009020802L);
//    washOrderDTO.setOrderType(2L);
//    washOrderDTO.setCashNum(120.00);
//    washOrderDTO.setWashWorker("测试103");
//    washOrderDTO.setVehicle("黑D00100");
//    washOrderDTO.setContactNum("13521212121");
//
//    try{
//      washOrderDTO =txnService.createWashOrder(washOrderDTO);
//      System.out.println("保存成功"+washOrderDTO.getId());
//    }catch (Exception e){
//      e.printStackTrace();
//    }

	}
}
