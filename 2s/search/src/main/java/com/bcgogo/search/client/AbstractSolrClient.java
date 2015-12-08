package com.bcgogo.search.client;

import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * @author xzhu  07/08/2012
 */
public abstract class AbstractSolrClient implements SolrClient {
  private Logger logger = LoggerFactory.getLogger(AbstractSolrClient.class);

  private boolean waitFlush = true;
  private boolean waitSearch = true;
  private boolean softCommit = true;

  protected SolrServer server;

  @Override
  public void solrCommit() {
    try {
      server.commit(waitFlush, waitSearch, softCommit);
    } catch (SolrServerException e) {
      logger.error("Solr will rollback because of [{}]", e.getMessage());
      try {
        server.rollback();
      } catch (Exception e1) {
      }
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void solrOptimize(){
    try {
      server.optimize(waitFlush, waitSearch);
    } catch (SolrServerException e) {
      logger.error("Solr optimize error because of [{}]", e.getMessage());
    } catch (IOException e) {
      logger.error("Solr optimize error because of [{}]", e.getMessage());
    }
  }

  @Override
  public void addDoc(SolrInputDocument doc) throws Exception {
    server.add(doc);
    solrCommit();
  }

  @Override
  public void addDocs(Collection<SolrInputDocument> docs) throws Exception {
    if(CollectionUtils.isNotEmpty(docs)){
      server.add(docs);
      solrCommit();
    }else{
      logger.warn("docs is null");
    }
  }


  @Override
  public void deleteById(String id) throws Exception {
    server.deleteById(id);
    solrCommit();
  }

  @Override
  public void deleteByIds(List<String> ids) throws Exception {
    server.deleteById(ids);
    solrCommit();
  }

  @Override
  public void deleteByQuery(String queryStr) throws Exception {
    server.deleteByQuery(queryStr);
    solrCommit();
  }

  @Override
  public void deleteAll() throws Exception {
    deleteByQuery("*:*");
  }

  @Override
  public QueryResponse queryById(String... id) throws Exception {
    if (id == null) return null;
    StringBuffer sb = new StringBuffer();
    sb.append("id:(");
    for (int i = 0; i < id.length; i++) {
      sb.append(id[i]);
      if (i < id.length - 1) {
        sb.append(" ");
      }
    }
    sb.append(") ");
    return query(new SolrQuery(sb.toString()).setParam("q.op","OR"));
  }

  @Override
  public QueryResponse query(SolrQuery query) {
    resetQueryRows(query);
    try {
      return server.query(query, SolrRequest.METHOD.POST);
    } catch (SolrServerException e) {
      throw new RuntimeException(e);
    }
  }

  private void resetQueryRows(SolrQuery query) {
    if (query != null && query.getRows() == null) {
      query.setRows(Integer.MAX_VALUE);
    }
  }
}
