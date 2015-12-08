package com.bcgogo.product.model;

import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 12-9-24
 * Time: 下午4:43
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "product_modify_log")
@Deprecated
public class OldProductModifyLog extends LongIdentifier {
	private Long productId;
	private Long shopId;
	private String previousStorageUnit;
	private String previousSellUnit;
	private Long previousRate;
	private String followingStorageUnit;
	private String followingSellUnit;
	private Long modificationTime;
	private Long userId;
	private String operation;

	@Column(name = "product_id")
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	@Column(name = "shop_id")
	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	@Column(name = "previous_storage_unit" ,length = 20)
	public String getPreviousStorageUnit() {
		return previousStorageUnit;
	}

	public void setPreviousStorageUnit(String previousStorageUnit) {
		this.previousStorageUnit = previousStorageUnit;
	}

	@Column(name = "previous_sell_unit" ,length = 20)
	public String getPreviousSellUnit() {
		return previousSellUnit;
	}

	public void setPreviousSellUnit(String previousSellUnit) {
		this.previousSellUnit = previousSellUnit;
	}

	@Column(name = "previous_rate")
	public Long getPreviousRate() {
		return previousRate;
	}

	public void setPreviousRate(Long previousRate) {
		this.previousRate = previousRate;
	}

	@Column(name = "following_storage_unit" ,length = 20)
	public String getFollowingStorageUnit() {
		return followingStorageUnit;
	}

	public void setFollowingStorageUnit(String followingStorageUnit) {
		this.followingStorageUnit = followingStorageUnit;
	}

	@Column(name = "following_sell_unit" ,length = 20)
	public String getFollowingSellUnit() {
		return followingSellUnit;
	}

	public void setFollowingSellUnit(String followingSellUnit) {
		this.followingSellUnit = followingSellUnit;
	}

	@Column(name = "modification_time" )
	public Long getModificationTime() {
		return modificationTime;
	}

	public void setModificationTime(Long modificationTime) {
		this.modificationTime = modificationTime;
	}

	@Column(name = "user_id" )
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "operation" ,length = 50)
	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}
}
