package com.bcgogo.user.service.app;

import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.api.ShopBindingDTO;
import com.bcgogo.api.ShopBindingInfo;
import com.bcgogo.common.Result;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.enums.app.ObdUserVehicleStatus;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserReader;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.app.AppUserShopVehicle;
import com.bcgogo.utils.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * User: ZhangJuntao
 * Date: 13-12-10
 * Time: 下午5:38
 */
@Component
public class AppUserShopBindingService implements IAppUserShopBindingService {
  private static final Logger LOG = LoggerFactory.getLogger(AppUserShopBindingService.class);
  @Autowired
  private UserDaoManager userDaoManager;

  @Override
  public Map<Long, Long> getVehicleIdBindingShopIdMap(String userNo, Long... vehicleId) {
    UserReader reader = userDaoManager.getReader();
    return reader.getVehicleIdBindingShopIdMap(userNo, vehicleId);
  }

  @Override
  public Long getBindingShopId(String userNo, Long vehicleId) {
    return getVehicleIdBindingShopIdMap(userNo, vehicleId).get(vehicleId);
  }

  @Override
  public Set<Long> getBindingShopIds(String userNo) {
    UserReader reader = userDaoManager.getReader();
    return reader.getAppUserBindingShopIds(userNo);
  }

  @Override
  public List<ShopBindingInfo> getBindingShop(String userNo) {
    List<ShopBindingInfo> shopBindingInfoList = new ArrayList<ShopBindingInfo>();
    List<AppUserShopVehicle> list = userDaoManager.getReader().getAppUserShopVehicleByUserNo(userNo);
    Set<Long> shopIds = new HashSet<Long>();
    Set<Long> vehicleIds = new HashSet<Long>();
    for (AppUserShopVehicle ausv : list) {
      if (ausv.getShopId() != null) shopIds.add(ausv.getShopId());
      if (ausv.getAppVehicleId() != null) vehicleIds.add(ausv.getAppVehicleId());
    }
    Map<Long, ShopDTO> shopDTOMap = ServiceManager.getService(IShopService.class).getShopByShopIds(shopIds.toArray(new Long[shopIds.size()]));
    Map<Long, AppVehicleDTO> appVehicleDTOMap = ServiceManager.getService(IAppUserVehicleObdService.class).getAppVehicleIdMapByAppUserNo(userNo);
    ShopBindingInfo info;
    for (AppUserShopVehicle ausv : list) {
      info = new ShopBindingInfo();
      info.setVehicleId(ausv.getAppVehicleId());
      info.setShopId(ausv.getShopId());
      if (info.getShopId() != null) {
        ShopDTO shopDTO = shopDTOMap.get(info.getShopId());
        if (shopDTO != null) info.setShopName(shopDTO.getName());
      }
      if (info.getVehicleId() != null) {
        AppVehicleDTO appVehicleDTO = appVehicleDTOMap.get(info.getVehicleId());
        if (appVehicleDTO != null) info.setVehicleNo(appVehicleDTO.getVehicleNo());
      }
      shopBindingInfoList.add(info);
    }
    return shopBindingInfoList;
  }

  @Override
  public Result binding(ShopBindingDTO bindingDTO, UserWriter writer) {
    String vResult = bindingDTO.validate();
    if (!bindingDTO.isSuccess(vResult)) {
      return new Result(vResult, false);
    }
    AppUserShopVehicle usv = getAppUserShopVehicle(bindingDTO.getAppUserNo(), bindingDTO.getVehicleId(), bindingDTO.getOrgShopId(), writer);
    if (usv == null) {
      usv = new AppUserShopVehicle(bindingDTO);
      writer.save(usv);
    } else {
      usv.setShopId(bindingDTO.getShopId());
      usv.setObdId(bindingDTO.getObdId());
      usv.setStatus(ObdUserVehicleStatus.BUNDLING);
      writer.update(usv);
    }

    return new Result(true);
  }

  @Override
  public Result binding(ShopBindingDTO bindingDTO) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Result result = binding(bindingDTO, writer);
      writer.commit(status);
      return result;
    } finally {
      writer.rollback(status);
    }
  }


  @Override
  public void unbinding(String appUserNo, Long vehicleId, Long shopId) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      unbinding(appUserNo, vehicleId, shopId, writer);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void unbinding(String appUserNo, Long vehicleId, UserWriter writer) {
    List<AppUserShopVehicle> usvLst = writer.getAppUserShopVehicle(appUserNo, vehicleId, null);
    for (AppUserShopVehicle usv : usvLst) {
      usv.setStatus(ObdUserVehicleStatus.BUNDLING);
      writer.update(usv);
    }
  }

  @Override
  public void unbinding(String appUserNo, Long vehicleId, Long shopId, UserWriter writer) {
    AppUserShopVehicle usv = getAppUserShopVehicle(appUserNo, vehicleId, shopId, writer);
    if (usv != null) {
      usv.setStatus(ObdUserVehicleStatus.BUNDLING);
      writer.update(usv);
    }
  }

  @Override
  public Map<Long, Long> getShopBindingVehicleIdShopIdMap(String appUserNo, Set<Long> vehicleIdSet) {
    List<AppUserShopVehicle> usvLst = userDaoManager.getWriter().getAppUserShopVehicle(appUserNo, null, null);
    Map<Long, Long> map = new HashMap<Long, Long>();
    for (AppUserShopVehicle entity : usvLst) {
      if (vehicleIdSet.contains(entity.getAppVehicleId())) {
        map.put(entity.getAppVehicleId(), entity.getShopId());
      }
    }
    return map;
  }

  private AppUserShopVehicle getAppUserShopVehicle(String appUserNo, Long vehicleId, Long shopId, UserWriter writer) {
    List<AppUserShopVehicle> usvLst = writer.getAppUserShopVehicle(appUserNo, vehicleId, shopId);
    if (CollectionUtil.isNotEmpty(usvLst)) {
      if (usvLst.size() > 1) {
        LOG.error("shop binding appUserNo:{},vehicleId:{},shopId:{} not unique.", new Object[]{appUserNo, vehicleId, shopId});
      }
    }
    return CollectionUtil.getFirst(usvLst);
  }

}
