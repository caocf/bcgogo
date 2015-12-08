package com.bcgogo.config.service;

import com.bcgogo.config.model.BaseStation;
import com.bcgogo.config.model.ConfigDaoManager;
import com.bcgogo.config.model.ConfigReader;
import com.bcgogo.config.model.ConfigWriter;
import com.bcgogo.config.util.BaseStationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Administrator on 2015/10/24.
 */
@Service
@Transactional
public class BaseStationService implements IBaseStationService{

    final static Logger LOG = LoggerFactory.getLogger(BaseStationService.class);

    @Autowired
    private ConfigDaoManager configDaoManager;

    @Override
    public BaseStation findStationByMncAndLacAndCi(Map map) throws IOException {

        BaseStation baseStation = new BaseStation();
        ConfigWriter writer = configDaoManager.getWriter();
        Object status = writer.begin();
        try{
            ConfigReader reader = configDaoManager.getReader();
            if (reader.findStationByMncAndLacAndCi(map) != null){
                baseStation = reader.findStationByMncAndLacAndCi(map);
            }else {
                String stationStr = BaseStationUtils.selectBaseStation(map);
                ObjectMapper objectMapper = new ObjectMapper();
                Map stationMap = objectMapper.readValue(stationStr,Map.class);
                if (stationMap.get("errcode").equals(0)) {
                    baseStation.setMcc((Integer) map.get("mcc"));
                    baseStation.setMnc((Integer) map.get("mnc"));
                    baseStation.setLac((Integer) map.get("lac"));
                    baseStation.setCi((Integer) map.get("ci"));
                    baseStation.setLat(Double.parseDouble((String) stationMap.get("lat")) );
                    baseStation.setLon(Double.parseDouble((String) stationMap.get("lon")));
                    baseStation.setRadius(Integer.parseInt((String) stationMap.get("radius")));
                    baseStation.setAddress((String) stationMap.get("address"));

                    writer.save(baseStation);
                    writer.commit(status);
                }
            }
            return baseStation;
        }catch(Exception e){
            LOG.error(e.getMessage());
            String stationStr = BaseStationUtils.selectBaseStation2(map);
            ObjectMapper objectMapper = new ObjectMapper();
            Map stationMap = objectMapper.readValue(stationStr,Map.class);
            if( stationStr != null && !stationMap.get("code").equals("0")){
                baseStation = null;
            }else {
                baseStation.setMcc((Integer) map.get("mcc"));
                baseStation.setMnc((Integer) map.get("mnc"));
                baseStation.setLac((Integer) map.get("lac"));
                baseStation.setCi((Integer) map.get("ci"));
                baseStation.setLat((Double) stationMap.get("lat"));
                baseStation.setLon((Double) stationMap.get("lon"));
                baseStation.setRadius((Integer) stationMap.get("radius"));
                baseStation.setAddress((String) stationMap.get("address"));

                writer.save(baseStation);
                writer.commit(status);
            }
            return baseStation;
        }finally {
            writer.rollback(status);
        }
    }
}
