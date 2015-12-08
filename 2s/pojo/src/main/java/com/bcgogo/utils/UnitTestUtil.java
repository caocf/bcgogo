package com.bcgogo.utils;



import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-11-7
 * Time: 下午2:57
 */
public class UnitTestUtil {

  public static  <T1, T2> boolean simpleCompareSame(T1 expected, T2 actual, Class<T1> expectedClazz, Class<T2> actualClazz) throws Exception {
      boolean isSame = true;
      Set<Class> baseClass = new HashSet<Class>();
      baseClass.add(byte.class);
      baseClass.add(Byte.class);
      baseClass.add(short.class);
      baseClass.add(Short.class);
      baseClass.add(int.class);
      baseClass.add(Integer.class);
      baseClass.add(long.class);
      baseClass.add(Long.class);
      baseClass.add(float.class);
      baseClass.add(Float.class);
      baseClass.add(double.class);
      baseClass.add(Double.class);
      baseClass.add(char.class);
      baseClass.add(Character.class);
      baseClass.add(boolean.class);
      baseClass.add(Boolean.class);
      baseClass.add(String.class);

      Map<String, Object> expectedMap = new HashMap<String, Object>();
      Map<String, Object> actualMap = new HashMap<String, Object>();
      if (expected != null && actual != null) {
        Field[] expectedFields = expectedClazz.getDeclaredFields();
        Field[] actualFields = actualClazz.getDeclaredFields();
        for (Field actualField : actualFields) {
          if (baseClass.contains(actualField.getType()) || actualField.getType().isEnum()) {
            actualField.setAccessible(true);
            actualMap.put(actualField.getName(), StringUtil.valueOf(actualField.get(actual)));
          }
        }

        for (Field expectedField : expectedFields) {
          if (actualMap.keySet().contains(expectedField.getName())) {
            expectedField.setAccessible(true);
            expectedMap.put(expectedField.getName(), StringUtil.valueOf(expectedField.get(expected)));
            if (!expectedMap.get(expectedField.getName()).equals(actualMap.get(expectedField.getName()))) {
              StringBuilder sb = new StringBuilder();
              sb.append("Test Field:")
                  .append(expectedField.getName())
                  .append(",期望值:")
                  .append(expectedMap.get(expectedField.getName()))
                  .append(",实际值:")
                  .append(actualMap.get(expectedField.getName()))
              ;
              throw new Exception(sb.toString());
            }

//            Assert.assertEquals(expectedField.getName(), expectedMap.get(expectedField.getName()), );
          }
        }

      }
      return isSame;
    }
}
