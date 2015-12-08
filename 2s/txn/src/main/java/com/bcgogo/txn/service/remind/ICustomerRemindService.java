package com.bcgogo.txn.service.remind;

import com.bcgogo.remind.dto.RemindEventDTO;
import com.bcgogo.user.dto.CustomerServiceJobDTO;

import java.util.List;

/**
 * Created by XinyuQiu on 14-5-26.
 */
public interface ICustomerRemindService {
  List<CustomerServiceJobDTO> generateCustomerServiceJob(Long shopId, List<RemindEventDTO> txnRemindEventDTOList) throws Exception;
}
