package com.bcgogo.config.service;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.config.cache.AreaCacheManager;
import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.config.cache.ConfigCacheManager;
import com.bcgogo.config.dto.*;
import com.bcgogo.config.model.*;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.ConcurrentScene;
import com.bcgogo.enums.RelationTypes;
import com.bcgogo.enums.ShopRelationStatus;
import com.bcgogo.enums.config.AttachmentType;
import com.bcgogo.enums.shop.*;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.exception.BcgogoExceptionType;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.ValidateImportDataDTO;
import com.bcgogo.user.merge.MergeChangeLogDTO;
import com.bcgogo.user.merge.MergeRecordDTO;
import com.bcgogo.user.merge.MergeResult;
import com.bcgogo.user.merge.MergeSnap;
import com.bcgogo.utils.*;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class ConfigService implements IConfigService {

  private static final Logger LOG = LoggerFactory.getLogger(ConfigService.class);
  private IApplyService applyService;

  public IApplyService getApplyService() {
    return applyService == null ? ServiceManager.getService(IApplyService.class) :applyService;
  }

  @Override
  public String getConfig(String name, Long shopId) {
    Config config = ConfigCacheManager.getConfig(name, shopId);
    if (config != null) {
      return config.getValue();
    }
    ConfigWriter writer = configDaoManager.getWriter();
    config = writer.getConfig(name, shopId);
    if (config != null) {
      ConfigCacheManager.addConfig(config);
      return config.getValue();
    } else {
      config = writer.getConfig(name, null);
      if (config != null) {
        ConfigCacheManager.addConfig(config);
        return config.getValue();
      }
    }
    return null;
  }

  public String getConfigWithoutCache(String name, Long shopId){
    ConfigWriter writer = configDaoManager.getWriter();
    Config config = writer.getConfig(name, shopId);
    if (config != null) {
      ConfigCacheManager.addConfig(config);
      return config.getValue();
    } else {
      config = writer.getConfig(name, null);
      if (config != null) {
        ConfigCacheManager.addConfig(config);
        return config.getValue();
      }
    }
    return null;
  }

  @Override
  public void setConfig(String name, String value, Long shopId) {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Config config = writer.getConfig(name, shopId);
      if (config == null) {
        config = new Config();
        config.setName(name);
        config.setShopId(shopId);
        config.setValue(value);
      } else {
        config.setValue(value);
      }
      writer.saveOrUpdate(config);
      writer.commit(status);
      //zoujianhong 在Memcached中添加变更标记
      MemCacheAdapter.set(config.assembleKey(), String.valueOf(System.currentTimeMillis()));
    } finally {
      writer.rollback(status);
    }
  }

  public void deleteConfig(String name, Long shopId) {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Config config = writer.getConfig(name, shopId);
      writer.delete(Config.class, config.getId());
    } finally {
      writer.rollback(status);
    }
    ConfigCacheManager.removeConfig(name, shopId);
  }

  public void saveOrUpdateConfig(ConfigDTO configDTO) {
    if (configDTO == null) {
      return;
    }
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Config config = writer.getConfig(configDTO.getName(), configDTO.getShopId());
      if (config == null) {
        config = new Config();
        config.setName(configDTO.getName());
        //shopId暂时不处理，现在默认为-1
        config.setShopId(-1l);
        config.setValue(configDTO.getValue());
        config.setDescription(configDTO.getDescription());
      } else {
        config.setValue(configDTO.getValue());
        config.setDescription(configDTO.getDescription());
        config.setShopId(configDTO.getShopId());
      }
      writer.saveOrUpdate(config);
      writer.commit(status);
      //zoujianhong 在Memcached中添加变更标记
      MemCacheAdapter.set(config.assembleKey(), String.valueOf(System.currentTimeMillis()));
    } catch (Exception e){
      LOG.error(e.getMessage(), e);
    }
    finally{
      writer.rollback(status);
    }
  }
  //查询config表
  public  List<ConfigDTO> getConfig(String name, String value,Long shopId,Pager pager) {
    List<ConfigDTO> configDTOs=new ArrayList<ConfigDTO>();
    ConfigWriter writer = configDaoManager.getWriter();
    List<Config> configs = writer.getConfig(name, value,shopId,pager);
    for(Config config:configs){
      configDTOs.add(config.toDTO());
    }
    return configDTOs;
  }
  public int countConfigs(String name,String value,Long shopId){
    ConfigWriter writer = configDaoManager.getWriter();
    return   writer.countConfigs(name,value,shopId);
  }

  @Override
  public ShopDTO getStoreManager(String name) {
    ConfigWriter writer = configDaoManager.getWriter();
    List<Shop> shopList = writer.getStoreManager(name);
    if (shopList.isEmpty()) return null;
    return shopList.get(0).toDTO();
  }

  @Deprecated//by zhangjuntao
  @Override
  public void activateShop(long shopId, ShopStatus shopStatus) throws BcgogoException, ParseException {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Shop shop = writer.getById(Shop.class, shopId);
      if (shop == null) throw new BcgogoException(BcgogoExceptionType.ShopNotFound);
      shop.setShopState(ShopState.ACTIVE);
      if(shopStatus==null)throw new BcgogoException("shop status is null!");
      if (ShopStatus.isRegistrationTrial(shopStatus)) {
        shop.setTrialStartTime(System.currentTimeMillis());
        shop.setTrialEndTime(getProbationaryPeriod());
      }
      shop.setShopStatus(shopStatus);
      shop.setRegistrationDate(System.currentTimeMillis());
      writer.save(shop);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  private Long getProbationaryPeriod() throws ParseException {
    String config = this.getConfig("SHOP_PROBATIONARY_PERIOD", ShopConstant.BC_SHOP_ID);
    Integer day = 30;
    if (StringUtil.isEmpty(config)) {
      LOG.warn("SHOP_PROBATIONARY_PERIOD config is empty!");
    } else {
      if (RegexUtils.isDigital(config)) {
        day = NumberUtil.intValue(config);
      } else {
        LOG.warn("SHOP_PROBATIONARY_PERIOD config is illegal!");
      }
    }
      return System.currentTimeMillis() + (1000 * 60 * 60 * 24L * day);
  }

  @Override
  public void deactivateShop(long shopId) throws BcgogoException {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();

    try {
      Shop shop = writer.getById(Shop.class, shopId);
      if (shop == null) throw new BcgogoException(BcgogoExceptionType.ShopNotFound);
        shop.setShopState(ShopState.IN_ACTIVE);
      ServiceManager.getService(IShopService.class).createShopOperationTask(writer, ShopOperateTaskScene.DISABLE_REGISTERED_PAID_SHOP, shop.getId());
      writer.save(shop);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<Shop> getShop() {
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.getShop();
  }

  @Override
  public List<Long> getShopId() {
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.getShopId();
  }

  /**
   * 做广告的店铺
   * @return
   */
  @Override
  public List<ShopDTO> getAdShops() {
    ConfigWriter writer = configDaoManager.getWriter();
    List<Shop> shopList = writer.getAdShops();
    if(CollectionUtils.isNotEmpty(shopList)){
      List<ShopDTO> shopDTOList = new ArrayList<ShopDTO>();
      for(Shop shop:shopList){
        shopDTOList.add(shop.toDTO());
      }
      return shopDTOList;
    }
    return null;
  }

@Override
  public List<ShopDTO> getActiveShop() {
    ConfigWriter writer = configDaoManager.getWriter();
    List<Shop> shopList = writer.getActiveShop();
    if(CollectionUtils.isNotEmpty(shopList)){
      List<ShopDTO> shopDTOList = new ArrayList<ShopDTO>();
      for(Shop shop:shopList){
        shopDTOList.add(shop.toDTO());
      }
      return shopDTOList;
    }
    return null;
  }

  @Override
  public List<ShopDTO> getShopSuggestion(String name,ShopKind shopKind,int maxRows) {
    ConfigWriter writer = configDaoManager.getWriter();
    List<Shop> shopList = writer.getShopSuggestion(name, shopKind, maxRows);
    if(CollectionUtils.isNotEmpty(shopList)){
      List<ShopDTO> shopDTOList = new ArrayList<ShopDTO>();
      for(Shop shop:shopList){
        shopDTOList.add(shop.toDTO());
      }
      return shopDTOList;
    }
    return null;
  }

  @Override
  public void updateShopList(ShopDTO ... shopDTOs){
    if(ArrayUtil.isEmpty(shopDTOs)){
      return;
    }
    ConfigWriter writer = configDaoManager.getWriter();
    Object status=writer.begin();
    try{
      for(ShopDTO shopDTO:shopDTOs){
        Shop shop=writer.getById(Shop.class,shopDTO.getId());
        if(shop==null){
          continue;
        }
        shop.fromDTO(shopDTO);
        writer.update(shop);
      }
      writer.commit(status);
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<ShopDTO> getActiveShopFromCache() {
    List<ShopDTO> shopDTOs = (List<ShopDTO>)MemCacheAdapter.get(MemcachePrefix.allActiveShops.getValue());
    if(CollectionUtils.isEmpty(shopDTOs)){
      shopDTOs = getActiveShop();
      MemCacheAdapter.set(DateUtil.DAY_MILLION_SECONDS/2, MemcachePrefix.allActiveShops.getValue(), shopDTOs);
    }
    return shopDTOs;
  }

  @Override
  public List<Shop> getSendInvitationCodeActiveShop() {
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.getSendInvitationCodeActiveShop();
  }

  @Override
  public List<String> getSendInvitationCodeActiveShopMobile() {
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.getSendInvitationCodeActiveShopMobile();
  }

  @Override
  public List<Shop> getShopExcludeTest() {
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.getShopExcludeTest();
  }

  //已注册
  @Override
  public List<Shop> getShopByState(int pageNo, int pageSize) {
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.getShopByState(pageNo, pageSize);
  }

  //shao
  @Override
  public int countShopByState() {
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.countShopByState();
  }

  //待注册
  @Override
  public List<Shop> getShopByState1(int pageNo, int pageSize) {
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.getShopByState1(pageNo, pageSize);
  }

  //shao
  @Override
  public int countShopByState1() {
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.countShopByState1();
  }

  @Override
  public ShopDTO getShopById(long shopId) {
    ConfigWriter writer = configDaoManager.getWriter();

    Shop shop = writer.getById(Shop.class, shopId);

    if (shop == null) return null;
    // add by zhuj
    ShopDTO shopDTO = shop.toDTO();
    ContactDTO[] contactDTOs = new ContactDTO[3];
    List<ShopContact> shopContacts = writer.getShopContactsByShopId(shop.getId());
    if (!CollectionUtils.isEmpty(shopContacts)) {
      if (shopContacts.size() > 3) {
        LOG.error("shop contact list size is over 3,shopId is" + shopId);
        throw new RuntimeException("shop contact list size is over 3,shopId is" + shopId);
      }
      for (int i = 0; i < shopContacts.size(); i++) {
        contactDTOs[i] = shopContacts.get(i).toDTO();
      }
    }
    shopDTO.setContacts(contactDTOs);

    return shopDTO;
  }

  @Override
  public ShopDTO getShopByName(String shopName) {
    ConfigWriter writer = configDaoManager.getWriter();

    List<Shop> shopList = writer.getShopByName(shopName);

    if (shopList.isEmpty()) return null;
    return shopList.get(0).toDTO();
  }

  @Override
  public List<ShopDTO> getShopByObscureName(String shopName) {
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.getShopByObscureName(shopName);
  }

  public List<Long> getAreaLeafsByParentId(Long parentNo){
    List<Long> noList = new ArrayList<Long>();
    if (parentNo==null) return noList;
    List<AreaDTO> parents = this.getChildAreaDTOList(parentNo);
    if (CollectionUtils.isEmpty(parents)) {
      noList.add(Long.valueOf(parentNo));
      return noList;
    }
    for (AreaDTO a : parents) {
      noList.add(a.getNo());
    }
    ConfigWriter writer = configDaoManager.getWriter();
    List<Area> children = writer.getAreaListByParentNos(noList);
    if (CollectionUtils.isNotEmpty(children)) {
      for (Area a : children) {
        noList.add(a.getNo());
      }
    }
    noList.add(Long.valueOf(parentNo));
    return noList;
  }

  @Override
  public List<Area> getArea(long no) {
    List<Area> areaList = new ArrayList<Area>();
    ConfigWriter writer = configDaoManager.getWriter();
    Area area = writer.getArea(no);
    while (area != null) {
      if (area.getParentNo() != null) {
        areaList.add(area);
        area = writer.getArea(area.getParentNo());
      } else {
        area = null;
      }
    }
    Collections.reverse(areaList);
    return areaList;
  }

  public List<AreaDTO> getChildAreaDTOList(Long parentNo) {
    return AreaCacheManager.getChildAreaDTOListByParentNo(parentNo);
  }

  public List<Business> getBusinessList(String parentNo) {
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.getBusinessList(parentNo);
  }

  public ShopBusinessDTO createShopBusiness(ShopBusinessDTO shopBusinessDTO) throws BcgogoException {
    if (shopBusinessDTO == null) throw new BcgogoException(BcgogoExceptionType.NullException);

    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();

    try {
      ShopBusiness shopBusiness = new ShopBusiness(shopBusinessDTO);

      writer.save(shopBusiness);
      writer.commit(status);

      shopBusinessDTO.setId(shopBusiness.getId());

      return shopBusiness.toDTO();
    } finally {
      writer.rollback(status);
    }
  }

  public List<ShopBusiness> getShopBusinessList(Long shopId) {
    ConfigWriter writer = configDaoManager.getWriter();

    return writer.getShopBusinessList(shopId);
  }

  public Long countShop() {
    ConfigWriter writer = configDaoManager.getWriter();

    return writer.countShop();
  }

  public List<ShopDTO> getShopByStoreManagerMobile(String mobile) {
    ConfigWriter writer = configDaoManager.getWriter();

    List<Shop> shopList = writer.getShopByStoreManagerMobile(mobile);

    if (shopList.isEmpty()) return null;

    List<ShopDTO> shopDTOList = new ArrayList<ShopDTO>();
    for (Shop shop : shopList) {
      shopDTOList.add(shop.toDTO());
    }

    return shopDTOList;
  }


  @Autowired
  private ConfigDaoManager configDaoManager;

  public ShopDTO getShopByIdWithoutContacts(Long shopId){
    if(shopId == null) return null;
    ConfigWriter writer = configDaoManager.getWriter();
    Shop shop = writer.getById(Shop.class, shopId);
    return shop==null?null:shop.toDTO();
  }
  @Override
  public ShopDTO getSimpleShopById(Long shopId) {
    if (shopId == null) return null;
    ConfigWriter writer = configDaoManager.getWriter();
    Shop shop = writer.getById(Shop.class, shopId);
    if(shop == null){
      return null;
    }
    ShopDTO shopDTO = shop.toDTO();
    return shopDTO;
  }
  public ShopDTO getShopById(Long shopId) {
    if (shopId == null) return null;
    ConfigWriter writer = configDaoManager.getWriter();
    Shop shop = writer.getById(Shop.class, shopId);
    if(shop == null){
      return null;
    }
    ShopDTO shopDTO = shop.toDTO();
    ContactDTO[] contactDTOs = getShopContactsByShopId(shop.getId());
    shopDTO.setContacts(contactDTOs);

    shopDTO.setAreaName(this.getShopAreaInfoByShopDTO(shopDTO));

    return shopDTO;
  }

  //地区表上传
  public void insertArea(Set dateSet) {
    Iterator iterator = dateSet.iterator();
    ConfigWriter writer = configDaoManager.getWriter();
    StringBuffer sb = null;
    Object status = writer.begin();
    try {
      while (iterator.hasNext()) {
        String[] strs = (String[]) iterator.next();
        if (strs.length != 3) {
          if (sb == null) sb = new StringBuffer("导入地区信息:如下数据格式不正确(数组长度不为3)\n");
          for (String errorLineStr : strs) {
            sb.append(errorLineStr + "  ");
          }
          sb.append("该行数组长度为:" + strs.length);
          sb.append("\n");
          continue;
        }
        Area area = new Area();
        area.setName(strs[0]);
        area.setNo(Long.parseLong(strs[1]));
        area.setParentNo(Long.parseLong(strs[2]));
        writer.save(area);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
      if (sb != null) LOG.error(sb.toString());
    }
  }

  public int countShopByAgentIdAndTime(Long agentId, String startTimeStr, String endTimeStr) throws Exception {
    ConfigWriter writer = configDaoManager.getWriter();
    Long startTime = new SimpleDateFormat("yyyy-MM-dd").parse(startTimeStr).getTime();
    Long endTime = new SimpleDateFormat("yyyy-MM-dd").parse(endTimeStr).getTime();
    return writer.countShopByAgentIdAndTime(agentId, startTime, endTime);
  }

  @Override
  public String getBuildVersion() {
    ResourceLoader resourceLoader = new DefaultResourceLoader();
    Resource url = resourceLoader.getResource("version.properties");
    if (version == null) {
      version = "1";
      try {
        version = StringUtil.convertinputStreamToString(url.getInputStream());

      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      }
    }
    return version;
  }

  private static String version = null;


  public byte[] InputStreamToByte(InputStream is) throws IOException {
    ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
    int ch;
    while ((ch = is.read()) != -1) {
      bytestream.write(ch);
    }
    byte imgdata[] = bytestream.toByteArray();
    bytestream.close();
    return imgdata;
  }

  @Override
  public List<ShopUnitDTO> getShopUnit(Long shopId) {
    ConfigWriter writer = configDaoManager.getWriter();
    List<ShopUnit> shopUnits = writer.getShopUnit(shopId);
    List<ShopUnitDTO> shopUnitDTOs = new ArrayList<ShopUnitDTO>();
    if (shopUnits != null) {
      for (ShopUnit shopUnit : shopUnits) {
        shopUnitDTOs.add(shopUnit.toDTO());
      }
    } else {
      shopUnitDTOs = null;
    }
    return shopUnitDTOs;
  }

  @Override
  public void batchSave(List<?> objects) throws Exception {
    if (CollectionUtils.isEmpty(objects)) return;
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for (Object o : objects) {
        writer.saveOrUpdate(o);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }
//public String getTempImge(HttpServletRequest request) throws Exception
//{
//        //获得ServletContext路径
//        String serverPath = request.getSession().getServletContext().getRealPath("/") + "file\\";
//        File fileDir = new File(serverPath);
//	            if(!fileDir.exists()){
//	                if(!fileDir.mkdir())
//	                    throw new Exception("目录不存在，创建失败！");
//	            }
//	            /*查找文件，如果不存在，就创建*/
//	            File file = new File(serverPath+"/temp.jpg");
//	            if(!file.exists())
//              {
//	                if(!file.createNewFile())
//	                    throw new Exception("文件不存在，创建失败！");
//              }
//                return  file.getAbsolutePath();
//}

  @Override
  public void updateSolrThesaurus(
      MultipartFile srcDic, MultipartFile targetDic, String charset, OutputStream outputStream) throws Exception {
    BufferedReader srcReader = null, targetReader = null;
    BufferedWriter bufferedWriter = null;
    try {
      srcReader = new BufferedReader(new InputStreamReader(srcDic.getInputStream(), charset));
      targetReader = new BufferedReader(new InputStreamReader(targetDic.getInputStream(), charset));

      Set<String> resultSet = new HashSet<String>();
      String rl = null;
      while ((rl = srcReader.readLine()) != null) {
        if (StringUtils.isNotBlank(rl.trim())) {
          resultSet.add(rl.trim());
        }
      }
      srcReader.close();
      while ((rl = targetReader.readLine()) != null) {
        if (StringUtils.isNotBlank(rl.trim())) {
          resultSet.add(rl.trim());
        }
      }
      targetReader.close();


      Iterator<String> iterator = resultSet.iterator();
      bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, charset));
      while (iterator.hasNext()) {
        bufferedWriter.write(iterator.next());
        bufferedWriter.newLine();
      }
      bufferedWriter.flush();
      outputStream.flush();
    } finally {
      bufferedWriter.close();
      outputStream.close();
    }

  }

  @Override
  public void updateOrderUnitSort(Long shopid, SalesOrderDTO orderDTO) {
    if (orderDTO == null || shopid == null || orderDTO.getItemDTOs() == null ||
        orderDTO.getItemDTOs().length == 0) {
      return;
    }
    ShopUnitDTO[] shopUnitDTOs = orderDTO.getShopUnits();
    List<ShopUnitDTO> shopUnitDTOList = new ArrayList<ShopUnitDTO>();
    if (shopUnitDTOs != null && shopUnitDTOs.length > 0) {
      for (int i = 0; i < shopUnitDTOs.length; i++) {
        if (shopUnitDTOs[i] == null || StringUtils.isBlank(shopUnitDTOs[i].getUnitName())) {
          continue;
        }
        for (SalesOrderItemDTO itemDTO : orderDTO.getItemDTOs()) {
          if ((shopUnitDTOs[i].getUnitName()).equals(itemDTO.getUnit())) {
            shopUnitDTOList.add(shopUnitDTOs[i]);
          }
        }
      }
    }
    if (CollectionUtils.isNotEmpty(shopUnitDTOList)) {
      updateUnitSort(shopid, shopUnitDTOList);
    }
  }

  @Override
  public void updateOrderUnitSort(Long shopid, RepairOrderDTO orderDTO) {
    if (orderDTO == null || shopid == null || orderDTO.getItemDTOs() == null ||
        orderDTO.getItemDTOs().length == 0) {
      return;
    }
    ShopUnitDTO[] shopUnitDTOs = orderDTO.getShopUnits();
    List<ShopUnitDTO> shopUnitDTOList = new ArrayList<ShopUnitDTO>();
    if (shopUnitDTOs != null && shopUnitDTOs.length > 0) {
      for (int i = 0; i < shopUnitDTOs.length; i++) {
        if (shopUnitDTOs[i] == null || StringUtils.isBlank(shopUnitDTOs[i].getUnitName())) {
          continue;
        }
        for (RepairOrderItemDTO itemDTO : orderDTO.getItemDTOs()) {
          if ((shopUnitDTOs[i].getUnitName()).equals(itemDTO.getUnit())) {
            shopUnitDTOList.add(shopUnitDTOs[i]);
          }
        }
      }
    }
    if (CollectionUtils.isNotEmpty(shopUnitDTOList)) {
      updateUnitSort(shopid, shopUnitDTOList);
    }
  }

  @Override
  public void updateOrderUnitSort(Long shopid, PurchaseInventoryDTO orderDTO) {
    if (orderDTO == null || shopid == null || orderDTO.getItemDTOs() == null ||
        orderDTO.getItemDTOs().length == 0) {
      return;
    }
    ShopUnitDTO[] shopUnitDTOs = orderDTO.getShopUnits();
    List<ShopUnitDTO> shopUnitDTOList = new ArrayList<ShopUnitDTO>();
    if (shopUnitDTOs != null && shopUnitDTOs.length > 0) {
      for (int i = 0; i < shopUnitDTOs.length; i++) {
        if (shopUnitDTOs[i] == null || StringUtils.isBlank(shopUnitDTOs[i].getUnitName())) {
          continue;
        }
        for (PurchaseInventoryItemDTO itemDTO : orderDTO.getItemDTOs()) {
          if ((shopUnitDTOs[i].getUnitName()).equals(itemDTO.getUnit())) {
            shopUnitDTOList.add(shopUnitDTOs[i]);
          }
        }
      }
    }
    if (CollectionUtils.isNotEmpty(shopUnitDTOList)) {
      updateUnitSort(shopid, shopUnitDTOList);
    }
  }

  @Override
  public void updateOrderUnitSort(Long shopid, PurchaseOrderDTO orderDTO) {
    if (orderDTO == null || shopid == null || orderDTO.getItemDTOs() == null ||
        orderDTO.getItemDTOs().length == 0) {
      return;
    }
    ShopUnitDTO[] shopUnitDTOs = orderDTO.getShopUnits();
    List<ShopUnitDTO> shopUnitDTOList = new ArrayList<ShopUnitDTO>();
    if (shopUnitDTOs != null && shopUnitDTOs.length > 0) {
      for (int i = 0; i < shopUnitDTOs.length; i++) {
        if (shopUnitDTOs[i] == null || StringUtils.isBlank(shopUnitDTOs[i].getUnitName())) {
          continue;
        }
        for (PurchaseOrderItemDTO itemDTO : orderDTO.getItemDTOs()) {
          if ((shopUnitDTOs[i].getUnitName()).equals(itemDTO.getUnit())) {
            shopUnitDTOList.add(shopUnitDTOs[i]);
          }
        }
      }
    }
    if (CollectionUtils.isNotEmpty(shopUnitDTOList)) {
      updateUnitSort(shopid, shopUnitDTOList);
    }
  }

  @Override
  public void updateOrderUnitSort(Long shopid, PurchaseReturnDTO orderDTO) {
    if (orderDTO == null || shopid == null || orderDTO.getItemDTOs() == null ||
        orderDTO.getItemDTOs().length == 0) {
      return;
    }
    ShopUnitDTO[] shopUnitDTOs = orderDTO.getShopUnits();
    List<ShopUnitDTO> shopUnitDTOList = new ArrayList<ShopUnitDTO>();
    if (shopUnitDTOs != null && shopUnitDTOs.length > 0) {
      for (int i = 0; i < shopUnitDTOs.length; i++) {
        if (shopUnitDTOs[i] == null || StringUtils.isBlank(shopUnitDTOs[i].getUnitName())) {
          continue;
        }
        for (PurchaseReturnItemDTO itemDTO : orderDTO.getItemDTOs()) {
          if ((shopUnitDTOs[i].getUnitName()).equals(itemDTO.getUnit())) {
            shopUnitDTOList.add(shopUnitDTOs[i]);
          }
        }
      }
    }
    if (CollectionUtils.isNotEmpty(shopUnitDTOList)) {
      updateUnitSort(shopid, shopUnitDTOList);
    }
  }


  @Override
  public void updateUnitSort(Long  shopId ,List<ShopUnitDTO> shopUnitDTOList) {
    if(!BcgogoConcurrentController.lock(ConcurrentScene.UPDATE_UNIT_SORT,shopId)){
      return;
    }
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    Long editTime = System.currentTimeMillis();
    try{
      if(CollectionUtils.isEmpty(shopUnitDTOList)) return;
      for(ShopUnitDTO shopUnitDTO:shopUnitDTOList){
        if(StringUtils.isNotBlank(shopUnitDTO.getUnitName())){
          ShopUnit shopUnit = writer.getShopUnitByUnitName(shopId,shopUnitDTO.getUnitName());
          if(shopUnit!=null){
            shopUnit.setLastEditTime(editTime);
            writer.update(shopUnit);
            editTime -=10;
          }else {
            shopUnit = new ShopUnit();
            shopUnit.setLastEditTime(editTime);
            shopUnit.setUnitName(shopUnitDTO.getUnitName());
            shopUnit.setShopId(shopId);
            writer.save(shopUnit);
            editTime -=10;
          }
        }else {
          continue;
        }
      }
      writer.commit(status);
    }finally {
      writer.rollback(status);
      BcgogoConcurrentController.release(ConcurrentScene.UPDATE_UNIT_SORT,shopId);
    }

  }

  @Override
  public List<ShopUnitDTO> initShopUnit(Long shopId) throws Exception {
    ConfigWriter writer = configDaoManager.getWriter();
    List<ShopUnit> shopUnits = writer.getShopUnit(ConfigConstant.CONFIG_SHOP_ID);
    List<ShopUnitDTO> shopUnitDTOs = new ArrayList<ShopUnitDTO>();
    if (CollectionUtils.isNotEmpty(shopUnits)) {
      for (ShopUnit shopUnit : shopUnits) {
        shopUnitDTOs.add(shopUnit.toDTO());
      }
      this.updateUnitSort(shopId, shopUnitDTOs);
      return shopUnitDTOs;
    }
    return null;
  }

  @Override
  public void saveOrUpdateUnitSort(Long shopId, String... units) throws Exception {
    if(!BcgogoConcurrentController.lock(ConcurrentScene.UPDATE_UNIT_SORT,shopId)){
      return;
    }
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    Long editTime = System.currentTimeMillis();
    try{
      List<ShopUnitDTO> shopUnitDTOs = getShopUnit(shopId);
      if(CollectionUtils.isEmpty(shopUnitDTOs)){
        initShopUnit(shopId);
      }
      if(units== null || units.length == 0) return;
      for(String unitName:units){
        if(StringUtils.isNotBlank(unitName)){
          ShopUnit shopUnit = writer.getShopUnitByUnitName(shopId,unitName);
          if(shopUnit!=null){
            shopUnit.setLastEditTime(editTime);
            writer.update(shopUnit);
            editTime -=10;
          }else {
            shopUnit = new ShopUnit();
            shopUnit.setLastEditTime(editTime);
            shopUnit.setUnitName(unitName);
            shopUnit.setShopId(shopId);
            writer.save(shopUnit);
            editTime -=10;
          }
        }else {
          continue;
        }
      }
      writer.commit(status);
    }finally {
      writer.rollback(status);
      BcgogoConcurrentController.release(ConcurrentScene.UPDATE_UNIT_SORT,shopId);
    }

  }

  public MergeResult saveMergeRecord(MergeResult<?,?> mergeResult) throws BcgogoException {
    ConfigWriter configWriter =configDaoManager.getWriter();
    Object status=configWriter.begin();
    try{
      //save merge change log
      if(CollectionUtils.isNotEmpty(mergeResult.getMergeChangeLogs())){
        MergeChangeLog mergeChangeLog=null;
        for(MergeChangeLogDTO mergeLog:mergeResult.getMergeChangeLogs()){
          mergeChangeLog=new MergeChangeLog();
          configWriter.save(mergeChangeLog.fromDTO(mergeLog));
        }
      }
      //save merge record
      Map<Long,?> mergeSnapMap=mergeResult.getMergeSnapMap();
      if(mergeSnapMap!=null&&CollectionUtils.isNotEmpty(mergeSnapMap.keySet())){
        MergeSnap mergeSnap=null;
        MergeRecord mergeRecord=null;
        for(Long childId:mergeSnapMap.keySet()){
          mergeRecord=new MergeRecord();
          mergeSnap =(MergeSnap)mergeSnapMap.get(childId);
          mergeRecord.fromDTO(mergeSnap.toMergeRecord());
          mergeRecord.setMergeType(mergeResult.getMergeType());
          mergeRecord.setMergeTime(System.currentTimeMillis());
          configWriter.save(mergeRecord);
        }
      }
      configWriter.commit(status);
      return mergeResult;
    }catch (Exception e){
      LOG.error("保存合并记录异常！！！");
      throw new BcgogoException(e.getMessage(),e);
    }finally {
      configWriter.rollback(status);
    }
  }




  @Override
  public void saveShopCustomerRelation(Long shopId, Long customerId){
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try{
      ShopCustomerRelation entity = new ShopCustomerRelation();
      entity.setShopId(shopId);
      entity.setCustomerId(customerId);
      writer.save(entity);
      writer.commit(status);
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public Long getTempCustomerIdByShopId(Long shopId){
    ConfigWriter writer = configDaoManager.getWriter();
    Long tempCustomerId = null;
    ShopCustomerRelation entity = writer.getShopCustomerRelationByShopId(shopId);
    if(entity!=null){
      tempCustomerId = entity.getCustomerId();
    }
    return tempCustomerId;
  }

  @Override
  public Long getTempShopIdByCustomerId(Long customerId){
    ConfigWriter writer = configDaoManager.getWriter();
    Long tempShopId = null;
    ShopCustomerRelation entity = writer.getShopCustomerRelationByCustomerId(customerId);
    if(entity!=null){
      tempShopId = entity.getShopId();
    }
    return tempShopId;
  }

  @Override
  public String getCustomerShopStatus(Long customerId){
    ConfigWriter writer = configDaoManager.getWriter();
    String shopStatus = writer.getCustomerShopStatus(customerId);
    return shopStatus;
  }

  @Override
  public void deleteShopCustomerRelation(Long customerId){
    ConfigWriter writer = configDaoManager.getWriter();
    ShopCustomerRelation entity = writer.getShopCustomerRelationByCustomerId(customerId);
    if(entity!=null){
      Object status = writer.begin();
      try{
        writer.delete(ShopCustomerRelation.class, entity.getId());
        writer.commit(status);
      }finally {
        writer.rollback(status);
      }
    }
  }

  @Override
  public void createWholesalerShopRelation(WholesalerShopRelationDTO wholesalerShopRelationDTO){
    ConfigWriter writer = configDaoManager.getWriter();
    WholesalerShopRelation entity = new WholesalerShopRelation();
    entity.setShopId(wholesalerShopRelationDTO.getShopId());
    entity.setWholesalerShopId(wholesalerShopRelationDTO.getWholesalerShopId());
    entity.setStatus(wholesalerShopRelationDTO.getStatus());
    Object status = writer.begin();
    try{
      writer.save(entity);
      Shop wholesalerShop = writer.getById(Shop.class, entity.getWholesalerShopId());
      if (wholesalerShop != null) {
        wholesalerShop.setRelativeCustomerAmount(NumberUtil.intValue(wholesalerShop.getRelativeCustomerAmount()) + 1);
      }
      writer.commit(status);
    }finally {
      writer.rollback(status);
    }
  }


  @Override
  public boolean createWholesalerShopRelationByShopRelation(ConfigWriter writer,ShopRelationInviteDTO shopRelationInviteDTO)throws Exception{
    Long customerShopId = null, wholesalerShopId = null;
    boolean isCreatedOrUpdated = false;
    if (InviteType.CUSTOMER_INVITE.equals(shopRelationInviteDTO.getInviteType())) {
      customerShopId = shopRelationInviteDTO.getOriginShopId();
      wholesalerShopId = shopRelationInviteDTO.getInvitedShopId();
    } else if (InviteType.SUPPLIER_INVITE.equals(shopRelationInviteDTO.getInviteType())) {
      customerShopId = shopRelationInviteDTO.getInvitedShopId();
      wholesalerShopId = shopRelationInviteDTO.getOriginShopId();
    }
    WholesalerShopRelation wholesalerShopRelation = CollectionUtil.uniqueResult(writer.getWholesalerShopRelationByCustomerShopIds(wholesalerShopId, null, customerShopId));
    if (wholesalerShopRelation == null || ShopRelationStatus.DISABLED.equals(wholesalerShopRelation.getStatus())) {
      wholesalerShopRelation = new WholesalerShopRelation(customerShopId, wholesalerShopId, ShopRelationStatus.ENABLED,
          RelationTypes.RELATED);
      writer.save(wholesalerShopRelation);
      isCreatedOrUpdated = true;
      Shop wholesalerShop = writer.getById(Shop.class, wholesalerShopRelation.getWholesalerShopId());
      if (wholesalerShop != null) {
        wholesalerShop.setRelativeCustomerAmount(NumberUtil.intValue(wholesalerShop.getRelativeCustomerAmount()) + 1);
      }
    }else if(wholesalerShopRelation.getRelationType() != RelationTypes.RELATED){
      wholesalerShopRelation.setRelationType(RelationTypes.RELATED);
      writer.update(wholesalerShopRelation);
      isCreatedOrUpdated = true;
    }
    return isCreatedOrUpdated;
  }

  @Override
  public List<WholesalerShopRelationDTO> getWholesalerShopRelationByWholesalerShopId(Long wholesalerShopId,
                                                                                     List<RelationTypes> relationTypeList){
    ConfigWriter writer = configDaoManager.getWriter();
    List<WholesalerShopRelationDTO> resultList = new ArrayList<WholesalerShopRelationDTO>();
    List<WholesalerShopRelation> relationList = writer.getWholesalerShopRelationByWholesalerShopId(wholesalerShopId,relationTypeList);
    if(CollectionUtils.isNotEmpty(relationList)){
      for(int i=0;i<relationList.size();i++){
        resultList.add(relationList.get(i).toDTO());
      }
    }
    return resultList;
  }

  @Override
  public List<WholesalerShopRelationDTO> getWholesalerShopRelationByShopId(Long shopId,List<RelationTypes> relationTypeList){
    ConfigWriter writer = configDaoManager.getWriter();
    List<WholesalerShopRelationDTO> resultList = new ArrayList<WholesalerShopRelationDTO>();
    List<WholesalerShopRelation> relationList = writer.getWholesalerShopRelationByShopId(shopId,relationTypeList);
    if(CollectionUtils.isNotEmpty(relationList)){
      for(int i=0;i<relationList.size();i++){
        resultList.add(relationList.get(i).toDTO());
      }
    }
    return resultList;
  }

  @Override
  public Map<Long, WholesalerShopRelationDTO> getWholesalerShopRelationMapByWholesalerShopId(Long shopId,
      List<RelationTypes> relationTypeList, Long... wholesalerShopId) {
    ConfigWriter writer = configDaoManager.getWriter();
    Map<Long, WholesalerShopRelationDTO> wholesalerShopRelationDTOs = new HashMap<Long, WholesalerShopRelationDTO>();
    List<WholesalerShopRelation> relationList = writer.getWholesalerShopRelationByWholesalerShopIds(shopId,
        relationTypeList, wholesalerShopId);
    if(CollectionUtils.isNotEmpty(relationList)){
      for(int i=0;i<relationList.size();i++){
        wholesalerShopRelationDTOs.put(relationList.get(i).getWholesalerShopId(), relationList.get(i).toDTO());
      }
    }
    return wholesalerShopRelationDTOs;
  }

  @Override
  public Map<Long, WholesalerShopRelationDTO> getWholesalerShopRelationMapByCustomerShopId(Long shopId, List<RelationTypes> relationTypeList,
                                                                                           Long... customerShopId) {
    ConfigWriter writer = configDaoManager.getWriter();
    Map<Long, WholesalerShopRelationDTO> wholesalerShopRelationDTOs = new HashMap<Long, WholesalerShopRelationDTO>();
    List<WholesalerShopRelation> relationList = writer.getWholesalerShopRelationByCustomerShopIds(shopId, relationTypeList, customerShopId);
    if(CollectionUtils.isNotEmpty(relationList)){
      for(int i=0;i<relationList.size();i++){
        wholesalerShopRelationDTOs.put(relationList.get(i).getShopId(), relationList.get(i).toDTO());
      }
    }
    return wholesalerShopRelationDTOs;
  }

  @Override
  public WholesalerShopRelationDTO getWholesalerShopRelationDTOByShopId(Long customerShopId, Long wholesalerShopId
      ,List<RelationTypes> relationTypeList) {
    WholesalerShopRelation wholesalerShopRelation = CollectionUtil.uniqueResult(
        configDaoManager.getWriter().getWholesalerShopRelationByWholesalerShopIds(customerShopId,
            relationTypeList, wholesalerShopId));
    if(wholesalerShopRelation!=null){
      return wholesalerShopRelation.toDTO();
    }
    return null;
  }

  @Override
  public Map<Long,ShopDTO> getShopByShopId(Long... shopId) {
    ConfigWriter writer = configDaoManager.getWriter();
    List<Shop> shopList = writer.getShopByShopId(shopId);
    Map<Long,ShopDTO> shopDTOMap = new HashMap<Long,ShopDTO>();
    if(CollectionUtils.isNotEmpty(shopList)){
      for(Shop shop : shopList){
        ShopDTO shopDTO = shop.toDTO();
        ContactDTO[] contactDTOs = getShopContactsByShopId(shop.getId());
        shopDTO.setContacts(contactDTOs);
        shopDTOMap.put(shop.getId(),shopDTO);
      }
    }
    return shopDTOMap;
  }

  public int getMergeRecordCount(MergeRecordDTO mergeRecordIndex){
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.getMergeRecordCount(mergeRecordIndex);
  }


  @Override
  public List<MergeRecord> getMergeRecords(MergeRecordDTO mergeRecordIndex){
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.getMergeRecords(mergeRecordIndex);
  }

  public MergeRecord getMergeRecordDetail(Long shopId,Long parentId,Long childId){
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.getMergeRecordDetail(shopId,parentId,childId);
  }


  @Override
  public void initAllShopArea() {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<Shop> shopList = getShop();
      ShopDTO shopDTO = null;
      if (CollectionUtil.isNotEmpty(shopList)) {
        for (Shop shop : shopList) {
          if(shop.getId().equals(10000010001090014L)){
            int i = 1;
            i++;
          }
          shopDTO = getApplyService().getShopAreaDTOInfo(shop.getId());
          if (shopDTO != null) {
            shop.setProvince(shopDTO.getProvince());
            shop.setCity(shopDTO.getCity());
            shop.setRegion(shopDTO.getRegion());
            writer.update(shop);
          }
        }
        writer.commit(status);
      }
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void initAllShopNamePy() {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<Shop> shopList = getShop();
      if (CollectionUtil.isNotEmpty(shopList)) {
        for (Shop shop : shopList) {
          if (StringUtils.isNotBlank(shop.getName())) {
            shop.setNameFl(PinyinUtil.converterToFirstSpell(shop.getName()));
            shop.setNamePy(PinyinUtil.converterToPingyin(shop.getName()));
          }
          writer.update(shop);
        }
        writer.commit(status);
      }
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public boolean checkLimitCustomerAmount(Long shopId, Integer toApplyCustomerAmount) {
    if(true){
      return true;
    }
    if (shopId != null) {
      return checkLimitCustomerAmount(getShopById(shopId), toApplyCustomerAmount);
    }
    return false;
  }

  @Override
  public boolean checkLimitCustomerAmount(ShopDTO shopDTO, Integer toApplyCustomerAmount) {
      int limitAmount = 0;
      if (shopDTO != null && shopDTO.getShopLevel() != null) {
        switch (shopDTO.getShopLevel()) {
          case PRIMARY_WHOLESALER:
            limitAmount = NumberUtil.intValue(getConfig("PRIMARY_WHOLESALER_LIMIT_CUSTOMER", ShopConstant.BC_SHOP_ID), 0);
            break;
        }
      }
      if (shopDTO != null && NumberUtil.intValue(shopDTO.getRelativeCustomerAmount()) + NumberUtil.intValue(toApplyCustomerAmount) <= limitAmount) {
        return true;
      }
    return false;
  }

  @Override
  public Map<Long, AreaDTO> getAreaByAreaNo(Set<Long> areaNos) {
    Map<Long, AreaDTO> areaMap = new HashMap<Long, AreaDTO>();
    if (CollectionUtil.isNotEmpty(areaNos)) {
      for(Long areaNo : areaNos){
        AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(areaNo);
        if(areaDTO!=null){
          areaMap.put(areaDTO.getNo(),areaDTO);
        }
      }
    }
    return areaMap;
  }

  @Override
  public String getRegisterInfoByRegisterShopId(Long registerShopId) {
    ConfigWriter writer = configDaoManager.getWriter();
    RegisterInfo registerInfo = writer.getRegisterInfoByRegisterShopId(registerShopId);
    if (registerInfo == null) {
      LOG.info("getRegisterInfoByRegisterShopId:{} is null .",registerShopId);
      return null;
    }
    return registerInfo.getInvitationCode();
  }

  @Override
  public RegisterInfoDTO getRegisterInfoDTOByRegisterShopId(Long registerShopId) {
    ConfigWriter writer = configDaoManager.getWriter();
     RegisterInfo registerInfo = writer.getRegisterInfoByRegisterShopId(registerShopId);
     if (registerInfo == null) {
       LOG.info("getRegisterInfoByRegisterShopId:{} is null .",registerShopId);
       return null;
     }
     return registerInfo.toDTO();
  }

  @Override
  public Result validateShopRegBasicInfo(Long shopId, Long customerId) {
    if(shopId == null || customerId == null){
      return new Result("对不起，当前客户不存在，无法升级",false);
    }
    ShopCustomerRelation shopCustomerRelation = configDaoManager.getWriter().getShopCustomerRelationByCustomerId(customerId);
    if(shopCustomerRelation!=null){
      return new Result("对不起，当前客户已经升级，请等待审核",false);
    }
    return new Result();
  }

  private void createInviteRegisterInfo(ConfigWriter writer, ShopDTO shopDTO) {
    if(shopDTO.getInvitationCodeDTO()!=null){
      RegisterInfo registerInfo = new RegisterInfo();
      registerInfo.from(shopDTO);
      writer.save(registerInfo);
    }
  }

  @Override
  public Set<Long> getRelationWholesalerShopIds(Long shopId, List<RelationTypes> relationTypeList) {
    Set<Long> supplierShopIds = new HashSet<Long>();
    if(shopId != null){
      supplierShopIds = configDaoManager.getWriter().getRelationWholesalerShopIds(shopId,relationTypeList);
    }
    return supplierShopIds;
  }

  @Override
  public Set<Long> getRelatedCustomerShopIds(Long wholesalerShopId, List<RelationTypes> relationTypeList) {
    Set<Long> customerShopIds = new HashSet<Long>();
       if(wholesalerShopId != null){
         customerShopIds = configDaoManager.getWriter().getRelatedCustomerShopIds(wholesalerShopId,relationTypeList);
       }
       return customerShopIds;
  }

  @Override
  public List<Long> getBcgogoRecommendSupplierShopIds(Long shopId) {
    if (shopId != null) {
      return configDaoManager.getWriter().getBcgogoRecommendSupplierShopIds(shopId,
          NumberUtil.parseLongValues(getConfig("WholesalerShopVersions",ShopConstant.BC_SHOP_ID)));
    }
    return null;
  }

  public AttachmentDTO getAttachmentByShopId(Long shopId,AttachmentType attachmentType) {
    ConfigWriter configWriter = configDaoManager.getWriter();
    List<Attachment> attachmentList = configWriter.getAttachmentByShopId(shopId, attachmentType);
    if (CollectionUtil.isEmpty(attachmentList)) {
      return null;
    }
    return attachmentList.get(0).toDTO();

  }

  @Override
  public String getShopAreaInfoByShopDTO(ShopDTO shopDTO) {
    StringBuffer areaInfo = new StringBuffer();
    if(shopDTO.getProvince()!=null){
      AreaDTO province = AreaCacheManager.getAreaDTOByNo(shopDTO.getProvince());
      areaInfo.append(province==null?"":province.getName());
      AreaDTO city = AreaCacheManager.getAreaDTOByNo(shopDTO.getCity());
      areaInfo.append(city==null?"":city.getName());
      AreaDTO region = AreaCacheManager.getAreaDTOByNo(shopDTO.getRegion());
      areaInfo.append(region==null?"":region.getName());
    }
    shopDTO.setAreaName(areaInfo.toString());
    return areaInfo.toString();
  }

  @Override
  public String getProvinceAndCityNoByCityName(String cityName,ValidateImportDataDTO validateImportDataDTO) {
      String provinceAndCityNo = "";
      if(validateImportDataDTO != null && validateImportDataDTO.getAreaDTOMap() != null) {
        for(AreaDTO areaDTO : validateImportDataDTO.getAreaDTOMap().values()) {
          if(areaDTO.getName().startsWith(cityName)) {
            if(areaDTO.getLevel() == 2) {
              provinceAndCityNo = areaDTO.getParentNo() + "," + areaDTO.getNo();
            } else {
              provinceAndCityNo = areaDTO.getNo() + "";
            }
            break;
          }
        }
      }

      return provinceAndCityNo;
  }
  @Override
  public ContactDTO[] getShopContactsByShopId(Long shopId) {
    ConfigWriter writer = configDaoManager.getWriter();
    ContactDTO[] contactDTOs = new ContactDTO[3];
    List<ShopContact> shopContacts = writer.getShopContactsByShopId(shopId);
    if (!CollectionUtils.isEmpty(shopContacts)) {
      int size = shopContacts.size();
      if (size > 3) {
        LOG.error("shop contact list size is over 3,shopId is" + shopId);
        size = 3;
      }
      for (int i = 0; i < size; i++) {
        contactDTOs[i] = shopContacts.get(i).toDTO();
      }
    }
    return contactDTOs;
  }

   public List<ShopBusinessScopeDTO> getShopBusinessScopeByShopId(Set<Long> shopIdSet) {
     ConfigWriter writer = configDaoManager.getWriter();

     List<ShopBusinessScopeDTO> shopBusinessScopeDTOList = new ArrayList<ShopBusinessScopeDTO>();
     List<ShopBusinessScope> shopBusinessScopeList = writer.getShopBusinessScopeByShopId(shopIdSet);
     if (CollectionUtils.isEmpty(shopBusinessScopeList)) {
       return shopBusinessScopeDTOList;
     }
     for (ShopBusinessScope scope : shopBusinessScopeList) {
       shopBusinessScopeDTOList.add(scope.toDTO());
     }
     return shopBusinessScopeDTOList;
   }

  @Override
   public List<Long> getShopBusinessScopeProductCategoryIdListByShopId(Long shopId) {
     ConfigWriter writer = configDaoManager.getWriter();
     return writer.getShopBusinessScopeProductCategoryIdListByShopId(shopId);
   }

  @Override
  public void saveShopBusinessScopeProductCategory(List<ShopBusinessScope> shopBusinessScopeList) {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
        for(ShopBusinessScope shopBusinessScope: shopBusinessScopeList){
          writer.save(shopBusinessScope);
        }
        writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }


   /**
   * 批量保存店铺的单位 注册时填写商品保存单位 专用
   * @param shopId
   * @param productDTOs
   * @throws Exception
   */
   @Override
   public void saveOrUpdateUnitSort(Long shopId, ProductDTO[] productDTOs) throws Exception {
       if (ArrayUtil.isEmpty(productDTOs)) {
         return;
       }
       List<ShopUnitDTO> shopUnitDTOs = getShopUnit(shopId);
       if (CollectionUtils.isEmpty(shopUnitDTOs)) {
         initShopUnit(shopId);
       }

       if (!BcgogoConcurrentController.lock(ConcurrentScene.UPDATE_UNIT_SORT, shopId)) {
         return;
       }
       ConfigWriter writer = configDaoManager.getWriter();
       Object status = writer.begin();
       Long editTime = System.currentTimeMillis();
       try {
         for (ProductDTO productDTO : productDTOs) {
           String unitName = productDTO.getSellUnit();
           if (StringUtils.isNotEmpty(unitName)) {
             ShopUnit shopUnit = writer.getShopUnitByUnitName(shopId, unitName);
             if (shopUnit != null) {
               shopUnit.setLastEditTime(editTime);
               writer.update(shopUnit);
               editTime -= 10;
             } else {
               shopUnit = new ShopUnit();
               shopUnit.setLastEditTime(editTime);
               shopUnit.setUnitName(unitName);
               shopUnit.setShopId(shopId);
               writer.save(shopUnit);
               editTime -= 10;
             }
           } else {
             continue;
           }
         }
         writer.commit(status);
       } finally {
         writer.rollback(status);
         BcgogoConcurrentController.release(ConcurrentScene.UPDATE_UNIT_SORT, shopId);
       }
   }

    /**
    * 注册时填写经营范围 保存经营范围
    * @param shopDTO
    */
    public void saveShopBusinessScopeFromShopDTO(ShopDTO shopDTO) {
      if (shopDTO == null || shopDTO.getId() == null || StringUtil.isEmpty(shopDTO.getThirdCategoryIdStr())) {
        return;
      }
      Long shopId = shopDTO.getId();
      String[] thirdCategoryIds = shopDTO.getThirdCategoryIdStr().split(",");

      if (ArrayUtil.isEmpty(thirdCategoryIds)) {
        return;
      }
      ConfigWriter configWriter = configDaoManager.getWriter();
      Object status = configWriter.begin();

      try {
        for (String thirdCategoryId : thirdCategoryIds) {
          if (!NumberUtil.isLongNumber(thirdCategoryId)) {
            continue;
          }
          ShopBusinessScope shopBusinessScope = new ShopBusinessScope();
          shopBusinessScope.setShopId(shopId);
          shopBusinessScope.setProductCategoryId(Long.valueOf(thirdCategoryId));
          configWriter.save(shopBusinessScope);
        }
        configWriter.commit(status);
      } finally {
        configWriter.rollback(status);
      }
    }

    public List<ShopDTO> getBcgogoRecommendSupplierShop(Long shopId) {
      List<ShopDTO> shopDTOList = null;
      if (shopId != null) {
        shopDTOList = configDaoManager.getWriter().getBcgogoRecommendSupplierShop(shopId,NumberUtil.parseLongValues(getConfig("WholesalerShopVersions", ShopConstant.BC_SHOP_ID)));
        if (CollectionUtil.isEmpty(shopDTOList)) {
          return shopDTOList;
        }
        for (ShopDTO shopDTO : shopDTOList) {
          shopDTO.setAreaName(this.getShopAreaInfoByShopDTO(shopDTO));
        }
      }
      return shopDTOList;
    }


    public List<ShopDTO> getShopByIds(Long... shopId) {
      ConfigWriter writer = configDaoManager.getWriter();
      List<Shop> shopList = writer.getShopByShopId(shopId);
      List<ShopDTO> shopDTOList = new ArrayList<ShopDTO>();
      if (CollectionUtil.isNotEmpty(shopList)) {
        for (Shop shop : shopList) {
          shopDTOList.add(shop.toDTO());
        }
      }
      return shopDTOList;
    }

  @Override
  public String[] getConfigTomcatIps() {
    String tomcatIps = getConfigWithoutCache(ShopConstant.TOMCAT_IPS, ShopConstant.BC_SHOP_ID);
    if (StringUtils.isBlank(tomcatIps)) {
      return null;
    }
    String[] ips = tomcatIps.split("\\|");
    if (ips.length <= 0) {
      return null;
    }
    return ips;
  }

  @Override
  public int countBeFavoured(Long paramShopId) {
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.countBeFavoured(paramShopId);
  }

  @Override
  public List<Shop> getTodoBugfixShops() {
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.getTodoBugfixShops();
  }

  @Override
  public String getConfigSocketReceiverIp() {
    String socketReceiverIp = getConfigWithoutCache(ShopConstant.SOCKET_RECEIVER_IP, ShopConstant.BC_SHOP_ID);
    if (StringUtils.isBlank(socketReceiverIp)) {
      return null;
    }
    return socketReceiverIp;
  }

  @Override
  public List<Long> getShopByShopVersionAndArea(Long[] shopVersionId, Long province, Long city, Long region) {
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.getShopByShopVersionAndArea(shopVersionId, province, city, region);
  }

  @Override
  public String getConfigApiTomcatIps() {
    String tomcatIp = getConfigWithoutCache(ShopConstant.TOMCAT_API_IP, ShopConstant.BC_SHOP_ID);
    if (StringUtils.isBlank(tomcatIp)) {
      return null;
    }
    return tomcatIp;
  }

  public List<Long> getAdShopIds(Integer size){
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.getAdShopIds(size);
  }

  public List<ShopDTO> getRecommendShopByShopArea(Long parentId,Long province,Long city,Long region){
    ConfigWriter writer = configDaoManager.getWriter();
    List<Shop> shops=writer.getRecommendShopByShopArea(parentId,province,city,region);
    if(CollectionUtil.isEmpty(shops)) return null;
    List<ShopDTO> shopDTOs=new ArrayList<ShopDTO>();
    for(Shop shop:shops){
        if("\u0000".equals(shop.getLandline())){
        shop.setLandline(null);
      }
      ShopDTO shopDTO = shop.toDTO();
      shopDTOs.add(shopDTO);
      //设置qq
      String qq=null;
      ContactDTO[] contactDTOs = getShopContactsByShopId(shop.getId());
      if(ArrayUtil.isNotEmpty(contactDTOs)){
        for(ContactDTO contactDTO:contactDTOs){
          if(contactDTO==null) continue;
          qq=contactDTO.getQq();
          if(StringUtil.isNotEmpty(contactDTO.getQq())&&contactDTO.getMainContact()==1){
            qq=contactDTO.getQq();
            break;
          }
        }
      }
      shopDTO.setQq(qq);
    }
    return shopDTOs;
  }

  public Map<Long,Integer> countRecommendShopByShopArea(Long province,Long city,Long region,Long... parentId){
    ConfigWriter writer = configDaoManager.getWriter();
    List<Object[]> objects=writer.countRecommendShopByShopArea(province, city, region, parentId);
    if(CollectionUtil.isEmpty(objects)) return null;
    Map<Long,Integer> countMap= new HashMap<Long, Integer>();
    for(Object[] obj:objects){
      countMap.put(NumberUtil.longValue(obj[0]),NumberUtil.intValue(obj[1]));
    }
    return countMap;
  }

  public void saveWXImageLib(WXImageLib ...imageLibs){
    if(ArrayUtil.isEmpty(imageLibs)) return ;
    ConfigWriter writer = configDaoManager.getWriter();
    Object status=writer.begin();
    try{
      for(WXImageLib imageLib:imageLibs){
        writer.save(imageLib);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  public WXImageLib getWXImageLib(String name){
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.getWXImageLib(name);
  }

}
