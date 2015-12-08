package com.bcgogo.utils;

import com.bcgogo.product.dto.ProductSupplierDTO;
import org.apache.commons.collections.CollectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 12-2-4
 * Time: 上午9:55
 * To change this template use File | Settings | File Templates.
 */
public class ServiceUtil {
  public static <T> String getJsonWithList(List<T> list) {
    if (list == null || list.size() == 0) {
      return "[]";
    }
    if (list.get(0) == null) {
      return "[]";
    }
    Field[] fields = list.get(0).getClass().getDeclaredFields();
    String[] fieldName = new String[fields.length];
    String[][] fieldValue = new String[list.size()][fields.length];
    for (int t = 0; t < fields.length; t++) {
      fieldName[t] = fields[t].getName();
    }
    for (int z = 0; z < list.size(); z++) {
      for (int w = 0; w < fields.length; w++) {
        fieldValue[z][w] = getReadMethodValue(fieldName[w], list.get(z));
      }
    }
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    for (int j = 0; j < list.size(); j++) {
      sb.append("{");
      for (int h = 0; h < fieldName.length; h++) {
        sb.append("\'" + fieldName[h] + "\':\'" + fieldValue[j][h].replace("'", "\\'") + "\',");
        if (h == fieldName.length - 1) {
          sb.delete(sb.length() - 1, sb.length());
        }
      }
      sb.append("},");
      if (j == list.size() - 1) {
        sb.delete(sb.length() - 1, sb.length());
      }
    }
    sb.append("]");
    return sb.toString();
  }

  private static <T> String getReadMethodValue(String fieldName, T x) {
    try {
      PropertyDescriptor pd = new PropertyDescriptor(fieldName, x.getClass());
      Method method = pd.getReadMethod();
      return method.invoke(x).toString();
    } catch (Exception e) {
      return "";
    }
  }

  //List 汉字首字母 排序
  public static List<String> sortList(List<String> stringList) {
    Collections.sort(stringList, new Comparator<String>() {
      @Override
      public int compare(String o1, String o2) {
        if (!(PinyinUtil.checkChs(o1))) {
          return 1;
        }
        if (!(PinyinUtil.checkChs(o2))) {
          return -1;
        }
        if (!PinyinUtil.checkChs(o1)) {
          return -1;
        }
        return PinyinUtil.getFirstLetter(o1).toUpperCase().compareTo(PinyinUtil.getFirstLetter(o2).toUpperCase());
      }
    });
    return stringList;
  }

  //List 首字母 分类
  public static List<String> classifyList(List<String> stringList) {
    List<String> results = new ArrayList<String>();
    stringList = ServiceUtil.sortList(stringList);
    boolean flag = false;
    String temp1 = "", temp2 = "";
    for (String str : stringList) {
      if (!PinyinUtil.checkChs(str) && flag == false) {
        results.add("<" + "其他" + ">");
        results.add(str);
        flag = true;
        continue;
      }
      if (flag == false) {
        temp1 = PinyinUtil.getFirstLetter(str);
        if (!temp1.toLowerCase().equals(temp2.toLowerCase())) {
          results.add("<" + temp1.toUpperCase() + ">");
        }
        temp2 = temp1;
      }
      results.add(str);
    }
    return results;
  }

  @Deprecated
	public static List<ProductSupplierDTO> getTopNProductSupplierDTO(List<ProductSupplierDTO> productSupplierDTOs,int num) {
		if (CollectionUtils.isEmpty(productSupplierDTOs)) {
			return productSupplierDTOs;
		}
		while (true) {
			long size = productSupplierDTOs.size();
			if (size <= num) {
				return productSupplierDTOs;
			}
			Long minTime = null;
			ProductSupplierDTO tempProductSupplierDTO = null;
			for (ProductSupplierDTO productSupplierDTO : productSupplierDTOs) {

				if (productSupplierDTO == null) {
					productSupplierDTOs.remove(productSupplierDTO);
					size--;
				}
				if (size <= num) {
					return productSupplierDTOs;
				}

				if (minTime == null) {
					minTime = productSupplierDTO.getLastUsedTime();
					tempProductSupplierDTO = productSupplierDTO;
				}
				if (minTime != null && minTime > productSupplierDTO.getLastUsedTime()) {
					minTime = productSupplierDTO.getLastUsedTime();
					tempProductSupplierDTO = productSupplierDTO;
				}
			}
			productSupplierDTOs.remove(tempProductSupplierDTO);
		}
	}
}
