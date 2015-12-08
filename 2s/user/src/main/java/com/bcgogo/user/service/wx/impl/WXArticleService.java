package com.bcgogo.user.service.wx.impl;

import com.bcgogo.api.WXFanDTO;
import com.bcgogo.common.Pager;
import com.bcgogo.config.model.Area;
import com.bcgogo.user.model.Customer;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.Vehicle;
import com.bcgogo.user.model.wx.*;
import com.bcgogo.user.service.wx.IWXArticleService;
import com.bcgogo.wx.WXArticleTemplateDTO;
import com.bcgogo.wx.user.WXUserDTO;
import com.bcgogo.wx.user.WXUserVehicleDTO;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-8-3
 * Time: 上午10:12
 * To change this template use File | Settings | File Templates.
 */
@Component
public class WXArticleService implements IWXArticleService {
    private static final Logger LOG = LoggerFactory.getLogger(WXArticleService.class);

  @Autowired
  private UserDaoManager userDaoManager;

  //微信素材数量获取
  @Override
  public int getCountWXArticleJob(WXArticleTemplateDTO wxArticleDTO) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getCountWXArticleJob(wxArticleDTO);

  }

  //微信素材列表
  @Override
  public List<WXArticleTemplateDTO> getWXArticleJobs(WXArticleTemplateDTO wxArticleDTO, Pager pager) {
    UserWriter writer = userDaoManager.getWriter();
    //根据shopID分页查询
    List<WXArticleTemplate> wxArticleJobs = writer.getWXArticleJobs(wxArticleDTO, pager);
    if (CollectionUtils.isEmpty(wxArticleJobs))
      return null;
    List<WXArticleTemplateDTO> wxArticleJobDTOs = new ArrayList<WXArticleTemplateDTO>();
    for (WXArticleTemplate wxArticleTemplate : wxArticleJobs) {
      if (wxArticleTemplate == null) continue;            //wxArticleTemplate为空跳过
      WXArticleTemplateDTO wxArticleTemplateDTO = wxArticleTemplate.toDTO();
      wxArticleJobDTOs.add(wxArticleTemplateDTO);
    }
    return wxArticleJobDTOs;
  }

  //删除微信素材（逻辑删除）
  public void deleteWeChat(WXArticleTemplate wxArticleTemplate){
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.update(wxArticleTemplate);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }



  //添加微信素材
  public void saveWeChat(WXArticleTemplate wxArticle){
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.saveOrUpdate(wxArticle);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  //修改微信素材
  public void modifyWeChat(WXArticleTemplate wxArticle){
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.update(wxArticle);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }



  //根据id查找微信素材模板
  @Override
  public WXArticleTemplate getWXArticleTemplateById(String id){
    UserWriter writer = userDaoManager.getWriter();
    WXArticleTemplate wxArticleTemplate = writer.getById(WXArticleTemplate.class,Long.valueOf(id)) ;
    return wxArticleTemplate;
  }

  //根据openId查找待WeUser
  @Override
  public WXUser getWeUserByOpenId(String openId){
    WXUser wxUser = new WXUser();
    UserWriter writer = userDaoManager.getWriter();
    if(writer.getWXUserByOpenId(openId).size()>0&&null!=writer.getWXUserByOpenId(openId)){
      wxUser  = writer.getWXUserByOpenId(openId).get(0);
    }
    return wxUser;
  }


  //微信WXUserVehicle数量获取
  @Override
  public int getCountWXUserVehicleJob(WXUserDTO wXUserDTO) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getCountWXUserVehicleJob(wXUserDTO);

  }

  //微信WXUserVehicle列表
  @Override
  public List<WXUserVehicleDTO> getWXUserVehicleJobs(WXUserDTO wXUserDTO) {
    UserWriter writer = userDaoManager.getWriter();
    List<WXUserVehicle> WXUserVehicleJobs = writer.getWXUserVehicleJobs(wXUserDTO);
    if (CollectionUtils.isEmpty(WXUserVehicleJobs))
      return null;
    List<WXUserVehicleDTO> wXUserVehicleDTOs = new ArrayList<WXUserVehicleDTO>();
    for (WXUserVehicle wXUserVehicle : WXUserVehicleJobs) {
      if (wXUserVehicle == null) continue;            //wxArticleTemplate为空跳过
      WXUserVehicleDTO wXUserVehicleDTO = wXUserVehicle.toDTO();
      wXUserVehicleDTOs.add(wXUserVehicleDTO);
    }
    return wXUserVehicleDTOs;
  }

  //根据id查找WXUserVehicle
  @Override
  public WXUserVehicle getWXUserVehicleById(String id){
    UserWriter writer = userDaoManager.getWriter();
    WXUserVehicle wXUserVehicle = writer.getById(WXUserVehicle.class,Long.valueOf(id)) ;
    return wXUserVehicle;
  }

  //修改WXUserVehicle
  public void modifyWXUserVehicle(WXUserVehicle wXUserVehicle){
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.update(wXUserVehicle);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  //添加WXUserVehicle
  public void saveWXUserVehicle(WXUserVehicle wXUserVehicle){
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.saveOrUpdate(wXUserVehicle);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  //删除WXUserVehicle
  public void deleteWXUserVehicle(WXUserVehicle wXUserVehicle){
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.update(wXUserVehicle);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  //根据no查找area
  @Override
  public Area getAreaByNo(String no){
    UserWriter writer = userDaoManager.getWriter();
    Area area = writer.getAreaByNo(no) ;
    return area;
  }


  @Override
  public WXFanDTO getWXFanDTOByLicence_no(String licence_no) {
    UserWriter writer = userDaoManager.getWriter();
    Object[] o = writer.getWXFanDTOByLicence_no(licence_no);
    WXFanDTO wXFanDTO = new WXFanDTO();
    if (o != null) {
      if(null!=o[0]){
        wXFanDTO.setLicenceNo(o[0].toString());
      }
      if(null!=o[1]){
        wXFanDTO.setModel(o[1].toString());
      }
      if(null!=o[2]){
        wXFanDTO.setBrand(o[2].toString());
      }
      if(null!=o[3]){
        wXFanDTO.setName(o[3].toString());
      }
      if(null!=o[4]){
        wXFanDTO.setMobile(o[4].toString());
      }
      if(null!=o[5]){
        wXFanDTO.setVehicleId(o[5].toString());
      }
      if(null!=o[6]){
        wXFanDTO.setCustomerId(o[6].toString());
      }
    }
    return wXFanDTO;
  }

  //根据id查找Vehicle
  @Override
  public Vehicle getVehicleByVehicleId(String vehicleId){
    UserWriter writer = userDaoManager.getWriter();
    Vehicle vehicle = writer.getById(Vehicle.class,Long.valueOf(vehicleId)) ;
    return vehicle;
  }

  //根据id查找WXUserVehicle
  @Override
  public Customer getCustomerByCustomerId(String customerId){
    UserWriter writer = userDaoManager.getWriter();
    Customer customer = writer.getById(Customer.class,Long.valueOf(customerId)) ;
    return customer;
  }


  //微信user数量获取
  @Override
  public int getCountWXUserJob(WXUserDTO wXUserDTO) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getCountWXUserJob(wXUserDTO);

  }

  //微信user列表
  @Override
  public List<WXUserDTO> getWXUserJobs(WXUserDTO wXUserDTO, Pager pager) {
    UserWriter writer = userDaoManager.getWriter();
    List<WXUser> wxUserJobs = writer.getWXUserJobs(wXUserDTO, pager);
    if (CollectionUtils.isEmpty(wxUserJobs))
      return null;
    List<WXUserDTO> wXUserDTOs = new ArrayList<WXUserDTO>();
    for (WXUser wXUser : wxUserJobs) {
      if (wXUser == null) continue;            //wxArticleTemplate为空跳过
      WXUserDTO wxUserDTO = wXUser.toDTO();
      if(wXUser!=null&& wXUser.getPublicNo()!=null){
        WXAccount wXAccount = writer.getPublicNameByPublicNo( wXUser.getPublicNo()) ;
        wxUserDTO.setPublicName(wXAccount.getName());
      }
      wXUserDTOs.add(wxUserDTO);
    }
    return wXUserDTOs;
  }

}
