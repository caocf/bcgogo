package com.bcgogo.search.client;

import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CloseHook;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrCore;
import org.apache.solr.servlet.SolrRequestParsers;

import java.io.File;
import java.util.Collection;

/**
 * @author xzhu
 */
public class BcgogoEmbeddedSolrClient extends AbstractSolrClient {

  public BcgogoEmbeddedSolrClient(String core) throws Exception {
    String solrHome = System.getProperty("solr.solr.home");
    if (solrHome == null) {
      throw new IllegalArgumentException("solr.solr.home is not set");
    }
    File home = new File(solrHome);
    File f = new File(home, "solr.xml");
    CoreContainer.Initializer initializer = new CoreContainer.Initializer();
    CoreContainer container = initializer.initialize();
    container.load(home.getPath(), f);

    for (SolrCore cor : container.getCores()) {
      cor.addCloseHook(new OnClose());
    }
    server = new BcgogoEmbeddedSolrServer(container, core);
  }

  void shutdown() {
    BcgogoEmbeddedSolrServer s = (BcgogoEmbeddedSolrServer) server;
    s.shutdown();
  }

  public static class OnClose extends CloseHook {
    @Override
    public void preClose(SolrCore core) {

    }

    @Override
    public void postClose(SolrCore core) {

    }
  }

  public class BcgogoEmbeddedSolrServer extends EmbeddedSolrServer {

    public BcgogoEmbeddedSolrServer(CoreContainer coreContainer, String coreName) {
      super(coreContainer, coreName);
      _parser = new SolrRequestParsers(null);
    }

    public void shutdown() {
      coreContainer.shutdown();
    }

    Collection<String> getNames() {
      return coreContainer.getCoreNames();
    }

    private transient final SolrRequestParsers _parser;
  }

}
