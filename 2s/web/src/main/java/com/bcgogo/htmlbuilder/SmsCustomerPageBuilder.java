package com.bcgogo.htmlbuilder;

import com.bcgogo.common.Pager;
import com.bcgogo.user.dto.CustomerRecordDTO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-3-20
 * Time: 下午3:22
 * To change this template use File | Settings | File Templates.
 */
@Component
public class SmsCustomerPageBuilder {

    public String build(List<CustomerRecordDTO> customerRecordDTOList, Pager pager){
        StringBuilder stringBuilder = new StringBuilder();
        if (customerRecordDTOList != null && !customerRecordDTOList.isEmpty() && pager != null) {
            stringBuilder.append("<table class=\"cus_table clear\" id=\"chk_show\" cellpadding=\"0\" cellspacing=\"0\">");
            stringBuilder.append("<colgroup>");
            stringBuilder.append("<col width=\"26px\" />");
            stringBuilder.append("<col width=\"40px\" style=\"width:40px\\9;\" />");
            stringBuilder.append("<col width=\"80px\" style=\"width:80px\\9;\" />");
            stringBuilder.append("<col width=\"53px\" style=\"width:53px\\9;\" />");
            stringBuilder.append("<col width=\"100px\" style=\"width:100px\\9;\" />");
            stringBuilder.append("<col width=\"88px\" style=\"width:88px\\9;\" />");
            stringBuilder.append("<col width=\"48px\" style=\"width:48px\\9;\" />");
            stringBuilder.append("<colgroup/><input type=\"hidden\" id=\"pageNo\" value=\"" + pager.getCurrentPage() + "\"/>");
            for (int m = 0; m < customerRecordDTOList.size(); m++) {
                String whiteSpace="&nbsp;";

                CustomerRecordDTO customerRecordDTO = customerRecordDTOList.get(m);
                 String nameStr=customerRecordDTO.getName()==null?whiteSpace:customerRecordDTO.getName();
                 String name =customerRecordDTO.getName()==null?whiteSpace:customerRecordDTO.getName();
                 if(name.length()>8){
                   name = name.substring(0,7);
                 }
                if (m % 2 == 0) {
                    stringBuilder.append("<tr>");
                } else {
                    stringBuilder.append("<tr class=\"cus_tableBg cor\">");
                }
                stringBuilder.append("<td><img src=\"/web/images/check_off.jpg\" alt=\"\" class=\"noclass\" id=" + (customerRecordDTO.getCustomerIdString()==null?whiteSpace:customerRecordDTO.getCustomerIdString()) + " name=\"" + (customerRecordDTO.getMobile()==null?whiteSpace:customerRecordDTO.getMobile()) + "\" onclick=\"clickSingleCustomer(this)\"/></td>");
                stringBuilder.append("<td>" + (m + 1) + "</td>");
                stringBuilder.append("<td>" + (customerRecordDTO.getLicenceNo()==null?whiteSpace:customerRecordDTO.getLicenceNo())+ "</td>");
                stringBuilder.append("<td title=\""+nameStr+"\">" + name + "</td>");
                stringBuilder.append("<td>" + (customerRecordDTO.getMobile()==null?whiteSpace:customerRecordDTO.getMobile())+ "</td>");
                stringBuilder.append("<td>" + (customerRecordDTO.getCarDateStr()==null?whiteSpace:customerRecordDTO.getCarDateStr())+ "</td>");
                stringBuilder.append("<td>" + (customerRecordDTO.getBirthdayStr()==null?whiteSpace:customerRecordDTO.getBirthdayStr()) + "</td>");
                stringBuilder.append("<input type=\"hidden\" id=\"phone" + (m) + "\" class=\"noclass\" value='" + customerRecordDTO.getMobile() + "'/>");
                stringBuilder.append("<td></td>");
                stringBuilder.append("</tr>");
            }
            stringBuilder.append("<input type=\"hidden\" id=\"totalPage\" name=\"\" value=\"" + pager.getTotalPage() + "\" />");
            stringBuilder.append("</table>");
        }
        return stringBuilder.toString();
    }
}
