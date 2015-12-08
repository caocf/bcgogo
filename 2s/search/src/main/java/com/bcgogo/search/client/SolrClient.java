package com.bcgogo.search.client;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;

import java.util.Collection;
import java.util.List;

/**
 * @author xzhu  07/08/2012
 */
public interface SolrClient {

  void addDoc(SolrInputDocument doc) throws Exception;

  void addDocs(Collection<SolrInputDocument> docs) throws Exception;


  void deleteById(String id) throws Exception;

  void deleteByIds(List<String> ids) throws Exception;

  void deleteByQuery(String queryStr) throws Exception;

  void deleteAll() throws Exception;

  QueryResponse queryById(String... id) throws Exception;

  QueryResponse query(SolrQuery query) throws Exception;

  void solrCommit() throws Exception;

  void solrOptimize();
}