package com.bcgogo.pojox.enums.config;

import com.bcgogo.pojox.util.RegexUtils;
import com.bcgogo.pojox.util.StringUtil;
import org.apache.commons.lang.ArrayUtils;

import java.util.*;

/**
 * User: ZhangJuntao
 * Date: 13-9-4
 * Time: 下午5:08
 */
public enum ImageVersion {
  IV_320X480,
  IV_480X800,
  IV_720X1280,
  IV_640X960,
  IV_640X1136;

  private static Map<ImageVersion, ImageScene> fullShopImageVersionMapping = new HashMap<ImageVersion, ImageScene>();
  private static Map<ImageVersion, ImageScene> bigShopImageVersionMapping = new HashMap<ImageVersion, ImageScene>();
  private static Map<ImageVersion, ImageScene> smallShopImageVersionMapping = new HashMap<ImageVersion, ImageScene>();

  private static Map<ImageVersion, ImageScene> fullAppUserBillImageVersionMapping = new HashMap<ImageVersion, ImageScene>();
  private static Map<ImageVersion, ImageScene> smallAppUserBillImageVersionMapping = new HashMap<ImageVersion, ImageScene>();

  static {
    //shop
    bigShopImageVersionMapping.put(IV_320X480, ImageScene.SHOP_IMAGE_APP_320X480_BIG);
    bigShopImageVersionMapping.put(IV_480X800, ImageScene.SHOP_IMAGE_APP_480X800_BIG);
    bigShopImageVersionMapping.put(IV_720X1280, ImageScene.SHOP_IMAGE_APP_720X1280_BIG);
    bigShopImageVersionMapping.put(IV_640X960, ImageScene.SHOP_IMAGE_APP_640X960_BIG);
    bigShopImageVersionMapping.put(IV_640X1136, ImageScene.SHOP_IMAGE_APP_640X1136_BIG);

    smallShopImageVersionMapping.put(IV_320X480, ImageScene.SHOP_IMAGE_APP_320X480_SMALL);
    smallShopImageVersionMapping.put(IV_480X800, ImageScene.SHOP_IMAGE_APP_480X800_SMALL);
    smallShopImageVersionMapping.put(IV_720X1280, ImageScene.SHOP_IMAGE_APP_720X1280_SMALL);
    smallShopImageVersionMapping.put(IV_640X960, ImageScene.SHOP_IMAGE_APP_640X960_SMALL);
    smallShopImageVersionMapping.put(IV_640X1136, ImageScene.SHOP_IMAGE_APP_640X1136_SMALL);

    fullShopImageVersionMapping.put(IV_320X480, ImageScene.SHOP_IMAGE_APP_320X480_FULL_SCREEN);
    fullShopImageVersionMapping.put(IV_480X800, ImageScene.SHOP_IMAGE_APP_480X800_FULL_SCREEN);
    fullShopImageVersionMapping.put(IV_720X1280, ImageScene.SHOP_IMAGE_APP_720X1280_FULL_SCREEN);
    fullShopImageVersionMapping.put(IV_640X960, ImageScene.SHOP_IMAGE_APP_640X960_FULL_SCREEN);
    fullShopImageVersionMapping.put(IV_640X1136, ImageScene.SHOP_IMAGE_APP_640X1136_FULL_SCREEN);

    //bill
    smallAppUserBillImageVersionMapping.put(IV_320X480, ImageScene.APP_USER_BILL_IMAGE_APP_320X480_SMALL);
    smallAppUserBillImageVersionMapping.put(IV_480X800, ImageScene.APP_USER_BILL_IMAGE_APP_480X800_SMALL);
    smallAppUserBillImageVersionMapping.put(IV_720X1280, ImageScene.APP_USER_BILL_IMAGE_APP_720X1280_SMALL);
    smallAppUserBillImageVersionMapping.put(IV_640X960, ImageScene.APP_USER_BILL_IMAGE_APP_640X960_SMALL);
    smallAppUserBillImageVersionMapping.put(IV_640X1136, ImageScene.APP_USER_BILL_IMAGE_APP_640X1136_SMALL);

    fullAppUserBillImageVersionMapping.put(IV_320X480, ImageScene.APP_USER_BILL_IMAGE_APP_320X480_FULL_SCREEN);
    fullAppUserBillImageVersionMapping.put(IV_480X800, ImageScene.APP_USER_BILL_IMAGE_APP_480X800_FULL_SCREEN);
    fullAppUserBillImageVersionMapping.put(IV_720X1280, ImageScene.APP_USER_BILL_IMAGE_APP_720X1280_FULL_SCREEN);
    fullAppUserBillImageVersionMapping.put(IV_640X960, ImageScene.APP_USER_BILL_IMAGE_APP_640X960_FULL_SCREEN);
    fullAppUserBillImageVersionMapping.put(IV_640X1136, ImageScene.APP_USER_BILL_IMAGE_APP_640X1136_FULL_SCREEN);

  }

  public static ImageVersion getImageVersion(String imageVersion) {
    if (StringUtil.isEmpty(imageVersion)) return null;
    String[] ratioArray = imageVersion.split("X");
    if (ArrayUtils.isEmpty(ratioArray) || ratioArray.length != 2) return null;
    if (!RegexUtils.isDigital(ratioArray[0]) || !RegexUtils.isDigital(ratioArray[1])) return null;
    Double height = Double.valueOf(ratioArray[0]), width = Double.valueOf(ratioArray[1]);
    if (height < width) {
      Double temp = height;
      height = width;
      width = temp;
    }
    return getRatioValue(height, width);
  }

  private static ImageVersion getRatioValue(Double height, Double width) {
    //根据 高 宽 过滤
    List<ImageVersion> versions = new ArrayList<ImageVersion>();
    //比例
    if (width >= 720 - 20) {
      versions.add(IV_720X1280);
    }
    if (width >= 640 - 20) {
      versions.add(IV_640X1136);
      versions.add(IV_640X960);
    }
    if (width >= 480 - 20) {
      versions.add(IV_480X800);
    }
    Iterator<ImageVersion> iterator = versions.iterator();
    while (iterator.hasNext()) {
      ImageVersion version = iterator.next();
      if (version == IV_480X800 && height < 800 - 20) {
        iterator.remove();
      }
      if (version == IV_640X960 && height < 960 - 20) {
        iterator.remove();
      }
      if (version == IV_640X1136 && height < 1136 - 20) {
        iterator.remove();
      }
      if (version == IV_720X1280 && height < 1280 - 20) {
        iterator.remove();
      }
    }
    return versions.isEmpty() ? IV_320X480 : versions.get(0);
  }

  public static ImageScene getBigShopImageVersion(ImageVersion version) {
    if (version == null) {
      return ImageScene.SHOP_IMAGE_APP_320X480_BIG;
    }
    ImageScene scene = bigShopImageVersionMapping.get(version);
    if (scene == null) {
      return ImageScene.SHOP_IMAGE_APP_320X480_BIG;
    }
    return scene;
  }

  public static ImageScene getFullShopImageVersion(ImageVersion version) {
    if (version == null) {
      return ImageScene.SHOP_IMAGE_APP_320X480_FULL_SCREEN;
    }
    ImageScene scene = fullShopImageVersionMapping.get(version);
    if (scene == null) {
      return ImageScene.SHOP_IMAGE_APP_320X480_FULL_SCREEN;
    }
    return scene;
  }

  public static ImageScene getSmallShopImageVersion(ImageVersion version) {
    if (version == null) {
      return ImageScene.SHOP_IMAGE_APP_320X480_SMALL;
    }
    ImageScene scene = smallShopImageVersionMapping.get(version);
    if (scene == null) {
      return ImageScene.SHOP_IMAGE_APP_320X480_SMALL;
    }
    return scene;
  }

  public static ImageScene getFullAppUserBillImageVersion(ImageVersion version) {
    if (version == null) {
      return ImageScene.APP_USER_BILL_IMAGE_APP_480X800_FULL_SCREEN;
    }
    ImageScene scene = fullAppUserBillImageVersionMapping.get(version);
    if (scene == null) {
      return ImageScene.APP_USER_BILL_IMAGE_APP_480X800_FULL_SCREEN;
    }
    return scene;
  }

  public static ImageScene getSmallAppUserBillImageVersion(ImageVersion version) {
    if (version == null) {
      return ImageScene.APP_USER_BILL_IMAGE_APP_320X480_SMALL;
    }
    ImageScene scene = smallAppUserBillImageVersionMapping.get(version);
    if (scene == null) {
      return ImageScene.APP_USER_BILL_IMAGE_APP_320X480_SMALL;
    }
    return scene;
  }

//  IV_320X480,
//  IV_480X800,
//  IV_720X1280,
//  IV_640X960,
//  IV_640X1136;
  public static void main(String[] args) {
    System.out.println(ImageVersion.getImageVersion("700X720"));
    System.out.println(ImageVersion.getImageVersion("700X1259"));
  }


}
