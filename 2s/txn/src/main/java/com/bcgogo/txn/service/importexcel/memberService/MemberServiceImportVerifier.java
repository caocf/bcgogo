package com.bcgogo.txn.service.importexcel.memberService;

import com.bcgogo.config.service.excelimport.ExcelImportException;
import com.bcgogo.config.service.excelimport.ImportVerifier;
import com.bcgogo.user.dto.ValidateImportDataDTO;
import com.bcgogo.user.service.excelimport.customer.CustomerImportConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-9-6
 * Time: 上午9:07
 * To change this template use File | Settings | File Templates.
 */
@Component
public class MemberServiceImportVerifier implements ImportVerifier
{
  @Override
  public String verify(Map<String, Object> data, Map<String, String> fieldMapping,ValidateImportDataDTO validateImportDataDTO) throws ExcelImportException
  {

    String verifyMemberNO = verifyMemberNO(data.get(fieldMapping.get(MemberServiceImportConstants.FieldName.MEMBER_NO)),validateImportDataDTO);
    if (StringUtils.isNotBlank(verifyMemberNO)) {
      return verifyMemberNO;
    }

    String verifyServiceName = verifyServiceName(data.get(fieldMapping.get(MemberServiceImportConstants.FieldName.MEMBER_NO)));
    if (StringUtils.isNotBlank(verifyServiceName)) {
      return verifyServiceName;
    }

    String verifyTimes = verifyTimes(data.get(fieldMapping.get(MemberServiceImportConstants.FieldName.TIMES)));
    if(StringUtils.isNotBlank(verifyTimes))
    {
      return verifyTimes;
    }

    String verifyDeadline = verifyDeadline(data.get(fieldMapping.get(MemberServiceImportConstants.FieldName.DEADLINE)));
    if(StringUtils.isNotBlank(verifyDeadline))
    {
      return verifyDeadline;
    }
    return null;
  }

  public String verifyMemberNO(Object value,ValidateImportDataDTO validateImportDataDTO)
  {
    if (value == null) {
      return MemberServiceImportConstants.CheckResultMessage.EMPTY_MEMBER_NO;
    }
    String memberNo = String.valueOf(value);
    if (StringUtils.isBlank(memberNo)) {
      return MemberServiceImportConstants.CheckResultMessage.EMPTY_MEMBER_NO;
    }
    if (memberNo.length() > MemberServiceImportConstants.FieldLength.FIELD_MEMBER_NO) {
      return MemberServiceImportConstants.CheckResultMessage.MEMBER_NO_TOO_LONG;
    }

    if(null != validateImportDataDTO && null != validateImportDataDTO.getMemberDTOMap()
        && null == validateImportDataDTO.getMemberDTOMap().get(memberNo))
    {
      return "\""+memberNo+"\""+ MemberServiceImportConstants.CheckResultMessage.MEMBER_NOT_EXIST_IN_TABLE;
    }
    return null;
  }

  public String verifyServiceName(Object value)
  {
    if (value == null) {
      return MemberServiceImportConstants.CheckResultMessage.EMPTY_SERVICE_NAME;
    }
    String memberNo = String.valueOf(value);
    if (StringUtils.isBlank(memberNo)) {
      return MemberServiceImportConstants.CheckResultMessage.EMPTY_SERVICE_NAME;
    }
    if (memberNo.length() > MemberServiceImportConstants.FieldLength.FIELD_SERVICE_NAME) {
      return MemberServiceImportConstants.CheckResultMessage.SERVICE_NAME_TOO_LONG;
    }
    return null;
  }

  public String verifyTimes(Object value)
  {
    if(value == null)
    {
      return MemberServiceImportConstants.CheckResultMessage.EMPTY_TIMES;
    }

    String times = String.valueOf(value);

    if(StringUtils.isBlank(times))
    {
      return MemberServiceImportConstants.CheckResultMessage.EMPTY_TIMES;
    }

    if(!isInteger(times))
    {
      return MemberServiceImportConstants.FieldName.TIMES_DESC+MemberServiceImportConstants.CheckResultMessage.NOT_INTEGER;
    }

    return null;
  }


  public String verifyDeadline(Object value)
  {
    if(null == value)
    {
      return null;
    }

    String deadline = String.valueOf(value);

    if(StringUtils.isBlank(deadline))
    {
      return null;
    }

    String datePattern1 = "\\d{4}-\\d{2}-\\d{2}";
    Pattern pattern = Pattern.compile(datePattern1);

    if(!pattern.matcher(deadline).matches())
    {
      return MemberServiceImportConstants.FieldName.DEADLINE_DESC+MemberServiceImportConstants.CheckResultMessage.FORMAT_NOT_EXPECT;
    }

    String remind = checkDateStrFormat(deadline);

    if(StringUtils.isNotBlank(remind))
    {
      return MemberServiceImportConstants.FieldName.DEADLINE_DESC+remind;
    }

    return null;
  }
    /**
   * 验证时间的正确性，月日是否符合实际
   * @param str
   * @param str
   * @return
   */
  public String checkDateStrFormat(String str)
  {
    int year = Integer.parseInt(str.split("-")[0]) ;
    int month = Integer.parseInt(str.split("-")[1]);
    int day = Integer.parseInt(str.split("-")[2]);

    String remind="";

    if(month<0 || month >12)
    {
      remind = "月份要在1到12之间";
      return remind;
    }

    switch (month)
    {
      case 1 : if(day>31) remind="1月份号数不能大于31";break;
      case 2 : if(year%4 == 0 && day>29) remind="闰年2月份号数不能大于29";
               else if(year%4 !=0 && day>28) remind="平年2月份号数不能大于29"; break;
      case 3 : if(day>31) remind="3月份号数不能大于31";break;
      case 4 : if(day>30) remind="4月份号数不能大于30";break;
      case 5 : if(day>31) remind="5月份号数不能大于31";break;
      case 6 : if(day>30) remind="6月份号数不能大于30";break;
      case 7 : if(day>31) remind="7月份号数不能大于31";break;
      case 8 : if(day>31) remind="8月份号数不能大于31"; break;
      case 9 : if(day>30) remind="9月份号数不能大于30"; break;
      case 10 : if(day>31) remind="10月份号数不能大于31";break;
      case 11 : if(day>30) remind="11月份号数不能大于30";break;
      case 12 : if(day>31) remind="12月份号数不能大于31";
    }

    return remind;
  }

  /**
   * 从excel传到后台的时候整数会变成double型
   * @return
   */
  public boolean isInteger(String str)
  {
    if(!NumberUtils.isNumber(str))
    {
      return false;
    }

    if(-1 == str.indexOf("\\."))
    {
      return true;
    }

    str = str.split("\\.")[1];

    if(Integer.valueOf(str).intValue() == 0)
    {
      return true;
    }
    else
    {
      return false;
    }
  }

}
