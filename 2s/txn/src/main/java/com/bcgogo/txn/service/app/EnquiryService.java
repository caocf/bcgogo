package com.bcgogo.txn.service.app;

import com.bcgogo.api.EnquiryDTO;
import com.bcgogo.api.EnquiryShopResponseDTO;
import com.bcgogo.api.EnquiryTargetShopDTO;
import com.bcgogo.common.Pager;
import com.bcgogo.common.Pair;
import com.bcgogo.common.Result;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.app.EnquiryShopResponseStatus;
import com.bcgogo.enums.app.EnquiryStatus;
import com.bcgogo.enums.app.EnquiryTargetShopStatus;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.enquiry.EnquirySearchConditionDTO;
import com.bcgogo.txn.dto.enquiry.ShopEnquiryDTO;
import com.bcgogo.txn.dto.pushMessage.enquiry.AppEnquiryParameter;
import com.bcgogo.txn.dto.pushMessage.enquiry.ShopQuoteEnquiryParameter;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.app.Enquiry;
import com.bcgogo.txn.model.app.EnquiryShopResponse;
import com.bcgogo.txn.model.app.EnquiryTargetShop;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.pushMessage.IEnquiryPushMessageService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-10-24
 * Time: 下午2:40
 */
@Component
public class EnquiryService implements IEnquiryService {
  private static final Logger LOG = LoggerFactory.getLogger(EnquiryService.class);

  @Autowired
  private TxnDaoManager txnDaoManager;

  /**
   * 处理保存预约单
   * @param enquiryDTO EnquiryDTO
   * @param imageScenes List<ImageScene>
   * @return EnquiryDTO
   * @throws Exception
   */
  @Override
  public EnquiryDTO handleSaveEnquiry(EnquiryDTO enquiryDTO, List<ImageScene> imageScenes) throws Exception {
    IImageService imageService = ServiceManager.getService(IImageService.class);
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      //保存enquiry
      enquiryDTO.setCreateTime(System.currentTimeMillis());
      enquiryDTO.setLastUpdateTime(System.currentTimeMillis());
      enquiryDTO.setStatus(EnquiryStatus.SAVED);
      Enquiry enquiry = new Enquiry();
      enquiry.fromDTO(enquiryDTO);
      writer.save(enquiry);
      enquiryDTO.setId(enquiry.getId());
      //保存enquiryTargetShop
      List<EnquiryTargetShopDTO> enquiryTargetShopDTOs = enquiryDTO.generateEnquiryTargetShopDTO();
      if (CollectionUtils.isNotEmpty(enquiryTargetShopDTOs)) {
        for (EnquiryTargetShopDTO enquiryTargetShopDTO : enquiryTargetShopDTOs) {
          if (enquiryTargetShopDTO != null && enquiryTargetShopDTO.validSaveOrUpdate()) {
            enquiryTargetShopDTO.setShopResponseStatus(EnquiryShopResponseStatus.UN_RESPONSE);
            enquiryTargetShopDTO.setId(null);
            EnquiryTargetShop enquiryTargetShop = new EnquiryTargetShop();
            enquiryTargetShop.saveFromDTO(enquiryTargetShopDTO);
            writer.save(enquiryTargetShop);
            enquiryTargetShopDTO.setId(enquiryTargetShop.getId());
          }
        }
      }
      enquiryDTO.setEnquiryTargetShops(enquiryTargetShopDTOs.toArray(new EnquiryTargetShopDTO[enquiryTargetShopDTOs.size()]));
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    //保存config库的enquiry  images
    imageService.handleEnquiryImages(enquiryDTO, imageScenes);
    return enquiryDTO;
  }

  /**
   * @param enquiryDTO
   * @param imageScenes
   * @return
   * @throws Exception
   */
  @Override
  public EnquiryDTO handleUpdateEnquiry(EnquiryDTO enquiryDTO, List<ImageScene> imageScenes) throws Exception {
    if (enquiryDTO != null && enquiryDTO.getId() != null) {
      IImageService imageService = ServiceManager.getService(IImageService.class);
      TxnWriter writer = txnDaoManager.getWriter();
      Object status = writer.begin();
      try {
        //保存enquiry
        enquiryDTO.setLastUpdateTime(System.currentTimeMillis());
        enquiryDTO.setStatus(EnquiryStatus.SAVED);
        Enquiry enquiry = writer.getEnquiryById(enquiryDTO.getId(), enquiryDTO.getAppUserNo());
        if (enquiry != null) {
          enquiryDTO.setCreateTime(enquiry.getCreateTime());
          enquiry.fromDTO(enquiryDTO);
          writer.update(enquiry);
          //保存更新删除enquiryTargetShop
          saveOrUpdateEnquiryTargetShops(enquiryDTO, writer);
          writer.commit(status);
        } else {
          throw new Exception("enquiry不存在，无法更新预约单！");
        }
      } finally {
        writer.rollback(status);
      }
      //保存config库的enquiry  images
      imageService.handleEnquiryImages(enquiryDTO, imageScenes);
    }
    return enquiryDTO;
  }

  @Override
  public EnquiryDTO handleSendEnquiry(EnquiryDTO enquiryDTO, List<ImageScene> imageScenes) throws Exception {
    IImageService imageService = ServiceManager.getService(IImageService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IEnquiryPushMessageService enquiryPushMessageService = ServiceManager.getService(IEnquiryPushMessageService.class);
    //组装单据号，发送时间
    if (!ArrayUtils.isEmpty(enquiryDTO.getEnquiryTargetShops())) {
      for (EnquiryTargetShopDTO enquiryTargetShopDTO : enquiryDTO.getEnquiryTargetShops()) {
        if (enquiryTargetShopDTO != null && enquiryTargetShopDTO.validToSent()) {
          enquiryTargetShopDTO.setReceiptNo(txnService.getReceiptNo(enquiryTargetShopDTO.getTargetShopId(), OrderTypes.ENQUIRY, null));
          enquiryTargetShopDTO.setSendTime(System.currentTimeMillis());
          enquiryTargetShopDTO.setShopResponseStatus(EnquiryShopResponseStatus.UN_RESPONSE);
        }
      }
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      //保存enquiry
      enquiryDTO.setStatus(EnquiryStatus.SENT);
      enquiryDTO.setLastUpdateTime(System.currentTimeMillis());
      Enquiry enquiry = null;
      if (enquiryDTO.getId() != null) {
        enquiry = writer.getEnquiryById(enquiryDTO.getId(), enquiryDTO.getAppUserNo());
        enquiryDTO.setCreateTime(enquiry.getCreateTime());
      }
      if (enquiry == null) {
        enquiry = new Enquiry();
        enquiryDTO.setCreateTime(enquiryDTO.getLastUpdateTime());
      }
      enquiry.fromDTO(enquiryDTO);
      writer.saveOrUpdate(enquiry);
      enquiryDTO.setId(enquiry.getId());
      saveOrUpdateEnquiryTargetShops(enquiryDTO, writer);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    //保存config库的enquiry  images
    imageService.handleEnquiryImages(enquiryDTO, imageScenes);

    if(!ArrayUtils.isEmpty(enquiryDTO.getEnquiryTargetShops())){
      for(EnquiryTargetShopDTO enquiryTargetShopDTO : enquiryDTO.getEnquiryTargetShops()){
        if (enquiryTargetShopDTO != null && enquiryTargetShopDTO.getStatus() == EnquiryTargetShopStatus.SENT) {
          enquiryPushMessageService.createAppSubmitEnquiryMessageToShop(new AppEnquiryParameter(enquiryTargetShopDTO,enquiryDTO));
        }
      }
    }
    return enquiryDTO;
  }

   //保存更新删除enquiryTargetShop
  private void saveOrUpdateEnquiryTargetShops(EnquiryDTO enquiryDTO, TxnWriter writer) {
    List<EnquiryTargetShop> enquiryTargetShops = writer.getEnquiryTargetShopByEnquiryIdAndStatus(enquiryDTO.getId(),
        EnquiryTargetShopStatus.ENABLED_STATUSES);
    List<EnquiryTargetShopDTO> enquiryTargetShopDTOs = enquiryDTO.generateEnquiryTargetShopDTO();
    if (CollectionUtils.isNotEmpty(enquiryTargetShopDTOs)) {
      for (EnquiryTargetShopDTO enquiryTargetShopDTO : enquiryTargetShopDTOs) {
        boolean isExist = false;
        if (CollectionUtils.isNotEmpty(enquiryTargetShops)) {
          Iterator<EnquiryTargetShop> iterator = enquiryTargetShops.iterator();
          while (iterator.hasNext()) {
            EnquiryTargetShop iteratorTargetShop = iterator.next();
            if (iteratorTargetShop != null && enquiryTargetShopDTO.getTargetShopId().equals(iteratorTargetShop.getTargetShopId())) {
              iteratorTargetShop.updateFromDTO(enquiryTargetShopDTO);
              writer.update(iteratorTargetShop);
              isExist = true;
              iterator.remove();
            }
          }
        }
        if (!isExist) {
          EnquiryTargetShop enquiryTargetShop = new EnquiryTargetShop();
          enquiryTargetShop.saveFromDTO(enquiryTargetShopDTO);
          writer.save(enquiryTargetShop);
          enquiryTargetShopDTO.setId(enquiryTargetShop.getId());
        }
      }
    }
    //删除失效的
    if (CollectionUtils.isNotEmpty(enquiryTargetShops)) {
      for (EnquiryTargetShop enquiryTargetShop : enquiryTargetShops) {
        enquiryTargetShop.setStatus(EnquiryTargetShopStatus.DISABLED);
        writer.update(enquiryTargetShop);
      }
    }
    enquiryDTO.setEnquiryTargetShops(enquiryTargetShopDTOs.toArray(new EnquiryTargetShopDTO[enquiryTargetShopDTOs.size()]));
  }

  @Override
  public EnquiryDTO getSimpleEnquiryDTO(Long id, String appUserNo) {
    EnquiryDTO enquiryDTO = null;
    if(id != null && StringUtils.isNotEmpty(appUserNo)){
      TxnWriter writer = txnDaoManager.getWriter();
      Enquiry enquiry = writer.getEnquiryById(id,appUserNo);
      if(enquiry != null){
        enquiryDTO = enquiry.toDTO();
      }
    }
    return enquiryDTO;
  }

  @Override
  public List<EnquiryDTO> getEnquiryListByUserNoAndStatus(String appUserNo, Set<EnquiryStatus> enquiryStatuses,
                                                          List<ImageScene> shopImageScenes, Pager pager) {
    IImageService imageService = ServiceManager.getService(IImageService.class);
    List<EnquiryDTO> enquiryDTOs = new ArrayList<EnquiryDTO>();
    if (StringUtils.isNotEmpty(appUserNo)) {
      enquiryDTOs = getSimpleEnquiryDTOsByAppUserNoAndStatus(appUserNo, enquiryStatuses, pager);
      Set<Long> ids = new HashSet<Long>();
      if (CollectionUtils.isNotEmpty(enquiryDTOs)) {
        for (EnquiryDTO enquiryDTO : enquiryDTOs) {
          if (enquiryDTO != null && enquiryDTO.getId() != null) {
            ids.add(enquiryDTO.getId());
          }
        }
      }

      imageService.addAppEnquiryImage(enquiryDTOs, shopImageScenes, true);

      if (CollectionUtils.isNotEmpty(ids)) {
        Map<Long, List<EnquiryShopResponseDTO>> enquiryShopResponseMap = getEnquiryShopResponseMapByEnquiryIds(ids);
        Set<EnquiryTargetShopStatus> enquiryTargetShopStatuses = new HashSet<EnquiryTargetShopStatus>();
        enquiryTargetShopStatuses.add(EnquiryTargetShopStatus.SENT);
        Map<Long, List<EnquiryTargetShopDTO>> enquiryTargetShopMap = getEnquiryTargetShopMapByEnquiryIdsAndStatus(ids, enquiryTargetShopStatuses);
        for (EnquiryDTO enquiryDTO : enquiryDTOs) {
          if (enquiryDTO != null && enquiryDTO.getId() != null) {
            List<EnquiryShopResponseDTO> enquiryShopResponseDTOs = enquiryShopResponseMap.get(enquiryDTO.getId());
            if (CollectionUtils.isNotEmpty(enquiryShopResponseDTOs)) {
              enquiryDTO.setEnquiryShopResponses(enquiryShopResponseDTOs.toArray(new EnquiryShopResponseDTO[enquiryShopResponseDTOs.size()]));
            }
            List<EnquiryTargetShopDTO> enquiryTargetShopDTOs = enquiryTargetShopMap.get(enquiryDTO.getId());
            if (CollectionUtils.isNotEmpty(enquiryTargetShopDTOs)) {
              enquiryDTO.setEnquiryTargetShops(enquiryTargetShopDTOs.toArray(new EnquiryTargetShopDTO[enquiryTargetShopDTOs.size()]));
            }
          }
        }
      }
    }
    return enquiryDTOs;
  }

  private Map<Long, List<EnquiryTargetShopDTO>> getEnquiryTargetShopMapByEnquiryIdsAndStatus(Set<Long> enquiryIds, Set<EnquiryTargetShopStatus> statuses) {
    Map<Long, List<EnquiryTargetShopDTO>> enquiryTargetShopMap = new HashMap<Long, List<EnquiryTargetShopDTO>>();
    if (CollectionUtils.isEmpty(enquiryIds)) {
      return enquiryTargetShopMap;
    }
    List<EnquiryTargetShop> enquiryShopResponses = txnDaoManager.getWriter().getEnquiryTargetShopByEnquiryIdsAndStatus(enquiryIds, statuses);
    if (CollectionUtils.isNotEmpty(enquiryShopResponses)) {
      for (EnquiryTargetShop enquiryTargetShop : enquiryShopResponses) {
        if (enquiryTargetShop != null && enquiryTargetShop.getEnquiryId() != null) {
          List<EnquiryTargetShopDTO> enquiryTargetShopDTOs = enquiryTargetShopMap.get(enquiryTargetShop.getEnquiryId());
          if (enquiryTargetShopDTOs == null) {
            enquiryTargetShopDTOs = new ArrayList<EnquiryTargetShopDTO>();
          }
          enquiryTargetShopDTOs.add(enquiryTargetShop.toDTO());
          enquiryTargetShopMap.put(enquiryTargetShop.getEnquiryId(), enquiryTargetShopDTOs);
        }
      }
    }
    return enquiryTargetShopMap;
  }

  private Map<Long, List<EnquiryShopResponseDTO>> getEnquiryShopResponseMapByEnquiryIds(Set<Long> ids) {
    Map<Long, List<EnquiryShopResponseDTO>> enquiryShopResponseMap = new HashMap<Long, List<EnquiryShopResponseDTO>>();
    if(CollectionUtils.isNotEmpty(ids)){
      List<EnquiryShopResponse> enquiryShopResponses = txnDaoManager.getWriter().getEnquiryShopResponseByEnquiryIds(ids);
      if(CollectionUtils.isNotEmpty(enquiryShopResponses)){
        for(EnquiryShopResponse enquiryShopResponse : enquiryShopResponses){
          if(enquiryShopResponse != null && enquiryShopResponse.getEnquiryId() != null) {
            List<EnquiryShopResponseDTO> enquiryShopResponseDTOs = enquiryShopResponseMap.get(enquiryShopResponse.getEnquiryId());
            if(enquiryShopResponseDTOs == null){
              enquiryShopResponseDTOs = new ArrayList<EnquiryShopResponseDTO>();
            }
            enquiryShopResponseDTOs.add(enquiryShopResponse.toDTO());
            enquiryShopResponseMap.put(enquiryShopResponse.getEnquiryId(), enquiryShopResponseDTOs);
          }
        }
      }
    }
    return enquiryShopResponseMap;
  }

  private List<EnquiryDTO> getSimpleEnquiryDTOsByAppUserNoAndStatus(String appUserNo, Set<EnquiryStatus> enquiryStatuses, Pager pager) {
    List<EnquiryDTO> enquiryDTOs = new ArrayList<EnquiryDTO>();
    if(StringUtils.isNotBlank(appUserNo)){
     List<Enquiry> enquiries = txnDaoManager.getWriter().getEnquiryByAppUserNoAndStatus(appUserNo, enquiryStatuses, pager);
     if(CollectionUtils.isNotEmpty(enquiries)){
       for(Enquiry enquiry : enquiries){
         enquiryDTOs.add(enquiry.toDTO());
       }
     }
    }
    return enquiryDTOs;
  }

  @Override
  public int countEnquiryListByUserNoAndStatus(String appUserNo, Set<EnquiryStatus> enquiryStatuses) {
    if(StringUtils.isNotEmpty(appUserNo)){
      TxnWriter writer = txnDaoManager.getWriter();
      return writer.countEnquiryListByUserNoAndStatus(appUserNo,enquiryStatuses);
    }
    return 0;
  }

  @Override
  public EnquiryDTO getEnquiryDTODetail(Long enquiryId, String appUserNo, List<ImageScene> imageScenes) {
    IImageService imageService = ServiceManager.getService(IImageService.class);
    EnquiryDTO enquiryDTO = null;
    if(enquiryId != null && StringUtils.isNotBlank(appUserNo)){
      Enquiry enquiry = txnDaoManager.getWriter().getEnquiryById(enquiryId,appUserNo);
      if(enquiry != null){
        enquiryDTO = enquiry.toDTO();
       Set<Long> enquiryIds = new HashSet<Long>();
        enquiryIds.add(enquiryId);
        List<EnquiryDTO> enquiryDTOs = new ArrayList<EnquiryDTO>();
        enquiryDTOs.add(enquiryDTO);
        imageService.addAppEnquiryImage(enquiryDTOs, imageScenes, false);
        Map<Long, List<EnquiryShopResponseDTO>> enquiryShopResponseMap = getEnquiryShopResponseMapByEnquiryIds(enquiryIds);
            Set<EnquiryTargetShopStatus> enquiryTargetShopStatuses = new HashSet<EnquiryTargetShopStatus>();
        if(enquiryDTO.getStatus() == EnquiryStatus.SENT){
          enquiryTargetShopStatuses.add(EnquiryTargetShopStatus.SENT);
        }else {
          enquiryTargetShopStatuses.addAll(EnquiryTargetShopStatus.ENABLED_STATUSES);
        }
        Map<Long, List<EnquiryTargetShopDTO>> enquiryTargetShopMap = getEnquiryTargetShopMapByEnquiryIdsAndStatus(enquiryIds, enquiryTargetShopStatuses);
        List<EnquiryShopResponseDTO> enquiryShopResponseDTOs = enquiryShopResponseMap.get(enquiryDTO.getId());
        if (CollectionUtils.isNotEmpty(enquiryShopResponseDTOs)) {
          enquiryDTO.setEnquiryShopResponses(enquiryShopResponseDTOs.toArray(new EnquiryShopResponseDTO[enquiryShopResponseDTOs.size()]));
        }
        List<EnquiryTargetShopDTO> enquiryTargetShopDTOs = enquiryTargetShopMap.get(enquiryDTO.getId());
        if (CollectionUtils.isNotEmpty(enquiryTargetShopDTOs)) {
          enquiryDTO.setEnquiryTargetShops(enquiryTargetShopDTOs.toArray(new EnquiryTargetShopDTO[enquiryTargetShopDTOs.size()]));
        }
      }
    }
    return enquiryDTO;
  }

  @Override
  public void handleDeleteEnquiry(String appUserNo, Long enquiryId) throws Exception {
    if (StringUtils.isNotBlank(appUserNo) && enquiryId != null) {
      TxnWriter writer = txnDaoManager.getWriter();
      Object status = writer.begin();
      try {
        Enquiry enquiry = writer.getEnquiryById(enquiryId, appUserNo);
        if (enquiry != null) {
          enquiry.setStatus(EnquiryStatus.DISABLED);
          writer.save(enquiry);
          writer.commit(status);
        }
      } finally {
        writer.rollback(status);
      }
    }
  }

  @Override
  public int countShopEnquiryDTOs(EnquirySearchConditionDTO searchCondition) {
    if(searchCondition == null || searchCondition.getShopId() == null){
      return 0;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countShopEnquiryDTOs(searchCondition);
  }

  @Override
  public List<ShopEnquiryDTO> searchShopEnquiryDTOs(EnquirySearchConditionDTO searchCondition) {
    List<ShopEnquiryDTO> enquiryDTOs = new ArrayList<ShopEnquiryDTO>();
    if(searchCondition == null || searchCondition.getShopId() == null){
       return enquiryDTOs;
     }
     TxnWriter writer = txnDaoManager.getWriter();
     List<Pair<Enquiry,EnquiryTargetShop>> enquiries =  writer.searchShopEnquiries(searchCondition);
    if(CollectionUtils.isNotEmpty(enquiries)){
      for (Pair<Enquiry,EnquiryTargetShop> pair : enquiries){
        if(pair != null && pair.getKey() != null && pair.getValue() != null){
          Enquiry enquiry = pair.getKey();
          EnquiryTargetShop enquiryTargetShop = pair.getValue();
          ShopEnquiryDTO shopEnquiryDTO = new ShopEnquiryDTO();
          shopEnquiryDTO.setEnquiryDTO(enquiry.toDTO());
          shopEnquiryDTO.setEnquiryTargetShopDTO(enquiryTargetShop.toDTO());
          enquiryDTOs.add(shopEnquiryDTO);
        }
      }
    }
    return enquiryDTOs;
  }

  @Override
  public ShopEnquiryDTO getShopEnquiryDTODetail(Long enquiryOrderId, Long shopId) {
    IImageService imageService = ServiceManager.getService(IImageService.class);
    ShopEnquiryDTO shopEnquiryDTO = null;
    if (enquiryOrderId != null && shopId != null) {
      TxnWriter writer = txnDaoManager.getWriter();
      Pair<Enquiry, EnquiryTargetShop> pair = writer.getShopEnquiryByIdAndShopId(enquiryOrderId, shopId);
      if (pair != null && pair.getValue() != null && pair.getKey() != null) {
        shopEnquiryDTO = new ShopEnquiryDTO();
        shopEnquiryDTO.setEnquiryDTO(pair.getKey().toDTO());
        shopEnquiryDTO.setEnquiryTargetShopDTO(pair.getValue().toDTO());
        List<EnquiryShopResponse> enquiryShopResponses = writer.getEnquiryShopResponseByEnquiryIdAndShopId(enquiryOrderId, shopId);
        if (CollectionUtils.isNotEmpty(enquiryShopResponses)) {
          List<EnquiryShopResponseDTO> shopResponseDTOs = new ArrayList<EnquiryShopResponseDTO>();
          for (EnquiryShopResponse enquiryShopResponse : enquiryShopResponses) {
            shopResponseDTOs.add(enquiryShopResponse.toDTO());
          }
          shopEnquiryDTO.setEnquiryShopResponses(shopResponseDTOs.toArray(new EnquiryShopResponseDTO[shopResponseDTOs.size()]));
        }
        List<ImageScene> imageScenes = new ArrayList<ImageScene>();
        imageScenes.add(ImageScene.SHOP_ENQUIRY_APP_SMALL);
        imageScenes.add(ImageScene.SHOP_ENQUIRY_APP_FULL);
        imageService.addShopEnquiryImage(shopEnquiryDTO,imageScenes,false);
      }
    }
    return shopEnquiryDTO;
  }

  @Override
  public ShopEnquiryDTO getSimpleShopEnquiryDTO(Long enquiryOrderId, Long shopId) {
    ShopEnquiryDTO shopEnquiryDTO = null;
    if (enquiryOrderId != null && shopId != null) {
      TxnWriter writer = txnDaoManager.getWriter();
      Pair<Enquiry, EnquiryTargetShop> pair = writer.getShopEnquiryByIdAndShopId(enquiryOrderId, shopId);
      if (pair != null && pair.getValue() != null && pair.getKey() != null) {
        shopEnquiryDTO = new ShopEnquiryDTO();
        shopEnquiryDTO.setEnquiryDTO(pair.getKey().toDTO());
        shopEnquiryDTO.setEnquiryTargetShopDTO(pair.getValue().toDTO());
      }
    }
    return shopEnquiryDTO;
  }

  @Override
  public Result validateAddResponse(ShopEnquiryDTO shopEnquiryDTO,EnquiryShopResponseDTO enquiryShopResponseDTO) {
    if(shopEnquiryDTO == null
        || shopEnquiryDTO.getEnquiryStatus() == EnquiryStatus.DISABLED
        || shopEnquiryDTO.getAppEnquiryTargetStatus() == EnquiryTargetShopStatus.DISABLED){
      return new Result("当前询价单已经关闭，请选择其他询价单报价",false);
    }
    if(enquiryShopResponseDTO == null || StringUtils.isBlank(enquiryShopResponseDTO.getResponseMsg())){
      return new Result("报价信息为空，请填写报价信息",false);
    }
    return new Result();
  }

  @Override
  public void handelAddEnquiryShopResponse(ShopEnquiryDTO shopEnquiryDTO, EnquiryShopResponseDTO enquiryShopResponseDTO)throws Exception{
    if(enquiryShopResponseDTO == null
        || enquiryShopResponseDTO.getEnquiryId() == null
        || enquiryShopResponseDTO.getShopId() == null
        || shopEnquiryDTO == null) {
      return;
    }
    IEnquiryPushMessageService enquiryPushMessageService = ServiceManager.getService(IEnquiryPushMessageService.class);
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      EnquiryShopResponse enquiryShopResponse = new EnquiryShopResponse();
      enquiryShopResponse.fromDTO(enquiryShopResponseDTO);
      writer.save(enquiryShopResponse);
      enquiryShopResponseDTO.setId(enquiryShopResponse.getId());
      List<EnquiryTargetShop> enquiryTargetShops = writer.getEnquiryTargetShopByShopIdAndEnquiryId(
          enquiryShopResponseDTO.getShopId(), enquiryShopResponse.getEnquiryId());
      if (CollectionUtils.isNotEmpty(enquiryTargetShops)) {
        EnquiryTargetShop enquiryTargetShop = enquiryTargetShops.get(0);
        enquiryTargetShop.setLastResponseTime(enquiryShopResponseDTO.getResponseTime());
        enquiryTargetShop.setShopResponseStatus(EnquiryShopResponseStatus.RESPONSE);
        writer.update(enquiryTargetShop);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    enquiryPushMessageService.createShopQuoteEnquiryMessageToApp(new ShopQuoteEnquiryParameter(shopEnquiryDTO,enquiryShopResponseDTO));
  }
}
