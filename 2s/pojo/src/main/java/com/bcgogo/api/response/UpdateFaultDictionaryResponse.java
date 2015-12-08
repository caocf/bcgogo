package com.bcgogo.api.response;

import com.bcgogo.BooleanEnum;
import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.DictionaryDTO;
import com.bcgogo.api.DictionaryFaultInfoDTO;
import com.bcgogo.api.FaultCodeInfo;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-8-19
 * Time: 下午4:08
 */
public class UpdateFaultDictionaryResponse extends ApiResponse {
  private Long dictionaryId;  //字典id
  private String dictionaryVersion;//字典版本
  private BooleanEnum isCommon; //是否通用字典，是True，不是False

  private List<FaultCodeInfo> faultCodeList = new ArrayList<FaultCodeInfo>();


  public UpdateFaultDictionaryResponse() {
    super();
  }

  public UpdateFaultDictionaryResponse(ApiResponse response) {
    super(response);
  }

  public List<FaultCodeInfo> getFaultCodeList() {
    return faultCodeList;
  }

  public void setFaultCodeList(List<FaultCodeInfo> faultCodeList) {
    this.faultCodeList = faultCodeList;
  }

  public String getDictionaryVersion() {
    return dictionaryVersion;
  }

  public void setDictionaryVersion(String dictionaryVersion) {
    this.dictionaryVersion = dictionaryVersion;
  }

  public BooleanEnum getIsCommon() {
    return isCommon;
  }

  public void setIsCommon(BooleanEnum isCommon) {
    this.isCommon = isCommon;
  }

  public Long getDictionaryId() {
    return dictionaryId;
  }

  public void setDictionaryId(Long dictionaryId) {
    this.dictionaryId = dictionaryId;
  }

  public void setDictionaryDTO(DictionaryDTO dictionaryDTO) {
    if(dictionaryDTO != null){
      setIsCommon(dictionaryDTO.getIsCommon());
      setDictionaryVersion(dictionaryDTO.getDictionaryVersion());
      setDictionaryId(dictionaryDTO.getId());
    }
  }

  public void setDictionaryInfoList(List<DictionaryFaultInfoDTO> dictionaryFaultInfoDTOs) {
    List<FaultCodeInfo> faultCodeList = null;
    if(CollectionUtils.isNotEmpty(dictionaryFaultInfoDTOs)){
      faultCodeList = new ArrayList<FaultCodeInfo>();
      for(DictionaryFaultInfoDTO dictionaryFaultInfoDTO : dictionaryFaultInfoDTOs){
        if(dictionaryFaultInfoDTO != null){
          faultCodeList.add(dictionaryFaultInfoDTO.toFaultCodeInfo());
        }
      }
    }
    setFaultCodeList(faultCodeList);
  }
}
