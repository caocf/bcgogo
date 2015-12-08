package com.bcgogo.txn.service;

import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.TxnWriter;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-3-1
 * Time: 上午10:44
 */
public interface IShoppingCartService {

  public void updateLoginUserShoppingCartInMemCache(Long shopId, Long userId) throws Exception;


  public void removeLogoutUserShoppingCartInMemCache(Long shopId, Long userId) throws Exception;

  void saveOrUpdateShoppingCartItems(Long shopId,Long userId, ShoppingCartItemDTO... shoppingCartItemDTOs) throws Exception;

  void updateShoppingCartItemAmount(Long shopId,Long shoppingCartItemId,Double amount) throws Exception;

  ShoppingCartDTO generateShoppingCartDTO(Long shopId, Long userId) throws Exception;

  int getShoppingCartMaxCapacity() throws Exception ;

  void deleteShoppingCartItemById(Long shopId,Long userId, Long... shoppingCartItemId) throws Exception;

  int getShoppingCartItemCountInMemCache(Long shopId, Long userId) throws Exception;

  void clearInvalidShoppingCartItems(Long shopId, Long userId) throws Exception;

  int getShoppingCartWarnCapacity() throws Exception;

  List<ShoppingCartItemDTO> getShoppingCartItemDTOById(Long shopId, Long userId, Long... shoppingCartItemId);

  public List<ShoppingCartItemDTO> getShopCarItemList(Long shopId, Long userId)throws  Exception;
}
