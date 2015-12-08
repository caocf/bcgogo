package com.bcgogo.config.dto.upYun;


import org.springframework.util.Assert;

/**
 * @author xzhu
 */
public class UpYunPolicyDTO {
  public static String DEFAULT_CONTENT_LENGTH_RANGE="0,5120000";//5M

	public static String BUCKET="bucket";// 空间名
	public static String EXPIRATION="expiration";// 过期时间
	public static String SAVE_KEY="save-key";///{year}/{mon}/{random}{.suffix}",命名规则，/2011/12/随机.扩展名
	public static String ALLOW_FILE_TYPE="allow-file-type";//"jpg,jpeg,gif,png", 仅允许上传图片
	public static String CONTENT_LENGTH_RANGE="content-length-range";//"0,102400",文件在 100K 以下
	public static String RETURN_URL="return-url";//"http://localhost/formtest/
  public static String NOTIFY_URL="notify-url";
  public static String NAME_CONTENT_TYPE="content-type";//指定文件类型	一般系统通过扩展名自动识别
  public static String IMAGE_WIDTH_RANGE = "image-width-range";	 //图片宽度范围	 可选	 格式：最小,最大，如 0,1024
  public static String CONTENT_MD5="content-md5";//	 文件校验码
  public static String CONTENT_SECRET="content-secret";//	 图片高度范围	 可选	 格式：最小,最大，如 0,1024
  public static String X_GMKERL_THUMBNAIL="x-gmkerl-thumbnail";//	 缩略图版本名称	 可选	 注意：必须是需图片空间，且使用该功能将丢弃原图。  如果文件类空间要使用原图缩小保存功能，必须设置 x-gmkerl-type 和 x-gmkerl-value 两个参数；
  public static String X_GMKERL_TYPE="x-gmkerl-type";//缩略图类型	 可选	 fix_width等
  public static String X_GMKERL_VALUE="x-gmkerl-value";//缩略图参数
  public static String X_GMKERL_QUALITY="x-gmkerl-quality";//缩略图质量
  public static String X_GMKERL_UNSHARP="x-gmkerl-unsharp";//图片锐化
  public static String X_GMKERL_ROTATE="x-gmkerl-rotate";//图片旋转
  public static String X_GMKERL_CROP="x-gmkerl-crop";//图片裁剪
  public static String X_GMKERL_EXIF_SWITCH="x-gmkerl-exif-switch";//是否保留exif信息 参数值：true (保留 EXIF 信息)

  public static String EXT_PARAM="ext-param";//	 额外参数	 可选	 最大长度为255个字节，仅支持 UTF-8 格式


	private String name;
	private Object value;

	public UpYunPolicyDTO(String name, Object value) {
    Assert.notNull(name,"name should not be null");
    Assert.notNull(value,"value should not be null");
    this.name = name;
    this.value = value;
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}
}
