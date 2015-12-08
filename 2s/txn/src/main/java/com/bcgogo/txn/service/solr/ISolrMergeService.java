package com.bcgogo.txn.service.solr;

import com.bcgogo.product.dto.ProductDTO;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 12-9-20
 * Time: 上午10:02
 * To change this template use File | Settings | File Templates.
 */
public interface ISolrMergeService {

	void mergeCacheProductDTO(Long shopId,List<ProductDTO> productDTOs)throws Exception;

}
