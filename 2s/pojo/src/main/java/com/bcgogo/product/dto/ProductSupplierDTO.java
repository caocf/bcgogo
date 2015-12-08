package com.bcgogo.product.dto;

import com.bcgogo.txn.dto.*;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.PinyinUtil;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 12-8-30
 * Time: 下午4:19
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class ProductSupplierDTO implements Serializable {
	private Long id;
	private Long productId;
	private Long supplierId;
	private Long shopId;
	private Long lastUsedTime;
	private String name;
	private String contact;
	private String mobile;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Long getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Long supplierId) {
		this.supplierId = supplierId;
	}

	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	public Long getLastUsedTime() {
		return lastUsedTime;
	}

	public void setLastUsedTime(Long lastUsedTime) {
		this.lastUsedTime = lastUsedTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String toSupplierInfoStr() {
		StringBuffer sb = new StringBuffer();
		PingyinInfo pingyinInfo = null;
		if (StringUtils.isNotBlank(name)) {
			sb.append(name).append(" ");
      pingyinInfo = PinyinUtil.getPingyinInfo(this.name);
      sb.append(pingyinInfo.pingyin).append(" ");
      sb.append(pingyinInfo.firstLetters).append(" ");
		}
		if (StringUtils.isNotBlank(contact)) {
			sb.append(contact).append(" ");
      pingyinInfo = PinyinUtil.getPingyinInfo(this.contact);
      sb.append(pingyinInfo.pingyin).append(" ");
      sb.append(pingyinInfo.firstLetters).append(" ");
		}
		if (StringUtils.isNotBlank(mobile)) {
			sb.append(mobile).append(" ");
		}
		if (supplierId != null) {
			sb.append(supplierId.toString()).append(" ");
		}
		if (sb.length() > 0) {
			sb.substring(0, sb.length() - 1);
		}
		return sb.toString();
	}

	public void setPurchaseInventory(PurchaseInventoryDTO orderDto, PurchaseInventoryItemDTO itemDTO) {
		this.setMobile(orderDto.getMobile());
		this.setName(orderDto.getSupplier());
		this.setContact(orderDto.getContact());
		this.setShopId(orderDto.getShopId());
		this.setSupplierId(orderDto.getSupplierId());
		this.setLastUsedTime(System.currentTimeMillis());
		this.setProductId(itemDTO.getProductId());
	}

	public void setPurchaseOrder(PurchaseOrderDTO orderDto, PurchaseOrderItemDTO itemDTO) {
		this.setMobile(orderDto.getMobile());
		this.setName(orderDto.getSupplier());
		this.setContact(orderDto.getContact());
		this.setShopId(orderDto.getShopId());
		this.setSupplierId(orderDto.getSupplierId());
		this.setProductId(itemDTO.getProductId());
		this.setLastUsedTime(System.currentTimeMillis());
	}

	public void setPurchaseReturn(PurchaseReturnDTO orderDto, PurchaseReturnItemDTO itemDTO) {
		this.setMobile(orderDto.getMobile());
		this.setName(orderDto.getSupplier());
		this.setContact(orderDto.getContact());
		this.setShopId(orderDto.getShopId());
		this.setSupplierId(orderDto.getSupplierId());
		this.setProductId(itemDTO.getProductId());
		this.setLastUsedTime(System.currentTimeMillis());
	}
	public ProductSupplierDTO(){

	}

	public ProductSupplierDTO(String productSupplierDetailJson) {
    Map<String, String> map = JsonUtil.jsonToStringMap(productSupplierDetailJson);
    if (StringUtils.isNotBlank(map.get("supplier_id"))) this.supplierId = Long.valueOf(map.get("supplier_id"));
    if (StringUtils.isNotBlank(map.get("product_id"))) this.productId = Long.valueOf(map.get("product_id"));
    if (StringUtils.isNotBlank(map.get("name"))) this.name = map.get("name");
    if (StringUtils.isNotBlank(map.get("mobile"))) this.mobile = map.get("mobile");
    if (StringUtils.isNotBlank(map.get("contact"))) this.name = map.get("contact");
    if (StringUtils.isNotBlank(map.get("lastUsedTime"))) this.lastUsedTime =Long.valueOf(map.get("lastUsedTime"));
  }


  public String generateProductSupplierDetail() {
    Map<String, String> map = new HashMap<String, String>();
	  if(StringUtils.isNotBlank(this.name)){
	   map.put("name", this.name);
    }
	  if(this.supplierId != null){
		 map.put("supplier_id", this.supplierId.toString());
	  }
	  if(this.productId != null){
		 map.put("product_id", this.productId.toString());
	  }

    if(StringUtils.isNotBlank(this.mobile)){
	   map.put("mobile", this.mobile);
    }

	  if(StringUtils.isNotBlank(this.contact)){
	  map.put("contact", this.contact);
    }
	  if(lastUsedTime != null){
		map.put("lastUsedTime", this.lastUsedTime.toString());
	  }
    return JsonUtil.mapToJson(map);
  }
}
