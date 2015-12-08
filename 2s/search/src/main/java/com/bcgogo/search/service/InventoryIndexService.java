package com.bcgogo.search.service;

import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.search.model.SearchDaoManager;
import com.bcgogo.search.model.SearchWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 12-4-10
 * Time: 下午2:09
 * To change this template use File | Settings | File Templates.
 */
@Component
public class InventoryIndexService implements IInventoryIndexService {
   private static final Logger LOG = LoggerFactory.getLogger(InventoryIndexService.class);
  @Autowired
  private SearchDaoManager searchDaoManager;


  @Override
  public void saveInventorySearchIndex(InventorySearchIndex inventorySearchIndex) {
    SearchWriter writer = searchDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.saveOrUpdate(inventorySearchIndex);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }
}