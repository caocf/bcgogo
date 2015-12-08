package com.bcgogo.search.client;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.ShopConstant;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

import java.net.MalformedURLException;

/**
 * @author xzhu
 */
public class BcgogoHttpSolrClient extends AbstractSolrClient {

  public BcgogoHttpSolrClient(String core) throws MalformedURLException {
    String url = null;
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    url = configService.getConfig("SOLR_URL", -1L);
    url = url + "/" + core;
    BcgogoHttpSolrServer httpSolrServer = new BcgogoHttpSolrServer(url);
    String config = configService.getConfig("SOLR_SO_TIMEOUT", ShopConstant.BC_SHOP_ID);
    int timeout = 10000;
    if (config != null) {
      timeout = Integer.valueOf(config);
    }
    httpSolrServer.setSoTimeout(timeout);
    httpSolrServer.setConnectionTimeout(1000);
    httpSolrServer.setDefaultMaxConnectionsPerHost(100);
    httpSolrServer.setMaxTotalConnections(100);
    httpSolrServer.setFollowRedirects(false);  // defaults to false
    // allowCompression defaults to false.
    // Server side must support gzip or deflate for this to have any effect.
    httpSolrServer.setAllowCompression(true);
    httpSolrServer.setMaxRetries(1);
    server = httpSolrServer;
  }

  public class BcgogoHttpSolrServer extends HttpSolrServer {

    public BcgogoHttpSolrServer(String solrServerUrl) throws MalformedURLException {
      super(solrServerUrl);

    }
  }
}
