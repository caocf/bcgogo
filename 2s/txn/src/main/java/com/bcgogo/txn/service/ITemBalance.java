package com.bcgogo.txn.service;

import com.bcgogo.txn.model.TemBlance;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zyj
 * Date: 12-2-6
 * Time: 下午5:08
 * To change this template use File | Settings | File Templates.
 */
public interface ITemBalance {
   public void saveBlance(Long shopId,float  smsBalance,float rechargeAmount) throws Exception;
  public void deleteBlance(Long id);
  public List<TemBlance> getSmsByShopId(int pageNo,int pageSize);
   public int coutSms();

}
