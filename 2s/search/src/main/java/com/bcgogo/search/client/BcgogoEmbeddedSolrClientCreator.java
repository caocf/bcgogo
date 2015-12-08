package com.bcgogo.search.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Caiwei
 * Date: 9/26/11
 * Time: 12:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class BcgogoEmbeddedSolrClientCreator {
  private final static Map<String, SolrClient> _clients = new ConcurrentHashMap<String, SolrClient>();

  public static SolrClient getBcgogoSolrClient(String core) throws Exception {
    SolrClient client = _clients.get(core);
    if (client != null) {
      return client;
    }
    synchronized (_clients) {
      client = new BcgogoEmbeddedSolrClient(core);
      _clients.put(core, client);
      return client;
    }
  }

  public static void close() {
    for (SolrClient client : _clients.values()) {
      BcgogoEmbeddedSolrClient c = (BcgogoEmbeddedSolrClient) client;
      c.shutdown();
    }
    _clients.clear();
  }
}
