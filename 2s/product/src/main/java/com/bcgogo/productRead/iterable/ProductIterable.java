package com.bcgogo.productRead.iterable;

import com.bcgogo.iterable.BatchGetDataIterable;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.productRead.service.IProductReadService;
import com.bcgogo.service.ServiceManager;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-7-22
 * Time: 下午5:15
 * To change this template use File | Settings | File Templates.
 */
public class ProductIterable extends BatchGetDataIterable<ProductDTO> {
  private Long shopId;

  public ProductIterable() {
  }

  public ProductIterable(Long shopId) {
    this.shopId = shopId;
  }
  @Override
  protected List<ProductDTO> getBatch(int start, int rows) {
   IProductReadService productReadService = ServiceManager.getService(IProductReadService.class);
    return productReadService.getProductDTOList(shopId, start, rows);
  }

}
