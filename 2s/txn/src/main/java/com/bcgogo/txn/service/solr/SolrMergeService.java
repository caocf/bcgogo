package com.bcgogo.txn.service.solr;

import com.bcgogo.common.Pair;
import com.bcgogo.constant.ProductConstants;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductSupplierDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.search.service.CurrentUsed.IProductCurrentUsedService;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.SupplierInventoryDTO;
import com.bcgogo.txn.service.productThrough.IProductThroughService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 12-9-20
 * Time: 上午10:09
 * To change this template use File | Settings | File Templates.
 */
@Component
@Deprecated
public class SolrMergeService implements ISolrMergeService {
	private static final Logger LOG = LoggerFactory.getLogger(SolrMergeService.class);

	@Override
	public void mergeCacheProductDTO(Long shopId, List<ProductDTO> productDTOs) throws Exception {
    //现在solr已经没有延迟30秒 所以直接return
    if(true){
      return;
    }

		ISearchService searchService = ServiceManager.getService(ISearchService.class);
		IProductService productService = ServiceManager.getService(IProductService.class);
    IProductThroughService productThroughService = ServiceManager.getService(IProductThroughService.class);
		List<Long> addedProductLocalInfoIdList = new ArrayList<Long>();
		List<Long> updatedProductLocalInfoIdList = new ArrayList<Long>();
		List<Long> deletedProductLocalInfoIdList = new ArrayList<Long>();
		generateAddedAndUpdatedProductLocalInfoIdList(productDTOs,shopId,addedProductLocalInfoIdList,updatedProductLocalInfoIdList,deletedProductLocalInfoIdList);
		LOG.debug("addedProductLocalInfoIdList cache中新增的产品数量:{}", addedProductLocalInfoIdList.size());
		LOG.debug("updatedProductLocalInfoIdList cache中更新的并且跟solr有交集的产品数量:{}", updatedProductLocalInfoIdList.size());
		Set<Long> productIdsForSupplier = new HashSet<Long>(addedProductLocalInfoIdList);
		productIdsForSupplier.addAll(updatedProductLocalInfoIdList);
    Map<Long,List<SupplierInventoryDTO>> supplierInventoryMap = new HashMap<Long, List<SupplierInventoryDTO>>();
		if(productIdsForSupplier !=null && !productIdsForSupplier.isEmpty()){
      supplierInventoryMap = productThroughService.getSupplierInventoryMap(shopId,productIdsForSupplier);
		}

		 //delete product
		if(CollectionUtils.isNotEmpty(deletedProductLocalInfoIdList)){
			Iterator<ProductDTO> iterator = productDTOs.iterator();
			while (iterator.hasNext()){
				ProductDTO productDTO = iterator.next();
				if(productDTO.getProductLocalInfoId() == null){
					continue;
				}
				if(deletedProductLocalInfoIdList.contains(productDTO.getProductLocalInfoId())){
					iterator.remove();
				}
			}
		}
		  //merge update的product信息
		if (CollectionUtils.isNotEmpty(updatedProductLocalInfoIdList) && CollectionUtils.isNotEmpty(productDTOs)) {
			Set<Long> updatedProductLocalInfoIdSet = new HashSet<Long>(updatedProductLocalInfoIdList.size());
			for (Long temp : updatedProductLocalInfoIdList) {
				if (temp != null) {
					updatedProductLocalInfoIdSet.add(temp);
				}
			}
			Map<Long, ProductDTO> updatedProductMap = productService.getProductDTOMapByProductLocalInfoIds(new HashSet<Long>(updatedProductLocalInfoIdList));
			List<InventorySearchIndex> inventorySearchIndexList = searchService.searchInventorySearchIndexByProductIds(shopId, updatedProductLocalInfoIdList.toArray(new Long[updatedProductLocalInfoIdList.size()]));
			Map<Long, Integer> productDTOIndexMap = new HashMap<Long, Integer>();
			for (ProductDTO productDTO : productDTOs) {
				productDTOIndexMap.put(productDTO.getProductLocalInfoId(), productDTOs.indexOf(productDTO));
			}
			Integer tempProductDTOIndex = null;
			for (InventorySearchIndex inventorySearchIndex : inventorySearchIndexList) {
				tempProductDTOIndex = productDTOIndexMap.get(inventorySearchIndex.getProductId());
				if (tempProductDTOIndex != null) {
					ProductDTO productDTOFromInventorySearchIndex = new ProductDTO(inventorySearchIndex.toDTO());
					if (updatedProductMap != null) {
						ProductDTO updatedProductDTO = updatedProductMap.get(inventorySearchIndex.getProductId());
						if (updatedProductDTO != null) {
							if(supplierInventoryMap != null && !supplierInventoryMap.isEmpty()){
								 List<SupplierInventoryDTO> supplierInventoryDTOs =  supplierInventoryMap.get(updatedProductDTO.getProductLocalInfoId());
								 if(CollectionUtils.isNotEmpty(supplierInventoryDTOs)){
									productDTOFromInventorySearchIndex.setSupplierInventoryDTOs(supplierInventoryDTOs);
								 }
							}
							productDTOFromInventorySearchIndex.setTradePrice(updatedProductDTO.getTradePrice());
							productDTOFromInventorySearchIndex.setStorageBin(updatedProductDTO.getStorageBin());
							productDTOFromInventorySearchIndex.setSellUnit(updatedProductDTO.getSellUnit());
							productDTOFromInventorySearchIndex.setStorageUnit(updatedProductDTO.getStorageUnit());
							productDTOFromInventorySearchIndex.setCommodityCode(updatedProductDTO.getCommodityCode());
						}
					}
					productDTOs.set(tempProductDTOIndex, productDTOFromInventorySearchIndex);
				}
			}
		}
		 //merge added的product信息
		if (CollectionUtils.isNotEmpty(addedProductLocalInfoIdList)) {
			List<InventorySearchIndex> inventorySearchIndexList = searchService.searchInventorySearchIndexByProductIds(shopId, addedProductLocalInfoIdList.toArray(new Long[addedProductLocalInfoIdList.size()]));
			Map<Long, ProductDTO> addedProductMap = productService.getProductDTOMapByProductLocalInfoIds(new HashSet<Long>(addedProductLocalInfoIdList));
			for (InventorySearchIndex inventorySearchIndex : inventorySearchIndexList) {
				ProductDTO productDTOFromInventorySearchIndex = new ProductDTO(inventorySearchIndex.toDTO());
				if (addedProductMap != null) {
					ProductDTO addedProductDTO = addedProductMap.get(inventorySearchIndex.getProductId());
					if (addedProductDTO != null) {
						if (supplierInventoryMap != null && !supplierInventoryMap.isEmpty()) {
							List<SupplierInventoryDTO> supplierInventoryDTOs = supplierInventoryMap.get(addedProductDTO.getProductLocalInfoId());
							if (CollectionUtils.isNotEmpty(supplierInventoryDTOs)) {
								productDTOFromInventorySearchIndex.setSupplierInventoryDTOs(supplierInventoryDTOs);
							}
						}
						productDTOFromInventorySearchIndex.setTradePrice(addedProductDTO.getTradePrice());
						productDTOFromInventorySearchIndex.setStorageBin(addedProductDTO.getStorageBin());
						productDTOFromInventorySearchIndex.setSellUnit(addedProductDTO.getSellUnit());
						productDTOFromInventorySearchIndex.setStorageUnit(addedProductDTO.getStorageUnit());
						productDTOFromInventorySearchIndex.setCommodityCode(addedProductDTO.getCommodityCode());
					}
				}
				productDTOs.add(productDTOFromInventorySearchIndex);
			}
		}
	}

	/**
	 * 1.cache 有&&新增   solr 有   这种数据不存
	 * 2.cache 有&&不是新增  solr 有   这种数据 solr 是旧的   要查db后 替换掉
	 * 3.cache 有&&新增   solr 没有   这种数据需要直接跟随在结果集后面显示
	 * 4.cache 有&&不是新增  solr 没有   这种数据不属于用户需要的
	 * 5.cache 没有                   就根据solr 的查询结果集为准
	 *
	 * @param productDTOs
	 * @param shopId
	 */
	private void generateAddedAndUpdatedProductLocalInfoIdList(List<ProductDTO> productDTOs, Long shopId, List<Long> addedProductLocalInfoIdList,
	                                                           List<Long> updatedProductLocalInfoIdList,List<Long> deletedProductLocalInfoIdList) {
	  IProductCurrentUsedService productCurrentUsedService = ServiceManager.getService(IProductCurrentUsedService.class);
	  Map<Long, Pair<Long, Boolean>> changeProductMap = productCurrentUsedService.getRecentChangedProductFromMemory(shopId);
		Map<Long, Long> deletedProductMap = productCurrentUsedService.getRecentDeletedProductFromMemory(shopId);
		List<Long> solrProductLocalInfoIdList = new ArrayList<Long>();
		if (CollectionUtils.isNotEmpty(productDTOs)) {
			for (ProductDTO productDTO : productDTOs) {
				solrProductLocalInfoIdList.add(productDTO.getProductLocalInfoId());
			}
			LOG.debug("productDTOs solr搜索出的产品:{}", solrProductLocalInfoIdList);
		}
	  if (changeProductMap != null && !changeProductMap.isEmpty()) {
	    //交集 并且不是新增
	    List<Long> interProductLocalInfoIdList = new ArrayList<Long>(changeProductMap.keySet());
	    interProductLocalInfoIdList.retainAll(solrProductLocalInfoIdList);
	    for (Long productLocalInfoId : interProductLocalInfoIdList) {
	      if (!changeProductMap.get(productLocalInfoId).getValue())
	        updatedProductLocalInfoIdList.add(productLocalInfoId);
	    }
	    //cache中 去掉 交集部分
	    addedProductLocalInfoIdList.addAll(changeProductMap.keySet());
	    addedProductLocalInfoIdList.removeAll(interProductLocalInfoIdList);
	    //排除 不是新增的
	    Iterator<Long> addedIter = addedProductLocalInfoIdList.iterator();
	    while (addedIter.hasNext()) {
	      if (!changeProductMap.get(addedIter.next()).getValue())
	        addedIter.remove();
	    }
	  }
		if (deletedProductMap != null && !deletedProductMap.isEmpty()) {
			List<Long> deletedProductId = new ArrayList<Long>(deletedProductMap.keySet());
			deletedProductId.retainAll(solrProductLocalInfoIdList);
			deletedProductLocalInfoIdList.addAll(deletedProductId);
		}
	}

}
