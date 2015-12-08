package com.bcgogo.txn.service;

import com.bcgogo.api.AppUserDTO;
import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.config.dto.image.DataImageDetailDTO;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.config.util.ImageUtils;
import com.bcgogo.constant.pushMessage.AppointConstant;
import com.bcgogo.constant.pushMessage.PushMessageParamsKeyConstant;
import com.bcgogo.enums.app.ServiceScope;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.enums.txn.AdvertStatus;
import com.bcgogo.enums.txn.pushMessage.PushMessageSourceType;
import com.bcgogo.enums.txn.pushMessage.PushMessageType;
import com.bcgogo.notification.velocity.AppointVelocityContext;
import com.bcgogo.notification.velocity.ShopAdvertVelocityContext;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.AdvertDTO;
import com.bcgogo.txn.dto.pushMessage.PushMessageDTO;
import com.bcgogo.txn.model.Advert;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.service.pushMessage.IAppointPushMessageService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.JsonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Administrator
 * Date: 14-4-14
 * Time: 下午5:06
 */
@Component
public class AdvertService implements IAdvertService {
  private static final Logger LOG = LoggerFactory.getLogger(AdvertService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public AdvertDTO saveOrUpdateAdvert(AdvertDTO advertDTO) {
    if (advertDTO == null) {
      return null;
    }
    TxnWriter txnWriter = txnDaoManager.getWriter();


    Advert advert = null;

    if (advertDTO.getId() != null) {
      advert = txnWriter.getById(Advert.class, advertDTO.getId());
      advert = advert.fromDTO(advertDTO);
    } else {
      advert = new Advert(advertDTO);
    }

    advert = saveOrUpdateAdvert(advert, txnWriter);
    return advert == null ? null : advert.toDTO();
  }


  public Advert saveOrUpdateAdvert(Advert advert, TxnWriter txnWriter) {
    if (txnWriter == null) {
      txnWriter = txnDaoManager.getWriter();
    }
    Object status = txnWriter.begin();
    try {
      if (advert.getId() == null) {
        txnWriter.save(advert);
      } else {
        txnWriter.update(advert);
      }
      txnWriter.commit(status);

    } finally {
      txnWriter.rollback(status);
    }
    return advert;
  }

  public AdvertDTO getAdvertById(Long id) {
    if (id == null) {
      return null;
    }
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Advert advert = txnWriter.getById(Advert.class, id);
    AdvertDTO advertDTO = advert == null ? null : advert.toDTO();
    if (advertDTO != null) {
      IImageService imageService = ServiceManager.getService(IImageService.class);
      List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
      imageSceneList.add(ImageScene.SHOP_ADVERT_INFO_DESCRIPTION_IMAGE);
      List<AdvertDTO> advertDTOList = imageService.addImageInfoToAdvertDTO(imageSceneList, false, advertDTO);
      advertDTO = CollectionUtil.isEmpty(advertDTOList) ? advertDTO : CollectionUtil.getFirst(advertDTOList);
    }

    return advertDTO;

  }

  public Result publishAdvert(Long id) {
    Result result = new Result(false);

    if (id != null) {
      TxnWriter txnWriter = txnDaoManager.getWriter();
      Advert advert = txnWriter.getById(Advert.class, id);

      if (advert != null && advert.getStatus() == AdvertStatus.WAIT_PUBLISH) {
        advert.setStatus(AdvertStatus.ACTIVE);
        advert.setPublishDate(System.currentTimeMillis());

        advert = saveOrUpdateAdvert(advert, txnWriter);
        result.setSuccess(true);
        result.setData(advert.toDTO());
        return result;
      }
    }

    result.setMsg("数据异常,请刷新页面后重试");
    return result;
  }

  public Result repealAdvert(Long id) {
    Result result = new Result(false);

    if (id != null) {
      TxnWriter txnWriter = txnDaoManager.getWriter();
      Advert advert = txnWriter.getById(Advert.class, id);

      if (advert != null && advert.getStatus() != AdvertStatus.REPEALED) {
        advert.setStatus(AdvertStatus.REPEALED);
        advert.setRepealDate(System.currentTimeMillis());

        advert = saveOrUpdateAdvert(advert, txnWriter);
        result.setSuccess(true);
        result.setData(advert.toDTO());
        return result;
      }
    }

    result.setMsg("数据异常,请刷新页面后重试");
    return result;
  }

  public int countAdvertByDateStatus(Long shopId, Long startDate, Long endDate, AdvertStatus[] advertStatuses) {
    TxnWriter txnWriter = txnDaoManager.getWriter();

    return txnWriter.countAdvertByDateStatus(shopId, startDate, endDate, advertStatuses);
  }

  public List<AdvertDTO> getAdvertByDateStatus(Long shopId, Long startDate, Long endDate, AdvertStatus[] advertStatuses, Pager pager) {
    TxnWriter txnWriter = txnDaoManager.getWriter();

    List<Advert> advertList = txnWriter.getAdvertByDateStatus(shopId, startDate, endDate, advertStatuses, pager);

    List<AdvertDTO> advertDTOList = new ArrayList<AdvertDTO>();
    if (CollectionUtil.isEmpty(advertList)) {
      return advertDTOList;
    }
    for (Advert advert : advertList) {
      advertDTOList.add(advert.toDTO());
    }
    return advertDTOList;
  }

  public void shopAdvertOverdueHandle() throws Exception {

    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.updateAdvertToOverdue(System.currentTimeMillis());
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }

  }


}
