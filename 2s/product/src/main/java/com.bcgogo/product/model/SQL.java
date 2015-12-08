package com.bcgogo.product.model;

import com.bcgogo.BooleanEnum;
import com.bcgogo.common.Pager;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.enums.*;
import com.bcgogo.enums.Product.ProductCategoryStatus;
import com.bcgogo.enums.Product.ProductCategoryType;
import com.bcgogo.enums.Product.SearchInputType;
import com.bcgogo.enums.config.VehicleSelectBrandModel;
import com.bcgogo.enums.txn.finance.PaymentType;
import com.bcgogo.product.PromotionOrderRecordQuery;
import com.bcgogo.product.dto.LicenseplateDTO;
import com.bcgogo.product.dto.NormalProductDTO;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductMappingDTO;
import com.bcgogo.product.model.app.DictionaryFaultInfo;
import com.bcgogo.product.productManage.ProductSearchCondition;
import com.bcgogo.product.productManage.PromotionSearchCondition;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.stat.dto.CostStatConditionDTO;
import com.bcgogo.txn.dto.PromotionIndex;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.utils.*;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;

import javax.management.QueryEval;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-23
 * Time: 下午4:18
 * To change this template use File | Settings | File Templates.
 */
public class SQL {

  public static Query getAllModel(Session session) {
    return session.createQuery("select m from Model as m");
  }

  public static Query getAllProduct(Session session) {
    return session.createQuery("select p from Product as p where p.spec is not null");
  }

  public static Query getProductCount(Session session) {
    return session.createQuery("select count(*) from Product as p");
  }

  public static Query getProducts(Session session, Long shopId, Long startId, int num) {
    StringBuffer sb = new StringBuffer();
    sb.append("select p from Product as p where p.id > :id");
    if (shopId != null)
      sb.append(" and p.shopId=:shopId");
    else
      sb.append(" and p.shopId!=1");
    sb.append(" order by id asc");
    Query query = session.createQuery(sb.toString()).setLong("id", startId);
    if (shopId != null)
      query.setLong("shopId", shopId);
    return query.setMaxResults(num);
  }

  public static Query getProductLocalInfosByCondition(Session session, ProductQueryCondition productQueryCondition) {
    StringBuffer sb = new StringBuffer();
    sb.append(" from ProductLocalInfo p where p.shopId=:shopId ");
    boolean productStatusFlag = productQueryCondition.getProductStatus() != null;
    if (productStatusFlag) {
      sb.append("and p.salesStatus=:productStatus ");
    }
    Query query = session.createQuery(sb.toString()).setLong("shopId", productQueryCondition.getShopId());
    if (productStatusFlag) {
      query.setParameter("productStatus", productQueryCondition.getProductStatus());
    }
    if (productQueryCondition.getPager() != null) {
      query.setFirstResult(productQueryCondition.getPager().getRowStart());
      query.setMaxResults(productQueryCondition.getPager().getPageSize());
    }
    return query;

  }

  public static Query getAllBrand(Session session) {
    return session.createQuery("select b from Brand as b order by b.firstLetter");
  }
  public static Query getAllProductUnit(Session session) {
    return session.createQuery("from ProductUnit");
  }
  /**
   * 通过首字母查询车型
   *
   * @param session
   * @param firstLetter
   * @return
   */
  public static Query getModelWithFirstLetter(Session session, String firstLetter, Long brandId) {
    if (brandId != null) {
      return session.createQuery("select m from Model as m where m.brandId=:brandId and m.firstLetter='" + firstLetter + "' order by m.firstLetter")
          .setLong("brandId", brandId);
    } else {
      return session.createQuery("select m from Model as m where  m.firstLetter='" + firstLetter + "' order by m.firstLetter");
    }
  }

  /**
   * 通过首字母查询品牌
   *
   * @param session
   * @param firstLetter
   * @return
   */
  public static Query getBrandWithFirstLetter(Session session, String firstLetter) {
    return session.createQuery("select b from Brand as b where b.firstLetter='" + firstLetter + "' order by b.firstLetter");
  }

  public static Query getBrandByName(Session session, String name) {
    StringBuffer sb = new StringBuffer("select t from Brand as t where 1=1 ");
    if (StringUtils.isNotBlank(name)) {
      sb.append(" and t.name=:name ");
    } else {
      sb.append(" and t.name is null ");
    }
    Query query = session.createQuery(sb.toString());
    if (StringUtils.isNotBlank(name)) {
      query.setString("name", name);
    }
    return query;
  }

  public static Query getModelByName(Session session, Long brandId, String name) {
    StringBuffer sb = new StringBuffer("select t from Model as t where 1=1 ");
    if (brandId != null) {
      sb.append(" and t.brandId=:brandId ");
    } else {
      sb.append(" and t.brandId is null ");
    }

    if (StringUtils.isNotBlank(name)) {
      sb.append(" and t.name=:name ");
    } else {
      sb.append(" and t.name is null ");
    }
    Query query = session.createQuery(sb.toString());

    if (brandId != null) {
      query.setLong("brandId", brandId);
    }
    if (StringUtils.isNotBlank(name)) {
      query.setString("name", name);
    }
    return query;
  }

  public static Query getYearByName(Session session, Integer year, Long modelId, Long brandId) {
    StringBuffer sb = new StringBuffer("select t from Year as t where 1=1 ");
    if (year == null) {
      sb.append(" and t.year is null ");
    } else {
      sb.append(" and t.year=").append(year);
    }
    if (modelId == null) {
      sb.append(" and t.modelId is null ");
    } else {
      sb.append(" and t.modelId=").append(modelId);
    }
    if (brandId == null) {
      sb.append(" and t.brandId is null ");
    } else {
      sb.append(" and t.brandId=").append(brandId);
    }
    return session.createQuery(sb.toString());
  }

  public static Query getEngineByName(Session session, String engine, Long yearId, Long modelId, Long brandId) {
    StringBuffer sbr = new StringBuffer(" select t from Engine as t where 1=1 ");
    if (engine == null || "".equals(engine)) {
      sbr.append(" and t.engine is null ");
    } else {
      sbr.append(" and t.engine='").append(engine).append("' ");
    }
    if (yearId == null) {
      sbr.append(" and t.yearId is null ");
    } else {
      sbr.append(" and t.yearId=").append(yearId);
    }
    if (modelId == null) {
      sbr.append(" and t.modelId is null ");
    } else {
      sbr.append(" and t.modelId=").append(modelId);
    }
    if (brandId == null) {
      sbr.append(" and t.brandId is null ");
    } else {
      sbr.append(" and t.brandId=").append(brandId);
    }
    return session.createQuery(sbr.toString());
  }


  /**
   * 根据关键字查品牌
   *
   * @param session
   * @param searchWord
   * @return
   */
  public static Query getBrandByKeyword(Session session, String searchWord) {
    return session.createQuery("select b from Brand as b where b.name like :searchWord order by b.firstLetter")
        .setString("searchWord", "%" + searchWord + "%");
  }


  public static SQLQuery getProductFirstLetterByWord(Session session, String chnChar) {
    return (SQLQuery) (session.createSQLQuery("SELECT firstLetter FROM chnfirstletter WHERE hanzi=:chnChar").setString("chnChar", chnChar))
        .setResultTransformer(Transformers.aliasToBean(ProductDTO.class));
  }

  public static Query getProductVehicleByProductId(Session session, Long productId) {
    return session.createQuery("from ProductVehicle as pv where pv.productId=:productId")
        .setLong("productId", productId);
  }


  public static Query getProductByProductInfo(Session session, String productname, String productbrand, String productspec, String productmodel) {
    StringBuffer hql = new StringBuffer();
    hql.append("from Product as p where 1=1 ");
    if (productname != null && !"".equals(productname)) {
      hql.append(" and p.name='" + productname + "' ");
    } else {
      hql.append(" and p.name is null ");
    }
    if (productbrand != null && !"".equals(productbrand)) {
      hql.append(" and p.brand='" + productbrand + "' ");
    } else {
      hql.append(" and p.brand is null ");
    }
    if (productspec != null && !"".equals(productspec)) {
      hql.append(" and p.spec='" + productspec + "' ");
    } else {
      hql.append(" and p.spec is null ");
    }
    if (productmodel != null && !"".equals(productmodel)) {
      hql.append(" and p.model='" + productmodel + "' ");
    } else {
      hql.append(" and p.model is null ");
    }
    return session.createQuery(hql.toString());
  }

  public static Query getProductVehicleByVehicleIds(Session session, Long productId, Long brandId, Long modelId, Long yearId, Long engineId) {
    StringBuffer hql = new StringBuffer("from ProductVehicle pv where productId=").append(productId);
    if (brandId != null) {
      hql.append(" and pv.brandId=").append(brandId);
    } else {
      hql.append(" and pv.brandId is null ");
    }
    if (modelId != null) {
      hql.append(" and pv.modelId=").append(modelId);
    } else {
      hql.append(" and pv.modelId is null ");
    }
    if (yearId != null) {
      hql.append(" and pv.yearId=").append(yearId);
    } else {
      hql.append(" and pv.yearId is null ");
    }
    if (engineId != null) {
      hql.append(" and pv.engineId=").append(engineId);
    } else {
      hql.append(" and pv.engineId is null ");
    }
    return session.createQuery(hql.toString());
  }

  public static Query getProductLocalInfoByProductId(Session session, Long productId, Long shopId) {
    StringBuffer sb = new StringBuffer("from ProductLocalInfo as pli where pli.productId=:productId ");
    if (shopId != null)
      sb.append(" and pli.shopId=:shopId ");
    Query query = session.createQuery(sb.toString()).setLong("productId", productId);
    if (shopId != null)
      query.setLong("shopId", shopId);
    return query;
  }

  public static Query getProductLocalInfoById(Session session, Long productId, Long shopId) {
    return session.createQuery("from ProductLocalInfo as pli where 1=1 and pli.shopId=:shopId and pli.id=:productId ")
        .setLong("shopId", shopId).setLong("productId", productId);
  }

  public static Query getProductLocalInfoByIds(Session session,Long shopId,Long... productIds) {
    StringBuffer sb=new StringBuffer("from ProductLocalInfo as pli where pli.shopId=:shopId");
    if(!ArrayUtil.isEmpty(productIds)){     //非空可以在service里控制
      sb.append(" and pli.id in(:productIds)");
    }
    Query query=session.createQuery(sb.toString()).setLong("shopId", shopId);
    if(!ArrayUtil.isEmpty(productIds)){
      query.setParameterList("productIds", productIds);
    }
    return query;
  }

  public static Query getProductLocalInfoByIds(Session session,Long... productLocalInfoIds) {
    return session.createQuery("from ProductLocalInfo as pli where pli.id in(:productLocalInfoIds) ")
        .setParameterList("productLocalInfoIds", productLocalInfoIds);
  }

  public static Query getProductByProductLocalInfoId(Session session,Long shopId,Long... productLocalInfoId) {
    StringBuilder sb=new StringBuilder("select p,l from Product p, ProductLocalInfo l where p.id = l.productId   and l.id in(:productLocalInfoId)");
    if(shopId!=null){
      sb.append(" and l.shopId = :shopId");
    }
    Query query=session.createQuery(sb.toString())
      .setParameterList("productLocalInfoId", productLocalInfoId);
    if(shopId!=null){
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query getProductDTOByRelationSupplier(Session session,Set<Long> shopIdSet) {
    return session.createQuery("select p,l from Product p, ProductLocalInfo l where p.id = l.productId  and l.shopId in(:shopIdSet) and l.salesStatus = :salesStatus order by l.lastInSalesTime desc")
        .setParameterList("shopIdSet", shopIdSet).setParameter("salesStatus", ProductStatus.InSales).setFirstResult(0).setMaxResults(4);
  }

  public static Query getProductByShopIdAndHaveUnit(Session session,Long shopId) {
    return session.createQuery("select p,l from Product p, ProductLocalInfo l where p.id = l.productId  and l.shopId = :shopId and (l.sellUnit is not null or l.storageUnit is not null)")
        .setLong("shopId", shopId);
  }

	public static Query getProductByProductLocalInfoIds(Session session,Long shopId, Long ...productLocalInfoId) {
		return session.createQuery("select p from Product p, ProductLocalInfo l where p.id = l.productId  and l.shopId = :shopId and l.id in(:productLocalInfoId)")
				       .setLong("shopId", shopId).setParameterList("productLocalInfoId", productLocalInfoId);
	}

  public static void executeSql(Session session, String sql) {
    session.createSQLQuery(sql);
  }

  //根据shopid找到对应的area名称

  public static SQLQuery getAreaByShopId(Session session, Long shopId) {
    return (SQLQuery) (session.createSQLQuery("SELECT b.name FROM config.area a,config.area b,config.shop s WHERE a.no=s.area_id AND a.parent_no=b.no AND s.id=:shopId").setLong("shopId", shopId))
        .setResultTransformer(Transformers.aliasToBean(AreaDTO.class));
  }

  // 根据地区ID 找到地区名称
  public static SQLQuery getCarNoByareaById(Session session, String areaid) {
    return (SQLQuery) (session.createSQLQuery("SELECT name FROM config.area WHERE no=:areaid").setString("areaid", areaid))
        .setResultTransformer(Transformers.aliasToBean(AreaDTO.class));
  }


  //根据地区查找车牌前缀
  public static SQLQuery getCarNoByarea(Session session, String area) {
    return (SQLQuery) (session.createSQLQuery("SELECT carno FROM licenseplate WHERE area_name LIKE:area ORDER BY carno").setString("area", area + "%"))
        .setResultTransformer(Transformers.aliasToBean(LicenseplateDTO.class));
  }

  public static Query getCarNoByAreaNo(Session session, Long areaNo) {
    return session.createQuery("from Licenseplate where area_no = :areaNo").setLong("areaNo", areaNo).setMaxResults(1);
  }

  //根据车牌前缀首汉字查询

  public static SQLQuery getCarNosByFirstLetters(Session session, String carnoFirstLetter) {
    return (SQLQuery) (session.createSQLQuery("SELECT DISTINCT carno FROM licenseplate WHERE carno LIKE:carnoFirstLetter ORDER BY carno").setString("carnoFirstLetter", carnoFirstLetter + "%"))
        .setResultTransformer(Transformers.aliasToBean(LicenseplateDTO.class));
  }

  //根据关键字首字符是字母查询字母所对应的车牌前缀、
  public static SQLQuery getCarNosByAreaFirstLetters(Session session, String areaFirstLetter) {
    return (SQLQuery) (session.createSQLQuery("SELECT DISTINCT area_firstcarno AS areaFirstcarno FROM licenseplate WHERE area_firstname =:areaFirstLetter ORDER BY area_firstcarno").setString("areaFirstLetter", areaFirstLetter))
        .setResultTransformer(Transformers.aliasToBean(LicenseplateDTO.class));
  }
  //根据车牌前缀查找车牌号

  public static SQLQuery getCarsByCarNos(Session session, String carNo, Long shopId) {
    return (SQLQuery) (session.createSQLQuery("SELECT bv.licence_no AS licenceNo FROM bcuser.vehicle bv WHERE bv.licence_no LIKE:carNo AND bv.shop_id=:shopId AND (bv.status IS NULL OR bv.status <> :status)ORDER BY bv.licence_no")
        .setString("carNo", carNo + "%").setLong("shopId", shopId).setString("status",VehicleStatus.DISABLED.toString()))
        .setResultTransformer(Transformers.aliasToBean(VehicleDTO.class));
  }

  //反向本地模糊查询车牌号
  public static SQLQuery getCarsByCarNosReverse(Session session, String carNoValue, String area, Long shopId) {
    return (SQLQuery) (session.createSQLQuery("SELECT bv.licence_no_revert AS licenceNoRevert FROM bcuser.vehicle bv WHERE bv.licence_no_revert LIKE:carNoValue AND bv.licence_no_revert LIKE:area AND bv.shop_id=:shopId AND (bv.status IS NULL OR bv.status <> :status)ORDER BY bv.licence_no_revert")
        .setString("carNoValue", carNoValue + "%").setString("area", "%" + area).setLong("shopId", shopId).setString("status", VehicleStatus.DISABLED.toString()))
        .setResultTransformer(Transformers.aliasToBean(VehicleDTO.class));
  }

  public static Query getProductAdminList(Session session, int pageNo, int pageSize) {
    return session.createQuery("select pa from ProductAdmin as pa ")
        .setFirstResult(pageNo * pageSize)
        .setMaxResults(pageSize);
  }

  //查找汉字首字母
  public static Query getFirstLetterFromChnFirstLetter(Session session, String hanzi) {
    return session.createQuery("select chn from Chnfirstletter as chn where chn.hanzi=:hanzi").setString("hanzi", hanzi);
  }

  public static Query getProductByShopIdAndBarcode(Session session, Long shopId, String barcode) {
    return session.createQuery("select new ProductBarcode(p.barcode, p, l.purchasePrice) from Product p, ProductLocalInfo l where p.id = l.productId  and l.shopId = :shopId and p.barcode =:barcode")
        .setLong("shopId", shopId).setString("barcode", barcode);
  }

  public static Query getProductById(Session session, Long productId, Long shopId) {
//	  session.setCacheMode(CacheMode.REFRESH);
//	  session.setFlushMode(FlushMode.ALWAYS);
	  try {
		  Query query = session.createQuery("select p from Product as p where p.id =:productId and p.shopId =:shopId")
				                .setLong("productId", productId).setLong("shopId", shopId);
		  return query;
	  } finally {
//		  session.setCacheMode(CacheMode.NORMAL);
//		  session.setFlushMode(FlushMode.AUTO);
	  }
  }

  public static Query getProductBarcodeByBarcode(Session session, String barcode) {
    return session.createQuery("select p from ProductBarcode p where p.barcode =:barcode").setString("barcode", barcode);
  }

  public static Query getStandardProductByBarcode(Session session, String barcode) {
    return session.createQuery("select p from Product p where p.shopId = 1 and p.barcode = :barcode").setString("barcode", barcode);
  }

  public static Query getProductDataByProductLocalInfoId(Session session, Long shopId, Long... productLocalInfoId) {
    StringBuffer sb = new StringBuffer("select p,pli from Product p,ProductLocalInfo pli where p.id = pli.productId and pli.id in(:productLocalInfoId)");
      sb.append(" and p.shopId=:shopId ");

    Query query = session.createQuery(sb.toString());
      query.setLong("shopId", shopId);
    query.setParameterList("productLocalInfoId", productLocalInfoId);
    return query;
  }

  public static Query getProductLocalInfoIdList(Session session, Long shopId, int start, int num) {
    StringBuffer sb = new StringBuffer();
    sb.append("select pli.id from ProductLocalInfo pli ");
      sb.append(" where pli.shopId=:shopId");

    Query query = session.createQuery(sb.toString());
      query.setLong("shopId", shopId);
    return query.setFirstResult(start).setMaxResults(num);
  }
  public static Query getVehicleDTOListByBrand(Session session) {
    SQLQuery query = session.createSQLQuery("SELECT b.id AS id,b.name AS brand,b.id AS brandId,b.first_letter AS brandPinYin FROM brand b " +
        " WHERE b.id NOT IN (SELECT brand_id FROM model m WHERE m.brand_id IS NOT NULL) " +
        " AND b.id NOT IN (SELECT brand_id FROM year y WHERE y.brand_id IS NOT NULL) " +
        " AND b.id NOT IN (SELECT brand_id FROM engine e WHERE e.brand_id IS NOT NULL)");
    return query.addScalar("id", StandardBasicTypes.LONG)
                .addScalar("brand", StandardBasicTypes.STRING)
                .addScalar("brandId", StandardBasicTypes.LONG)
                .addScalar("brandPinYin", StandardBasicTypes.STRING)
                .setResultTransformer(Transformers.aliasToBean(VehicleDTO.class));
  }

  public static Query getVehicleDTOListByModel(Session session) {
    SQLQuery query = session.createSQLQuery("SELECT m.id AS id,b.name AS brand,b.first_letter AS brandPinYin,m.brand_id AS brandId,m.name AS model,m.id AS modelId,m.first_letter AS modelPinYin " +
        " FROM model m LEFT JOIN brand b ON m.brand_id = b.id " +
        " WHERE m.id NOT IN (SELECT model_id FROM year y WHERE y.model_id IS NOT NULL) " +
        " AND m.id NOT IN (SELECT model_id FROM engine e WHERE e.model_id IS NOT NULL)");
    return query.addScalar("id", StandardBasicTypes.LONG)
        .addScalar("brand", StandardBasicTypes.STRING)
        .addScalar("brandPinYin", StandardBasicTypes.STRING)
        .addScalar("brandId", StandardBasicTypes.LONG)
        .addScalar("model", StandardBasicTypes.STRING)
        .addScalar("modelPinYin", StandardBasicTypes.STRING)
        .addScalar("modelId", StandardBasicTypes.LONG)
        .setResultTransformer(Transformers.aliasToBean(VehicleDTO.class));
  }

  public static Query getVehicleDTOListByYear(Session session) {
    SQLQuery query = session.createSQLQuery("SELECT y.id AS id,b.name AS brand,b.first_letter AS brandPinYin,y.brand_id AS brandId,m.name AS model,y.model_id AS modelId,m.first_letter AS modelPinYin,y.year AS year,y.id AS yearId " +
        " FROM year y LEFT JOIN brand b ON y.brand_id = b.id " +
        " LEFT JOIN model m ON y.model_id = m.id " +
        " AND y.id NOT IN (SELECT year_id FROM engine e WHERE e.year_id IS NOT NULL)");
    return query.addScalar("id", StandardBasicTypes.LONG)
        .addScalar("brand", StandardBasicTypes.STRING)
        .addScalar("brandPinYin", StandardBasicTypes.STRING)
        .addScalar("brandId", StandardBasicTypes.LONG)
        .addScalar("model", StandardBasicTypes.STRING)
        .addScalar("modelPinYin", StandardBasicTypes.STRING)
        .addScalar("modelId", StandardBasicTypes.LONG)
        .addScalar("year", StandardBasicTypes.STRING)
        .addScalar("yearId", StandardBasicTypes.LONG)
        .setResultTransformer(Transformers.aliasToBean(VehicleDTO.class));
  }
  public static Query getVehicleDTOListByEngine(Session session) {
    SQLQuery query = session.createSQLQuery("SELECT e.id AS id,b.name AS brand,b.first_letter AS brandPinYin,e.brand_id AS brandId,m.name AS model,e.model_id AS modelId,m.first_letter AS modelPinYin,y.year AS year,e.id AS yearId,e.engine AS engine,e.id AS engineId " +
        " FROM engine e LEFT JOIN brand b ON e.brand_id = b.id " +
        " LEFT JOIN model m ON e.model_id = m.id " +
        " LEFT JOIN year y ON e.year_id = y.id ");
    return query.addScalar("id", StandardBasicTypes.LONG)
        .addScalar("brand", StandardBasicTypes.STRING)
        .addScalar("brandPinYin", StandardBasicTypes.STRING)
        .addScalar("brandId", StandardBasicTypes.LONG)
        .addScalar("model", StandardBasicTypes.STRING)
        .addScalar("modelPinYin", StandardBasicTypes.STRING)
        .addScalar("modelId", StandardBasicTypes.LONG)
        .addScalar("year", StandardBasicTypes.STRING)
        .addScalar("yearId", StandardBasicTypes.LONG)
        .addScalar("engine", StandardBasicTypes.STRING)
        .addScalar("engineId", StandardBasicTypes.LONG)
        .setResultTransformer(Transformers.aliasToBean(VehicleDTO.class));
  }
	public static Query getProductSupplier(Session session, Long productId, Long shopId) {
		StringBuffer sb = new StringBuffer();
		sb.append("select p from ProductSupplier p where p.productId =:productId and p.shopId =:shopId");
		return session.createQuery(sb.toString()).setLong("productId",productId).setLong("shopId",shopId);
	}

  public static Query getProductSupplierByProductIds(Session session, Long[] productLocalInfoIds, Long shopId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select p from ProductSupplier p where p.productId in (:productId) ");
      sb.append(" and p.shopId =:shopId");
	  sb.append(" order by p.lastUsedTime desc");
    Query query = session.createQuery(sb.toString()).setParameterList("productId", productLocalInfoIds);
      query.setLong("shopId", shopId);
    return query;
  }

	public static Query getProductDTOMapByProductLocalInfoIds(Session session, Set<Long> productLocalInfoIds) {
		StringBuffer sb = new StringBuffer("select p,pli from Product p,ProductLocalInfo pli where p.id = pli.productId and pli.id in(:productLocalInfoIds)");
    Query query = session.createQuery(sb.toString());
    query.setParameterList("productLocalInfoIds", productLocalInfoIds);
    return query;
	}

	public static Query getProductDTOMapByProductLocalInfoIds(Session session,Long shopId, Set<Long> productLocalInfoIds) {
		StringBuffer sb = new StringBuffer("select p,pli from Product p,ProductLocalInfo pli where p.shopId =:shopId and p.id = pli.productId and pli.id in(:productLocalInfoIds)");
    Query query = session.createQuery(sb.toString());
		query.setLong("shopId",shopId).setParameterList("productLocalInfoIds", productLocalInfoIds);
    return query;
	}

  public static Query getLastProductSupplierByProductIds(Session session, Long[] productIds)
  {
    StringBuffer sb = new StringBuffer("from ProductSupplier ps where ps.productId in(:productIds) group by ps.supplierId having count(ps.productId) =:size order by max(ps.lastUsedTime) desc");
    Query query = session.createQuery(sb.toString());
    query.setParameterList("productIds", productIds);
    query.setInteger("size", productIds.length);
    return query;
  }

	public static Query getProductByCommodityCode(Session session, long shopId, String commodityCode) {
		StringBuffer sb = new StringBuffer();
		sb.append("select p,pl from Product p , ProductLocalInfo pl where p.id = pl.productId and p.shopId = pl.shopId");
		sb.append(" and p.shopId =:shopId and p.commodityCode =:commodityCode");
		sb.append(" and (p.status is null or p.status <>'DISABLED')");
		return session.createQuery(sb.toString()).setLong("shopId",shopId).setString("commodityCode",commodityCode);
	}

	public static Query getProductsByCommodityCodes(Session session, Long shopId, Set<String> commodityCodeSet) {
		StringBuffer sb = new StringBuffer();
		sb.append("select p,pl from Product p , ProductLocalInfo pl where p.id = pl.productId and p.shopId = pl.shopId");
		sb.append(" and p.commodityCode in(:commodityCodeSet)");
		if(shopId != null){
			sb.append(" and p.shopId =:shopId");
		}
		sb.append(" and (p.status is null or p.status <>'DISABLED')");
		Query q = session.createQuery(sb.toString()).setParameterList("commodityCodeSet",commodityCodeSet);
		if(shopId != null){
			q.setLong("shopId",shopId);
		}
		return q;
	}

  public static Query getKindIdByName(Session session, Long shopId, String productKind){
    return session.createQuery("select k from Kind k where shopId =:shopId and k.name =:name and k.status =:status").setLong("shopId",shopId).setString("name",productKind).setString("status", KindStatus.ENABLE.toString());
  }

  public static Query getProductKindsRecentlyUsed(Session session, Long shopId){
    return session.createQuery("select k.name from Kind k where k.shopId =:shopId and k.status =:status order by last_update desc").setLong("shopId",shopId).
        setString("status", KindStatus.ENABLE.toString()).setFirstResult(0).setMaxResults(15);
  }

  public static Query getProductKindsWithFuzzyQuery(Session session, Long shopId, String keyword){
    return session.createQuery("select k.name from Kind k where k.shopId =:shopId and k.name like :keyword and k.status =:status order by last_update desc").
        setLong("shopId",shopId).setString("keyword","%"+keyword+"%").setString("status", KindStatus.ENABLE.toString()).setFirstResult(0).setMaxResults(15);
  }

  public static Query getKindIdByDisabledName(Session session, Long shopId, String productKind){
    return session.createQuery("select k from Kind k where shopId =:shopId and k.name =:name and k.status =:status").setLong("shopId",shopId).setString("name",productKind).setString("status", KindStatus.DISABLED.toString());
  }
  //根据id 就不需要用状态过滤
  public static Query getProductKindById(Session session,Long... kindId){
    return session.createQuery("select k from Kind k where k.id in (:kindId)").setParameterList("kindId",kindId);
  }

  //根据id,shopId 就不需要用状态过滤
  public static Query getProductKindByShopIdAndIds(Session session,Long shopId,Set<Long> ids){
    return session.createQuery("select k from Kind k where k.id in (:ids) and k.shopId =:shopId").setParameterList("ids",ids).setLong("shopId",shopId);
  }

    //根据name,shopId 就不需要用状态过滤
  public static Query getKindByNames(Session session,Long shopId,Set<String> names){
    return session.createQuery("select k from Kind k where k.name in (:names) and k.shopId =:shopId").setParameterList("names",names).setLong("shopId",shopId);
  }

  public static Query getAllEnabledProductKindByShop(Session session,Long shopId){
    return session.createQuery("select k from Kind k where k.shopId in (:shopId) and k.status =:status").setLong("shopId",shopId).setString("status", KindStatus.ENABLE.toString());
  }

  public static Query getProductDTOsByProductKindId(Session session, long shopId, Long... productKindId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select p,pl from Product p,ProductLocalInfo pl where p.id = pl.productId and p.shopId = pl.shopId");
    sb.append(" and p.shopId =:shopId and p.kindId in(:productKindId)");
    sb.append(" and (p.status is null or p.status <>'DISABLED')");
    return session.createQuery(sb.toString()).setLong("shopId", shopId).setParameterList("productKindId", productKindId);
  }


  public static Query getProductListForCostStat(Session session, Long shopId, String[] queryFields) {
    StringBuffer sb =new StringBuffer();
    sb.append("select group_concat(id), name ");
    sb.append(", case when brand is null then '' when ASCII(brand)=0 then '' else brand end as brand ");
    sb.append(", case when product_vehicle_brand is null then '' when ASCII(product_vehicle_brand)=0 then '' else product_vehicle_brand end " +
        ", case when product_vehicle_model is null then '' when ASCII(product_vehicle_model)=0 then '' else product_vehicle_model end ");
    sb.append("from product where shop_id = 1 or shop_id = :shopId ");
    sb.append("group by name ");
    if(ArrayUtils.contains(queryFields, CostStatConditionDTO.FIELD_BRAND)){
      sb.append(", case when brand is null then '' when ASCII(brand)=0 then '' else brand end ");
    }
    if(ArrayUtils.contains(queryFields,CostStatConditionDTO.FIELD_VEHICLE_MODEL)){
      sb.append(", case when product_vehicle_brand is null then '' when ASCII(product_vehicle_brand)=0 then '' else product_vehicle_brand end " +
          ", case when product_vehicle_model is null then '' when ASCII(product_vehicle_model)=0 then '' else product_vehicle_model end ");
    }

    return session.createSQLQuery(sb.toString()).setLong("shopId", shopId);
  }

  public static Query getProductLocalInfoIdListByProductIds(Session session, List<Long> ids) {
    return session.createSQLQuery("SELECT group_concat(id) FROM product_local_info WHERE product_id IN :ids").setParameterList("ids", ids);
  }

  public static Query getCustomerTradedProductMappingByProductIds(Session session, Long customerShopId, Long... productLocalInfoIds) {
		StringBuffer sb = new StringBuffer();
		sb.append("select pm from ProductMapping pm where pm.customerProductId in (:productLocalInfoIds) and pm.status = 'ENABLED' and (tradeStatus=:purchase_Inventory or tradeStatus=:trade_finished)");
		if(customerShopId != null){
			sb.append(" and pm.customerShopId =:customerShopId");
		}
		Query query = session.createQuery(sb.toString()).setParameterList("productLocalInfoIds",productLocalInfoIds)
        .setString("purchase_Inventory",ProductStatus.TradeStatus.PURCHASE_INVENTORY.toString()).setString("trade_finished", ProductStatus.TradeStatus.TRADE_FINISHED.toString());
		if(customerShopId != null){
			query.setLong("customerShopId",customerShopId);
		}
		return query;
	}

  public static Query getProductMappings(Session session,ProductMappingDTO productMappingIndex) {
    StringBuffer sb = new StringBuffer();
    sb.append("from ProductMapping pm where pm.status = 'ENABLED'");
    if(productMappingIndex.getCustomerShopId()!=null){
      sb.append(" and pm.customerShopId=:customerShopId");
    }
    if(productMappingIndex.getSupplierShopId()!=null){
      sb.append(" and pm.supplierShopId=:supplierShopId");
    }
    if(!ArrayUtil.isEmpty(productMappingIndex.getCustomerProductIds())){
      sb.append(" and pm.customerProductId in (:customerProductIds)");
    }
    if(!ArrayUtil.isEmpty(productMappingIndex.getSupplierProductIds())){
      sb.append(" and pm.supplierProductId in (:supplierProductIds)");
    }
    Query query = session.createQuery(sb.toString());
    if(productMappingIndex.getCustomerShopId()!=null){
      query.setLong("customerShopId",productMappingIndex.getCustomerShopId());
    }
    if(productMappingIndex.getSupplierShopId()!=null){
      query.setLong("supplierShopId",productMappingIndex.getSupplierShopId());
    }
    if(!ArrayUtil.isEmpty(productMappingIndex.getCustomerProductIds())){
      query.setParameterList("customerProductIds",productMappingIndex.getCustomerProductIds());
    }
    if(!ArrayUtil.isEmpty(productMappingIndex.getSupplierProductIds())){
      query.setParameterList("supplierProductIds",productMappingIndex.getSupplierProductIds());
    }
    return query;
  }

	public static Query getSupplierProductMappingByProductIds(Session session, Long supplierShopId, Long customerShopId, Set<Long> productLocalInfoIds) {
		StringBuffer sb = new StringBuffer();
		sb.append("select pm from ProductMapping pm where pm.customerShopId =:customerShopId  ");
		sb.append(" and pm.supplierShopId =:supplierShopId");
		sb.append(" and pm.status = 'ENABLED'");
		sb.append(" and pm.supplierProductId in (:productLocalInfoIds)");
		return session.createQuery(sb.toString()).setParameterList("productLocalInfoIds", productLocalInfoIds)
				       .setLong("supplierShopId", supplierShopId)
				       .setLong("customerShopId", customerShopId);
	}

  /**
   * 该方法废弃，目前商品唯一性约束是用7属性来处理，by QYX 2013-07-11
   * @param session
   * @param shopId
   * @param productDTO
   * @return
   */
//  @Deprecated
//	public static Query getProductsBy6P(Session session, Long shopId, ProductDTO productDTO) {
//		StringBuffer hql = new StringBuffer();
//		hql.append("select p from Product as p where p.shopId=:shopId ");
//		if (StringUtils.isNotBlank(productDTO.getName())) {
//			hql.append(" and p.name=:name");
//		} else {
//			hql.append(" and (p.name is null or  p.name = '' or  p.name = '\0')");
//		}
//		if (StringUtils.isNotBlank(productDTO.getBrand())) {
//			hql.append(" and p.brand=:brand");
//		} else {
//			hql.append(" and (p.brand is null or  p.brand = '' or  p.brand = '\0')");
//		}
//		if (StringUtils.isNotBlank(productDTO.getSpec())) {
//			hql.append(" and p.spec=:spec");
//		} else {
//			hql.append(" and (p.spec is null or  p.spec = '' or  p.spec = '\0')");
//		}
//		if (StringUtils.isNotBlank(productDTO.getModel())) {
//			hql.append(" and p.model=:model");
//		} else {
//			hql.append(" and (p.model is null or  p.model = '' or  p.model = '\0')");
//		}
//		if (StringUtils.isNotBlank(productDTO.getProductVehicleBrand())) {
//			hql.append(" and p.productVehicleBrand=:productVehicleBrand");
//		} else {
//			hql.append(" and (p.productVehicleBrand is null or  p.productVehicleBrand = '' or  p.productVehicleBrand = '\0')");
//		}
//		if (StringUtils.isNotBlank(productDTO.getProductVehicleModel())) {
//			hql.append(" and p.productVehicleModel=:productVehicleModel");
//		} else {
//			hql.append(" and (p.productVehicleModel is null or  p.productVehicleModel = '' or  p.productVehicleModel = '\0')");
//		}
//		hql.append(" and (p.status is null or p.status <> 'DISABLED')");
//		Query query = session.createQuery(hql.toString()).setLong("shopId", shopId);
//		if (StringUtils.isNotBlank(productDTO.getName())) {
//			query = query.setString("name", productDTO.getName());
//		}
//		if (StringUtils.isNotBlank(productDTO.getBrand())) {
//			query = query.setString("brand", productDTO.getBrand());
//		}
//		if (StringUtils.isNotBlank(productDTO.getSpec())) {
//			query = query.setString("spec", productDTO.getSpec());
//		}
//		if (StringUtils.isNotBlank(productDTO.getModel())) {
//			query = query.setString("model", productDTO.getModel());
//		}
//		if (StringUtils.isNotBlank(productDTO.getProductVehicleBrand())) {
//			query = query.setString("productVehicleBrand", productDTO.getProductVehicleBrand());
//		}
//		if (StringUtils.isNotBlank(productDTO.getProductVehicleModel())) {
//			query = query.setString("productVehicleModel", productDTO.getProductVehicleModel());
//		}
//		return query;
//	}

  public static Query getProductsBy7P(Session session, Long shopId, ProductDTO productDTO) {
		StringBuffer hql = new StringBuffer();
		hql.append("select p from Product as p where p.shopId=:shopId ");
    if(StringUtils.isNotBlank(productDTO.getCommodityCode())){
      hql.append(" and p.commodityCode =:commodityCode ");
    }else {
      hql.append(" and p.commodityCode is null ");
    }
		if (StringUtils.isNotBlank(productDTO.getName())) {
			hql.append(" and p.name=:name");
		} else {
			hql.append(" and (p.name is null or  p.name = '' or  p.name = '\0')");
		}
		if (StringUtils.isNotBlank(productDTO.getBrand())) {
			hql.append(" and p.brand=:brand");
		} else {
			hql.append(" and (p.brand is null or  p.brand = '' or  p.brand = '\0')");
		}
		if (StringUtils.isNotBlank(productDTO.getSpec())) {
			hql.append(" and p.spec=:spec");
		} else {
			hql.append(" and (p.spec is null or  p.spec = '' or  p.spec = '\0')");
		}
		if (StringUtils.isNotBlank(productDTO.getModel())) {
			hql.append(" and p.model=:model");
		} else {
			hql.append(" and (p.model is null or  p.model = '' or  p.model = '\0')");
		}
		if (StringUtils.isNotBlank(productDTO.getProductVehicleBrand())) {
			hql.append(" and p.productVehicleBrand=:productVehicleBrand");
		} else {
			hql.append(" and (p.productVehicleBrand is null or  p.productVehicleBrand = '' or  p.productVehicleBrand = '\0')");
		}
		if (StringUtils.isNotBlank(productDTO.getProductVehicleModel())) {
			hql.append(" and p.productVehicleModel=:productVehicleModel");
		} else {
			hql.append(" and (p.productVehicleModel is null or  p.productVehicleModel = '' or  p.productVehicleModel = '\0')");
		}
		hql.append(" and (p.status is null or p.status <> 'DISABLED')");
		Query query = session.createQuery(hql.toString()).setLong("shopId", shopId);
    if (StringUtils.isNotBlank(productDTO.getCommodityCode())) {
      query = query.setString("commodityCode", productDTO.getCommodityCode());
    }
    if (StringUtils.isNotBlank(productDTO.getName())) {
			query = query.setString("name", productDTO.getName());
		}
		if (StringUtils.isNotBlank(productDTO.getBrand())) {
			query = query.setString("brand", productDTO.getBrand());
		}
		if (StringUtils.isNotBlank(productDTO.getSpec())) {
			query = query.setString("spec", productDTO.getSpec());
		}
		if (StringUtils.isNotBlank(productDTO.getModel())) {
			query = query.setString("model", productDTO.getModel());
		}
		if (StringUtils.isNotBlank(productDTO.getProductVehicleBrand())) {
			query = query.setString("productVehicleBrand", productDTO.getProductVehicleBrand());
		}
		if (StringUtils.isNotBlank(productDTO.getProductVehicleModel())) {
			query = query.setString("productVehicleModel", productDTO.getProductVehicleModel());
		}
		return query;
	}

  public static Query getCustomerProductMappingByCustomerProductLocalInfoIds(Session session,  Long customerShopId,Long supplierShopId, Long... customerProductLocalInfoIds) {
    StringBuffer sb = new StringBuffer();
    sb.append("select pm from ProductMapping pm where pm.customerShopId =:customerShopId  ");
    sb.append(" and pm.supplierShopId =:supplierShopId");
    sb.append(" and pm.status = 'ENABLED'");
    sb.append(" and pm.customerProductId in (:customerProductLocalInfoIds)");
    return session.createQuery(sb.toString()).setParameterList("customerProductLocalInfoIds", customerProductLocalInfoIds)
        .setLong("supplierShopId", supplierShopId)
        .setLong("customerShopId", customerShopId);
  }



	public static Query getProductMapping(Session session, Long customerShopId, Long supplierShopId, Long[] productIds) {
				StringBuffer sb = new StringBuffer();
		sb.append("select pm from ProductMapping pm where pm.customerShopId =:customerShopId  ");
		sb.append(" and pm.supplierShopId =:supplierShopId");
		sb.append(" and pm.status = 'ENABLED'");
		sb.append(" and (pm.supplierProductId in (:productIds) or pm.customerProductId in (:productIds))");
		return session.createQuery(sb.toString()).setParameterList("productIds", productIds)
				       .setLong("supplierShopId", supplierShopId)
				       .setLong("customerShopId", customerShopId);
	}
  public static Query getEnabledKindDTOById(Session session,Long shopId,Long id)
  {
    return session.createQuery("from Kind k where k.shopId =:shopId and k.id=:id and k.status =:status")
        .setLong("shopId",shopId).setLong("id",id).setString("status",KindStatus.ENABLE.toString());
  }

  public static Query getProductLocalInfoByCategoryId(Session session,Long shopId,Long categoryId)
  {
    return session.createQuery("from ProductLocalInfo pli where pli.shopId = :shopId and pli.businessCategoryId = :categoryId")
        .setLong("shopId",shopId).setLong("categoryId",categoryId);
  }


  public static Query getProductCategoryByName(Session session, Long shopId,String name) {
    StringBuffer sb = new StringBuffer("from ProductCategory d where d.name =:name");
    if(shopId!=null){
      sb.append(" and (d.shopId =").append(ShopConstant.BC_ADMIN_SHOP_ID).append(" or d.shopId=:shopId)");
    }else{
      sb.append(" and d.shopId =").append(ShopConstant.BC_ADMIN_SHOP_ID);
    }
    Query query = session.createQuery(sb.toString());
    query.setString("name", name);
    if(shopId!=null){
      query.setLong("shopId", shopId);
    }
    return query;
  }


   public static Query getProductCategoryByParentId(Session session, Long shopId,Long parentId,Pager pager) {
     if (parentId != null) {
       if (pager == null) {
         return session.createQuery("from ProductCategory d where d.shopId =:shopId and d.parentId=:parentId")
             .setLong("shopId", shopId).setLong("parentId", parentId);
       } else {
         return session.createQuery("from ProductCategory d where d.shopId =:shopId and d.parentId=:parentId")
             .setLong("shopId", shopId).setLong("parentId", parentId).setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
       }
     }
     return session.createQuery("from ProductCategory d where d.shopId =:shopId")
         .setLong("shopId", shopId);
   }


  public static Query getProductCategoryByNameParentId(Session session, Long shopId,String name,Long parentId) {
    if (StringUtil.isEmpty(name)) {
      return session.createQuery("from ProductCategory d where d.shopId =:shopId  and d.parentId=:parentId")
          .setLong("shopId", shopId).setLong("parentId", parentId);
    }
    return session.createQuery("from ProductCategory d where d.shopId =:shopId and d.name =:name and d.parentId=:parentId")
        .setLong("shopId", shopId).setString("name", name).setLong("parentId", parentId);
  }

    public static Query getProductCategoryFuzzyName(Session session, Long shopId, String name, Pager pager) {
        if (pager == null) {
            if (StringUtils.isEmpty(name)) {
                return session.createQuery("from ProductCategory d where d.shopId =:shopId and id != -1 ")
                        .setLong("shopId", shopId);
            }
            return session.createQuery("from ProductCategory d where d.shopId =:shopId and id != -1 and d.name like:name")
                    .setLong("shopId", shopId).setString("name", "%" + name + "%");
        }

        if (StringUtils.isEmpty(name)) {
            return session.createQuery("from ProductCategory d where d.shopId =:shopId and id != -1 ")
                    .setLong("shopId", shopId).setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
        }
        return session.createQuery("from ProductCategory d where d.shopId =:shopId and id != -1 and d.name like:name")
                .setLong("shopId", shopId).setString("name", "%" + name + "%").setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());

    }

  public static Query getSecondCategoryByParentId(Session session, Long shopId,Long parentId) {
    if (parentId != null) {
      return session.createQuery("from ProductCategory d where d.shopId =:shopId and d.parentId=:parentId and d.categoryType=:type")
          .setLong("shopId", shopId).setLong("parentId", parentId).setParameter("type", ProductCategoryType.SECOND_CATEGORY);
    }
    return session.createQuery("from ProductCategory d where d.shopId =:shopId and d.categoryType=:type")
        .setLong("shopId", shopId).setParameter("type", ProductCategoryType.SECOND_CATEGORY);
  }

  public static Query countProductByNormalProductId(Session session, Long... normalProductId) {
    return session.createQuery("select p.normalProductId,count(p.id) from Product p where p.normalProductId in(:normalProductId) GROUP BY p.normalProductId")
        .setParameterList("normalProductId", normalProductId);
  }

  public static Query getNormalProduct(Session session,ProductSearchCondition searchCondition) {
    StringBuffer sql = new StringBuffer("select * from normal_product np");
    if (searchCondition!=null) {
      if(searchCondition.getThirdCategoryId()==null){
        if (searchCondition.getSecondCategoryId()!=null) {
          sql.append(" left join product_category pc on pc.id=np.product_category_id ");
        } else if (searchCondition.getFirstCategoryId()!=null && searchCondition.getSecondCategoryId()==null) {
          sql.append(" left join product_category pc on pc.id=np.product_category_id ");
          sql.append(" left join product_category pc1 on pc1.id=pc.parent_id ");
        }
      }
      if (StringUtils.isNotBlank(searchCondition.getVehicleBrand()) || StringUtils.isNotBlank(searchCondition.getVehicleModel())) {
        sql.append(" left join normal_product_vehicle_brand_model npvbm on npvbm.normal_product_id=np.id");
      }

      sql.append(" where 1=1");
      if(searchCondition.getThirdCategoryId()==null){
        if (searchCondition.getSecondCategoryId()!=null) {
          sql.append(" and pc.parent_id =:secondId");
        } else if (searchCondition.getFirstCategoryId()!=null && searchCondition.getSecondCategoryId()==null) {
          sql.append(" and pc1.parent_id =:firstId");
        }
        if(StringUtils.isNotBlank(searchCondition.getProductName())) {
          sql.append(" and np.product_name like :productName");
        }
      }else{
        sql.append(" and np.product_category_id=:thirdId");
      }
      if (StringUtils.isNotBlank(searchCondition.getVehicleBrand()) && StringUtils.isNotBlank(searchCondition.getVehicleModel())) {
        sql.append(" and ((npvbm.brand_name like :vehicleBrand and npvbm.model_name like :vehicleModel) or np.select_brand_model='"+ VehicleSelectBrandModel.ALL_MODEL+"')");
      }else if (StringUtils.isNotBlank(searchCondition.getVehicleBrand()) && StringUtils.isBlank(searchCondition.getVehicleModel())) {
        sql.append(" and (npvbm.brand_name like :vehicleBrand or np.select_brand_model='"+ VehicleSelectBrandModel.ALL_MODEL+"')");
      }else if (StringUtils.isNotBlank(searchCondition.getVehicleModel()) && StringUtils.isBlank(searchCondition.getVehicleBrand())) {
        sql.append(" and (npvbm.model_name like :vehicleModel or np.select_brand_model='"+ VehicleSelectBrandModel.ALL_MODEL+"')");
      }

      if (StringUtils.isNotBlank(searchCondition.getBrand())) {
        sql.append(" and np.brand like :brand");
      }
      if (StringUtils.isNotBlank(searchCondition.getSpec())) {
        sql.append(" and np.spec like :spec");
      }
      if (StringUtils.isNotBlank(searchCondition.getModel())) {
        sql.append(" and np.model like :model");
      }

      if (StringUtils.isNotBlank(searchCondition.getCommodityCode())) {
        sql.append(" and np.commodity_code like :commodityCode");
      }
    }

    sql.append(" order by np.product_name");

    Query q = session.createSQLQuery(sql.toString()).addEntity(NormalProduct.class)
        .setFirstResult((searchCondition.getPage() - 1) * searchCondition.getLimit()).setMaxResults(searchCondition.getLimit());

    if (searchCondition!=null) {
      if(searchCondition.getThirdCategoryId()==null){
        if (searchCondition.getSecondCategoryId()!=null) {
          q.setLong("secondId", searchCondition.getSecondCategoryId());
        } else if (searchCondition.getFirstCategoryId()!=null && searchCondition.getSecondCategoryId()==null) {
          q.setLong("firstId", searchCondition.getFirstCategoryId());
        }
        if(StringUtils.isNotBlank(searchCondition.getProductName())) {
          q.setString("productName", "%" + searchCondition.getProductName() + "%");
        }
      }else{
        q.setLong("thirdId", searchCondition.getThirdCategoryId());
      }
      if (StringUtils.isNotBlank(searchCondition.getVehicleBrand())) {
        q.setString("vehicleBrand", "%" + searchCondition.getVehicleBrand() + "%");
      }
      if (StringUtils.isNotBlank(searchCondition.getVehicleModel())) {
        q.setString("vehicleModel", "%" + searchCondition.getVehicleModel() + "%");
      }

      if (StringUtils.isNotBlank(searchCondition.getBrand())) {
        q.setString("brand", "%" + searchCondition.getBrand() + "%");
      }
      if (StringUtils.isNotBlank(searchCondition.getSpec())) {
        q.setString("spec", "%" + searchCondition.getSpec() + "%");
      }
      if (StringUtils.isNotBlank(searchCondition.getModel())) {
        q.setString("model", "%" + searchCondition.getModel() + "%");
      }

      if (StringUtils.isNotBlank(searchCondition.getCommodityCode())) {
        q.setString("commodityCode", "%" + searchCondition.getCommodityCode() + "%");
      }
    }

    return q;
  }

  public static Query countNormalProductDTO(Session session,ProductSearchCondition searchCondition) {
    StringBuffer sql = new StringBuffer("select count(np.id) from normal_product np");
    if (searchCondition!=null) {
      if(searchCondition.getThirdCategoryId()==null){
        if (searchCondition.getSecondCategoryId()!=null) {
          sql.append(" left join product_category pc on pc.id=np.product_category_id ");
        } else if (searchCondition.getFirstCategoryId()!=null && searchCondition.getSecondCategoryId()==null) {
          sql.append(" left join product_category pc on pc.id=np.product_category_id ");
          sql.append(" left join product_category pc1 on pc1.id=pc.parent_id ");
        }
      }
      if (StringUtils.isNotBlank(searchCondition.getVehicleBrand()) || StringUtils.isNotBlank(searchCondition.getVehicleModel())) {
        sql.append(" left join normal_product_vehicle_brand_model npvbm on npvbm.normal_product_id=np.id");
      }

      sql.append(" where 1=1");
      if(searchCondition.getThirdCategoryId()==null){
        if (searchCondition.getSecondCategoryId()!=null) {
          sql.append(" and pc.parent_id =:secondId");
        } else if (searchCondition.getFirstCategoryId()!=null && searchCondition.getSecondCategoryId()==null) {
          sql.append(" and pc1.parent_id =:firstId");
        }
        if(StringUtils.isNotBlank(searchCondition.getProductName())) {
          sql.append(" and np.product_name like :productName");
        }
      }else{
        sql.append(" and np.product_category_id=:thirdId");
      }
      if (StringUtils.isNotBlank(searchCondition.getVehicleBrand()) && StringUtils.isNotBlank(searchCondition.getVehicleModel())) {
        sql.append(" and ((npvbm.brand_name like :vehicleBrand and npvbm.model_name like :vehicleModel) or np.select_brand_model='"+ VehicleSelectBrandModel.ALL_MODEL+"')");
      }else if (StringUtils.isNotBlank(searchCondition.getVehicleBrand()) && StringUtils.isBlank(searchCondition.getVehicleModel())) {
        sql.append(" and (npvbm.brand_name like :vehicleBrand or np.select_brand_model='"+ VehicleSelectBrandModel.ALL_MODEL+"')");
      }else if (StringUtils.isNotBlank(searchCondition.getVehicleModel()) && StringUtils.isBlank(searchCondition.getVehicleBrand())) {
        sql.append(" and (npvbm.model_name like :vehicleModel or np.select_brand_model='"+ VehicleSelectBrandModel.ALL_MODEL+"')");
      }

      if (StringUtils.isNotBlank(searchCondition.getBrand())) {
        sql.append(" and np.brand like :brand");
      }
      if (StringUtils.isNotBlank(searchCondition.getSpec())) {
        sql.append(" and np.spec like :spec");
      }
      if (StringUtils.isNotBlank(searchCondition.getModel())) {
        sql.append(" and np.model like :model");
      }

      if (StringUtils.isNotBlank(searchCondition.getCommodityCode())) {
        sql.append(" and np.commodity_code like :commodityCode");
      }
    }

    Query q = session.createSQLQuery(sql.toString());

    if (searchCondition!=null) {
      if(searchCondition.getThirdCategoryId()==null){
        if (searchCondition.getSecondCategoryId()!=null) {
          q.setLong("secondId", searchCondition.getSecondCategoryId());
        } else if (searchCondition.getFirstCategoryId()!=null && searchCondition.getSecondCategoryId()==null) {
          q.setLong("firstId", searchCondition.getFirstCategoryId());
        }
        if(StringUtils.isNotBlank(searchCondition.getProductName())) {
          q.setString("productName", "%" + searchCondition.getProductName() + "%");
        }
      }else{
        q.setLong("thirdId", searchCondition.getThirdCategoryId());
      }
      if (StringUtils.isNotBlank(searchCondition.getVehicleBrand())) {
        q.setString("vehicleBrand", "%" + searchCondition.getVehicleBrand() + "%");
      }
      if (StringUtils.isNotBlank(searchCondition.getVehicleModel())) {
        q.setString("vehicleModel", "%" + searchCondition.getVehicleModel() + "%");
      }

      if (StringUtils.isNotBlank(searchCondition.getBrand())) {
        q.setString("brand", "%" + searchCondition.getBrand() + "%");
      }
      if (StringUtils.isNotBlank(searchCondition.getSpec())) {
        q.setString("spec", "%" + searchCondition.getSpec() + "%");
      }
      if (StringUtils.isNotBlank(searchCondition.getModel())) {
        q.setString("model", "%" + searchCondition.getModel() + "%");
      }

      if (StringUtils.isNotBlank(searchCondition.getCommodityCode())) {
        q.setString("commodityCode", "%" + searchCondition.getCommodityCode() + "%");
      }
    }

    return q;
  }

  public static Query getProductByNormalProductId(Session session, Long id) {
    return session.createQuery("from Product p where p.normalProductId = :id")
        .setLong("id", id);
  }
  public static Query getSimpleProductListById(Session session, Long... id) {
    return session.createQuery("from Product p where p.id in(:id)")
        .setParameterList("id", id);
  }

  public static Query getShopAdProductLocalInfo(Session session, Long... shopIds) {
    return session.createQuery("from ProductLocalInfo pl where pl.salesStatus=:salesStatus and  pl.adStatus=:adStatus and pl.shopId in(:shopIds)")
      .setParameter("salesStatus",ProductStatus.InSales)
      .setParameter("adStatus",ProductAdStatus.ENABLED)
        .setParameterList("shopIds", shopIds);
  }

  public static Query getLastInSalesProductLocalInfo(Session session, Long shopId,int maxRows) {
    return session.createQuery("from ProductLocalInfo pl where pl.salesStatus=:salesStatus and pl.shopId =:shopId order by lastInSalesTime desc ")
      .setParameter("salesStatus",ProductStatus.InSales)
      .setParameter("shopId", shopId).setFirstResult(0).setMaxResults(maxRows);
  }


  public static Query getNormalProductByCommodityCode(Session session,String commodityCode)
  {
    return session.createQuery("from NormalProduct np where np.commodityCode =:commodityCode")
        .setString("commodityCode",commodityCode);
  }

  public static Query getNormalProductBySixProperty(Session session,NormalProductDTO normalProductDTO)
  {
    StringBuffer hql = new StringBuffer();
    hql.append("select p from NormalProduct as p where p.productName =:productName");

    if (StringUtils.isNotBlank(normalProductDTO.getBrand())) {
      hql.append(" and p.brand=:brand");
    }else {
      hql.append(" and (p.brand is null or  p.brand = '' or  p.brand = '\0')");
    }
    if (StringUtils.isNotBlank(normalProductDTO.getSpec())) {
      hql.append(" and p.spec=:spec");
    }else {
      hql.append(" and (p.spec is null or  p.spec = '' or  p.spec = '\0')");
    }
    if (StringUtils.isNotBlank(normalProductDTO.getModel())) {
      hql.append(" and p.model=:model");
    }else {
      hql.append(" and (p.model is null or  p.model = '' or  p.model = '\0')");
    }
    if (StringUtils.isNotBlank(normalProductDTO.getVehicleBrand())) {
      hql.append(" and p.vehicleBrand=:vehicleBrand");
    }else {
      hql.append(" and (p.vehicleBrand is null or  p.vehicleBrand = '' or  p.vehicleBrand = '\0')");
    }
    if (StringUtils.isNotBlank(normalProductDTO.getVehicleModel())) {
      hql.append(" and p.vehicleModel=:vehicleModel");
    }else {
      hql.append(" and (p.vehicleModel is null or  p.vehicleModel = '' or  p.vehicleModel = '\0')");
    }

    Query query = session.createQuery(hql.toString());
    query = query.setString("productName", normalProductDTO.getProductName());

    if (StringUtils.isNotBlank(normalProductDTO.getBrand())) {
      query = query.setString("brand", normalProductDTO.getBrand());
    }
    if (StringUtils.isNotBlank(normalProductDTO.getSpec())) {
      query = query.setString("spec", normalProductDTO.getSpec());
    }
    if (StringUtils.isNotBlank(normalProductDTO.getModel())) {
      query = query.setString("model", normalProductDTO.getModel());
    }

    if (StringUtils.isNotBlank(normalProductDTO.getVehicleBrand())) {
      query = query.setString("vehicleBrand", normalProductDTO.getVehicleBrand());
    }
    if (StringUtils.isNotBlank(normalProductDTO.getVehicleModel())) {
      query = query.setString("vehicleModel", normalProductDTO.getVehicleModel());
    }

    return query;
  }

  public static Query getProductCategoryByShopId(Session session, Long shopId) {
    return session.createQuery("from ProductCategory d where d.shopId =:shopId").setLong("shopId", shopId);
  }
  public static Query getProductCategoryIdsByShopId(Session session, Long shopId,int start,int pageSize) {
    return session.createQuery("select d.id from ProductCategory d where d.shopId =:shopId").setLong("shopId", shopId).setFirstResult(start).setMaxResults(pageSize);
  }

  public static Query getFirstProductCategory(Session session) {
    return session.createQuery("from ProductCategory d where d.categoryType = :type")
        .setString("type", ProductCategoryType.FIRST_CATEGORY.toString());
  }

  public static Query getAllProductCategory(Session session) {
    return session.createQuery("from ProductCategory d where d.parentId is not null and d.status=:status").setParameter("status", ProductCategoryStatus.ENABLED);
  }

  public static Query getThirdCategoryByCondition(Session session,ProductSearchCondition searchCondition) {
    StringBuffer sql = new StringBuffer();
    if (StringUtils.isBlank(searchCondition.getProductName())) {
      sql.append("select * from product_category pc where pc.category_type =:type");
    } else {
      sql.append("select * from product_category pc where pc.category_type =:type and pc.name like :name");
    }

    if (null != searchCondition.getSecondCategoryId()) {
      sql.append(" and pc.parent_id = :secondId");
    } else if (null != searchCondition.getFirstCategoryId()) {
      sql.append(" and pc.parent_id in (select pc1.id from product_category pc1 where pc1.parent_id =:firstId)");
    }

    Query q = session.createSQLQuery(sql.toString()).addEntity(ProductCategory.class)
        .setString("type", ProductCategoryType.THIRD_CATEGORY.toString()).setMaxResults(searchCondition.getLimit());


    if (StringUtils.isNotBlank(searchCondition.getProductName())) {
      q.setString("name", "%" + searchCondition.getProductName() + "%");
    }

    if (null != searchCondition.getSecondCategoryId()) {
      q.setLong("secondId", searchCondition.getSecondCategoryId());
    } else if (null != searchCondition.getFirstCategoryId()) {
      q.setLong("firstId", searchCondition.getFirstCategoryId());
    }

    return q;
  }

  public static Query getNormalProductByCondition(Session session,ProductSearchCondition searchCondition) {
    //before search : 把当前条件框后面的框中的内容都设为空
    StringBuffer sql = new StringBuffer();
    String searchFieldStr = null;
    if (SearchInputType.COMMODITY_CODE.toString().equals(searchCondition.getInputName())) {
      searchFieldStr=" distinct np.commodityCode ";
    }else if (SearchInputType.BRAND.toString().equals(searchCondition.getInputName())) {
      searchFieldStr=" distinct np.brand ";
    }else if (SearchInputType.MODEL.toString().equals(searchCondition.getInputName())) {
      searchFieldStr=" distinct np.model ";
    }else if (SearchInputType.SPEC.toString().equals(searchCondition.getInputName())) {
      searchFieldStr=" distinct np.spec ";
    }else if (SearchInputType.VEHICLE_BRAND.toString().equals(searchCondition.getInputName())) {
      searchFieldStr=" distinct np.vehicleBrand ";
    }else if (SearchInputType.VEHICLE_MODEL.toString().equals(searchCondition.getInputName())) {
      searchFieldStr=" distinct np.vehicleModel ";
    }
    sql.append("select "+searchFieldStr+" from NormalProduct np where 1=1");

    if (StringUtils.isNotBlank(searchCondition.getProductName())) {
      if (null != searchCondition.getThirdCategoryId()) {
        sql.append(" and np.productCategoryId =:productCategoryId");
      } else {
        sql.append(" and np.productName =:productName");
      }
    }

    if (StringUtils.isNotBlank(searchCondition.getBrand())) {
      if (SearchInputType.BRAND.toString().equals(searchCondition.getInputName())) {
        sql.append(" and np.brand like :brand");
      } else {
        sql.append(" and np.brand = :brand");
      }
    }


    if (StringUtils.isNotBlank(searchCondition.getSpec())) {
      if (SearchInputType.SPEC.toString().equals(searchCondition.getInputName())) {
        sql.append(" and np.spec like :spec");
      } else {
        sql.append(" and np.spec = :spec");
      }
    }

    if (StringUtils.isNotBlank(searchCondition.getModel())) {
      if (SearchInputType.MODEL.toString().equals(searchCondition.getInputName())) {
        sql.append(" and np.model like :model");
      } else {
        sql.append(" and np.model = :model");
      }
    }

    if (StringUtils.isNotBlank(searchCondition.getVehicleBrand())) {
      if (SearchInputType.VEHICLE_BRAND.toString().equals(searchCondition.getInputName())) {
        sql.append(" and np.vehicleBrand like :vehicleBrand");
      } else {
        sql.append(" and np.vehicleBrand = :vehicleBrand");
      }
    }

    if (StringUtils.isNotBlank(searchCondition.getVehicleModel())) {
      if (SearchInputType.VEHICLE_MODEL.toString().equals(searchCondition.getInputName())) {
        sql.append(" and np.vehicleModel like :vehicleModel");
      } else {
        sql.append(" and np.vehicleModel = :vehicleModel");
      }
    }

    if (StringUtils.isNotBlank(searchCondition.getCommodityCode())) {
      if (SearchInputType.COMMODITY_CODE.toString().equals(searchCondition.getInputName())) {
        sql.append(" and np.commodityCode like :commodityCode");
      } else {
        sql.append(" and np.commodityCode = :commodityCode");
      }
    }

    Query q = session.createQuery(sql.toString())
        .setFirstResult((searchCondition.getPage() - 1) * searchCondition.getLimit()).setMaxResults(searchCondition.getLimit());

    if (StringUtils.isNotBlank(searchCondition.getProductName())) {
      if (null != searchCondition.getThirdCategoryId()) {
        q.setLong("productCategoryId", searchCondition.getThirdCategoryId());
      } else {
        q.setString("productName", searchCondition.getProductName());
      }
    }

    if (StringUtils.isNotBlank(searchCondition.getBrand())) {
      if (SearchInputType.BRAND.toString().equals(searchCondition.getInputName())) {
        q.setString("brand", "%" + searchCondition.getBrand() + "%");
      } else {
        q.setString("brand", searchCondition.getBrand());
      }
    }

    if (StringUtils.isNotBlank(searchCondition.getSpec())) {
      if (SearchInputType.SPEC.toString().equals(searchCondition.getInputName())) {
        sql.append(" and np.spec like :spec");
        q.setString("spec", "%" + searchCondition.getSpec() + "%");
      } else {
        q.setString("spec", searchCondition.getSpec());
      }
    }

    if (StringUtils.isNotBlank(searchCondition.getModel())) {
      if (SearchInputType.MODEL.toString().equals(searchCondition.getInputName())) {
        q.setString("model", "%" + searchCondition.getModel() + "%");
      } else {
        q.setString("model", searchCondition.getModel());
      }
    }

    if (StringUtils.isNotBlank(searchCondition.getVehicleBrand())) {
      if (SearchInputType.VEHICLE_BRAND.toString().equals(searchCondition.getInputName())) {
        q.setString("vehicleBrand", "%" + searchCondition.getVehicleBrand() + "%");
      } else {
        q.setString("vehicleBrand", searchCondition.getVehicleBrand());
      }
    }

    if (StringUtils.isNotBlank(searchCondition.getVehicleModel())) {
      if (SearchInputType.VEHICLE_MODEL.toString().equals(searchCondition.getInputName())) {
        q.setString("vehicleModel", "%" + searchCondition.getVehicleModel() + "%");
      } else {
        q.setString("vehicleModel", searchCondition.getVehicleModel());
      }
    }
    if (StringUtils.isNotBlank(searchCondition.getCommodityCode())) {
      if (SearchInputType.COMMODITY_CODE.toString().equals(searchCondition.getInputName())) {
        q.setString("commodityCode", "%" + searchCondition.getCommodityCode() + "%");
      } else {
        q.setString("commodityCode", searchCondition.getCommodityCode());
      }
    }
    return q;
  }

  public static Query getCategoryListByIds(Session session,List<Long> ids)
  {
    return session.createQuery("select pc from ProductCategory pc where pc.id in (:ids)")
        .setParameterList("ids",ids);
  }

  public static Query countShopProductsByNormalProductId(Session session,Long id)
  {
    return session.createQuery("select count(p.id) from Product p where p.normalProductId=:id")
        .setLong("id",id);
  }

  public static Query getShopProductsByNormalProductId(Session session,Long id,int page,int limit)
  {
    return session.createQuery("select p from Product p where p.normalProductId=:id order by p.name")
        .setLong("id",id).setFirstResult((page-1)*limit).setMaxResults(limit);
  }

  public static Query getProductLocalInfoByProductIds(Session session,List<Long> productIds)
  {
    return session.createQuery("select pli from ProductLocalInfo pli where pli.productId in (:productIds)")
        .setParameterList("productIds",productIds);
  }

  public static Query getProductLocalInfoByProductIds(Session session,Long shopId,Set<Long> productIds)
  {
    return session.createQuery("select pli from ProductLocalInfo pli where pli.shopId =:shopId and  pli.productId in (:productIds)")
        .setParameterList("productIds",productIds).setLong("shopId",shopId);
  }

  public static Query getProductLocalInfoByLocalInfoIds(Session session, Long shopId, Set<Long> productIds) {
    return session.createQuery("select pli from ProductLocalInfo pli where pli.id in (:productIds) and pli.shopId =:shopId")
        .setParameterList("productIds", productIds).setLong("shopId", shopId);
  }

  public static Query countShopProductsByCondition(Session session,ProductSearchCondition searchCondition)
  {
    return session.createQuery("select count(p) from Product p where p.shopId != 1");
  }

  public static Query getShopProductsByCondition(Session session,ProductSearchCondition searchCondition)
  {
    return session.createQuery("select p from Product p where p.shopId != 1")
          .setFirstResult((searchCondition.getPage()-1)*searchCondition.getLimit()).setMaxResults(searchCondition.getLimit());
  }

  public static Query getNormalBrandByKeyWord(Session session,String keyWord)
  {
    if(StringUtils.isBlank(keyWord))
    {
      return session.createQuery("select nb from NormalBrand nb");
    }
    else
    {
      return session.createQuery("select nb from NormalBrand nb where nb.name like :keyWord")
          .setString("keyWord","%"+keyWord+"%");
    }
  }

  public static Query getNormalModelByBrandId(Session session,Long id)
  {
    return session.createQuery("select nm from NormalModel nm where nm.normalBrandId = :brandId")
        .setLong("brandId",id);
  }

  public static Query getProductByIds(Session session,List<Long> ids)
  {
    return session.createQuery("select p from Product p where  p.id in (:ids)")
        .setParameterList("ids",ids);
  }

  public static Query getNormalProductDTOByIds(Session session,List<Long> ids)
  {
    return session.createQuery("select np from NormalProduct np where np.id in (:ids)")
        .setParameterList("ids",ids);
  }

  public static Query getProductLocalInfoByNormalProductId(Session session, Long normalProductId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select pl from Product p,ProductLocalInfo pl where p.id = pl.productId and p.normalProductId=:normalProductId");
    return session.createQuery(sb.toString()).setLong("normalProductId", normalProductId);
  }

  public static Query getAllNormalProducts(Session session) {
    StringBuffer sb = new StringBuffer();
    sb.append(" from NormalProduct p ");
    return session.createQuery(sb.toString());
  }

  public static Query getModelHasBrandId(Session session)
  {
    return session.createQuery("select m from Model m where m.brandId is not null");
  }

  public static Query getBrandList(Session session,Long... id)
  {
    return session.createQuery("select b from Brand b where b.id in (:id)")
        .setParameterList("id",id);
  }

  public static Query getNormalProductDTOByCategoryId(Session session,Long categoryId)
  {
    return session.createQuery("select n from NormalProduct n where n.productCategoryId = :categoryId")
        .setLong("categoryId",categoryId);
  }


  public static Query getAllProductLocalInfoWithTradePriceAndNotInSales(Session session, Long shopId) {
    return session.createQuery("select pli from ProductLocalInfo as pli,Product as p  where pli.productId = p.id and pli.shopId=:shopId and pli.tradePrice > 0 and pli.salesStatus =:salesStatus and (p.status is null or p.status =:productStatus)")
        .setLong("shopId", shopId).setParameter("salesStatus", ProductStatus.NotInSales).setParameter("productStatus", ProductStatus.ENABLED);
  }

  public static Query countAllProductLocalInfoWithNotTradePriceAndNotInSales(Session session,Long shopId) {
    return session.createQuery("select count(pli.id) from ProductLocalInfo as pli,Product as p where pli.productId = p.id and pli.shopId=:shopId and (pli.tradePrice = 0 or pli.tradePrice is null) and pli.salesStatus =:salesStatus and (p.status is null or p.status =:productStatus) ")
        .setLong("shopId", shopId).setParameter("salesStatus", ProductStatus.NotInSales).setParameter("productStatus", ProductStatus.ENABLED);
  }

  public static Query getAllProductLocalInfInSales(Session session,Long shopId) {
    return session.createQuery("select pli from ProductLocalInfo as pli,Product as p where pli.productId = p.id and pli.shopId=:shopId and pli.salesStatus =:salesStatus and (p.status is null or p.status =:productStatus) ")
        .setLong("shopId", shopId).setParameter("salesStatus", ProductStatus.InSales).setParameter("productStatus", ProductStatus.ENABLED);
  }

  public static Query getProductInfo(Session session,ProductSearchCondition conditionDTO) {
    StringBuffer sb=new StringBuffer();
    sb.append("select pli,p from ProductLocalInfo as pli,Product as p where pli.productId = p.id and (p.status is null or p.status =:productStatus)");
    if(conditionDTO.getShopId()!=null){
      sb.append(" and pli.shopId=:shopId");
    }else if(ArrayUtil.isNotEmpty(conditionDTO.getWholesalerShopIds())){
      sb.append(" and pli.shopId in (:wholesalerShopIds)");
    }
    if(conditionDTO.getProductId()!=null){
      sb.append(" and pli.id=:id");
    }else if(ArrayUtil.isNotEmpty(conditionDTO.getProductIds())){
      sb.append(" and pli.id in (:ids)");
    }
    if(conditionDTO.getSalesStatus()!=null){
      sb.append(" and pli.salesStatus =:salesStatus");
    }
    if (conditionDTO.getSort() != null) {
      sb.append(" order by").append(" pli.").append(conditionDTO.getSortCondition().getOrderBy()).append(" ").append(conditionDTO.getSortCondition().getOrder());
      sb.append(conditionDTO.getSortCondition().toOrderString());
    }
    Query query= session.createQuery(sb.toString()).setParameter("productStatus", ProductStatus.ENABLED);
    if(conditionDTO.getShopId()!=null){
      query.setLong("shopId",conditionDTO.getShopId());
    }else if(ArrayUtil.isNotEmpty(conditionDTO.getWholesalerShopIds())){
      query.setParameterList("wholesalerShopIds",conditionDTO.getWholesalerShopIds());
    }
    if(conditionDTO.getProductId()!=null){
      query.setLong("id",conditionDTO.getProductId());
    }else if(ArrayUtil.isNotEmpty(conditionDTO.getProductIds())){
      query.setParameterList("ids",conditionDTO.getProductIds());
    }
    if(conditionDTO.getSalesStatus()!=null){
      query.setParameter("salesStatus",conditionDTO.getSalesStatus());
    }
    if(conditionDTO.getLimit()!=null){
      query.setFirstResult(conditionDTO.getStart()).setMaxResults(conditionDTO.getLimit());
    }
    return query;
  }

  public static Query getPromotionsByProductLocalInfoIds(Session session, Long shopId,Long... productLocalInfoId) {
    StringBuffer sb = new StringBuffer("select p,pp from Promotions p,PromotionsProduct pp where p.id = pp.promotionsId and p.deleted =:deleted and pp.deleted =:deleted");
    sb.append(" and p.shopId=:shopId ");
    sb.append(" and pp.productLocalInfoId in(:productLocalInfoId) ");

    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId);
    query.setParameter("deleted", DeletedType.FALSE);
    query.setParameterList("productLocalInfoId",productLocalInfoId);
    return query;
  }

  public static Query getPromotionsByProductIds(Session session,Set<Long> shopIdSet,Long... productIds) {
     StringBuffer sb = new StringBuffer("select p,pp from Promotions p,PromotionsProduct pp where p.id = pp.promotionsId and p.deleted =:deleted and pp.deleted =:deleted");
     sb.append(" and p.shopId in (:shopIdSet)");
     sb.append(" and pp.productLocalInfoId in(:productIds) ");
     Query query = session.createQuery(sb.toString()).setParameterList("shopIdSet", shopIdSet).setParameter("deleted", DeletedType.FALSE)
     .setParameterList("productIds",productIds);
     return query;
   }

//  public static Query getPromotionsProductByRange(Session session,Long shopId,Long startTime,Long endTime) {
//    StringBuffer sb = new StringBuffer("select p,pp from Promotions p,PromotionsProduct pp where p.id = pp.promotionsId and p.deleted =:deleted and pp.deleted =:deleted");
//    sb.append(" and p.shopId=:shopId ");
//    sb.append(" and (");
//    sb.append("(p.startTime>=:startTime and p.p.startTime<:=endTime)");
//    sb.append(" or (p.endTime>:=startTime and p.endTime<:=endTime)");
//    sb.append("or (p.startTime<startTime and p.endTime is NULL)");
//    sb.append(" )");
//    Query query = session.createQuery(sb.toString());
//    query.setLong("shopId", shopId);
//    query.setParameter("deleted", DeletedType.FALSE);
//    query.setLong("startTime",startTime);
//    query.setLong("endTime",endTime);
//    return query;
//  }
//
//  public static Query getPromotionsProductByUnLimitedTime(Session session,Long shopId,Long startTime) {
//    StringBuffer sb = new StringBuffer("select p,pp from Promotions p,PromotionsProduct pp where p.id = pp.promotionsId and p.deleted =:deleted and pp.deleted =:deleted");
//    sb.append(" and p.shopId=:shopId ");
//    sb.append(" and (");
//    sb.append("(p.startTime<=:endTime )");
//    sb.append(" )");
//    Query query = session.createQuery(sb.toString());
//    query.setLong("shopId", shopId);
//    query.setParameter("deleted", DeletedType.FALSE);
//    query.setLong("startTime",startTime);
//    query.setLong("endTime",endTime);
//    return query;
//  }
//

  public static Query getPromotionsRulesByProductLocalInfoIds(Session session, Long shopId,Long... productLocalInfoId) {
    StringBuffer sb = new StringBuffer("select distinct pr from Promotions p,PromotionsProduct pp,PromotionsRule pr where p.id=pp.promotionsId and p.id = pr.promotionsId and p.deleted =:deleted and pp.deleted =:deleted");
    sb.append(" and p.shopId=:shopId ");
    sb.append(" and pp.productLocalInfoId in(:productLocalInfoId) ");

    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId);
    query.setParameter("deleted",DeletedType.FALSE);
    query.setParameterList("productLocalInfoId",productLocalInfoId);
    return query;
  }

  public static Query getPromotionsRuleByPromotionsIds(Session session, Long shopId,Long[] promotionsIds) {
    StringBuffer sb = new StringBuffer("from PromotionsRule pr where pr.shopId=:shopId");
    sb.append(" and pr.promotionsId in(:promotionsIds)");
    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId).setParameterList("promotionsIds",promotionsIds);
    return query;
  }

  public static Query getPromotionsRuleMJSByRuleIds(Session session, Long shopId,Long[] ruleIds) {
    StringBuffer sb = new StringBuffer("from PromotionsRuleMJS pr where pr.shopId=:shopId");
    sb.append(" and pr.promotionsRuleId in(:ruleIds)");
    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId).setParameterList("ruleIds",ruleIds);
    return query;
  }

  public static Query getPromotionsByRange(Session session, Long shopId,PromotionsEnum.PromotionsRanges range) {
    StringBuffer sb = new StringBuffer("from Promotions p where p.deleted =:deleted");
    sb.append(" and p.shopId=:shopId ");
    sb.append(" and p.range=:range ");

    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId);
    query.setParameter("deleted",DeletedType.FALSE);
    query.setParameter("range",range);
    return query;
  }
  public static Query getPromotionsByPromotionsId(Session session, Long shopId,Long...promotionsId) {
    StringBuffer sb = new StringBuffer("from Promotions p where p.shopId=:shopId");
    sb.append(" and p.id in(:promotionsId) ");

    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId);
    query.setParameterList("id",promotionsId);
    return query;
  }

  public static Query getPromotionsRuleByPromotionsId(Session session, Long promotionsId) {
    StringBuffer sb = new StringBuffer("from PromotionsRule pp where pp.promotionsId =:promotionsId)");
    Query query = session.createQuery(sb.toString());
    query.setLong("promotionsId", promotionsId);
    return query;
  }

  public static Query getPromotionsById(Session session, Long shopId,Long... ids) {
    StringBuffer sb = new StringBuffer("from Promotions p where p.shopId=:shopId");
    if(!ArrayUtil.isEmpty(ids)){
      sb.append(" and p.id in (:ids)");
    }
    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId);
    if(!ArrayUtil.isEmpty(ids)){
      query.setParameterList("ids",ids);
    }
    return query;
  }

 public static Query getPromotionsByPromotionsType(Session session,Long shopId,PromotionsEnum.PromotionsTypes type) {
    StringBuffer sb = new StringBuffer("from Promotions p where p.shopId=:shopId and type=:type");
    return session.createQuery(sb.toString()).setLong("shopId", shopId).setString("type",type.toString());
  }

public static Query getCurrentPromotions(Session session,Long shopId) {
    StringBuffer sb = new StringBuffer("from Promotions p where p.shopId=:shopId and startTime<:now and endTime>:now");
    return session.createQuery(sb.toString()).setLong("shopId", shopId).setLong("now", System.currentTimeMillis());
  }

  public static Query cancelPromotionsByProductLocalInfoIds(Session session, Long shopId,Long... productLocalInfoId) {
    StringBuffer sb = new StringBuffer("update PromotionsProduct pp set pp.deleted=:deleted where pp.shopId=:shopId");
    sb.append(" and pp.productLocalInfoId in(:productLocalInfoId) ");

    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId);
    query.setParameter("deleted",DeletedType.TRUE);
    query.setParameterList("productLocalInfoId",productLocalInfoId);
    return query;
  }

  public static Query deletePromotionsNoProductUsed(Session session, Long shopId) {
    StringBuffer sb = new StringBuffer("update Promotions p set p.deleted=:deleted where p.id not in (select promotionsId from PromotionsProduct pp where pp.shopId=:shopId and pp.deleted='"+DeletedType.FALSE+"')");
    sb.append(" and p.shopId=:shopId ");
    sb.append(" and p.range!=:range ");

    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId);
    query.setParameter("range", PromotionsEnum.PromotionsRanges.ALL);
    query.setParameter("deleted",DeletedType.TRUE);
    return query;
  }

  public static Query cancelPromotionsByPromotionsId(Session session, Long shopId,Long promotionsId) {
    StringBuffer sb = new StringBuffer("update PromotionsProduct pp set pp.deleted=:deleted where pp.shopId=:shopId ");
    sb.append(" and pp.promotionsId =:promotionsId ");

    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId);
    query.setParameter("deleted",DeletedType.TRUE);
    query.setParameter("promotionsId",promotionsId);
    return query;
  }

  public static Query getPromotionsProductByPromotionsId(Session session, Long shopId,Long... promotionsIds) {
    StringBuffer sb = new StringBuffer("from PromotionsProduct pp where pp.shopId=:shopId");
    if(ArrayUtil.isNotEmpty(promotionsIds)){
      sb.append(" and pp.promotionsId in(:promotionsIds)");
    }
    sb.append(" and pp.deleted =:deleted ");
    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId).setParameter("deleted",DeletedType.FALSE);
    if(ArrayUtil.isNotEmpty(promotionsIds)){
      query.setParameterList("promotionsIds",promotionsIds);
    }
    return query;
  }

  public static Query getPromotionsProductDTOByProductIds(Session session, Long shopId,Long... productIds) {
    StringBuffer sb = new StringBuffer("from PromotionsProduct pp where pp.shopId=:shopId");
    sb.append(" and pp.productLocalInfoId in(:productIds)");
    sb.append(" and pp.deleted =:deleted ");
    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId).setParameter("deleted",DeletedType.FALSE);
    query.setParameterList("productIds",productIds);
    return query;
  }

  public static Query getPromotionsProductDTO(Session session,PromotionSearchCondition condition) {
    StringBuffer sb = new StringBuffer("select pp from PromotionsProduct pp,Promotions p where pp.promotionsId=p.id");
    if(condition.getShopId()!=null){
      sb.append(" and pp.shopId=:shopId");
    }
    if(ArrayUtil.isNotEmpty(condition.getProductIds())){
      sb.append(" and pp.productLocalInfoId in(:productIds)");
    }
    if(condition.getPromotionStatus()!=null){
      sb.append(" and p.status=:promotionStatus");
    }else if(CollectionUtil.isNotEmpty(condition.getPromotionStatusList())) {
      sb.append(" and p.status in (:promotionStatusList)");
    }
    if(condition.getShopKind()!=null){
      sb.append(" and p.shopKind=:shopKind");
    }
    sb.append(" and pp.deleted =:deleted ");
    if(condition.getSort()!=null){
       sb.append(" order by").append(" p.").append(condition.getSort().getOrderBy()).append(" ").append(condition.getSort().getOrder());
    }
    Query query = session.createQuery(sb.toString()).setParameter("deleted",DeletedType.FALSE);
    if(condition.getShopId()!=null){
      query.setLong("shopId", condition.getShopId());
    }
    if(ArrayUtil.isNotEmpty(condition.getProductIds())){
      query.setParameterList("productIds",condition.getProductIds());
    }
    if(condition.getShopKind()!=null){
      query.setParameter("shopKind",condition.getShopKind());
    }
    if(condition.getPromotionStatus()!=null){
      query.setParameter("promotionStatus",condition.getPromotionStatus());
    }
    if(CollectionUtil.isNotEmpty(condition.getPromotionStatusList())) {
      query.setParameterList("promotionStatusList",condition.getPromotionStatusList());
    }
     if(condition.getMaxRows()!=0){
      query.setFirstResult(condition.getStartPageNo()-1).setMaxResults(condition.getMaxRows());
    }
    return query;
  }

  public static Query getPromotionsProductDTO(Session session,Long shopId,Long productId,Long startTime,Long endTime,Long...promotionsIdList) {
    StringBuffer sb = new StringBuffer("select pp from Promotions p,PromotionsProduct pp where p.id=pp.promotionsId and p.shopId=:shopId");
    sb.append(" and p.deleted =:deleted and pp.deleted =:deleted");
    sb.append(" and pp.productLocalInfoId =:productId)");
    if(startTime!=null){
      sb.append(" and p.startTime>startTime");
    }
    if(endTime!=null){
      sb.append(" and p.endTime<endTime");
    }
    if(ArrayUtil.isNotEmpty(promotionsIdList)){
      sb.append(" and pp.promotionsId in (:promotionsIdList)");
    }
    sb.append(" and pp.deleted =:deleted ");
    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId).setLong("productId",productId);
    query.setParameter("deleted",DeletedType.FALSE);
    if(ArrayUtil.isNotEmpty(promotionsIdList)){
      query.setParameterList("promotionsIdList",promotionsIdList);
    }
    if(startTime!=null){
      query.setLong("startTime",startTime);
    }
    if(endTime!=null){
      query.setLong("endTime",endTime);
    }
    return query;
  }

 public static Query getPromotionsAreaByPromotionsIds(Session session, Long... promotionsIds) {
    StringBuffer sb = new StringBuffer("from PromotionsArea pa where ");
    sb.append(" pa.promotionsId in(:promotionsIds)");
    sb.append(" and pa.deleted =:deleted ");
    return session.createQuery(sb.toString())
      .setParameter("deleted",DeletedType.FALSE).setParameterList("promotionsIds",promotionsIds);
  }

  public static Query getPromotionsProduct(Session session,Long shopId,Long promotionsId,Long... productIds) {
    StringBuffer sb = new StringBuffer("from PromotionsProduct pp where pp.shopId=:shopId");
    sb.append(" and pp.promotionsId =:promotionsId ");
    if(ArrayUtil.isNotEmpty(productIds)){
      sb.append(" and pp.productLocalInfoId in (:productIds)");
    }
    sb.append(" and pp.deleted =:deleted ");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId);
    query.setParameter("deleted",DeletedType.FALSE);
    query.setParameter("promotionsId",promotionsId);
    if(ArrayUtil.isNotEmpty(productIds)){
      query.setParameterList("productIds",productIds);
    }
    return query;
  }

  public static Query countPromotions(Session session,PromotionIndex condition) {
    StringBuffer sb = new StringBuffer("select count(p) from Promotions p where p.shopId=:shopId and p.deleted =:deleted ");
    if(condition.getId()!=null){
      sb.append(" and p.id =:id ");
    }
     if(StringUtil.isNotEmpty(condition.getName())){
      sb.append(" and p.name like :name");
    }
    if(condition.getShopId()!=null){
      sb.append(" and p.shopId=:shopId");
    }
    if(condition.getStatus()!=null){
      sb.append(" and p.status=:status");
    }else {
      if(!ArrayUtil.isEmpty(condition.getPromotionStatusList())){
        sb.append(" and p.status in (:status)");
      }
    }
   if(condition.getType()!=null){
      sb.append(" and p.type=:type");
    }else {
      if(!ArrayUtil.isEmpty(condition.getTypes())){
        sb.append(" and p.type in (:types)");
      }
   }
    Query query = session.createQuery(sb.toString()).setLong("shopId", condition.getShopId()).setParameter("deleted",DeletedType.FALSE);
    if(condition.getId()!=null){
      query.setParameter("id",condition.getId());
    }
    if(StringUtil.isNotEmpty(condition.getName())){
      query.setParameter("name","%"+condition.getName()+"%");
    }
    if(condition.getShopId()!=null){
      query.setLong("shopId", condition.getShopId());
    }
    if(condition.getStatus()!=null){
      query.setString("status",condition.getStatus().toString());
    }else {
      if(!ArrayUtil.isEmpty(condition.getPromotionStatusList())){
        query.setParameterList("status",condition.getPromotionStatusList());
      }
    }
    if(condition.getType()!=null){
      query.setString("type",condition.getType().toString());
    }else {
      if(!ArrayUtil.isEmpty(condition.getTypes())){
        query.setParameterList("types",condition.getTypes());
      }
    }
    return query;
  }

  public static Query getPromotions(Session session,PromotionIndex condition) {
    StringBuffer sb = new StringBuffer("from Promotions p where p.deleted =:deleted ");
    if(condition.getId()!=null){
      sb.append(" and p.id =:id ");
    }else if(ArrayUtil.isNotEmpty(condition.getPromotionsIdList())){
      sb.append(" and p.id in (:idList)");
    }
    if(StringUtil.isNotEmpty(condition.getName())){
      sb.append(" and p.name like :name");
    }
    if(condition.getShopId()!=null){
      sb.append(" and p.shopId=:shopId");
    }
    if(condition.getStatus()!=null){
      sb.append(" and p.status=:status");
    }else {
      if(!ArrayUtil.isEmpty(condition.getPromotionStatusList())){
        sb.append(" and p.status in (:status)");
      }
    }

    if(condition.getType()!=null){
      sb.append(" and p.type=:type");
    }else {
      if(!ArrayUtil.isEmpty(condition.getTypes())){
        sb.append(" and p.type in (:types)");
      }
    }
    if(StringUtil.isNotEmpty(condition.getCurrentSort())&&StringUtil.isNotEmpty(condition.getSortFiled())){
      sb.append(" order by ").append(condition.getSortFiled()).append(" ").append(condition.getCurrentSort());
    }

    Query query = session.createQuery(sb.toString()).setParameter("deleted",DeletedType.FALSE);
    Pager pager=condition.getPager();
    if(pager!=null){
      query.setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
    }
    if(condition.getId()!=null){
      query.setParameter("id",condition.getId());
    }else if(ArrayUtil.isNotEmpty(condition.getPromotionsIdList())){
      query.setParameterList("idList",condition.getPromotionsIdList());
    }
    if(StringUtil.isNotEmpty(condition.getName())){
      query.setParameter("name","%"+condition.getName()+"%");
    }
    if(condition.getShopId()!=null){
      query.setLong("shopId", condition.getShopId());
    }
    if(condition.getStatus()!=null){
      query.setString("status",condition.getStatus().toString());
    }else {
      if(!ArrayUtil.isEmpty(condition.getPromotionStatusList())){
        query.setParameterList("status",condition.getPromotionStatusList());
      }
    }
    if(condition.getType()!=null){
      query.setString("type",condition.getType().toString());
    }else {
      if(!ArrayUtil.isEmpty(condition.getTypes())){
        query.setParameterList("types",condition.getTypes());
      }
    }
    return query;
  }

  public static Query getProductInSalesAndNoPromotions(Session session, Long shopId) {
    StringBuffer sb = new StringBuffer("select pli from ProductLocalInfo pli ,Product as pro where pli.productId = pro.id and pli.shopId=:shopId");
    sb.append(" and (pro.status is null or pro.status =:productStatus)");
    sb.append(" and pli.salesStatus =:salesStatus and pli.id not in (");
    sb.append(" select pp.productLocalInfoId from Promotions p,PromotionsProduct pp");
    sb.append(" where p.shopId =:shopId and p.shopId=pp.shopId");
    sb.append(" and p.id =pp.promotionsId ");
    sb.append(" and pp.deleted =:deleted ");
    sb.append(" and p.deleted =:deleted )");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId);
    query.setParameter("salesStatus",ProductStatus.InSales);
    query.setParameter("productStatus",ProductStatus.ENABLED);
    query.setParameter("deleted",DeletedType.FALSE);
    return query;
  }

public static Query getUnlimitedPromotions(Session session, Long shopId) {
    StringBuffer sb = new StringBuffer("from Promotions where shopId=:shopId and endTime is NULL and deleted=:deleted");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId);
    query.setParameter("deleted",DeletedType.FALSE);
    return query;
  }

  public static Query getPromotionsByRange(Session session, Long shopId,Long startTime,Long endTime) {
    StringBuffer sb = new StringBuffer("from Promotions p where p.shopId=:shopId and p.deleted=:deleted");
    sb.append(" and (");
    sb.append("(p.startTime<=:startTime and p.endTime>=:startTime)");
    if(endTime!=null){
      sb.append(" or (p.startTime<=:endTime and p.endTime>=:endTime)");
      sb.append(" or (p.startTime>=:startTime and p.endTime<=:endTime)");
    }else {
      sb.append("or (p.endTime>=:startTime)");
    }
    sb.append(")");
    Query query = session.createQuery(sb.toString()).setLong("shopId", shopId).setParameter("deleted",DeletedType.FALSE);
    query.setLong("startTime",startTime);
    if(endTime!=null)
    query.setLong("endTime",endTime);
    return query;
  }

  public static Query countProductInSalesByProductLocalInfoId(Session session, Long shopId,Long... productLocalInfoIds) {
    StringBuffer sb = new StringBuffer("select count(pli.id) from ProductLocalInfo pli,Product as pro ");
    sb.append(" where pli.productId = pro.id and pli.shopId=:shopId ");
    sb.append(" and (pro.status is null or pro.status =:productStatus)");
    sb.append(" and pli.salesStatus =:salesStatus");
    sb.append(" and pli.id in(:productLocalInfoIds) ");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId);
    query.setParameter("salesStatus",ProductStatus.InSales);
    query.setParameter("productStatus",ProductStatus.ENABLED);
    query.setParameterList("productLocalInfoIds",productLocalInfoIds);
    return query;
  }

  public static Query getPromotionsProductByPromotionsIdAndProductLocalInfoId(Session session, Long shopId,Long promotionsId,Long productLocalInfoId) {
    StringBuffer sb = new StringBuffer("from PromotionsProduct pp where pp.shopId=:shopId");
    sb.append(" and pp.promotionsId =:promotionsId ");
    sb.append(" and pp.productLocalInfoId =:productLocalInfoId ");
    sb.append(" and pp.deleted =:deleted ");
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId);
    query.setLong("productLocalInfoId",productLocalInfoId);
    query.setParameter("deleted",DeletedType.FALSE);
    query.setParameter("promotionsId",promotionsId);
    return query;
  }

  public static Query countProductMappingByShopIds(Session session, Long customerShopId, Long supplierShopId
      , List<ProductStatus> productStatuses, List<ProductStatus.TradeStatus> tradeStatus) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(p.id) from ProductMapping p where p.customerShopId =:customerShopId");
    sb.append(" and p.supplierShopId =:supplierShopId ");
    if (CollectionUtil.isNotEmpty(productStatuses)) {
      sb.append(" and p.status in(:productStatuses)");
    }
    if (CollectionUtil.isNotEmpty(tradeStatus)) {
      sb.append(" and p.tradeStatus in(:tradeStatus)");
    }
    Query query = session.createQuery(sb.toString())
        .setLong("customerShopId", customerShopId)
        .setLong("supplierShopId", supplierShopId);

    if (CollectionUtil.isNotEmpty(productStatuses)) {
      query.setParameterList("productStatuses", productStatuses);
    }
    if (CollectionUtil.isNotEmpty(tradeStatus)) {
      query.setParameterList("tradeStatus", tradeStatus);
    }
    return query;
  }

  public static Query getProductMappingByShopIds(Session session, Long customerShopId, Long supplierShopId
      , List<ProductStatus> productStatuses, List<ProductStatus.TradeStatus> tradeStatus,Pager pager) {
    StringBuffer sb = new StringBuffer();
    sb.append("select p from ProductMapping p where p.customerShopId =:customerShopId");
    sb.append(" and p.supplierShopId =:supplierShopId ");
    if (CollectionUtil.isNotEmpty(productStatuses)) {
      sb.append(" and p.status in(:productStatuses)");
    }
    if (CollectionUtil.isNotEmpty(tradeStatus)) {
      sb.append(" and p.tradeStatus in(:tradeStatus)");
    }
    sb.append(" order by p.id asc");
    Query query = session.createQuery(sb.toString())
        .setFirstResult(pager.getRowStart())
        .setMaxResults(pager.getPageSize())
        .setLong("customerShopId", customerShopId)
        .setLong("supplierShopId", supplierShopId);

    if (CollectionUtil.isNotEmpty(productStatuses)) {
      query.setParameterList("productStatuses", productStatuses);
    }
    if (CollectionUtil.isNotEmpty(tradeStatus)) {
      query.setParameterList("tradeStatus", tradeStatus);
    }
    return query;
  }

  public static Query getProductByIds(Session session, Long shopId, Long[] productIds) {
    return session.createQuery("select p from Product p where p.shopId = :shopId and p.id in (:ids)")
        .setLong("shopId", shopId).setParameterList("ids",productIds);
  }

  public static Query getProduct(Session session,Long shopId)
  {
    return session.createQuery("select p from Product p where p.shopId = :shopId and (status is null or status != :status)")
        .setLong("shopId",shopId).setParameter("status", ProductStatus.DISABLED);
  }

  public static Query getAllCommodityCode(Session session, Long shopId) {
    return session.createQuery("select p.commodityCode from Product p where p.shopId = :shopId and (status is null or status != :status) and p.commodityCode is not null")
        .setLong("shopId", shopId).setParameter("status", ProductStatus.DISABLED);
  }

  public static Query countAllStockProduct(Session session,Long shopId) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(pl.id) as amount from product_local_info pl left join product p on pl.product_id = p.id ");
    sb.append(" where pl.shop_id =:shopId and (p.status is null or p.status=:productStatus )");
    Query sqlQuery = session.createSQLQuery(sb.toString())
        .addScalar("amount", StandardBasicTypes.INTEGER)
        .setLong("shopId", shopId)
        .setParameter("productStatus", ProductStatus.ENABLED.name());
    return sqlQuery;
  }

 public static Query countProductInOffSales(Session session, Long shopId,ProductStatus productStatus) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(pl.id) as amount from product_local_info pl left join product p on pl.product_id = p.id ");
    sb.append(" where pl.sales_status =:salesStatus and pl.shop_id =:shopId and (p.status is null or p.status=:productStatus )");
    Query sqlQuery = session.createSQLQuery(sb.toString())
        .addScalar("amount", StandardBasicTypes.INTEGER)
        .setLong("shopId", shopId)
        .setParameter("salesStatus", productStatus.toString())
        .setParameter("productStatus", ProductStatus.ENABLED.toString());
    return sqlQuery;
  }

   public static Query countProductByPromotions(Session session, Long shopId,Long[] productIds) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(pl.id) as amount from product_local_info pl left join product p on pl.product_id = p.id ");
    sb.append(" where pl.shop_id =:shopId and pl.id in(:productIds) and (p.status is null or p.status=:productStatus )");
    Query sqlQuery = session.createSQLQuery(sb.toString())
        .addScalar("amount", StandardBasicTypes.INTEGER)
        .setLong("shopId", shopId)
        .setParameterList("productIds", productIds)
        .setParameter("productStatus", ProductStatus.ENABLED.toString());
    return sqlQuery;
  }


  public static Query getProductSupplierByShopId(Session session, Long shopId, Pager pager) {
    StringBuffer sb = new StringBuffer();
    sb.append("from ProductSupplier where shopId =: shopId");
    Query q = session.createQuery(sb.toString())
        .setLong("shopId", shopId)
        .setFirstResult(pager.getRowStart())
        .setMaxResults(pager.getPageSize());
    return q;
  }

  public static Query countProductSupplierByShopId(Session session, Long shopId) {
    StringBuffer sb = new StringBuffer();
     sb.append("select count(p.id) from ProductSupplier p where p.shopId =: shopId");
     Query q = session.createQuery(sb.toString())
         .setLong("shopId", shopId);
     return q;
  }

  public static Query getBcgogoRecommendProductIds(Session session, Long normalProductId, Double comparePrice,Long... shopIds) {
    StringBuilder sql = new StringBuilder();
    sql.append(" select pli.id from product p");
    sql.append(" left join product_local_info pli on p.id=pli.product_id ");
    sql.append(" where pli.shop_id in (:shopIds) and p.normal_product_id =:normalProductId and pli.sales_status=:salesStatus ");
    if (comparePrice != null) {
      sql.append(" and pli.trade_price <:comparePrice ");
    }
    sql.append(" and pli.trade_price > 0 order by pli.trade_price asc");
    Query q = session.createSQLQuery(sql.toString()).addScalar("id",StandardBasicTypes.LONG).setParameterList("shopIds", shopIds)
        .setLong("normalProductId", normalProductId).setString("salesStatus", ProductStatus.InSales.name());
    if (comparePrice != null) {
      q.setDouble("comparePrice", comparePrice);
    }
    return q;
  }

  public static Query countProductByNormalProductIdAndShopId(Session session, long normalProductId, long shopId, long shopProductId) {
    return session.createQuery("select count(*) from Product  where shopId = :shopId and normalProductId =:normalProductId and id=:shopProductId")
        .setLong("shopId", shopId).setLong("normalProductId", normalProductId).setLong("shopProductId", shopProductId);
  }

  public static Query getProductByNormalProductId(Session session, Long shopId, Long normalProductId) {
    return session.createQuery("from Product  where shopId = :shopId and normalProductId =:normalProductId")
        .setLong("shopId", shopId).setLong("normalProductId", normalProductId);
  }

  public static Query getShopRegisterProductList(Session session, Long shopId) {
    return session.createQuery(" from ShopRegisterProduct where shopId=:shopId  ")
        .setLong("shopId", shopId);

  }

  public static Query getCategoryParentIds(Session session,List<Long> ids)
  {
    return session.createQuery("select parentId from ProductCategory pc where pc.id in (:ids)")
      .setParameterList("ids",ids);
  }

  public static Query getPromotionOrderRecord(Session session,Long orderId){
    String hql="from PromotionOrderRecord where  orderId=:orderId";
    return session.createQuery(hql).setLong("orderId",orderId);
  }

  public static Query getPromotionOrderRecord(Session session,Long supplierShopId,Long promotionsId,Long supplierProductId){
    String hql="from PromotionOrderRecord where supplierShopId=:supplierShopId and promotionsId=:promotionsId and productId=:productId";
    return session.createQuery(hql).setLong("supplierShopId",supplierShopId).setLong("promotionsId",promotionsId).setLong("productId",supplierProductId);
  }

  public static Query getInSalingProductForSupplyDemand(Session session,Long shopId,int startPageNo,int maxSize,boolean pEndTimFlag){
    StringBuilder sb=new StringBuilder();
    sb.append("select distinct(pp.productLocalInfoId) from Promotions p,PromotionsProduct pp  where p.shopId=:shopId and pp.promotionsId=p.id and p.status=:status and p.deleted=:deleted and pp.deleted=:deleted");
    if(pEndTimFlag){
      sb.append(" and p.endTime is not null order by p.endTime asc");
    }else{
      sb.append(" and p.endTime is null");
    }
    return session.createQuery(sb.toString()).setLong("shopId",shopId).setParameter("status", PromotionsEnum.PromotionStatus.USING).setParameter("deleted",DeletedType.FALSE)
      .setFirstResult(startPageNo).setMaxResults(maxSize);
  }

  public static Query getInSalingProductWithOutPromotion(Session session,Long shopId,int startPageNo,int pageSize){
    StringBuilder sb=new StringBuilder();
    sb.append("select pli.id from ProductLocalInfo pli where pli.shopId=:shopId and pli.salesStatus=:salesStatus");
    sb.append(" and pli.id not in(select pp.productLocalInfoId from Promotions p,PromotionsProduct pp where p.shopId=:shopId and pp.promotionsId=p.id and p.status=:status)");
    sb.append(" order by pli.lastInSalesTime desc");
    return session.createQuery(sb.toString()).setLong("shopId",shopId).setParameter("salesStatus",ProductStatus.InSales).setParameter("status", PromotionsEnum.PromotionStatus.USING)
      .setFirstResult(startPageNo).setMaxResults(pageSize);
  }

  public static Query getProductCategoryByIds(Session session, Set<Long> ids) {
    return session.createQuery("from ProductCategory pc where pc.id in (:ids)").setParameterList("ids", ids);
  }

  public static Query getShopRegisterProductListByShopId(Session session, Long shopId) {
    return  session.createQuery("from ShopRegisterProduct where shopId=:shopId").setLong("shopId", shopId);
  }
  public static Query getPromotionOrderRecordUsedAmount(Session session, Long productId, Long promotionsId, Long shopId, Long orderId) {
    String hql = "select sum(amount) from PromotionOrderRecord where customerShopId = :shopId and productId = :productId and promotionsId = :promotionsId " +
        "and orderStatus in (:orderStatus) and deleted = :deleted";
    if(orderId != null){
      hql += " and orderId != :orderId";
    }
    List<OrderStatus> validStatus = new ArrayList<OrderStatus>();
    validStatus.addAll(OrderUtil.purchaseOrderInProgress);
    validStatus.addAll(OrderUtil.purchaseOrderSettled);
    Query q = session.createQuery(hql).setLong("shopId", shopId).setLong("productId", productId).setLong("promotionsId", promotionsId)
        .setParameterList("orderStatus", validStatus).setParameter("deleted", DeletedType.FALSE);
    if(orderId != null){
      q.setLong("orderId", orderId);
    }
    return q;
  }


  public static Query getThirdProductCategoryByName(Session session, Long shopId, String name) {

    if (StringUtil.isEmpty(name)) {
      return session.createQuery("from ProductCategory d where d.shopId =:shopId and id != -1 and d.categoryType =:type")
          .setLong("shopId", shopId).setParameter("type", ProductCategoryType.THIRD_CATEGORY).setMaxResults(30);

    }
    return session.createQuery("from ProductCategory d where d.shopId =:shopId and id != -1 and d.name like:name and d.categoryType =:type")
        .setLong("shopId", shopId).setString("name", "%" + name + "%").setParameter("type", ProductCategoryType.THIRD_CATEGORY).setMaxResults(30);

  }

  public static Query getNormalProductVehicleBrandModelByNormalProductId(Session session,Long... normalProductId) {
    StringBuffer hql = new StringBuffer();
    hql.append(" from NormalProductVehicleBrandModel as p where p.normalProductId in(:normalProductId) and p.deleted =:deleted");
    Query query = session.createQuery(hql.toString());
    query = query.setParameterList("normalProductId", normalProductId).setParameter("deleted",DeletedType.FALSE);
    return query;
  }

  public static Query getShopVehicleBrandModelByShopId(Session session,Long shopId) {
    StringBuffer hql = new StringBuffer();
    hql.append(" from ShopVehicleBrandModel as p where p.shopId =:shopId and (p.deleted =:deleted or p.deleted is null)");
    Query query = session.createQuery(hql.toString());
    query = query.setLong("shopId", shopId).setParameter("deleted",DeletedType.FALSE);
    return query;
  }

  public static Query getStandardVehicleBrandByName(Session session,String name,String firstLetter) {
    StringBuffer hql = new StringBuffer();
    hql.append(" from StandardVehicleBrand as p where 1=1 ");

    if (StringUtils.isNotEmpty(name)) {
      hql.append(" and name=:name ");
    }
    if (StringUtils.isNotEmpty(firstLetter)) {
      hql.append(" and firstLetter=:firstLetter ");
    }
    Query query = session.createQuery(hql.toString());
    if (StringUtils.isNotEmpty(name)) {
      query.setString("name", name);
    }
    if (StringUtils.isNotEmpty(firstLetter)) {
      query.setString("firstLetter", firstLetter);
    }

    return query;
  }

  public static Query getStandardVehicleBrandSuggestionByName(Session session, String name, String firstLetter) {
    StringBuilder hql = new StringBuilder();
    hql.append(" from StandardVehicleBrand as p where 1=1 ");

    if (StringUtils.isNotEmpty(name)) {
      hql.append(" and UPPER(name) like:name ");
    }
    if (StringUtils.isNotEmpty(firstLetter)) {
      hql.append(" and firstLetter like:firstLetter ");
    }
    hql.append(" order by frequency desc ");
    Query query = session.createQuery(hql.toString());
    if (StringUtils.isNotEmpty(name)) {
      query.setString("name", "%" + name + "%");
    } else {
      query.setMaxResults(10);
    }
    if (StringUtils.isNotEmpty(firstLetter)) {
      query.setString("firstLetter", "%" + firstLetter + "%");
    }

    return query;
  }

   public static Query getStandardVehicleModelByName(Session session,Long standardVehicleBrandId,String name) {
     StringBuffer hql = new StringBuffer();
     hql.append(" from StandardVehicleModel as p where 1=1 ");

     if (StringUtils.isNotEmpty(name)) {
       hql.append(" and name=:name ");
     }
     if (standardVehicleBrandId != null) {
       hql.append(" and standardVehicleBrandId=:standardVehicleBrandId ");
     }
     Query query = session.createQuery(hql.toString());
     if (StringUtils.isNotEmpty(name)) {
       query.setString("name", name);
     }
     if (standardVehicleBrandId != null) {
       query.setLong("standardVehicleBrandId", standardVehicleBrandId);
     }

     return query;
   }

   public static Query getStandardVehicleModelSuggestionByName(Session session,Long standardVehicleBrandId,String name) {
     StringBuffer hql = new StringBuffer();
     hql.append(" from StandardVehicleModel as p where 1=1 ");

     if (StringUtils.isNotEmpty(name)) {
       hql.append(" and UPPER(name) like :name ");
     }
     if (standardVehicleBrandId != null) {
       hql.append(" and standardVehicleBrandId=:standardVehicleBrandId ");
     }
     hql.append(" order by frequency desc ");
     Query query = session.createQuery(hql.toString());
     if (StringUtils.isNotEmpty(name)) {
       query.setString("name", "%" + name + "%");
     } else {
       query.setMaxResults(10);
     }
     if (standardVehicleBrandId != null) {
       query.setLong("standardVehicleBrandId", standardVehicleBrandId);
     }

     return query;
   }

  public static Query getCommonDictionary(Session session) {
    StringBuffer hql = new StringBuffer();
    hql.append(" from Dictionary d where d.isCommon =:isCommon ");
    Query query = session.createQuery(hql.toString());
    query.setParameter("isCommon", BooleanEnum.TRUE);
    return query;
  }

  public static Query getVehicleDictionaryByVehicleModelId(Session session, Long vehicleModelId) {
    StringBuffer hql = new StringBuffer();
    hql.append("select vd from VehicleDictionary vd,StandardVehicleModel svm where svm.id =:vehicleModelId and vd.brandId = svm.standardVehicleBrandId  ");
    Query query = session.createQuery(hql.toString());
    query.setParameter("vehicleModelId", vehicleModelId);
    return query;
  }

  public static Query getDictionaryFaultInfo(Session session, Long dictionaryId) {
    return session.createQuery("from DictionaryFaultInfo  where dictionaryId=:dictionaryId")
        .setLong("dictionaryId", dictionaryId);
  }
  public static Query getPromotionOrderRecordsByCondition(Session session, PromotionOrderRecordQuery promotionOrderRecordQuery) {
    StringBuilder sql = new StringBuilder(" from PromotionOrderRecord rec where 1=1 ");
    if (promotionOrderRecordQuery.getProductId() != null) {
      sql.append(" and rec.productId=:productId ");
    }
    if (promotionOrderRecordQuery.getOrderId() != null) {
      sql.append(" and rec.orderId=:orderId ");
    }
    if (promotionOrderRecordQuery.getShopId() != null) {
      sql.append(" and rec.customerShopId=:customerShopId ");
    }
    if (promotionOrderRecordQuery.getSupplierShopId() != null) {
      sql.append(" and rec.supplierShopId=:supplierShopId ");
    }
    Query query = session.createQuery(sql.toString());
    if (promotionOrderRecordQuery.getProductId() != null) {
      query.setLong("productId", promotionOrderRecordQuery.getProductId());
    }
    if (promotionOrderRecordQuery.getOrderId() != null) {
      query.setLong("orderId", promotionOrderRecordQuery.getOrderId());
    }
    if (promotionOrderRecordQuery.getShopId() != null) {
      query.setLong("customerShopId", promotionOrderRecordQuery.getShopId());
    }
    if (promotionOrderRecordQuery.getSupplierShopId() != null) {
      query.setLong("supplierShopId", promotionOrderRecordQuery.getSupplierShopId());
    }
    return query;
  }

  public static Query getProductCategoryRelation(Session session,Long shopId,Long ... productLocalIds){
    StringBuilder sql = new StringBuilder(" from ProductCategoryRelation where shopId=:shopId and productLocalInfoId in (:productLocalInfoIds)  ");
    return session.createQuery(sql.toString()).setLong("shopId",shopId).setParameterList("productLocalInfoIds", productLocalIds);
  }

  public static Query getPromotionOrderRecordsById(Session session, Long... id) {
    StringBuilder sql = new StringBuilder(" from PromotionOrderRecord rec where rec.id in :ids");
    return session.createQuery(sql.toString()).setParameterList("ids", id);
  }

  public static Query getStandardVehicleBrandByIds(Session session, Set<Long> ids) {
    return session.createQuery("from StandardVehicleBrand where id in (:ids)")
        .setParameterList("ids", ids);
  }

  public static Query getNameStandardVehicleBrandByNames(Session session, Set<String> names) {
    return session.createQuery("from StandardVehicleBrand where name in (:names)")
        .setParameterList("names", names);
  }

  public static Query getNameStandardVehicleModelByNames(Session session, Set<String> names) {
    return session.createQuery("from StandardVehicleModel where name in (:names)")
        .setParameterList("names", names);
  }

  public static Query getStandardVehicleModelByIds(Session session, Set<Long> ids) {
    return session.createQuery("from StandardVehicleModel where id in (:ids)")
        .setParameterList("ids", ids);
  }

  public static Query getBcgogoProductDTOByPaymentType(Session session,PaymentType paymentType) {
    StringBuilder sql = new StringBuilder(" from BcgogoProduct where 1=1");
    if(paymentType!=null){
      sql.append(" and paymentType=:paymentType");
    }
    Query query = session.createQuery(sql.toString());
    if(paymentType!=null){
      query.setParameter("paymentType",paymentType);
    }
    return query;
  }

  public static Query getBcgogoProductPropertyByProductId(Session session, Long... productId) {
    return session.createQuery("from BcgogoProductProperty where productId in(:productId) ").setParameterList("productId",productId);
  }
  public static Query getLicenseplateByCarno(Session session, String carno) {
    return session.createQuery("from Licenseplate where carno =:carno")
        .setParameter("carno", carno).setMaxResults(1);
  }

  public static Query getCommonDictionaryFaultInfo(Session session, Set<String> codes) {
    StringBuilder sb = new StringBuilder();
    sb.append("select df from DictionaryFaultInfo df,Dictionary dic where df.dictionaryId = dic.id");
    sb.append(" and dic.isCommon =:isCommon and df.faultCode in(:codes)");
    return session.createQuery(sb.toString())
        .setParameter("isCommon", BooleanEnum.TRUE)
        .setParameterList("codes", codes);
  }

  public static Query getDictionaryFaultInfoByBrandIdAndCodes(Session session, Long brandId, Set<String> codes) {
    StringBuilder sb = new StringBuilder();
    sb.append("select distinct df.* from dictionary_fault_info df left join dictionary dic on df.dictionary_id = dic.id ");
    sb.append(" left join vehicle_dictionary vd on vd.dictionary_id = dic.id ");
    sb.append("where dic.is_common =:isCommon and df.fault_code in(:codes) and vd.brand_id =:brandId");
    return session.createSQLQuery(sb.toString()).addEntity(DictionaryFaultInfo.class)
        .setParameter("isCommon", BooleanEnum.FALSE.name())
        .setParameterList("codes", codes)
        .setParameter("brandId", brandId);
  }

  public static StringBuffer buildQueryString(ProductSearchCondition searchCondition) {
    StringBuffer sql = new StringBuffer();
    if (null == searchCondition) {
      sql.append("select * from normal_product np");
    } else {
      if (null != searchCondition.getThirdCategoryId()) {
        sql.append("select * from normal_product np where np.product_category_id=:thirdId");
      } else {
        if (null != searchCondition.getSecondCategoryId()) {
          sql.append("select * from normal_product np where np.product_category_id in");
          sql.append(" (select pc.id from product_category pc where pc.parent_id =:secondId)");
        } else if (null != searchCondition.getFirstCategoryId() && null == searchCondition.getSecondCategoryId()) {
          sql.append("select * from normal_product np where np.product_category_id in");
          sql.append(" (select pc.id from product_category pc where pc.parent_id in");
          sql.append(" (select pc1.id from product_category pc1 where pc1.parent_id =:firstId))");
        }
      }

      if (StringUtils.isBlank(sql.toString())) {
        sql.append("select * from normal_product np where 1=1");
      }

      if (null == searchCondition.getThirdCategoryId() && StringUtils.isNotBlank(searchCondition.getProductName())) {
        sql.append(" and np.product_name like :productName");
      }

      if (StringUtils.isNotBlank(searchCondition.getBrand())) {
        sql.append(" and np.brand like :brand");
      }
      if (StringUtils.isNotBlank(searchCondition.getSpec())) {
        sql.append(" and np.spec like :spec");
      }
      if (StringUtils.isNotBlank(searchCondition.getModel())) {
        sql.append(" and np.model like :model");
      }
      if (StringUtils.isNotBlank(searchCondition.getVehicleBrand())) {
        sql.append(" and np.vehicle_brand like :vehicleBrand");
      }
      if (StringUtils.isNotBlank(searchCondition.getVehicleModel())) {
        sql.append(" and np.vehicle_model like :vehicleModel");
      }
      if (StringUtils.isNotBlank(searchCondition.getCommodityCode())) {
        sql.append(" and np.commodity_code like :commodityCode");
      }
    }

    sql.append(" order by np.product_name");
    return sql;
  }

  public static void setQueryParameter(Query q, ProductSearchCondition searchCondition) {
    if (null != searchCondition) {
      if (null != searchCondition.getThirdCategoryId()) {
        q.setLong("thirdId", searchCondition.getThirdCategoryId());
      } else {
        if (null != searchCondition.getSecondCategoryId()) {
          q.setLong("secondId", searchCondition.getSecondCategoryId());
        } else if (null != searchCondition.getFirstCategoryId() && null == searchCondition.getSecondCategoryId()) {
          q.setLong("firstId", searchCondition.getFirstCategoryId());
        }
      }

      if (null == searchCondition.getThirdCategoryId() && StringUtils.isNotBlank(searchCondition.getProductName())) {
        q.setString("productName", "%" + searchCondition.getProductName() + "%");
      }
      if (StringUtils.isNotBlank(searchCondition.getBrand())) {
        q.setString("brand", "%" + searchCondition.getBrand() + "%");
      }
      if (StringUtils.isNotBlank(searchCondition.getSpec())) {
        q.setString("spec", "%" + searchCondition.getSpec() + "%");
      }
      if (StringUtils.isNotBlank(searchCondition.getModel())) {
        q.setString("model", "%" + searchCondition.getModel() + "%");
      }
      if (StringUtils.isNotBlank(searchCondition.getVehicleBrand())) {
        q.setString("vehicleBrand", "%" + searchCondition.getVehicleBrand() + "%");
      }
      if (StringUtils.isNotBlank(searchCondition.getVehicleModel())) {
        q.setString("vehicleModel", "%" + searchCondition.getVehicleModel() + "%");
      }
      if (StringUtils.isNotBlank(searchCondition.getCommodityCode())) {
        q.setString("commodityCode", "%" + searchCondition.getCommodityCode() + "%");
      }
    }
  }


  public static Query getAllNormalProductByCondition(Session session, ProductSearchCondition searchCondition) {

    StringBuffer sql = buildQueryString(searchCondition);
    Query q = session.createSQLQuery(sql.toString()).addEntity(NormalProduct.class);

    setQueryParameter(q, searchCondition);
    return q;
  }

  public static Query getAdShopIdByShopArea(Session session,Long province,Long city,Long region) {
    StringBuilder sql = new StringBuilder("select distinct s.id from  config.shop s left join config.shop_ad_area sa on sa.shop_id=s.id " +
      "where s.product_ad_type='ALL' or (s.product_ad_type='PART' and sa.area_id in (:province,:city,:region)) ");
    sql.append(" order by s.ad_price_per_month desc");
    return session.createSQLQuery(sql.toString())
      .addScalar("id",StandardBasicTypes.LONG)
      .setParameter("province",province)
      .setParameter("city",city)
      .setParameter("region",region).setMaxResults(50);
  }

  public static Query getCommodityAdProductIds(Session session,Pager pager,Long... shopIds) {
    StringBuilder sql = new StringBuilder();
    sql.append("select * from(select p.id as id from product_local_info p where p.sales_status='InSales' " +
      "and p.shop_id in(:shopIds) order by p.last_in_sales_time desc limit 2) as t1");
    sql.append(" union ");
    sql.append("select * from (select p.id from product_local_info p join config.shop s on s.id=p.shop_id where p.sales_status='InSales' and p.ad_status='ENABLED' " +
      "and s.id in(:shopIds) order by s.ad_price_per_month desc) as t2");
    Query query = session.createSQLQuery(sql.toString())
      .addScalar("id",StandardBasicTypes.LONG)
       .setParameterList("shopIds",shopIds)
      .setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
    return query;
  }

  public static Query countCommodityAdProduct(Session session,Long... adShopIds) {
    StringBuilder sql = new StringBuilder();
    sql.append("select count(*) from (");
    sql.append("(select p.id as id from product_local_info p where p.sales_status='InSales' " +
      "and p.shop_id in(:shopIds) order by p.last_in_sales_time desc limit 2)");
    sql.append(" union ");
    sql.append("(select p.id from product_local_info p join config.shop s on s.id=p.shop_id where p.sales_status='InSales' and p.ad_status='ENABLED' " +
      "and s.id in(:shopIds))");
    sql.append(") as t_ad_id");
    Query query = session.createSQLQuery(sql.toString())
      .setParameterList("shopIds",adShopIds);
    return query;
  }

  public static Query getLackAutoPreBuyProductId(Session session,Long shopId,Long startDate,int autoPreBuyAmount) {
    StringBuilder sql = new StringBuilder();
    sql.append("select p.id as id from product_local_info p join  txn.inventory i on p.id=i.id  where p.shop_id=:shopId and i.amount=0 and i.last_update>:last_update order by last_sales_time desc");
    Query query = session.createSQLQuery(sql.toString())
      .addScalar("id",StandardBasicTypes.LONG)
      .setParameter("shopId",shopId)
      .setParameter("last_update",startDate)
      .setFirstResult(0).setMaxResults(autoPreBuyAmount);
    return query;
  }

  public static Query getDictionaryFaultInfoDTOByFaultCode(Session session,String faultCode) {
    StringBuilder sb = new StringBuilder();
    sb.append("from DictionaryFaultInfo where faultCode=:faultCode"  );
    return session.createQuery(sb.toString())
        .setParameter("faultCode", faultCode) .setMaxResults(1)
        ;
  }

}
