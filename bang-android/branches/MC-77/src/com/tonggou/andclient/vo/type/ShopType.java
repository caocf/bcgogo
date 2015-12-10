package com.tonggou.andclient.vo.type;

/**
 * 商店类型
 * 
 * <ul>
 * 	<li> {@link #ALL}
 * 	<li> {@link #SHOP_4S}
 * </ul>
 * 
 * @author lwz
 *
 */
public enum ShopType {
	/**
	 * 所有商店
	 */
	ALL("ALL"),	
	
	/**
	 * 4S 店
	 */
	SHOP_4S("SHOP_4S");
	
	private String value;
	
	private ShopType(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
}
