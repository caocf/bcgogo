package com.bcgogo.search;

import com.bcgogo.search.service.ISolrMatchStopWordService;
import com.bcgogo.search.util.SolrUtil;
import com.bcgogo.service.ServiceManager;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: Caiwei
 * Date: 10/4/11
 * Time: 10:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class SolrUtilTest  extends AbstractTest{
  @Before
  public void setUpTest() throws Exception {
  }

  @Test
  public void testGetImitateSolrMatchScore() throws Exception {
    List<String> stopWords = new ArrayList<String>();
    stopWords.add("总成");
    ISolrMatchStopWordService solrMatchStopWordService = ServiceManager.getService(ISolrMatchStopWordService.class);
    solrMatchStopWordService.saveSolrMatchStopWord(stopWords);

    assertEquals(0d, SolrUtil.getImitateSolrMatchScore("发动机总成","大灯总成",SolrUtil.MATCH_RULE_PARTICIPLE_ALL),0.001d);
    assertEquals(0d, SolrUtil.getImitateSolrMatchScore("发动机总成","大灯总成",SolrUtil.MATCH_RULE_PARTICIPLE_SINGLE),0.001d);


    assertEquals(0d, SolrUtil.getImitateSolrMatchScore("发动机a总成","a洗刷刷",SolrUtil.MATCH_RULE_PARTICIPLE_ALL),0.001d);
    assertEquals(0d, SolrUtil.getImitateSolrMatchScore("发动机a总成","a洗刷刷",SolrUtil.MATCH_RULE_PARTICIPLE_SINGLE),0.001d);


    assertEquals(0d, SolrUtil.getImitateSolrMatchScore("发动机 囧 总成","a洗刷刷 囧",SolrUtil.MATCH_RULE_PARTICIPLE_ALL),0.001d);
    assertEquals(0d, SolrUtil.getImitateSolrMatchScore("发动机 囧 总成","a洗刷刷 囧",SolrUtil.MATCH_RULE_PARTICIPLE_SINGLE),0.001d);
  }

}
