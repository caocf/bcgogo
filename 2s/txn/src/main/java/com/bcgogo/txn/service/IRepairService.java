package com.bcgogo.txn.service;

import com.bcgogo.cache.DataHolder;
import com.bcgogo.common.Result;
import com.bcgogo.enums.OperationTypes;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.RepairRemindEventTypes;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.search.dto.CustomerSupplierSearchConditionDTO;
import com.bcgogo.search.dto.InventorySearchIndexDTO;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.search.dto.CarConstructionInvoiceSearchResultListDTO;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.Service;
import com.bcgogo.txn.model.TxnWriter;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 12-12-12
 * Time: 上午11:05
 * To change this template use File | Settings | File Templates.
 */
public interface IRepairService {

  void handleProductForRepairOrder(RepairOrderDTO repairOrderDTO) throws Exception;

  void saveRepairOrderWithPicking(RepairOrderDTO repairOrderDTO) throws Exception;

//  public String initRepairOrderService(RepairOrderDTO repairOrderDTO) throws Exception;

  /**
   * 根据名字获取服务 包括已经删除的同名服务
   *
   * @param shopId
   * @param isIncludeDisabled
   * @param serviceNames
   * @return
   */
  public Map<String, Service> getRepairOrderServiceByNames(Long shopId, boolean isIncludeDisabled, String... serviceNames);

  Map<String, CategoryDTO> initBusinessCategoryRepairOrder(RepairOrderDTO repairOrderDTO) throws Exception;

  void updateRepairOrderWithPicking(RepairOrderDTO repairOrderDTO) throws Exception;

  Result validateCopy(Long repairOrderId, Long shopId);

  void saveOrUpdateRepairRemindEventWithRepairPicking(RepairOrderDTO repairOrderDTO) throws Exception;

  RepairOrderDTO createRepairOrder(RepairOrderDTO repairOrderDTO) throws Exception;

  RepairOrderDTO RFCreateRepairOrder(RepairOrderDTO repairOrderDTO) throws Exception;

  public void setServiceWorksAndProductSaler(RepairOrderDTO repairOrderDTO) throws Exception;

  RepairOrderDTO updateRepairOrder(RepairOrderDTO repairOrderDTO) throws Exception;

  boolean addNewProduct(RepairOrderDTO repairOrderDTO, RepairOrderItemDTO itemDTO, ProductDTO productDTO) throws Exception;

  void addVehicleToProduct(RepairOrderDTO repairOrderDTO, RepairOrderItemDTO itemDTO) throws Exception;

  Result validateRepairPicking(RepairOrderDTO repairOrderDTO, String validateType) throws Exception;

  void updateInventoryByRepealRepairOrderWithRepairPicking(RepairOrderDTO repairOrderDTO, TxnWriter writer) throws Exception;

  void getProductInfo(RepairOrderDTO repairOrderDTO) throws Exception;

  void initRepairOrderModel(RepairOrderDTO repairOrderDTO, ModelMap model) throws Exception;

  Result validateRepairOrderOnSaveDraft(RepairOrderDTO repairOrderDTO) throws Exception;

  /**
   * 只取repairOrder  不取item和客户信息
   */

  RepairOrderDTO getSimpleRepairOrderDTO(Long shopId, Long id);


  /**
   * 新增或者更新产品信息，老商品新增单位,不包括InventorySearchIndex
   *
   * @param repairOrderDTO
   * @return
   * @throws Exception
   */
  void saveOrUpdateProductForRepairOrder(RepairOrderDTO repairOrderDTO) throws Exception;

  RepairOrderDTO RFUpdateRepairOrder(RepairOrderDTO repairOrderDTO) throws Exception;

  void saveOrUpdateRepairRemindEventNoRepairPicking(RepairOrderDTO repairOrderDTO) throws Exception;

  void repealRepairOrder(RepairOrderDTO repairOrderDTO, Long toStorehouseId) throws Exception;

  public List<InventorySearchIndex> updateInventoryAndInventorySearchIndexByRepealedRepairOrderDTO(Long toStorehouseId, RepairOrderDTO repairOrderDTO, TxnWriter writer) throws Exception;

  public CarConstructionInvoiceSearchResultListDTO getRepairOrderByShopId(Long shopId, OrderStatus[] orderStatus, RepairRemindEventTypes repairRemindEventTypes, int pagNo, int pageSize) throws Exception;

  public int countRepairOrderByDate(Long shopId, Long fromTime, Long toTime);

  public void getCustomerByTodayServiceVehicle(Long shopId, CustomerSupplierSearchConditionDTO searchConditionDTO) throws Exception;

  public List<String> getTodayServiceVehicleByCustomerId(Long shopId, Set<Long> customerIdSet) throws Exception;

  Map<Long, List<RepairOrderServiceDTO>> getRepairOrderServiceDTOMap(Set<Long> repairOrderIds);

  /**
   * 保存对应的代金券交易记录到数据库
   *
   * @param repairOrderDTO
   */
  public void updateConsumingRecordFromRepairOrder(RepairOrderDTO repairOrderDTO);

  Result accountRepairOrderByCouponConsumingRecord(RepairOrderDTO repairOrderDTO) throws Exception;

  public void overdueConsumingRecordAccount(Long overdueTime) throws Exception;
}

