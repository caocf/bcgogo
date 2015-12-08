package com.bcgogo.config.service.UMPush.UMPushConstant;

/**
 * Created by XinyuQiu on 14-4-29.
 */
public enum UMPushType {

  //      unicast-单播,
  //      filecast-文件播(多个device_token可以通过文件形式批量发送）
  //      broadcast-广播,
  //      groupcast-组播(按照filter条件筛选特定用户群, 具体请参照filter参数)
  //      customizedcast(通过开发者定义的alias和友盟的device_tokens进行映射,
  //        可以传入单个alias, 也可以传入文件id。具体请参照alias和file_id参数)
  unicast,broadcast,groupcast,customizedcast,filecast
}
