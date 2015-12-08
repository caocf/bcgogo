package com.bcgogo.user.service.solr;

import com.bcgogo.BooleanEnum;
import com.bcgogo.enums.VehicleStatus;
import com.bcgogo.search.client.SolrClientHelper;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.CustomerVehicleDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.user.service.IContactService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.IVehicleService;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.PinyinUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 14-1-22
 * Time: 下午3:12
 * To change this template use File | Settings | File Templates.
 */
@Service
public class VehicleSolrWriterService implements IVehicleSolrWriterService {
  private static final Logger LOG = LoggerFactory.getLogger(IVehicleSolrWriterService.class);

  @Override
  public void reCreateVehicleSolrIndex(Long shopId, int rows) throws Exception {
    if (shopId == null) return;
    SolrClientHelper.getVehicleSolrClient().deleteByQuery("shop_id:" + shopId + " AND doc_type:" + SolrClientHelper.BcgogoSolrDocumentType.VEHICLE_DOC_TYPE.getValue());

    int start = 0;
    while (true) {
      List<Long> vehicleIdList = ServiceManager.getService(IVehicleService.class).getVehicleIds(shopId, start, rows);
      if (CollectionUtils.isEmpty(vehicleIdList)) break;
      start += vehicleIdList.size();
      createVehicleSolrIndex(shopId, vehicleIdList.toArray(new Long[vehicleIdList.size()]));
    }
  }

  @Override
  public void createVehicleSolrIndex(Long shopId, Long... vehicleId) throws Exception {
    if (ArrayUtils.isEmpty(vehicleId)) return;
    List<VehicleDTO> vehicleDTOList = ServiceManager.getService(IUserService.class).getVehicleByIds(shopId, vehicleId);
    if (CollectionUtils.isEmpty(vehicleDTOList)) return;

    Map<Long,CustomerVehicleDTO> customerVehicleDTOMap = ServiceManager.getService(IVehicleService.class).getCustomerVehicleDTOMapByVehicleIds(vehicleId);

    Set<Long> customerIdSet = new HashSet<Long>();
    for(CustomerVehicleDTO customerVehicleDTO:customerVehicleDTOMap.values()){
      if(customerVehicleDTO.getCustomerId()!=null)
        customerIdSet.add(customerVehicleDTO.getCustomerId());
    }
    Map<Long, List<ContactDTO>> contactDTOMap= ServiceManager.getService(IContactService.class).getContactsByCustomerOrSupplierIds(new ArrayList<Long>(customerIdSet), "customer");
    Map<Long,Boolean> isAppVehicleMap = ServiceManager.getService(IVehicleService.class).isAppVehicle(vehicleId);
    Set<String> startWithSubStringSet = new HashSet<String>();
    Set<String> endWithSubStringSet = new HashSet<String>();
    Set<String> containedWithSubStringSet = new HashSet<String>();
    CustomerVehicleDTO customerVehicleDTO = null;
    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
    for (VehicleDTO vehicleDTO : vehicleDTOList) {
      if (StringUtils.isBlank(vehicleDTO.getLicenceNo())) {
        LOG.warn("vehicle id[{}] LicenceNo is null!", vehicleDTO.getId());
        continue;
      }
      customerVehicleDTO = customerVehicleDTOMap.get(vehicleDTO.getId());
      if (customerVehicleDTO==null) {
        LOG.warn("vehicle id[{}] customerVehicleDTO is null!", vehicleDTO.getId());
        continue;
      }
      SolrInputDocument doc = new SolrInputDocument();

      doc.addField("doc_type", SolrClientHelper.BcgogoSolrDocumentType.VEHICLE_DOC_TYPE.getValue());
      doc.addField("id", vehicleDTO.getId());
      doc.addField("shop_id", vehicleDTO.getShopId());
      doc.addField("licence_no", vehicleDTO.getLicenceNo());
      doc.addField("created_time", vehicleDTO.getCreatedTime());

      doc.addField("vehicle_contact",vehicleDTO.getContact());
      doc.addField("vehicle_mobile",vehicleDTO.getMobile());
      if (VehicleStatus.DISABLED.equals(vehicleDTO.getStatus())||VehicleStatus.DISABLED.equals(customerVehicleDTO.getStatus())) {
        doc.addField("vehicle_status",VehicleStatus.DISABLED.toString());
      }else{
        doc.addField("vehicle_status",VehicleStatus.ENABLED.toString());
      }

      if(StringUtils.isNotBlank(vehicleDTO.getMobile())){
        doc.addField("is_mobile_vehicle","1");
      }else{
        List<ContactDTO> contactDTOList = contactDTOMap.get(customerVehicleDTO.getCustomerId());
        if(CollectionUtils.isNotEmpty(contactDTOList)){
          for(ContactDTO contactDTO : contactDTOList){
            if(StringUtils.isNotBlank(contactDTO.getMobile())){
              doc.addField("is_mobile_vehicle","1");
              break;
            }
          }
        }
      }

      doc.addField("obd_id",vehicleDTO.getObdId());
      doc.addField("is_app", BooleanUtils.isTrue(isAppVehicleMap.get(vehicleDTO.getId())));

      doc.addField("vehicle_model",vehicleDTO.getModel());
      doc.addField("vehicle_brand",vehicleDTO.getBrand());
      doc.addField("vehicle_color",vehicleDTO.getColor());
      doc.addField("obd_mileage",vehicleDTO.getObdMileage());
      doc.addField("engine_no", vehicleDTO.getEngineNo());
      doc.addField("chassis_number", vehicleDTO.getChassisNumber());
      doc.addField("gsm_obd_imei", vehicleDTO.getGsmObdImei());
      doc.addField("gsm_obd_imei_mobile", vehicleDTO.getGsmObdImeiMoblie());

      if(customerVehicleDTO.getMaintainMileage()!=null){
        doc.addField("maintain_mileage", NumberUtil.doubleVal(customerVehicleDTO.getMaintainMileage()));
        if(vehicleDTO.getObdMileage()!=null){
          doc.addField("maintain_intervals_mileage",NumberUtil.doubleVal(customerVehicleDTO.getMaintainMileage())-vehicleDTO.getObdMileage());
        }
      }
      doc.addField("customer_id",customerVehicleDTO.getCustomerId());
      doc.addField("maintain_time",customerVehicleDTO.getMaintainTime());
      doc.addField("insure_time",customerVehicleDTO.getInsureTime());

      doc.addField("vehicle_total_consume_amount",customerVehicleDTO.getTotalConsume());
      doc.addField("vehicle_total_consume_count",customerVehicleDTO.getConsumeTimes());
      doc.addField("vehicle_last_consume_time",customerVehicleDTO.getLastExpenditureDate());
      doc.addField("last_consume_order_type",customerVehicleDTO.getLastOrderType());
      doc.addField("last_consume_order_id",customerVehicleDTO.getLastOrderId());

      startWithSubStringSet.clear();
      endWithSubStringSet.clear();
      containedWithSubStringSet.clear();
      generateStartWithString(vehicleDTO.getLicenceNo(), startWithSubStringSet, endWithSubStringSet, containedWithSubStringSet);
      doc.addField("licence_no_start", StringUtil.arrayToStr(" ", startWithSubStringSet.toArray(new String[startWithSubStringSet.size()])));
      doc.addField("licence_no_end", StringUtil.arrayToStr(" ", endWithSubStringSet.toArray(new String[endWithSubStringSet.size()])));
      doc.addField("licence_no_contained", StringUtil.arrayToStr(" ", containedWithSubStringSet.toArray(new String[containedWithSubStringSet.size()])));


      //发动机号码
      if (StringUtil.isNotEmpty(vehicleDTO.getEngineNo())) {
        startWithSubStringSet.clear();
        endWithSubStringSet.clear();
        containedWithSubStringSet.clear();
        generateStartWithString(vehicleDTO.getEngineNo(), startWithSubStringSet, endWithSubStringSet, containedWithSubStringSet);
        doc.addField("engine_no_start", StringUtil.arrayToStr(" ", startWithSubStringSet.toArray(new String[startWithSubStringSet.size()])));
        doc.addField("engine_no_end", StringUtil.arrayToStr(" ", endWithSubStringSet.toArray(new String[endWithSubStringSet.size()])));
        doc.addField("engine_no_contained", StringUtil.arrayToStr(" ", containedWithSubStringSet.toArray(new String[containedWithSubStringSet.size()])));
      }

      //车架号
      if (StringUtil.isNotEmpty(vehicleDTO.getChassisNumber())) {
        startWithSubStringSet.clear();
        endWithSubStringSet.clear();
        containedWithSubStringSet.clear();
        generateStartWithString(vehicleDTO.getChassisNumber(), startWithSubStringSet, endWithSubStringSet, containedWithSubStringSet);
        doc.addField("chassis_number_start", StringUtil.arrayToStr(" ", startWithSubStringSet.toArray(new String[startWithSubStringSet.size()])));
        doc.addField("chassis_number_end", StringUtil.arrayToStr(" ", endWithSubStringSet.toArray(new String[endWithSubStringSet.size()])));
        doc.addField("chassis_number_contained", StringUtil.arrayToStr(" ", containedWithSubStringSet.toArray(new String[containedWithSubStringSet.size()])));
      }

      //gsm obd imei
      if (StringUtil.isNotEmpty(vehicleDTO.getGsmObdImei())) {
        startWithSubStringSet.clear();
        endWithSubStringSet.clear();
        containedWithSubStringSet.clear();
        generateStartWithString(vehicleDTO.getGsmObdImei(), startWithSubStringSet, endWithSubStringSet, containedWithSubStringSet);
        doc.addField("gsm_obd_imei_start", StringUtil.arrayToStr(" ", startWithSubStringSet.toArray(new String[startWithSubStringSet.size()])));
        doc.addField("gsm_obd_imei_end", StringUtil.arrayToStr(" ", endWithSubStringSet.toArray(new String[endWithSubStringSet.size()])));
        doc.addField("gsm_obd_imei_contained", StringUtil.arrayToStr(" ", containedWithSubStringSet.toArray(new String[containedWithSubStringSet.size()])));
      }

      //gsm_obd_imei_mobile
      if (StringUtil.isNotEmpty(vehicleDTO.getGsmObdImeiMoblie())) {
        startWithSubStringSet.clear();
        endWithSubStringSet.clear();
        containedWithSubStringSet.clear();
        generateStartWithString(vehicleDTO.getGsmObdImeiMoblie(), startWithSubStringSet, endWithSubStringSet, containedWithSubStringSet);
        doc.addField("gsm_obd_imei_mobile_start", StringUtil.arrayToStr(" ", startWithSubStringSet.toArray(new String[startWithSubStringSet.size()])));
        doc.addField("gsm_obd_imei_mobile_end", StringUtil.arrayToStr(" ", endWithSubStringSet.toArray(new String[endWithSubStringSet.size()])));
        doc.addField("gsm_obd_imei_mobile_contained", StringUtil.arrayToStr(" ", containedWithSubStringSet.toArray(new String[containedWithSubStringSet.size()])));
      }

      String licenceNOFirstLetters = PinyinUtil.converterToFirstSpell(vehicleDTO.getLicenceNo());
      startWithSubStringSet.clear();
      endWithSubStringSet.clear();
      containedWithSubStringSet.clear();
      generateStartWithString(licenceNOFirstLetters, startWithSubStringSet, endWithSubStringSet, containedWithSubStringSet);
      doc.addField("licence_no_fl_start", StringUtil.arrayToStr(" ", startWithSubStringSet.toArray(new String[startWithSubStringSet.size()])).toUpperCase());
      doc.addField("licence_no_fl_end", StringUtil.arrayToStr(" ", endWithSubStringSet.toArray(new String[endWithSubStringSet.size()])).toUpperCase());
      doc.addField("licence_no_fl_contained", StringUtil.arrayToStr(" ", containedWithSubStringSet.toArray(new String[containedWithSubStringSet.size()])).toUpperCase());

      docs.add(doc);
    }
    SolrClientHelper.getVehicleSolrClient().addDocs(docs);
    LOG.debug("shopId:{}", shopId);
    LOG.debug("createVehicleSolrIndex 成功");

  }

  @Override
  public void createVehicleSolrIndexByLicenceNo(String... licenceNo) throws Exception {
    List<VehicleDTO> vehicleDTOList = ServiceManager.getService(IVehicleService.class).getVehicleDTOByLicenceNo(licenceNo);
    if(CollectionUtils.isNotEmpty(vehicleDTOList)){
      Map<Long,Set<Long>> shopVehicleMap = new HashMap<Long, Set<Long>>();
      Set<Long> vehicleIdSet = null;
      for(VehicleDTO vehicleDTO:vehicleDTOList){
        vehicleIdSet = shopVehicleMap.get(vehicleDTO.getShopId());
        if(vehicleIdSet==null){
          vehicleIdSet = new HashSet<Long>();
        }
        vehicleIdSet.add(vehicleDTO.getId());
        shopVehicleMap.put(vehicleDTO.getShopId(),vehicleIdSet);
      }
      for(Map.Entry<Long,Set<Long>> entry:shopVehicleMap.entrySet()){
        this.createVehicleSolrIndex(entry.getKey(),entry.getValue().toArray(new Long[entry.getValue().size()]));
      }
    }
  }


  private void generateStartWithString(String s, Set<String> startWithSubStringSet, Set<String> endWithSubStringSet, Set<String> containedWithSubStringSet) {
    int size = s.length();
    for (int i = 1; i <= size; i++) {
      startWithSubStringSet.add(s.substring(0, i));
      endWithSubStringSet.add(s.substring(size - i));
    }
    Set<String> exclude = new HashSet<String>();
    exclude.addAll(startWithSubStringSet);
    exclude.addAll(endWithSubStringSet);
    StringUtil.parserToSubString(s, containedWithSubStringSet, exclude);
    containedWithSubStringSet.remove(s);
    startWithSubStringSet.remove(s);
    endWithSubStringSet.remove(s);
  }
}
