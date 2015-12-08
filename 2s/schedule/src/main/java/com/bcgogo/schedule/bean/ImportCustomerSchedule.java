package com.bcgogo.schedule.bean;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.model.ProductDaoManager;
import com.bcgogo.product.model.ProductLocalInfo;
import com.bcgogo.product.model.ProductWriter;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.model.Inventory;
import com.bcgogo.txn.model.PurchasePrice;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.service.solr.IProductSolrWriterService;
import com.bcgogo.user.service.solr.IVehicleSolrWriterService;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.CustomerRecordDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.user.service.IContactService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.PinyinUtil;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.StringUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-3-31
 * Time: 下午4:02
 * To change this template use File | Settings | File Templates.
 */
public class ImportCustomerSchedule extends BcgogoQuartzJobBean {

  private static final Logger LOG = LoggerFactory.getLogger(ImportCustomerSchedule.class);

  private static boolean lock = false;

  private static synchronized boolean isLock() {
    if (lock) {
      return true;
    }
    lock = true;
    LOG.info("上锁");
    return false;
  }

  @Override
  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    LOG.info("导入定时钟启动 ...");
    if (isLock()) {
      LOG.info("已存在未结束线程，本线程中止！");
      return;
    }
    LOG.info("当前没有未结束线程，本线程可以正常执行！.");
    try {

      IConfigService configService = ServiceManager.getService(IConfigService.class);
      String filePath = configService.getConfig("ImportFilePath", ShopConstant.BC_SHOP_ID);
      String targetPath = configService.getConfig("ImportFileTargetPath", ShopConstant.BC_SHOP_ID);
      if (StringUtil.isEmpty(filePath) || StringUtil.isEmpty(targetPath)) {
        return;
      }
      LOG.info("源目录路径：" + filePath);
      LOG.info("目标目录路径：" + targetPath);
      File dir = new File(filePath);
      if (!dir.isDirectory()) {
        return;
      }
      File[] files = dir.listFiles();
      for (File file : files) {
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
          if (file.isDirectory()) {
            continue;
          }
          LOG.info("导入文件：" + file.getName());
          inputStream = new FileInputStream(file);
          LOG.info("成功打开文件流：" + file.getName());
          inputStreamReader = new InputStreamReader(inputStream, "GBK");
          bufferedReader = new BufferedReader(inputStreamReader);
          String strLine = null;
          List<String[]> dataList = new ArrayList<String[]>();
          bufferedReader.readLine();
          while ((strLine = bufferedReader.readLine()) != null) {
            dataList.add(strLine.split(","));
          }

          LOG.info("成功关闭文件流：" + file.getName());
          LOG.info("开始导入数据");
          if (file.getName().indexOf("ImportCustomer") >= 0) {
            LOG.info("开始导入客户");
            importCustomer(dataList);
            LOG.info("导入客户结束");
          } else if (file.getName().indexOf("ImportSupplier") >= 0) {
            LOG.info("开始导入供应商");
            importSupplier(dataList);
            LOG.info("导入供应商结束");
          } else if (file.getName().indexOf("ImportInventory") >= 0) {
            LOG.info("开始导入库存");
            importInventory(dataList);
            LOG.info("导入库存结束");
          }

        } catch (Exception e) {
          LOG.info("导入文件出现异常");
          LOG.error(e.getMessage(),e);
        } finally {
          if (inputStream != null) {
            inputStream.close();
          }
          if (inputStreamReader != null) {
            inputStreamReader.close();
          }
          if (bufferedReader != null) {
            bufferedReader.close();
          }
        }
        LOG.info("开始转移文件");
        boolean isSuccess = file.renameTo(new File(targetPath + UUID.randomUUID().toString() + "_" + file.getName()));
        if (isSuccess) {
          LOG.info("转移文件成功");
        } else {
          LOG.info("转移文件失败");
        }
      }
    } catch (Exception e) {
      LOG.info("导入文件出现异常");
      LOG.error(e.getMessage(),e);
    } finally {
      lock = false;
      LOG.info("解锁");
    }
  }

  /**
   * 导入客户
   *
   * @param dataList
   * @return
   */
  public boolean importCustomer(List<String[]> dataList) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    for (String[] strings : dataList) {
      try {
        Long shopId = StringUtil.isEmpty(strings[16]) ? 0L : NumberUtil.longValue(strings[16], 0L);
        CustomerDTO customerDTO = new CustomerDTO();
        if (StringUtil.isEmpty(strings[0])) {
          continue;
        }
        if (!StringUtil.isEmpty(strings[0])) {
          customerDTO.setName(strings[0]);
        }
        if (!StringUtil.isEmpty(strings[1])) {
          customerDTO.setContact(strings[1]);
        }
        if (!StringUtil.isEmpty(strings[2])) {
          customerDTO.setEmail(strings[2]);
        }
        if (!StringUtil.isEmpty(strings[3])) {
          customerDTO.setMobile(strings[3]);
        }
        if (!StringUtil.isEmpty(strings[4])) {
          customerDTO.setLandLine(strings[4]);
        }
        if (!StringUtil.isEmpty(strings[5])) {
          customerDTO.setAddress(strings[5]);
        }
        if (!StringUtil.isEmpty(strings[6])) {
          customerDTO.setFax(strings[6]);
        }
        if (!StringUtil.isEmpty(strings[7])) {
          customerDTO.setQq(strings[7]);
        }
        if (!StringUtil.isEmpty(strings[8])) {
          customerDTO.setBank(strings[8]);
        }
        if (!StringUtil.isEmpty(strings[9])) {
          customerDTO.setAccount(strings[9]);
        }
        if (!StringUtil.isEmpty(strings[10])) {
          customerDTO.setMemo(strings[10]);
        }

        customerDTO.setShopId(shopId);
        customerDTO.setCustomerKind("1");
        userService.createCustomer(customerDTO);

        ContactDTO contactDTO = new ContactDTO(null, customerDTO.getContact(), customerDTO.getMobile(), customerDTO.getEmail(), customerDTO.getQq(),
                    customerDTO.getId(), null, shopId, 0, 1, 1, 0);
        ServiceManager.getService(IContactService.class).saveContact(contactDTO);

        if (!StringUtil.isEmpty(strings[11])) {
          VehicleDTO vehicle = new VehicleDTO();
          vehicle.setShopId(shopId);
          vehicle.setLicenceNo(strings[11]);
          if (!StringUtil.isEmpty(strings[11])) {
            vehicle.setLicenceNoRevert(new StringBuffer(strings[11]).reverse().toString().toUpperCase());
          }
          vehicle.setBrand(strings[12]);
          vehicle.setModel(strings[13]);
          vehicle.setYear(strings[14]);
          vehicle.setEngine(strings[15]);
          userService.createVehicle(vehicle);
          ServiceManager.getService(IVehicleSolrWriterService.class).createVehicleSolrIndex(shopId, vehicle.getId());
          userService.addVehicleToCustomer(vehicle.getId(), customerDTO.getId());
        }


        CustomerRecordDTO customerRecordDTO = new CustomerRecordDTO();
        customerRecordDTO.setName(customerDTO.getName());
        customerRecordDTO.setContact(customerDTO.getContact());
        customerRecordDTO.setEmail(customerDTO.getEmail());
        customerRecordDTO.setMobile(customerDTO.getMobile());
        customerRecordDTO.setAddress(customerDTO.getAddress());
        customerRecordDTO.setFax(customerDTO.getFax());
        customerRecordDTO.setQq(customerDTO.getQq());
        customerRecordDTO.setBank(customerDTO.getBank());
        customerRecordDTO.setAccount(customerDTO.getBank());
        customerRecordDTO.setCustomerId(customerDTO.getId());
        customerRecordDTO.setShopId(customerDTO.getShopId());
        userService.createCustomerRecord(customerRecordDTO);
      } catch (Exception e) {
        try {
          LOG.error("导入客户出错　：　" + strings[0], e);
        } catch (Exception ex) {

        }
        continue;
      }

    }
    return true;
  }

  public boolean importSupplier(List<String[]> dataList) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    for (String[] strings : dataList) {
      try {
        if (StringUtil.isEmpty(strings[0])) {
          continue;
        }
        SupplierDTO supplierDTO = new SupplierDTO();
        if (!StringUtil.isEmpty(strings[0])) {
          supplierDTO.setName(strings[0]);
        }
        if (!StringUtil.isEmpty(strings[1])) {
          supplierDTO.setAbbr(strings[1]);
        }
        if (!StringUtil.isEmpty(strings[2])) {
          supplierDTO.setMobile(strings[2]);
        }
        if (!StringUtil.isEmpty(strings[3])) {
          supplierDTO.setLandLine(strings[3]);
        }
        if (!StringUtil.isEmpty(strings[4])) {
          supplierDTO.setContact(strings[4]);
        }
        if (!StringUtil.isEmpty(strings[5])) {
          supplierDTO.setAddress(strings[5]);
        }
        if (!StringUtil.isEmpty(strings[6])) {
          supplierDTO.setFax(strings[6]);
        }
        if (!StringUtil.isEmpty(strings[7])) {
          supplierDTO.setEmail(strings[7]);
        }
        if (!StringUtil.isEmpty(strings[8])) {
          supplierDTO.setQq(strings[8]);
        }
        if (!StringUtil.isEmpty(strings[9])) {
          supplierDTO.setMemo(strings[9]);
        }
        if (!StringUtil.isEmpty(strings[10])) {
          supplierDTO.setShopId(NumberUtil.longValue(strings[10], 0L));
        }

        userService.createSupplier(supplierDTO);

        ContactDTO contactDTO = new ContactDTO(null, supplierDTO.getContact(), supplierDTO.getMobile(), supplierDTO.getEmail(), supplierDTO.getQq(),
            null, supplierDTO.getId(), supplierDTO.getShopId(), 0, 1, 1, 0);
        ServiceManager.getService(IContactService.class).saveContact(contactDTO);
      } catch (Exception e) {
        try {
          LOG.error("导入供应商出错　：　" + strings[0], e);
        } catch (Exception ex) {

        }
        continue;
      }

    }
    return true;
  }

  /**
   * 导入库存
   *
   * @param dataList
   * @return
   */
  public boolean importInventory(List<String[]> dataList) {
    IProductSolrWriterService productSolrWriterService = ServiceManager.getService(IProductSolrWriterService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    productDaoManager = ServiceManager.getService(ProductDaoManager.class);
    txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    ProductWriter productWriter = productDaoManager.getWriter();
    TxnWriter txnWriter = txnDaoManager.getWriter();

    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    Long shopId = 0L;
    for (String[] strings : dataList) {
      try {
        shopId = StringUtil.isEmpty(strings[12]) ? 0L : NumberUtil.longValue(strings[12], 0);

        if (StringUtil.isEmpty(strings[0])) {
          continue;
        }
        ProductDTO productDTO = new ProductDTO();
        if (!StringUtil.isEmpty(strings[0])) {
          productDTO.setName(strings[0]);
          productDTO.setFirstLetter(PinyinUtil.getFirstLetter(strings[0]));
          productDTO.setFirstLetterCombination(PinyinUtil.converterToFirstSpell(strings[0]));

        }
        if (!StringUtil.isEmpty(strings[1])) {
          productDTO.setBrand(strings[1]);
        }
        if (!StringUtil.isEmpty(strings[2])) {
          productDTO.setSpec(strings[2]);
        }
        if (!StringUtil.isEmpty(strings[3])) {
          productDTO.setModel(strings[3]);
        }
        if (!StringUtil.isEmpty(strings[4])) {
          productDTO.setVehicleBrand(strings[4]);
          productDTO.setProductVehicleBrand(strings[4]);
        } else {
          productDTO.setProductVehicleBrand("全部");
        }
        if (!StringUtil.isEmpty(strings[5])) {
          productDTO.setVehicleModel(strings[5]);
          productDTO.setProductVehicleModel(strings[5]);
        }
        if (!StringUtil.isEmpty(strings[6])) {
          productDTO.setVehicleYear(strings[6]);
          productDTO.setProductVehicleYear(strings[6]);
        }
        if (!StringUtil.isEmpty(strings[7])) {
          productDTO.setVehicleEngine(strings[7]);
          productDTO.setProductVehicleEngine(strings[7]);
        }

        productDTO.setProductVehicleStatus(1); //使用车型默认为全部
        productDTO.setCheckStatus(0); //审核状态默认都为新
        productDTO.setShopId(shopId);
        productService.createProduct(productDTO);


        Object productStatus = productWriter.begin();
        ProductLocalInfo productLocalInfo = new ProductLocalInfo();
        productLocalInfo.setProductId(productDTO.getId());
        Double purchasePriceValue = StringUtil.isEmpty(strings[9]) ? 0 : NumberUtil.doubleValue(strings[9], 0);
        if (!StringUtil.isEmpty(strings[9])) {
          productLocalInfo.setPurchasePrice(NumberUtil.doubleValue(strings[9], 0));
        } else {
          productLocalInfo.setPurchasePrice(0.0);
        }
        productLocalInfo.setShopId(shopId);
        productWriter.save(productLocalInfo);
        productWriter.commit(productStatus);

        Object txnStatus = txnWriter.begin();
        Inventory inventory = new Inventory();
        inventory.setId(productLocalInfo.getId());

        Double amount = StringUtil.isEmpty(strings[8]) ? 0 : NumberUtil.doubleValue(strings[8], 0);
        inventory.setAmount(amount);
        inventory.setShopId(shopId);
        txnWriter.saveOrUpdate(inventory);

        PurchasePrice purchasePrice = new PurchasePrice();
        purchasePrice.setShopId(shopId);
        purchasePrice.setProductId(productLocalInfo.getId());
        purchasePrice.setPrice(purchasePriceValue);
        purchasePrice.setDate(System.currentTimeMillis());
        txnWriter.save(purchasePrice);
        txnWriter.commit(txnStatus);

        InventorySearchIndex inventorySearchIndex = new InventorySearchIndex();
        inventorySearchIndex.setShopId(shopId);
        inventorySearchIndex.setProductId(productLocalInfo.getId());
        if (!StringUtil.isEmpty(strings[0])) {
          inventorySearchIndex.setProductName(strings[0]);
        }
        if (!StringUtil.isEmpty(strings[1])) {
          inventorySearchIndex.setProductBrand(strings[1]);
        }
        if (!StringUtil.isEmpty(strings[2])) {
          inventorySearchIndex.setProductSpec(strings[2]);
        }
        if (!StringUtil.isEmpty(strings[3])) {
          inventorySearchIndex.setProductModel(strings[3]);
        }
        if (!StringUtil.isEmpty(strings[4])) {
          inventorySearchIndex.setBrand(strings[4]);
        } else {
          inventorySearchIndex.setBrand("全部");
        }
        if (!StringUtil.isEmpty(strings[5])) {
          inventorySearchIndex.setModel(strings[5]);
        }
        if (!StringUtil.isEmpty(strings[6])) {
          inventorySearchIndex.setYear(strings[6]);
        }
        if (!StringUtil.isEmpty(strings[7])) {
          inventorySearchIndex.setEngine(strings[7]);
        }
        inventorySearchIndex.setProductVehicleStatus(0);
        inventorySearchIndex.setEditDate(System.currentTimeMillis());
        inventorySearchIndex.setAmount(amount);
        inventorySearchIndex.setPurchasePrice(purchasePriceValue);
        inventorySearchIndex.setParentProductId(productDTO.getId());
        List<InventorySearchIndex> inventorySearchIndexList = new ArrayList<InventorySearchIndex>();
        inventorySearchIndexList.add(inventorySearchIndex);
        searchService.addOrUpdateInventorySearchIndexWithList(inventorySearchIndexList);
      } catch (Exception e) {
        try {
          LOG.error("导入库存出错　：　" + strings[0], e);
        } catch (Exception ex) {

        }
        continue;
      }
    }
    try {
      productSolrWriterService.reCreateProductSolrIndex(shopId, 2000);
      LOG.info("solr重建product索引店面shopId:" + shopId);
    } catch (Exception e) {
      LOG.error("solr重建product索引失败！");
      LOG.error(e.getMessage(), e);
    }
    return true;
  }


  private ProductDaoManager productDaoManager;

  private TxnDaoManager txnDaoManager;
}
