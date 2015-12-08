package com.bcgogo.txn.service.exportExcel;

import com.bcgogo.common.Pager;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.excelexport.BcgogoExportDataIterator;
import com.bcgogo.exception.PageException;
import com.bcgogo.search.dto.OrderSearchConditionDTO;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.assistantStat.AssistantAchievementStatDTO;
import com.bcgogo.txn.dto.assistantStat.AssistantStatSearchDTO;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.stat.assistantStat.IAssistantStatService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: mayan
 * Date: 13-12-11
 * Time: 下午5:40
 * To change this template use File | Settings | File Templates.
 */
public class BusinessStaffDataIterator extends BcgogoExportDataIterator{
    private static final Logger LOG = LoggerFactory.getLogger(InventoryExportDataIterator.class);
    private static final int PAGE_SIZE = 1000;
    private AssistantStatSearchDTO assistantStatSearchDTO;

    public BusinessStaffDataIterator(AssistantStatSearchDTO assistantStatSearchDTO) throws PageException {
        super(assistantStatSearchDTO.getMaxRows(), PAGE_SIZE, Integer.valueOf(ServiceManager.getService(IConfigService.class).getConfig("TotalNumPerExcel", ShopConstant.BC_SHOP_ID)));
        this.assistantStatSearchDTO=assistantStatSearchDTO;
    }

    @Override
    protected int getTotalRows() {
        if(assistantStatSearchDTO!=null){
            return assistantStatSearchDTO.getMaxRows();
        }else{
            return 0;
        }
    }

    @Override
    public Object next() {
       //取下一页数据
        getPage().gotoNextPage();
         //生成要导出的数据
        List<List<String>> rows = assembleBusinessTransactionInfo(getAssistantAchievementStatDTO(assistantStatSearchDTO),assistantStatSearchDTO);
        return rows;

    }

    @Override
    protected List<String> getHead() {
        List<String> head = null;
        if("ALL".equals(assistantStatSearchDTO.getAchievementOrderTypeStr())){
            if(assistantStatSearchDTO.getAssistantAchievementStatDTOList().get(0).getAssistantName()!=null){
                head = Arrays.asList(BusinessStaffConstant.staff_allFields);
            }else{
                head = Arrays.asList(BusinessStaffConstant.department_allFields);
            }

        }else if("REPAIR_SERVICE".equals(assistantStatSearchDTO.getAchievementOrderTypeStr())){
            if(assistantStatSearchDTO.getAssistantAchievementStatDTOList().get(0).getAssistantName()!=null){
                head = Arrays.asList(BusinessStaffConstant.staff_vehicleFields);
            }else{
                head = Arrays.asList(BusinessStaffConstant.department_vehicleFields);
            }
        }else if("MEMBER".equals(assistantStatSearchDTO.getAchievementOrderTypeStr())){
            if(assistantStatSearchDTO.getAssistantAchievementStatDTOList().get(0).getAssistantName()!=null){
                head = Arrays.asList(BusinessStaffConstant.staff_memberFields);
            }else{
                head = Arrays.asList(BusinessStaffConstant.department_memberFields);
            }
        }else if("WASH_BEAUTY".equals(assistantStatSearchDTO.getAchievementOrderTypeStr())){
            if(assistantStatSearchDTO.getAssistantAchievementStatDTOList().get(0).getAssistantName()!=null){
                head = Arrays.asList(BusinessStaffConstant.staff_washFields);
            }else{
                head = Arrays.asList(BusinessStaffConstant.department_washFields);
            }
        }else if("SALES".equals(assistantStatSearchDTO.getAchievementOrderTypeStr())){
            if(assistantStatSearchDTO.getAssistantAchievementStatDTOList().get(0).getAssistantName()!=null){
                head = Arrays.asList(BusinessStaffConstant.staff_salesFields);
            }else{
                head = Arrays.asList(BusinessStaffConstant.department_salesFields);
            }
        }else if("BUSINESS_ACCOUNT".equals(assistantStatSearchDTO.getAchievementOrderTypeStr())){
            if(assistantStatSearchDTO.getAssistantAchievementStatDTOList().get(0).getAssistantName()!=null){
                head = Arrays.asList(BusinessStaffConstant.staff_businessAccountFields);
            }else{
                head = Arrays.asList(BusinessStaffConstant.department_businessAccountFields);
            }
        }
        return head;
    }


    @Override
    protected List<String> getTailShowInfo() {
        return null;
    }

    @Override
    protected List<String> getHeadShowInfo() {
        return null;
    }

    private List<AssistantAchievementStatDTO> getAssistantAchievementStatDTO(AssistantStatSearchDTO assistantStatSearchDTO){
        return assistantStatSearchDTO.getAssistantAchievementStatDTOList();
    }

    private List<List<String>> assembleBusinessTransactionInfo(List<AssistantAchievementStatDTO> assistantAchievementStatDTOs,AssistantStatSearchDTO assistantStatSearchDTO) {
        if(CollectionUtil.isEmpty(assistantAchievementStatDTOs)){
            return null;
        }
        List<List<String>> rows=new ArrayList<List<String>>();
        for(AssistantAchievementStatDTO assistantAchievementStatDTO:assistantAchievementStatDTOs){
            List<String> row=new ArrayList<String>();
            if(assistantAchievementStatDTO.getAssistantName()!=null){
                row.add(assistantAchievementStatDTO.getAssistantName().equals("") ? "":assistantAchievementStatDTO.getAssistantName());//员工
            }
          if(assistantAchievementStatDTO.getDepartmentName()==null){
            assistantAchievementStatDTO.setDepartmentName("");
          }
            row.add(assistantAchievementStatDTO.getDepartmentName().equals("") ? "":assistantAchievementStatDTO.getDepartmentName());  //部门
            if("ALL".equals(assistantStatSearchDTO.getAchievementOrderTypeStr())||"REPAIR_SERVICE".equals(assistantStatSearchDTO.getAchievementOrderTypeStr())){
                row.add(assistantAchievementStatDTO.getStandardHours()==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString(assistantAchievementStatDTO.getStandardHours(),2));//车辆施工标准工时
                row.add(assistantAchievementStatDTO.getStandardService()==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString(assistantAchievementStatDTO.getStandardService(),2));//车辆施工工时金额
                row.add(assistantAchievementStatDTO.getActualHours()==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString(assistantAchievementStatDTO.getActualHours(),2));   //车辆施工实际工时
                row.add(assistantAchievementStatDTO.getActualService()==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString(assistantAchievementStatDTO.getActualService(),2));  //车辆施工收入
                    if(assistantStatSearchDTO.getAchievementCalculateWayStr().equals("CALCULATE_BY_ASSISTANT")){
                        row.add(assistantAchievementStatDTO.getServiceAchievementByAssistant()==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString(assistantAchievementStatDTO.getServiceAchievementByAssistant(),2));  //车辆施工提成
                    }
                    if(assistantStatSearchDTO.getAchievementCalculateWayStr().equals("CALCULATE_BY_DETAIL")){
                        row.add(assistantAchievementStatDTO.getServiceAchievement()==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString(assistantAchievementStatDTO.getServiceAchievement(),2));  //车辆施工提成
                    }
            }

            if("ALL".equals(assistantStatSearchDTO.getAchievementOrderTypeStr())||"MEMBER".equals(assistantStatSearchDTO.getAchievementOrderTypeStr())){
                if(assistantStatSearchDTO.getServiceIdStr().equals("")){
                    row.add((assistantAchievementStatDTO.getMember()+assistantAchievementStatDTO.getMemberRenew())==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString((assistantAchievementStatDTO.getMember()+assistantAchievementStatDTO.getMemberRenew()),2));  //会员卡收入
                    if(assistantStatSearchDTO.getAchievementCalculateWayStr().equals("CALCULATE_BY_ASSISTANT")){
                    row.add((assistantAchievementStatDTO.getMemberAchievementByAssistant()+assistantAchievementStatDTO.getMemberRenewAchievementByAssistant())==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString((assistantAchievementStatDTO.getMemberAchievementByAssistant()+assistantAchievementStatDTO.getMemberRenewAchievementByAssistant()),2));  //会员卡提成
                    }
                    if(assistantStatSearchDTO.getAchievementCalculateWayStr().equals("CALCULATE_BY_DETAIL")){
                        row.add((assistantAchievementStatDTO.getMemberAchievement()+assistantAchievementStatDTO.getMemberRenewAchievement())==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString((assistantAchievementStatDTO.getMemberAchievement()+assistantAchievementStatDTO.getMemberRenewAchievement()),2));  //会员卡提成
                    }
                }else if(assistantStatSearchDTO.getServiceIdStr().equals("NEW")){
                    row.add(assistantAchievementStatDTO.getMember()==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString(assistantAchievementStatDTO.getMember(),2));  //会员卡收入
                    if(assistantStatSearchDTO.getAchievementCalculateWayStr().equals("CALCULATE_BY_ASSISTANT")){
                    row.add(assistantAchievementStatDTO.getMemberAchievementByAssistant()==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString(assistantAchievementStatDTO.getMemberAchievementByAssistant(),2));  //会员卡提成
                    }
                    if(assistantStatSearchDTO.getAchievementCalculateWayStr().equals("CALCULATE_BY_DETAIL")){
                        row.add(assistantAchievementStatDTO.getMemberAchievement()==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString(assistantAchievementStatDTO.getMemberAchievement(),2));  //会员卡提成
                    }
                    }else{
                    row.add(assistantAchievementStatDTO.getMemberRenew()==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString(assistantAchievementStatDTO.getMemberRenew(),2));  //会员卡收入
                    if(assistantStatSearchDTO.getAchievementCalculateWayStr().equals("CALCULATE_BY_ASSISTANT")){
                    row.add(assistantAchievementStatDTO.getMemberRenewAchievementByAssistant()==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString(assistantAchievementStatDTO.getMemberRenewAchievementByAssistant(),2));  //会员卡提成
                    }
                    if(assistantStatSearchDTO.getAchievementCalculateWayStr().equals("CALCULATE_BY_DETAIL")){
                        row.add(assistantAchievementStatDTO.getMemberRenewAchievement()==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString(assistantAchievementStatDTO.getMemberRenewAchievement(),2));  //会员卡提成
                    }
                }
            }

            if("ALL".equals(assistantStatSearchDTO.getAchievementOrderTypeStr())||"WASH_BEAUTY".equals(assistantStatSearchDTO.getAchievementOrderTypeStr())){
                row.add(assistantAchievementStatDTO.getWashTimes()==0 ? StringUtil.doubleToString(0.0,0): StringUtil.longToString(assistantAchievementStatDTO.getWashTimes(),"0"));  //洗车次数
                row.add(assistantAchievementStatDTO.getWash()==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString(assistantAchievementStatDTO.getWash(),2));  //洗车收入
                if(assistantStatSearchDTO.getAchievementCalculateWayStr().equals("CALCULATE_BY_ASSISTANT")){
                row.add(assistantAchievementStatDTO.getWashAchievementByAssistant()==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString(assistantAchievementStatDTO.getWashAchievementByAssistant(),2));  //洗车提成
                }
                if(assistantStatSearchDTO.getAchievementCalculateWayStr().equals("CALCULATE_BY_DETAIL")){
                    row.add(assistantAchievementStatDTO.getWashAchievement()==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString(assistantAchievementStatDTO.getWashAchievement(),2));  //洗车提成
                }
            }

            if("ALL".equals(assistantStatSearchDTO.getAchievementOrderTypeStr())||"SALES".equals(assistantStatSearchDTO.getAchievementOrderTypeStr())){
                if(assistantStatSearchDTO.getAchievementCalculateWayStr().equals("CALCULATE_BY_ASSISTANT")){
                    row.add(assistantAchievementStatDTO.getSale()==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString(assistantAchievementStatDTO.getSale(),2));  //商品销售收入
                    row.add(assistantAchievementStatDTO.getSalesAchievementByAssistant()==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString(assistantAchievementStatDTO.getSalesAchievementByAssistant(),2));  //商品销售提成
                    row.add(assistantAchievementStatDTO.getSalesProfit()==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString(assistantAchievementStatDTO.getSalesProfit(),2));  //商品销售利润
                    row.add(assistantAchievementStatDTO.getSalesProfitAchievementByAssistant()==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString(assistantAchievementStatDTO.getSalesProfitAchievementByAssistant(),2));  //商品销售利润提成

                }
                if(assistantStatSearchDTO.getAchievementCalculateWayStr().equals("CALCULATE_BY_DETAIL")){
                    row.add(assistantAchievementStatDTO.getSale()==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString(assistantAchievementStatDTO.getSale(),2));  //商品销售收入
                    row.add(assistantAchievementStatDTO.getSaleAchievement()==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString(assistantAchievementStatDTO.getSaleAchievement(),2));  //商品销售提成
                    row.add(assistantAchievementStatDTO.getSalesProfit()==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString(assistantAchievementStatDTO.getSalesProfit(),2));  //商品销售利润
                    row.add(assistantAchievementStatDTO.getSalesProfitAchievement()==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString(assistantAchievementStatDTO.getSalesProfitAchievement(),2));  //商品销售利润提成

                }

            }

            if("ALL".equals(assistantStatSearchDTO.getAchievementOrderTypeStr())||"BUSINESS_ACCOUNT".equals(assistantStatSearchDTO.getAchievementOrderTypeStr())){
                row.add(assistantAchievementStatDTO.getBusinessAccount()==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString(assistantAchievementStatDTO.getBusinessAccount(),2));  //营业外记账收入
            }
            if(assistantStatSearchDTO.getAchievementOrderTypeStr()!=null){
                if(assistantStatSearchDTO.getAchievementOrderTypeStr().equals("ALL")){
                    if(assistantStatSearchDTO.getAchievementCalculateWayStr().equals("CALCULATE_BY_ASSISTANT")){
                        row.add(assistantAchievementStatDTO.getStatSum()==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString(assistantAchievementStatDTO.getStatSum(),2));  //合计收入
                        row.add(assistantAchievementStatDTO.getAchievementSumByAssistant()==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString(assistantAchievementStatDTO.getAchievementSumByAssistant(),2));  //合计提成
                        row.add(assistantAchievementStatDTO.getSalesProfit()==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString(assistantAchievementStatDTO.getSalesProfit(),2));  //合计利润
                        row.add(assistantAchievementStatDTO.getSalesProfitAchievementByAssistant()==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString(assistantAchievementStatDTO.getSalesProfitAchievementByAssistant(),2));  //合计利润提成

                    }
                    if(assistantStatSearchDTO.getAchievementCalculateWayStr().equals("CALCULATE_BY_DETAIL")){
                        row.add(assistantAchievementStatDTO.getStatSum()==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString(assistantAchievementStatDTO.getStatSum(),2));  //合计收入
                        row.add(assistantAchievementStatDTO.getAchievementSum()==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString(assistantAchievementStatDTO.getAchievementSum(),2));  //合计提成
                        row.add(assistantAchievementStatDTO.getSalesProfit()==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString(assistantAchievementStatDTO.getSalesProfit(),2));  //合计利润
                        row.add(assistantAchievementStatDTO.getSalesProfitAchievement()==0.0 ? StringUtil.doubleToString(0.0,2): StringUtil.doubleToString(assistantAchievementStatDTO.getSalesProfitAchievement(),2));  //合计利润提成

                    }
                    }
                rows.add(row);
            }

        }
        return rows;
    }

}
