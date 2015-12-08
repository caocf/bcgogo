package com.bcgogo.remind.message;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.ExeStatus;
import com.bcgogo.enums.MessageValidTimePeriod;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.enums.notification.SmsChannel;
import com.bcgogo.enums.sms.SenderType;
import com.bcgogo.enums.txn.message.MessageType;
import com.bcgogo.notification.dto.SmsJobDTO;
import com.bcgogo.notification.model.PromotionMsgJob;
import com.bcgogo.notification.service.INotificationService;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.model.Promotions;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.product.service.IPromotionsService;
import com.bcgogo.remind.dto.MessageReceiverDTO;
import com.bcgogo.remind.dto.message.MessageDTO;
import com.bcgogo.search.dto.CustomerSupplierSearchConditionDTO;
import com.bcgogo.search.dto.CustomerSupplierSearchResultListDTO;
import com.bcgogo.search.dto.ProductSearchResultListDTO;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.service.product.ISearchProductService;
import com.bcgogo.search.service.user.ISearchCustomerSupplierService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.messageCenter.IMessageService;
import com.bcgogo.txn.service.messageCenter.INoticeService;
import com.bcgogo.txn.service.solr.ISolrMergeService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.ISupplierService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Lijinlong
 * Date: 12-11-9
 * Time: 上午11:26
 */
@Controller
@RequestMapping("/message.do")
public class MessageController extends AbstractMessageController{
  private static final Logger LOG = LoggerFactory.getLogger(MessageController.class);


  @RequestMapping(params = "method=selectCustomer")
  @ResponseBody
  public Object selectCustomer(HttpServletRequest request, CustomerSupplierSearchConditionDTO customerSupplierSearchConditionDTO, Integer startPageNo, Integer maxRows) throws Exception {
    ISearchCustomerSupplierService searchCustomerSupplierService = ServiceManager.getService(ISearchCustomerSupplierService.class);
    try {
      customerSupplierSearchConditionDTO.setCustomerOrSupplier("customer");
      customerSupplierSearchConditionDTO.setShopId(WebUtil.getShopId(request));
      customerSupplierSearchConditionDTO.setStart((startPageNo - 1) * maxRows);
      customerSupplierSearchConditionDTO.setRows(maxRows);
      customerSupplierSearchConditionDTO.setSearchStrategies(new CustomerSupplierSearchConditionDTO.SearchStrategy[]{CustomerSupplierSearchConditionDTO.SearchStrategy.customerOrSupplierShopIdNotEmpty});

      CustomerSupplierSearchResultListDTO customerSupplierSearchResultListDTO = searchCustomerSupplierService.queryCustomerWithUnknownField(customerSupplierSearchConditionDTO);
      Pager pager = new Pager(Integer.valueOf(customerSupplierSearchResultListDTO.getNumFound() + ""), startPageNo, maxRows);

      List<Object> result = new ArrayList<Object>();
      result.add(customerSupplierSearchResultListDTO);
      result.add(pager);
      return result;
    } catch (Exception e) {
      LOG.debug("/message.do");
      LOG.debug("method=selectCustomer");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug(e.getMessage(), e);
    }
    return null;
  }

  @RequestMapping(params = "method=selectProduct")
  @ResponseBody
  public Object selectProduct(HttpServletRequest request, SearchConditionDTO searchConditionDTO, Integer startPageNo, Integer maxRows) {
    try {
      ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      searchConditionDTO.setShopId(shopId);
      searchConditionDTO.setStart((startPageNo - 1) * maxRows);
      searchConditionDTO.setRows(maxRows);
      searchConditionDTO.setSalesStatus(ProductStatus.InSales);//只能选择上架商品
      if (searchConditionDTO.isEmptyOfProductInfo()) {
        searchConditionDTO.setSort("inventory_amount desc,storage_time desc");
      }
      //知道field的情况下
      ProductSearchResultListDTO productSearchResultListDTO = searchProductService.queryProductWithStdQuery(searchConditionDTO);
      //合并solr延时提交memcach中的商品
      ServiceManager.getService(ISolrMergeService.class).mergeCacheProductDTO(shopId, productSearchResultListDTO.getProducts());
      Pager pager = new Pager(Integer.valueOf(productSearchResultListDTO.getNumFound() + ""), startPageNo, maxRows);

      List<Object> result = new ArrayList<Object>();
      result.add(productSearchResultListDTO);
      result.add(pager);

      return result;
    } catch (Exception e) {
      LOG.debug("/message.do");
      LOG.debug("method=selectProduct");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug(e.getMessage(), e);
    }
    return null;
  }

  @RequestMapping(params = "method=saveMessage")
  public String saveMessage(ModelMap model, HttpServletRequest request, MessageDTO messageDTO) {
    Long shopId = WebUtil.getShopId(request);
    if (shopId == null)
      return "/remind/pushMessage/addStationMessage";

    try {
      messageDTO.setShopId(shopId);
      messageDTO.setEditDate(System.currentTimeMillis());
      messageDTO.setEditorId(WebUtil.getUserId(request));
      messageDTO.setEditor(WebUtil.getUserName(request));

      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(DateUtil.getTheDayTime());
      messageDTO.setValidDateFrom(cal.getTimeInMillis());
      MessageValidTimePeriod validTimePeriod = messageDTO.getValidTimePeriod();

      cal.add(Calendar.MONTH, validTimePeriod.getNumber());
      messageDTO.setValidDateTo(cal.getTimeInMillis());

      List<MessageReceiverDTO> messageReceiverDTOList = new ArrayList<MessageReceiverDTO>();

      StringBuffer receiveMobiles = new StringBuffer();
      if (StringUtils.isNotBlank(messageDTO.getMessageReceivers())) {
        String[] customerIds = StringUtils.split(messageDTO.getMessageReceivers(), ",");
        Set<Long> receiverIdSet = new HashSet<Long>();
        for (String customerId : customerIds) {
          if (StringUtils.isNotBlank(customerId)) {
            receiverIdSet.add(Long.valueOf(customerId));
          }
        }
        ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
        ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
        Map<Long, CustomerDTO> customerDTOMap = customerService.getCustomerByIdSet(shopId, receiverIdSet);
        Set<Long> customerShopIds = new HashSet<Long>();
        if(!customerDTOMap.isEmpty() && CollectionUtil.isNotEmpty(customerDTOMap.values())){
          for(CustomerDTO customerDTO : customerDTOMap.values()){
            if(customerDTO != null && customerDTO.getCustomerShopId() != null){
              customerShopIds.add(customerDTO.getCustomerShopId());
            }
          }
        }
        Map<Long,SupplierDTO> supplierDTOMap =  supplierService.getSupplierByNativeShopIds(WebUtil.getShopId(request), customerShopIds.toArray(new Long[customerShopIds.size()]));
        for (Long receiverId : receiverIdSet) {
          CustomerDTO customerDTO = customerDTOMap.get(receiverId);
          SupplierDTO supplierDTO = supplierDTOMap.get(customerDTO.getCustomerShopId());
          if (customerDTO != null && supplierDTO != null) {
            if (StringUtils.isNotBlank(customerDTO.getMobile())) {
              receiveMobiles.append(customerDTO.getMobile()).append(",");
            }
            MessageReceiverDTO messageReceiverDTO = new MessageReceiverDTO();
            messageReceiverDTO.setReceiverId(receiverId);
            messageReceiverDTO.setReceiverName(customerDTO.getName());
            messageReceiverDTO.setReceiverShopId(customerDTO.getCustomerShopId());

            messageReceiverDTO.setSenderName(supplierDTO.getName());
            messageReceiverDTO.setSenderId(supplierDTO.getId());
            messageReceiverDTOList.add(messageReceiverDTO);
          }
        }
      }
      messageDTO.setMessageReceiverDTOList(messageReceiverDTOList);
      messageDTO.setContentText(StringUtil.Html2Text(messageDTO.getContent()));

      messageService.saveMessage(messageDTO);

      if (messageDTO.isSmsFlag() && StringUtils.isNotBlank(StringUtil.Html2Text(messageDTO.getContent())) && receiveMobiles.length()>0) {
        //发送短信
        INotificationService notificationService = ServiceManager.getService(INotificationService.class);
        SmsJobDTO smsJobDTO = new SmsJobDTO();
        smsJobDTO.setShopId(shopId);
        smsJobDTO.setContent(StringUtil.Html2Text(messageDTO.getContent()));
        smsJobDTO.setReceiveMobile(receiveMobiles.toString());
        smsJobDTO.setStartTime(System.currentTimeMillis());
        smsJobDTO.setSmsId(System.nanoTime());
        smsJobDTO.setSender(SenderType.Shop);
        smsJobDTO.setSmsChannel(SmsChannel.MARKETING);
        ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(shopId);
        if(StringUtil.isNotEmpty(shopDTO.getShortname())){
          smsJobDTO.setShopName(shopDTO.getShortname());
        } else{
          smsJobDTO.setShopName(shopDTO.getName());
        }
        notificationService.sendSmsAsync(smsJobDTO);
        LOG.debug("站内消息发送短信:{}", smsJobDTO.getContent());
      }
    } catch (Exception e) {
      LOG.debug("/message.do");
      LOG.debug("method=sendMessage");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug(e.getMessage(), e);
    }
    return "redirect:stationMessage.do?method=showSendStationMessageList";
  }

  @RequestMapping(params = "method=savePromotionMsgJob")
  @ResponseBody
  public Result sendPromotionMsg(ModelMap model, HttpServletRequest request, MessageDTO messageDTO) {
    Result result=new Result(true);
    Long shopId = WebUtil.getShopId(request);
    try {
      messageDTO.setShopId(shopId);
      messageDTO.setEditDate(System.currentTimeMillis());
      messageDTO.setEditorId(WebUtil.getUserId(request));
      messageDTO.setEditor(WebUtil.getUserName(request));
      PromotionMsgJob job=new PromotionMsgJob();
      job.setShopId(shopId);
      job.setExeStatus(ExeStatus.READY);
      job.setUserId(WebUtil.getUserId(request));
      job.setPromotionsId(messageDTO.getPromotionsIdStr());
      Promotions promotions=CollectionUtil.getFirst(ServiceManager.getService(IPromotionsService.class).getPromotionsById(shopId, NumberUtil.toLong(messageDTO.getPromotionsIdStr())));
      if(promotions!=null){
        messageDTO.setValidDateFrom(promotions.getStartTime());
        if(promotions.getEndTime()!=null){
          messageDTO.setValidDateTo(promotions.getEndTime());
        }else{
          messageDTO.setValidTimePeriod(MessageValidTimePeriod.UNLIMITED);
        }

      }
      job.setExeTime(DateUtil.convertDateStringToDateLong(DateUtil.STANDARD,messageDTO.getSendTimeStr()));
      job.setMessageJson(JsonUtil.objectToJson(messageDTO));
      ServiceManager.getService(INotificationService.class).savePromotionMsgJob(result,job);
      result.setMsg("发送成功。");
      return result;
    } catch (Exception e) {
      LOG.debug(e.getMessage(), e);
      return null;
    }
  }



  @RequestMapping(params = "method=toCommodityQuotations")
  public String toCommodityQuotations(ModelMap modelMap, HttpServletRequest request,HttpServletResponse response, Long messageId, String productIds) {
    ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shopId can't be null.");
      MessageDTO messageDTO = messageService.getMessageById(messageId);
      if (messageDTO == null) throw new Exception("messageDTO can't be null.");

      Long messageShopId = messageDTO.getShopId();
      //messageShopId 是当前店铺 就是  自己店的  连接到库存页面
      if (WebUtil.getShopId(request).equals(messageShopId)) {
        modelMap.addAttribute("productIds", productIds);
        return "redirect:goodsindex.do?method=creategoodsindex";
      } else {
        SearchConditionDTO searchConditionDTO = new SearchConditionDTO();
        searchConditionDTO.setProductIds(productIds);
        Map<Long, SupplierDTO> supplierDTOMap = supplierService.getSupplierBySupplierShopId(WebUtil.getShopId(request), messageShopId);
        if (MapUtils.isNotEmpty(supplierDTOMap)) {
          SupplierDTO supplierDTO = CollectionUtil.getFirst(supplierDTOMap.values());
          searchConditionDTO.setWholesalerName(supplierDTO.getName());
          searchConditionDTO.setShopId(messageShopId);
        }
        modelMap.addAttribute("searchConditionDTO", searchConditionDTO);
        modelMap.addAttribute("fromSource","messageCenter");
        return "/autoaccessoryonline/commodityQuotations";
      }

    } catch (Exception e) {
      LOG.debug("/message.do");
      LOG.debug("method=toCommodityQuotations");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=validateProduct")
  @ResponseBody
  public Result validateProduct(HttpServletRequest request, Long messageId, String productIds) {
    IProductService productService = ServiceManager.getService(IProductService.class);
    Long shopId = null;
    Long userId = null;
    try {
      shopId = WebUtil.getShopId(request);
      userId = WebUtil.getUserId(request);
      if (shopId == null || messageId == null) {
        LOG.error("message.do?method=validateProduct, shopId:{}, messageId:{}", shopId, messageId);
        return new Result("验证失败", "验证失败，请重试！", false);
      }
      Set<Long> productLocalInfoIdSet = new HashSet<Long>();
      if (StringUtils.isNotBlank(productIds)) {
        String[] productIdArr = productIds.split(",");
        if (!ArrayUtils.isEmpty(productIdArr)) {
          for (String productId : productIdArr) {
            if (StringUtils.isNotBlank(productId)) {
              productLocalInfoIdSet.add(Long.parseLong(productId));
            }
          }
        }
      }
      if (CollectionUtils.isEmpty(productLocalInfoIdSet)) {
        return new Result("对不起,当前消息中没有商品!", false);
      }
      // //messageShopId 是当前店铺 就是  自己店的
      MessageDTO messageDTO = messageService.getMessageById(messageId);
      if (messageDTO == null) {
        return new Result("对不起,消息不存在!", false);
      } else {
        if (shopId.equals(messageDTO.getShopId())) {
          Map<Long, ProductDTO> productDTOMap = productService.getProductDTOMapByProductLocalInfoIds(shopId, productLocalInfoIdSet);
          List<ProductDTO> productDTOList = new ArrayList<ProductDTO>(productDTOMap.values());
          if (CollectionUtils.isNotEmpty(productDTOList)) {
            Iterator<ProductDTO> iterator = productDTOList.iterator();
            while (iterator.hasNext()) {
              ProductDTO productDTO = iterator.next();
              if (ProductStatus.DISABLED.equals(productDTO.getStatus())) {
                iterator.remove();
              }
            }
          }
          if (CollectionUtils.isNotEmpty(productDTOList)) {
            return new Result(true,"redirectToStockSearch");
          } else {
            return new Result("对不起,当前消息中的商品都已经删除!", false);
          }
        } else {
          if (productService.checkProductInSalesByProductLocalInfoId(messageDTO.getShopId(), productLocalInfoIdSet.toArray(new Long[productLocalInfoIdSet.size()]))) {
            return new Result(true,"redirectToCommodityQuotations");
          } else {
            return new Result("对不起，该消息已过期！商品已被全部下架!", false);
          }
        }
      }
    } catch (Exception e) {
      LOG.error("message.do?method=validateProduct. shopId:{}, userId:{}, messageId:{},productIds:{}", new Object[]{shopId, userId, messageId, productIds});
      LOG.error(e.getMessage(), e);
      return new Result("验证失败", "验证失败，请重试！", false);
    }
  }
}
