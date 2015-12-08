package com.bcgogo.user.service.wx;


import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.WXFanDTO;
import com.bcgogo.api.response.ApiGsmUserQRResponse;
import com.bcgogo.api.response.ApiResultResponse;
import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.config.dto.juhe.VehicleViolateRegulationRecordDTO;
import com.bcgogo.notification.model.WXMsg;
import com.bcgogo.notification.model.WXMsgReceiver;
import com.bcgogo.notification.model.WXSendStatusReport;
import com.bcgogo.search.dto.OrderSearchConditionDTO;
import com.bcgogo.search.dto.OrderSearchResultDTO;
import com.bcgogo.txn.dto.RepairOrderDTO;
import com.bcgogo.txn.dto.SalesOrderDTO;
import com.bcgogo.txn.dto.WashBeautyOrderDTO;
import com.bcgogo.user.model.wx.*;
import com.bcgogo.wx.ErrCode;
import com.bcgogo.wx.WXAccountType;
import com.bcgogo.wx.WXArticleDTO;
import com.bcgogo.wx.WXJsApiTicketSign;
import com.bcgogo.wx.qr.QRScene;
import com.bcgogo.wx.qr.WXQRCodeSearchCondition;
import com.bcgogo.wx.user.*;
import com.bcgogo.wx.qr.WXQRCodeDTO;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-8-13
 * Time: 上午10:17
 * To change this template use File | Settings | File Templates.
 */
public interface IWXUserService {

  List<WXQRCodeDTO> createLimitQRCode(String publicNo, Long shopId, QRScene scene) throws Exception;

  WXQRCodeDTO createTempQRCode(String publicNo, Long shopId, QRScene scene) throws Exception;

  WXQRCodeDTO getUnExpireWXQRCodeDTO(String publicNo,String appUserNo);

  WXQRCodeDTO getUnExpireWXQRCodeDTOByShopId(Long shopId,QRScene scene);

  AppUserWXQRCodeDTO getAppUserWXQRCodeDTOByAppUserNo(String appUserNo);

  AppUserWXQRCodeDTO getAppUserWXQRCodeDTO(String publicNo,String appUserNo);

  AppUserWXQRCodeDTO getAppUserWXQRCodeDTOBySceneId(Long sceneId);

  List<WXQRCodeDTO> batchCreateLimitQRCode(String publicNo, Long shopId, int maxScene, QRScene scene) throws Exception;

  WXQRCodeDTO getWXQRCodeDTOBySceneId(String publicNo, Long sceneId);

  List<WXQRCodeDTO> getWXQRCodeDTO(WXQRCodeSearchCondition condition);

  void saveOrUpdateAppUserWXQRCodeDTO(AppUserWXQRCodeDTO codeDTO);

  void saveOrUpdateWXQRCodeDTOs(WXQRCodeDTO... codeDTOs);

  WXQRCodeDTO getWXQRCodeDTOByShopId(String publicNo, Long shopId, QRScene scene) throws Exception;

  WXQRCodeDTO getUnAssignedWXQRCode(String publicNo, Long shopId) throws Exception;

  boolean buildWXImageLib(String filePath) throws Exception;

  String getVehicleBindRemindMsgXml(String publicNo, String openId) throws IOException;

  List<WXArticleDTO> getHistoryBillArticleDTO(String openId) throws Exception;

  String getHistoryBill(String fromUserName, String toUserName) throws Exception;

  String getVehicleBindRemindMsg(String publicNo, String openId) throws IOException;

  String getMemberCardNews(String publicNo, String openId) throws Exception;

  List<OrderSearchResultDTO> getWOrdersByConditionDTO(OrderSearchConditionDTO conditionDTO) throws Exception;

  String getVehicleNews(String publicNo, String openId) throws Exception;

//  String queryVehicleViolateRegulationNews(String publicNo,String openId) throws Exception;

  String getVRegulationMsg(String openId) throws Exception;

  void sendMirrorVRegulationTemplateMsg(List<VehicleViolateRegulationRecordDTO> recordDTOs) throws Exception;

  void sendVRegulationTemplateMsg(List<VehicleViolateRegulationRecordDTO> recordDTOs) throws Exception;

  WXSubscribeRecord getWXSubscribeRecordById(Long id);

  void saveOrUpdateWXSubscribeRecord(WXSubscribeRecordDTO recordDTO);

//  WXKWTemplateDTO getWXKWTemplate(String publicNo, String title) throws Exception;

  WXKWTemplateDTO getCachedWXKWTemplate(String publicNo, String title) throws Exception;

  Result sendShopMassNewsMsg(Long shopId, WXArticleDTO articleDTO) throws Exception;

  Result saveMassNewsMsg(String publicNo, WXArticleDTO articleDTO);

  //保存并发送
  Result saveAndSendCustomNewsMsg(WXArticleDTO articleDTO) throws Exception;

  void saveArticle(WXArticleDTO articleDTO) throws Exception;

  void uploadShopArticleImg(WXArticleDTO articleDTO) throws Exception;

  String getShopWelcomeWord(Long shopId, String openId);

  WXJsApiTicketSign getWXJsApiTicketSign(String publicNo,String url) throws Exception;



  List<WXUserVehicleDTO> getWXUserVehicleByOpenId(String... openId) throws Exception;

  public Map<String, List<WXUserVehicleDTO>> getWXUserVehicleMapByOpenIds(Set<String> openIds) throws Exception;

  List<WXUserVehicleDTO> getWXUserVehicleByVehicleNo(String vehicleNo) throws Exception;

  List<WXUserVehicleDTO> getWXUserVehicle(String openId, String vehicleNo) throws Exception;

  boolean synALLWXUserDTOs() throws Exception;

  void synUserDTOList(String publicNo) throws Exception;

  CreateWXGroupResult createGroup(Long shopId, String name) throws Exception;

  ErrCode addUserToGroup(Long shopId, String groupId, String openId) throws Exception;

  List<WXUserDTO> getPlatFormWXUserList(String publicNo) throws Exception;

  WXUserDTO getWXUserFromPlat(String publicNo, String openId);

  Result saveShopWXUser(String toUserName, String fromUserName, Long shopId);

  List<AppWXUserDTO> getAppWXUserDTO(String appUserNo, String openId);

  List<AppWXUserDTO> getAppWXUserDTOByOpenId(String openId);

  List<AppWXUserDTO> getAppWXUserDTOByAppUserNo(String appUserNo);

  Result doUnBindVehicle(String openId, String vehicleNo) throws Exception;

//  Result saveOrUpdateWXUserVehicle(String openId,String vehicleNo,String vin,String engineNo,String juheCityCode) throws Exception;

  List<ShopWXUserDTO> getShopWXUserByOpenId(String openId);

  List<ShopWXUserDTO> getShopWXUserByShopId(Long shopId);

  int countWXUser(WXAccountType accountType);

  void encryptAccount(String publicNo, String appSecret) throws Exception;

  List<WXUserDTO> getWXUserDTOByOpenIds(String... openIds);

  WXUserDTO getWXUserDTOByOpenId(String openId);

  WXUserDTO getWXUserDTOById(Long id);

  boolean isUserExist(String openId);

  List<WXUserDTO> getWXUserDTOByPager(WXAccountType accountType,Pager pager);

  List<WXUserDTO> getWXUserDTOByVehicleNo(String vehicleNo);

  List<WXUserDTO> getWXUserDTOByShopId(Long shopId);

  List<WXUserDTO> getWXUserDTOByPublicNo(String publicNo);

  Result saveOrUpdateWXUserVehicle(WXUserVehicleDTO dto);

  WXUserVehicleDTO getWXUserVehicleDTOById(Long userVehicleId);

  WXUserVehicle getWXUserVehicleById(Long userVehicleId);

  Long saveOrUpdateWXUser(WXUserDTO userDTO);

  void encryptAllAccount() throws Exception;

  ShopWXUserDTO getShopWXUserInfo(String openId, String publicNo);

  ShopWXUserDTO getShopWXUser(Long shopId, String openId);

  Long saveOrUpdateShopWXUser(ShopWXUserDTO userDTO);

  void saveOrUpdateAppWXUser(AppWXUserDTO appWXUserDTO);

  int countWxUserBySearchCondition(WXUserSearchCondition searchCondition);

  List<WXUserDTO> getWxUserDTOsBySearchCondition(WXUserSearchCondition searchCondition,Pager pager);

  int countMyFans(WXUserSearchCondition searchCondition);


  List<WXUserDTO> getMyFans(WXUserSearchCondition searchCondition, Pager pager);

  void generateVehicleInfo(Long shopId, List<WXUserDTO> wxUserDTOs) throws Exception;

  Map<String, WXFanDTO> getWxShopVehicleMapByVehicleNos(Long shopId, Set<String> vehicleNos);

}