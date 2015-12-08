package com.bcgogo.api;

import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by XinyuQiu on 14-7-8.
 */
public class MultiObdSimUpdateDTO {
  private static final Logger LOG = LoggerFactory.getLogger(MultiObdSimUpdateDTO.class);

  private ObdSimBindDTO newObdSimBindDTO;
  private ObdSimBindDTO[] toUpdateObdSimBindDTO;

  public ObdSimBindDTO getNewObdSimBindDTO() {
    return newObdSimBindDTO;
  }

  public void setNewObdSimBindDTO(ObdSimBindDTO newObdSimBindDTO) {
    this.newObdSimBindDTO = newObdSimBindDTO;
  }

  public ObdSimBindDTO[] getToUpdateObdSimBindDTO() {
    return toUpdateObdSimBindDTO;
  }

  public void setToUpdateObdSimBindDTO(ObdSimBindDTO[] toUpdateObdSimBindDTO) {
    this.toUpdateObdSimBindDTO = toUpdateObdSimBindDTO;
  }

  public void generateUpdateInfo() {
    if(newObdSimBindDTO!=null && StringUtils.isNotBlank(newObdSimBindDTO.getUseDateStr())){
      try {
        newObdSimBindDTO.setUseDate(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_YEAR_MON,newObdSimBindDTO.getUseDateStr()));
      }catch (Exception e){
        LOG.error(e.getMessage(),e);
      }
    }
  }

}
