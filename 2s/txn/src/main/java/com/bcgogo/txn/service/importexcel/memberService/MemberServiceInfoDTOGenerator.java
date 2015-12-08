package com.bcgogo.txn.service.importexcel.memberService;

import com.bcgogo.enums.MemberStatus;
import com.bcgogo.txn.dto.ServiceDTO;
import com.bcgogo.user.dto.MemberServiceDTO;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-9-6
 * Time: 上午9:07
 * To change this template use File | Settings | File Templates.
 */
@Component
public class MemberServiceInfoDTOGenerator {
  private static final Logger LOG = LoggerFactory.getLogger(MemberServiceInfoDTOGenerator.class);

    public MemberServiceDTO generate(Map<String, Object> data, Map<String, String> fieldMapping, Long shopId) throws Exception {
      if (shopId == null) {
        return null;
      }
      MemberServiceDTO memberServiceDTO = new MemberServiceDTO();
      ServiceDTO serviceDTO = new ServiceDTO();
      serviceDTO.setShopId(shopId);

      if (data.get(fieldMapping.get(MemberServiceImportConstants.FieldName.MEMBER_NO)) != null) {
        memberServiceDTO.setMemberNo(String.valueOf(data.get(fieldMapping.get(MemberServiceImportConstants.FieldName.MEMBER_NO))));
      }
      if (data.get(fieldMapping.get(MemberServiceImportConstants.FieldName.SERVICE_NAME)) != null) {
        serviceDTO.setName(String.valueOf(data.get(fieldMapping.get(MemberServiceImportConstants.FieldName.SERVICE_NAME))));
        memberServiceDTO.setServiceName(serviceDTO.getName());
      }
      if (data.get(fieldMapping.get(MemberServiceImportConstants.FieldName.TIMES)) != null) {
        memberServiceDTO.setTimes(Integer.parseInt(String.valueOf(data.get(fieldMapping.get(MemberServiceImportConstants.FieldName.TIMES))).split("\\.")[0]));
      }
      if (data.get(fieldMapping.get(MemberServiceImportConstants.FieldName.DEADLINE)) != null)
      {
        String deadlineStr = String.valueOf(data.get(fieldMapping.get(MemberServiceImportConstants.FieldName.DEADLINE)));

        if(StringUtils.isBlank(deadlineStr))
        {
          memberServiceDTO.setDeadline(Long.parseLong("-1"));
        }
        else
        {
          memberServiceDTO.setDeadlineStr(deadlineStr);
          long date = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, memberServiceDTO.getDeadlineStr()) + 86399000L;
          memberServiceDTO.setDeadline(date);
        }
      }
      else
      {
        memberServiceDTO.setDeadline(Long.parseLong("-1"));
      }

      memberServiceDTO.setServiceDTO(serviceDTO);
      memberServiceDTO.setStatus(MemberStatus.ENABLED);
      return memberServiceDTO;
    }

}
