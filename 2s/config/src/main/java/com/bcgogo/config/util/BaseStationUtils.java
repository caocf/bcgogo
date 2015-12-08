package com.bcgogo.config.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Administrator on 2015/10/23.
 */
public class BaseStationUtils {
    private static final Logger LOG = LoggerFactory.getLogger(BaseStationUtils.class);

    /**
     *
     * @param mcc 国家代码：中国代码 460
     * @param mnc 网络类型：0移动，1联通(电信对应sid)，十进制
     * @param lac (电信对应nid)，十进制
     * @param ci (电信对应bid)，十进制
     * @param coord 坐标类型(wgs84/gcj02/bd09)，默认wgs84
     * @param output 返回格式(csv/json/xml)，默认csv
     * @return
     */
    public static String selectBaseStation(Map map){

        String url ,strResult = null;
        if (map.get("mnc").equals(3)){
            url = "http://www.cellocation.com/cell/?mcc="+map.get("mcc")+"&sid="+map.get("mnc")+"&nid="+map.get("lac")+"&bid="+map.get("ci")+"&output=json";

        }else{
            url = "http://www.cellocation.com/cell/?mcc="+map.get("mcc")+"&mnc="+map.get("mnc")+"&lac="+map.get("lac")+"&ci="+map.get("ci")+"&output=json";
        }
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        try {
//            int i=1/0;
            HttpResponse response = client.execute(request);
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                strResult = EntityUtils.toString(response.getEntity());
            }else {
                LOG.info("url地址错误");
            }
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return strResult;
    }

    public static String selectBaseStation2(Map map)  {
        String url , strResult = null;
        ObjectMapper objectMapper = new ObjectMapper();
        Map stationMap = new HashMap();
        if (map.get("mnc").equals(3)){
            url = "http://apis.baidu.com/apix/apix_station_data/apix_mobi?sid="+map.get("mnc")+"&nid="+map.get("lac")+"&cellid="+map.get("ci")+"&ishex=0";

        }else{
            url = "http://apis.baidu.com/apix/apix_station_data/apix_mobi?mnc="+map.get("mnc")+"&lac="+map.get("lac")+"&cellid="+map.get("ci")+"&ishex=0";
        }
        LOG.info("-------"+url);
        DefaultHttpClient client = new DefaultHttpClient();
        Header header = new BasicHeader("apikey","6b11e8ea564909c493d49eea614a5be7");
        HttpGet request = new HttpGet(url);
        request.addHeader(header);
        try {
            HttpResponse response = client.execute(request);
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                strResult = EntityUtils.toString(response.getEntity());
                stationMap = objectMapper.readValue(strResult,Map.class);
                if(stationMap.get("code").equals("0")){
                    strResult = strResult.substring(1,strResult.length()-1);
                    strResult = strResult.substring(strResult.indexOf("{")-1,strResult.indexOf("}")+1);
                    stationMap = objectMapper.readValue(strResult,Map.class);
                    stationMap.remove("city");
                    stationMap.remove("dist");
                    stationMap.remove("prov");
                    stationMap.remove("number");
                    stationMap.remove("town");
                    stationMap.remove("street");
                    stationMap.put("radius",stationMap.get("acc"));
                    stationMap.remove("acc");
                    stationMap.put("address",stationMap.get("addr"));
                    stationMap.remove("addr");
                    stationMap.put("code","0");
                    strResult = objectMapper.writeValueAsString(stationMap);
                }
            }else {
                LOG.info("url地址错误");
            }
        }catch (Exception e){
            LOG.error(e.getMessage());
        }
        return strResult;
    }

//    public static void main(String args[]) throws IOException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        String str;
//        str = selectBaseStation2().substring(1,selectBaseStation2().length()-1);
//        str = str.substring(str.indexOf("{")-1,str.indexOf("}")+1);
//        Map stationMap = objectMapper.readValue(str,Map.class);
//        stationMap.remove("city");
//        stationMap.remove("dist");
//        stationMap.remove("prov");
//        stationMap.remove("number");
//        stationMap.remove("town");
//        stationMap.remove("street");
//        stationMap.put("radius",stationMap.get("acc"));
//        stationMap.remove("acc");
//        stationMap.put("address",stationMap.get("addr"));
//        stationMap.remove("addr");
//        LOG.info(objectMapper.writeValueAsString(stationMap));
//    }

}
