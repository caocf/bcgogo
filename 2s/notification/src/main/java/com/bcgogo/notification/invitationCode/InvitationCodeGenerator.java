package com.bcgogo.notification.invitationCode;

import com.bcgogo.common.Result;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.notification.InvitationCodeStatus;
import com.bcgogo.enums.notification.InvitationCodeType;
import com.bcgogo.enums.notification.OperatorType;
import com.bcgogo.notification.dto.InvitationCodeDTO;
import com.bcgogo.notification.model.InvitationCode;
import com.bcgogo.notification.model.InvitationCodeRecycle;
import com.bcgogo.notification.model.NotificationDaoManager;
import com.bcgogo.notification.model.NotificationWriter;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.RandomUtils;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-17
 * Time: 下午8:01
 */
@Component
public class InvitationCodeGenerator implements InvitationCodeGeneratorClient {
  private static final Logger LOG = LoggerFactory.getLogger(InvitationCodeGenerator.class);
  private final static long CREATE_INVITATION_CODE_TIMEOUT = 60000l;
  private final static long DEFAULT_EXPIRATION_TIME = 3600000l * 24 * 10;//默认10天过期
  private final static int PAGE_SIZE = 1000;

  @Autowired
  private NotificationDaoManager notificationDaoManager;

  @Override
  public List<InvitationCodeDTO> createInvitationCodes(InvitationCodeType invitationCodeType, OperatorType inviterType, Long inviterId, OperatorType inviteeType, List<Long> inviteeIds, Long expirationTime, boolean checkingDuplicated) throws Exception {
    List<InvitationCodeDTO> invitationCodeDTOList = new ArrayList<InvitationCodeDTO>();
    if (inviterType == null || inviterId == null || inviteeType == null || CollectionUtil.isEmpty(inviteeIds)) {
      LOG.warn("parameters can't be null:[inviterType:" + inviterType + ",inviterId:" + inviterId + ",inviteeType:" + inviteeType + ",inviteeIds:" + inviteeIds);
      return invitationCodeDTOList;
    }
    InvitationCodeDTO dto;
    String code;
    List<String> codeList = RandomUtils.randomAlphabeticList(inviteeIds.size());
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    String effectiveDate;
    if (checkingDuplicated) {
      long beginning = System.currentTimeMillis();
      while (duplicatedInvitationCodeDuplicate(codeList)) {
        codeList = RandomUtils.randomAlphabeticList(inviteeIds.size());
        if (System.currentTimeMillis() - beginning > CREATE_INVITATION_CODE_TIMEOUT) {
          throw new Exception("create invitationCode timeout exception!");
        }
      }
    }
    for (int i = 0; i < codeList.size(); i++) {
      dto = new InvitationCodeDTO();
      dto.setInviterId(inviterId);
      dto.setInviterType(inviterType);
      dto.setInviteeId(inviteeIds.get(i));
      dto.setInviteeType(inviteeType);
      dto.setStatus(InvitationCodeStatus.EFFECTIVE);
      dto.setInviteTime(System.currentTimeMillis());
      dto.setType(invitationCodeType);
      if (expirationTime == null) {
        effectiveDate = configService.getConfig("INVITATION_CODE_EFFECTIVE_DATE", ShopConstant.BC_SHOP_ID);
        if (StringUtil.isEmpty(effectiveDate)) {
          dto.setExpirationTime(System.currentTimeMillis() + DEFAULT_EXPIRATION_TIME);
        } else {
          dto.setExpirationTime(System.currentTimeMillis() + Long.valueOf(configService.getConfig("INVITATION_CODE_EFFECTIVE_DATE", ShopConstant.BC_SHOP_ID)));
        }
      } else {
        dto.setExpirationTime(expirationTime);
      }
      code = codeList.get(i);
      dto.setCode(code);
      invitationCodeDTOList.add(dto);
    }
    saveInvitationCodes(invitationCodeDTOList);
    return invitationCodeDTOList;
  }

  @Override
  public String createInvitationCode(InvitationCodeType invitationCodeType, OperatorType inviterType, Long inviterId, OperatorType inviteeType, Long inviteeId, Long expirationTime) throws Exception {
    if (inviteeId == null) throw new Exception("parameters can't be null:[inviteeId:" + inviteeId);
    List<Long> inviteeIds = new ArrayList<Long>();
    inviteeIds.add(inviteeId);
    List<InvitationCodeDTO> invitationCodeDTOList = createInvitationCodes(invitationCodeType, inviterType, inviterId, inviteeType, inviteeIds, expirationTime, true);
    if (CollectionUtil.isEmpty(invitationCodeDTOList)) return null;
    return invitationCodeDTOList.get(0).getCode();
  }

  private void saveInvitationCodes(List<InvitationCodeDTO> dtoList) {
    if (CollectionUtil.isEmpty(dtoList)) return;
    NotificationWriter writer = notificationDaoManager.getWriter();
    Object status = writer.begin();
    InvitationCode code;
    try {
      for (InvitationCodeDTO dto : dtoList) {
        code = new InvitationCode();
        writer.save(code.fromDto(dto));
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  private boolean duplicatedInvitationCodeDuplicate(List<String> codes) {
    NotificationWriter writer = notificationDaoManager.getWriter();
    return writer.countEffectiveInvitationCodeByCode(codes) > 0l;
  }

  @Override
  public InvitationCodeDTO findEffectiveInvitationCodeByCode(String code) {
    NotificationWriter writer = notificationDaoManager.getWriter();
    return writer.findEffectiveInvitationCodeByCode(code.toLowerCase());
  }

  public InvitationCodeDTO findInvitationCodeByCode(String code) {
    NotificationWriter writer = notificationDaoManager.getWriter();
    return writer.findInvitationCodeByCode(code.toLowerCase());
  }

  @Override
  public void updateInvitationCodeToUsed(String code) {
    NotificationWriter writer = notificationDaoManager.getWriter();
    InvitationCodeDTO dto = writer.findInvitationCodeByCode(code);
    if (dto == null) {
      LOG.warn("can't find InvitationCode by code:{}", code);
      return;
    }
    Object status = writer.begin();
    try {
      InvitationCode invitationCode = writer.getById(InvitationCode.class, dto.getId());
      invitationCode.setStatus(InvitationCodeStatus.BE_USED);
      invitationCode.setUsageTime(System.currentTimeMillis());
      writer.save(invitationCode);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void recycleInvitationCode(Integer pageSize) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    if (pageSize == null) {
      pageSize = PAGE_SIZE;
    }
    long id = 0;
    List<InvitationCode> codeList;
    List<InvitationCodeRecycle> recycles = new ArrayList<InvitationCodeRecycle>();
    List<InvitationCode> codes = new ArrayList<InvitationCode>();
    InvitationCodeRecycle recycle;
    while (true) {
      NotificationWriter writer = notificationDaoManager.getWriter();
      codeList = writer.getInvitationCode(pageSize, id);   //得到将要reindex的orderId
      if (CollectionUtils.isEmpty(codeList)) break;
      Object status = writer.begin();
      try {
        for (InvitationCode code : codeList) {
          //过期 无效
          if (InvitationCodeStatus.isRecycle(code.getExpirationTime(), configService.getConfig("INVITATION_CODE_OVERDUE_DATE", ShopConstant.BC_SHOP_ID), code.getStatus())) {
            recycle = new InvitationCodeRecycle();
            if (code.getExpirationTime() < System.currentTimeMillis()) {
              recycle = recycle.fromInvitationCode(code);
            }
            recycle.setStatus(InvitationCodeStatus.INVALID_OVERDUE);
            recycles.add(recycle);
            codes.add(code);
          } else if (InvitationCodeStatus.isOverdue(code.getExpirationTime(), configService.getConfig("INVITATION_CODE_EFFECTIVE_DATE", ShopConstant.BC_SHOP_ID))) {
            //过期
            code.setStatus(InvitationCodeStatus.OVERDUE);
            writer.update(code);
          }
        }
        id = codeList.get(codeList.size() - 1).getId();
        if (CollectionUtils.isNotEmpty(recycles)) {
          for (int i = 0, max = recycles.size(); i < max; i++) {
            writer.save(recycles.get(i));
            writer.delete(InvitationCode.class, codes.get(i).getId());
          }
        }
        writer.commit(status);
      } finally {
        writer.rollback(status);
        recycles.clear();
        codes.clear();
      }
    }
  }

  @Override
  public InvitationCodeDTO validateInvitationCode(String invitationCode, Result result, Map<String, Object> resultData) {
    InvitationCodeDTO invitationCodeDTO = null;
    if (StringUtil.isNotEmpty(invitationCode)) {
      invitationCodeDTO = findInvitationCodeByCode(invitationCode);
      if (invitationCodeDTO == null || InvitationCodeStatus.BE_USED.equals(invitationCodeDTO.getStatus()) || InvitationCodeStatus.INVALID_OVERDUE.equals(invitationCodeDTO.getStatus())) {
        result.setSuccess(false);
        resultData.put("invitationCode", "error");
      } else if (InvitationCodeStatus.OVERDUE.equals(invitationCodeDTO.getStatus())) {
        result.setSuccess(false);
        resultData.put("invitationCode", "overdue");
      } else if (InvitationCodeStatus.EFFECTIVE.equals(invitationCodeDTO.getStatus())) {
        result.setSuccess(true);
      } else {
        result.setSuccess(false);
        resultData.put("invitationCode", "error");
      }
    } else {
      result.setSuccess(false);
      resultData.put("invitationCode", "empty");
    }
    return invitationCodeDTO;
  }
}
