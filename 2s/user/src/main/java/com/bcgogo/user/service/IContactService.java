package com.bcgogo.user.service;

import com.bcgogo.notification.dto.SmsDTO;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.model.UserWriter;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by IntelliJ IDEA.
 * User: zhuj
 * Date: 13-6-4
 * Time: 下午3:05
 * 联系人相关的服务
 */
public interface IContactService {

  /**
   * 根据客户id、供应商id、店铺id、联系人姓名查询联系人相关信息
   *
   * @param customerId
   * @param supplierId
   * @param shopId
   * @param name       联系人姓名
   * @param mobile
   * @return
   */
  public List<ContactDTO> getContactByCusOrSupOrShopIdOrName(Long customerId, Long supplierId, Long shopId, String name, String... mobile);

 ContactDTO getContactDTO(Long shopId,Long customerId,Long supplierId,String mobile);


  /**
   * 新增联系人
   *
   * @param contactDTO
   * @return
   */
  public ContactDTO saveContact(ContactDTO contactDTO);

  void saveContact(ContactDTO... contactDTOs);

  /**
   * 通过id列表查询id对应的联系人列表
   *
   * @param ids  (cutomerId or supplierIds)
   * @param type "customer" or "supplier"
   * @return
   */
  public Map<Long, List<ContactDTO>> getContactsByCustomerOrSupplierIds(List<Long> ids, String type);

  /**
   * 新增既是客户又是供应商的联系人
   * 3个参数都不能为空 否则返回null
   *
   * @param customerId
   * @param supplierId
   * @param contactDTOs
   * @return
   */
  public ContactDTO[] addContactsBelongCustomerAndSupplier(Long customerId, Long supplierId,Long shopId, ContactDTO[] contactDTOs);

  public ContactDTO[] addContactsBelongCustomerAndSupplier(UserWriter writer,Long customerId, Long supplierId,Long shopId, ContactDTO[] contactDTOs);

  ContactDTO getDefaultContactDTO(Long shopId,String mobile);

  /**
   * 更新既是供应商又是客户的联系人信息 分为3种情况
   * 1.客户的联系人列表里面 和contactDTOs的交集 更新(设置了supplierId，标示共有)
   * 2.供应商的联系人列表里面 和contactDTOs的交集 更新(设置了customerId，标示共有)
   * 3.客户、供应商共有的列表连和contactDTOs的交集 更新
   *
   * @param customerId
   * @param supplierId
   * @param contactDTOs
   * @return
   */
  public ContactDTO[] updateContactsBelongCustomerAndSupplier(Long customerId, Long supplierId,Long shopId, ContactDTO[] contactDTOs);

  public ContactDTO[] updateContactsBelongCustomerAndSupplier(UserWriter writer, Long customerId, Long supplierId,Long shopId, ContactDTO[] contactDTOs);

 Set<Long> getAppCustomerIdFromContact(Long shopId,List<Long> contactIds);

//  List<ContactDTO> generateContactAppFlag(Long shopId,List<ContactDTO> contactDTOs);

  /**
   * 更新供应商信息
   *
   * @param contactDTO
   * @return
   */
  public boolean updateContact(ContactDTO contactDTO);

  /**
   * 解绑本身关联的客户供应商的联系人信息，目前处理为 客户、供应商各自保留一份
   *
   * @param customerId
   * @param supplierId
   * @return
   */
  public boolean cancelRelatedCusSupContacts(Long customerId, Long supplierId,Long shopId);

  /**
   * 删除关联的用户下面的联系人
   * 即将关联的id置为null
   *
   * @param customerId
   * @param supplierId
   * @param type       删除客户还是供应商id
   * @return
   */
  public boolean deleteRelatedCusSupContacts(Long customerId, Long supplierId, String type, UserWriter userWriter);

  /**
   * 将单个id（客户、供应商）下的所有联系人置为不可用
   *
   * @param id
   * @param type
   * @return
   */
  public boolean disabledContactsByIdAndType(Long id, String type, UserWriter userWriter);

  public Map<Long,ContactDTO> getMainContactDTOMapByCusIds(Long... customerIds);


  void deleteContact(Long shopId,Long ... contactIds);

  List<ContactDTO> getContactsByIds(Long shopId,Long... ids);

  List<ContactDTO> getContactsByIds(Long... contactIds);

  List<Long> getOtherContactsIds(Long shopId, int start, int pageSize);

  SmsDTO saveSmsInfo(SmsDTO smsDTO);

}
