package com.bcgogo.search.model;

import com.bcgogo.service.GenericReaderDao;
import org.hibernate.SessionFactory;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 12/31/11
 * Time: 5:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchReader extends GenericReaderDao {

  public SearchReader(SessionFactory sessionFactory) {
    super(sessionFactory);
  }




}
