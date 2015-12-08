package com.bcgogo.config.service;

import com.bcgogo.config.model.BaseStation;
import com.fasterxml.jackson.core.JsonParseException;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Administrator on 2015/10/24.
 */
public interface IBaseStationService {

    public BaseStation findStationByMncAndLacAndCi(Map map) throws IOException;
}
