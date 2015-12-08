package com.bcgogo.txn.service.app;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.AppUserBillDTO;
import com.bcgogo.common.AllListResult;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.enums.app.AppUserBillStatus;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.enums.config.DataType;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.enums.config.ImageType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnReader;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.app.AppUserBill;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * User: ZhangJuntao
 * Date: 13-10-25
 * Time: 上午11:16
 */
@Component
public class AppUserBillService implements IAppUserBillService {
  private static final Logger LOG = LoggerFactory.getLogger(AppUserBillService.class);

  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public boolean deleteAppUserBillService(Long id, String appUserNo, String failMsg) {
    if (StringUtils.isNotBlank(failMsg)) return false;
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      //delete bill
      AppUserBill bill = writer.getById(AppUserBill.class, id);
      if (bill == null) {
        LOG.error("can't find bill by id {}", id);
        return false;
      }
      if (bill.getAppUserNo().equals(appUserNo)) {
        bill.setStatus(AppUserBillStatus.DISABLED);
        writer.update(bill);
        writer.commit(status);
      } else {
        return false;
      }
      return true;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public ApiResponse saveAppUserBill(AppUserBillDTO dto) throws Exception {
    String vResult = "";
    TxnReader reader = txnDaoManager.getReader();
    if (validate(vResult, reader)) {
      return MessageCode.toApiResponse(MessageCode.ACCOUNT_SAVE_SUCCESS, vResult);
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      if (dto.getId() == null) {
        //add bill
        AppUserBill bill = new AppUserBill(dto);
        writer.save(bill);
        dto.setId(bill.getId());
      } else {
        //update  bill
        AppUserBill bill = writer.getById(AppUserBill.class, dto.getId());
        bill.fromDTO(dto);
        writer.update(bill);
      }
      writer.commit(status);
      saveImage(dto);
      return null;
    } finally {
      writer.rollback(status);
    }
  }

  private void saveImage(AppUserBillDTO dto) throws Exception {
    Set<ImageType> set = new HashSet<ImageType>();
    set.add(ImageType.APP_USER_BILL_MAIN_IMAGE);
    set.add(ImageType.APP_USER_BILL_AUXILIARY_IMAGE);
    ServiceManager.getService(IImageService.class).
        saveOrUpdateAppUserImages(dto.getAppUserNo(), set, DataType.APP_USER_BILL, dto.getId(), dto.getImageList());
  }

  private boolean validate(String result, TxnReader reader) {
    //todo validate
    return false;
  }

  @Override
  public AllListResult<AppUserBillDTO> getAppUserBillListByUserNo(String appUserNo, List<ImageScene> imageSceneList, int currentPage, int pageSize) {
    //get bills
    TxnReader reader = txnDaoManager.getReader();
    List<AppUserBill> appUserBills = reader.getAppUserBillListByUserNo(appUserNo, pageSize, currentPage);
    AllListResult<AppUserBillDTO> result = new AllListResult<AppUserBillDTO>();
    List<AppUserBillDTO> appUserBillDTOs = new ArrayList<AppUserBillDTO>();
    for (AppUserBill bill : appUserBills) {
      appUserBillDTOs.add(bill.toDTO());
    }
    ServiceManager.getService(IImageService.class).addAppUserBillImages(imageSceneList, false, appUserBillDTOs);
    result.setTotalRows(reader.countAppUserBillListByUserNo(appUserNo));
    result.setResults(appUserBillDTOs);
    return result;
  }

  @Override
  public AppUserBillDTO getAppUserBillById(Long id, List<ImageScene> imageSceneList) {
    TxnReader reader = txnDaoManager.getReader();
    AppUserBill bill = reader.getById(AppUserBill.class, id);
    if (bill == null) {
      LOG.error("can't find bill by id {}", id);
      return null;
    }
    AppUserBillDTO dto = bill.toDTO();
    ServiceManager.getService(IImageService.class).addAppUserBillImages(imageSceneList, false, Collections.singletonList(dto));
    return dto;
  }


}
