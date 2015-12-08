package com.bcgogo.pinyin.model;

import com.bcgogo.pinyin.util.PinyinUtil;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 13-4-19
 * Time: 上午11:56
 */
public class Product {
  private Long id;
  private String name;
  private String brand;
  private String model;
  private String spec;
  private String productVehicleBrand;
  private String productVehicleModel;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getSpec() {
    return spec;
  }

  public void setSpec(String spec) {
    this.spec = spec;
  }

  public String getProductVehicleBrand() {
    return productVehicleBrand;
  }

  public void setProductVehicleBrand(String productVehicleBrand) {
    this.productVehicleBrand = productVehicleBrand;
  }

  public String getProductVehicleModel() {
    return productVehicleModel;
  }

  public void setProductVehicleModel(String productVehicleModel) {
    this.productVehicleModel = productVehicleModel;
  }

  public boolean containsHomophoneWord(Map<String, String[]> homophones){
    String[] sources = {name, brand, model, spec, productVehicleBrand, productVehicleModel };
    for(String source: sources){
      if(PinyinUtil.containHomophoneWord(source, homophones)){
        System.out.println(id+":"+source);
        return true;
      }
    }
    return false;
  }


  @Override
  public String toString() {
    return "Product{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", brand='" + brand + '\'' +
        ", model='" + model + '\'' +
        ", spec='" + spec + '\'' +
        ", productVehicleBrand='" + productVehicleBrand + '\'' +
        ", productVehicleModel='" + productVehicleModel + '\'' +
        '}';
  }

  public void putFieldsInSet(Set<String> set) {
    String[] sources = {name, brand, model, spec, productVehicleBrand, productVehicleModel };
    for(String source:sources){
      set.add(source);
    }
  }

  public void putTokensInSet(Set<String> tokenNames) {
    String[] sources = {name, brand, model, spec, productVehicleBrand, productVehicleModel };
    try {
      for (String source : sources) {
        if(StringUtils.isNotBlank(source)){
          tokenNames.addAll(PinyinUtil.getTocken(source));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
