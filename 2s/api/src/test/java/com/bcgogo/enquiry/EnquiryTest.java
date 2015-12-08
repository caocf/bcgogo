package com.bcgogo.enquiry;

import com.bcgogo.AbstractTest;
import com.bcgogo.api.*;
import com.bcgogo.api.controller.EnquiryController;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.model.ConfigDaoManager;
import com.bcgogo.config.model.ConfigWriter;
import com.bcgogo.config.model.DataImageRelation;
import com.bcgogo.config.model.ImageInfo;
import com.bcgogo.enums.app.EnquiryTargetShopStatus;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.enums.app.ValidateConstant;
import com.bcgogo.enums.common.ObjectStatus;
import com.bcgogo.enums.config.DataType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.app.Enquiry;
import com.bcgogo.txn.model.app.EnquiryTargetShop;
import com.bcgogo.utils.*;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-11-5
 * Time: 下午1:43
 */
public class EnquiryTest extends AbstractTest {

  private static final String IMAGE_PATH1 = "/2013/10/11/10000010001310032/024639-path1.jpg";
  private static final String IMAGE_PATH2 = "/2013/10/11/10000010001310032/024639-path2.jpg";

  /**
   * 使用穷举法测试接口，并对保存成功之后Enquiry，EnquiryTargetShop，DataImageRelation，ImageInfo 数据对象做比对操作。
   * 不能排除并发，业务操作流程，数据冗余保存，后续业务扩展的错误
   * 数据对象比对的要求是Entity和对应的DTO 有相同名字的基本数据类型
   * 另外接口不带入的数据，不返回的数据需要单独测试,比如 Enquiry.createTime,Enquiry.lastUpdate
   * 该测试暂时不做单元测试
   */

  public void saveEnquiryTest() throws Exception {
    //描述总共有三种情况，不填，上界，越界
    String[] description = {null, StringUtil.getCharacterNumberOrChinese(ValidateConstant.ENQUIRY_DESCRIPTION_MAX_LENGTH),
        StringUtil.getCharacterNumberOrChinese(ValidateConstant.ENQUIRY_DESCRIPTION_MAX_LENGTH + 1)};

    //照片总共有三种情况，不填，上界(上界范围内只填写path)，越界 ，
    Object[] enquiryImages = new Object[3];
    enquiryImages[0] = null;
    AppUserImageDTO[] enquiryImages1 = new AppUserImageDTO[ValidateConstant.ENQUIRY_IMAGES_MAX_SIZE];
    for (int i = 0; i < enquiryImages1.length; i++) {
      AppUserImageDTO appUserImageDTO = new AppUserImageDTO();
      appUserImageDTO.setImagePath("/2013/10/11/10000010001310032/024639-" + StringUtil.getCharacterAndNumber(30) + ".jpg");
      enquiryImages1[i] = appUserImageDTO;
    }
    enquiryImages[1] = enquiryImages1;
    AppUserImageDTO[] enquiryImages2 = new AppUserImageDTO[ValidateConstant.ENQUIRY_IMAGES_MAX_SIZE + 1];
    for (int i = 0; i < enquiryImages2.length; i++) {
      AppUserImageDTO appUserImageDTO = new AppUserImageDTO();
      appUserImageDTO.setImagePath("/2013/10/11/10000010001310032/024640-" + StringUtil.getCharacterAndNumber(30) + ".jpg");
      enquiryImages2[i] = appUserImageDTO;
    }
    enquiryImages[2] = enquiryImages2;

    //发送店铺总共有五种情况，不填，上界4种情况(一种情况下EnquiryTargetShopStatus 不相同)，越界 ，
    Object[] enquiryTargetShops = new Object[6];
    enquiryTargetShops[0] = null;
    EnquiryTargetShopStatus[] allEnquiryTargetShopStatus = new EnquiryTargetShopStatus[EnquiryTargetShopStatus.values().length];
    allEnquiryTargetShopStatus[0] = EnquiryTargetShopStatus.SELECTED;
    allEnquiryTargetShopStatus[1] = EnquiryTargetShopStatus.UNSELECTED;
    allEnquiryTargetShopStatus[2] = EnquiryTargetShopStatus.SENT;
    allEnquiryTargetShopStatus[3] = EnquiryTargetShopStatus.DISABLED;
    for (int m = 0; m < allEnquiryTargetShopStatus.length; m++) {
      EnquiryTargetShopDTO[] enquiryTargetShopDTOs = new EnquiryTargetShopDTO[ValidateConstant.ENQUIRY_TARGET_SHOP_MAX_SIZE];
      for (int i = 0; i < enquiryTargetShopDTOs.length; i++) {
        ShopDTO shopDTO = createShop(null);
        EnquiryTargetShopDTO enquiryTargetShopDTO = new EnquiryTargetShopDTO();
        enquiryTargetShopDTO.setTargetShopId(shopDTO.getId());
        enquiryTargetShopDTO.setTargetShopName(shopDTO.getName());
        enquiryTargetShopDTO.setStatus(allEnquiryTargetShopStatus[m]);
        enquiryTargetShopDTOs[i] = enquiryTargetShopDTO;
      }
      enquiryTargetShops[m + 1] = enquiryTargetShopDTOs;
    }
    EnquiryTargetShopDTO[] enquiryTargetShopDTO5 = new EnquiryTargetShopDTO[ValidateConstant.ENQUIRY_TARGET_SHOP_MAX_SIZE + 1];
    for (int i = 0; i < enquiryTargetShopDTO5.length; i++) {
      ShopDTO shopDTO = createShop(null);
      EnquiryTargetShopDTO enquiryTargetShopDTO = new EnquiryTargetShopDTO();
      enquiryTargetShopDTO.setTargetShopId(shopDTO.getId());
      enquiryTargetShopDTO.setTargetShopName(shopDTO.getName());
      enquiryTargetShopDTO.setStatus(allEnquiryTargetShopStatus[enquiryTargetShopDTO5.length % allEnquiryTargetShopStatus.length]);
      enquiryTargetShopDTO5[i] = enquiryTargetShopDTO;
    }
    enquiryTargetShops[5] = enquiryTargetShopDTO5;


    //用户名总共有三种情况，不填，上界，越界
    String[] appUserName = {null,
        StringUtil.getCharacterNumberOrChinese(ValidateConstant.ENQUIRY_APP_USER_NAME_MAX_LENGTH),
        StringUtil.getCharacterNumberOrChinese(ValidateConstant.ENQUIRY_APP_USER_NAME_MAX_LENGTH + 1)};

    //用手机号总共有三种情况，不填，上界，非法字符
    String[] appUserMobile = {null, "18001557667", "19001557667"};
    //用车牌号号总共有三种情况，不填，上界，非法字符
    String[] vehicleNo = {null, "苏E552UQ", "非法车牌号"};
    //用户账单编号
    Long billId = 132456345673456L;

    EnquiryController enquiryController = new EnquiryController();
    createAppUserAndLogin();
    if (request == null) {
      request = new MockHttpServletRequest();
    }
    if (response == null) {
      response = new MockHttpServletResponse();
    }
    request.setCookies(response.getCookies());
    TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    TxnWriter txnWriter = txnDaoManager.getWriter();

    ConfigDaoManager configDaoManager = ServiceManager.getService(ConfigDaoManager.class);
    ConfigWriter configWriter = configDaoManager.getWriter();
    for (int descriptionIndex = 0; descriptionIndex < description.length; descriptionIndex++) {
      for (int enquiryImagesIndex = 0; enquiryImagesIndex < enquiryImages.length; enquiryImagesIndex++) {
        for (int enquiryTargetShopsIndex = 0; enquiryTargetShopsIndex < enquiryTargetShops.length; enquiryTargetShopsIndex++) {
          for (int appUserNameIndex = 0; appUserNameIndex < appUserName.length; appUserNameIndex++) {
            for (int appUserMobileIndex = 0; appUserMobileIndex < appUserMobile.length; appUserMobileIndex++) {
              for (int vehicleNoIndex = 0; vehicleNoIndex < vehicleNo.length; vehicleNoIndex++) {
                EnquiryDTO enquiryDTO = new EnquiryDTO();
                enquiryDTO.setDescription(description[descriptionIndex]);
                if (enquiryImages[enquiryImagesIndex] != null) {
                  enquiryDTO.setEnquiryImages((AppUserImageDTO[]) BcgogoBeanUtils.deepCopyBean(enquiryImages[enquiryImagesIndex]));
                }
                if (enquiryTargetShops[enquiryTargetShopsIndex] != null) {
                  enquiryDTO.setEnquiryTargetShops(((EnquiryTargetShopDTO[]) BcgogoBeanUtils.deepCopyBean(enquiryTargetShops[enquiryTargetShopsIndex])));
                }
                enquiryDTO.setAppUserName(appUserName[appUserNameIndex]);
                enquiryDTO.setAppUserMobile(appUserMobile[appUserMobileIndex]);
                enquiryDTO.setVehicleNo(vehicleNo[vehicleNoIndex]);
                enquiryDTO.setBillId(billId);
                ApiResponse apiResponse;
                if (descriptionIndex == 0 && enquiryImagesIndex == 1 && enquiryTargetShopsIndex == 0
                    && appUserNameIndex == 0 && appUserMobileIndex == 0 && vehicleNoIndex == 0) {
                  apiResponse = enquiryController.saveEnquiry(request, response, enquiryDTO);
                } else {
                  apiResponse = enquiryController.saveEnquiry(request, response, enquiryDTO);
                }
//                apiResponse = enquiryController.saveEnquiry(request, response, enquiryDTO);
                if (descriptionIndex == 0 && enquiryImagesIndex == 0) {
                  Assert.assertEquals(MessageCode.ENQUIRY_SAVE_FAIL.getCode(), apiResponse.getMsgCode());

                } else if (descriptionIndex == 2) {
                  Assert.assertEquals(MessageCode.ENQUIRY_SAVE_FAIL.getCode(), apiResponse.getMsgCode());

                } else if (enquiryImagesIndex == 2) {
                  Assert.assertEquals(MessageCode.ENQUIRY_SAVE_FAIL.getCode(), apiResponse.getMsgCode());

                } else if (enquiryTargetShopsIndex == 3 || enquiryTargetShopsIndex == 4) {
                  Assert.assertEquals(MessageCode.ENQUIRY_SAVE_FAIL.getCode(), apiResponse.getMsgCode());

                } else if (enquiryTargetShopsIndex == 5) {
                  Assert.assertEquals(MessageCode.ENQUIRY_SAVE_FAIL.getCode(), apiResponse.getMsgCode());

                } else if (appUserNameIndex == 2) {
                  Assert.assertEquals(MessageCode.ENQUIRY_SAVE_FAIL.getCode(), apiResponse.getMsgCode());

                } else if (vehicleNoIndex == 2) {
                  Assert.assertEquals(MessageCode.ENQUIRY_SAVE_FAIL.getCode(), apiResponse.getMsgCode());

                } else if (appUserMobileIndex == 2) {
                  Assert.assertEquals(MessageCode.ENQUIRY_SAVE_FAIL.getCode(), apiResponse.getMsgCode());

                } else {

                  Assert.assertEquals(descriptionIndex + " " +
                      enquiryImagesIndex + " " +
                      enquiryTargetShopsIndex + " " +
                      appUserNameIndex + " " +
                      appUserMobileIndex + " " +
                      vehicleNoIndex + " " +
                      JsonUtil.objectToJson(enquiryDTO),
                      MessageCode.ENQUIRY_SAVE_SUCCESS.getCode(), apiResponse.getMsgCode());

                  Enquiry enquiry = txnWriter.getById(Enquiry.class, enquiryDTO.getId());
                  UnitTestUtil.simpleCompareSame(enquiryDTO, enquiry, EnquiryDTO.class, Enquiry.class);
                  Assert.assertNotNull(enquiry.getCreateTime());
                  Assert.assertNotNull(enquiry.getLastUpdateTime());

                  if (ArrayUtil.isNotEmpty(enquiryDTO.getEnquiryTargetShops())) {
                    for (EnquiryTargetShopDTO enquiryTargetShopDTO : enquiryDTO.getEnquiryTargetShops()) {
                      if (enquiryTargetShopDTO != null) {
                        EnquiryTargetShop enquiryTargetShop = txnWriter.getById(EnquiryTargetShop.class, enquiryTargetShopDTO.getId());
                        UnitTestUtil.simpleCompareSame(enquiryTargetShopDTO, enquiryTargetShop, EnquiryTargetShopDTO.class, EnquiryTargetShop.class);
                      }
                    }
                  }
                  if (ArrayUtil.isNotEmpty(enquiryDTO.getEnquiryImages())) {
                    for (AppUserImageDTO appUserImageDTO : enquiryDTO.getEnquiryImages()) {
                      if (appUserImageDTO != null) {
                        DataImageRelation dataImageRelation = configWriter.getById(DataImageRelation.class, appUserImageDTO.getImageId());
                        Assert.assertEquals(enquiryDTO.getAppUserNo(), dataImageRelation.getAppUserNo());
                        Assert.assertEquals(enquiryDTO.getId(), dataImageRelation.getDataId());
                        Assert.assertEquals(DataType.APP_ENQUIRY, dataImageRelation.getDataType());
                        Assert.assertEquals(appUserImageDTO.getSequence(), dataImageRelation.getImageSequence());
                        Assert.assertEquals(ObjectStatus.ENABLED, dataImageRelation.getStatus());
                        ImageInfo imageInfo = configWriter.getById(ImageInfo.class, dataImageRelation.getImageId());
                        Assert.assertEquals(ObjectStatus.ENABLED, imageInfo.getStatus());
                        Assert.assertEquals(enquiryDTO.getAppUserNo(), imageInfo.getAppUserNo());
                        Assert.assertEquals(appUserImageDTO.getImagePath(), imageInfo.getPath());
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  /**
   *   该测试暂时不做单元测试
   */

  public EnquiryDTO createSavedEnquiry(ShopDTO shopDTO) throws Exception {
    if (shopDTO == null) {
      shopDTO = createShop(null);
    }
    createAppUserAndLogin();
    EnquiryDTO enquiryDTO = new EnquiryDTO();
    enquiryDTO.setDescription(StringUtil.getCharacterNumberOrChinese(ValidateConstant.ENQUIRY_DESCRIPTION_MAX_LENGTH));
    EnquiryTargetShopDTO[] enquiryTargetShopDTOs = new EnquiryTargetShopDTO[1];
    for (int i = 0; i < enquiryTargetShopDTOs.length; i++) {
      EnquiryTargetShopDTO enquiryTargetShopDTO = new EnquiryTargetShopDTO();
      enquiryTargetShopDTO.setTargetShopId(shopDTO.getId());
      enquiryTargetShopDTO.setTargetShopName(shopDTO.getName());
      enquiryTargetShopDTO.setStatus(EnquiryTargetShopStatus.SELECTED);
      enquiryTargetShopDTOs[i] = enquiryTargetShopDTO;
    }
    enquiryDTO.setEnquiryTargetShops(enquiryTargetShopDTOs);
    AppUserImageDTO[] enquiryImages = new AppUserImageDTO[1];
    for (int i = 0; i < enquiryImages.length; i++) {
      AppUserImageDTO appUserImageDTO = new AppUserImageDTO();
      appUserImageDTO.setImagePath(IMAGE_PATH1);
      enquiryImages[i] = appUserImageDTO;
    }
    enquiryDTO.setEnquiryImages(enquiryImages);

    enquiryDTO.setAppUserName(StringUtil.getCharacterNumberOrChinese(ValidateConstant.ENQUIRY_APP_USER_NAME_MAX_LENGTH));
    enquiryDTO.setAppUserMobile("18001557667");
    enquiryDTO.setVehicleNo("苏E552UQ");
    enquiryDTO.setBillId(1234567890123456789L);
    if (request == null) {
      request = new MockHttpServletRequest();
    }
    if (response == null) {
      response = new MockHttpServletResponse();
    }
    request.setCookies(response.getCookies());
    EnquiryController enquiryController = new EnquiryController();
    enquiryController.saveEnquiry(request, response, enquiryDTO);
    return enquiryDTO;
  }

  public void updateEnquiryTest() throws Exception {
    ShopDTO shopDTO = createShop(null);
    EnquiryDTO enquiryDTO = createSavedEnquiry(shopDTO);
    EnquiryDTO enquiryLastSavedDTO = BcgogoBeanUtils.deepCopyBean(enquiryDTO);
    enquiryDTO.setDescription(StringUtil.getCharacterNumberOrChinese(ValidateConstant.ENQUIRY_DESCRIPTION_MAX_LENGTH));
    ShopDTO shopDTO2 = createShop(null);
    EnquiryTargetShopDTO[] enquiryTargetShopDTOs = new EnquiryTargetShopDTO[1];
    for (int i = 0; i < enquiryTargetShopDTOs.length; i++) {
      EnquiryTargetShopDTO enquiryTargetShopDTO = new EnquiryTargetShopDTO();
      enquiryTargetShopDTO.setTargetShopId(shopDTO2.getId());
      enquiryTargetShopDTO.setTargetShopName(shopDTO2.getName());
      enquiryTargetShopDTO.setStatus(EnquiryTargetShopStatus.UNSELECTED);
      enquiryTargetShopDTOs[i] = enquiryTargetShopDTO;
    }
    enquiryDTO.setEnquiryTargetShops(enquiryTargetShopDTOs);
    AppUserImageDTO[] enquiryImages = new AppUserImageDTO[2];
    AppUserImageDTO appUserImageDTO1 = new AppUserImageDTO();
    appUserImageDTO1.setImagePath(IMAGE_PATH2);
    enquiryImages[0] = appUserImageDTO1;
    AppUserImageDTO appUserImageDTO2 = new AppUserImageDTO();
    appUserImageDTO2.setImagePath(IMAGE_PATH1);
    enquiryImages[1] = appUserImageDTO2;
    enquiryDTO.setEnquiryImages(enquiryImages);

    enquiryDTO.setAppUserName(StringUtil.getCharacterNumberOrChinese(ValidateConstant.ENQUIRY_APP_USER_NAME_MAX_LENGTH));
    enquiryDTO.setAppUserMobile("18001557668");
    enquiryDTO.setVehicleNo("苏E552UW");
    enquiryDTO.setBillId(1234567890123456790L);
    EnquiryController enquiryController = new EnquiryController();
    ApiResponse apiResponse = enquiryController.updateEnquiry(request, response, enquiryDTO);
    Assert.assertEquals(JsonUtil.objectToJson(enquiryDTO), MessageCode.ENQUIRY_UPDATE_SUCCESS.getCode(), apiResponse.getMsgCode());
    TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    TxnWriter txnWriter = txnDaoManager.getWriter();
    ConfigDaoManager configDaoManager = ServiceManager.getService(ConfigDaoManager.class);
    ConfigWriter configWriter = configDaoManager.getWriter();
    Enquiry enquiry = txnWriter.getById(Enquiry.class, enquiryDTO.getId());
    UnitTestUtil.simpleCompareSame(enquiryDTO, enquiry, EnquiryDTO.class, Enquiry.class);
    Assert.assertNotNull(enquiry.getCreateTime());
    Assert.assertNotNull(enquiry.getLastUpdateTime());

    for (EnquiryTargetShopDTO enquiryTargetShopDTO : enquiryDTO.getEnquiryTargetShops()) {
      EnquiryTargetShop enquiryTargetShop = txnWriter.getById(EnquiryTargetShop.class, enquiryTargetShopDTO.getId());
      UnitTestUtil.simpleCompareSame(enquiryTargetShopDTO, enquiryTargetShop, EnquiryTargetShopDTO.class, EnquiryTargetShop.class);
    }
    for (AppUserImageDTO appUserImageDTO : enquiryDTO.getEnquiryImages()) {
      DataImageRelation dataImageRelation = configWriter.getById(DataImageRelation.class, appUserImageDTO.getImageId());
      Assert.assertEquals(enquiryDTO.getAppUserNo(), dataImageRelation.getAppUserNo());
      Assert.assertEquals(enquiryDTO.getId(), dataImageRelation.getDataId());
      Assert.assertEquals(DataType.APP_ENQUIRY, dataImageRelation.getDataType());
      Assert.assertEquals(appUserImageDTO.getSequence(), dataImageRelation.getImageSequence());
      Assert.assertEquals(ObjectStatus.ENABLED, dataImageRelation.getStatus());
      ImageInfo imageInfo = configWriter.getById(ImageInfo.class, dataImageRelation.getImageId());
      Assert.assertEquals(ObjectStatus.ENABLED, imageInfo.getStatus());
      Assert.assertEquals(enquiryDTO.getAppUserNo(), imageInfo.getAppUserNo());
      Assert.assertEquals(appUserImageDTO.getImagePath(), imageInfo.getPath());
    }

    for (EnquiryTargetShopDTO enquiryTargetShopDTO : enquiryLastSavedDTO.getEnquiryTargetShops()) {
      EnquiryTargetShop enquiryTargetShop = txnWriter.getById(EnquiryTargetShop.class, enquiryTargetShopDTO.getId());
      Assert.assertEquals(shopDTO.getId(), enquiryTargetShop.getTargetShopId());
      Assert.assertEquals(shopDTO.getName(), enquiryTargetShop.getTargetShopName());
      Assert.assertEquals(EnquiryTargetShopStatus.DISABLED, enquiryTargetShop.getStatus());
    }
  }

  public void sendEnquiryTest() throws Exception {
      ShopDTO shopDTO = createShop(null);
      EnquiryDTO enquiryDTO = createSavedEnquiry(shopDTO);
      EnquiryDTO enquiryLastSavedDTO = BcgogoBeanUtils.deepCopyBean(enquiryDTO);
      enquiryDTO.setDescription(StringUtil.getCharacterNumberOrChinese(ValidateConstant.ENQUIRY_DESCRIPTION_MAX_LENGTH));
      ShopDTO shopDTO2 = createShop(null);
      EnquiryTargetShopDTO[] enquiryTargetShopDTOs = new EnquiryTargetShopDTO[1];
      for (int i = 0; i < enquiryTargetShopDTOs.length; i++) {
        EnquiryTargetShopDTO enquiryTargetShopDTO = new EnquiryTargetShopDTO();
        enquiryTargetShopDTO.setTargetShopId(shopDTO2.getId());
        enquiryTargetShopDTO.setTargetShopName(shopDTO2.getName());
        enquiryTargetShopDTO.setStatus(EnquiryTargetShopStatus.SENT);
        enquiryTargetShopDTOs[i] = enquiryTargetShopDTO;
      }
      enquiryDTO.setEnquiryTargetShops(enquiryTargetShopDTOs);
      AppUserImageDTO[] enquiryImages = new AppUserImageDTO[2];
      AppUserImageDTO appUserImageDTO1 = new AppUserImageDTO();
      appUserImageDTO1.setImagePath(IMAGE_PATH2);
      enquiryImages[0] = appUserImageDTO1;
      AppUserImageDTO appUserImageDTO2 = new AppUserImageDTO();
      appUserImageDTO2.setImagePath(IMAGE_PATH1);
      enquiryImages[1] = appUserImageDTO2;
      enquiryDTO.setEnquiryImages(enquiryImages);

      enquiryDTO.setAppUserName(StringUtil.getCharacterNumberOrChinese(ValidateConstant.ENQUIRY_APP_USER_NAME_MAX_LENGTH));
      enquiryDTO.setAppUserMobile("18001557668");
      enquiryDTO.setVehicleNo("苏E552UW");
      enquiryDTO.setBillId(1234567890123456790L);
      EnquiryController enquiryController = new EnquiryController();
      ApiResponse apiResponse = enquiryController.sendEnquiry(request, response, enquiryDTO);
      Assert.assertEquals(apiResponse.getMessage(), MessageCode.ENQUIRY_SEND_SUCCESS.getCode(), apiResponse.getMsgCode());
      TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
      TxnWriter txnWriter = txnDaoManager.getWriter();
      ConfigDaoManager configDaoManager = ServiceManager.getService(ConfigDaoManager.class);
      ConfigWriter configWriter = configDaoManager.getWriter();
      Enquiry enquiry = txnWriter.getById(Enquiry.class, enquiryDTO.getId());
      UnitTestUtil.simpleCompareSame(enquiryDTO, enquiry, EnquiryDTO.class, Enquiry.class);
      Assert.assertNotNull(enquiry.getCreateTime());
      Assert.assertNotNull(enquiry.getLastUpdateTime());

      for (EnquiryTargetShopDTO enquiryTargetShopDTO : enquiryDTO.getEnquiryTargetShops()) {
        EnquiryTargetShop enquiryTargetShop = txnWriter.getById(EnquiryTargetShop.class, enquiryTargetShopDTO.getId());
        UnitTestUtil.simpleCompareSame(enquiryTargetShopDTO, enquiryTargetShop, EnquiryTargetShopDTO.class, EnquiryTargetShop.class);
        Assert.assertTrue(StringUtil.isNotEmpty(enquiryTargetShop.getReceiptNo()));
      }
      for (AppUserImageDTO appUserImageDTO : enquiryDTO.getEnquiryImages()) {
        DataImageRelation dataImageRelation = configWriter.getById(DataImageRelation.class, appUserImageDTO.getImageId());
        Assert.assertEquals(enquiryDTO.getAppUserNo(), dataImageRelation.getAppUserNo());
        Assert.assertEquals(enquiryDTO.getId(), dataImageRelation.getDataId());
        Assert.assertEquals(DataType.APP_ENQUIRY, dataImageRelation.getDataType());
        Assert.assertEquals(appUserImageDTO.getSequence(), dataImageRelation.getImageSequence());
        Assert.assertEquals(ObjectStatus.ENABLED, dataImageRelation.getStatus());
        ImageInfo imageInfo = configWriter.getById(ImageInfo.class, dataImageRelation.getImageId());
        Assert.assertEquals(ObjectStatus.ENABLED, imageInfo.getStatus());
        Assert.assertEquals(enquiryDTO.getAppUserNo(), imageInfo.getAppUserNo());
        Assert.assertEquals(appUserImageDTO.getImagePath(), imageInfo.getPath());
      }

      for (EnquiryTargetShopDTO enquiryTargetShopDTO : enquiryLastSavedDTO.getEnquiryTargetShops()) {
        EnquiryTargetShop enquiryTargetShop = txnWriter.getById(EnquiryTargetShop.class, enquiryTargetShopDTO.getId());
        Assert.assertEquals(shopDTO.getId(), enquiryTargetShop.getTargetShopId());
        Assert.assertEquals(shopDTO.getName(), enquiryTargetShop.getTargetShopName());
        Assert.assertEquals(EnquiryTargetShopStatus.DISABLED, enquiryTargetShop.getStatus());
      }
    }

}
