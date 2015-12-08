package com.bcgogo.user.service.wx;

import com.bcgogo.user.model.wx.WXShopAccount;
import com.bcgogo.wx.WXShopAccountSearchCondition;
import com.bcgogo.wx.user.WXAccountDTO;
import com.bcgogo.wx.user.WXShopAccountDTO;

import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-12-18
 * Time: 上午11:11
 */
public interface IWXAccountService {

  List<WXAccountDTO> getWXAccountDTOByCondition(WXShopAccountSearchCondition condition) throws IOException;

  WXAccountDTO getWXAccountDTOByPublicNo(String publicNo) throws IOException;

  WXAccountDTO getWXAccountByOpenId(String openId) throws IOException;

  WXAccountDTO getWXAccountDTOById(Long id) throws IOException;

  WXAccountDTO getWXAccountDTOByShopId(Long shopId) throws IOException;

  WXAccountDTO getDecryptedWXAccountByShopId(Long shopId) throws Exception;

  WXAccountDTO getDecryptedWXAccountByPublicNo(String publicNo) throws Exception;

  WXAccountDTO getCachedWXAccount(String publicNo) throws Exception;

  WXAccountDTO getDefaultWXAccount() throws Exception;

  void saveOrUpdateWXAccount(WXAccountDTO accountDTO);

  List<WXShopAccountDTO> getWXShopAccountDTO(Long shopId,Long accountId);

   List<WXShopAccount> getWXShopAccount(Long shopId,Long accountId);

  WXShopAccountDTO getWXShopAccountDTOById(Long id);

  WXShopAccount getWXShopAccountByShopId(Long shopId);


  WXShopAccountDTO getWXShopAccountDTOByShopId(Long shopId);

  List<WXAccountDTO> getAllWXAccount() throws IOException;

  void createDefaultWXShopAccount(Long shopId) throws Exception;

   void saveOrUpdateWXShopAccountDTO(WXShopAccountDTO... shopAccountDTOs);

   List<WXAccountDTO> getWXAccountDTO(WXShopAccountSearchCondition condition) throws IOException;

  int countWXAccount(WXShopAccountSearchCondition condition);

  int countWXShopAccount(WXShopAccountSearchCondition condition);

  List<WXShopAccountDTO> getWXShopAccountDTO(WXShopAccountSearchCondition condition) throws IOException;


}
