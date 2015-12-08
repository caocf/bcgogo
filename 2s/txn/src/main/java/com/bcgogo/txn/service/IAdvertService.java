package com.bcgogo.txn.service;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.enums.txn.AdvertStatus;
import com.bcgogo.txn.dto.AdvertDTO;
import com.bcgogo.txn.dto.AllocateRecordDTO;
import com.bcgogo.txn.dto.AllocateRecordSearchConditionDTO;
import com.bcgogo.txn.model.TxnWriter;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 14-04-14
 * Time: 上午5:15
 * To change this template use File | Settings | File Templates.
 */
public interface IAdvertService {

  public AdvertDTO saveOrUpdateAdvert(AdvertDTO advertDTO);

  public AdvertDTO getAdvertById(Long id);

  public Result publishAdvert(Long idStr);

  public Result repealAdvert(Long idStr);

  public int countAdvertByDateStatus(Long shopId,Long startDate,Long endDate,AdvertStatus[] advertStatuses);

  public List<AdvertDTO> getAdvertByDateStatus(Long shopId,Long startDate,Long endDate,AdvertStatus[] advertStatuses,Pager pager);

  public void shopAdvertOverdueHandle() throws Exception;
}
