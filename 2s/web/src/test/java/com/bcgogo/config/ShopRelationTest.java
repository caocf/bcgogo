package com.bcgogo.config;

import com.bcgogo.AbstractTest;
import com.bcgogo.common.Pager;
import com.bcgogo.config.dto.ApplyShopSearchCondition;
import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.ShopRelationInviteDTO;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.enums.ObjectTypes;
import com.bcgogo.enums.OperationTypes;
import com.bcgogo.enums.RelationTypes;
import com.bcgogo.enums.shop.InviteStatus;
import com.bcgogo.enums.shop.InviteType;
import com.bcgogo.enums.shop.ShopStatus;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.utils.CollectionUtil;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-2-24
 * Time: 下午2:26
 * To change this template use File | Settings | File Templates.
 */
public class ShopRelationTest extends AbstractTest {

  @Before
  public void setUp() throws Exception {
    initServices();
  }

  @Test
  public void testSearchApplyCustomerShop()throws Exception{
    ApplyShopSearchCondition searchCondition = new ApplyShopSearchCondition();
    ShopDTO shopDTO = createRandomShop();
    shopDTO.setShopVersionId(10000010017531657L);    //汽配专业版
    shopDTO = ServiceManager.getService(IShopService.class).createShop(shopDTO);
    Long supplierShopId = shopDTO.getId();
    shopDTO = createRandomShop();
    configService.activateShop(supplierShopId, ShopStatus.REGISTERED_PAID);
    shopDTO.setShopVersionId(10000010017531654L);    //汽修综合版
    ServiceManager.getService(IShopService.class).createShop(shopDTO);
    Long customerShopId = shopDTO.getId();
    configService.activateShop(customerShopId, ShopStatus.REGISTERED_PAID);

    searchCondition.setShopId(supplierShopId);
    ShopDTO supplierShopDTO = configService.getShopById(supplierShopId);
    searchCondition.setShopAreaId(supplierShopDTO.getAreaId());
    searchCondition.setProvinceNo(supplierShopDTO.getProvince());
    //系统推荐给客户的批发商店铺版本ID
    String shopVersionIdStr = "10000010017531653,10000010017531654,10000010017531655,10000010017531656,10000010039823882";
    Integer total = applyService.countApplyCustomerShop(searchCondition,shopVersionIdStr, false);
    Pager pager = new Pager(total, 1, 10);
    List<ApplyShopSearchCondition> shopDTOs = applyService.searchApplyCustomerShop(searchCondition,shopVersionIdStr, pager, false);
    Assert.assertEquals(1,shopDTOs.size());
    Assert.assertEquals(customerShopId,shopDTOs.get(0).getShopId());

    searchCondition = new ApplyShopSearchCondition();
    searchCondition.setShopId(customerShopId);
    //系统推荐给批发商的客户店铺版本ID
    shopVersionIdStr = "10000010037193620,10000010017531657,10000010037193619";
     total = applyService.countApplySupplierShop(searchCondition,shopVersionIdStr, false);
    pager = new Pager(total, 1, 10);
    shopDTOs = applyService.searchApplySupplierShop(searchCondition,shopVersionIdStr, pager, false);
    Assert.assertEquals(1,shopDTOs.size());
    Assert.assertEquals(supplierShopId,shopDTOs.get(0).getShopId());

    //客户申请供应商关联
    applyService.batchSaveApplySupplierRelation(customerShopId, 1001L, supplierShopId);
    Map<Long,ShopRelationInviteDTO> shopRelationInviteDTOMap = applyService.getShopRelationInviteDTOMapByInvitedShopIds(null, null, customerShopId, null, supplierShopId);
    ShopRelationInviteDTO shopRelationInviteDTO = CollectionUtil.uniqueResult(shopRelationInviteDTOMap.values());
    Assert.assertEquals(InviteStatus.PENDING, shopRelationInviteDTO.getStatus());
    Assert.assertEquals(InviteType.CUSTOMER_INVITE,shopRelationInviteDTO.getInviteType());
    Assert.assertEquals(1001L, shopRelationInviteDTO.getOriginUserId().longValue());
    List<OperationLogDTO> operationLogDTOs = operationLogService.getOprationLogByObjectId(ObjectTypes.APPLY_SUPPLIER, shopRelationInviteDTO.getId());
    Assert.assertEquals(customerShopId,CollectionUtil.uniqueResult(operationLogDTOs).getShopId());
    Assert.assertEquals(1001L, CollectionUtil.uniqueResult(operationLogDTOs).getUserId().longValue());
    Assert.assertEquals(OperationTypes.CREATE, CollectionUtil.uniqueResult(operationLogDTOs).getOperationType());

    //供应商拒绝请求
    shopRelationInviteDTO.setOperationMan("TestUserName");
    shopRelationInviteDTO.setOperationManId(1002L);
    shopRelationInviteDTO.setRefuseMsg("供应商拒绝理由123");
    applyService.refuseApply(shopRelationInviteDTO);



    shopRelationInviteDTO =  CollectionUtil.uniqueResult(applyService.getShopRelationInviteDTOMapByInvitedShopIds(
        null, null, customerShopId, null, supplierShopId).values());
    Assert.assertEquals(InviteType.CUSTOMER_INVITE,shopRelationInviteDTO.getInviteType());
    Assert.assertEquals(InviteStatus.REFUSED,shopRelationInviteDTO.getStatus());
    Assert.assertEquals("TestUserName",shopRelationInviteDTO.getOperationMan());
    Assert.assertEquals(1002L,shopRelationInviteDTO.getOperationManId().longValue());
    Assert.assertEquals("供应商拒绝理由123",shopRelationInviteDTO.getRefuseMsg());

    //这里有两个 operationLogDTO order by create之后取第一个
    operationLogDTOs = operationLogService.getOprationLogByObjectId(ObjectTypes.APPLY_SUPPLIER, shopRelationInviteDTO.getId());
    Assert.assertEquals(supplierShopId,operationLogDTOs.get(1).getShopId());
    Assert.assertEquals(1002L,operationLogDTOs.get(1).getUserId().longValue());
    Assert.assertEquals(OperationTypes.REFUSE, operationLogDTOs.get(1).getOperationType());

    //客户申请供应商关联
    applyService.batchSaveApplySupplierRelation(customerShopId, 1001L, supplierShopId);
    shopRelationInviteDTOMap = applyService.getShopRelationInviteDTOMapByInvitedShopIds(null, InviteStatus.PENDING, customerShopId, null, supplierShopId);
    shopRelationInviteDTO = CollectionUtil.uniqueResult(shopRelationInviteDTOMap.values());
    Assert.assertEquals(InviteType.CUSTOMER_INVITE, shopRelationInviteDTO.getInviteType());
    Assert.assertEquals(1001L, shopRelationInviteDTO.getOriginUserId().longValue());
    operationLogDTOs = operationLogService.getOprationLogByObjectId(ObjectTypes.APPLY_SUPPLIER, shopRelationInviteDTO.getId());
    Assert.assertEquals(customerShopId, CollectionUtil.uniqueResult(operationLogDTOs).getShopId());
    Assert.assertEquals(1001L, CollectionUtil.uniqueResult(operationLogDTOs).getUserId().longValue());
    Assert.assertEquals(OperationTypes.CREATE, CollectionUtil.uniqueResult(operationLogDTOs).getOperationType());

    //供应商同意请求
    shopRelationInviteDTO.setOperationMan("供应商同意用户");
    shopRelationInviteDTO.setOperationManId(1003L);
    boolean isSuccess = applyService.acceptApply(shopRelationInviteDTO);
    Assert.assertEquals(true,isSuccess);
    ShopDTO customerShopDTO = configService.getShopById(customerShopId);
    //给供应商店铺下创建一个“被申请关联”客户，
    CustomerDTO customerDTO = userService.createRelationCustomer(supplierShopDTO, customerShopDTO, RelationTypes.RECOMMEND_RELATED);
    ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerDTO.getId());
    //给客户店铺下创建一个“申请关联”供应商
    SupplierDTO supplierDTO = rfiTxnService.createRelationSupplier(customerShopDTO, supplierShopDTO, RelationTypes.APPLY_RELATED);
    noticeService.createSupplierAcceptNoticeToSupplier(customerShopDTO, shopRelationInviteDTO, customerDTO);
    noticeService.createSupplierAcceptNoticeToCustomer(shopDTO, shopRelationInviteDTO, supplierDTO);
    shopRelationInviteDTO.setCustomerId(customerDTO.getId());
    shopRelationInviteDTO.setSupplierId(supplierDTO.getId());
    applyService.updateShopRelationInvite(shopRelationInviteDTO);

    shopRelationInviteDTO = applyService.getShopRelationInviteDTOByInvitedShopIdAndId(supplierShopId,shopRelationInviteDTO.getId());
    Assert.assertEquals(InviteType.CUSTOMER_INVITE, shopRelationInviteDTO.getInviteType());
    Assert.assertEquals(InviteStatus.ACCEPTED, shopRelationInviteDTO.getStatus());
    Assert.assertEquals("供应商同意用户", shopRelationInviteDTO.getOperationMan());
    Assert.assertEquals(1003L, shopRelationInviteDTO.getOperationManId().longValue());

    Assert.assertEquals(customerShopDTO.getName(),customerDTO.getName());
    Assert.assertEquals(customerShopDTO.getId(),customerDTO.getCustomerShopId());
    Assert.assertEquals(RelationTypes.RECOMMEND_RELATED,customerDTO.getRelationType());
    Assert.assertEquals(supplierShopDTO.getName(),supplierDTO.getName());
    Assert.assertEquals(supplierShopDTO.getId(),supplierDTO.getSupplierShopId());
    Assert.assertEquals(RelationTypes.APPLY_RELATED,supplierDTO.getRelationType());

    //这里有两个 operationLogDTO order by create之后取第一个
    operationLogDTOs = operationLogService.getOprationLogByObjectId(ObjectTypes.APPLY_SUPPLIER, shopRelationInviteDTO.getId());
    Assert.assertEquals(supplierShopId, operationLogDTOs.get(1).getShopId());
    Assert.assertEquals(1003L, operationLogDTOs.get(1).getUserId().longValue());
    Assert.assertEquals(OperationTypes.ACCEPT, operationLogDTOs.get(1).getOperationType());

    supplierShopDTO = configService.getShopById(supplierShopId);
    Assert.assertEquals(1,supplierShopDTO.getRelativeCustomerAmount().intValue());


  }
}
