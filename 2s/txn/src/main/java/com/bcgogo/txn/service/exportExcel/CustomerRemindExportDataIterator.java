package com.bcgogo.txn.service.exportExcel;

import com.bcgogo.config.service.excelexport.BcgogoExportDataIterator;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.RemindEventType;
import com.bcgogo.exception.PageException;
import com.bcgogo.remind.dto.RemindEventDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.RemindEventStrategy;
import com.bcgogo.txn.service.RemindEventStrategySelector;
import com.bcgogo.txn.service.remind.ICustomerRemindService;
import com.bcgogo.user.dto.CustomerServiceJobDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XinyuQiu on 14-5-25.
 */
public class CustomerRemindExportDataIterator extends BcgogoExportDataIterator {
  private static final Logger LOG = LoggerFactory.getLogger(CustomerRemindExportDataIterator.class);
  private static final int PAGE_SIZE = 1000;

  private Boolean isOverdue = null;
  private Boolean hasRemind = null;
  private int totalRows = 0;
  private Long shopId = null;




  public CustomerRemindExportDataIterator(Long shopId, Boolean isOverdue,Boolean hasRemind,int totalRows)throws PageException{
    super( totalRows,PAGE_SIZE, ConfigUtils.getTotalNumPerExcel());
    this.isOverdue = isOverdue;
    this.hasRemind = hasRemind;
    this.totalRows = totalRows;
    this.shopId = shopId;

  }


  @Override
  protected int getTotalRows() {
    return totalRows;
  }

  @Override
  protected List<String> getHead() {
    List<String> head = new ArrayList<String>();
    head.addAll(ExportCustomerRemindConstant.fieldList);
    return head;
  }

  @Override
  protected List<String> getHeadShowInfo() {
    List<String> headShowInfo = new ArrayList<String>();
    headShowInfo.add("共有" + getTotalRows() + "条记录");
    return headShowInfo;
  }

  @Override
  protected List<String> getTailShowInfo() {
    return null;
  }

  @Override
  public Object next() {
    //取下一页数据
    getPage().gotoNextPage();
    //生成要导出的数据
    List<List<String>> rows = assembleCustomerRemindInfo();
    return rows;
  }

  private  List<List<String>> assembleCustomerRemindInfo(){
    ICustomerRemindService customerRemindService = ServiceManager.getService(ICustomerRemindService.class);
    try{

      RemindEventStrategySelector remindEventStrategySelector = ServiceManager.getService(RemindEventStrategySelector.class);
      RemindEventStrategy customerRemindEventStrategy = remindEventStrategySelector.selectStrategy(RemindEventType.CUSTOMER_SERVICE);
      List<RemindEventDTO> customerRemindEventDTOs = customerRemindEventStrategy.queryRemindEvent(shopId, isOverdue, hasRemind,
          DateUtil.getStartTimeOfToday(),getPage().getCurrentPage()-1, getPage().getPageSize());

      List<CustomerServiceJobDTO> customerServiceJobDTOList = new ArrayList<CustomerServiceJobDTO>();

      if(CollectionUtils.isNotEmpty(customerRemindEventDTOs)){
        customerServiceJobDTOList = customerRemindService.generateCustomerServiceJob(shopId,customerRemindEventDTOs);
      }
      if(CollectionUtils.isNotEmpty(customerServiceJobDTOList)){
        List<List<String>> result = new ArrayList<List<String>>();

        for(CustomerServiceJobDTO dto : customerServiceJobDTOList){
          if(dto != null){
            List<String> row = new ArrayList<String>();
            row.add(StringUtil.valueOf(dto.getAppointName()));
            row.add(StringUtil.valueOf(dto.getLicenceNo()));
            row.add(StringUtil.valueOf(dto.getVehicleCustomerName()));
            row.add(StringUtil.valueOf(dto.getVehicleMobile()));
            row.add(StringUtil.valueOf(dto.getCustomerName()));
            row.add(StringUtil.valueOf(dto.getMobile()));
            row.add(StringUtil.valueOf(dto.getRemindTimeStr()));
            //下次保养里程
            row.add(dto.getRemindMileage()!= null ?dto.getRemindMileage().toString():"" );
            //当前里程
            row.add(StringUtil.valueOf(dto.getCurrentMileage()));
            //距离保养里程
            String remainMileageStr = "";
            Long remainMileage = dto.getCurrentMileage() != null && dto.getRemindMileage() != null ?
                dto.getCurrentMileage().longValue() - dto.getRemindMileage() : null;
            if(remainMileage != null){
             if(remainMileage >0){
               remainMileageStr = "超出" + remainMileage + "km";
             }else {
               remainMileageStr = "还剩" + (0-remainMileage) + "km";
             }
            }
            row.add(remainMileageStr);
            //状态
            if(StringUtils.isNotBlank(dto.getStatus())){
              row.add(dto.getStatus().equals("reminded")?"已提醒":"未提醒");
            }else{
              row.add(null);
            }
            result.add(row);
          }
        }
        return result;
      }
      return null;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return null;
    }


  }


}
