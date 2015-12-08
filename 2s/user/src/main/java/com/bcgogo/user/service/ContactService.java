package com.bcgogo.user.service;

import com.bcgogo.api.AppUserDTO;
import com.bcgogo.notification.dto.SmsDTO;
import com.bcgogo.notification.dto.SmsIndexDTO;
import com.bcgogo.notification.service.INotificationService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.CustomerVehicleDTO;
import com.bcgogo.user.model.Contact;
import com.bcgogo.user.model.CustomerVehicle;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.service.app.IAppUserCustomerMatchService;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.ContactConstant;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: zhuj
 * Date: 13-6-4
 * Time: 下午3:09
 * 联系人服务
 */
@Component
public class ContactService implements IContactService {

  private static final Logger LOG = LoggerFactory.getLogger(ContactService.class);
  public static final String CUSTOMER = "customer";

  @Autowired
  private UserDaoManager userDaoManager;

  @Override
  public List<ContactDTO> getContactByCusOrSupOrShopIdOrName(Long customerId, Long supplierId, Long shopId, String name, String... mobiles) {
    List<ContactDTO> contactDTOs = new ArrayList<ContactDTO>();
    if (customerId == null && supplierId == null && shopId == null && StringUtils.isBlank(name)) {
      LOG.warn("contact service:customerId、supplierId、shopId、name is blank");
      return contactDTOs;
    }
    try {
      UserWriter userWriter = userDaoManager.getWriter();
      List<Contact> contacts = userWriter.getContactByCusOrSupOrNameOrMobile(customerId, supplierId, shopId, name, mobiles);
      if (!CollectionUtils.isEmpty(contacts)) {
        for (Contact contact : contacts) {
          contactDTOs.add(contact.toDTO());
        }
      }
      return contactDTOs;
    } catch (Exception e) {
      LOG.error("查询联系人失败,customerId:{},supplierId:{},shopId:{},name:{}", e, new Object[]{customerId, supplierId, shopId, name});
      return contactDTOs;
    }
  }

  @Override
  public ContactDTO getContactDTO(Long shopId,Long customerId,Long supplierId,String mobile) {
    if (shopId == null || supplierId == null || StringUtils.isBlank(mobile)) {
      return null;
    }
    List<ContactDTO> contactDTOs = new ArrayList<ContactDTO>();
    UserWriter userWriter = userDaoManager.getWriter();
    Contact contact=CollectionUtil.getFirst(userWriter.getContact(shopId,customerId,supplierId,mobile));
    return contact!=null?contact.toDTO():null;
  }

  @Override
  public ContactDTO saveContact(ContactDTO contactDTO) {
    if (contactDTO == null) {
      LOG.warn("saveContact,contactDTO is null");
      return null;
    }
    UserWriter userWriter = userDaoManager.getWriter();
    Object status = userWriter.begin();
    try {
      Contact contact = new Contact();
      userWriter.save(contact.fromDTO(contactDTO));
      userWriter.commit(status);
      contactDTO.setId(contact.getId());
      return contactDTO;
    } finally {
      userWriter.rollback(status);
    }
  }

  @Override
  public void saveContact(ContactDTO... contactDTOs) {
    if (ArrayUtil.isEmpty(contactDTOs)) {
      LOG.warn("contactDTO is empty!");
      return;
    }
    UserWriter userWriter = userDaoManager.getWriter();
    Object status = userWriter.begin();
    try {
      for(ContactDTO contactDTO:contactDTOs){
        Contact contact = new Contact();
        userWriter.save(contact.fromDTO(contactDTO));
        contactDTO.setId(contact.getId());
      }
      userWriter.commit(status);
    } finally {
      userWriter.rollback(status);
    }
  }

  @Override
  public Map<Long, List<ContactDTO>> getContactsByCustomerOrSupplierIds(List<Long> customerOrSupplierIds, String type) {
    Map<Long, List<ContactDTO>> resultMap = new HashMap<Long, List<ContactDTO>>();
    if(CollectionUtils.isEmpty(customerOrSupplierIds)){
      return resultMap;
    }
    UserWriter userWriter = userDaoManager.getWriter();
    Map<Long, List<Contact>> contactMap = new HashMap<Long, List<Contact>>();
    if (StringUtils.equals(type, CUSTOMER)) {
      contactMap = userWriter.getContactsByCusIds(customerOrSupplierIds);
    } else {
      contactMap = userWriter.getContactsBySupIds(customerOrSupplierIds);
    }
    if (MapUtils.isNotEmpty(contactMap)) {
      for (Long key : contactMap.keySet()) {
        List<ContactDTO> contactDTOs = new ArrayList<ContactDTO>();
        List<Contact> contactList = contactMap.get(key);
        if (!CollectionUtils.isEmpty(contactList)) {
          for (Contact contact : contactList) {
            contactDTOs.add(contact.toDTO());
          }
        }
        resultMap.put(key, contactDTOs);
      }
    }

    return resultMap;
  }

  @Override
  public List<ContactDTO> getContactsByIds(Long shopId,Long... ids) {
    List<ContactDTO> contactDTOList = new ArrayList<ContactDTO>();
    UserWriter userWriter = userDaoManager.getWriter();
    List<Contact> contactList = userWriter.getContactsByids(shopId,ids);
    if (CollectionUtils.isNotEmpty(contactList)) {
      for (Contact contact : contactList) {
        contactDTOList.add(contact.toDTO());
      }
    }
    return contactDTOList;
  }

  @Override
  public List<ContactDTO> getContactsByIds(Long... contactIds) {
    return  getContactsByIds(null,contactIds);
  }

  @Override
  public List<Long> getOtherContactsIds(Long shopId, int start, int pageSize) {
    UserWriter userWriter = userDaoManager.getWriter();
    return userWriter.getOtherContactsIds(shopId, start, pageSize);
  }

  @Override
  public ContactDTO[] addContactsBelongCustomerAndSupplier(Long customerId, Long supplierId,Long shopId, ContactDTO[] contactDTOs) {
    if (customerId == null || supplierId == null || ArrayUtils.isEmpty(contactDTOs)) {
      return null;
    }
    UserWriter userWriter = userDaoManager.getWriter();
    Object status = userWriter.begin();
    try {

      addContactsBelongCustomerAndSupplier(userWriter,customerId,supplierId,shopId,contactDTOs);
      userWriter.commit(status);
      return contactDTOs;
    } finally {
      userWriter.rollback(status);
    }
  }

  @Override
  public ContactDTO getDefaultContactDTO(Long shopId,String mobile){
    if(shopId==null|| StringUtil.isEmpty(mobile)) return null;
    List<ContactDTO> contactDTOList=getContactByCusOrSupOrShopIdOrName(null,null,shopId,null,mobile);
    if(CollectionUtil.isEmpty(contactDTOList)) return null;
    for(ContactDTO contactDTO:contactDTOList){
      if(contactDTO==null) continue;
      if(contactDTO.getCustomerId()!=null||contactDTO.getSupplierId()!=null){
        return contactDTO;
      }
    }
    return CollectionUtil.getFirst(contactDTOList);
  }

  @Override
  public ContactDTO[] addContactsBelongCustomerAndSupplier(UserWriter userWriter, Long customerId, Long supplierId, Long shopId, ContactDTO[] contactDTOs) {
    if (customerId == null || supplierId == null || ArrayUtils.isEmpty(contactDTOs)) {
      return null;
    }

    // 将原来单个的客户或者供应商的联系人置为disabled 原本为update方法的职责，移到这边
    disabledContactsByIdAndType(customerId, "customer", null);
    disabledContactsByIdAndType(supplierId, "supplier", null);

    for (int i = 0; i < contactDTOs.length; i++) {
      if (contactDTOs[i] != null && contactDTOs[i].isValidContact() && (contactDTOs[i].getId() == null || contactDTOs[i].getId() == 0L)) {
        contactDTOs[i].setCustomerId(customerId);
        contactDTOs[i].setSupplierId(supplierId);
        Contact contact = new Contact();
        contact.fromDTO(contactDTOs[i]);
        contact.setDisabled(1);
        contact.setShopId(shopId);
        userWriter.save(contact);
        contactDTOs[i] = contact.toDTO();
      }
    }
    return contactDTOs;
  }

  @Override
  public ContactDTO[] updateContactsBelongCustomerAndSupplier(Long customerId, Long supplierId,Long shopId, ContactDTO[] contactDTOs) {
    if (ArrayUtils.isEmpty(contactDTOs)) {
      return null;
    }
    UserWriter userWriter = userDaoManager.getWriter();
    Object status = userWriter.begin();
    try {
      updateContactsBelongCustomerAndSupplier(userWriter,customerId,supplierId,shopId,contactDTOs);
      userWriter.commit(status);
      return contactDTOs;
    } catch (Exception e) {
      LOG.error("updateContactsBelongCustomerAndSupplier"+e.getMessage(), e);
      return contactDTOs;
    } finally {
      userWriter.rollback(status);
    }
  }

  @Override
  public ContactDTO[] updateContactsBelongCustomerAndSupplier(UserWriter userWriter, Long customerId, Long supplierId, Long shopId, ContactDTO[] contactDTOs) {
    if (ArrayUtils.isEmpty(contactDTOs)) {
      return null;
    }
    List<Long> toBeUpdatedIds = new ArrayList<Long>();
    for (ContactDTO contactDTO : contactDTOs) {
      if (contactDTO != null && contactDTO.isValidContact() && contactDTO.getId() != null && contactDTO.getId() != 0L) {
        toBeUpdatedIds.add(contactDTO.getId());
      }
    }
    List<Contact> customerSupplierContactList = new ArrayList<Contact>();
    if (customerId != null && supplierId != null) {
      customerSupplierContactList = userWriter.getContactByCusAndSup(customerId, supplierId);
    }
    if (!CollectionUtils.isEmpty(customerSupplierContactList)) { // 已经合并了
      canMergeContactListUpdate(customerId, supplierId, contactDTOs, toBeUpdatedIds, userWriter, customerSupplierContactList);
    } /*else {
      List<Contact> contactList = new ArrayList<Contact>();
      List<Contact> customerContactList = new ArrayList<Contact>();
      List<Contact> supplierContactList = new ArrayList<Contact>();
      if (customerId != null) {
        customerContactList = userWriter.getContactByCusOrSupOrNameOrMobile(customerId, null, shopId, null, null);
      }
      boolean customerContactIsEmpty = CollectionUtils.isEmpty(customerContactList);
      if (!customerContactIsEmpty) {
        contactList.addAll(customerContactList);

      }
      if (supplierId != null) {
        supplierContactList = userWriter.getContactByCusOrSupOrNameOrMobile(null, supplierId, shopId, null, null);
      }
      boolean supplierContactIsEmpty = CollectionUtils.isEmpty(supplierContactList);
      if (!supplierContactIsEmpty) {
        contactList.addAll(supplierContactList);
      }


       *//* 如果两边联系人不同的数量>3 表示无法合并 直接更新单边
        否则更新 db中已经有的单边为双边 删除db中存在 目前页面没有的*//*


      List<Contact> diffList = diffContactList(contactList);
      boolean canMerged = diffList.size() <= 3 ; // 这个地方依赖数数据的正确性 TODO BUG BCSHOP-9603 需要判断这个用户或者供应商是否已经合并 需要查询客户或者供应商
      if (canMerged) {
        canMergeContactListUpdate(customerId, supplierId, contactDTOs, toBeUpdatedIds, userWriter, contactList);
      } else {
        // 更新customer or supplier 一边的情况（无法合并）
        if (!customerContactIsEmpty) {
          for (Contact contact : customerContactList) {
            if (!toBeUpdatedIds.contains(contact.getId())) {
              contact.setDisabled(0);
            } else {
              for (ContactDTO contactDTO : contactDTOs) {
                if (contactDTO.getId().equals(contact.getId())) { //大Long 比较实用equals
                  contactDTO.setCustomerId(customerId);
                  contact.fromDTO(contactDTO);
                  break;
                }
              }
            }
            userWriter.update(contact);
          }
        } else if (!supplierContactIsEmpty) {
          for (Contact contact : supplierContactList) {
            if (!toBeUpdatedIds.contains(contact.getId())) {
              contact.setDisabled(0);
            } else {
              for (ContactDTO contactDTO : contactDTOs) {
                if (contactDTO.getId().equals(contact.getId())) { //大Long 比较实用equals
                  contactDTO.setSupplierId(supplierId);
                  contact.fromDTO(contactDTO);
                  break;
                }
              }
            }
            userWriter.update(contact);
          }
        }
      }
    }*/
    return contactDTOs;
  }

  @Override
  public Set<Long> getAppCustomerIdFromContact(Long shopId,List<Long> contactIds){
    if(shopId==null||CollectionUtil.isEmpty(contactIds)) return null;
    List<ContactDTO> contactDTOs=getContactsByIds(shopId, ArrayUtil.toLongArr(contactIds));
    Set<Long> customerIds=new HashSet<Long>();
    if(CollectionUtil.isNotEmpty(contactDTOs)){
      for(ContactDTO contactDTO:contactDTOs){
        if(contactDTO.getCustomerId()==null) continue;
        customerIds.add(contactDTO.getCustomerId());
      }
    }
   List<CustomerVehicleDTO> customerVehicleDTOs=ServiceManager.getService(IUserService.class).getCustomerVehicleDTO(ArrayUtil.toLongArr(contactIds));
    if(CollectionUtil.isNotEmpty(customerVehicleDTOs)){
      for(CustomerVehicleDTO customerVehicleDTO:customerVehicleDTOs){
        if(customerVehicleDTO==null||customerVehicleDTO.getCustomerId()==null||customerIds.contains(customerVehicleDTO.getCustomerId())){
          continue;
        }
        customerIds.add(customerVehicleDTO.getCustomerId());
      }
    }
    if(CollectionUtil.isEmpty(customerIds)) return null;
    Map<Long, List<AppUserDTO>> appUserDTOMap= ServiceManager.getService(IAppUserCustomerMatchService.class).getAppUserMapByCustomerIds(customerIds);
    return appUserDTOMap.keySet();
  }

//  @Override
//  public List<ContactDTO> generateContactAppFlag(Long shopId,List<ContactDTO> contactDTOs){
//    if(shopId==null||CollectionUtil.isEmpty(contactDTOs)) return contactDTOs;
//    Set<Long> customerIds=new HashSet<Long>();
//    if(CollectionUtil.isNotEmpty(contactDTOs)){
//      for(ContactDTO contactDTO:contactDTOs){
//        if(contactDTO==null||contactDTO.getCustomerId()==null){
//          continue;
//        }
//        customerIds.add(contactDTO.getCustomerId());
//      }
//    }
//    if(CollectionUtil.isEmpty(customerIds)) return contactDTOs;
//    Map<Long, AppUserDTO> appUserDTOMap= ServiceManager.getService(IAppUserCustomerMatchService.class).getAppUserMapByCustomerIds(customerIds);
//    Set<Long> appCustomerIds=appUserDTOMap.keySet();
//    if(CollectionUtil.isEmpty(appCustomerIds)) return contactDTOs;
//    for(ContactDTO contactDTO:contactDTOs){
//      if(appCustomerIds.contains(contactDTO.getCustomerId())){
//        contactDTO.setAppCustomerFlag(true);
//      }
//    }
//    return contactDTOs;
//  }

  private void canMergeContactListUpdate(Long customerId, Long supplierId, ContactDTO[] contactDTOs, List<Long> toBeUpdatedIds, UserWriter userWriter, List<Contact> customerSupplierContactList) {
    for (Contact contact : customerSupplierContactList) {
      if (!toBeUpdatedIds.contains(contact.getId())) {
        contact.setDisabled(ContactConstant.DISABLED);
      } else {
        for (ContactDTO contactDTO : contactDTOs) {
          if (contactDTO.getId().equals(contact.getId())) { //大Long 比较时用equals
            contactDTO.setCustomerId(customerId);
            contactDTO.setSupplierId(supplierId);
            contact.fromDTO(contactDTO);
            break;
          }
        }
      }
      userWriter.update(contact);
    }
  }

  public List<Contact> diffContactList(List<Contact> contactList) {
    if (CollectionUtils.isEmpty(contactList)) {
      return contactList;
    }
    List<Contact> diffList = new ArrayList<Contact>();
    next:
    for (Contact contact : contactList) {
      Iterator<Contact> contactIterator = diffList.iterator();
      while (contactIterator.hasNext()) {
        Contact diffTemp = contactIterator.next();
        if (StringUtils.equals(diffTemp.getMobile(), contact.getMobile())) {
          if (StringUtils.isBlank(diffTemp.getName()) || StringUtils.equals(diffTemp.getName(), contact.getName())) { // 只要原来的用户名为空 就remove掉
            contactIterator.remove();
          } else {
            continue next;
          }
        }
      }
      diffList.add(contact);
    }
    return diffList;
  }

  /*public static final void main(String[] args) {
    List<Contact> contactList = new ArrayList<Contact>();
    Contact contact = new Contact();
    contact.setMobile("13825489786");
    contact.setName("");
    contactList.add(contact);
    Contact contact1 = new Contact();
    contact1.setMobile("13825489786");
    contact1.setName("terry");
    contactList.add(contact1);
    Contact contact2 = new Contact();
    contact2.setMobile("13825489789");
    contact2.setName("terry");
    contactList.add(contact2);
    Contact contact3 = new Contact();
    contact3.setMobile("13825489789");
    contact3.setName("terry");
    contactList.add(contact3);
    List<Contact> contactList1 = diffContactList(contactList);
    System.out.println(contactList1.size());
  }*/


  @Override
  public boolean updateContact(ContactDTO contactDTO) {
    if (contactDTO == null || contactDTO.getId() == null) {
      return false;
    }
    UserWriter userWriter = userDaoManager.getWriter();
    Object status = userWriter.begin();
    try {
      Contact contact = userWriter.getById(Contact.class, contactDTO.getId());
      if (contact == null) {
        LOG.error("通过联系人id:" + contactDTO.getId() + "，无法查询到对应的联系人信息.");
        return false;
      }
      contact.fromDTO(contactDTO);
      userWriter.save(contact);
      userWriter.commit(status);
      return true;
    } finally {
      userWriter.rollback(status);
    }
  }

  @Override
  public boolean cancelRelatedCusSupContacts(Long customerId, Long supplierId,Long shopId) {
    UserWriter userWriter = userDaoManager.getWriter();
    Object status = userWriter.begin();
    try {
      List<Contact> contactList = userWriter.getContactByCusAndSup(customerId, supplierId); // 只处理合并过的
      if (!CollectionUtils.isEmpty(contactList)) {
        filterNotRelatedContacts(contactList);
        if (!CollectionUtils.isEmpty(contactList)) {
          for (Contact contact : contactList) {
            Contact contactCopy = new Contact();
            BeanUtils.copyProperties(contact, contactCopy);
            contact.setSupplierId(null);
            userWriter.update(contact);
            contactCopy.setCustomerId(null);
            userWriter.save(contactCopy);
          }
        }
      }
      userWriter.commit(status);
    } catch (Exception e) {
      LOG.error("cancelRelatedCusSupContacts", e);
    } finally {
      userWriter.rollback(status);
    }
    return true;
  }

  @Override
  public boolean deleteRelatedCusSupContacts(Long customerId, Long supplierId, String type, UserWriter userWriter) {
    if (customerId == null || supplierId == null || StringUtils.isBlank(type)) {
      return false;
    }
    Object status = null;
    if (userWriter == null) { // 外部不存在事务
      userWriter = userDaoManager.getWriter();
      status = userWriter.begin();
    }
    try {
      List<Contact> contactList = userWriter.getContactByCusAndSup(customerId, supplierId);
      if (!CollectionUtils.isEmpty(contactList)) {
        for (Contact contact : contactList) {
          if (StringUtils.equals(type, "customer")) {
            contact.setCustomerId(null);
          } else if (StringUtils.equals(type, "supplier")) {
            contact.setSupplierId(null);
          } else if(StringUtils.equals(type, "customerAndSupplier")){
            contact.setCustomerId(null);
            contact.setSupplierId(null);
          }
          userWriter.update(contact);
        }
      }
      if (status != null) {
        userWriter.commit(status);
      }
      return true;
    } catch (Exception e) {
      LOG.error("deleteRelatedCusSupContacts,error", e);
      return false;
    } finally {
      if (status != null) {
        userWriter.rollback(status);
      }
    }
  }

  @Override
  public void deleteContact(Long shopId,Long ... contactIds){
    if(ArrayUtil.isEmpty(contactIds)) return;
    List<ContactDTO> contactDTOs=getContactsByIds(shopId,contactIds);
    UserWriter writer= userDaoManager.getWriter();
    Object status=writer.begin();
    try{
      if(CollectionUtil.isNotEmpty(contactDTOs)){
        for(ContactDTO contactDTO:contactDTOs){
          if(contactDTO==null||contactDTO.getId()==null) continue;
          writer.delete(Contact.class,contactDTO.getId());
        }
      }
      writer.commit(status);
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public boolean disabledContactsByIdAndType(Long id, String type, UserWriter userWriter) {
    if (id == null || StringUtils.isBlank(type)) {
      return false;
    }
    Object status = null;
    if (userWriter == null) { // 外部不存在事务
      userWriter = userDaoManager.getWriter();
      status = userWriter.begin();
    }
    try {
      List<Contact> contactList = new ArrayList<Contact>();
      if (StringUtils.equals("customer", type)) {
        contactList = userWriter.getContactByCusOrSupOrNameOrMobile(id, null, null, null, null);
        if (!CollectionUtils.isEmpty(contactList)) {
          filterSupplierContactList(contactList);
        }
      } else if (StringUtils.equals("supplier", type)) {
        contactList = userWriter.getContactByCusOrSupOrNameOrMobile(null, id, null, null, null);
        if (!CollectionUtils.isEmpty(contactList)) {
          filterCustomerContactList(contactList);
        }
      }
      if (!CollectionUtils.isEmpty(contactList)) {
        for (Contact contact : contactList) {
          contact.setDisabled(0);
          userWriter.update(contact);
        }
      }
      if (status != null) {
        userWriter.commit(status);
      }
      return true;
    } catch (Exception e) {
      LOG.error("disabledContactsByIdAndType,error", e);
      return false;
    } finally {
      if (status != null) {
        userWriter.rollback(status);
      }
    }
  }

  @Override
  public Map<Long, ContactDTO> getMainContactDTOMapByCusIds(Long... customerIds) {
    UserWriter userWriter = userDaoManager.getWriter();
    return userWriter.getMainContactByCusId(customerIds);
  }

  private void filterCustomerContactList(List<Contact> contacts) {
    if (org.apache.commons.collections.CollectionUtils.isEmpty(contacts)) {
      return;
    }
    Iterator<Contact> contactIterator = contacts.iterator();
    while (contactIterator.hasNext()) {
      Contact contact = contactIterator.next();
      if (contact.getCustomerId() != null && contact.getCustomerId() != 0L) {
        contactIterator.remove();
      }
    }
  }

  private void filterSupplierContactList(List<Contact> contacts) {
    if (CollectionUtils.isEmpty(contacts)) {
      return;
    }
    Iterator<Contact> contactIterator = contacts.iterator();
    while (contactIterator.hasNext()) {
      Contact contact = contactIterator.next();
      if (contact.getSupplierId() != null && contact.getSupplierId()  != 0L) {
        contactIterator.remove();
      }
    }
  }

  private void filterNotRelatedContacts(List<Contact> contacts) {
    if (!CollectionUtils.isEmpty(contacts)) {
      Iterator<Contact> iterator = contacts.iterator();
      while (iterator.hasNext()) {
        Contact contact = iterator.next();
        if (contact.getCustomerId() == null || contact.getSupplierId() == null) {
          iterator.remove();
        }
      }
    }
  }

  @Override
  public SmsDTO saveSmsInfo(SmsDTO smsDTO){
    List<ContactDTO> contactDTOs=smsDTO.getContactDTOs();
    if(smsDTO==null||CollectionUtil.isEmpty(contactDTOs)) return null;
    smsDTO.setContactDTOs(contactDTOs);
    StringBuilder sb=new StringBuilder();
    for(ContactDTO contactDTO:contactDTOs){
      if(contactDTO==null) continue;
      sb.append(contactDTO.getId()).append(",");
    }
    smsDTO.setContactIds(sb.toString().substring(0,sb.length()-1));
    INotificationService notificationService=ServiceManager.getService(INotificationService.class);
    notificationService.saveOrUpdateSms(smsDTO);
    SmsIndexDTO smsIndexDTO=new SmsIndexDTO();
    smsIndexDTO.setShopId(smsDTO.getShopId());
    smsIndexDTO.setSmsId(smsDTO.getId());
    if(CollectionUtil.isNotEmpty(contactDTOs)){
      IUserService userService=ServiceManager.getService(IUserService.class);
      List<Long> customerIds=new ArrayList<Long>();
      List<Long> supplierIds=new ArrayList<Long>();
      for(ContactDTO contactDTO:contactDTOs){
        if(contactDTO.getCustomerId()!=null){
          customerIds.add(contactDTO.getCustomerId());
        }
        if(contactDTO.getSupplierId()!=null){
          supplierIds.add(contactDTO.getSupplierId());
        }
      }
      smsDTO.setCustomerDTOs(userService.getCustomerDTOByIds(smsDTO.getShopId(),ArrayUtil.toLongArr(customerIds)));
      smsDTO.setSupplierDTOs(userService.getSupplierDTOByIds(smsDTO.getShopId(),ArrayUtil.toLongArr(supplierIds)));
    }
    smsIndexDTO.fromSmsDTO(smsDTO);
    notificationService.saveOrUpdateSmsIndex(smsIndexDTO);
    return smsDTO;
  }

}
