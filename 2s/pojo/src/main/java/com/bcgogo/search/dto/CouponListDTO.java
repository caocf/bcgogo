package com.bcgogo.search.dto;

import com.bcgogo.common.Pager;
import com.bcgogo.txn.dto.ConsumingRecordDTO;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author : ztyu
 * Date: 2015/11/6
 * Time: 16:15
 */
public class CouponListDTO {

    private List<ConsumingRecordDTO> consumingRecordDTOs;
    private Pager pager;

    public List<ConsumingRecordDTO> getConsumingRecordDTOs() {
        return consumingRecordDTOs;
    }

    public void setConsumingRecordDTOs(List<ConsumingRecordDTO> consumingRecordDTOs) {
        this.consumingRecordDTOs = consumingRecordDTOs;
    }

    public Pager getPager() {
        return pager;
    }

    public void setPager(Pager pager) {
        this.pager = pager;
    }
}
