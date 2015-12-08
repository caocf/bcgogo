package com.bcgogo.config.service.camera;


import com.bcgogo.camera.CameraConfigDTO;
import com.bcgogo.camera.CameraDTO;
import com.bcgogo.camera.CameraRecordDTO;
import com.bcgogo.camera.CameraSearchCondition;
import com.bcgogo.common.Pager;
import com.bcgogo.config.model.Camera;
import com.bcgogo.config.model.CameraRecord;
import com.bcgogo.config.model.CameraShop;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhangjie
 * Date: 14-12-24
 * Time: 上午10:17
 * To change this template use File | Settings | File Templates.
 */
public interface ICameraService {
  void saveOrUpdateCameraRef(CameraDTO cameraDTO);
  List<CameraDTO> getCameraDTOList(Pager pager,String shopId);
  int getCameraDTOListAccount(String shopId);
  void unBandShop(CameraDTO cameraDTO);
  List<CameraRecordDTO> getCameraRecordDTOList(Pager pager,String id);
  int getCameraRecordDTOListAccount(String id );
  Camera saveOrUpdateCameraVLPR(CameraDTO cameraDTO);
  Camera getCamera(Camera camera);
  Camera getCameraBySerialNo(String serialNo);
  void saveCameraRecordDTO(CameraRecordDTO cameraRecordDTO);
  String getShopNameByCameraId(Long camera_id);
  CameraRecord getCameraRecordByVehicle_no(String vehicle_no);
//  CameraConfig getCameraConfig(String name, Long shopId);
  CameraConfigDTO getCameraConfigByCameraId(String CameraId);
  void updateCameraConfig(CameraConfigDTO cameraConfigDTO);
  void updateCameraConfig_admin(CameraConfigDTO cameraConfigDTO);
  CameraShop getCameraShop(String id);
  List<CameraRecordDTO> getCameraRecordListByShopId(Pager pager,CameraSearchCondition condition);
  int getCameraRecordDTOListAccountByShopId(CameraSearchCondition condition);
  List<CameraConfigDTO> getCameraConfigByShopId(String shopId);
  CameraConfigDTO getCameraConfigBySerialNo(String cameraSerialNo);
  List<CameraSearchCondition> getVehicle_nos(CameraSearchCondition condition);
  int getCountCameraConfigByShopId(String shopId);
}