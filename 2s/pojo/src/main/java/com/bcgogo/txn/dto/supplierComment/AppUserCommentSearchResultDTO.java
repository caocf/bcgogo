package com.bcgogo.txn.dto.supplierComment;

import com.bcgogo.common.Pager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-9-12
 * Time: 下午5:18
 * To change this template use File | Settings | File Templates.
 */
public class AppUserCommentSearchResultDTO {
    private List<AppUserCommentRecordDTO>  appUserCommentRecordDTOs;
    private CommentStatDTO commentStatDTO;
    private Pager pager;

  public List<AppUserCommentRecordDTO> getAppUserCommentRecordDTOs() {
    return appUserCommentRecordDTOs;
  }

  public void setAppUserCommentRecordDTOs(List<AppUserCommentRecordDTO> appUserCommentRecordDTOs) {
    this.appUserCommentRecordDTOs = appUserCommentRecordDTOs;
  }

  public CommentStatDTO getCommentStatDTO() {
    return commentStatDTO;
  }

  public void setCommentStatDTO(CommentStatDTO commentStatDTO) {
    this.commentStatDTO = commentStatDTO;
  }

  public Pager getPager() {
    return pager;
  }

  public void setPager(Pager pager) {
    this.pager = pager;
  }
}
