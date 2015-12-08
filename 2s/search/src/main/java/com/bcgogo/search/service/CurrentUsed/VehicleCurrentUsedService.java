package com.bcgogo.search.service.CurrentUsed;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.search.dto.CurrentUsedVehicleDTO;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.model.CurrentUsedVehicle;
import com.bcgogo.search.model.SearchDaoManager;
import com.bcgogo.search.model.SearchWriter;
import com.bcgogo.search.service.SearchService;
import com.bcgogo.search.util.SolrQueryUtils;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.BcgogoOrderDto;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.SearchConstant;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-5-29
 * Time: 上午9:27
 * 常用车辆的service
 * 负责保存更新 车辆品牌
 */

@Component
public class VehicleCurrentUsedService implements IVehicleCurrentUsedService {
  private static final Logger LOG = LoggerFactory.getLogger(SearchService.class);

  //从memcache中取常用数据，如果不存在，先从数据库查找，然后放入memcache中
  @Override
  public List<CurrentUsedVehicle> getCurrentUsedVehiclesFromMemory(SearchConditionDTO searchConditionDTO) {
    searchConditionDTO.setSearchField("vehicle_" + searchConditionDTO.getSearchField());
    List<CurrentUsedVehicle> currentUsedVehicleList = (List<CurrentUsedVehicle>) MemCacheAdapter.get(getMemCacheKey(searchConditionDTO));
    if (LOG.isDebugEnabled()) {
      if (CollectionUtils.isNotEmpty(currentUsedVehicleList)) {
        for (CurrentUsedVehicle cuv : currentUsedVehicleList) {
          LOG.debug("memcache currentUsedVehicle is " + cuv.getBrand());
        }
      } else {
        LOG.debug("memcache currentUsedVehicle is null .");
      }
    }
    if (CollectionUtils.isEmpty(currentUsedVehicleList)) {
      SearchWriter writer = getSearchDaoManager().getWriter();
      currentUsedVehicleList = writer.getCurrentUsedVehicle(searchConditionDTO);
      MemCacheAdapter.add(getMemCacheKey(searchConditionDTO), currentUsedVehicleList);
    }
    String keyWord=searchConditionDTO.getSearchWord();
    if(StringUtil.isEmpty(keyWord)){
      return currentUsedVehicleList;
    }
    List<CurrentUsedVehicle> vehicles=new ArrayList<CurrentUsedVehicle>();
    if(CollectionUtil.isNotEmpty(currentUsedVehicleList)){
      for(CurrentUsedVehicle vehicle:currentUsedVehicleList){
          if(StringUtil.isNotEmpty(vehicle.getBrand())&&vehicle.getBrand().contains(keyWord)){
            vehicles.add(vehicle);
          }
      }
    }
    return vehicles;
  }

  /**
   * 品牌下拉框更新，对每个单据通过多线程处理
   *
   * @param bcgogoOrderDto 每个单据的dto
   */
  public void currentUsedVehicleSaved(BcgogoOrderDto bcgogoOrderDto) {
    if (CollectionUtils.isEmpty(bcgogoOrderDto.getCurrentUsedVehicleDTOList())) return;
    //更新常用车辆数据库数据
    this.currentUsedVehicleDBSaved(bcgogoOrderDto);
    //更新常用车辆内存数据
    this.currentUsedVehicleMemorySaved(bcgogoOrderDto);
  }

  //更新数据库CurrentUsedVehicle
  public void currentUsedVehicleDBSaved(BcgogoOrderDto bcgogoOrderDto) {
    if (CollectionUtils.isEmpty(bcgogoOrderDto.getCurrentUsedVehicleDTOList())) return;
    if (LOG.isDebugEnabled())
      LOG.debug(bcgogoOrderDto.getShopId() + " DBSaved:" + bcgogoOrderDto.getCurrentUsedVehicleDTOList().toString());
    SearchWriter writer = getSearchDaoManager().getWriter();
    SearchConditionDTO searchConditionDTO = new SearchConditionDTO();
    searchConditionDTO.setShopId(bcgogoOrderDto.getShopId());
    Object status = writer.begin();
    try {
      //更新品牌
//      searchConditionDTO.setSearchField("vehicle_" + SearchConstant.VEHICLE_BRAND);
      List<CurrentUsedVehicleDTO> currentUsedVehicleDTOListByBrand = getCurrentUsedVehicleDTOListByBrand(bcgogoOrderDto);
      if (CollectionUtils.isNotEmpty(currentUsedVehicleDTOListByBrand)) {
        List<CurrentUsedVehicle> currentUsedVehicleListByBrand = writer.getCurrentUsedVehicle(searchConditionDTO);
        this.updateCurrentUsedVehicleDTOsImpl(writer, currentUsedVehicleDTOListByBrand, currentUsedVehicleListByBrand);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  //更新memcached
  public void currentUsedVehicleMemorySaved(BcgogoOrderDto bcgogoOrderDto) {
    if (CollectionUtils.isEmpty(bcgogoOrderDto.getCurrentUsedVehicleDTOList())) return;
    if (LOG.isDebugEnabled())
      LOG.debug(bcgogoOrderDto.getShopId() + " MemorySaved:" + bcgogoOrderDto.getCurrentUsedVehicleDTOList().toString());
    SearchConditionDTO searchConditionDTO = new SearchConditionDTO();
    searchConditionDTO.setShopId(bcgogoOrderDto.getShopId());
    searchConditionDTO.setSearchField(SearchConstant.VEHICLE_BRAND);          //todo brand 与   vehicle_brand 问题 临时解决
    List<CurrentUsedVehicleDTO> currentUsedVehicleDTOListByBrand = getCurrentUsedVehicleDTOListByBrand(bcgogoOrderDto);
    if (CollectionUtils.isNotEmpty(currentUsedVehicleDTOListByBrand)) {
      List<CurrentUsedVehicle> currentUsedVehicleListByBrand = this.getCurrentUsedVehiclesFromMemory(searchConditionDTO);
      currentUsedVehicleListByBrand = this.updateCurrentUsedVehicleDTOsImpl(null, currentUsedVehicleDTOListByBrand, currentUsedVehicleListByBrand);
      MemCacheAdapter.set(getMemCacheKey(searchConditionDTO), currentUsedVehicleListByBrand);
    }
  }

  private List<CurrentUsedVehicle> updateCurrentUsedVehicleDTOsImpl(SearchWriter writer, List<CurrentUsedVehicleDTO> currentUsedVehicleDTOList, List<CurrentUsedVehicle> currentUsedVehicleList) {
    boolean flag = false;
    int selectOptionNum = SolrQueryUtils.getSelectOptionNumber();
    //list中存在此product 更新此product order
    for (CurrentUsedVehicleDTO currentUsedVehicleDTO : currentUsedVehicleDTOList) {
      for (CurrentUsedVehicle currentUsedVehicle : currentUsedVehicleList) {
        if (currentUsedVehicle.equals(currentUsedVehicleDTO)) {
          if (writer != null)
            writer.update(currentUsedVehicle.fromDTO(currentUsedVehicleDTO));
          else
            currentUsedVehicle.fromDTO(currentUsedVehicleDTO);
          flag = true;
          break;
        }
      }
      if (flag) {
        flag = false;
        continue;
      }
      //判断list中有没有此product
      if (currentUsedVehicleList.size() < selectOptionNum) {
        CurrentUsedVehicle currentUsedVehicle = new CurrentUsedVehicle(currentUsedVehicleDTO);
        if (writer != null)
          writer.save(currentUsedVehicle);
        currentUsedVehicleList.add(currentUsedVehicle);
        continue;
      }
      //其他情况 更新掉最后一个
      //根据时间排序
      currentUsedVehicleList = this.sortList(currentUsedVehicleList);
      //判断list中存在此product 根据使用时间更新一条数据
      if (writer != null) {
        writer.update(currentUsedVehicleList.get(selectOptionNum - 1).fromDTO(currentUsedVehicleDTO));
      } else {
        //判断list中存在此product 根据使用时间更新一条数据
        currentUsedVehicleList.remove(selectOptionNum - 1);
        currentUsedVehicleList.add(new CurrentUsedVehicle(currentUsedVehicleDTO));
      }
    }
    if (writer == null)
      return this.sortList(currentUsedVehicleList);
    else
      return null;
  }

  private List<CurrentUsedVehicle> sortList(List<CurrentUsedVehicle> currentUsedVehicleList) {
    Collections.sort(currentUsedVehicleList, new Comparator<CurrentUsedVehicle>() {
      @Override
      public int compare(CurrentUsedVehicle pov1, CurrentUsedVehicle pov2) {
        if (pov1.getTimeOrder() == null) {
          return 1;
        }
        if (pov2.getTimeOrder() == null) {
          return -1;
        }
        try {                          //02<01 ?-1:1  从大到小顺序
          return pov2.getTimeOrder().compareTo(pov1.getTimeOrder());
        } catch (Exception e) {
          if (pov2.getTimeOrder() == null || pov1.getTimeOrder() == null) {
            LOG.error("db-shop: " + pov1.getShopId() + " orderTime id is null!", e);
          }
          return -1;
        }
      }
    });
    return currentUsedVehicleList;
  }

  public List<CurrentUsedVehicleDTO> getCurrentUsedVehicleDTOListByBrand(BcgogoOrderDto bcgogoOrderDto) {
    Set<String> vehicleSet = new HashSet<String>();
    List<CurrentUsedVehicleDTO> currentUsedVehicleDTOListByBrand = new ArrayList<CurrentUsedVehicleDTO>();
    for (CurrentUsedVehicleDTO currentUsedVehicleDTO : bcgogoOrderDto.getCurrentUsedVehicleDTOList()) {
      if (StringUtils.isBlank(currentUsedVehicleDTO.getBrand()))
        continue;
      if (!vehicleSet.add(currentUsedVehicleDTO.getBrand())) continue;
      CurrentUsedVehicleDTO cuvDTO = new CurrentUsedVehicleDTO();
      cuvDTO.setShopId(currentUsedVehicleDTO.getShopId());
      cuvDTO.setBrand(currentUsedVehicleDTO.getBrand());
      cuvDTO.setTimeOrder(System.currentTimeMillis());
      currentUsedVehicleDTOListByBrand.add(cuvDTO);
      if (currentUsedVehicleDTOListByBrand.size() == 5) break;
    }
    return currentUsedVehicleDTOListByBrand;
  }

  //memcache的KEY
  public String getMemCacheKey(SearchConditionDTO searchConditionDTO) {
    return MemcachePrefix.currentUsed.getValue() + searchConditionDTO.getSearchField() + "_" + String.valueOf(searchConditionDTO.getShopId());
  }


  @Autowired
  private SearchDaoManager searchDaoManager;


  public SearchDaoManager getSearchDaoManager() {
    return searchDaoManager;
  }

  private IConfigService configService;

  private IConfigService getConfigService() {
    if (configService == null) {
      configService = ServiceManager.getService(IConfigService.class);
    }
    return configService;
  }


}
