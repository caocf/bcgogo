package com.bcgogo.constant;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-6
 * Time: 下午6:04
 */
public enum MemcachePrefix {
  shopResource("shop_resource_"),
  resource("resource_"),
  user("user_"),
  userGroupUser("user_group_user_"),
  userGroup("user_group_"),
  userGroupResources("user_group_resources_"),
  userGroupRole("user_group_role_"),
  module("module_"),
  role("role_"),
  roleResource("role_resource_"),
  config("config_"),
  currentUsed("current_used_"),
  inventoryLimit("inventory_limit_"),
  inventorySum("inventory_sum_"),
  shopConfig("shop_config_"),
  shopConfigSyncTime("shop_config_syncTime_"),
  messageSwitch("message_switch"),
  concurrentLock("concurrent_lock_"),
  productInventory("product_inventory_"),
	entityOnLoadFlag("entity_onLoad_flag"),
	sensitiveWords("bcgogo_sensitive_words"),
	productDelete("product_delete_"),
  sysReminder("sys_reminder_"),
	sensitiveWordsFlag("bcgogo_sensitive_words_flag"),
  todoRemind("toto_remind_"),
  receiverMessage("receiver_message_"),
  userGuide("user_guide_"),
  userGuideFlows("user_guide_flows"),
  systemUrlMonitorStat("system_url_monitor_stat_"),
  shoppingCartProductNumber("shopping_cart_product_number_"),
  pageCustomizerConfig("page_customizer_config_"),
  solrMatchStopWord("solr_match_stop_word_"),
  apiSession("api_session_"),
  xApiSession("x_api_session_"),
  allActiveShops("all_active_shops"),
  areaRefreshFlag("area_refresh_flag"),
  ;

  private String value;

  MemcachePrefix(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

//  public static void main(String[] args) {
//    System.out.println(MemcachePrefix.currentUsed);
//  }

}
