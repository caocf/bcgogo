package com.bcgogo.user.service.solr;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 14-1-22
 * Time: 下午4:02
 * To change this template use File | Settings | File Templates.
 */
public interface IVehicleSolrWriterService {
  void reCreateVehicleSolrIndex(Long shopId, int rows) throws Exception;

  void createVehicleSolrIndex(Long shopId, Long... vehicleId) throws Exception;

  void createVehicleSolrIndexByLicenceNo(String... LicenceNo) throws Exception;
}
