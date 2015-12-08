package com.bcgogo.user.service;

import com.bcgogo.api.ImpactDetailDTO;
import com.bcgogo.etl.ImpactDTO;
import com.bcgogo.etl.ImpactVideoExpDTO;
import com.bcgogo.txn.dto.pushMessage.impact.ImpactInfoSearchConditionDTO;
import com.bcgogo.user.ImpactAndVideoDTO;
import com.bcgogo.user.ImpactVideoDTO;
import com.bcgogo.user.model.ImpactVideo;
import com.bcgogo.user.model.app.OBD;
import com.bcgogo.user.model.app.ObdUserVehicle;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-3-31
 * Time: 17:50
 */
public interface IImpactService {


  void saveOrUpdateImpactVideo(ImpactVideoDTO videoDTO);

  List<ImpactVideoDTO> getImpactVideoDTOByAppUserNo(String appUserNo);

  int statImpactVideo(String appUserNo,Long startTime,Long endTime);

  ImpactVideoDTO getImpactVideoDTOById(Long impactVideoId);

  ImpactVideoDTO getImpactVideoDTOByUUID(String uuid);

  ImpactVideo getImpactVideoById(Long impactVideoId);

  void saveOrUpdateImpact(ImpactDTO impactDTO) ;

  void saveImpactCollectData(ImpactDTO impactDTO);

  ImpactDTO getImpactDTOByUUID(String uuid);

  ObdUserVehicle getObdUserVehicle(String appUserNo);

  OBD getObdById(Long id);

  List<ImpactVideoExpDTO> getImpactVideoExpDTOByAppUserNo(String appUserNo);

  String getImpactVideoUrl(Long impactVideoId);

  void deleImpactVideoExpDTOByAppUserNo(String impactVideoId);

  List<ImpactVideoExpDTO> getImpactVideoExpDTOByAppUserNo_page(String shopId,ImpactInfoSearchConditionDTO impactInfoSearchConditionDTO);

  int countGetImpactVideoExpDTOs(String appUserNo);

  int countGetImpactVideoExpDTOs_page(String shopId,ImpactInfoSearchConditionDTO impactInfoSearchConditionDTO);

  void deleteShopImpactInfo(Long... ids);

      //获取碰撞详情
  List<ImpactDetailDTO> getImpact_detail(Long shopId,String impactId,long uploadTime);

  ImpactDetailDTO getImpact_detailByIdAndTime(Long shopId, String impactId, long uploadTime);

  List<ImpactAndVideoDTO> getImpactAndVideo ( long vehicleId , long datetime , int count );

  void saveOneImpact ( String appUserNo );
}
