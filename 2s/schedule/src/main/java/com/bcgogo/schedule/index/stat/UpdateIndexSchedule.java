package com.bcgogo.schedule.index.stat;

import com.bcgogo.enums.OrderTypes;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.search.service.IOrderIndexService;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.IUserService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-6-29
 * Time: 下午2:00
 * To change this template use File | Settings | File Templates.
 */
public class UpdateIndexSchedule extends BcgogoQuartzJobBean {
  public static final Logger LOG = LoggerFactory.getLogger(UpdateIndexSchedule.class);
  private static boolean lock = false;

  @Override
  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
//    if (lock) {
//      return;
//    } else {
//      lock = true;
//      try {
//        LOG.info("后台开始更新order_index和item_index");
//        IConfigService configService = ServiceManager.getService(IConfigService.class);
//        ISearchService searchService = ServiceManager.getService(ISearchService.class);
//
//        int pageSize = 100;//分页查询 每一页查询的条数
//        int pageNum = 0; //页数
//
//        List<Shop> shopList = configService.getShop();
//        long startTime;
//        long endTime;
//        Calendar calendar = Calendar.getInstance();
//        calendar.clear();
//        int year = 2012;
//
//        for (Shop shop : shopList) {
//          long shopId = shop.getId();
//          //long shopId = 10000010001790080L;
//
//          //year
//          calendar.set(year, 0, 1, 0, 0, 0);
//          startTime = calendar.getTimeInMillis();
//          calendar.add(Calendar.YEAR, 1);
//          endTime = calendar.getTimeInMillis();
//
//          int itemIndexNum = (int) searchService.countItemIndexByShopId(shopId);
//          if (itemIndexNum > 0) {
//            pageNum = itemIndexNum % pageSize == 0 ? (itemIndexNum / pageSize) : (itemIndexNum / pageSize + 1);
//          }
//          for (int i = 0; i < pageNum; i++) {
//            List<ItemIndexDTO> itemIndexDTOList = searchService.getItemIndexListByShopId(shopId, i, pageSize);
//            if (itemIndexDTOList != null && itemIndexDTOList.size() > 0) {
//              for (ItemIndexDTO itemIndexDTO : itemIndexDTOList) {
//                if (itemIndexDTO == null || itemIndexDTO.getCustomerId() == null) {
//                  continue;
//                }
//                try {
//                  this.updateIndexCustomerInfo(itemIndexDTO, null);
//                } catch (Exception e) {
//                  LOG.error("/UpdateIndexSchedule");
//                  LOG.error("method=updateIndexCustomerInfo");
//                  LOG.error("后台更新order_index和item_index失败,customer_id为" + itemIndexDTO.getCustomerId() + "id为" + itemIndexDTO.getId());
//                  e.printStackTrace();
//                  continue;
//                }
//              }
//            }
//          }
//
//          int orderIndexNum = (int) searchService.countOrderIndexByShopId(shopId);
//          if (orderIndexNum > 0) {
//            pageNum = orderIndexNum % pageSize == 0 ? (orderIndexNum / pageSize) : (orderIndexNum / pageSize + 1);
//          }
//          for (int i = 0; i < pageNum; i++) {
//            List<OrderIndexDTO> orderIndexDTOList = searchService.getOrderIndexListByShopId(shopId, i, pageSize);
//            if (orderIndexDTOList != null && orderIndexDTOList.size() > 0) {
//              for (OrderIndexDTO orderIndexDTO : orderIndexDTOList) {
//                if (orderIndexDTO == null || orderIndexDTO.getCustomerOrSupplierId() == null) {
//                  continue;
//                }
//                try {
//                  this.updateIndexCustomerInfo(null, orderIndexDTO);
//                } catch (Exception e) {
//                  LOG.error("/UpdateIndexSchedule");
//                  LOG.error("method=updateIndexCustomerInfo");
//                  LOG.error("后台更新order_index和item_index失败,customer_id为" + orderIndexDTO.getCustomerOrSupplierId());
//                  e.printStackTrace();
//                  continue;
//                }
//              }
//            }
//          }
//        }
//      } catch (Exception e) {
//        LOG.error("/UpdateIndexSchedule");
//        LOG.error("method=executeJob");
//        LOG.error("后台更新order_index和item_index失败");
//        e.printStackTrace();
//        LOG.error(e.getMessage(), e);
//      } finally {
//        LOG.info("后台更新order_index和item_index 结束");
//        lock = false;
//      }
//    }
  }

  /**
   * 更新order_index和item_index中客户 供应商信息
   *
   * @param itemIndexDTO
   * @param orderIndexDTO
   */
  private void updateIndexCustomerInfo(ItemIndexDTO itemIndexDTO, OrderIndexDTO orderIndexDTO) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IOrderIndexService orderIndexService = ServiceManager.getService(IOrderIndexService.class);

    boolean itemUpdate = false; //判断是否需要更新
    boolean orderUpdate = false; //判断是否需要更新
    long customerOrSupplierId = 0;//客户或者供应商id

    if (itemIndexDTO != null) {
      customerOrSupplierId = itemIndexDTO.getCustomerId();
      String oldName = "";
      String newName = "";
      //如果是客户
      CustomerDTO customerDTO = userService.getCustomerById(customerOrSupplierId);
      if (customerDTO != null && customerDTO.getName() != null) {
        if(itemIndexDTO.getCustomerOrSupplierName() == null){
            oldName = itemIndexDTO.getCustomerOrSupplierName();
            itemIndexDTO.setCustomerOrSupplierName(customerDTO.getName());
            newName = itemIndexDTO.getCustomerOrSupplierName();
            itemUpdate = true;
        }else {
          if (!itemIndexDTO.getCustomerOrSupplierName().equals(customerDTO.getName())) {
            oldName = itemIndexDTO.getCustomerOrSupplierName();
            itemIndexDTO.setCustomerOrSupplierName(customerDTO.getName());
            newName = itemIndexDTO.getCustomerOrSupplierName();
            itemUpdate = true;
          }
        }

      } else {
        //如果是供应商
        SupplierDTO supplierDTO = userService.getSupplierById(customerOrSupplierId);
        if (supplierDTO != null && supplierDTO.getName() != null) {
          if(itemIndexDTO.getCustomerOrSupplierName() == null){
              oldName = itemIndexDTO.getCustomerOrSupplierName();
              itemIndexDTO.setCustomerOrSupplierName(supplierDTO.getName());
              newName = itemIndexDTO.getCustomerOrSupplierName();
              itemUpdate = true;
          }else {
            if (!itemIndexDTO.getCustomerOrSupplierName().equals(supplierDTO.getName())) {
              oldName = itemIndexDTO.getCustomerOrSupplierName();
              itemIndexDTO.setCustomerOrSupplierName(supplierDTO.getName());
              newName = itemIndexDTO.getCustomerOrSupplierName();
              itemUpdate = true;
            }
          }
        }
      }

      if (itemUpdate == true) {
        itemUpdate = false;
        searchService.updateItemIndexName(itemIndexDTO);
        LOG.info("itemIndex表 customer_id 为" + itemIndexDTO.getCustomerId() + "姓名被更新,从" + oldName + "更新为" + newName);
      }

    } else if (orderIndexDTO != null) {
      customerOrSupplierId = orderIndexDTO.getCustomerOrSupplierId();
      StringBuilder updateContent = new StringBuilder();
      updateContent.append("更新内容:");
      //如果是客户
      CustomerDTO customerDTO = userService.getCustomerById(customerOrSupplierId);
      if (customerDTO != null && customerDTO.getName() != null) {
        if(orderIndexDTO.getCustomerOrSupplierName() == null) {
          updateContent.append("name被更新,从" + orderIndexDTO.getCustomerOrSupplierName());
          orderIndexDTO.setCustomerOrSupplierName(customerDTO.getName());
          updateContent.append("更新为" + orderIndexDTO.getCustomerOrSupplierName());
          orderUpdate = true;
        } else {
          if (!orderIndexDTO.getCustomerOrSupplierName().equals(customerDTO.getName())) {
            updateContent.append("name被更新,从" + orderIndexDTO.getCustomerOrSupplierName());
            orderIndexDTO.setCustomerOrSupplierName(customerDTO.getName());
            updateContent.append("更新为" + orderIndexDTO.getCustomerOrSupplierName());
            orderUpdate = true;
          }
        }

        if(orderIndexDTO.getContactNum() == null){
          updateContent.append("mobile被更新,从" + orderIndexDTO.getContactNum());
          orderIndexDTO.setContactNum(customerDTO.getMobile());
          updateContent.append("更新为" + orderIndexDTO.getContactNum());
          orderUpdate = true;
        }else {
          if (!orderIndexDTO.getContactNum().equals(customerDTO.getMobile())) {
            updateContent.append("mobile被更新,从" + orderIndexDTO.getContactNum());
            orderIndexDTO.setContactNum(customerDTO.getMobile());
            updateContent.append("更新为" + orderIndexDTO.getContactNum());
            orderUpdate = true;
          }
        }


      } else {
        //如果是供应商
        SupplierDTO supplierDTO = userService.getSupplierById(customerOrSupplierId);
        if (supplierDTO != null && supplierDTO.getName() != null) {
          if(orderIndexDTO.getCustomerOrSupplierName() == null) {
            updateContent.append("name被更新,从" + orderIndexDTO.getCustomerOrSupplierName());
            orderIndexDTO.setCustomerOrSupplierName(supplierDTO.getName());
            updateContent.append("更新为" + orderIndexDTO.getCustomerOrSupplierName());
            orderUpdate = true;
          }else {
            if (!orderIndexDTO.getCustomerOrSupplierName().equals(supplierDTO.getName())) {
              updateContent.append("name被更新,从" + orderIndexDTO.getCustomerOrSupplierName());
              orderIndexDTO.setCustomerOrSupplierName(supplierDTO.getName());
              updateContent.append("更新为" + orderIndexDTO.getCustomerOrSupplierName());
              orderUpdate = true;
            }
          }

          if(orderIndexDTO.getContactNum() == null) {
            updateContent.append("联系方式被更新,从" + orderIndexDTO.getContactNum());
            orderIndexDTO.setContactNum(customerDTO.getMobile());
            updateContent.append("更新为" + orderIndexDTO.getContactNum());
            orderUpdate = true;
          }else {
            if (!orderIndexDTO.getContactNum().equals(customerDTO.getMobile())) {
              updateContent.append("联系方式被更新,从" + orderIndexDTO.getContactNum());
              orderIndexDTO.setContactNum(customerDTO.getMobile());
              updateContent.append("更新为" + orderIndexDTO.getContactNum());
              orderUpdate = true;
            }
          }
        }
      }

      //更新欠款
      OrderTypes orderType = orderIndexDTO.getOrderType();
      if (orderUpdate == true) {
        orderUpdate = false;
        searchService.updateOrderIndex(orderIndexDTO);
        LOG.info("orderIndex表,order_id为" + orderIndexDTO.getOrderId() + " customer_id 为" + orderIndexDTO.getCustomerOrSupplierId() + "被更新");
        LOG.info(updateContent.toString());
        //更新solr中的内容     update by zhangjuntao
//        List<OrderIndexDTO> orderIndexDTOList = new ArrayList<OrderIndexDTO>();
//        orderIndexDTOList.add(orderIndexDTO);
//        try {
//          orderIndexService.addOrderIndexToSolr(orderIndexDTOList);
//        } catch (Exception e) {
//          LOG.error("/UpdateIndexSchedule");
//          LOG.error("method=updateIndexCustomerInfo");
//          LOG.error(e.getMessage(),e);
//          LOG.error("orderIndex信息放入solr失败");
//        }
        }
      }
    }
}


