package com.bcgogo.txn.dto.supplierComment;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询结果封装类
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-3-18
 * Time: 下午5:56
 * To change this template use File | Settings | File Templates.
 */
public class CommentSearchResultDTO {
  private List<SupplierCommentRecordDTO> recordDTOList = new ArrayList<SupplierCommentRecordDTO>();

  public List<SupplierCommentRecordDTO> getRecordDTOList() {
    return recordDTOList;
  }

  public void setRecordDTOList(List<SupplierCommentRecordDTO> recordDTOList) {
    this.recordDTOList = recordDTOList;
  }
}
