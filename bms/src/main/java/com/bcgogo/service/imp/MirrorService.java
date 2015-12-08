package com.bcgogo.service.imp;

import com.bcgogo.service.IMirrorService;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-6-3
 * Time: 15:36
 */
@Service
public class MirrorService implements IMirrorService{

//   @Override
// public Result validateAndUpdateMirrorLoginInfo(String vSessionId){
//     RmiProxyFactoryBean rmiProxyFactoryBean = new RmiProxyFactoryBean();
//          rmiProxyFactoryBean.setServiceUrl("rmi://127.0.0.1:19110/bcgogoRmiServer");
//          rmiProxyFactoryBean.setServiceInterface(IBcgogoRmiServer.class);
//          try {
//            String id = "356824200008819";
//            rmiProxyFactoryBean.afterPropertiesSet();
//            IBcgogoRmiServer rmiServer = (IBcgogoRmiServer) rmiProxyFactoryBean.getObject();
//            Result result = rmiServer.sendMsg(id, "vib0,3");
//            System.out.println(result);
//          } catch (Exception e) {
//            System.out.print(e.getMessage());
//          }
//  }
}
