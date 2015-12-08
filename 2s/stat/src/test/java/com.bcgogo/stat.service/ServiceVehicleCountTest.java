package com.bcgogo.stat.service;

import com.bcgogo.AbstractTest;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.ServiceVehicleCountDTO;
import com.bcgogo.stat.model.ServiceVehicleCount;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: 001
 * Date: 12-2-10
 * Time: 下午12:44
 * To change this template use File | Settings | File Templates.
 */
public class ServiceVehicleCountTest extends AbstractTest {
    IServiceVehicleCountService isvcs= ServiceManager.getService(IServiceVehicleCountService.class);
     @Test
    public void testGetServiceVehicleCountByTime() {
           List<ServiceVehicleCount> count=isvcs.getServiceVehicleCountByTime(10000010001000000l, 1328803200484l);
           assertEquals(count, null);
     }
    @Test
    public void testSaveServiceVehicleCount()
    {
        ServiceVehicleCountDTO svcDTO=new ServiceVehicleCountDTO(10000010001000000l,2l,1328803200484l);
        isvcs.saveServiceVehicleCount(svcDTO);

    }
}
