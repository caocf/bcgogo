package com.bcgogo.search;

import com.bcgogo.search.client.SolrClientHelper;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 * To change this template use File | Settings | File Templates.
 */
public class SolrClientTest extends AbstractTest {

  @Before
  public void setUpTest() throws Exception {
  }

  @Test
  public void solrClientTest() throws Exception {


    SolrInputDocument doc = new SolrInputDocument();
    doc.addField("id", "1", 1);
    doc.addField("product_name", "机油滤清器");
    doc.addField("shop_id", 0);
    SolrClientHelper.getProductSolrClient().addDoc(doc);
    QueryResponse res = SolrClientHelper.getProductSolrClient().queryById("1", "2", "3");
    assertEquals(1, res.getResults().size());


    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
    doc = new SolrInputDocument();
    doc.addField("id", "2", 1);
    doc.addField("product_name", "机油泵");
    doc.addField("shop_id", 0);

    docs.add(doc);

    doc = new SolrInputDocument();
    doc.addField("id", "3", 1);
    doc.addField("product_name", "昆仑机滤");
    doc.addField("shop_id", 0);

    docs.add(doc);

    doc = new SolrInputDocument();
    doc.addField("id", "4", 1);
    doc.addField("product_name", "壳牌机油滤");
    doc.addField("shop_id", 0);

    docs.add(doc);

    doc = new SolrInputDocument();
    doc.addField("id", "5", 1);
    doc.addField("product_name", "长城机滤");
    doc.addField("shop_id", 0);

    docs.add(doc);

    doc = new SolrInputDocument();
    doc.addField("id", "6", 1);
    doc.addField("product_name", "长城机油滤清器");
    doc.addField("shop_id", 0);

    docs.add(doc);

    doc = new SolrInputDocument();
    doc.addField("id", "7", 1);
    doc.addField("product_name", "长城机油");
    doc.addField("shop_id", 0);
    doc.addField("product_model","6L");

    docs.add(doc);

    SolrClientHelper.getProductSolrClient().addDocs(docs);
    res = SolrClientHelper.getProductSolrClient().queryById("1", "2", "3");
    assertEquals(3, res.getResults().size());
  }


}