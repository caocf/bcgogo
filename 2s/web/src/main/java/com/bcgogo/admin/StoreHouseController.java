package com.bcgogo.admin;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.txn.dto.StoreHouseDTO;
import com.bcgogo.txn.service.IStoreHouseService;
import com.bcgogo.txn.service.StoreHouseService;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.ValidatorConstant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 * Date: 12-10-22
 * Time: 下午5:33
 * 仓库管理
 */
@Controller
@RequestMapping("/storehouse.do")
public class StoreHouseController {
  private static final Logger LOG = LoggerFactory.getLogger(StoreHouseController.class);
  private static final int DEFAULT_MAX_ROWS = 15;
  private static final int DEFAULT_START_PAGE = 1;

  @Autowired
  private IStoreHouseService storeHouseService;

  @RequestMapping(params = "method=storehouseManager")
  public String storehouseManager(HttpServletRequest request) {
    Long shopId = WebUtil.getShopId(request);
    try {
      if (shopId == null) throw new Exception("shop id is null!");
    } catch (Exception e) {
      LOG.debug("/storehouse.do");
      LOG.debug("method=storehouseManager");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return "/admin/customConfig/storehouseList";
  }

  @RequestMapping(params = "method=getStoreHouseList")
  @ResponseBody
  public Object getStoreHouseList(HttpServletRequest request,String startPageNo,String maxRows) {
    Long shopId = WebUtil.getShopId(request);
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      int count = storeHouseService.countStoreHouses(shopId);
      Pager pager = new Pager(count, NumberUtil.intValue(startPageNo,DEFAULT_START_PAGE), NumberUtil.intValue(maxRows,DEFAULT_MAX_ROWS));
      List<StoreHouseDTO> storeHouseDTOList = storeHouseService.searchStoreHouses(shopId,pager.getRowStart(), NumberUtil.intValue(maxRows,DEFAULT_MAX_ROWS));
      List<Object> result = new ArrayList<Object>();
      Map<String,Object> data=new HashMap<String,Object>();
      data.put("storeHouseData",storeHouseDTOList);
      data.put("storeHouseDataCount",count);
      result.add(data);
      result.add(pager);
      return result;
    } catch (Exception e) {
      LOG.debug("/storehouse.do");
      LOG.debug("method=getStoreHouseList");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  @RequestMapping(params = "method=getStoreHouseDTOById")
  @ResponseBody
  public Object getStoreHouseDTOById(HttpServletRequest request,Long id) {
    Long shopId = WebUtil.getShopId(request);
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      if (id == null) throw new Exception("id is null!");
      return storeHouseService.getStoreHouseDTOById(shopId,id);
    } catch (Exception e) {
      LOG.debug("/storehouse.do");
      LOG.debug("method=getStoreHouseDTOById");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  @RequestMapping(params = "method=deleteStoreHouseById")
  @ResponseBody
  public Object deleteStoreHouseById(HttpServletRequest request,Long id) {
    Long shopId = WebUtil.getShopId(request);
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      if (id == null) throw new Exception("id is null!");
      //检验能不能删除 1.必须有一个仓库 2.所有库存为0  3.关联单据必须已经是结算和作废状态
      int count = storeHouseService.countStoreHouses(shopId);
      if(count<=1){
        return new Result(ValidatorConstant.RETAIN_DEFAULT_STOREHOUSE_MSG,false);
      }
      Double allInventoryAmount = storeHouseService.sumStoreHouseAllInventoryAmountByStoreHouseId(shopId,id);
      if(allInventoryAmount>0){
        return new Result(ValidatorConstant.STOREHOUSE_INVENTORY_AMOUNT_NOT_EMPTY_MSG,false);
      }
      if(storeHouseService.checkStoreHouseUsedInProcessingOrder(shopId,id)){
        return new Result(ValidatorConstant.STOREHOUSE_USED_PROCESSING_ORDER_MSG,false);
      }
      //入库单，销售退货单,销售单，施工单，退货单页面
      storeHouseService.deleteStoreHouseById(shopId,id);
      return new Result();
    } catch (Exception e) {
      LOG.debug("/storehouse.do");
      LOG.debug("method=deleteStoreHouseById");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  @RequestMapping(params = "method=saveStoreHouse")
  @ResponseBody
  public Object saveStoreHouse(HttpServletRequest request,StoreHouseDTO storeHouseDTO) {
    Long shopId = WebUtil.getShopId(request);
    Long userId = WebUtil.getUserId(request);
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      if (userId == null) throw new Exception("user id is null!");
      storeHouseDTO.setShopId(shopId);
      storeHouseDTO.setUserId(userId);

      if (storeHouseDTO == null) throw new Exception("storeHouseDTO is null!");
      if (StringUtils.isBlank(storeHouseDTO.getName())){
        return new Result(ValidatorConstant.STOREHOUSE_NAME_NULL_MSG,false);
      }
      if(storeHouseService.checkStoreHouseExist(shopId, storeHouseDTO)){
        return new Result(ValidatorConstant.STOREHOUSE_NAME_EXIST_MSG,false);
      }
      storeHouseService.saveOrUpdateStoreHouse(storeHouseDTO);
      return new Result();
    } catch (Exception e) {
      LOG.debug("/storehouse.do");
      LOG.debug("method=deleteStoreHouseById");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  @RequestMapping(params = "method=getAllStoreHouseDTOs")
  @ResponseBody
  public Object getAllStoreHouseDTOs(HttpServletRequest request) {
    Long shopId = WebUtil.getShopId(request);
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      return storeHouseService.getAllStoreHousesByShopId(shopId);
    } catch (Exception e) {
      LOG.debug("/storehouse.do");
      LOG.debug("method=getAllStoreHouses");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }
}
