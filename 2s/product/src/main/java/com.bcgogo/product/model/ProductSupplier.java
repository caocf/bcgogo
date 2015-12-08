package com.bcgogo.product.model;


import com.bcgogo.model.LongIdentifier;
import com.bcgogo.product.dto.ProductSupplierDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 12-8-30
 * Time: 下午4:11
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "product_supplier")
@Deprecated
public class ProductSupplier extends LongIdentifier {
	private Long productId;
	private Long supplierId;
	private Long shopId;
	private Long lastUsedTime;
	private String name;
	private String contact;
	private String mobile;

	public ProductSupplier(ProductSupplierDTO productSupplierDTO) {
		productId = productSupplierDTO.getProductId();
		supplierId = productSupplierDTO.getSupplierId();
		shopId = productSupplierDTO.getShopId();
		lastUsedTime = productSupplierDTO.getLastUsedTime();
		name = productSupplierDTO.getName();
		contact = productSupplierDTO.getContact();
		mobile = productSupplierDTO.getMobile();
	}

	public ProductSupplier(){
	}

	@Column(name = "product_id")
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	@Column(name = "supplier_id")
	public Long getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Long supplierId) {
		this.supplierId = supplierId;
	}

	@Column(name = "shop_id")
	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	@Column(name = "last_used_time")
	public Long getLastUsedTime() {
		return lastUsedTime;
	}

	public void setLastUsedTime(Long lastUsedTime) {
		this.lastUsedTime = lastUsedTime;
	}

	@Column(name = "name", length = 100)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "contact", length = 100)
	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	@Column(name = "mobile", length = 100)
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public ProductSupplierDTO toDTO() {
		ProductSupplierDTO productSupplierDTO = new ProductSupplierDTO();
		productSupplierDTO.setId(this.getId());
		productSupplierDTO.setProductId(this.getProductId());
		productSupplierDTO.setSupplierId(this.getSupplierId());
		productSupplierDTO.setLastUsedTime(this.getLastUsedTime());
		productSupplierDTO.setShopId(this.getShopId());
		productSupplierDTO.setName(this.getName());
		productSupplierDTO.setContact(this.getContact());
		productSupplierDTO.setMobile(this.getMobile());
		return productSupplierDTO;
	}
}
