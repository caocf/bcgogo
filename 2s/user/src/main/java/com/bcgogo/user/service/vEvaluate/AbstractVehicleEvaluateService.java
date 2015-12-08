package com.bcgogo.user.service.vEvaluate;

import com.bcgogo.user.model.EvaluateRecord;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.utils.StringUtil;
import com.bcgogo.vehicle.evalute.EvaluateRecordDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-11-17
 * Time: 下午6:17
 */
@Component
public abstract class AbstractVehicleEvaluateService implements IVehicleEvaluateService{

  @Autowired
  private UserDaoManager userDaoManager;

  @Override
  public void saveOrUpdateEvaluateRecord(EvaluateRecordDTO recordDTO){
    UserWriter writer=userDaoManager.getWriter();
    Object status=writer.begin();
    EvaluateRecord record=null;
    try{
      if(recordDTO.getId()!=null){
        record=getEvaluateRecordById(recordDTO.getId());
        if(record==null) return;
      }else {
        record=new EvaluateRecord();
      }
      record.fromDTO(recordDTO);
      writer.saveOrUpdate(record);
      writer.commit(status);
      recordDTO.setId(record.getId());
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public EvaluateRecordDTO getLastEvaluateRecordDTOByVehicleNo(String vehicleNo){
    if(StringUtil.isEmpty(vehicleNo)) return null;
    UserWriter writer=userDaoManager.getWriter();
    EvaluateRecord record=writer.getLastEvaluateRecordDTOByVehicleNo(vehicleNo);
    return record!=null?record.toDTO():null;
  }

  @Override
  public EvaluateRecordDTO getEvaluateRecordDTOById(Long id){
    if(id==null) return null;
    EvaluateRecord record=getEvaluateRecordById(id);
    return record!=null?record.toDTO():null;
  }

  public EvaluateRecord getEvaluateRecordById(Long id){
    if(id==null) return null;
    UserWriter writer=userDaoManager.getWriter();
    return writer.getById(EvaluateRecord.class,id);
  }

}
