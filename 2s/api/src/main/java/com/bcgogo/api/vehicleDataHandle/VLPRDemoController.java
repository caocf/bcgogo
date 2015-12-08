package com.bcgogo.api.vehicleDataHandle;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.util.CameraConstant;
import com.bcgogo.api.util.StringHandleUtil;
import com.bcgogo.camera.CameraConfigDTO;
import com.bcgogo.camera.CameraDTO;
import com.bcgogo.camera.CameraRecordDTO;
import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.config.model.Camera;
import com.bcgogo.config.model.CameraShop;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.camera.ICameraService;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.enums.ConcurrentScene;
import com.bcgogo.enums.ConsumeType;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.orderEvent.WashOrderSavedEvent;
import com.bcgogo.txn.bcgogoListener.publisher.BcgogoEventPublisher;
import com.bcgogo.txn.dto.CategoryDTO;
import com.bcgogo.txn.dto.ServiceDTO;
import com.bcgogo.txn.dto.WashBeautyOrderDTO;
import com.bcgogo.txn.dto.WashBeautyOrderItemDTO;
import com.bcgogo.txn.model.Service;
import com.bcgogo.txn.service.*;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.model.Customer;
import com.bcgogo.user.model.CustomerVehicle;
import com.bcgogo.user.model.Vehicle;
import com.bcgogo.user.service.IMembersService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by XinyuQiu on 14-10-23.
 */
@Controller
@RequestMapping("/wxVehicle")
public class VLPRDemoController {
  private static final Logger LOG = LoggerFactory.getLogger(VLPRDemoController.class);

//  @ResponseBody
//  @RequestMapping(value = "/save", method = RequestMethod.POST)//,@RequestBody AlarmInfoPlate alarmInfoPlate
//  public ApiResponse save(HttpServletRequest request, HttpServletResponse response) throws Exception {
//    try {
////      String appUserNo = SessionUtil.getAppUserNo(request, response);
//      ApiResponse apiResponse = MessageCode.toApiResponse(MessageCode.ACCOUNT_SAVE_SUCCESS);
//      Enumeration enumeration = request.getParameterNames();
//      Object result = null;
//      while (enumeration != null && enumeration.hasMoreElements()){
//        result = enumeration.nextElement();
////        LOG.warn(JsonUtil.objectToJson(result));
//      }
//      AlarmInfoPlateRequest alarmInfoPlateRequest = JsonUtil.jsonToObj(result.toString(),AlarmInfoPlateRequest.class);
////      LOG.warn(JsonUtil.objectToJson(alarmInfoPlateRequest));
//      String Serialno = alarmInfoPlateRequest.getAlarmInfoPlate().getSerialno();
//      String license = alarmInfoPlateRequest.getAlarmInfoPlate().getResult().getPlateResult().getLicense();
//      StringBuilder sb = new StringBuilder();
//      sb.append("【").append(DateUtil.dateLongToStr(System.currentTimeMillis(), DateUtil.ALL)).append("】")
//          .append("【").append(Serialno).append("】")
//          .append("【").append(license).append("】");
//      LOG.warn(sb.toString());
////      ServiceManager.getService(IAppUserBillService.class)
////          .saveAppUserBill(billRequest.toAppUserBillDTO(appUserNo));
////      apiResponse.setDebug(billRequest.toString());
//      return apiResponse;
//    } catch (Exception e) {
//      LOG.error(e.getMessage(), e);
//      return MessageCode.toApiResponse(MessageCode.ACCOUNT_SAVE_EXCEPTION);
//    }
//  }

  /*
  心跳包数据处理，一分钟产生一条
   */
  @ResponseBody
  @RequestMapping(value = "/register", method = RequestMethod.POST)//,@RequestBody AlarmInfoPlate alarmInfoPlate
  public ApiResponse register(HttpServletRequest request, HttpServletResponse response) throws Exception {
    try {
      ApiResponse apiResponse = MessageCode.toApiResponse(MessageCode.ACCOUNT_SAVE_SUCCESS);
      InputStream inputStream = null;
      inputStream = request.getInputStream();
      StringWriter writer = new StringWriter();
      IOUtils.copy(inputStream, writer, "UTF-8");
      String reqContent = writer.toString();

      if (StringUtils.isNotBlank(reqContent)) {
        LOG.warn("心跳包数据:" + reqContent);
        String str =  StringHandleUtil.replaceBlank(reqContent).replace("-","").replace("\"","");
        String temp = str.substring(0,str.indexOf("ContentDisposition"));
        str = str.replace(temp,"").replace("ContentDisposition:formdata;","");

        String device_name = StringHandleUtil.getData("name=device_name(\\w+)name", str);
        String ipAddr = str.substring(str.indexOf("ipaddr")+6,str.indexOf("name=port"));
        String port =  StringHandleUtil.getData("name=port(\\w+)name",str);
        String user_name =  StringHandleUtil.getData("name=user_name(\\w+)name",str);
        String pass_wd =  StringHandleUtil.getData("name=pass_wd(\\w+)name",str);
        String serialNo =  StringHandleUtil.getData("name=serialno(\\w+)name",str);
        String channel_num =  StringHandleUtil.getData("name=channel_num(\\w+)",str);
        //待续
        //暂时控制台打印如下：
        System.out.println("心跳包预处理数据："+str);
        System.out.println("心跳包处理后的数据如下：");
        System.out.println("device_name：" + device_name);
        System.out.println("ipAddr："+ipAddr);
        System.out.println("port："+port);
        System.out.println("user_name："+user_name);
        System.out.println("pass_wd："+pass_wd);
        System.out.println("serialNo："+serialNo);
        System.out.println("channel_num："+channel_num);
        CameraDTO cameraDTONew = new CameraDTO();
        cameraDTONew.setSerial_no(serialNo);
        cameraDTONew.setLast_heart_date(formatter.format(System.currentTimeMillis()));
        cameraDTONew.setLan_ip(ipAddr);
        cameraDTONew.setLan_port(port);
        cameraDTONew.setUsername(user_name);
        cameraDTONew.setPassword(pass_wd);
     //先从缓存取camera信息，如果为null，就根据设备序列号到数据库里查询。
     // 如果查询不到，新增该摄像头信息并放到缓存中；
     // 如果查到则对比现有数据和查到的数据，不同则更新现有数据到数据库并且放到缓存中，相同则放到缓存中
     //以上数据,数据一致
        ICameraService cameraService= ServiceManager.getService(ICameraService.class);
        CameraDTO cameraDTO=(CameraDTO) MemCacheAdapter.get(CameraConstant.KEY_HEART_BEAT_DATA +serialNo);
//        System.out.println("缓存中的心跳时间："+cameraDTO.getLast_heart_date());
        if(cameraDTO == null){//缓存中找不到 cameraDTO
          Camera camera = new  Camera();
          camera.setSerial_no(serialNo);
          Camera camera_ = cameraService.getCamera(camera);
          if(camera_ == null){    //数据库查不到 camera ，新增camera到数据库并且放到缓存中
            Camera camera_new = cameraService.saveOrUpdateCameraVLPR(cameraDTONew);
            cameraDTONew.setId(camera_new.getId().toString());
            MemCacheAdapter.set(CameraConstant.KEY_HEART_BEAT_DATA+cameraDTONew.getSerial_no(),cameraDTONew,new Date(System.currentTimeMillis() + CameraConstant.M_TIME_HEART_BEAT_DATA));
          }else{  //数据库中查到camera，属性比较
            cameraDTO =  camera_.toCameraDTO();
            //比较开始
            gotAndDealCamera(cameraDTONew,cameraDTO);
          }
        }else{//缓存中找到cameraDTO
          //比较开始
          gotAndDealCamera(cameraDTONew,cameraDTO);
        }
      } else {
        LOG.error("reqContent empty");
      }
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.ACCOUNT_SAVE_EXCEPTION);
    }
  }

 /*
 报警数据(车牌号上报数据)处理，摄像头捕获到获得该数据
  */
  @ResponseBody
  @RequestMapping(value = "/alert", method = RequestMethod.POST)//,@RequestBody AlarmInfoPlate alarmInfoPlate
  public ApiResponse alert(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String lockKey="";
    try {
      ApiResponse apiResponse = MessageCode.toApiResponse(MessageCode.ACCOUNT_SAVE_SUCCESS);
      InputStream inputStream = null;
      inputStream = request.getInputStream();
      StringWriter writer = new StringWriter();
      IOUtils.copy(inputStream, writer, "UTF-8");
      String reqContent = writer.toString();
      if (StringUtils.isNotBlank(reqContent)) {
        LOG.warn("报警数据:" + reqContent);
        AlarmInfoPlateRequest alarmInfoPlateRequest = JsonUtil.jsonToObj(reqContent.toString(), AlarmInfoPlateRequest.class);
        if (alarmInfoPlateRequest != null) {
          String serialno = alarmInfoPlateRequest.getAlarmInfoPlate().getSerialno();
          String license = alarmInfoPlateRequest.getAlarmInfoPlate().getResult().getPlateResult().getLicense();
//          String serialno = "3e893e146b92f9fc";
//          String license = "苏E00001";
          if(StringUtil.isEmpty(license)) return null;
          lockKey=license;
          if (!BcgogoConcurrentController.lock(ConcurrentScene.CAMERA_VARN_DATA, lockKey)){
            return null;
          }
          StringBuilder sb = new StringBuilder();
          sb.append("【").append(DateUtil.dateLongToStr(System.currentTimeMillis(), DateUtil.ALL)).append("】")
              .append("【").append(serialno).append("】")
              .append("【").append(license).append("】");
          LOG.warn(sb.toString());
          //-----------------------------------------车牌上报数据处理开始------------------------------------------------------
          ICameraService cameraService= ServiceManager.getService(ICameraService.class);
          Camera camera_ = new  Camera();
          camera_.setSerial_no(serialno.replace("-",""));
          Camera camera  = cameraService.getCamera(camera_);
          CameraConfigDTO cameraConfigDTO = cameraService.getCameraConfigByCameraId(camera.getId().toString());
          String white_vehicle_nos =cameraConfigDTO.getWhite_vehicle_nos(); //白名单车牌获取

          if(white_vehicle_nos.indexOf(license) == -1 && StringUtils.isNotEmpty(license) && !"#无#".equals(license)){ //白名单中不存在该车牌号,并且车牌号不为空，再进行以下处理，否则不用处理
            CameraRecordDTO cameraRecordDTO=(CameraRecordDTO) MemCacheAdapter.get(CameraConstant.KEY_LICENSE_REPORT_DATA+serialno.replace("-","")+license);
            if(cameraRecordDTO==null){
              System.out.println("cameraRecordDTO为空的时候执行<<<<<<<<");
              saveLicenseReportAndMemcache(serialno.replace("-",""),license);
            }else{
              String interval_time = cameraConfigDTO.getInterval_time_warn(); //时间间隔获取
//              CameraRecord cameraRecord = cameraService.getCameraRecordByVehicle_no(license);
              long b = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_ALL, formatter.format(System.currentTimeMillis())) -DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_ALL, cameraRecordDTO.getArrive_date());
              System.out.println("间隔时间<<<<<<<<"+b);
              if(b> NumberUtil.longValue(interval_time)*60*1000){
                saveLicenseReportAndMemcache(serialno.replace("-", ""), license);
              }
            }
          }
        }
      } else {
        LOG.error("reqContent empty");
      }
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.ACCOUNT_SAVE_EXCEPTION);
    }finally {
      BcgogoConcurrentController.release(ConcurrentScene.CAMERA_VARN_DATA,lockKey);
    }
  }

  public void saveLicenseReportAndMemcache(String serialno,String license){
    ICameraService cameraService= ServiceManager.getService(ICameraService.class);
    Camera camera_ = new  Camera();
    camera_.setSerial_no(serialno);
    Camera camera  = cameraService.getCamera(camera_);
    CameraConfigDTO cameraConfigDTO = cameraService.getCameraConfigByCameraId(camera.getId().toString());
    String is_user_member_card = cameraConfigDTO.getMember_card(); //是否自动扣会员卡消费
    String generate_order_type = cameraConfigDTO.getOrder_type(); //自动生成单据类型
    String construction_project_value =cameraConfigDTO.getConstruction_project_value(); //施工项目serviceId获取
    CameraShop cameraShop =  cameraService.getCameraShop(camera.getId().toString());
    Long orderId=0L;
    if(cameraShop!=null&& 0L!=cameraShop.getShop_id()){
      if("YES".equals(generate_order_type)){
        generate_order_type = "已生成";

        //生成单据
        try {
          orderId =  saveWashBeauty(cameraShop.getShop_id(),serialno, license,is_user_member_card,construction_project_value);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }else{
        generate_order_type = "未生成";
      }

      //保存记录到cameraRecord
      //组装摄像头记录
      CameraRecordDTO cameraRecordDTO = new CameraRecordDTO();
      cameraRecordDTO.setCamera_id(camera.getId().toString());
      cameraRecordDTO.setShop_id(cameraShop.getShop_id().toString());
      cameraRecordDTO.setVehicle_no(license);
      cameraRecordDTO.setArrive_date(formatter.format(System.currentTimeMillis()));
      cameraRecordDTO.setRef_order_type(generate_order_type);
      cameraRecordDTO.setName(cameraService.getShopNameByCameraId(camera.getId()));
      cameraRecordDTO.setOrder_id(orderId);
      cameraService.saveCameraRecordDTO(cameraRecordDTO);
      MemCacheAdapter.set(CameraConstant.KEY_LICENSE_REPORT_DATA+serialno+license,cameraRecordDTO,new Date(System.currentTimeMillis() + CameraConstant.M_TIME_LICENSE_REPORT_DATA));
    }

  }

  /**
   *  生成单据
   * @param shopId
   * @param licenceNo
   * @throws Exception
   */
  public Long saveWashBeauty(Long shopId,String serialno,String licenceNo,String is_user_member_card,String construction_project_value ) throws Exception {
    ITxnService txnServiceI = ServiceManager.getService(ITxnService.class);
    RFITxnService txnService = ServiceManager.getService(RFITxnService.class);
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    List<VehicleDTO> vehicleDTOs = userService.getVehicleByLicenceNo(shopId, licenceNo);
    List<CustomerDTO> customerDTOs = userService.getCustomerByLicenceNo(shopId, licenceNo); //先根据shopId和车牌号找客户
    WashBeautyOrderDTO washBeautyOrderDTO = new WashBeautyOrderDTO();
    CustomerDTO customerDTO = null;
    VehicleDTO vehicleDTO = null;
//    if ("customer".equals(request.getParameter("type"))) {
//      if (customerDTOs != null && customerDTOs.size() > 0) {
//        customerDTO = customerDTOs.get(0);
//      }
// else if (customerId != null) {        //如果没找到再根据customerId找客户(如果customerId存在
//        customerDTO = userService.getCustomerDTOByCustomerId(customerId, shopId);
//      }
//    } else {
      if (vehicleDTOs != null && vehicleDTOs.size() > 0) {
        washBeautyOrderDTO.setVehicleDTO(vehicleDTOs.get(0));
        washBeautyOrderDTO.setVechicleId(vehicleDTOs.get(0).getId());
      }else {
        vehicleDTO = new VehicleDTO();
        vehicleDTO.setShopId(shopId);
        vehicleDTO.setLicenceNo(licenceNo);
        Vehicle vehicle =  userService.saveVehicle(vehicleDTO);
        vehicleDTO =  vehicle.toDTO();
        washBeautyOrderDTO.setVehicleDTO(vehicleDTO);
        washBeautyOrderDTO.setVechicleId(vehicleDTO.getId());
      }

      if (customerDTOs != null && customerDTOs.size() > 0) {
        customerDTO = customerDTOs.get(0);
        washBeautyOrderDTO.setCustomerId(customerDTO.getId());
      } else{
        customerDTO = new CustomerDTO();
        customerDTO.setName(licenceNo);
        customerDTO.setShopId(shopId);
        Customer customer = userService.saveCustomer(customerDTO);
        customerDTO =  customer.toDTO();
        CustomerVehicle customerVehicle = new CustomerVehicle();
        customerVehicle.setCustomerId(customerDTO.getId());
        customerVehicle.setVehicleId(washBeautyOrderDTO.getVechicleId());
        userService.saveCustomerVehicle(customerVehicle);
        ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerDTO.getId());
        washBeautyOrderDTO.setCustomerId(customerDTO.getId());
      }
//    }


//    else {
//      if ("customer".equals(request.getParameter("type"))) {
//        washBeautyOrderDTO.setBrand(request.getParameter("brand"));
//        washBeautyOrderDTO.setModel(request.getParameter("model"));
//        washBeautyOrderDTO.setVehicleContact(request.getParameter("vehicleContact"));
//        washBeautyOrderDTO.setVehicleMobile(request.getParameter("vehicleMobile"));
//        washBeautyOrderDTO.setVehicleColor(request.getParameter("vehicleColor"));
//        washBeautyOrderDTO.setVehicleChassisNo(request.getParameter("vehicleChassisNo"));
//        washBeautyOrderDTO.setVehicleEngineNo(request.getParameter("vehicleEngineNo"));
//      }
//    }
    if (customerDTO != null) {
      washBeautyOrderDTO.setCustomerDTO(customerDTO);
      CustomerRecordDTO customerRecordDTO = userService.getCustomerRecordDTOByCustomerIdAndShopId(shopId,customerDTO.getId());
      if(null != customerRecordDTO)
      {
        washBeautyOrderDTO.setTotalReturnDebt(NumberUtil.numberValue(customerRecordDTO.getTotalPayable(),0D));
      }
      else
      {
        washBeautyOrderDTO.setTotalReturnDebt(0D);
      }
      if (washBeautyOrderDTO.getCustomerId() != null) {

        MemberDTO memberDTO = membersService.getMemberByCustomerId(shopId, washBeautyOrderDTO.getCustomerId());

        if (null != memberDTO) {
          memberDTO.setStatus(membersService.getMemberStatusByMemberDTO(memberDTO));
          memberDTO.setStatusStr(memberDTO.getStatus().getStatus());
        }

        washBeautyOrderDTO.setMemberDTO(memberDTO);
        if(memberDTO!=null&&StringUtil.isNotEmpty(memberDTO.getMemberNo())){
          washBeautyOrderDTO.setAccountMemberNo(memberDTO.getMemberNo());
        }

      }
    }
//    else {
//      if ("customer".equals(request.getParameter("type"))) {
//        washBeautyOrderDTO.setCustomer(customer);
//        washBeautyOrderDTO.setMobile(mobile);
//        washBeautyOrderDTO.setLandLine(landLine);
//      } else if (null == customerDTO) {
//        if (null != customerId) {
//
//          List<CustomerVehicleDTO> customerVehicleDTOList = userService.getVehicleByCustomerId(customerId);
//          if (CollectionUtils.isEmpty(customerVehicleDTOList)) {
//            washBeautyOrderDTO.setCustomer(customer);
//            washBeautyOrderDTO.setMobile(mobile);
//            washBeautyOrderDTO.setLandLine(landLine);
//            washBeautyOrderDTO.setCustomerId(customerId);
//            MemberDTO memberDTO = membersService.getMemberByCustomerId(shopId, washBeautyOrderDTO.getCustomerId());
//            if (null != memberDTO) {
//              memberDTO.setStatus(membersService.getMemberStatusByMemberDTO(memberDTO));
//              memberDTO.setStatusStr(memberDTO.getStatus().getStatus());
//            }
//            washBeautyOrderDTO.setMemberDTO(memberDTO);
//          }
//        }
//      }
//    }
    if (washBeautyOrderDTO.getMemberDTO() != null && washBeautyOrderDTO.getMemberDTO().getMemberServiceDTOs() != null) {
      for (MemberServiceDTO memberServiceDTO : washBeautyOrderDTO.getMemberDTO().getMemberServiceDTOs()) {
        Service service = txnService.getServiceById(memberServiceDTO.getServiceId());
        if (service != null) {
          memberServiceDTO.setServiceName(service.getName());
        }
      }
    }
    washBeautyOrderDTO.setServiceDTOs(txnService.getServiceByWashBeauty(shopId, washBeautyOrderDTO.getMemberDTO()));
     if (washBeautyOrderDTO.getServiceDTOs() == null) {
      ServiceDTO[] serviceDTOs = new ServiceDTO[1];
      serviceDTOs[0] = new ServiceDTO();
      serviceDTOs[0].setName("无服务");
      washBeautyOrderDTO.setServiceDTOs(serviceDTOs);
    }
//      WashBeautyOrderItemDTO[] washBeautyOrderItemDTOs = new WashBeautyOrderItemDTO[1];
//      washBeautyOrderItemDTOs[0] = new WashBeautyOrderItemDTO();
//      washBeautyOrderItemDTOs[0].setSurplusTimes(washBeautyOrderDTO.getServiceDTOs()[0].getSurplusTimes());
//      washBeautyOrderItemDTOs[0].setPrice(washBeautyOrderDTO.getServiceDTOs()[0].getPrice());
//      washBeautyOrderDTO.setWashBeautyOrderItemDTOs(washBeautyOrderItemDTOs);
//      washBeautyOrderDTO.setTotal(washBeautyOrderDTO.getServiceDTOs()[0].getPrice());
    washBeautyOrderDTO.setLicenceNo(licenceNo);
    washBeautyOrderDTO.setSalesManDTOs(userService.getSalesManList(shopId));
    setTotalDebtAndConsume(washBeautyOrderDTO);

//    String vestDateStr = request.getParameter("vestDateStr");
//    if(StringUtil.isNotEmpty(vestDateStr)){
//      washBeautyOrderDTO.setVestDateStr(vestDateStr);
//    }else{
      washBeautyOrderDTO.setVestDateStr(DateUtil.getNowTimeStr(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN));
//    }
    washBeautyOrderDTO.setShopId(shopId);
    //添加施工项目
    Double total =0D;
    String[] serviceIds = construction_project_value.split(",");
    IWashBeautyService washBeautyService = ServiceManager.getService(IWashBeautyService.class);
    //会员卡扣费
    if("YES".equals(is_user_member_card)){
      washBeautyOrderDTO =  washBeautyService.accountMemberWithWashBeauty_camera(washBeautyOrderDTO,serviceIds);
    }else{
      WashBeautyOrderItemDTO[] washBeautyOrderItemDTOs = new WashBeautyOrderItemDTO[serviceIds.length];
      for(int i=0;i<serviceIds.length;i++){
        ServiceDTO serviceDTO = new ServiceDTO();
        serviceDTO = txnServiceI.getServiceById(Long.valueOf(serviceIds[i]));
        washBeautyOrderItemDTOs[i] = new WashBeautyOrderItemDTO();
        washBeautyOrderItemDTOs[i].fromServiceDTO(serviceDTO);
        washBeautyOrderItemDTOs[i].setConsumeTypeStr(ConsumeType.MONEY);
         if(serviceDTO!=null&&serviceDTO.getPrice()!=null){
           total+= serviceDTO.getPrice();
         }
        }
     washBeautyOrderDTO.setSettledAmount(NumberUtil.toReserve(total, NumberUtil.MONEY_PRECISION));
     washBeautyOrderDTO.setTotal(NumberUtil.toReserve(total, NumberUtil.MONEY_PRECISION));
     washBeautyOrderDTO.setWashBeautyOrderItemDTOs(washBeautyOrderItemDTOs);
    }


//
//    ServiceDTO serviceDTO = CollectionUtil.getFirst(serviceDTOs);
//    if (serviceDTO != null) {
//      WashBeautyOrderItemDTO[] washBeautyOrderItemDTOs = new WashBeautyOrderItemDTO[1];
//      CategoryDTO categoryDTO = getRfiTxnService().getCateGoryByServiceId(washBeautyOrderDTO.getShopId(), serviceDTO.getId());
//      if (categoryDTO != null) {
//        serviceDTO.setCategoryDTO(categoryDTO);
//      }
//      washBeautyOrderItemDTOs[0] = new WashBeautyOrderItemDTO();
//      washBeautyOrderItemDTOs[0].fromServiceDTO(serviceDTO);
//      washBeautyOrderItemDTOs[0].setConsumeTypeStr(ConsumeType.MONEY);
////      washBeautyOrderItemDTOs[0].setCouponType(ConsumeType.MONEY.getType());
//
//      washBeautyOrderDTO.setWashBeautyOrderItemDTOs(washBeautyOrderItemDTOs);
//      washBeautyOrderDTO.setTotal(NumberUtil.toReserve(serviceDTO.getPrice(), NumberUtil.MONEY_PRECISION));
//    }

    if (washBeautyOrderDTO != null && ArrayUtil.isEmpty(washBeautyOrderDTO.getWashBeautyOrderItemDTOs())) {
      setUseTimesMostService(washBeautyOrderDTO);
    }
    RFITxnService rfiTxnService1 = ServiceManager.getService(RFITxnService.class);
    washBeautyOrderDTO.setVestDateStr(formatter.format(System.currentTimeMillis()));
    //组装洗车单静态信息
    prepareWashBeauty(washBeautyOrderDTO);
    //单据号
    if (StringUtils.isBlank(washBeautyOrderDTO.getReceiptNo())) {
      washBeautyOrderDTO.setReceiptNo(txnServiceI.getReceiptNo(shopId, OrderTypes.WASH_BEAUTY, null));
    }
    //保存车型基本信息
    rfiTxnService1.populateWashBeautyOrderDTO(washBeautyOrderDTO);
    //处理客户信息
    rfiTxnService1.doCustomerAndVehicle(shopId, null, washBeautyOrderDTO.getCustomerId(), washBeautyOrderDTO);
    //处理施工人信息
    washBeautyService.setServiceWorks(washBeautyOrderDTO);
    //保存洗车单
    WashBeautyOrderDTO washBeautyOrderDTO1 = washBeautyService.saveWashBeautyOrder(shopId, 0L, washBeautyOrderDTO);
    //更新关联预约单的状态
    IAppointOrderService appointOrderService = ServiceManager.getService(IAppointOrderService.class);
    appointOrderService.handelAppointOrderAfterSaveWashBeauty(washBeautyOrderDTO);
    //营业统计
    BcgogoEventPublisher bcgogoEventPublisher  = new BcgogoEventPublisher();
    WashOrderSavedEvent washOrderSavedEvent = new WashOrderSavedEvent(washBeautyOrderDTO1);
    bcgogoEventPublisher.publisherWashBeautyOrderSaved(washOrderSavedEvent);
    //每新增一张单据，就要将同一个客户里面的欠款提醒的状态改为未提醒
    ServiceManager.getService(ITxnService.class).updateRemindEventStatus(washBeautyOrderDTO.getShopId(),washBeautyOrderDTO.getCustomerId(),"customer");
    //发送微信账单到车主
    ServiceManager.getService(WXTxnService.class).sendConsumeMsg(washBeautyOrderDTO);
    //发送打印命令，客户端自动打印单据
    ServiceManager.getService(IPrintService.class).sendPrintCommand(washBeautyOrderDTO.getShopId(),washBeautyOrderDTO.getId(),serialno);
//    BusinessStatDTO businessStatDTO = new BusinessStatDTO();
//    businessStatDTO.setShopId(shopId);
//    businessStatDTO.setWash(NumberUtil.doubleVal(washBeautyOrderDTO1.getSettledAmount()) + NumberUtil.doubleVal(washBeautyOrderDTO1.getDebt()));
//    long vestDate = washBeautyOrderDTO.getVestDate();
//    businessStatDTO.setStatTime(vestDate);
//    businessStatDTO.setStatYear(DateUtil.getYearByVestDate(vestDate));
//    businessStatDTO.setStatMonth(DateUtil.getMonthByVestDate(vestDate));
//    businessStatDTO.setStatDay(DateUtil.getDayByVestDate(vestDate));
//    IBusinessStatService businessStatService = ServiceManager.getService(IBusinessStatService.class);
//    businessStatService.saveBusinessStatChangeFromDTO(businessStatDTO);


    System.out.println("---------------------------------打印相关信息如下---开始-----------------------------------------");
    System.out.println("车牌号:"+washBeautyOrderDTO1.getLicenceNo());
    System.out.println("washBeautyOrderId:"+washBeautyOrderDTO1.getId());
    System.out.println("单据号:"+washBeautyOrderDTO1.getReceiptNo());
    System.out.println("vechicleId:"+washBeautyOrderDTO1.getVechicleId());
    System.out.println("customerId:"+washBeautyOrderDTO1.getCustomerId());
    System.out.println("shopId:"+washBeautyOrderDTO1.getShopId());
    System.out.println("查询单据链接:"+" http://localhost:8080/web/washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId="+washBeautyOrderDTO1.getId());
    System.out.println("---------------------------------打印相关信息如下---结束-----------------------------------------");


    return washBeautyOrderDTO1.getId();
  }


  public void setTotalDebtAndConsume(WashBeautyOrderDTO washBeautyOrderDTO) {
    if (washBeautyOrderDTO.getShopId() == null || washBeautyOrderDTO.getCustomerId() == null) {
      return;
    }

    CustomerRecordDTO customerRecordDTO = getUserService().getCustomerRecordDTOByCustomerIdAndShopId(washBeautyOrderDTO.getShopId(), washBeautyOrderDTO.getCustomerId());
    if (null != customerRecordDTO) {
      washBeautyOrderDTO.setTotalReturnDebt(NumberUtil.numberValue(customerRecordDTO.getTotalPayable(), 0D));
      washBeautyOrderDTO.setTotalReceivable(NumberUtil.numberValue(customerRecordDTO.getTotalReceivable(), 0D));
      washBeautyOrderDTO.setTotalConsume(NumberUtil.numberValue(customerRecordDTO.getTotalAmount(), 0D));
    } else {
      washBeautyOrderDTO.setTotalReturnDebt(0D);
    }
  }

  public void setUseTimesMostService(WashBeautyOrderDTO washBeautyOrderDTO) {
    if (washBeautyOrderDTO == null || ArrayUtil.isNotEmpty(washBeautyOrderDTO.getWashBeautyOrderItemDTOs()) || washBeautyOrderDTO.getShopId() == null) {
      return;
    }

    List<ServiceDTO> serviceDTOs = getTxnService().getUseTimesMostService(washBeautyOrderDTO.getShopId());
    ServiceDTO serviceDTO = CollectionUtil.getFirst(serviceDTOs);
    if (serviceDTO != null) {
      WashBeautyOrderItemDTO[] washBeautyOrderItemDTOs = new WashBeautyOrderItemDTO[1];
      CategoryDTO categoryDTO = getRfiTxnService().getCateGoryByServiceId(washBeautyOrderDTO.getShopId(), serviceDTO.getId());
      if (categoryDTO != null) {
        serviceDTO.setCategoryDTO(categoryDTO);
      }
      washBeautyOrderItemDTOs[0] = new WashBeautyOrderItemDTO();
      washBeautyOrderItemDTOs[0].fromServiceDTO(serviceDTO);
      washBeautyOrderItemDTOs[0].setConsumeTypeStr(ConsumeType.MONEY);
//      washBeautyOrderItemDTOs[0].setCouponType(ConsumeType.MONEY.getType());

      washBeautyOrderDTO.setWashBeautyOrderItemDTOs(washBeautyOrderItemDTOs);
      washBeautyOrderDTO.setTotal(NumberUtil.toReserve(serviceDTO.getPrice(), NumberUtil.MONEY_PRECISION));
    }

  }

  private IUserService userService;
  private RFITxnService rfiTxnService ;
  private ITxnService txnService;

  public ITxnService getTxnService() {
    if(txnService == null){
      txnService = ServiceManager.getService(ITxnService.class);
    }
    return txnService;
  }

  public void setTxnService(ITxnService txnService) {
    this.txnService = txnService;
  }

  public RFITxnService getRfiTxnService() {
    if(rfiTxnService == null){
      return ServiceManager.getService(RFITxnService.class);
    }
    return rfiTxnService;
  }

  public void setRfiTxnService(RFITxnService rfiTxnService) {
    this.rfiTxnService = rfiTxnService;
  }

  public IUserService getUserService() {
    if(userService == null){
      userService = ServiceManager.getService(IUserService.class);
    }
    return userService;
  }

  public void setUserService(IUserService userService) {
    this.userService = userService;
  }

  /**
   *  心跳时间保存
   * @param cameraDTONew
   * @param cameraDTO
   * @throws ParseException
   */
  public void gotAndDealCamera(CameraDTO cameraDTONew,CameraDTO cameraDTO) throws ParseException {
    ICameraService cameraService= ServiceManager.getService(ICameraService.class);
    //比较开始
    Boolean a = true;
    if(!cameraDTONew.getSerial_no().equals(cameraDTO.getSerial_no())){
      cameraDTO.setSerial_no(cameraDTONew.getSerial_no());
      a = false;
    }
    if(!cameraDTONew.getLan_ip().equals(cameraDTO.getLan_ip())){
      cameraDTO.setLan_ip(cameraDTONew.getLan_ip());
      a = false;
    }
    if(!cameraDTONew.getLan_port().equals(cameraDTO.getLan_port())){
      cameraDTO.setLan_port(cameraDTONew.getLan_port());
      a = false;
    }
    if(!cameraDTONew.getUsername().equals(cameraDTO.getUsername())){
      cameraDTO.setUsername(cameraDTONew.getUsername());
      a= false;
    }
    if (!cameraDTONew.getPassword().equals(cameraDTO.getPassword())){
      cameraDTO.setPassword(cameraDTONew.getPassword());
      a= false;
    }
    if(!a){ //对比数据有改动，更新camera到数据库，并且放到缓存
      cameraDTO.setLast_heart_date(formatter.format(System.currentTimeMillis()));
      cameraService.saveOrUpdateCameraVLPR(cameraDTO);
      MemCacheAdapter.set(CameraConstant.KEY_HEART_BEAT_DATA+cameraDTO.getSerial_no(),cameraDTO,new Date(System.currentTimeMillis() + CameraConstant.M_TIME_HEART_BEAT_DATA));
    }else{
      //没有改动 从数据库取camera，比较心跳时间，如果时间超过5分钟
//      Camera camera = new  Camera();
//      camera.setSerial_no(cameraDTONew.getSerial_no());
//      Camera camera_ = cameraService.getCamera(camera);
      long b = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_ALL, formatter.format(System.currentTimeMillis())) -
          DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_ALL, cameraDTO.getLast_heart_date());
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      String interval_time = configService.getConfig("HEART_DATA_INTERVAL_TIME", ShopConstant.BC_SHOP_ID);
      long interval_time_long = 0L;
      if(StringUtils.isNotEmpty(interval_time)){
        interval_time_long = NumberUtil.longValue(interval_time);
      }
      if(b>interval_time_long){
        //比较上次更新时间，如果超过5分钟就更新下数据库
        cameraDTO.setLast_heart_date(formatter.format(System.currentTimeMillis()));
        cameraService.saveOrUpdateCameraVLPR(cameraDTO);
        MemCacheAdapter.set(CameraConstant.KEY_HEART_BEAT_DATA+cameraDTO.getSerial_no(),cameraDTO,new Date(System.currentTimeMillis() + CameraConstant.M_TIME_HEART_BEAT_DATA));
        System.out.println("5分钟更新的时间："+formatter.format(System.currentTimeMillis())+"  数据库时间："+DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_ALL, formatter.format(System.currentTimeMillis())));
      }

    }
  }

  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private void prepareWashBeauty(WashBeautyOrderDTO washBeautyOrderDTO) throws Exception {

    //保存消费时间
    String vestDateStr = washBeautyOrderDTO.getVestDateStr();
    if (StringUtil.isNotEmpty(vestDateStr)) {
      Long vestDate = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, vestDateStr);
      washBeautyOrderDTO.setVestDate(vestDate);
    } else {
      washBeautyOrderDTO.setVestDate(System.currentTimeMillis());
      washBeautyOrderDTO.setVestDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, washBeautyOrderDTO.getVestDate()));
    }
    washBeautyOrderDTO.setEditDate(System.currentTimeMillis());
    //过滤掉空行,去掉营业分类首位空格。
    if (!ArrayUtils.isEmpty(washBeautyOrderDTO.getWashBeautyOrderItemDTOs())) {
      List<WashBeautyOrderItemDTO> washBeautyOrderItemDTOs = new ArrayList<WashBeautyOrderItemDTO>();
      for (WashBeautyOrderItemDTO w : washBeautyOrderDTO.getWashBeautyOrderItemDTOs()) {
        if (w.getServiceId() != null) {
          if (StringUtils.isNotBlank(w.getBusinessCategoryName())) {
            w.setBusinessCategoryName(w.getBusinessCategoryName().trim());
          }
          washBeautyOrderItemDTOs.add(w);
        }
      }
      washBeautyOrderDTO.setWashBeautyOrderItemDTOs(washBeautyOrderItemDTOs.toArray(new WashBeautyOrderItemDTO[washBeautyOrderItemDTOs.size()]));
    }
    if (washBeautyOrderDTO.getAfterMemberDiscountTotal() == null) {
      washBeautyOrderDTO.setAfterMemberDiscountTotal(washBeautyOrderDTO.getTotal());
    }
    //还款时间
    washBeautyOrderDTO.setRepaymentTime(DateUtil.convertDateStringToDateLong("yyyy-MM-dd", washBeautyOrderDTO.getHuankuanTime()));
    washBeautyOrderDTO.setVechicle(washBeautyOrderDTO.getLicenceNo());
  }
}
