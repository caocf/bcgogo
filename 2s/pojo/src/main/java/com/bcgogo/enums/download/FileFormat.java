package com.bcgogo.enums.download;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 13-2-4
 * Time: 上午10:46
 * To change this template use File | Settings | File Templates.
 */
public enum FileFormat {
  xls("excel"),
  rar("压缩文件");
  String name;

  FileFormat(String name){
    this.name = name;
  }

  public String getName(){
    return this.name;
  }
}
