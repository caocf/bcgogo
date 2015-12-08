package com.bcgogo.txn.service.app;

import com.bcgogo.api.EnquiryDTO;
import com.bcgogo.api.EnquiryShopResponseDTO;
import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.enums.app.EnquiryStatus;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.txn.dto.enquiry.EnquirySearchConditionDTO;
import com.bcgogo.txn.dto.enquiry.ShopEnquiryDTO;

import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-10-24
 * Time: 下午2:40
 */
public interface IEnquiryService {

  /**
   * 处理保存预约单
   * @param enquiryDTO
   * @param imageScenes
   * @return
   * @throws Exception
   */
  EnquiryDTO handleSaveEnquiry(EnquiryDTO enquiryDTO, List<ImageScene> imageScenes)throws Exception;

  /**
   * 处理更新预约单
   * @param enquiryDTO
   * @param imageScenes
   * @return
   * @throws Exception
   */
  EnquiryDTO handleUpdateEnquiry(EnquiryDTO enquiryDTO, List<ImageScene> imageScenes)throws Exception;

  /**
   * 处理发送询价单
   * @param enquiryDTO
   * @param imageScenes
   * @return
   * @throws Exception
   */
  EnquiryDTO handleSendEnquiry(EnquiryDTO enquiryDTO, List<ImageScene> imageScenes)throws Exception;


  /**
   * 只查出enquiry 不包含其他关联的数据
   * @param id
   * @param appUserNo
   * @return
   */
  EnquiryDTO getSimpleEnquiryDTO(Long id, String appUserNo);

  List<EnquiryDTO> getEnquiryListByUserNoAndStatus(String appUserNo, Set<EnquiryStatus> enquiryStatuses,
                                                   List<ImageScene> shopImageScenes, Pager pager);

  int countEnquiryListByUserNoAndStatus(String appUserNo, Set<EnquiryStatus> enquiryStatuses);

  EnquiryDTO getEnquiryDTODetail(Long enquiryId, String appUserNo, List<ImageScene> commonAppUserImageScenes);

  void handleDeleteEnquiry(String appUserNo, Long enquiryId) throws Exception;

  //web店铺询价单查询统计
  int countShopEnquiryDTOs(EnquirySearchConditionDTO searchCondition);

  //web店铺询价单查询
  List<ShopEnquiryDTO> searchShopEnquiryDTOs(EnquirySearchConditionDTO searchCondition);

  //web 店铺询价单详情
  ShopEnquiryDTO getShopEnquiryDTODetail(Long enquiryOrderId, Long shopId);

  //web 店铺询价简单信息
  ShopEnquiryDTO getSimpleShopEnquiryDTO(Long enquiryOrderId, Long shopId);

  //店铺回复询价单校验
  Result validateAddResponse(ShopEnquiryDTO shopEnquiryDTO,EnquiryShopResponseDTO enquiryShopResponseDTO);

  //保存店铺回复
  void handelAddEnquiryShopResponse(ShopEnquiryDTO shopEnquiryDTO, EnquiryShopResponseDTO enquiryShopResponseDTO) throws Exception;
}
