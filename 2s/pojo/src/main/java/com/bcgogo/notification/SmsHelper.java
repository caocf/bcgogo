package com.bcgogo.notification;

import com.bcgogo.common.Node;
import com.bcgogo.enums.CustomerStatus;
import com.bcgogo.enums.RelationTypes;
import com.bcgogo.enums.SolrIdPrefix;
import com.bcgogo.enums.notification.ContactGroupType;
import com.bcgogo.notification.dto.ContactGroupDTO;
import com.bcgogo.notification.dto.OutBoxDTO;
import com.bcgogo.notification.dto.SmsDTO;
import com.bcgogo.notification.dto.SmsJobDTO;
import com.bcgogo.search.dto.SearchSuggestionDTO;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.utils.*;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-12-19
 * Time: 下午5:07
 * To change this template use File | Settings | File Templates.
 */
public class SmsHelper {
  public static final int SMS_LIST_NUM = 10;
  public static final int SMS_TEMPLATE_LIST_NUM = 10;
  public static final int SMS_CONTACT_SUGGESTION_NUM = 10;
  public static final int SMS_CONTACT_LIST_NUM = 10;

  public static Map toSmsListFromOutBox(List<OutBoxDTO> outBoxDTOList) throws ParseException {
    Map<String,List<OutBoxDTO>> resultMap=new HashMap<String, List<OutBoxDTO>>();
    if(CollectionUtil.isEmpty(outBoxDTOList)) return resultMap;
    List<OutBoxDTO> data_today=new ArrayList<OutBoxDTO>();
    List<OutBoxDTO> data_yesterday=new ArrayList<OutBoxDTO>();
    List<OutBoxDTO> data_others=new ArrayList<OutBoxDTO>();
    resultMap.put("data_today",data_today);
    resultMap.put("data_yesterday",data_yesterday);
    resultMap.put("data_others",data_others);
    for(OutBoxDTO outBoxDTO:outBoxDTOList){
      if(outBoxDTO==null|| StringUtil.isEmpty(outBoxDTO.getSendTime())){
        continue;
      }
      if(DateUtil.isInToday(outBoxDTO.getEditDate())){
        data_today.add(outBoxDTO);
      }else if(DateUtil.isInYesterday(outBoxDTO.getEditDate())){
        data_yesterday.add(outBoxDTO);
      }else {
        data_others.add(outBoxDTO);
      }
    }
    return resultMap;
  }

  public static Map toSmsListFromSmsJob(List<SmsJobDTO> smsJobs) throws ParseException {
    Map<String,List<SmsJobDTO>> resultMap=new HashMap<String, List<SmsJobDTO>>();
    if(CollectionUtil.isEmpty(smsJobs)) return resultMap;
    List<SmsJobDTO> data_today=new ArrayList<SmsJobDTO>();
    List<SmsJobDTO> data_yesterday=new ArrayList<SmsJobDTO>();
    List<SmsJobDTO> data_others=new ArrayList<SmsJobDTO>();
    resultMap.put("data_today",data_today);
    resultMap.put("data_yesterday",data_yesterday);
    resultMap.put("data_others",data_others);
    for(SmsJobDTO smsJobDTO:smsJobs){
      if(smsJobDTO==null|| StringUtil.isEmpty(smsJobDTO.getEditDateStr())){
        continue;
      }
      Long sendTime=DateUtil.convertDateStringToDateLong(DateUtil.ALL, smsJobDTO.getEditDateStr());
      if(DateUtil.isInToday(sendTime)){
        data_today.add(smsJobDTO);
      }else if(DateUtil.isInYesterday(sendTime)){
        data_yesterday.add(smsJobDTO);
      }else {
        data_others.add(smsJobDTO);
      }
    }
    return resultMap;
  }

  public static Map toSmsListFromSmsDTO(List<SmsDTO> smsDTOs){
    Map<String,List<SmsDTO>> resultMap=new HashMap<String, List<SmsDTO>>();
    if(CollectionUtil.isEmpty(smsDTOs)) return resultMap;
    List<SmsDTO> data_today=new ArrayList<SmsDTO>();
    List<SmsDTO> data_yesterday=new ArrayList<SmsDTO>();
    List<SmsDTO> data_others=new ArrayList<SmsDTO>();
    List<SmsDTO> data_last_week=new ArrayList<SmsDTO>();
    for(SmsDTO smsDTO:smsDTOs){
      if(smsDTO==null||smsDTO.getEditDate()==null){
        continue;
      }
      if(DateUtil.isInToday(smsDTO.getEditDate())){
        data_today.add(smsDTO);
      }else if(DateUtil.isInYesterday(smsDTO.getEditDate())){
        data_yesterday.add(smsDTO);
      }else {
        data_others.add(smsDTO);
      }
    }

    if(CollectionUtil.isEmpty(data_today)&&CollectionUtil.isEmpty(data_yesterday)){
      data_others=new ArrayList<SmsDTO>();
      Long startOfLastWeek=DateUtil.getLastWeekStartTime();
      Long endOfLastWeek=DateUtil.getLastWeekEndTime();
      Long editDate=null;
      for(SmsDTO smsDTO:smsDTOs){
        if(smsDTO==null||smsDTO.getEditDate()==null){
          continue;
        }
        editDate=smsDTO.getEditDate();
        if(editDate>=startOfLastWeek&&editDate<=endOfLastWeek){
          data_last_week.add(smsDTO);
        }else {
          data_others.add(smsDTO);
        }
      }
    }
    resultMap.put("data_today",data_today);
    resultMap.put("data_yesterday",data_yesterday);
    resultMap.put("data_last_week",data_last_week);
    resultMap.put("data_others",data_others);
    return resultMap;
  }

  public static List<Map<String, String>> toContactDropDownItemMap(List<SearchSuggestionDTO> searchSuggestionDTOs) {
    if(CollectionUtil.isEmpty(searchSuggestionDTOs)) return null;
    List<Map<String, String>> result = new ArrayList<Map<String, String>>();
    List<String> nameList=new ArrayList<String>();
    String name=null;
    for(SearchSuggestionDTO suggestionDTO:searchSuggestionDTOs){
      name=suggestionDTO.suggestionEntry.get(0)[1];
      if(!nameList.contains(name)){
        nameList.add(name);
      }
    }
    for(String keyWord:nameList){
      Map<String,String> nameItem=new HashMap<String, String>();
      nameItem.put("keyWord",keyWord);
      result.add(nameItem);
    }
    return result;
  }

  public static List<ContactTreeNode> toTreeNode(List<ContactGroupDTO> contactGroupDTOs){
    if(CollectionUtil.isEmpty(contactGroupDTOs)) return new ArrayList<ContactTreeNode>();
    List<ContactTreeNode> nodeList=new ArrayList<ContactTreeNode>();
    ContactGroupDTO groupDTO=null;
    ContactDTO contactDTO=null;
    List<ContactDTO> contactDTOs=null;
    for(int i=0;i<contactGroupDTOs.size();i++){
      groupDTO=contactGroupDTOs.get(i);
      if(groupDTO==null||ContactGroupType.OTHERS.equals(groupDTO.getContactGroupType())){
        continue;
      }
      int nodeNum=CollectionUtil.isNotEmpty(groupDTO.getContactDTOs())?groupDTO.getContactDTOs().size():0;
      String groupName=contactGroupDTOs.get(i).getName()+" ("+nodeNum+")";
      ContactTreeNode groupNode= new ContactTreeNode(NumberUtil.longValue(i+1), 0L,groupName,true,false);
      groupNode.setContactGroupId(groupDTO.getId());
      groupNode.setGroupNodeFlag(true);
      groupNode.setNodeNum(nodeNum);
      groupNode.setGroupName(contactGroupDTOs.get(i).getName());
      nodeList.add(groupNode);
      contactDTOs=groupDTO.getContactDTOs();
      boolean appCustomerFlag=ContactGroupType.APP_CUSTOMER.equals(groupDTO.getContactGroupType())?true:false;
      groupNode.setAppCustomerFlag(appCustomerFlag);
      if(CollectionUtil.isNotEmpty(groupDTO.getContactDTOs())){
        for(int j=0;j<contactDTOs.size();j++){
          contactDTO=contactDTOs.get(j);
          contactDTO.setName(contactDTO.getName()==null?"未命名":contactDTO.getName());
          ContactTreeNode contactNode=new ContactTreeNode(NumberUtil.longValue(StringUtil.valueOf(i+1)+(j+1)),NumberUtil.longValue(i+1),contactDTO.getName()+"<"+(StringUtil.isNotEmpty(contactDTO.getMobile())?contactDTO.getMobile():"暂无手机")+">" , true, true);
          contactNode.setMobile(contactDTO.getMobile());
          contactNode.setSpecialIdStr(contactDTO.getSpecialIdStr());
          contactNode.setAppCustomerFlag(appCustomerFlag);
          contactNode.setsGroupType(groupDTO.getContactGroupType());
          nodeList.add(contactNode);
        }
      }
    }
    return nodeList;
  }

  public static String genSmsIndexContent(SmsDTO smsDTO){
    if(smsDTO==null) return "";
    StringBuilder sb=new StringBuilder();
    if(CollectionUtil.isNotEmpty(smsDTO.getContactDTOs())){
      List<ContactDTO> contactDTOs=smsDTO.getContactDTOs();
      for(ContactDTO contactDTO:contactDTOs){
        if(StringUtil.isNotEmpty(contactDTO.getName())){
          sb.append(contactDTO.getName()).append(",");
        }
        if(StringUtil.isNotEmpty(contactDTO.getCustomerOrSupplierName())){
          sb.append(contactDTO.getCustomerOrSupplierName()).append(",");
        }
        if(StringUtil.isNotEmpty(contactDTO.getMobile())){
          sb.append(contactDTO.getMobile()).append(",");
        }
      }
    }
    if(CollectionUtil.isNotEmpty(smsDTO.getContactGroupDTOs())){
      List<ContactGroupDTO> groupDTOs=smsDTO.getContactGroupDTOs();
      for(ContactGroupDTO groupDTO:groupDTOs){
        if(StringUtil.isNotEmpty(groupDTO.getName())){
          sb.append(groupDTO.getName()).append(",");
        }
      }
    }
    if(CollectionUtil.isNotEmpty(smsDTO.getCustomerDTOs())){
      List<CustomerDTO> customerDTOs=smsDTO.getCustomerDTOs();
      for(CustomerDTO customerDTO:customerDTOs){
        if(StringUtil.isNotEmpty(customerDTO.getName())){
          sb.append(customerDTO.getName()).append(",");
        }
      }
    }
    if(CollectionUtil.isNotEmpty(smsDTO.getSupplierDTOs())){
      List<SupplierDTO> supplierDTOs=smsDTO.getSupplierDTOs();
      for(SupplierDTO supplierDTO:supplierDTOs){
        if(StringUtil.isNotEmpty(supplierDTO.getName())){
          sb.append(supplierDTO.getName()).append(",");
        }
      }
    }
    sb.append(smsDTO.getContent());
    return sb.toString();
  }

  public static CustomerDTO genCustomerDTOFromContact(ContactDTO contactDTO){
    if(contactDTO==null) return null;
    CustomerDTO customerDTO=new CustomerDTO();
    customerDTO.setShopId(contactDTO.getShopId());
    customerDTO.setName(contactDTO.getName());
    customerDTO.setMobile(contactDTO.getMobile());
    customerDTO.setStatus(CustomerStatus.ENABLED);
    customerDTO.setRelationType(RelationTypes.UNRELATED);
    return customerDTO;
  }

  public static SupplierDTO genSupplierDTOFromContact(ContactDTO contactDTO){
    if(contactDTO==null) return null;
    SupplierDTO supplierDTO=new SupplierDTO();
    supplierDTO.setShopId(contactDTO.getShopId());
    supplierDTO.setName(contactDTO.getName());
    supplierDTO.setMobile(contactDTO.getMobile());
    supplierDTO.setStatus(CustomerStatus.ENABLED);
    supplierDTO.setRelationType(RelationTypes.UNRELATED);
    return supplierDTO;
  }

  //针对有specialId没id的contactDTO
  public static List<Long> genContactIdFromSmsDTO(SmsDTO smsDTO){
    return genContactIdFromSmsDTO(smsDTO.getContactDTOs());
  }

  public static List<Long> genContactIdFromSmsDTO(List<ContactDTO> contactDTOs){
    if(CollectionUtil.isEmpty(contactDTOs)){
      return null;
    }
    List<Long> contactIdList = new ArrayList<Long>();
    for(ContactDTO contactDTO:contactDTOs){
      if(contactDTO==null||StringUtil.isEmpty(contactDTO.getSpecialIdStr())) continue;
      Long contactId=null;
      if(contactDTO.getSpecialIdStr().contains("_")){
        contactId=NumberUtil.longValue(contactDTO.getSpecialIdStr().split("_")[1]);
      }else {
        contactId=NumberUtil.longValue(contactDTO.getSpecialIdStr());
      }
      if(contactIdList.contains(contactId)) {
        continue;
      }
      contactIdList.add(contactId);
    }
    return contactIdList;
  }

  public static boolean hasAppCustomerGroup(SmsDTO smsDTO){
    if(CollectionUtil.isEmpty(smsDTO.getContactGroupDTOs())){
      return false;
    }
    List<ContactGroupDTO> contactGroupDTOs=smsDTO.getContactGroupDTOs();
    for(ContactGroupDTO groupDTO:contactGroupDTOs){
      if(ContactGroupType.APP_CUSTOMER.getType().equals(groupDTO.getName())){
        return true;
      }
    }
    return false;
  }

  /**
   * 过滤掉手机号为空的联系人，如果联系人名为空，则使用客户或供应商的名称
   * @param contactDTOs
   * @return
   */
  public static List<ContactDTO> preProcessContact(List<ContactDTO> contactDTOs){
    if(CollectionUtil.isEmpty(contactDTOs)) return null;
    List<ContactDTO> allContactDTOs=new ArrayList<ContactDTO>();
    for(ContactDTO contactDTO:contactDTOs){
      if(contactDTO==null) continue;
      if(StringUtils.isBlank(contactDTO.getName())){
        contactDTO.setName(contactDTO.getCustomerOrSupplierName());
      }
      allContactDTOs.add(contactDTO);
    }
    for(ContactDTO contactDTO:contactDTOs){
      if(contactDTO==null||StringUtil.isEmpty(contactDTO.getSpecialIdStr())){
        continue;
      }
      String sourceFromStr=contactDTO.getSpecialIdStr().split("_")[0];
      if(SolrIdPrefix.OTHER.toString().equals(sourceFromStr)){
        contactDTO.setDataSourceFrom(SolrIdPrefix.OTHER);
      }else if(SolrIdPrefix.CUSTOMER.toString().equals(sourceFromStr)){
        contactDTO.setDataSourceFrom(SolrIdPrefix.CUSTOMER);
      }else if(SolrIdPrefix.SUPPLIER.toString().equals(sourceFromStr)){
        contactDTO.setDataSourceFrom(SolrIdPrefix.SUPPLIER);
      }else if(SolrIdPrefix.VEHICLE.toString().equals(sourceFromStr)){
        contactDTO.setDataSourceFrom(SolrIdPrefix.VEHICLE);
      }
    }
    return allContactDTOs;
  }

  public static ContactDTO getFirstHasMobileContactDTO(List<ContactDTO> contactDTOList) {
    if(CollectionUtil.isNotEmpty(contactDTOList)){
      for(ContactDTO contactDTO:contactDTOList){
        if(StringUtil.isNotEmpty(contactDTO.getMobile())){
          return contactDTO;
        }
      }
    }
    return null;
  }

  public static List<ContactGroupDTO> filterContactGroupByVersion(boolean isWholesalerVersion,List<ContactGroupDTO> groupDTOs){
    if(!isWholesalerVersion||CollectionUtil.isEmpty(groupDTOs)) return groupDTOs;
    List<ContactGroupDTO> contactGroupDTOs=new ArrayList<ContactGroupDTO>();
    for(ContactGroupDTO groupDTO:groupDTOs){
      if(ContactGroupType.MEMBER.equals(groupDTO.getContactGroupType())||ContactGroupType.APP_CUSTOMER.equals(groupDTO.getContactGroupType())){
        continue;
      }
      contactGroupDTOs.add(groupDTO);
    }
    return contactGroupDTOs;
  }

}
