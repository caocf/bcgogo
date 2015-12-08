package com.bcgogo.productRead.model;

import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.model.*;
import com.bcgogo.service.GenericReaderDao;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class ProductReadReader extends GenericReaderDao {

  public ProductReadReader(SessionFactory sessionFactory) {
    super(sessionFactory);
  }
  public List<Object[]> getProductDTListByProductLocalInfoIds(Long shopId,Set<Long> productLocalInfoIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getProductDTOMapByProductLocalInfoIds(session, shopId, productLocalInfoIds);
      return query.list();
    } finally {
      release(session);
    }
  }


  public List<ShopRegisterProduct> getShopRegisterProductList(Long shopId) {

    Session session =  this.getSession();
    try {
      Query q = SQL.getShopRegisterProductList(session, shopId);
      return (List<ShopRegisterProduct>) q.list();
    } finally {
      release(session);
    }
  }

  public List<ProductCategory> getCategoryListByIds(List<Long> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return null;
    }
    Session session =  this.getSession();

    try {
      Query q = SQL.getCategoryListByIds(session, ids);

      return (List<ProductCategory>) q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 获取店铺一定数量的产品信息
   * @param shopId
   * @param start
   * @param rows
   * @return
   */
  public List<Product> getProducts(Long shopId, int start, int rows) {
    Session session = getSession();
    try {
      Query query = SQL.getProducts(session, shopId, start, rows);
      return (List<Product>)query.list();
    } finally {
      release(session);
    }
  }


  public List<ShopVehicleBrandModel> getShopVehicleBrandModelByShopId(Long shopId) {
    Session session = getSession();
    try {
      Query query = SQL.getShopVehicleBrandModelByShopId(session, shopId);
      return (List<ShopVehicleBrandModel>) query.list();
    } finally {
      release(session);
    }
  }

  public List<StandardVehicleBrand> getAllStandardVehicleBrand() {
    Session session = getSession();
    try {
      Query query = SQL.getAllStandardVehicleBrand(session);
      return (List<StandardVehicleBrand>) query.list();
    } finally {
      release(session);
    }
  }

  public List<StandardVehicleModel> getAllStandardVehicleModel() {
    Session session = getSession();
    try {
      Query query = SQL.getAllStandardVehicleModel(session);
      return (List<StandardVehicleModel>) query.list();
    } finally {
      release(session);
    }
  }
}
