package com.bcgogo.user.model.app;

import com.bcgogo.api.ObdSimBindDTO;
import com.bcgogo.enums.app.ObdSimBindStatus;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created by XinyuQiu on 14-6-16.
 */
@Entity
@Table(name = "obd_sim_bind")
public class ObdSimBind  extends LongIdentifier {
  private Long obdId;
  private Long simId;
  private Long obdHistoryId;
  private Long simHistoryId;
  private ObdSimBindStatus status;
  private Long bindTime;

  public void fromObdSimBindDTO(ObdSimBindDTO obdSimBindDTO) {
    if(obdSimBindDTO != null){
      setObdId(obdSimBindDTO.getObdId());
      setSimId(obdSimBindDTO.getSimId());
      setObdHistoryId(obdSimBindDTO.getObdHistoryId());
      setSimHistoryId(obdSimBindDTO.getSimHistoryId());
      setStatus(ObdSimBindStatus.ENABLED);
      setBindTime(System.currentTimeMillis());
    }
  }

  @Column(name = "obd_id")
  public Long getObdId() {
    return obdId;
  }

  public void setObdId(Long obdId) {
    this.obdId = obdId;
  }

  @Column(name = "sim_id")
  public Long getSimId() {
    return simId;
  }

  public void setSimId(Long simId) {
    this.simId = simId;
  }

  @Column(name = "obd_history_id")
  public Long getObdHistoryId() {
    return obdHistoryId;
  }

  public void setObdHistoryId(Long obdHistoryId) {
    this.obdHistoryId = obdHistoryId;
  }

  @Column(name = "sim_history_id")
  public Long getSimHistoryId() {
    return simHistoryId;
  }

  public void setSimHistoryId(Long simHistoryId) {
    this.simHistoryId = simHistoryId;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public ObdSimBindStatus getStatus() {
    return status;
  }

  public void setStatus(ObdSimBindStatus status) {
    this.status = status;
  }

  @Column(name = "bind_time")
  public Long getBindTime() {
    return bindTime;
  }

  public void setBindTime(Long bindTime) {
    this.bindTime = bindTime;
  }

  public ObdSimBindDTO toDTO(){
    ObdSimBindDTO obdSimBindDTO=new ObdSimBindDTO();
    return obdSimBindDTO;
  }


}
