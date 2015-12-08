package com.bcgogo.user;

import com.bcgogo.common.*;
import com.bcgogo.config.model.ConfigDaoManager;
import com.bcgogo.config.model.ConfigWriter;
import com.bcgogo.config.model.ShopContact;
import com.bcgogo.config.service.IRecentlyUsedDataService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.config.RecentlyUsedDataType;
import com.bcgogo.enums.notification.ContactGroupType;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.notification.SmsHelper;
import com.bcgogo.notification.dto.ContactGroupDTO;
import com.bcgogo.search.dto.CustomerSupplierSearchConditionDTO;
import com.bcgogo.search.dto.CustomerSupplierSearchResultListDTO;
import com.bcgogo.search.dto.SearchSuggestionDTO;
import com.bcgogo.search.service.user.ISearchCustomerSupplierService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.model.Contact;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.service.IContactService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.*;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
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
 * Created with IntelliJ IDEA.
 * User: terry
 * Date: 13-6-17
 * Time: 下午1:18
 * To change this template use File | Settings | File Templates.]
 * 联系人信息查询
 */

@Controller
@RequestMapping("/contact.do")
public class ContactController {

  public static final Logger LOG = LoggerFactory.getLogger(ContactController.class);

  @Autowired
  IContactService contactService;
  @RequestMapping(params = "method=getContactsByIdAndType")
  @ResponseBody
  public Object getContactsByIdAndType(HttpServletRequest request) {
    Long shopId = WebUtil.getShopId(request);
    Map<String, Object> result = new HashMap<String, Object>();
    String id = request.getParameter("id");
    String type = request.getParameter("type");
    String uuid = request.getParameter("uuid");
    if (StringUtil.isEmpty(id) || StringUtil.isEmpty(type) || StringUtil.isEmpty(uuid)) {
      LOG.error("[getContactsByIdAndType],input param is blank.");
      return result; // TODO ok?
    }
    List<Map> mapList = getContactsByIdAndType(request, id,shopId, type);
    result.put("uuid", uuid);
    result.put("data", mapList);
    return result;
  }

  /**
   * 查询数据并封装前面需要的格式
   * TODO zhuj 以后重构掉 ...　直接调用了dao...
   * @param id
   * @param type
   * @return
   */
  private List<Map> getContactsByIdAndType(HttpServletRequest request, String id,Long shopId, String type) {
    boolean isWholesaler = ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request));
    List<Map> mapList = new ArrayList<Map>();
    Map<String, String> propertyMap;
    UserWriter writer = ServiceManager.getService(UserDaoManager.class).getWriter();
    ConfigWriter configWriter = ServiceManager.getService(ConfigDaoManager.class).getWriter();
    List<Contact> contacts = new ArrayList<Contact>();
    List<ShopContact> shopContacts = new ArrayList<ShopContact>();
    if ("customer".equals(type)) {
      contacts = writer.getContactByCusOrSupOrNameOrMobile(Long.parseLong(id), null, shopId, null, null);
    } else if ("supplier".equals(type)) {
      contacts = writer.getContactByCusOrSupOrNameOrMobile(null, Long.parseLong(id), shopId, null, null);
    } else if ("shop".equals(type)) {
      shopContacts = configWriter.getShopContactsByShopId(Long.parseLong(id));
    } else {
      //on the fly
    }
    if (!CollectionUtils.isEmpty(contacts)) {
      for (Contact contact : contacts) {
        propertyMap = new HashMap<String, String>();
        propertyMap.put("id", String.valueOf(contact.getId()));
        propertyMap.put("name",contact.getName());
        propertyMap.put("mobile", contact.getMobile());
        propertyMap.put("email", contact.getEmail());
        propertyMap.put("qq", contact.getQq());
        Map<String, Object> data = new HashMap<String, Object>();
        if (isWholesaler) {
          data.put("label", "联系人:" + contact.getName() + " ");
        } else {
          data.put("label", contact.getName());
        }
        data.put("details", propertyMap);
        data.put("type", "option");  //目前只使用   option  （category）暂时不用
        mapList.add(data);
      }
    } else if (!CollectionUtils.isEmpty(shopContacts)) {
      for (ShopContact contact : shopContacts) {
        propertyMap = new HashMap<String, String>();
        propertyMap.put("id", String.valueOf(contact.getId()));
        propertyMap.put("name",contact.getName());
        propertyMap.put("mobile", contact.getMobile());
        propertyMap.put("email", contact.getEmail());
        propertyMap.put("qq", contact.getQq());
        Map<String, Object> data = new HashMap<String, Object>();
        if (isWholesaler) {
          data.put("label", "联系人:" + contact.getName() + " ");
        } else {
          data.put("label", contact.getName());
        }
        data.put("details", propertyMap);
        data.put("type", "option");  //目前只使用   option  （category）暂时不用
        mapList.add(data);
      }
    }
    return mapList;
  }

  @RequestMapping(params = "method=queryContactSuggestion")
  @ResponseBody
  public Object queryContactSuggestion(HttpServletRequest request,CustomerSupplierSearchConditionDTO searchConditionDTO){
    ISearchCustomerSupplierService searchCustomerSupplierService=ServiceManager.getService(ISearchCustomerSupplierService.class);
    try{
      searchConditionDTO.setStart(0);
      searchConditionDTO.setRows(SmsHelper.SMS_CONTACT_SUGGESTION_NUM);
      searchConditionDTO.setShopId(WebUtil.getShopId(request));
      searchConditionDTO.setSearchWord(StringUtil.toTrim(searchConditionDTO.getSearchWord()));
      searchConditionDTO.setSearchStrategies(new CustomerSupplierSearchConditionDTO.SearchStrategy[]{CustomerSupplierSearchConditionDTO.SearchStrategy.mobileNotEmpty});
      searchConditionDTO.setSearchField(CustomerSupplierSearchConditionDTO.MULTI_FIELD_TO_SINGLE);
      List<SearchSuggestionDTO> searchSuggestionDTOs=searchCustomerSupplierService.queryContactSuggestion(searchConditionDTO);
      return SmsHelper.toContactDropDownItemMap(searchSuggestionDTOs);
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return null;
    }
  }

  @RequestMapping(params = "method=querySmsContactSuggestion")
  @ResponseBody
  public Object querySmsContactSuggestion(HttpServletRequest request,String searchType,CustomerSupplierSearchConditionDTO searchConditionDTO){
    ISearchCustomerSupplierService searchCustomerSupplierService=ServiceManager.getService(ISearchCustomerSupplierService.class);
    try{
      searchConditionDTO.setStart(0);
      searchConditionDTO.setRows(SmsHelper.SMS_CONTACT_SUGGESTION_NUM);
      searchConditionDTO.setShopId(WebUtil.getShopId(request));
      searchConditionDTO.setSearchStrategies(new CustomerSupplierSearchConditionDTO.SearchStrategy[]{CustomerSupplierSearchConditionDTO.SearchStrategy.mobileNotEmpty});
      List<SearchSuggestionDTO> suggestionDTOs=searchCustomerSupplierService.queryContactSuggestion(searchConditionDTO);

      if("tokenSuggestion".equals(searchType)){
        List<Map<String,Object>> contacts=new ArrayList<Map<String,Object>>();
        if(CollectionUtil.isNotEmpty(suggestionDTOs)){
          String tValue="";
          for(SearchSuggestionDTO suggestionDTO:suggestionDTOs){
            List<String[]> entry=suggestionDTO.suggestionEntry;
            if(CollectionUtil.isNotEmpty(entry)){
//            [ { label: "Choice1", value: "value1" }, ... ]
              Map<String,Object> map = new HashMap<String, Object>();
              tValue=entry.get(2)[1];
              tValue=(StringUtil.isEmpty(tValue)?"未命名":tValue)+"<"+entry.get(3)[1]+">";
              String value = StringUtil.jointStrings(entry.get(2)[1],StringUtil.valueOf(entry.get(3)[1])," ");
              String label = StringUtil.jointStrings(entry.get(1)[1],value," ");
              map.put("label",label);
              map.put("value",value);
              map.put("editable","false");
              Map<String,String> params = new HashMap<String, String>();
              params.put("type","contact");
              params.put("contactId",entry.get(0)[1]);
              params.put("name",entry.get(2)[1]);
              params.put("mobile",entry.get(3)[1]);
              map.put("params",params);
              contacts.add(map);
            }
          }
        }
        return contacts;
      }else{
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map> dropDownList = new ArrayList<Map>();
        List<String> existsList = new ArrayList<String>();
        if (CollectionUtils.isNotEmpty(suggestionDTOs)) {
          for (SearchSuggestionDTO pssDTO : suggestionDTOs){
            Map dropDownItem = pssDTO.toStandardDropDownItemMap();
            if(!existsList.contains(dropDownItem.get("label"))){
              dropDownList.add(dropDownItem);
              existsList.add(dropDownItem.get("label").toString());
            }
          }
        }
        result.put("uuid", searchConditionDTO.getUuid());
        result.put("data", dropDownList);
        return result;
      }
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return null;
    }
  }

  @RequestMapping(params = "method=queryContact")
  @ResponseBody
  public Object queryContact(HttpServletRequest request,CustomerSupplierSearchConditionDTO searchConditionDTO){
    ISearchCustomerSupplierService searchCustomerSupplierService=ServiceManager.getService(ISearchCustomerSupplierService.class);
    try{
      if (searchConditionDTO.getStartPageNo() <= 0)
        throw new BcgogoException("parameter startPage is illegal!");
      searchConditionDTO.setMaxRows(searchConditionDTO.getMaxRows()==0?SmsHelper.SMS_CONTACT_LIST_NUM:searchConditionDTO.getMaxRows());
      searchConditionDTO.setStart((searchConditionDTO.getStartPageNo() - 1)*searchConditionDTO.getMaxRows());
      searchConditionDTO.setShopId(WebUtil.getShopId(request));
      searchConditionDTO.setSearchStrategies(new CustomerSupplierSearchConditionDTO.SearchStrategy[]{CustomerSupplierSearchConditionDTO.SearchStrategy.mobileNotEmpty});
      CustomerSupplierSearchResultListDTO resultListDTO=searchCustomerSupplierService.queryContact(searchConditionDTO);
      PagingListResult<ContactDTO> result=new PagingListResult<ContactDTO>();
      result.setResults(resultListDTO.getContactDTOList());
      result.setPager(new Pager(NumberUtil.intValue(resultListDTO.getNumFound()),searchConditionDTO.getStartPageNo(),searchConditionDTO.getMaxRows()));
      return result;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return null;
    }
  }


  @RequestMapping(params = "method=deleteAllRecentlyUsedContact")
  @ResponseBody
  public Object deleteAllRecentlyUsedContact(HttpServletRequest request){
    IRecentlyUsedDataService recentlyUsedDataService=ServiceManager.getService(IRecentlyUsedDataService.class);
    try{
      recentlyUsedDataService.deleteAllRecentlyUsedDataByType(WebUtil.getShopId(request),WebUtil.getUserId(request), RecentlyUsedDataType.USED_SMS_CONTACT);
      return new Result();
    }catch (Exception e){
      LOG.debug("/contact.do?method=deleteAllRecentlyUsedContact");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(),e);
      return null;
    }
  }

  @RequestMapping(params = "method=deleteContact")
  @ResponseBody
  public Object deleteContact(HttpServletRequest request,String [] specialIds){
    IUserService userService=ServiceManager.getService(IUserService.class);
    Result result=new Result();
    try{
      if(ArrayUtil.isEmpty(specialIds)) return result.LogErrorMsg("请选择要删除的联系人");
      Long shopId=WebUtil.getShopId(request);
      List<Long> contactIds=new ArrayList<Long>();
      for(String specialId:specialIds){
        if(StringUtil.isEmpty(specialId)){
          continue;
        }
        if(specialId.contains("_")){
          specialId=specialId.split("_")[1];
        }
        contactIds.add(NumberUtil.longValue(specialId));
      }

      List<ContactDTO> contactDTOs=userService.getContactDTOByIdFormContactVehicle(shopId,ArrayUtil.toLongArr(contactIds));
      if(CollectionUtil.isEmpty(contactDTOs)) return result.LogErrorMsg("您选择的联系人不存在，或已删除。");
      for(ContactDTO contactDTO:contactDTOs){
        if(contactDTO==null) continue;
        if(contactDTO.getCustomerId()!=null||contactDTO.getSupplierId()!=null||Boolean.TRUE.equals(contactDTO.getVehicleContactFlag())){
          return result.LogErrorMsg("只可以删除未分组的联系人。");
        }
      }
      contactService.deleteContact(shopId,ArrayUtil.toLongArr(contactIds));
      ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).deleteContactSolrIndexById(specialIds);
      return result;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return result.LogErrorMsg("网络异常。");
    }
  }

  @RequestMapping(params = "method=getContactGroupTreeNode")
  @ResponseBody
  public Object getContactGroupTreeNode(HttpServletRequest request) {
    Result result = new Result();
    try {
       IUserService userService=ServiceManager.getService(IUserService.class);
      List<ContactGroupDTO> contactGroupDTOs= SmsHelper.filterContactGroupByVersion(ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request)),userService.getContactGroup());
      if(CollectionUtil.isEmpty(contactGroupDTOs)) return null;
      CustomerSupplierSearchConditionDTO searchConditionDTO=new CustomerSupplierSearchConditionDTO();
      searchConditionDTO.setStart(0);
      searchConditionDTO.setRows(Integer.MAX_VALUE);
      searchConditionDTO.setShopId(WebUtil.getShopId(request));
      for(ContactGroupDTO groupDTO:contactGroupDTOs){
        if(groupDTO==null) continue;
        searchConditionDTO.setContactGroupType(groupDTO.getContactGroupType());

        if(ContactGroupType.mobileGroups.contains(groupDTO.getContactGroupType())){
          searchConditionDTO.setSearchStrategies(new CustomerSupplierSearchConditionDTO.SearchStrategy[]{CustomerSupplierSearchConditionDTO.SearchStrategy.mobileNotEmpty});
        }else {
          searchConditionDTO.setSearchStrategies(null);
        }
        ISearchCustomerSupplierService searchCustomerSupplierService=ServiceManager.getService(ISearchCustomerSupplierService.class);
        CustomerSupplierSearchResultListDTO resultListDTO=searchCustomerSupplierService.queryContact(searchConditionDTO);
        groupDTO.setContactDTOs(SmsHelper.preProcessContact(resultListDTO.getContactDTOList()));
      }
      result.setData(SmsHelper.toTreeNode(contactGroupDTOs));
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return result.LogErrorMsg("网络异常。");
    }
  }
}
