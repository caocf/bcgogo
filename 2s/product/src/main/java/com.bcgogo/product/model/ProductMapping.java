package com.bcgogo.product.model;

import com.bcgogo.enums.ProductStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.product.dto.ProductMappingDTO;
import com.bcgogo.txn.dto.PurchaseInventoryDTO;
import com.bcgogo.txn.dto.PurchaseInventoryItemDTO;
import com.bcgogo.txn.dto.PurchaseOrderDTO;
import com.bcgogo.txn.dto.PurchaseOrderItemDTO;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.UnitUtil;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 12-11-13
 * Time: 下午1:22
 * To change this template use File | Settings | File Templates.
 */

/**
 * 表中的两个productId均为productLocalInfo中的id
 */
@Entity
@Table(name = "product_mapping")
public class ProductMapping extends LongIdentifier  {
	private Long customerProductId;
	private Long customerShopId;
	private Long supplierProductId;
	private Long supplierShopId;
  private Long supplierId;
	private ProductStatus status;
  private ProductStatus.TradeStatus tradeStatus;
  private Long customerLastPurchaseDate;
  private Double customerLastPurchaseAmount;
  private Double customerLastPurchasePrice;


	public ProductMapping() {
	}

  public ProductMapping(PurchaseInventoryDTO purchaseInventoryDTO,PurchaseInventoryItemDTO purchaseInventoryItemDTO) {
    supplierId =  purchaseInventoryDTO.getSupplierId();
    supplierShopId = purchaseInventoryDTO.getSupplierShopId();
    customerShopId = purchaseInventoryDTO.getShopId();
    customerProductId = purchaseInventoryItemDTO.getProductId();
    supplierProductId = purchaseInventoryItemDTO.getSupplierProductId();
    status = ProductStatus.ENABLED;
    if(UnitUtil.isStorageUnit(purchaseInventoryItemDTO.getUnit(), purchaseInventoryItemDTO)){
      this.setCustomerLastPurchaseAmount(NumberUtil.round(purchaseInventoryItemDTO.getAmount() * purchaseInventoryItemDTO.getRate(), 1));
      this.setCustomerLastPurchasePrice(NumberUtil.round(NumberUtil.doubleVal(purchaseInventoryItemDTO.getPurchasePrice())/purchaseInventoryItemDTO.getRate(),NumberUtil.MONEY_PRECISION));
    }else{
      this.setCustomerLastPurchaseAmount(purchaseInventoryItemDTO.getAmount());
      this.setCustomerLastPurchasePrice(NumberUtil.doubleVal(purchaseInventoryItemDTO.getPurchasePrice()));
    }
    customerLastPurchaseDate = purchaseInventoryDTO.getVestDate();

  }
  @Column(name="customer_last_purchase_date" )
  public Long getCustomerLastPurchaseDate() {
    return customerLastPurchaseDate;
  }

  public void setCustomerLastPurchaseDate(Long customerLastPurchaseDate) {
    this.customerLastPurchaseDate = customerLastPurchaseDate;
  }
  @Column(name="customer_last_purchase_amount")
  public Double getCustomerLastPurchaseAmount() {
    return customerLastPurchaseAmount;
  }

  public void setCustomerLastPurchaseAmount(Double customerLastPurchaseAmount) {
    this.customerLastPurchaseAmount = customerLastPurchaseAmount;
  }

  @Column(name="customer_last_purchase_price")
  public Double getCustomerLastPurchasePrice() {
    return customerLastPurchasePrice;
  }

  public void setCustomerLastPurchasePrice(Double customerLastPurchasePrice) {
    this.customerLastPurchasePrice = customerLastPurchasePrice;
  }

  @Column(name = "customer_product_id",nullable=false)
	public Long getCustomerProductId() {
		return customerProductId;
	}

	public void setCustomerProductId(Long customerProductId) {
		this.customerProductId = customerProductId;
	}

	@Column(name = "customer_shop_id",nullable=false)
	public Long getCustomerShopId() {
		return customerShopId;
	}

	public void setCustomerShopId(Long customerShopId) {
		this.customerShopId = customerShopId;
	}

	@Column(name = "supplier_product_id",nullable=false)
	public Long getSupplierProductId() {
		return supplierProductId;
	}

	public void setSupplierProductId(Long supplierProductId) {
		this.supplierProductId = supplierProductId;
	}

	@Column(name = "supplier_shop_id",nullable=false)
	public Long getSupplierShopId() {
		return supplierShopId;
	}

	public void setSupplierShopId(Long supplierShopId) {
		this.supplierShopId = supplierShopId;
	}
  @Column(name = "supplier_id")
  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }

  @Column(name = "status")
	@Enumerated(EnumType.STRING)
	public ProductStatus getStatus() {
		return status;
	}

	public void setStatus(ProductStatus status) {
		this.status = status;
	}

  @Column(name = "trade_status")
	@Enumerated(EnumType.STRING)
  public ProductStatus.TradeStatus getTradeStatus() {
    return tradeStatus;
  }

  public void setTradeStatus(ProductStatus.TradeStatus tradeStatus) {
    this.tradeStatus = tradeStatus;
  }

	public ProductMappingDTO toDTO(){
		ProductMappingDTO productMappingDTO = new ProductMappingDTO();
    productMappingDTO.setId(this.getId());
		productMappingDTO.setCustomerProductId(this.customerProductId);
		productMappingDTO.setCustomerShopId(this.customerShopId);
		productMappingDTO.setStatus(this.status);
    productMappingDTO.setTradeStatus(this.getTradeStatus());
		productMappingDTO.setSupplierShopId(this.supplierShopId);
		productMappingDTO.setSupplierProductId(this.supplierProductId);
    productMappingDTO.setSupplierId(this.supplierId);
    productMappingDTO.setCustomerLastPurchaseAmount(this.customerLastPurchaseAmount);
    productMappingDTO.setCustomerLastPurchaseDate(this.customerLastPurchaseDate);
    productMappingDTO.setCustomerLastPurchasePrice(this.customerLastPurchasePrice);
		return productMappingDTO;
	}

	public void fromDTO(ProductMappingDTO productMappingDTO){
		if(productMappingDTO == null){
			return;
		}
		this.setCustomerProductId(productMappingDTO.getCustomerProductId());
		this.setCustomerShopId(productMappingDTO.getCustomerShopId());
		this.setSupplierProductId(productMappingDTO.getSupplierProductId());
		this.setSupplierShopId(productMappingDTO.getSupplierShopId());
    this.setSupplierId(productMappingDTO.getSupplierId());
		this.setStatus(productMappingDTO.getStatus());
    this.setCustomerLastPurchaseAmount(productMappingDTO.getCustomerLastPurchaseAmount());
    this.setCustomerLastPurchaseDate(productMappingDTO.getCustomerLastPurchaseDate());
    this.setCustomerLastPurchasePrice(productMappingDTO.getCustomerLastPurchasePrice());
	}
}
