package com.bcgogo.txn.service.exportExcel;

import com.bcgogo.config.service.excelexport.BcgogoExportDataIterator;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.exception.PageException;
import com.bcgogo.search.dto.VehicleSearchConditionDTO;
import com.bcgogo.search.dto.VehicleSearchResultDTO;
import com.bcgogo.search.service.vehicle.ISearchVehicleService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.pushMessage.faultCode.FaultInfoSearchConditionDTO;
import com.bcgogo.txn.service.vehicleSearch.IVehicleGenerateService;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XinyuQiu on 14-5-26.
 */
public class VehicleListExportDataIterator extends BcgogoExportDataIterator {
  private static final Logger LOG = LoggerFactory.getLogger(VehicleListExportDataIterator.class);
  private static final int PAGE_SIZE = 1000;
  private int totalRows = 0;
  private VehicleSearchConditionDTO vehicleSearchConditionDTO;

  public VehicleListExportDataIterator(int totalRows, VehicleSearchConditionDTO vehicleSearchConditionDTO) throws PageException {
    super(totalRows, PAGE_SIZE, ConfigUtils.getTotalNumPerExcel());
    this.vehicleSearchConditionDTO = vehicleSearchConditionDTO;
    this.totalRows = totalRows;
    vehicleSearchConditionDTO.setMaxRows(PAGE_SIZE);
  }

  @Override
  protected int getTotalRows() {
    return totalRows;
  }

  @Override
  protected List<String> getHead() {
    return ExportVehicleListConstant.fieldList;
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
    return assembleVehicleInfo();
  }

  private List<List<String>> assembleVehicleInfo(){
    ISearchVehicleService searchVehicleService = ServiceManager.getService(ISearchVehicleService.class);
    IVehicleGenerateService vehicleGenerateService = ServiceManager.getService(IVehicleGenerateService.class);
    try{
      VehicleSearchResultDTO vehicleSearchResultDTO = searchVehicleService.queryVehicle(vehicleSearchConditionDTO);
      if(vehicleSearchResultDTO != null && CollectionUtils.isNotEmpty(vehicleSearchResultDTO.getVehicleDTOList())){
        vehicleGenerateService.generateVehicleSearchResult(vehicleSearchConditionDTO.getShopId(), vehicleSearchResultDTO);
        List<List<String>> rows = new ArrayList<List<String>>();
        for(VehicleDTO vehicleDTO : vehicleSearchResultDTO.getVehicleDTOList()){
          if(vehicleDTO != null){
            List<String> row = new ArrayList<String>();
            row.add(StringUtil.valueOf(vehicleDTO.getLicenceNo()));
            row.add(StringUtil.valueOf(vehicleDTO.getContact()));
            row.add(StringUtil.valueOf(vehicleDTO.getMobile()));
            if(vehicleDTO.getCustomerDTO()!=null){
              row.add(StringUtil.valueOf(vehicleDTO.getCustomerDTO().getName()));
              row.add(StringUtil.valueOf(vehicleDTO.getCustomerDTO().getMobile()));
            }else {
              row.add("");
              row.add("");
            }
            row.add(StringUtil.joinStrings(" ", "", vehicleDTO.getBrand(), vehicleDTO.getModel(), vehicleDTO.getColor()));
            row.add(StringUtil.valueOf(vehicleDTO.getVehicleTotalConsumeCount()));
            row.add(StringUtil.valueOf(vehicleDTO.getVehicleTotalConsumeAmount()));
            row.add(StringUtil.valueOf(vehicleDTO.getVehicleLastConsumeTimeStr()));
            row.add(StringUtil.valueOf(vehicleDTO.getObdMileage()));
            row.add(StringUtil.valueOf(vehicleDTO.getMaintainMileage()));
            row.add(StringUtil.valueOf(vehicleDTO.getMaintainTimeStr()));
            StringBuilder  maintainIntervalsMileageSb  = new StringBuilder();
            if(vehicleDTO.getMaintainIntervalsMileage()!=null){
              if(vehicleDTO.getMaintainIntervalsMileage()<0){
                maintainIntervalsMileageSb.append("超出").append(Math.abs(NumberUtil.round(vehicleDTO.getMaintainIntervalsMileage(),0)));
              }else {
                maintainIntervalsMileageSb.append("还有").append(NumberUtil.round(vehicleDTO.getMaintainIntervalsMileage(),0));
              }
              maintainIntervalsMileageSb.append("KM");
            }
            row.add(maintainIntervalsMileageSb.toString());

            StringBuilder  maintainIntervalsDaysSb  = new StringBuilder();
            if(vehicleDTO.getMaintainIntervalsDays()!=null){
              if(vehicleDTO.getMaintainIntervalsDays()<0){
                maintainIntervalsDaysSb.append("超出").append(Math.abs(vehicleDTO.getMaintainIntervalsDays()));
              }else {
                maintainIntervalsDaysSb.append("还有").append(vehicleDTO.getMaintainIntervalsDays());
              }
              maintainIntervalsDaysSb.append("天");
            }
            row.add(maintainIntervalsDaysSb.toString());
            row.add(StringUtil.valueOf(vehicleDTO.getInsureTimeStr()));


            rows.add(row);
          }
        }
        return rows;
      }else {
        return null;
      }


    }catch (Exception e){
      LOG.error(e.getMessage(),e);

    }

      return null;
  }
}
