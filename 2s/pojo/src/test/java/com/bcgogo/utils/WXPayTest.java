package com.bcgogo.utils;
import com.bcgogo.appPay.wxPay.utils.WXPaySignUtil;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/11/29
 * Time: 17:55.
 */
public class WXPayTest {
    public static void main(String[] args){
        try{
            // Configure and open a connection to the site you will send the request
            URL url = new URL("https://api.mch.weixin.qq.com/pay/unifiedorder");
            URLConnection urlConnection = url.openConnection();
            // 设置doOutput属性为true表示将使用此urlConnection写入数据
            urlConnection.setDoOutput(true);
            // 定义待写入数据的内容类型，我们设置为application/x-www-form-urlencoded类型
            urlConnection.setRequestProperty("content-type", "application/xml");
            // 得到请求的输出流对象
            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            // 把数据写入请求的Body
            String test = WXPaySignUtil.postXML("1", "heh", "2015102734086092860");
            System.out.print(test);
            out.write(test);
            out.flush();
            out.close();

            // 从服务器读取响应
            InputStream inputStream = urlConnection.getInputStream();
            String encoding = urlConnection.getContentEncoding();
            String body = IOUtils.toString(inputStream, encoding);
            try {
                Map map = XMLParser.parseXml(body);
                System.out.print(map);
            }catch(Exception e){
                System.out.print(e.getMessage());
            }
        }catch(IOException e){
            System.out.print(e.getMessage());
        }
    }

}
