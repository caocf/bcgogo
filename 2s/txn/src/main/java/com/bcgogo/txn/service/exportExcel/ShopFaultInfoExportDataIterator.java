package com.bcgogo.txn.service.exportExcel;

import com.bcgogo.config.service.excelexport.BcgogoExportDataIterator;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.YesNo;
import com.bcgogo.enums.txn.Status;
import com.bcgogo.exception.PageException;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.pushMessage.faultCode.FaultInfoSearchConditionDTO;
import com.bcgogo.txn.dto.pushMessage.faultCode.FaultInfoToShopDTO;
import com.bcgogo.txn.service.pushMessage.faultCode.IShopFaultInfoService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XinyuQiu on 14-5-26.
 */
public class ShopFaultInfoExportDataIterator extends BcgogoExportDataIterator {

  private static final Logger LOG = LoggerFactory.getLogger(ShopFaultInfoExportDataIterator.class);
  private static final int PAGE_SIZE = 1000;
  private int totalRows = 0;

  private FaultInfoSearchConditionDTO searchCondition;

  public ShopFaultInfoExportDataIterator(FaultInfoSearchConditionDTO searchCondition,int totalRows) throws PageException {
    super(totalRows, PAGE_SIZE, ConfigUtils.getTotalNumPerExcel());
    this.searchCondition = searchCondition;
    searchCondition.setStartPageNo(getPage().getCurrentPage());
    searchCondition.setMaxRows(getPage().getPageSize());
    this.totalRows = totalRows;
  }


  @Override
  protected int getTotalRows() {
    return totalRows;
  }

  @Override
  protected List<String> getHead() {
    return ExportShopFaultInfoConstant.fieldList;
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
    return assembleShopFaultInfo();
  }

  private List<List<String>> assembleShopFaultInfo() {
    IShopFaultInfoService shopFaultInfoService = ServiceManager.getService(IShopFaultInfoService.class);
    List<FaultInfoToShopDTO> faultInfoToShopDTOs = shopFaultInfoService.getShopFaultInfoList(searchCondition);
    List<List<String>> rows = new ArrayList<List<String>>();
    if (CollectionUtils.isNotEmpty(faultInfoToShopDTOs)) {
      for (FaultInfoToShopDTO faultInfoToShopDTO : faultInfoToShopDTOs) {
        if (faultInfoToShopDTO != null) {
          List<String> row = new ArrayList<String>();
          row.add(StringUtil.valueOf(faultInfoToShopDTO.getFaultCodeReportTimeStr()));
          row.add(StringUtil.valueOf(faultInfoToShopDTO.getFaultAlertTypeValue()));
          row.add(StringUtil.valueOf(faultInfoToShopDTO.getVehicleNo()));
          row.add(StringUtil.joinStrings(" ","",faultInfoToShopDTO.getFaultCode(),
              faultInfoToShopDTO.getFaultCodeCategory(),faultInfoToShopDTO.getFaultCodeDescription()));
          row.add(StringUtil.joinStrings(" ","",faultInfoToShopDTO.getVehicleBrand(),faultInfoToShopDTO.getVehicleModel()));
          row.add(StringUtil.valueOf(faultInfoToShopDTO.getMobile()));
          row.add(StringUtil.valueOf(faultInfoToShopDTO.getCustomerName()));
          row.add(StringUtil.valueOf(faultInfoToShopDTO.getCustomerMobile()));
          StringBuilder status = new StringBuilder();
          if(Status.DELETED.equals(faultInfoToShopDTO.getStatus())){
            status.append("已删除");
          }else {
            if(YesNo.YES.equals(faultInfoToShopDTO.getIsSendMessage())){
              status.append("已发消息");
            }
            if(YesNo.YES.equals(faultInfoToShopDTO.getIsCreateAppointOrder())){
              if(YesNo.YES.equals(faultInfoToShopDTO.getIsSendMessage())){
                status.append(" ");
              }
              status.append("已预约");
            }
            if(status.length() == 0){
              status.append("未处理");
            }
          }
          row.add(status.toString());
          rows.add(row);
        }
      }
    }
    return rows;
  }


}
