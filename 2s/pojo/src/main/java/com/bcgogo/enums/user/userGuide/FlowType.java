//package com.bcgogo.enums.user.userGuide;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created with IntelliJ IDEA.
// * User: ZhangJuntao
// * Date: 13-2-28
// * Time: 下午2:12
// */
//public enum FlowType {
//  REPAIR_FLOW_GROUP, // 汽修
//  TXN_FLOW_GROUP;   // 汽配
//  private static Map<Long, FlowType> lookup = new HashMap<Long, FlowType>();
//
//  static {
//    //每个shop version的id
//    lookup.put(10000010017531653L, REPAIR_FLOW_GROUP);
//    lookup.put(10000010017531654L, REPAIR_FLOW_GROUP);
//    lookup.put(10000010017531655L, REPAIR_FLOW_GROUP);
//    lookup.put(10000010017531656L, REPAIR_FLOW_GROUP);
//    lookup.put(10000010017531657L, TXN_FLOW_GROUP);
//    lookup.put(10000010037193619L, TXN_FLOW_GROUP);
//    lookup.put(10000010039823882L, REPAIR_FLOW_GROUP);
//    lookup.put(10000010037193620L, TXN_FLOW_GROUP);
//  }
//
//  public static FlowType shopVersionFlowTypeMap(long shopVersionId) {
//    return lookup.get(shopVersionId);
//  }
//}
