package com.bcgogo.txn;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.txn.dto.QualifiedCredentialsDTO;
import com.bcgogo.txn.dto.RepairOrderDTO;
import com.bcgogo.utils.MoneyUtil;
import com.bcgogo.utils.NumberUtil;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.*;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-9-4
 * Time: 上午11:58
 * To change this template use File | Settings | File Templates.
 */
public final class ExportOrderListExcel {
  public  static void createExcel(OutputStream os,RepairOrderDTO repairOrderDTO,ShopDTO shopDTO,QualifiedCredentialsDTO qualifiedCredentialsDTO) throws WriteException, IOException, BiffException {

    //创建工作薄
    WritableWorkbook workbook = Workbook.createWorkbook(os);               //new File("D:\\eee.xls")
    //创建新的一页
    WritableSheet sheet = workbook.createSheet("First Sheet", 0);
    //加边框
    WritableFont   wf   =   new   WritableFont(WritableFont.createFont("宋体"),10,WritableFont.NO_BOLD);
    WritableCellFormat   wcf   =   new   WritableCellFormat(wf);
    wcf.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN);
    //构造表头
    sheet.mergeCells(0, 0, 8, 0);//添加合并单元格，第一个参数是起始列，第二个参数是起始行，第三个参数是终止列，第四个参数是终止行
    WritableFont bold = new WritableFont(WritableFont.ARIAL,17,WritableFont.BOLD);//设置字体种类和黑体显示,字体为Arial,字号大小为10,采用黑体显示
    WritableCellFormat titleFormate = new WritableCellFormat(bold);//生成一个单元格样式控制对象
    titleFormate.setAlignment(jxl.format.Alignment.CENTRE);//单元格中的内容水平方向居中
    titleFormate.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);//单元格的内容垂直方向居中
    Label title = new Label(0,0,"江苏省机动车维修费用结算清单",titleFormate);
    sheet.setRowView(0, 600, false);//设置第一行的高度
    sheet.addCell(title);

    WritableFont bold1 = new WritableFont(WritableFont.createFont("宋体"),10,WritableFont.NO_BOLD);//设置字体种类和黑体显示,字体为Arial,字号大小为10,采用黑体显示
    WritableCellFormat titleFormate1 = new WritableCellFormat(bold1);//生成一个单元格样式控制对象
    titleFormate1.setAlignment(jxl.format.Alignment.CENTRE);//单元格中的内容水平方向居中
    titleFormate1.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);//单元格的内容垂直方向居中
    titleFormate1.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN);
    sheet.mergeCells(0,1,0,4);
    Label customerLablel = new Label(0,1,"托修方",titleFormate1);
    sheet.addCell(customerLablel);

    sheet.mergeCells(0,5,0,7);
    Label ownerLablel = new Label(0,5,"承修方",titleFormate1);
    sheet.addCell(ownerLablel);
    //
    Label ownerName = new Label(1,1,"单位名称（车主姓名）",wcf);
    sheet.addCell(ownerName);
    Label carNumber = new Label(1,2,"车牌号码",wcf);
    sheet.addCell(carNumber);
    Label date = new Label(1,3,"进厂日期",wcf);
    sheet.addCell(date);
    Label read = new Label(1,4,"出厂里程表读数(km)",wcf);
    sheet.addCell(read);

    sheet.mergeCells(2,1,4,1);
    Label ownerNameValue = new Label(2,1,repairOrderDTO.getCustomerName()==null?"":repairOrderDTO.getCustomerName(),wcf); //位名称（车主姓名）
    sheet.addCell(ownerNameValue);
    Label carNumberValue = new Label(2,2,repairOrderDTO.getLicenceNo()==null?"":repairOrderDTO.getLicenceNo(),wcf);       //车牌号码
    sheet.addCell(carNumberValue);
    Label dateValue = new Label(2,3,repairOrderDTO.getStartDateStr()==null?"":repairOrderDTO.getStartDateStr(),wcf); //进厂日期
    sheet.addCell(dateValue);
    Label readValue = new Label(2,4,repairOrderDTO.getStartMileage()==null?"":repairOrderDTO.getStartMileage().toString(),wcf);  //出厂里程表读数
    sheet.addCell(readValue);
    //
    Label type = new Label(3,2,"厂牌型号",wcf);
    sheet.addCell(type);
    Label contractNO = new Label(3,3,"合同编号",wcf);
    sheet.addCell(contractNO);
    Label certificate = new Label(3,4,"合格证号",wcf);
    sheet.addCell(certificate);

    Label typeValue = new Label(4,2,repairOrderDTO.getBrand()==null?"":repairOrderDTO.getBrand(),wcf); //厂牌型号
    sheet.addCell(typeValue);
    Label contractNOValue = new Label(4,3,repairOrderDTO.getRepairContractNo()==null?"":repairOrderDTO.getRepairContractNo(),wcf); //合同编号
    sheet.addCell(contractNOValue);
    Label certificateValue = new Label(4,4,repairOrderDTO.getQualifiedNo()==null?"":repairOrderDTO.getQualifiedNo(),wcf);   //合格证明
    sheet.addCell(certificateValue);
    //
    Label customer = new Label(5,1,"送修人",wcf);
    sheet.addCell(customer);
    Label repairType = new Label(5,2,"维修类别",wcf);
    sheet.addCell(repairType);
    Label workOrderNO = new Label(5,3,"工单号码",wcf);
    sheet.addCell(workOrderNO);
    Label callNum = new Label(5,4,"联系电话",wcf);
    sheet.addCell(callNum);

    Label customerValue;
    if(repairOrderDTO.getCustomerName()!=null){
       customerValue = new Label(6,1,repairOrderDTO.getCustomerName(),wcf);  //送修人
    } else if(repairOrderDTO.getVehicleContact()!=null){
       customerValue = new Label(6,1,repairOrderDTO.getVehicleContact(),wcf);  //送修人
    } else{
       customerValue = new Label(6,1,"",wcf);  //送修人
    }
    sheet.addCell(customerValue);
    Label repairTypeValue = new Label(6,2,qualifiedCredentialsDTO.getRepairType()==null?"":qualifiedCredentialsDTO.getRepairType(),wcf);//维修类别
    sheet.addCell(repairTypeValue);
    Label workOrderNOValue = new Label(6,3,repairOrderDTO.getId()==null?"":repairOrderDTO.getId().toString(),wcf); //工单号码
    sheet.addCell(workOrderNOValue);
    Label callNumValue = new Label(6,4,repairOrderDTO.getMobile()==null?"":repairOrderDTO.getMobile(),wcf);  //联系电话
    sheet.addCell(callNumValue);
    //
    Label unitName = new Label(1,5,"单位名称",wcf);
    sheet.addCell(unitName);
    Label untiAddr = new Label(1,6,"单位地址",wcf);
    sheet.addCell(untiAddr);
    Label openingBank = new Label(1,7,"开户银行",wcf);
    sheet.addCell(openingBank);

    sheet.mergeCells(2,6,4,6);
    sheet.mergeCells(2,5,4,5);
    sheet.mergeCells(2,7,4,7);
    Label unitNameValue = new Label(2,5,shopDTO.getName()==null?"":shopDTO.getName(),wcf); //单位名称
    sheet.addCell(unitNameValue);
    Label untiAddrValue = new Label(2,6,shopDTO.getAddress()==null?"":shopDTO.getAddress(),wcf); //单位地址
    sheet.addCell(untiAddrValue);
    Label openingBankValue = new Label(2,7,"",wcf); //开户银行
    sheet.addCell(openingBankValue);
    //
    Label call = new Label(5,5,"联系电话",wcf);
    sheet.addCell(call);
    Label email = new Label(5,6,"E_Mail",wcf);
    sheet.addCell(email);
    Label username = new Label(5,7,"账号",wcf);
    sheet.addCell(username);

    Label callValue = new Label(6,5,shopDTO.getMobile()==null?"":shopDTO.getMobile(),wcf);//联系电话
    sheet.addCell(callValue);
    Label emailValue = new Label(6,6,shopDTO.getEmail()==null?"":shopDTO.getEmail(),wcf);    //Email
    sheet.addCell(emailValue);
    Label usernameValue = new Label(6,7,"",wcf);    //账号
    sheet.addCell(usernameValue);
    //
    sheet.mergeCells(0, 8, 8, 8);
    Label table1 = new Label(0,8,"表一 维修费用结算表（维修费用=维修诊断费+检测费+材料费+工时费+加工费+其它费用）",wcf);
    sheet.addCell(table1);
    //

    sheet.mergeCells(1, 9, 3, 9);
    sheet.mergeCells(1, 10, 3, 10);
    sheet.mergeCells(1, 11, 3, 11);
    sheet.mergeCells(1, 12, 3, 12);
    sheet.mergeCells(1, 13, 3, 13);
    sheet.mergeCells(1, 14, 3, 14);
    sheet.mergeCells(1, 15, 3, 15);
    sheet.mergeCells(1, 16, 3, 16);

    sheet.mergeCells(4, 9, 5, 9);
    sheet.mergeCells(4, 10, 5, 10);
    sheet.mergeCells(4, 11, 5, 11);
    sheet.mergeCells(4, 12, 5, 12);
    sheet.mergeCells(4, 13, 5, 13);
    sheet.mergeCells(4, 14, 5, 14);
    sheet.mergeCells(4, 15, 5, 15);
    sheet.mergeCells(4, 16, 5, 16);


    sheet.mergeCells(6, 9, 8, 9);
    sheet.mergeCells(6, 10, 8, 10);
    sheet.mergeCells(6, 11, 8, 11);
    sheet.mergeCells(6, 12, 8, 12);
    sheet.mergeCells(6, 13, 8, 13);
    sheet.mergeCells(6, 14, 8, 14);
    sheet.mergeCells(6, 15, 8, 15);
    sheet.mergeCells(6, 16, 8, 16);

    sheet.mergeCells(6, 1, 8, 1);
    sheet.mergeCells(6, 2, 8, 2);
    sheet.mergeCells(6, 3, 8, 3);
    sheet.mergeCells(6, 4, 8, 4);
    sheet.mergeCells(6, 5, 8, 5);
    sheet.mergeCells(6, 6, 8, 6);
    sheet.mergeCells(6, 7, 8, 7);

    Label serialNumber = new Label(0,9,"序号",wcf);
    sheet.addCell(serialNumber);
    Label name = new Label(1,9,"名称",wcf);
    sheet.addCell(name);
    Label money = new Label(4,9,"金额（元）",wcf);
    sheet.addCell(money);
    Label remarks = new Label(6,9,"备注",wcf);
    sheet.addCell(remarks);
    //
    Label number1 = new Label(0,10,"1",wcf);
    sheet.addCell(number1);
    Label number2 = new Label(0,11,"2",wcf);
    sheet.addCell(number2);
    Label number3 = new Label(0,12,"3",wcf);
    sheet.addCell(number3);
    Label number4 = new Label(0,13,"4",wcf);
    sheet.addCell(number4);
    Label number5 = new Label(0,14,"5",wcf);
    sheet.addCell(number5);
    Label number6 = new Label(0,15,"6",wcf);
    sheet.addCell(number6);
    Label number7 = new Label(0,16,"7",wcf);
    sheet.addCell(number7);
    //
    Label money1 = new Label(1,10,"维修诊断费",wcf);
    sheet.addCell(money1);
    Label weixiu = new Label(4,10,"0.0",wcf) ;
    sheet.addCell(weixiu);
    Label money2 = new Label(1,11,"检测费",wcf);
    sheet.addCell(money2);
    Label jiance = new Label(4,11,"0.0",wcf);
    sheet.addCell(jiance);
    Label money3 = new Label(1,12,"材料费",wcf);
    sheet.addCell(money3);
    Label money4 = new Label(1,13,"工时费",wcf);
    sheet.addCell(money4);

// 工时费
    double servicemoney = 0.00;
    double actual = 0.00;
    if(null!=repairOrderDTO.getServiceDTOs()&&0<repairOrderDTO.getServiceDTOs().length){
      for(int k=0;k< repairOrderDTO.getServiceDTOs().length;k++){
        if(null!= repairOrderDTO.getServiceDTOs()[k].getTotal()){
          servicemoney+= repairOrderDTO.getServiceDTOs()[k].getTotal();
        }
        if(null!=repairOrderDTO.getServiceDTOs()[k].getActualHours()){
          actual+= repairOrderDTO.getServiceDTOs()[k].getActualHours();
        }
      }
    }


    double sigleValue=0.00;
    if(servicemoney!=0.00&&servicemoney!=0&&actual!=0&&actual!=0.00){
       sigleValue = servicemoney/actual;
    }

    String gsf = servicemoney+"";
    Label serviceMoney = new Label(4,13,gsf,wcf);
    sheet.addCell(serviceMoney);


    Label money5 = new Label(1,14,"加工费",wcf);
    sheet.addCell(money5);
    Label money5value1 = new Label(4,14,"0.0",wcf);
    sheet.addCell(money5value1);
    Label money6 = new Label(1,15,"其他费用",wcf);
    sheet.addCell(money6);

//    Label timeMoney = new Label(2,15,repairOrderDTO.getOtherIncomeTotal()==null?"":repairOrderDTO.getOtherIncomeTotal().toString());  //其他费用
//    sheet.addCell(timeMoney);
    //其他费用
    double othermoney = 0.00;
    if(null!=repairOrderDTO.getOtherIncomeItemDTOList()&&0!=repairOrderDTO.getOtherIncomeItemDTOList().size()){
      for(int i=0;i<repairOrderDTO.getOtherIncomeItemDTOList().size();i++){
        othermoney+=  repairOrderDTO.getOtherIncomeItemDTOList().get(i) .getPrice();
      }
    }
    Label otherMoney = new Label(4,15,othermoney+"",wcf);
    sheet.addCell(otherMoney);

    //材料费
    double itemmoney=0.00;
    if(null!=repairOrderDTO.getItemDTOs()&&0!=repairOrderDTO.getItemDTOs().length){
      for(int j=0;j< repairOrderDTO.getItemDTOs().length;j++){
        itemmoney+=   repairOrderDTO.getItemDTOs()[j].getTotal();
      }
    }
    Label itemMoney = new Label(4,12,itemmoney+"",wcf);
    sheet.addCell(itemMoney);



    Label money7 = new Label(1,16,"合计金额（元）",wcf);
    sheet.addCell(money7);

     double totalvalue=  servicemoney+ othermoney+ itemmoney;

    Label _totleValue = new Label(4,16,totalvalue==0.0?"0.0":String.valueOf(totalvalue),wcf);
    sheet.addCell(_totleValue);
    //
    sheet.mergeCells(0, 17, 8,17);
    Label table2 = new Label(0,17,"实收金额大写（元）:"+ MoneyUtil.toBigType(totalvalue+""),wcf);
    sheet.addCell(table2);


    sheet.mergeCells(0, 18,8, 18);
    Label table3 = new Label(0,18,"表二 维修诊断费",wcf);
    sheet.addCell(table3);
    //
    Label serialNumber1 = new Label(0,19,"序号",wcf);
    sheet.addCell(serialNumber1);
    Label name1 = new Label(1,19,"维修诊断项目",wcf);
    sheet.addCell(name1);
    Label moneyOne = new Label(4,19,"金额（元）",wcf);
    sheet.addCell(moneyOne);
    Label remarks1 = new Label(6,19,"备注",wcf);
    sheet.addCell(remarks1);

    Label no3 = new Label(0,20,"1",wcf);
    sheet.addCell(no3);
    Label no4 = new Label(0,21,"2",wcf);
    sheet.addCell(no4);

    Label no1 = new Label(1,20,"",wcf);
    sheet.addCell(no1);
    Label no2 = new Label(1,21,"",wcf);
    sheet.addCell(no2);
    //
    sheet.mergeCells(1, 19, 3, 19);
    sheet.mergeCells(1, 20, 3, 20);
    sheet.mergeCells(1, 21, 3, 21);

    sheet.mergeCells(4, 19, 5, 19);
    sheet.mergeCells(4, 20, 5, 20);
    sheet.mergeCells(4, 21, 5, 21);

    sheet.mergeCells(6, 19, 8, 19);
    sheet.mergeCells(6, 20, 8, 20);
    sheet.mergeCells(6, 21, 8, 21);
    //
    sheet.mergeCells(0, 22, 8,22);
    Label table4 = new Label(0,22,"维修诊断费合计金额（元）：",wcf);
    sheet.addCell(table4);
    sheet.mergeCells(0, 23,8, 23);
    Label table5 = new Label(0,23,"表三 检测费",wcf);
    sheet.addCell(table5);

    Label serialNumber2 = new Label(0,24,"序号",wcf);
    sheet.addCell(serialNumber2);
    Label name2 = new Label(1,24,"检测项目",wcf);
    sheet.addCell(name2);
    Label moneyOne1 = new Label(4,24,"金额（元）",wcf);
    sheet.addCell(moneyOne1);
    Label remarks2 = new Label(6,24,"备注",wcf);
    sheet.addCell(remarks2);

    Label no5 = new Label(0,25,"1",wcf);
    sheet.addCell(no5);
    Label no6 = new Label(0,26,"2",wcf);
    sheet.addCell(no6);

    Label no7 = new Label(1,25,"",wcf);
    sheet.addCell(no7);
    Label no8 = new Label(1,26,"",wcf);
    sheet.addCell(no8);
    //
    sheet.mergeCells(1, 24, 3, 24);
    sheet.mergeCells(1, 25, 3, 25);
    sheet.mergeCells(1, 26, 3, 26);

    sheet.mergeCells(4, 24, 5, 24);
    sheet.mergeCells(4, 25, 5, 25);
    sheet.mergeCells(4, 26, 5, 26);

    sheet.mergeCells(6, 24, 8, 24);
    sheet.mergeCells(6, 25, 8, 25);
    sheet.mergeCells(6, 26, 8, 26);
    //
    sheet.mergeCells(0, 27,8,27);
    Label table6 = new Label(0,27,"检测费合计金额（元）：",wcf);
    sheet.addCell(table6);
    sheet.mergeCells(0, 28,8, 28);
    Label table7 = new Label(0,28,"表四 材料费",wcf);
    sheet.addCell(table7);

    sheet.mergeCells(1, 29, 2, 29);

    Label serialNumber3 = new Label(0,29,"序号",wcf);
    sheet.addCell(serialNumber3);
    Label serialNumber25 = new Label(1,29,"材料名称",wcf);
    sheet.addCell(serialNumber25);
    Label serialNumber26 = new Label(3,29,"厂牌规格",wcf);
    sheet.addCell(serialNumber26);
    Label serialNumber27 = new Label(4,29,"单位",wcf);
    sheet.addCell(serialNumber27);
    Label serialNumber28 = new Label(5,29,"数量",wcf);
    sheet.addCell(serialNumber28);
    Label serialNumber29 = new Label(6,29,"单价（元）",wcf);
    sheet.addCell(serialNumber29);
    Label serialNumber30 = new Label(7,29,"金额（元）",wcf);
    sheet.addCell(serialNumber30);
    Label serialNumber31 = new Label(8,29,"备注",wcf);
    sheet.addCell(serialNumber31);



    //材料费
    int rows = 30;   //总计数
    int i =0;
    double itemTotle=0.00;
    if (repairOrderDTO.getItemDTOs()!=null&&repairOrderDTO.getItemDTOs().length>0){
      for( ;i<repairOrderDTO.getItemDTOs().length;i++){
        sheet.mergeCells(1, rows+i, 2, rows+i);
        Label O_i = new Label(0,rows+i,String.valueOf(i+1),wcf);
        Label a_i = new Label(1,rows+i,repairOrderDTO.getItemDTOs()[i].getProductName()==null?"":repairOrderDTO.getItemDTOs()[i].getProductName(),wcf);
        Label b_i = new Label(3,rows+i,repairOrderDTO.getItemDTOs()[i].getBrand()==null?"":repairOrderDTO.getItemDTOs()[i].getBrand(),wcf);
        Label c_i = new Label(4,rows+i,repairOrderDTO.getItemDTOs()[i].getUnit()==null?"":repairOrderDTO.getItemDTOs()[i].getUnit(),wcf);
        Label d_i = new Label(5,rows+i,repairOrderDTO.getItemDTOs()[i].getAmount()==null?"":repairOrderDTO.getItemDTOs()[i].getAmount().toString(),wcf);
        Label e_i = new Label(6,rows+i,repairOrderDTO.getItemDTOs()[i].getPrice()==null?"":repairOrderDTO.getItemDTOs()[i].getPrice().toString(),wcf);
        Label f_i = new Label(7,rows+i,repairOrderDTO.getItemDTOs()[i].getTotal()==null?"":repairOrderDTO.getItemDTOs()[i].getTotal().toString(),wcf);
        Label g_i = new Label(8,rows+i,"",wcf);
        sheet.addCell(O_i);
        sheet.addCell(a_i);
        sheet.addCell(b_i);
        sheet.addCell(c_i);
        sheet.addCell(d_i);
        sheet.addCell(e_i);
        sheet.addCell(f_i);
        sheet.addCell(g_i);
        itemTotle+= repairOrderDTO.getItemDTOs()[i].getTotal()==null?0:repairOrderDTO.getItemDTOs()[i].getTotal();
      }
      rows+=i;
      rows-=1;
    }else{
      sheet.mergeCells(1, rows, 2, rows);
      sheet.mergeCells(1, rows+1, 2, rows+1);
      Label empty0 = new Label(0,rows,"",wcf);
      sheet.addCell(empty0);
      Label empty0_1 = new Label(1,rows,"",wcf);
      sheet.addCell(empty0_1);
      Label empty0_2 = new Label(3,rows,"",wcf);
      sheet.addCell(empty0_2);
      Label empty0_3 = new Label(4,rows,"",wcf);
      sheet.addCell(empty0_3);
      Label empty0_4 = new Label(5,rows,"",wcf);
      sheet.addCell(empty0_4);
      Label empty0_5 = new Label(6,rows,"",wcf);
      sheet.addCell(empty0_5);
      Label empty0_6 = new Label(7,rows,"",wcf);
      sheet.addCell(empty0_6);
      Label empty0_13 = new Label(8,rows,"",wcf);
      sheet.addCell(empty0_13);
      //
      Label empty1 = new Label(0,rows+1,"",wcf);
      sheet.addCell(empty1);
      Label empty0_7 = new Label(1,rows+1,"",wcf);
      sheet.addCell(empty0_7);
      Label empty0_8 = new Label(3,rows+1,"",wcf);
      sheet.addCell(empty0_8);
      Label empty0_9 = new Label(4,rows+1,"",wcf);
      sheet.addCell(empty0_9);
      Label empty0_10 = new Label(5,rows+1,"",wcf);
      sheet.addCell(empty0_10);
      Label empty0_11 = new Label(6,rows+1,"",wcf);
      sheet.addCell(empty0_11);
      Label empty0_12 = new Label(7,rows+1,"",wcf);
      sheet.addCell(empty0_12);
      Label empty0_14 = new Label(8,rows+1,"",wcf);
      sheet.addCell(empty0_14);
      rows+=1;
    }

    WritableFont bold2 = new WritableFont(WritableFont.createFont("宋体"),10,WritableFont.NO_BOLD);//设置字体种类和黑体显示,字体为Arial,字号大小为10,采用黑体显示
    WritableCellFormat titleFormate2 = new WritableCellFormat(bold2);//生成一个单元格样式控制对象
    titleFormate2.setAlignment(jxl.format.Alignment.CENTRE);//单元格中的内容水平方向居中
    titleFormate2.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);//单元格的内容垂直方向居中
    titleFormate2.setBorder(jxl.format.Border.ALL,jxl.format.BorderLineStyle.THIN);
      sheet.mergeCells(0, rows+1, 1, rows+2);
      sheet.mergeCells(2, rows+1, 8, rows+2);
      Label serialNumber53 = new Label(0, rows+1,"托修方自备配件",titleFormate2);
      sheet.addCell(serialNumber53);
      Label serialNumber100 = new Label(2, rows+1,"",wcf);
      sheet.addCell(serialNumber100);
      rows+=2;
      //
      sheet.mergeCells(0, rows+1, 8,  rows+1);
      Label serialNumber54 = new Label(0, rows+1,"材料费合计金额（元）："+itemTotle+"",wcf);
      sheet.addCell(serialNumber54);
      rows+=1;
      //
      sheet.mergeCells(0, rows+1, 1, rows+1);
      sheet.mergeCells(2, rows+1, 8, rows+1);
      Label serialNumber55 = new Label(0,rows+1,"表五 工时费",wcf);
      sheet.addCell(serialNumber55);

      if(sigleValue!=0.00&&sigleValue!=0){
        String result = String.format("%.2f", sigleValue);
        Label serialNumber56 = new Label(2,rows+1,"工时单价（元）："+NumberUtil.round(sigleValue,2)+"",wcf);
        sheet.addCell(serialNumber56);
      }else{
        Label serialNumber56 = new Label(2,rows+1,"工时单价（元）：",wcf);
        sheet.addCell(serialNumber56);
      }

      rows+=1;
      //
      sheet.mergeCells(1, rows+1, 3, rows+1);
      sheet.mergeCells(7, rows+1, 8, rows+1);
      //
      Label serialNumber57 = new Label(0,rows+1,"序号",wcf);
      sheet.addCell(serialNumber57);
      Label serialNumber58 = new Label(1,rows+1,"维修项目",wcf);
      sheet.addCell(serialNumber58);
      Label serialNumber59 = new Label(4,rows+1,"结算工时",wcf);
      sheet.addCell(serialNumber59);
      Label serialNumber60 = new Label(5,rows+1,"单价",wcf);
      sheet.addCell(serialNumber60);
      Label serialNumber61 = new Label(6,rows+1,"金额（元）",wcf);
      sheet.addCell(serialNumber61);
      Label serialNumber62 = new Label(7,rows+1,"备注",wcf);
      sheet.addCell(serialNumber62);
      rows+=1;
    //工时费
    int j = 0;
    double serviceTotle = 0.00;
    if (repairOrderDTO.getServiceDTOs()!=null&&repairOrderDTO.getServiceDTOs().length>0){
      for( ;j<repairOrderDTO.getServiceDTOs().length;j++){
        sheet.mergeCells(1, rows+1+j, 3, rows+1+j);
        sheet.mergeCells(7, rows+1+j, 8, rows+1+j);
        Label O_j = new Label(0,rows+1+j,String.valueOf(j+1),wcf);
        Label a_j = new Label(1,rows+1+j,repairOrderDTO.getServiceDTOs()[j].getService()==null?"":repairOrderDTO.getServiceDTOs()[j].getService(),wcf);
        Label b_j = new Label(4,rows+1+j,repairOrderDTO.getServiceDTOs()[j].getActualHours()==null?"":repairOrderDTO.getServiceDTOs()[j].getActualHours().toString(),wcf);
        Label c_j = new Label(5,rows+1+j,repairOrderDTO.getServiceDTOs()[j].getStandardUnitPrice()==null?"":repairOrderDTO.getServiceDTOs()[j].getStandardUnitPrice().toString(),wcf);
        Label d_j = new Label(6,rows+1+j,repairOrderDTO.getServiceDTOs()[j].getTotal()==null?"":repairOrderDTO.getServiceDTOs()[j].getTotal().toString(),wcf);
        Label e_j = new Label(7,rows+1+j,repairOrderDTO.getServiceDTOs()[j].getMemo()==null?"":repairOrderDTO.getServiceDTOs()[j].getMemo(),wcf);
        sheet.addCell(O_j);
        sheet.addCell(a_j);
        sheet.addCell(b_j);
        sheet.addCell(c_j);
        sheet.addCell(d_j);
        sheet.addCell(e_j);
        serviceTotle+= repairOrderDTO.getServiceDTOs()[j].getTotal()==null?0:repairOrderDTO.getServiceDTOs()[j].getTotal();
      }

      rows+=j;
    }else{
      sheet.mergeCells(1, rows+1, 3, rows+1);
      sheet.mergeCells(7, rows+1, 8, rows+1);
      sheet.mergeCells(1, rows+2, 3, rows+2);
      sheet.mergeCells(7, rows+2, 8, rows+2);
      Label empty2 = new Label(0,rows+1,"",wcf);
      sheet.addCell(empty2);
      Label empty2_1 = new Label(1,rows+1,"",wcf);
      sheet.addCell(empty2_1);
      Label empty2_2 = new Label(4,rows+1,"",wcf);
      sheet.addCell(empty2_2);
      Label empty2_3 = new Label(5,rows+1,"",wcf);
      sheet.addCell(empty2_3);
      Label empty2_4 = new Label(6,rows+1,"",wcf);
      sheet.addCell(empty2_4);
      Label empty2_5 = new Label(7,rows+1,"",wcf);
      sheet.addCell(empty2_5);
      //
      Label empty3 = new Label(0,rows+2,"",wcf);
      sheet.addCell(empty3);
      Label empty3_1 = new Label(1,rows+2,"",wcf);
      sheet.addCell(empty3_1);
      Label empty3_2 = new Label(4,rows+2,"",wcf);
      sheet.addCell(empty3_2);
      Label empty3_3 = new Label(5,rows+2,"",wcf);
      sheet.addCell(empty3_3);
      Label empty3_4 = new Label(6,rows+2,"",wcf);
      sheet.addCell(empty3_4);
      Label empty3_5 = new Label(7,rows+2,"",wcf);
      sheet.addCell(empty3_5);
       rows+=2;
    }



    sheet.mergeCells(0, rows+1, 8, rows+1);
    Label serialNumber81 = new Label(0,rows+1,"工时费合计金额（元）："+serviceTotle+"",wcf);
    sheet.addCell(serialNumber81);
    rows+=1;
    //
    sheet.mergeCells(0, rows+1, 8, rows+1);
    Label serialNumber82 = new Label(0,rows+1,"表六 其它费用",wcf);
    sheet.addCell(serialNumber82);
    rows+=1;
    //
    sheet.mergeCells(1, rows+1, 3, rows+1);
    sheet.mergeCells(4, rows+1, 6, rows+1);
    sheet.mergeCells(7, rows+1, 8, rows+1);

    Label serialNumber83 = new Label(0,rows+1,"序号",wcf);
    sheet.addCell(serialNumber83);
    Label serialNumber84 = new Label(1,rows+1,"费用项目",wcf);
    sheet.addCell(serialNumber84);
    Label serialNumber85 = new Label(4,rows+1,"金额（元）",wcf);
    sheet.addCell(serialNumber85);
    Label serialNumber86 = new Label(7,rows+1,"备注",wcf);
    sheet.addCell(serialNumber86);
    rows+=1;
    //
    //其他费用
    int k = 0;
    double otherTotle =0.00;
    if (repairOrderDTO.getOtherIncomeItemDTOList()!=null&&repairOrderDTO.getOtherIncomeItemDTOList().size()>0){
      for( ;k<repairOrderDTO.getOtherIncomeItemDTOList().size();k++){
        sheet.mergeCells(1, rows+1+k, 3, rows+1+k);
        sheet.mergeCells(4, rows+1+k, 6, rows+1+k);
        sheet.mergeCells(7, rows+1+k, 8, rows+1+k);
        Label O_j = new Label(0, rows+1+k,String.valueOf(k+1),wcf);
        Label a_j = new Label(1, rows+1+k,repairOrderDTO.getOtherIncomeItemDTOList().get(k).getName()==null?"":repairOrderDTO.getOtherIncomeItemDTOList().get(k).getName(),wcf);
        Label b_j = new Label(4, rows+1+k,repairOrderDTO.getOtherIncomeItemDTOList().get(k).getPrice()==null?"":repairOrderDTO.getOtherIncomeItemDTOList().get(k).getPrice().toString(),wcf);
        Label c_j = new Label(7, rows+1+k,repairOrderDTO.getOtherIncomeItemDTOList().get(k).getMemo()==null?"":repairOrderDTO.getOtherIncomeItemDTOList().get(k).getMemo(),wcf);
        sheet.addCell(O_j);
        sheet.addCell(a_j);
        sheet.addCell(b_j);
        sheet.addCell(c_j);
        otherTotle+= repairOrderDTO.getOtherIncomeItemDTOList().get(k).getPrice()==null?0:repairOrderDTO.getOtherIncomeItemDTOList().get(k).getPrice();
      }

      rows+=j;
    }else{
      sheet.mergeCells(1, rows+1, 3, rows+1);
      sheet.mergeCells(1, rows+2, 3, rows+2);
      sheet.mergeCells(4, rows+1, 6, rows+1);
      sheet.mergeCells(4, rows+2, 6, rows+2);
      sheet.mergeCells(7, rows+1, 8, rows+1);
      sheet.mergeCells(7, rows+2, 8, rows+2);
      Label empty4 = new Label(0,rows+1,"",wcf);
      sheet.addCell(empty4);
      Label empty4_1 = new Label(1,rows+1,"",wcf);
      sheet.addCell(empty4_1);
      Label empty4_2 = new Label(4,rows+1,"",wcf);
      sheet.addCell(empty4_2);
      Label empty4_3 = new Label(7,rows+1,"",wcf);
      sheet.addCell(empty4_3);
      //
      Label empty5 = new Label(0,rows+2,"",wcf);
      sheet.addCell(empty5);
      Label empty5_1 = new Label(1,rows+2,"",wcf);
      sheet.addCell(empty5_1);
      Label empty5_2 = new Label(4,rows+2,"",wcf);
      sheet.addCell(empty5_2);
      Label empty5_3 = new Label(7,rows+2,"",wcf);
      sheet.addCell(empty5_3);
      rows+=2;
    }

    //
    sheet.mergeCells(0, rows+1, 8, rows+1);
    Label serialNumber89 = new Label(0,rows+1,"其它费用合计金额（元）："+otherTotle+"",wcf);
    sheet.addCell(serialNumber89);
    rows+=1;
    //
    sheet.mergeCells(0, rows+1, 8, rows+1);
    sheet.mergeCells(0, rows+2, 8, rows+2);
    sheet.mergeCells(0, rows+3, 8, rows+3);
    Label serialNumber90 = new Label(0,rows+1,"1、是否有托修人支付费用更换的旧配件：",wcf);
    sheet.addCell(serialNumber90);
    Label serialNumber91 = new Label(0,rows+2, "口旧配件已确认，并由托修方回收  口旧配件已确认，托修方声明放弃  口无旧配件",wcf);
    sheet.addCell(serialNumber91);
    Label serialNumber92 = new Label(0,rows+3,"2、结算清单项目及应付金额经双方核实，客户签字后生效",wcf);
    sheet.addCell(serialNumber92);
    rows+=3;
    //
    sheet.mergeCells(0, rows+1, 4, rows+1);
    sheet.mergeCells(5, rows+1, 8, rows+1);
    Label serialNumber93 = new Label(0,rows+3,"客户签字：");
    sheet.addCell(serialNumber93);
    Label serialNumber94 = new Label(5,rows+3,"结算员签章：");
    sheet.addCell(serialNumber94);
    rows+=3;
    //
    sheet.mergeCells(0, 77, 4, rows+2);
    sheet.mergeCells(5, 77, 8, rows+2);
    Label serialNumber95 = new Label(0,rows+2,"结算日期：");
    sheet.addCell(serialNumber95);
    Label serialNumber96 = new Label(5,rows+2,"承修方（盖章）");
    sheet.addCell(serialNumber96);
    rows+=2;

    Label empty24 = new Label(4,20,"",wcf);
    sheet.addCell(empty24);
    Label empty25 = new Label(4,21,"",wcf);
    sheet.addCell(empty25);
    Label empty26 = new Label(4,25,"",wcf);
    sheet.addCell(empty26);
    Label empty27 = new Label(4,26,"",wcf);
    sheet.addCell(empty27);
    Label empty28 = new Label(6,10,"",wcf);
    sheet.addCell(empty28);
    Label empty29 = new Label(6,11,"",wcf);
    sheet.addCell(empty29);
    Label empty30 = new Label(6,12,"",wcf);
    sheet.addCell(empty30);
    Label empty31 = new Label(6,13,"",wcf);
    sheet.addCell(empty31);
    Label empty32 = new Label(6,14,"",wcf);
    sheet.addCell(empty32);
    Label empty33 = new Label(6,15,"",wcf);
    sheet.addCell(empty33);
    Label empty34 = new Label(6,16,"",wcf);
    sheet.addCell(empty34);
    Label empty35 = new Label(6,20,"",wcf);
    sheet.addCell(empty35);
    Label empty36 = new Label(6,21,"",wcf);
    sheet.addCell(empty36);
    Label empty37 = new Label(6,25,"",wcf);
    sheet.addCell(empty37);
    Label empty38 = new Label(6,26,"",wcf);
    sheet.addCell(empty38);

    //把创建的内容写入到输出流中，并关闭输出流
    workbook.write();
    workbook.close();
  }

}


