package com.bcgogo.productRead.model;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-6-24
 * Time: 下午1:31
 * To change this template use File | Settings | File Templates.
 */
class SQL {
  public static Query getProductDTOMapByProductLocalInfoIds(Session session, Long shopId, Set<Long> productLocalInfoIds) {
    StringBuffer sb = new StringBuffer("select p,pli from Product p,ProductLocalInfo pli where p.shopId =:shopId and p.id = pli.productId and pli.id in(:productLocalInfoIds)");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId).setParameterList("productLocalInfoIds", productLocalInfoIds);
    return query;
  }

  public static Query getShopRegisterProductList(Session session, Long shopId) {
    return session.createQuery(" from ShopRegisterProduct where shopId=:shopId  ")
        .setLong("shopId", shopId);

  }

  public static Query getCategoryListByIds(Session session, List<Long> ids) {
    return session.createQuery("select pc from ProductCategory pc where pc.id in (:ids)")
        .setParameterList("ids", ids);
  }

  public static Query getProducts(Session session, Long shopId, int start, int rows) {
    StringBuffer sb = new StringBuffer();
    sb.append("select p from Product as p");
    if (shopId != null)
      sb.append(" where p.shopId=:shopId");

    Query query = session.createQuery(sb.toString());
    if (shopId != null)
      query.setLong("shopId", shopId);
    return query.setFirstResult(start).setMaxResults(rows);
  }


  public static Query getShopVehicleBrandModelByShopId(Session session,Long shopId) {
    StringBuffer hql = new StringBuffer();
    hql.append(" from ShopVehicleBrandModel as p where p.shopId =:shopId");
    Query query = session.createQuery(hql.toString());
    query = query.setLong("shopId", shopId);
    return query;
  }

  public static Query getAllStandardVehicleBrand(Session session) {
    StringBuffer hql = new StringBuffer();
    hql.append(" from StandardVehicleBrand");
    Query query = session.createQuery(hql.toString());
    return query;
  }

  public static Query getAllStandardVehicleModel(Session session) {
    StringBuffer hql = new StringBuffer();
    hql.append(" from StandardVehicleModel");
    Query query = session.createQuery(hql.toString());
    return query;
  }
}
