package com.bcgogo.product.dto;

import com.bcgogo.base.BaseDTO;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.txn.dto.InventoryDTO;
import com.bcgogo.utils.StringUtil;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 12-11-13
 * Time: 下午1:39
 * To change this template use File | Settings | File Templates.
 */
public class ProductMappingDTO extends BaseDTO implements Serializable {

	private Long customerProductId;
	private Long customerShopId;
	private Long supplierProductId;
	private Long supplierShopId;
  private Long supplierId;
	private ProductStatus status;
	private ProductDTO customerProductDTO;
	private ProductDTO supplierProductDTO;
	private InventoryDTO customerInventoryDTO;
  private Long[] customerProductIds;
  private Long[] supplierProductIds;
  private ProductStatus.TradeStatus tradeStatus;

  private Long customerLastPurchaseDate;
  private Double customerLastPurchaseAmount;
  private Double customerLastPurchasePrice;

  public Long getCustomerLastPurchaseDate() {
    return customerLastPurchaseDate;
  }

  public void setCustomerLastPurchaseDate(Long customerLastPurchaseDate) {
    this.customerLastPurchaseDate = customerLastPurchaseDate;
  }

  public Double getCustomerLastPurchaseAmount() {
    return customerLastPurchaseAmount;
  }

  public void setCustomerLastPurchaseAmount(Double customerLastPurchaseAmount) {
    this.customerLastPurchaseAmount = customerLastPurchaseAmount;
  }

  public Double getCustomerLastPurchasePrice() {
    return customerLastPurchasePrice;
  }

  public void setCustomerLastPurchasePrice(Double customerLastPurchasePrice) {
    this.customerLastPurchasePrice = customerLastPurchasePrice;
  }

  public Long getCustomerProductId() {
		return customerProductId;
	}

	public void setCustomerProductId(Long customerProductId) {
		this.customerProductId = customerProductId;
	}

	public Long getCustomerShopId() {
		return customerShopId;
	}

	public void setCustomerShopId(Long customerShopId) {
		this.customerShopId = customerShopId;
	}

	public Long getSupplierProductId() {
		return supplierProductId;
	}

	public void setSupplierProductId(Long supplierProductId) {
		this.supplierProductId = supplierProductId;
	}

	public Long getSupplierShopId() {
		return supplierShopId;
	}

	public void setSupplierShopId(Long supplierShopId) {
		this.supplierShopId = supplierShopId;
	}

	public ProductStatus getStatus() {
		return status;
	}

	public void setStatus(ProductStatus status) {
		this.status = status;
	}

	public ProductDTO getCustomerProductDTO() {
		return customerProductDTO;
	}

	public void setCustomerProductDTO(ProductDTO customerProductDTO) {
		this.customerProductDTO = customerProductDTO;
	}

	public ProductDTO getSupplierProductDTO() {
		return supplierProductDTO;
	}

	public void setSupplierProductDTO(ProductDTO supplierProductDTO) {
		this.supplierProductDTO = supplierProductDTO;
	}

  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }

	public InventoryDTO getCustomerInventoryDTO() {
		return customerInventoryDTO;
	}

	public void setCustomerInventoryDTO(InventoryDTO customerInventoryDTO) {
		this.customerInventoryDTO = customerInventoryDTO;
	}

  public Long[] getCustomerProductIds() {
    return customerProductIds;
  }

  public void setCustomerProductIds(Long[] customerProductIds) {
    this.customerProductIds = customerProductIds;
  }

  public Long[] getSupplierProductIds() {
    return supplierProductIds;
  }

  public void setSupplierProductIds(Long[] supplierProductIds) {
    this.supplierProductIds = supplierProductIds;
  }

  public ProductStatus.TradeStatus getTradeStatus() {
    return tradeStatus;
  }

  public void setTradeStatus(ProductStatus.TradeStatus tradeStatus) {
    this.tradeStatus = tradeStatus;
  }

	public boolean isProductMappingEnabled() {
		if (customerProductDTO != null && supplierProductDTO != null) {
			if (StringUtil.isEqual(customerProductDTO.getName(), supplierProductDTO.getName())
					    && StringUtil.isEqual(customerProductDTO.getBrand(), supplierProductDTO.getBrand())
					    && StringUtil.isEqual(customerProductDTO.getModel(), supplierProductDTO.getModel())
					    && StringUtil.isEqual(customerProductDTO.getProductVehicleBrand(), supplierProductDTO.getProductVehicleBrand())
					    && StringUtil.isEqual(customerProductDTO.getProductVehicleModel(), supplierProductDTO.getProductVehicleModel())
					    && !ProductStatus.ENABLED.equals(customerProductDTO.getStatus())
					    && !ProductStatus.ENABLED.equals(supplierProductDTO.getStatus())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
