package com.bcgogo.common;

import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.threadPool.OrderThreadPool;
import com.bcgogo.txn.service.solr.IProductSolrWriterService;
import com.bcgogo.user.service.solr.IVehicleSolrWriterService;
import com.bcgogo.utils.ArrayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-8-21
 * Time: 下午2:11
 * To change this template use File | Settings | File Templates.
 */
public class SolrHelper {
  public static final Logger LOG = LoggerFactory.getLogger(SolrHelper.class);
  private static IProductSolrWriterService productSolrWriterService;
  private static IVehicleSolrWriterService vehicleSolrWriterService;

  public static synchronized IProductSolrWriterService getProductSolrWriterService() {
    if(productSolrWriterService==null){
      productSolrWriterService= ServiceManager.getService(IProductSolrWriterService.class);
    }
    return productSolrWriterService;
  }

  public static synchronized IVehicleSolrWriterService getVehicleSolrWriterService() {
    if(vehicleSolrWriterService==null){
      vehicleSolrWriterService= ServiceManager.getService(IVehicleSolrWriterService.class);
    }
    return vehicleSolrWriterService;
  }


  public static boolean doProductReindex(Long shopId,Long ... productIdArr){
    if(ArrayUtil.isEmpty(productIdArr)){
      return false;
    }
    final Long[] finalProductIdArr = productIdArr;
    final Long finalShopId = shopId;
    OrderThreadPool.getInstance().execute(new Runnable() {
      @Override
      public void run() {
        try {
          getProductSolrWriterService().createProductSolrIndex(finalShopId,finalProductIdArr);
        } catch (Exception e) {
          LOG.error(e.getMessage(), e);
        }
      }
    });
    return true;
  }

  public static boolean doVehicleReindex(Long shopId,Long ... vehicleIdArr){
    if(ArrayUtil.isEmpty(vehicleIdArr)){
      return false;
    }
    final Long[] finalIdArr = vehicleIdArr;
    final Long finalShopId = shopId;
    OrderThreadPool.getInstance().execute(new Runnable() {
      @Override
      public void run() {
        try {
          getVehicleSolrWriterService().createVehicleSolrIndex(finalShopId,finalIdArr);
        } catch (Exception e) {
          LOG.error(e.getMessage(), e);
        }
      }
    });
    return true;
  }

  public static boolean doProductReindex(Long shopId,Result result){
    if(result==null||!result.isSuccess()||ArrayUtil.isEmpty(result.getDataList())){
      return false;
    }
    doProductReindex(shopId,ArrayUtil.toLongArr(result.getDataList()));
    return true;
  }

}
