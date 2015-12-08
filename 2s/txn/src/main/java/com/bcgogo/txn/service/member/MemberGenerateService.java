package com.bcgogo.txn.service.member;

import com.bcgogo.api.ApiMemberServiceDTO;
import com.bcgogo.api.MemberInfoDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.ServiceDTO;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.RFITxnService;
import com.bcgogo.user.dto.MemberDTO;
import com.bcgogo.user.dto.MemberServiceDTO;
import com.bcgogo.user.model.MemberService;
import com.bcgogo.user.service.IMembersService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-12-16
 * Time: 下午3:31
 */
@Component
public class MemberGenerateService implements IMemberGenerateService {
  private static final Logger LOG = LoggerFactory.getLogger(MemberGenerateService.class);

  @Override
  public MemberInfoDTO generateAppMemberInfoDTO(MemberDTO memberDTO) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    if (memberDTO != null && memberDTO.getId() != null && memberDTO.getShopId() != null) {
      Long shopId = memberDTO.getShopId();
      Long memberId = memberDTO.getId();
      Double memberConsumeTotal = txnService.getMemberCardConsumeTotal(shopId, memberId);
      memberDTO.setMemberConsumeTotal(memberConsumeTotal);
      List<MemberServiceDTO> memberServiceDTOs = membersService.getMemberServiceEnabledByMemberId(shopId, memberId);
      List<ApiMemberServiceDTO> apiMemberServiceDTOs = new ArrayList<ApiMemberServiceDTO>();
      if (CollectionUtils.isNotEmpty(memberServiceDTOs)) {
        Set<Long> serviceIds = new HashSet<Long>();
        for (MemberServiceDTO memberServiceDTO : memberServiceDTOs) {
          if (memberServiceDTO != null && memberServiceDTO.getServiceId() != null) {
            serviceIds.add(memberServiceDTO.getServiceId());
          }
        }
        RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
        Map<Long, ServiceDTO> serviceDTOMap = rfiTxnService.getServiceDTOMapByIds(shopId, serviceIds);
        for (MemberServiceDTO memberServiceDTO : memberServiceDTOs) {
          if (memberServiceDTO != null && memberServiceDTO.getServiceId() != null) {
            ServiceDTO serviceDTO = serviceDTOMap.get(memberServiceDTO.getServiceId());
            if (serviceDTO != null && StringUtils.isNotBlank(serviceDTO.getName())) {
              memberServiceDTO.setServiceName(serviceDTO.getName());
            }
            apiMemberServiceDTOs.add(memberServiceDTO.toApiMemberServiceDTO());
          }
        }
      }
      MemberInfoDTO memberInfoDTO = memberDTO.toMemberInfo();
      memberInfoDTO.setMemberServiceList(apiMemberServiceDTOs);
      return memberInfoDTO;
    }
    return null;
  }
}
