package com.bcgogo.search.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Caiwei
 * Date: 10/2/11
 * Time: 9:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class BcgogoHttpSolrClientCreator {
  private final static Map<String, SolrClient> _clients = new ConcurrentHashMap<String, SolrClient>();

  public static SolrClient getBcgogoSolrClient(String core) throws Exception {
    SolrClient client = _clients.get(core);
    if (client != null) {
      return client;
    }
    synchronized (_clients) {
      client = new BcgogoHttpSolrClient(core);
      _clients.put(core, client);
      return client;
    }
  }

  public static void close() {
    _clients.clear();
  }
}
