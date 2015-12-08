package com.bcgogo.config.cache;

import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.ShopAdAreaDTO;
import com.bcgogo.config.model.Area;
import com.bcgogo.config.model.ConfigDaoManager;
import com.bcgogo.config.model.ConfigReader;
import com.bcgogo.config.model.ConfigWriter;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 管理本地area缓存
 * Created by IntelliJ IDEA.
 * User: xzhu
 */
public class AreaCacheManager {
  private static final Logger LOG = LoggerFactory.getLogger(AreaCacheManager.class);

  private static Map<Long, AreaDTO> allAreaDTOMap = new HashMap<Long, AreaDTO>();

//  private static Long areaTreeSyncTime = null;
//  private static final AtomicBoolean _initialize = new AtomicBoolean(true);
//  private static Integer counter = 0;

//  static {
//    if (_initialize.compareAndSet(true, false)) {
//      areaTreeSyncTime = System.currentTimeMillis();
//      MemCacheAdapter.set(MemcachePrefix.areaRefreshFlag.getValue(), areaTreeSyncTime);
//    }
//  }

  /**
   * 根据cityCode获取juheCode
   * @param cityCode
   * @return
   */
  public synchronized static String getJuheCodeByCityCode(Long cityCode){
    if(cityCode==null||cityCode==-1) return null;
    AreaDTO areaDTO=AreaCacheManager.getAreaDTOByNo(cityCode);
    if(areaDTO==null) return null;
    if(StringUtil.isNotEmpty(areaDTO.getJuheCityCode())){
      return areaDTO.getJuheCityCode();
    }else {
      return getJuheCodeByCityCode(areaDTO.getParentNo());
    }
  }


  public synchronized static AreaDTO getAreaDTOByNo(Long no) {
    if (needRefresh()) {
      synAreaDTOTree();
    }
    return allAreaDTOMap.get(no);
  }

  private static boolean needRefresh() {
    return MapUtils.isEmpty(allAreaDTOMap);
//    Object memCacheAreaTreeSyncTime = MemCacheAdapter.get(MemcachePrefix.areaRefreshFlag.getValue());
//    if (memCacheAreaTreeSyncTime == null) {
//      memCacheAreaTreeSyncTime = System.currentTimeMillis();
//      MemCacheAdapter.set(MemcachePrefix.areaRefreshFlag.getValue(), memCacheAreaTreeSyncTime);
//      return true;
//    }
//    return MapUtils.isEmpty(allAreaDTOMap) || areaTreeSyncTime == null || Long.valueOf(String.valueOf(memCacheAreaTreeSyncTime)) - areaTreeSyncTime > 0;
  }

  public synchronized static List<AreaDTO> getAreaDTOListByNo(Long... noList) {
    List<AreaDTO> areaDTOs = new ArrayList<AreaDTO>();
    if (ArrayUtil.isEmpty(noList)) {
      return areaDTOs;
    }
    if (needRefresh()) {
      synAreaDTOTree();
    }
    for (Long no : noList) {
      if (no == null) {
        continue;
      }
      if (no == -1l) {
        AreaDTO areaDTO = new AreaDTO();
        areaDTO.setNo(-1l);
        areaDTO.setName("全国");
        areaDTOs.add(areaDTO);
        continue;
      }
      areaDTOs.add(allAreaDTOMap.get(no));
    }
    return areaDTOs;
  }

  public static List<AreaDTO> getChildAreaDTOListByParentNo(Long parentNo) {
    AreaDTO parentAreaDTO = getAreaDTOByNo(parentNo);
    if (parentAreaDTO != null) {
      return parentAreaDTO.getChildAreaDTOList();
    } else {
      return null;
    }
  }

  private static void synAreaDTOTree() {
    ConfigDaoManager configDaoManager = ServiceManager.getService(ConfigDaoManager.class);
    ConfigWriter writer = configDaoManager.getWriter();
    ConfigReader reader = configDaoManager.getReader();
    List<Area> allAreaList = writer.getAllAreaList();
    Set<String> juheSet = reader.getActiveJuheArea();
    AreaDTO dto;
    for (Area area : allAreaList) {
      dto = area.toDTO();
      dto.setJuheStatus(juheSet.contains(area.getJuheCityCode()));
      allAreaDTOMap.put(area.getNo(), dto);
    }
    AreaDTO rootAreaDTO = new AreaDTO();
    rootAreaDTO.setName("全国");
    rootAreaDTO.setLevel(0);
    rootAreaDTO.setLeaf(false);
    rootAreaDTO.setNo(1l);
    rootAreaDTO.setCityCode(1);
    if (MapUtils.isNotEmpty(allAreaDTOMap)) {
      constructTree(rootAreaDTO, 0);
    }
    allAreaDTOMap.put(1l, rootAreaDTO);
//    areaTreeSyncTime = System.currentTimeMillis();
//    counter++;
//    if (counter > 1000) {
//      LOG.error("AreaCacheManager counter is {},synAreaDTOTree too much.", counter);
//    }
  }

  private static void constructTree(AreaDTO parentAreaDTO, int rootLevel) {
    List<AreaDTO> childAreaDTOList = new ArrayList<AreaDTO>();
    //构造根节点
    for (AreaDTO areaDTO : allAreaDTOMap.values()) {
      if (NumberUtil.compare(areaDTO.getParentNo(), parentAreaDTO.getNo())) {
        areaDTO.setFullName(StringUtil.formateStr(parentAreaDTO.getFullName()) + areaDTO.getName());
        //设置深度
        areaDTO.setLevel(rootLevel + 1);
        childAreaDTOList.add(areaDTO);
      }
    }
    //设置子节点
    parentAreaDTO.setChildAreaDTOList(childAreaDTOList);
    //设置是否叶子节点
    if (CollectionUtils.isEmpty(childAreaDTOList)) {
      parentAreaDTO.setLeaf(true);
    } else {
      parentAreaDTO.setLeaf(false);
    }
    //递归构造子节点
    for (AreaDTO areaDTO : childAreaDTOList) {
      //进入子节点构造时深度+1
      constructTree(areaDTO, ++rootLevel);
      //递归调用返回时，构造子节点的兄弟节点，深度要和该子节点深度一样，因为之前加1，所以要减1
      --rootLevel;
    }
  }

  /**
   * 过滤出所有市，并且加入特殊区域，只用于导入客户供应商所在区域信息
   */
  public static Map<String, AreaDTO> getAreaDTOMap() {
    Map<String, AreaDTO> areaDTOMap = new HashMap<String, AreaDTO>();
    if (needRefresh()) {
      synAreaDTOTree();
    }
    for (AreaDTO areaDTO : allAreaDTOMap.values()) {
      //当出现4个直辖市，3个行政区，5个自治区时，当用户导入这些市时，则默认导入到省
      if (ArrayUtils.contains(AreaDTO.importAreaIncludeProvince, areaDTO.getNo())) {
        areaDTOMap.put(areaDTO.getName(), areaDTO);
        continue;
      }
      //过滤出所有市
      if (areaDTO.getLevel() == 2 && !ArrayUtils.contains(AreaDTO.importAreaExcludeCity, areaDTO.getName())) {
        areaDTOMap.put(areaDTO.getName(), areaDTO);
      }
    }
    return areaDTOMap;
  }

  public static Map<Long,AreaDTO> getAllAreaDTOMap(){
    if (needRefresh()) {
      synAreaDTOTree();
    }
    return allAreaDTOMap;
  }

  public static String getAreaInfo(Long province, Long city, Long region){
    StringBuffer sb = new StringBuffer();
    if (province != null) {
      AreaDTO areaDTO = getAreaDTOByNo(province);
      if (areaDTO != null) {
        sb.append(areaDTO.getName());
      }
    }
    if (city != null) {
      AreaDTO areaDTO = getAreaDTOByNo(city);
      if (areaDTO != null) {
        sb.append(areaDTO.getName());
      }
    }
    if (region != null) {
      AreaDTO areaDTO = getAreaDTOByNo(region);
      if (areaDTO != null) {
        sb.append(areaDTO.getName());
      }
    }
    return sb.toString();
  }

  public static ShopAdAreaDTO generateShopAdAreaDTO(Long shopAdAreaId) {
    if (shopAdAreaId != null) {
      AreaDTO areaDTO = getAreaDTOByNo(shopAdAreaId);
      if (areaDTO != null) {
        ShopAdAreaDTO shopAdAreaDTO = new ShopAdAreaDTO();
        shopAdAreaDTO.setAreaId(shopAdAreaId);
        if (areaDTO.getLevel() == 1) {
          shopAdAreaDTO.setProvince(areaDTO.getNo());
        } else if (areaDTO.getLevel() == 2) {
          shopAdAreaDTO.setCity(areaDTO.getNo());
          AreaDTO provinceAreaDTO = getAreaDTOByNo(areaDTO.getParentNo());
          if (provinceAreaDTO != null) {
            shopAdAreaDTO.setProvince(provinceAreaDTO.getNo());
          }
        } else if (areaDTO.getLevel() == 3) {
          shopAdAreaDTO.setRegion(areaDTO.getNo());
          AreaDTO cityAreaDTO = getAreaDTOByNo(areaDTO.getParentNo());
          if (cityAreaDTO != null) {
            shopAdAreaDTO.setCity(cityAreaDTO.getNo());
            AreaDTO provinceAreaDTO = getAreaDTOByNo(cityAreaDTO.getParentNo());
            if (provinceAreaDTO != null) {
              shopAdAreaDTO.setProvince(provinceAreaDTO.getNo());
            }
          }
        }
        return shopAdAreaDTO;
      }
    }
    return null;
  }
}