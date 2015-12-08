package com.bcgogo.utils;

/**
 * Created by IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 12-10-22
 * Time: 下午4:52
 * To change this template use File | Settings | File Templates.
 */
public class ValidatorConstant {

	public static final String DELETED_PRODUCT_SAVE_MSG = "商品已经被删除，如果继续操作，该商品将被激活！";

	public static final String NO_DELETED_PRODUCT_MSG = "没有发现被删除的商品！";

	public static final String ORDER_IS_NULL_MSG = "结算失败，单据信息为空！";

  public static final String SELLER_PRODUCT_LACKING_MSG = "卖家商品库存不足或者卖家没发货！";

	public static final String REQUEST_ERROR_MSG = "网络异常，请联系客服！";

	public static final String DELETED_PRODUCT_REPEAL_MSG = "商品已经被删除，恢复该商品库存后才能作废本单据！\n您是否确定要恢复商品库存并作废本单据？";

  public static final String PRODUCT_HAVE_DELETED_MSG = "该商品已经删除，请勿重复操作!";

	public static final String PRODUCT_INVENTORY_NOT_EMPTY_MSG = "该商品库存大于0，不能删除！";

  public static final String PRODUCT_IN_SALES_MSG = "该商品正在上架销售中，不能删除！";

	public static final String PRODUCT_HAVE_UNSETTLED_ORDER_MSG = "该商品有未结算单据，请结算或作废后删除!";

  public static final String SUPPLIER_ACCOUNT_FAIL = "结算失败，单据信息已经被修改！";

	public static final String DISABLED_SERVICE_COPY_MSG = "服务已经被删除，复制时将不显示该服务！";

  public static final String ORDER_NEW_PRODUCT_ERROR = "本单据不能使用新商品,请修改后再提交！";

  public static final String ORDER_PRODUCT_MATCH_SUPPLIER_ERROR = "有部分商品不属于当前供应商,请修改后再提交！";

	public static final String WHOLESALER_PRODUCT_NOT_EXIST = "供应商没有上架销售上述商品，请重新选择商品！";

	public static final String WHOLESALER_COMMODITY_CODE_CONFLICT = "批发商商品上述商品与本地商品编码重复，请修改商品编码！";

	public static final String GOTO_PRODUCT_SEARCH = "goodsindex.do?method=creategoodsindex&commodityCode=";

  public static final String ORDER_STATUS_CORRECT = "单据状态符合，可以结算！";

  public static final String ORDER_STATUS_NO_CORRECT = "单据状态不正确！";

  public static final String ORDER_SETTLED_SUCCESS = "结算成功";

  public static final String ORDER_SETTLED_FAILURE = "结算失败";

	public static final String NOT_ENOUGH_INVENTORY_TO_DISPATCH = "商品库存不足，无法发货";

	public static final String NO_REPAIR_PICKING_TO_OUT_STORAGE = "领料单不存在，无法出库";
  public static final String RETAIN_DEFAULT_STOREHOUSE_MSG = "对不起,必须保留一个默认仓库！";

  public static final String STOREHOUSE_INVENTORY_AMOUNT_NOT_EMPTY_MSG = "对不起,该仓库中还有商品库存，不能被删除！";

  public static final String STOREHOUSE_NAME_NULL_MSG = "对不起,仓库名称不能为空！";

  public static final String STOREHOUSE_NAME_EXIST_MSG = "对不起,仓库名称不能重复！";

  public static final String STOREHOUSE_USED_PROCESSING_ORDER_MSG = "对不起,有未结算单据在使用当前仓库,仓库不能被删除！";

  public static final String REPAIR_PICKING_USED_PROCESSING_ORDER_MSG = "对不起,有未结算的施工单或维修领料单，维修领料开关不能关闭！";

  public static final String REPAIR_USED_PROCESSING_ORDER_MSG = "对不起,有未结算的施工单，维修领料开关不能打开，请先结算或作废该单据！";

  public static final String ORDER_NULL_MSG = "对不起,单据内容不能为空!";

  public static final String  PRODUCT_INVENTORY_LACK = "对不起,商品库存不足,请重新输入！";

  public static final String  PRODUCT_STOREHOUSE_INVENTORY_LACK = "对不起,当前仓库商品库存不足,是否进行调拨?";

  public static final String  STOREHOUSE_NULL_MSG = "对不起,仓库不能为空！";

  public static final String ORDER_STATEMENT_ACCOUNTED = "作废失败，单据已对账！";

  public static final String  STOREHOUSE_DELETED_MSG = "对不起,当前仓库已经被删除！";

  public static final String  ALLOCATE_OR_PURCHASE = "调拨还是入库？";

  public static final String  DEFAULT_REFUSE_APPLY_MSG = "本公司经营范围与您的公司不符！";

  public static final String REGISTER_SUCCESS = "注册成功，请等待审核！<BR>如有疑问，请拨打客服电话：0512-66733331";

  public static final String  SUPPLIER_INVENTORY_LACK = "对不起,商品供应商库存不足！";

  public static final String CUSTOMER_NAME_DUPLICATE = "请选择是新增用户还是现有用户！";

  public static final String DEPOSIT_LACK = "供应商定金余额不足,不能结算！";


}


