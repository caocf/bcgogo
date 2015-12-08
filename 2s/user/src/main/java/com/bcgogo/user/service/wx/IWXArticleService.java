package com.bcgogo.user.service.wx;

import com.bcgogo.api.WXFanDTO;
import com.bcgogo.common.Pager;
import com.bcgogo.config.model.Area;
import com.bcgogo.user.model.Customer;
import com.bcgogo.user.model.Vehicle;
import com.bcgogo.user.model.wx.WXArticleTemplate;
import com.bcgogo.user.model.wx.WXUser;
import com.bcgogo.user.model.wx.WXUserVehicle;
import com.bcgogo.wx.WXArticleTemplateDTO;
import com.bcgogo.wx.user.WXUserDTO;
import com.bcgogo.wx.user.WXUserVehicleDTO;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-8-3
 * Time: 上午10:12
 * To change this template use File | Settings | File Templates.
 */
public interface IWXArticleService {
   int getCountWXArticleJob(WXArticleTemplateDTO wxArticleDTO);
   List<WXArticleTemplateDTO> getWXArticleJobs(WXArticleTemplateDTO wxArticleDTO, Pager pager);
   void saveWeChat(WXArticleTemplate wxArticle);
   WXArticleTemplate getWXArticleTemplateById(String id);

  WXUser getWeUserByOpenId(String openId);

  int getCountWXUserJob(WXUserDTO wXUserDTO);
  List<WXUserDTO> getWXUserJobs(WXUserDTO wXUserDTO, Pager pager);
  int getCountWXUserVehicleJob(WXUserDTO wXUserDTO);
  List<WXUserVehicleDTO> getWXUserVehicleJobs(WXUserDTO wXUserDTO);
  WXUserVehicle getWXUserVehicleById(String id);
  Area getAreaByNo(String no);
  WXFanDTO getWXFanDTOByLicence_no(String licence_no);
  Vehicle getVehicleByVehicleId(String vehicleId);
  Customer getCustomerByCustomerId(String customerId);
}
