package com.bcgogo.config.model;

import com.bcgogo.config.dto.image.ImageVersionConfigDTO;
import com.bcgogo.enums.common.ObjectStatus;
import com.bcgogo.enums.config.ImageFormat;
import com.bcgogo.enums.config.ThumbnailsType;
import com.bcgogo.enums.config.WaterMarkPosition;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-7-26
 * Time: 下午5:34
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "image_version_config")
public class ImageVersionConfig extends LongIdentifier {
  private Long shopId;
  private String name;//版本名称，加在图片访问URL后缀，必须与实际图片云存储空间中的配置命名一致；
  private ObjectStatus status;

  private ThumbnailsType thumbnailsType;//缩略方式
  private Integer sizeFixWidth;//设定宽度（如果不限制，置空）；
  private Integer sizeFixHeight;//设定高度（如果不限制，置空）；
  private Integer lessenValue;//缩小比例： %（输入1-99整数值）

  private int gif2jpgThumb;//将动态GIF转换成静态图片 （0表示否|1表示是）；
  private int needSharpen;//是否锐化（0表示否|1表示是）；
  private int needWatermark;//是否添加水印（0表示否|1表示是）；
  private WaterMarkPosition watermarkPosition;//水印位置；
  private String watermarkName;//水印名称；
  private Integer watermarkOpacity;//透明度；
  private Integer watermarkMarginX;//距离左边；
  private Integer watermarkMarginY;//距离上边；
  private ImageFormat format;//静态图片输出格式 默认：gif/jpg/png将保留原来格式，其余图片格式将转换为jpg格式。该设置仅对静态图片有效（含动态gif转静态后的图片）。
  private Integer quality;//图片质量： 1-100整数值，数值越大质量越好 图片质量设置仅对jpg/webp格式输出有效，一般建议设置65-75。
  private String description;//描述；

  @Column(name="shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name="name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  public ObjectStatus getStatus() {
    return status;
  }

  public void setStatus(ObjectStatus status) {
    this.status = status;
  }
  @Enumerated(EnumType.STRING)
  @Column(name = "thumbnails_type")
  public ThumbnailsType getThumbnailsType() {
    return thumbnailsType;
  }

  public void setThumbnailsType(ThumbnailsType thumbnailsType) {
    this.thumbnailsType = thumbnailsType;
  }

  @Column(name="size_fix_width")
  public Integer getSizeFixWidth() {
    return sizeFixWidth;
  }

  public void setSizeFixWidth(Integer sizeFixWidth) {
    this.sizeFixWidth = sizeFixWidth;
  }

  @Column(name="size_fix_height")
  public Integer getSizeFixHeight() {
    return sizeFixHeight;
  }

  public void setSizeFixHeight(Integer sizeFixHeight) {
    this.sizeFixHeight = sizeFixHeight;
  }

  @Column(name="need_sharpen")
  public int getNeedSharpen() {
    return needSharpen;
  }

  public void setNeedSharpen(int needSharpen) {
    this.needSharpen = needSharpen;
  }

  @Column(name="need_watermark")
  public int getNeedWatermark() {
    return needWatermark;
  }

  public void setNeedWatermark(int needWatermark) {
    this.needWatermark = needWatermark;
  }
  @Enumerated(EnumType.STRING)
  @Column(name = "watermark_position")
  public WaterMarkPosition getWatermarkPosition() {
    return watermarkPosition;
  }

  public void setWatermarkPosition(WaterMarkPosition watermarkPosition) {
    this.watermarkPosition = watermarkPosition;
  }
  @Column(name="watermark_name")
  public String getWatermarkName() {
    return watermarkName;
  }

  public void setWatermarkName(String watermarkName) {
    this.watermarkName = watermarkName;
  }

  @Column(name="watermark_opacity")
  public Integer getWatermarkOpacity() {
    return watermarkOpacity;
  }

  public void setWatermarkOpacity(Integer watermarkOpacity) {
    this.watermarkOpacity = watermarkOpacity;
  }
  @Column(name="lessen_value")
  public Integer getLessenValue() {
    return lessenValue;
  }

  public void setLessenValue(Integer lessenValue) {
    this.lessenValue = lessenValue;
  }

  @Column(name="gif2jpg_thumb")
  public int getGif2jpgThumb() {
    return gif2jpgThumb;
  }

  public void setGif2jpgThumb(int gif2jpgThumb) {
    this.gif2jpgThumb = gif2jpgThumb;
  }

  @Column(name="watermark_margin_x")
  public Integer getWatermarkMarginX() {
    return watermarkMarginX;
  }

  public void setWatermarkMarginX(Integer watermarkMarginX) {
    this.watermarkMarginX = watermarkMarginX;
  }
  @Column(name="watermark_margin_y")
  public Integer getWatermarkMarginY() {
    return watermarkMarginY;
  }

  public void setWatermarkMarginY(Integer watermarkMarginY) {
    this.watermarkMarginY = watermarkMarginY;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "format")
  public ImageFormat getFormat() {
    return format;
  }

  public void setFormat(ImageFormat format) {
    this.format = format;
  }

  @Column(name="quality")
  public Integer getQuality() {
    return quality;
  }

  public void setQuality(Integer quality) {
    this.quality = quality;
  }

  @Column(name="description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ImageVersionConfigDTO toDTO(){
    ImageVersionConfigDTO imageVersionConfigDTO = new ImageVersionConfigDTO();
    imageVersionConfigDTO.setShopId(this.getShopId());
    imageVersionConfigDTO.setId(this.getId());
    imageVersionConfigDTO.setDescription(this.getDescription());
    imageVersionConfigDTO.setFormat(this.getFormat());
    imageVersionConfigDTO.setGif2jpgThumb(this.getGif2jpgThumb());
    imageVersionConfigDTO.setLessenValue(this.getLessenValue());
    imageVersionConfigDTO.setName(this.getName());
    imageVersionConfigDTO.setNeedSharpen(this.getNeedSharpen());
    imageVersionConfigDTO.setNeedWatermark(this.getNeedWatermark());
    imageVersionConfigDTO.setQuality(this.getQuality());
    imageVersionConfigDTO.setSizeFixHeight(this.getSizeFixHeight());
    imageVersionConfigDTO.setSizeFixWidth(this.getSizeFixWidth());
    imageVersionConfigDTO.setStatus(this.getStatus());
    imageVersionConfigDTO.setThumbnailsType(this.getThumbnailsType());
    imageVersionConfigDTO.setWatermarkMarginX(this.getWatermarkMarginX());
    imageVersionConfigDTO.setWatermarkMarginY(this.getWatermarkMarginY());
    imageVersionConfigDTO.setWatermarkName(this.getWatermarkName());
    imageVersionConfigDTO.setWatermarkOpacity(this.getWatermarkOpacity());
    imageVersionConfigDTO.setWatermarkPosition(this.getWatermarkPosition());
    return imageVersionConfigDTO;
  }
}
