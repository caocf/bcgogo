package com.bcgogo.search.service.CurrentUsed;

import com.bcgogo.common.Pair;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.search.dto.CurrentUsedProductDTO;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.dto.SearchMemoryConditionDTO;
import com.bcgogo.search.dto.SearchSuggestionDTO;
import com.bcgogo.search.model.CurrentUsedProduct;
import com.bcgogo.search.model.SearchDaoManager;
import com.bcgogo.search.model.SearchWriter;
import com.bcgogo.search.service.SearchService;
import com.bcgogo.search.service.product.ISearchProductService;
import com.bcgogo.search.util.SolrQueryUtils;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.BcgogoOrderDto;
import com.bcgogo.utils.SearchConstant;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-5-23
 * Time: 下午1:58
 * 常用产品的service
 * 负责保存更新 产品品名与品牌
 */

@Component
public class ProductCurrentUsedService implements IProductCurrentUsedService {
  private static final Logger LOG = LoggerFactory.getLogger(SearchService.class);

	private ISearchProductService searchProductService ;

	public ISearchProductService getSearchProductService() {
		if(searchProductService == null){
			return ServiceManager.getService(ISearchProductService.class);
		}
		return searchProductService;
	}

	public void setSearchProductService(ISearchProductService searchProductService) {
		this.searchProductService = searchProductService;
	}

	/**
   * 品牌下拉框更新，对每个单据通过多线程处理
   *
   * @param bcgogoOrderDto 每个单据的dto
   */
  public void currentUsedProductSaved(BcgogoOrderDto bcgogoOrderDto) {
    currentUsedProductSaved(bcgogoOrderDto.getShopId(), bcgogoOrderDto.getCurrentUsedProductDTOList());
  }

  public void currentUsedProductSaved(Long shopId, List<CurrentUsedProductDTO> currentUsedProductDTOList){
    if (CollectionUtils.isEmpty(currentUsedProductDTOList)) return;
    //更新常用商品数据库数据
    this.currentUsedProductDBSaved(shopId, currentUsedProductDTOList);
    //更新常用商品内存数据
    this.currentUsedProductMemorySaved(shopId, currentUsedProductDTOList);
  }

  //从memcache中取常用数据，如果不存在，先从数据库查找，然后放入memcache中
  @Override
  public List<CurrentUsedProduct> getCurrentUsedProductsFromMemory(SearchMemoryConditionDTO searchMemoryConditionDTO) {
    List<CurrentUsedProduct> currentUsedProductList = (List<CurrentUsedProduct>) MemCacheAdapter.get(getMemCacheKey(searchMemoryConditionDTO));
    if (LOG.isDebugEnabled()) {
      if (CollectionUtils.isNotEmpty(currentUsedProductList)) {
        for (CurrentUsedProduct cup : currentUsedProductList) {
          LOG.debug("memcache CurrentUsedProduct is " + cup.getProductName() + " , " + cup.getBrand() + ".");
        }
      } else {
        LOG.debug("memcache CurrentUsedProduct is null .");
      }
    }
    if (CollectionUtils.isEmpty(currentUsedProductList)) {
      SearchWriter writer = getSearchDaoManager().getWriter();
      currentUsedProductList = writer.getCurrentUsedProduct(searchMemoryConditionDTO);
      MemCacheAdapter.add(getMemCacheKey(searchMemoryConditionDTO), currentUsedProductList);
    }
    return currentUsedProductList;
  }

  //更新数据库CurrentUsedProduct
  public void currentUsedProductDBSaved(Long shopId, List<CurrentUsedProductDTO> currentUsedProductDTOs) {
    if (CollectionUtils.isEmpty(currentUsedProductDTOs)) return;
    LOG.debug("{} DBSaved:{}", shopId, currentUsedProductDTOs);
    SearchWriter writer = getSearchDaoManager().getWriter();
    SearchMemoryConditionDTO searchMemoryConditionDTO = new SearchMemoryConditionDTO();
    searchMemoryConditionDTO.setShopId(shopId);
    Object status = writer.begin();
    try {
      //更新品牌
      searchMemoryConditionDTO.setSearchField(SearchMemoryConditionDTO.PRODUCT_BRAND);
      List<CurrentUsedProductDTO> currentUsedProductDTOListByProductBrand = getCurrentUsedProductDTOListByProductBrand(currentUsedProductDTOs);
      if (CollectionUtils.isNotEmpty(currentUsedProductDTOListByProductBrand)) {
        List<CurrentUsedProduct> currentUsedProductListByBrand = writer.getCurrentUsedProduct(searchMemoryConditionDTO);
        this.updateCurrentUsedProductDTOsImpl(writer, currentUsedProductDTOListByProductBrand, currentUsedProductListByBrand, SearchConstant.PRODUCT_BRAND);
      }
      //更新品名
      searchMemoryConditionDTO.setSearchField(SearchMemoryConditionDTO.PRODUCT_NAME);
      List<CurrentUsedProductDTO> currentUsedProductDTOListByProductName = getCurrentUsedProductDTOListByProductName(currentUsedProductDTOs);
      if (CollectionUtils.isEmpty(currentUsedProductDTOListByProductName)) return;
      List<CurrentUsedProduct> currentUsedProductListByProductName = writer.getCurrentUsedProduct(searchMemoryConditionDTO);
      this.updateCurrentUsedProductDTOsImpl(writer, currentUsedProductDTOListByProductName, currentUsedProductListByProductName, SearchConstant.PRODUCT_NAME);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  //更新memcached CurrentUsedProduct
  public void currentUsedProductMemorySaved(Long shopId, List<CurrentUsedProductDTO> currentUsedProductDTOList) {
    if (CollectionUtils.isEmpty(currentUsedProductDTOList)) return;
    if (LOG.isDebugEnabled())
      LOG.debug(shopId + " MemorySaved:" + currentUsedProductDTOList);
    SearchMemoryConditionDTO searchMemoryConditionDTO = new SearchMemoryConditionDTO();
    searchMemoryConditionDTO.setShopId(shopId);
    searchMemoryConditionDTO.setSearchField(SearchMemoryConditionDTO.PRODUCT_BRAND);
    List<CurrentUsedProductDTO> currentUsedProductDTOListByProductBrand = getCurrentUsedProductDTOListByProductBrand(currentUsedProductDTOList);
    if (CollectionUtils.isNotEmpty(currentUsedProductDTOListByProductBrand)) {
      List<CurrentUsedProduct> currentUsedProductListByBrand = this.getCurrentUsedProductsFromMemory(searchMemoryConditionDTO);
      currentUsedProductListByBrand = this.updateCurrentUsedProductDTOsImpl(null, currentUsedProductDTOListByProductBrand, currentUsedProductListByBrand, SearchConstant.PRODUCT_BRAND);
      MemCacheAdapter.set(getMemCacheKey(searchMemoryConditionDTO), currentUsedProductListByBrand);
    }
    //更新品名
    searchMemoryConditionDTO.setSearchField(SearchMemoryConditionDTO.PRODUCT_NAME);
    List<CurrentUsedProductDTO> currentUsedProductDTOListByProductName = getCurrentUsedProductDTOListByProductName(currentUsedProductDTOList);
    if (CollectionUtils.isEmpty(currentUsedProductDTOListByProductName)) return;
    List<CurrentUsedProduct> currentUsedProductListByProductName = this.getCurrentUsedProductsFromMemory(searchMemoryConditionDTO);
    currentUsedProductListByProductName = this.updateCurrentUsedProductDTOsImpl(null, currentUsedProductDTOListByProductName, currentUsedProductListByProductName, SearchConstant.PRODUCT_NAME);
    MemCacheAdapter.set(getMemCacheKey(searchMemoryConditionDTO), currentUsedProductListByProductName);
  }


  private List<CurrentUsedProduct> updateCurrentUsedProductDTOsImpl(SearchWriter writer, List<CurrentUsedProductDTO> currentUsedProductDTOList, List<CurrentUsedProduct> currentUsedProductList, String productType) {
    boolean flag = false;
    int selectOptionNum = SolrQueryUtils.getSelectOptionNumber();
    //list中存在此product 更新此product order
    for (CurrentUsedProductDTO currentUsedProductDTO : currentUsedProductDTOList) {
      for (CurrentUsedProduct currentUsedProduct : currentUsedProductList) {
        if (currentUsedProduct.equals(currentUsedProductDTO, productType)) {
          if (writer != null)
            writer.update(currentUsedProduct.fromDTO(currentUsedProductDTO));
          else
            currentUsedProduct.fromDTO(currentUsedProductDTO);
          flag = true;
          break;
        }
      }
      if (flag) {
        flag = false;
        continue;
      }
      //判断list中有没有此product
      if (currentUsedProductList.size() < selectOptionNum) {
        CurrentUsedProduct currentUsedProduct = new CurrentUsedProduct(currentUsedProductDTO);
        if (writer != null)
          writer.save(currentUsedProduct);
        currentUsedProductList.add(currentUsedProduct);
        continue;
      }
      //其他情况 更新掉最后一个
      //根据时间排序
      currentUsedProductList = this.sortList(currentUsedProductList);
      //判断list中存在此product 根据使用时间更新一条数据
      if (writer != null) {
        writer.update(currentUsedProductList.get(selectOptionNum - 1).fromDTO(currentUsedProductDTO));
      } else {
        //判断list中存在此product 根据使用时间更新一条数据
        currentUsedProductList.remove(selectOptionNum - 1);
        currentUsedProductList.add(new CurrentUsedProduct(currentUsedProductDTO));
      }
    }
    if (writer == null)
      return this.sortList(currentUsedProductList);
    else
      return null;
  }

  private List<CurrentUsedProduct> sortList(List<CurrentUsedProduct> currentUsedProductList) {
    Collections.sort(currentUsedProductList, new Comparator<CurrentUsedProduct>() {
      @Override
      public int compare(CurrentUsedProduct pou1, CurrentUsedProduct pou2) {
        if (pou1.getTimeOrder() == null) {
          return 1;
        }
        if (pou2.getTimeOrder() == null) {
          return -1;
        }
        try {                          //02<01 ?-1:1  从大到小顺序
          return pou2.getTimeOrder().compareTo(pou1.getTimeOrder());
        } catch (Exception e) {
          if (pou2.getTimeOrder() == null || pou1.getTimeOrder() == null) {
            LOG.error("db-shop: " + pou1.getShopId() + " orderTime id is null!", e);
          }
          return -1;
        }
      }
    });
    return currentUsedProductList;
  }


  public List<CurrentUsedProductDTO> getCurrentUsedProductDTOListByProductBrand(List<CurrentUsedProductDTO> currentUsedProductDTOListFromOrder) {
    Set<String> productSet = new HashSet<String>();
    List<CurrentUsedProductDTO> currentUsedProductDTOList = new ArrayList<CurrentUsedProductDTO>();
    for (CurrentUsedProductDTO currentUsedProductDTO : currentUsedProductDTOListFromOrder) {
      if (StringUtils.isBlank(currentUsedProductDTO.getProductName()) || StringUtils.isBlank(currentUsedProductDTO.getBrand()))
        continue;
      if (!productSet.add(currentUsedProductDTO.getBrand())) continue;
      CurrentUsedProductDTO cupDTO = new CurrentUsedProductDTO();
      cupDTO.setShopId(currentUsedProductDTO.getShopId());
      cupDTO.setBrand(currentUsedProductDTO.getBrand());
      cupDTO.setTimeOrder(System.currentTimeMillis());
      cupDTO.setProductName(currentUsedProductDTO.getProductName());
      cupDTO.setType(SearchConstant.PRODUCT_BRAND);
      currentUsedProductDTOList.add(cupDTO);
    }
    return currentUsedProductDTOList;
  }

  public List<CurrentUsedProductDTO> getCurrentUsedProductDTOListByProductName(List<CurrentUsedProductDTO> currentUsedProductDTOsFromOrder) {
    Set<String> brandSet = new HashSet<String>();
    List<CurrentUsedProductDTO> currentUsedProductDTOList = new ArrayList<CurrentUsedProductDTO>();
    CurrentUsedProductDTO cupDTO = null;
    for (CurrentUsedProductDTO currentUsedProductDTO : currentUsedProductDTOsFromOrder) {
      if (StringUtils.isBlank(currentUsedProductDTO.getProductName())) continue;
      if (!brandSet.add(currentUsedProductDTO.getProductName())) continue;
      cupDTO = new CurrentUsedProductDTO();
      cupDTO.setShopId(currentUsedProductDTO.getShopId());
      cupDTO.setTimeOrder(System.currentTimeMillis());
      cupDTO.setProductName(currentUsedProductDTO.getProductName());
      cupDTO.setType(SearchConstant.PRODUCT_NAME);
      currentUsedProductDTOList.add(cupDTO);
    }
    return currentUsedProductDTOList;
  }

  //memcache的KEY
  public String getMemCacheKey(SearchMemoryConditionDTO searchMemoryConditionDTO) {
    if(SearchConditionDTO.PRODUCT_INFO.equals(searchMemoryConditionDTO.getSearchField())){
      return MemcachePrefix.currentUsed.getValue() + SearchConditionDTO.PRODUCT_NAME + "_" + String.valueOf(searchMemoryConditionDTO.getShopId());
    }
    return MemcachePrefix.currentUsed.getValue() + searchMemoryConditionDTO.getSearchField() + "_" + String.valueOf(searchMemoryConditionDTO.getShopId());
  }


  @Autowired
  private SearchDaoManager searchDaoManager;


  public SearchDaoManager getSearchDaoManager() {
    return searchDaoManager;
  }

  private IConfigService configService;

  private IConfigService getConfigService() {
    if (configService == null) {
      configService = ServiceManager.getService(IConfigService.class);
    }
    return configService;
  }

  @Override
  public void saveRecentChangedProductInMemory(Long shopId,Map<Long, Pair<Long, Boolean>> newRecentChangedProductMap) throws Exception{
    //现在solr已经没有延迟30秒 所以直接return
    if(true){
      return;
    }
    LOG.debug(shopId + " 更新 Memory Cache");
    Map<Long, Pair<Long, Boolean>> recentChangedProductMap = this.getRecentChangedProductFromMemory(shopId);
    if (newRecentChangedProductMap != null && newRecentChangedProductMap.size() > 0) {
	    Set<Long> productIdSet = newRecentChangedProductMap.keySet();
	    Long[] productIds = productIdSet.toArray(new Long[productIdSet.size()]);
	    List<ProductDTO> productDTOs = getSearchProductService().queryProductByLocalInfoIds(shopId,productIds);
	    Map<Long,ProductDTO> productDTOMap = new HashMap<Long, ProductDTO>();
	    if(CollectionUtils.isNotEmpty(productDTOs)){
		    for(ProductDTO productDTO :productDTOs){
			    if(productDTO == null || productDTO.getProductLocalInfoId() == null) continue;
			    productDTOMap.put(productDTO.getProductLocalInfoId(),productDTO);
		    }
	    }
	    for(int i=0,len = productIds.length;i<len;i++){
		    if(productDTOMap.get(productIds[i]) == null){
			    newRecentChangedProductMap.put(productIds[i],new Pair<Long, Boolean>(System.currentTimeMillis(),true));
		    }else {
			    newRecentChangedProductMap.put(productIds[i],new Pair<Long, Boolean>(System.currentTimeMillis(),false));
		    }
	    }
      //更新 新增
      if (LOG.isDebugEnabled())
        LOG.debug(shopId + " MemorySaved:" + newRecentChangedProductMap.size()+"个");
      recentChangedProductMap.putAll(newRecentChangedProductMap);
    }
    MemCacheAdapter.set(MemcachePrefix.productInventory.getValue() + shopId, recentChangedProductMap);
  }

	@Override
	public void saveRecentDeletedProductInMemory(Long shopId, Map<Long, Long> newRecentDeletedProductMap) throws Exception {
    //现在solr已经没有延迟30秒 所以直接return
    if(true){
      return;
    }
    Map<Long, Long> recentDeletedProductMap = this.getRecentDeletedProductFromMemory(shopId);
		if (newRecentDeletedProductMap != null && newRecentDeletedProductMap.size() > 0) {
			recentDeletedProductMap.putAll(newRecentDeletedProductMap);
			//删除  recentDeletedProductMap 中相应的记录
			Map<Long, Pair<Long, Boolean>> recentChangedProductMap = this.getRecentChangedProductFromMemory(shopId);
			if (recentChangedProductMap != null && recentChangedProductMap.size() > 0) {
				Iterator<Map.Entry<Long, Long>> iterator = newRecentDeletedProductMap.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry<Long, Long> entry = iterator.next();
					if (recentChangedProductMap.get(entry.getKey()) != null) {
						recentChangedProductMap.remove(entry.getKey());
						LOG.debug("cache中存在的产品ID:{},该商品已经被逻辑删除，将被移除recentChangedProductMap", entry.getKey());
					}
				}
				MemCacheAdapter.set(MemcachePrefix.productInventory.getValue() + shopId, recentChangedProductMap);
			}
		}
		MemCacheAdapter.set(MemcachePrefix.productDelete.getValue() + shopId, recentDeletedProductMap);
	}

	@Override
  public void saveRecentChangedProductInMemory(BcgogoOrderDto bcgogoOrderDto) throws Exception{
    //现在solr已经没有延迟30秒 所以直接return
    if(true){
      return;
    }
    saveRecentChangedProductInMemory(bcgogoOrderDto.getShopId(),bcgogoOrderDto.generateRecentChangedProductDTOList());
  }

  @Override
  public Map<Long, Pair<Long, Boolean>> getRecentChangedProductFromMemory(Long shopId) {
    Map<Long, Pair<Long, Boolean>> recentChangedProductMap = (Map<Long, Pair<Long, Boolean>>) MemCacheAdapter.get(MemcachePrefix.productInventory.getValue() + shopId);
    //remove  过期的
    if (recentChangedProductMap != null) {
      LOG.debug("cache中recentChangedProductMap size:"+recentChangedProductMap.size());
      int expirationTime = SolrQueryUtils.getRecentChangedProductExpirationTime();
      Iterator<Map.Entry<Long, Pair<Long, Boolean>>> iterator = recentChangedProductMap.entrySet().iterator();
      while (iterator.hasNext()) {
        Map.Entry<Long, Pair<Long, Boolean>> entry = iterator.next();
        LOG.debug("cache中存在的产品ID:{},该商品的状态为 ：{},cache存在时间为：{}ms",
		                 new Object[]{entry.getKey(), entry.getValue().getValue(), System.currentTimeMillis() - entry.getValue().getKey()});
        if (System.currentTimeMillis() - entry.getValue().getKey() >= expirationTime * 1000) {
          LOG.debug("cache中过期的产品ID:" + entry.getKey());
          iterator.remove();
        }
      }
      MemCacheAdapter.set(MemcachePrefix.productInventory.getValue() + shopId, recentChangedProductMap);
    } else {
      LOG.debug("cache中recentChangedProductMap is NULL");
      recentChangedProductMap = new HashMap<Long, Pair<Long, Boolean>>();
    }
    return recentChangedProductMap;
  }

	@Override
	public Map<Long, Long> getRecentDeletedProductFromMemory(Long shopId) {
	 Map<Long,Long> recentDeletedProductMap = (Map<Long, Long>) MemCacheAdapter.get(MemcachePrefix.productDelete.getValue() + shopId);
		if (recentDeletedProductMap != null) {
			LOG.debug("shopId:{},cache中recentDeletedProductMap size:{}",shopId,recentDeletedProductMap.size());
			//remove 过期的
			int expirationTime = SolrQueryUtils.getRecentChangedProductExpirationTime();
			Iterator<Map.Entry<Long, Long>> iterator = recentDeletedProductMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<Long, Long> entry = iterator.next();
				if (System.currentTimeMillis() - entry.getValue() >= expirationTime * 1000) {
					LOG.debug("productDeleteCache中过期的产品ID:" + entry.getKey());
					iterator.remove();
				}
			}
		} else {
      recentDeletedProductMap = new HashMap<Long,Long>();
    }
		return recentDeletedProductMap;
	}


  @Override
  public List<SearchSuggestionDTO> getProductSuggestionFromMemory(SearchMemoryConditionDTO searchMemoryConditionDTO) throws Exception {
    List<SearchSuggestionDTO> suggestionResults = new ArrayList<SearchSuggestionDTO>();
    //先从内存中找
    //得到 下拉框前5个常用product  memcache
    List<CurrentUsedProduct> currentUsedProductFilterList = new ArrayList<CurrentUsedProduct>();

    List<CurrentUsedProduct> currentUsedProductList = getCurrentUsedProductsFromMemory(searchMemoryConditionDTO);
    if (CollectionUtils.isNotEmpty(currentUsedProductList)) {
      for (CurrentUsedProduct currentUsedProduct : currentUsedProductList) {
        //如果是品牌 加上 product_name field 过滤
        if (searchMemoryConditionDTO.searchFieldEquals(SearchConditionDTO.PRODUCT_BRAND) && (StringUtils.isBlank(searchMemoryConditionDTO.getProductName()) || searchMemoryConditionDTO.getProductName().equals(currentUsedProduct.getProductName()))) {
          currentUsedProductFilterList.add(currentUsedProduct);
        } else if (searchMemoryConditionDTO.searchFieldEquals(SearchConditionDTO.PRODUCT_NAME,SearchConditionDTO.PRODUCT_INFO)) {
          currentUsedProductFilterList.add(currentUsedProduct);
        }
      }
    }

    SearchSuggestionDTO searchSuggestionDTO = null;
    for (CurrentUsedProduct currentUsedProduct : currentUsedProductFilterList) {
      searchSuggestionDTO = new SearchSuggestionDTO(searchMemoryConditionDTO.getUuid());
      if (StringUtil.isAllEmpty(currentUsedProduct.getProductName(), currentUsedProduct.getBrand())) continue;
      if (searchMemoryConditionDTO.searchFieldEquals(SearchConditionDTO.PRODUCT_NAME,SearchConditionDTO.PRODUCT_INFO)) {
        searchSuggestionDTO.addEntry(currentUsedProduct.getType(), currentUsedProduct.getProductName());
      } else {
        searchSuggestionDTO.addEntry(currentUsedProduct.getType(), currentUsedProduct.getBrand());
      }
      suggestionResults.add(searchSuggestionDTO);
    }
    return suggestionResults;
  }
}
