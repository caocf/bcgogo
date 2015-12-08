package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 12-10-16
 * Time: 下午4:44
 * To change this template use File | Settings | File Templates.
 */
public enum ProductStatus {
  //商品删除使用
	ENABLED("有效"),
	DISABLED("失效"),
  TEMP("临时"),
  //商品上下架使用
  NotInSales("下架"),
  InSales("上架");
	private final String name;

	private ProductStatus(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

  public enum TradeStatus {
    PURCHASE("店铺采购"),
    INVENTORY("店铺入库"),
    PURCHASE_INVENTORY("店铺采购并入库"),
    SETTLED("批发商结算"),
    TRADE_FINISHED("交易完成");

    private final String name;
    private TradeStatus(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

}
