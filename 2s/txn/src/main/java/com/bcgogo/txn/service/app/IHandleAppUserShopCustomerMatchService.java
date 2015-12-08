package com.bcgogo.txn.service.app;

import com.bcgogo.enums.ExeStatus;
import com.bcgogo.enums.app.TaskType;
import com.bcgogo.enums.notification.OperatorType;
import com.bcgogo.user.dto.AppUserCustomerUpdateTaskDTO;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-12-5
 * Time: 下午1:40
 */
public interface IHandleAppUserShopCustomerMatchService {

  void handleAppUserCustomerMatch(Long appUserId);
}
