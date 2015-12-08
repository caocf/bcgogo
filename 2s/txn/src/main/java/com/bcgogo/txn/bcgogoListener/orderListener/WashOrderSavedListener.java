package com.bcgogo.txn.bcgogoListener.orderListener;

import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IOperationLogService;
import com.bcgogo.enums.*;
import com.bcgogo.product.service.IProductSolrService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.orderEvent.WashOrderSavedEvent;
import com.bcgogo.txn.dto.WashBeautyOrderDTO;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.txn.service.IVehicleStatService;
import com.bcgogo.txn.service.solr.IOrderSolrWriterService;
import com.bcgogo.user.service.solr.IVehicleSolrWriterService;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-4-10
 * Time: 下午5:09
 */
public class WashOrderSavedListener extends OrderSavedListener {
  private WashOrderSavedEvent washOrderSavedEvent;
	private ICustomerService customerService;

	public ICustomerService getCustomerService() {
		if(customerService == null){
			customerService = ServiceManager.getService(ICustomerService.class);
		}
		return customerService;
	}

	public void setCustomerService(ICustomerService customerService) {
		this.customerService = customerService;
	}

	public WashOrderSavedListener(WashOrderSavedEvent washOrderSavedEvent) {
    super();
    this.washOrderSavedEvent = washOrderSavedEvent;
  }

  public void run() {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    WashBeautyOrderDTO washBeautyOrderDTO = this.washOrderSavedEvent.getWashBeautyOrderDTO();
    if (washBeautyOrderDTO == null) {
      LOG.error("WashOrderSavedListener washBeautyOrderDTO is null!");
      return;
    }

    boolean isRepeal = false;
    if(washBeautyOrderDTO.getStatus() == OrderStatus.WASH_REPEAL){
      isRepeal = true;
    }

    try {
      if (isRepeal) {
        businessStatByOrder(washBeautyOrderDTO, isRepeal, true, washBeautyOrderDTO.getVestDate());
      } else {

        //ad by WLF 保存洗车单的创建日志
        ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(
            new OperationLogDTO(washBeautyOrderDTO.getShopId(), washBeautyOrderDTO.getUserId(), washBeautyOrderDTO.getId(), ObjectTypes.WASH_ORDER, OperationTypes.CREATE)) ;
        //更新缓存
        if (washBeautyOrderDTO.getDebt() > 0) {
          txnService.updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.DEBT, washBeautyOrderDTO.getShopId());
        }

        if (DateUtil.isCurrentTime(washBeautyOrderDTO.getVestDate())) {
          businessStatByOrder(washBeautyOrderDTO, isRepeal, true, washBeautyOrderDTO.getVestDate());
        } else {
          businessStatByOrder(washBeautyOrderDTO, isRepeal, false, washBeautyOrderDTO.getVestDate());
          orderRunBusinessStatChange(washBeautyOrderDTO);
        }
        if(StringUtils.isNotEmpty(washBeautyOrderDTO.getAppUserNo())){
          ServiceManager.getService(IAppUserService.class)
              .updateAppUserLastExpenseShopId(washBeautyOrderDTO.getShopId(), washBeautyOrderDTO.getAppUserNo());
        }
      }
      vehicleServeStat(washBeautyOrderDTO, isRepeal);
      IVehicleStatService vehicleStatService = ServiceManager.getService(IVehicleStatService.class);
      vehicleStatService.customerVehicleConsumeStat(washBeautyOrderDTO,OrderTypes.WASH_BEAUTY,isRepeal,null);
      reCreateSolrIndex(washBeautyOrderDTO);
    } catch (Exception e) {
      LOG.error("WashOrderSavedListener.run" + washBeautyOrderDTO.toString());
      LOG.error(e.getMessage(),e);
    }
    washOrderSavedEvent.setOrderFlag(true);
  }

  private void reCreateSolrIndex(WashBeautyOrderDTO washBeautyOrderDTO) throws Exception {
    //reindex order solr
    ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(washBeautyOrderDTO.getShopId()), OrderTypes.WASH_BEAUTY, washBeautyOrderDTO.getId());

    ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(washBeautyOrderDTO.getCustomerId());

    ServiceManager.getService(IOrderSolrWriterService.class).createRepairServiceSolrIndex(washBeautyOrderDTO.getShopId(), washBeautyOrderDTO.getServiceIds());

       //新增车辆信息添加到solr
    if (washBeautyOrderDTO.isAddVehicleInfoToSolr()) {
      VehicleDTO vehicleDTOForSolr = new VehicleDTO(washBeautyOrderDTO);
      if (vehicleDTOForSolr.getId() != null) {
        List<VehicleDTO> vehicleDTOs = new ArrayList<VehicleDTO>();
        vehicleDTOs.add(vehicleDTOForSolr);
        ServiceManager.getService(IProductSolrService.class).addVehicleForSearch(vehicleDTOs);
      }
    }

    ServiceManager.getService(IVehicleSolrWriterService.class).createVehicleSolrIndex(washBeautyOrderDTO.getShopId(), washBeautyOrderDTO.getVechicleId());
  }
}

