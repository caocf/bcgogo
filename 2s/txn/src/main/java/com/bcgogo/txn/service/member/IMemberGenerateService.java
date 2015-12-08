package com.bcgogo.txn.service.member;

import com.bcgogo.api.MemberInfoDTO;
import com.bcgogo.user.dto.MemberDTO;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-12-16
 * Time: 下午3:30
 */
public interface IMemberGenerateService {
  MemberInfoDTO generateAppMemberInfoDTO(MemberDTO memberDTO)throws Exception;
}
