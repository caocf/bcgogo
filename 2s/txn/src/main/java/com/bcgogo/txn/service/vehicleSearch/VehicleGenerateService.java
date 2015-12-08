package com.bcgogo.txn.service.vehicleSearch;

import com.bcgogo.search.dto.VehicleSearchResultDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.model.Service;
import com.bcgogo.txn.service.RFITxnService;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.service.IContactService;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.IMembersService;
import com.bcgogo.user.service.app.IAppUserService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by XinyuQiu on 14-5-27.
 */
@Component
public class VehicleGenerateService implements IVehicleGenerateService {

  @Override
  public void generateVehicleSearchResult(Long shopId, VehicleSearchResultDTO vehicleSearchResultDTO) throws Exception {
    if(CollectionUtils.isNotEmpty(vehicleSearchResultDTO.getVehicleDTOList())){
      Set<Long> customerIdSet = new HashSet<Long>();
      for(VehicleDTO vehicleDTO:vehicleSearchResultDTO.getVehicleDTOList()){
        if(vehicleDTO.getCustomerId()!=null)
          customerIdSet.add(vehicleDTO.getCustomerId());
      }
      Map<Long,CustomerDTO> customerDTOMap = ServiceManager.getService(ICustomerService.class).getCustomerByIdSet(shopId,customerIdSet);
      Map<Long,ContactDTO> mainContactDTOMap = ServiceManager.getService(IContactService.class).getMainContactDTOMapByCusIds(customerIdSet.toArray(new Long[customerIdSet.size()]));

      for(CustomerDTO customerDTO : customerDTOMap.values()){
        ContactDTO mainContactDTO = mainContactDTOMap.get(customerDTO.getId());
        if(mainContactDTO!=null){
          customerDTO.setContactId(mainContactDTO.getId());
          customerDTO.setContact(mainContactDTO.getName());
          customerDTO.setMobile(mainContactDTO.getMobile());
        }
        //组装app,OBD 信息
        IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
        appUserService.generateVehicleAppInfo(vehicleSearchResultDTO.getVehicleDTOList());
        //获得该卡拥有的服务
        MemberDTO memberDTO = ServiceManager.getService(IMembersService.class).getMemberByCustomerId(shopId, customerDTO.getId());
        if (memberDTO != null) {
          if (memberDTO.getMemberServiceDTOs() != null) {
            RFITxnService txnService = ServiceManager.getService(RFITxnService.class);
            for (MemberServiceDTO memberServiceDTO : memberDTO.getMemberServiceDTOs()) {
              Service service = txnService.getServiceById(memberServiceDTO.getServiceId());
              if (service != null) {
                memberServiceDTO.setServiceName(service.getName());
              }
            }
          }
        }
        customerDTO.setMemberDTO(memberDTO);
      }

      for(VehicleDTO vehicleDTO:vehicleSearchResultDTO.getVehicleDTOList()){
        if(vehicleDTO.getCustomerId()!=null){
          vehicleDTO.setCustomerDTO(customerDTOMap.get(vehicleDTO.getCustomerId()));
        }
      }
    }
  }

}
