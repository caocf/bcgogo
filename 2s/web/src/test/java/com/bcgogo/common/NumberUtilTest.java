package com.bcgogo.common;

import com.bcgogo.notification.dto.OutBoxDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.EncryptionUtil;
import com.bcgogo.utils.NumberUtil;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-5-19
 * Time: 上午9:43
 * To change this template use File | Settings | File Templates.
 */
public class NumberUtilTest {
  private static final Logger LOG = LoggerFactory.getLogger(NumberUtilTest.class);

  @Test
  public void testToReserveValue() {
//     String code=RandomStringUtils.randomAlphanumeric(6).toLowerCase();
//    StringBuilder builder = new StringBuilder();
//    if (StringUtils.isNotEmpty(code)) {
//      builder.append(code.substring(0, 1)).append(code.substring(1, 3).toUpperCase()).append(code.substring(3, 5)).append(code.substring(5, 6).toUpperCase());
//    }
//    System.out.println(code);
//    System.out.println(builder.toString());
//    String[] temp = "/web/notice.do?method=showNoticeList".split("\\.do\\?method=");
//    String key = "request"+temp[0] + "/" + temp[1];
//    System.out.println(key.replace("/", "_"));

//    String[] excludeFlowName=new String[]{"a","b"};
//    String[] newExcludeFlowName = new String[excludeFlowName.length + 1];
//    System.arraycopy(excludeFlowName, 0, newExcludeFlowName, 0, excludeFlowName.length);
//    newExcludeFlowName[excludeFlowName.length] = "c";
//    System.out.println(newExcludeFlowName);
//    excludeFlowName= newExcludeFlowName;
//    System.out.println(excludeFlowName);

//    System.out.println(com.bcgogo.utils.StringUtil.subUpString("/web/notice.do?method=showNoticeList".replace("/", "_").replaceAll("\\.do\\?method=", "_")));

    double value = 123.4;
    double value1 = 123.4566;
    Assert.assertEquals("123.40", com.bcgogo.utils.NumberUtil.roundToString(value, 2));
    Assert.assertEquals("123.46", com.bcgogo.utils.NumberUtil.roundToString(value1, 2));

  }

  @Test
  public void formatDoubleTest() throws ParseException {
    Double val = 30000000d;
    String str = NumberUtil.formatDoubleWithComma(val, "0.0");
    Assert.assertEquals("30,000,000", str);

    val = null;
    str = NumberUtil.formatDoubleWithComma(val, "0.0");
    Assert.assertEquals("0.0", str);


  }

  @Test
  public void isEqualTest() throws ParseException {
    Double val = 30000000d;
    Double val2 = 30000000.0001d;
    Double val3 = 30000000.0000000d;
    Assert.assertTrue(NumberUtil.isEqual(val,val2,0.001d));
    Assert.assertFalse(NumberUtil.isEqual(val,val2,0.00001d));
    Assert.assertTrue(NumberUtil.isEqual(val,val3,null));
  }

  @Test
  public void isZeroTest() throws ParseException {
    Double val = 0000d;
    Double val2 = 00000000.0001d;
    Assert.assertTrue(NumberUtil.isZero(val));
    Assert.assertTrue(NumberUtil.isZero(val,0.001d));
    Assert.assertFalse(NumberUtil.isZero(val2,0.00001d));
    Assert.assertTrue(NumberUtil.isZero(val2,0.01d));
  }

  @Test
  public void iteratorTest() throws ParseException {
//    String[] strs = new String[]{"a", "b", "c", "d", "e"};
//    List<String> strList = Arrays.asList(strs);
    List<String> list = new ArrayList<String>();
    list.add("a");
    list.add("b");
    list.add("c");
    list.add("d");
    list.add("c");
    list.add("e");
    Iterator<String> it = list.iterator();
    while (it.hasNext()) {
      if (it.next().equals("c")) {
        it.remove();
      }
    }
    Assert.assertEquals(4, list.size());
    list.clear();
    System.out.println(list.size());
//    try{
//      String a=null;
//      if(a.equals("a")){
//
//      }
//    } catch (Exception e){
//      LOG.error(e.getMessage(),e);
//      LOG.error("失败",e);
//    }
  }

  @Test
  public void equTest() throws Exception {
    Long a = 10000040001000006l;
    Long b = 10000040001000006l;
    Long c = 10000040021000007l;

    Assert.assertEquals(false, a == b);
    Assert.assertEquals(true, a.equals(b));
    Assert.assertEquals(false, a.equals(c));
    Assert.assertEquals(false, a.longValue() == c.longValue());
    Assert.assertEquals(true, a.longValue() == b.longValue());
    OutBoxDTO outBoxDTO = new OutBoxDTO();
    outBoxDTO.setContent("<html>");
    System.out.println(outBoxDTO.getContent());
    System.out.println(EncryptionUtil.encryptPassword("6763.com", 1L));
    System.out.println(EncryptionUtil.encryptPassword("6763", 1L));
    System.out.println(EncryptionUtil.encryptPassword(".com", 1L));
    System.out.println(EncryptionUtil.encryptPassword("6763.com", 1L).length());

    List<String> aa = new ArrayList<String>();
    aa.add("1");
    aa.add("2");
    aa.add("3");
    List<String> bb = new ArrayList<String>();
    bb.add("2");
    bb.add("3");
    aa.removeAll(bb);
    System.out.println(aa);
  }


}
