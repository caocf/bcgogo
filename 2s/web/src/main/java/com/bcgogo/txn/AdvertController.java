package com.bcgogo.txn;

import com.bcgogo.api.AppUserDTO;
import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.image.DataImageRelationDTO;
import com.bcgogo.config.dto.image.ImageInfoDTO;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.config.upyun.UpYunManager;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.config.util.ImageUtils;
import com.bcgogo.enums.YesNo;
import com.bcgogo.enums.config.DataType;
import com.bcgogo.enums.config.ImageType;
import com.bcgogo.enums.txn.AdvertStatus;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.AdvertDTO;
import com.bcgogo.txn.dto.StoreHouseDTO;
import com.bcgogo.txn.service.IAdvertService;
import com.bcgogo.txn.service.IStoreHouseService;
import com.bcgogo.txn.service.pushMessage.IAppointPushMessageService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import com.opensymphony.module.sitemesh.Page;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * User: Administrator
 * Date: 14-4-14
 * Time: 下午4:56
 */
@Controller
@RequestMapping("/advert.do")
public class AdvertController {

  private static final Logger LOG = LoggerFactory.getLogger(AllocateRecordController.class);


  @RequestMapping(params = "method=toAdvertList")
  public String toAdvertList(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    if (WebUtil.getShopId(request) == null) {
      return "/";
    }
    model.put("upYunFileDTO", UpYunManager.getInstance().generateDefaultUpYunFileDTO(WebUtil.getShopId(request)));
    try {
      model.put("beginDateStr",DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,System.currentTimeMillis()));
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }

    return "/customer/advert/advertList";
  }


  @RequestMapping(params = "method=searchShopAdvert")
  @ResponseBody
  public List searchShopAdvert(ModelMap model, HttpServletRequest request, String startTimeStr, String endTimeStr, AdvertStatus[] advertStatus, Integer startPageNo) {
    Long shopId = WebUtil.getShopId(request);

    List list = new ArrayList();

    try {
      if (shopId == null) {
        return list;
      }
      IAdvertService advertService = ServiceManager.getService(IAdvertService.class);

      Long startDate = null;
      Long endDate = null;
      if (StringUtil.isNotEmpty(startTimeStr)) {
        startDate = DateUtil.getStartTimeOfDate(startTimeStr);
      }
      if (StringUtil.isNotEmpty(endTimeStr)) {
        endDate = DateUtil.getEndTimeOfDate(endTimeStr);
      }

      int count = advertService.countAdvertByDateStatus(shopId, startDate, endDate, advertStatus);
      Pager pager = new Pager(count, startPageNo == null ? 1 : startPageNo, 10);

      List<AdvertDTO> advertDTOList = null;
      if (count > 0) {
        advertDTOList = advertService.getAdvertByDateStatus(shopId, startDate, endDate, advertStatus, pager);
      }
      list.add(advertDTOList);
      list.add(pager);
      return list;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return list;
  }


  @RequestMapping(params = "method=saveOrUpdateAdvert")
  @ResponseBody
  public Result saveOrUpdateAdvert(ModelMap model, HttpServletRequest request, AdvertDTO advertDTO) {
    Long shopId = WebUtil.getShopId(request);

    Result result = new Result();
    try {
      if (shopId == null) {
        return result;
      }
      IAdvertService advertService = ServiceManager.getService(IAdvertService.class);

      advertDTO.setShopId(shopId);
      advertDTO.setEditDate(System.currentTimeMillis());
      advertDTO.setUser(WebUtil.getUserName(request));
      advertDTO.setUserId(WebUtil.getUserId(request));

      if (StringUtil.isEmpty(advertDTO.getBeginDateStr())) {
        advertDTO.setBeginDate(DateUtil.getStartTimeOfToday());
      } else {
        advertDTO.setBeginDate(DateUtil.getStartTimeOfDate(advertDTO.getBeginDateStr()));
      }

      if (StringUtil.isNotEmpty(advertDTO.getEndDateStr())) {
        advertDTO.setEndDate(DateUtil.getEndTimeOfDate(advertDTO.getEndDateStr()));
      }

      if (advertDTO.getStatus() == AdvertStatus.ACTIVE) {
        advertDTO.setPublishDate(System.currentTimeMillis());
      }


      List<DataImageRelationDTO> dataImageRelationDTOList = new ArrayList<DataImageRelationDTO>();

      String description = advertDTO.getDescription();
      String descriptionTemp = advertDTO.getDescription();
      if (StringUtils.isNotBlank(description)) {
        List<String> imageUrlList = StringUtil.getImgStr(description);
        if (CollectionUtils.isNotEmpty(imageUrlList)) {
          DataImageRelationDTO dataImageRelationDTO = null;
          String imageUrl = null;
          for (int i = 0; i < imageUrlList.size(); i++) {
            imageUrl = imageUrlList.get(i);
            if (StringUtils.isNotBlank(imageUrl)) {
              dataImageRelationDTO = new DataImageRelationDTO(shopId, null, DataType.SHOP_ADVERT, ImageType.SHOP_ADVERT_IMAGE, i);
              dataImageRelationDTO.setImageInfoDTO(new ImageInfoDTO(shopId, imageUrl.split(ConfigUtils.getUpYunSeparator())[0].replaceAll(ConfigUtils.getUpYunDomainUrl(), "")));//编辑的时候过滤 !version
              dataImageRelationDTOList.add(dataImageRelationDTO);
              description = description.replaceAll(imageUrl, ImageUtils.ImageSrcPlaceHolder + i);
            }
          }
          advertDTO.setContainImage(YesNo.YES);
          advertDTO.setDescription(description);
        }
      }

      AdvertDTO resultDTO = advertService.saveOrUpdateAdvert(advertDTO);


      Set<ImageType> imageTypeSet = new HashSet<ImageType>();
      imageTypeSet.add(ImageType.SHOP_ADVERT_IMAGE);
      if (CollectionUtils.isNotEmpty(dataImageRelationDTOList)) {
        for (DataImageRelationDTO dataImageRelationDTO : dataImageRelationDTOList) {
          dataImageRelationDTO.setDataId(resultDTO.getId());
        }

        IImageService imageService = ServiceManager.getService(IImageService.class);
        imageService.saveOrUpdateDataImageDTOs(shopId, imageTypeSet, DataType.SHOP_ADVERT, resultDTO.getId(), dataImageRelationDTOList.toArray(new DataImageRelationDTO[dataImageRelationDTOList.size()]));
      }


      if (resultDTO == null) {
        result.setSuccess(false);
      } else {
        result.setSuccess(true);
        result.setData(resultDTO);
        resultDTO.setDescription(descriptionTemp);

        if (resultDTO.getStatus() == AdvertStatus.ACTIVE) {
          IAppointPushMessageService pushMessageService = ServiceManager.getService(IAppointPushMessageService.class);
          pushMessageService.createShopAdvertMessage2App(advertDTO);
        }

      }

      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return result;
  }


  @RequestMapping(params = "method=publishAdvert")
  @ResponseBody
  public Result publishAdvert(ModelMap model, HttpServletRequest request, String idStr) {
    Long shopId = WebUtil.getShopId(request);

    Result result = new Result(false);
    try {
      if (shopId == null) {
        return result;
      }
      IAdvertService advertService = ServiceManager.getService(IAdvertService.class);
      result = advertService.publishAdvert(NumberUtil.longValue(idStr));

      if (result.isSuccess()) {
        AdvertDTO advertDTO = (AdvertDTO) result.getData();
        IAppointPushMessageService pushMessageService = ServiceManager.getService(IAppointPushMessageService.class);
        pushMessageService.createShopAdvertMessage2App(advertDTO);
      }

      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=repealAdvert")
  @ResponseBody
  public Result repealAdvert(ModelMap model, HttpServletRequest request, String idStr) {
    Long shopId = WebUtil.getShopId(request);

    Result result = new Result(false);
    try {
      if (shopId == null) {
        return result;
      }
      IAdvertService advertService = ServiceManager.getService(IAdvertService.class);
      result = advertService.repealAdvert(NumberUtil.longValue(idStr));

      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=getAdvertDetail")
  @ResponseBody
  public Result getAdvertDetail(ModelMap model, HttpServletRequest request, String idStr) {
    Long shopId = WebUtil.getShopId(request);

    Result result = new Result(false);
    try {
      if (shopId == null) {
        return result;
      }
      IAdvertService advertService = ServiceManager.getService(IAdvertService.class);
      AdvertDTO advertDTO = null;
      if (NumberUtil.isLongNumber(idStr)) {
        advertDTO = advertService.getAdvertById(Long.valueOf(idStr));
        if (advertDTO != null) {
          result.setData(advertDTO);
          result.setSuccess(true);
        }
      }
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return result;
  }


}
