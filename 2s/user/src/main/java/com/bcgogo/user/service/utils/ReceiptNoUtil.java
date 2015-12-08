package com.bcgogo.user.service.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/11/30
 * Time: 9:16.
 */
public class ReceiptNoUtil {

    /** 年月日(无下划线) yyyyMMdd */
    public static final String dtShort = "yyyyMMdd";

    private static final Logger LOG = LoggerFactory.getLogger(ReceiptNoUtil.class);

    public static String createReceiptNo() {

        Date date=new Date();
        DateFormat df=new SimpleDateFormat(dtShort);
        StringBuilder receiptNo = new StringBuilder();
        receiptNo.append(df.format(date));
        String str = "" + System.currentTimeMillis() / 100;
        str = str.substring(3, str.length());
        String temp = str.substring(0, 4), temp2 = str.substring(4, str.length());
        StringBuffer bf = new StringBuffer();
        bf.append(temp);
        Random random = new Random();
        int num = random.nextInt(9), numLocate = random.nextInt(temp.length());
        bf.insert(numLocate, num + "");
        int num1 = random.nextInt(9), num1Locat = random.nextInt(temp2.length()),
                num2 = random.nextInt(9), num2Locat = random.nextInt(temp2.length());
        StringBuffer bf2 = new StringBuffer();
        bf2.append(temp2);
        bf2.insert(num1Locat, num1 + "");
        bf2.insert(num2Locat, num2 + "");
        int locate = random.nextInt(bf2.length());
        bf2.insert(locate, bf);
        receiptNo.append(bf2);
        return receiptNo.toString();
    }
}
