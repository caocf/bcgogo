package com.bcgogo.txn;

import com.bcgogo.AbstractTest;
import com.bcgogo.enums.CategoryType;
import com.bcgogo.txn.dto.CategoryServiceSearchDTO;
import com.bcgogo.txn.dto.ServiceDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;

/**
 * Created by IntelliJ IDEA.
 * User: zhoudongming
 * Date: 12-7-23
 * Time: 下午8:29
 * To change this template use File | Settings | File Templates.
 */
public class SetConstructionTest extends AbstractTest{
  @Before
  public void setUp() throws Exception{
    categoryController = new CategoryController();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
  }

  @Test
  public void testCreateCategoryServiceSearchDTO() throws Exception{
    if(true){
      return;//todo 这个test需要从新写了。by qxy
    }
    ModelMap model = new ModelMap();
    Long shopId = createShop();
    request.getSession().setAttribute("shopId", shopId);
    saveServiceAndCategory(shopId);
    categoryController.getCategoryItemSearch(model, request, null);
    CategoryServiceSearchDTO categoryServiceSearchDTO = (CategoryServiceSearchDTO)model.get("categoryServiceSearchDTO");
    ServiceDTO[] serviceDTOs = categoryServiceSearchDTO.getServiceDTOs();
    Assert.assertEquals(1,serviceDTOs.length);
    ServiceDTO serviceDTO = serviceDTOs[0];
    Assert.assertEquals("洗车",serviceDTO.getName());
    Assert.assertEquals(20,serviceDTO.getPrice(),0.001);
    Assert.assertEquals(5,serviceDTO.getPercentageAmount(),0.001);
    Assert.assertEquals("洗车",serviceDTO.getCategoryName());
    Assert.assertEquals(CategoryType.BUSINESS_CLASSIFICATION,serviceDTO.getCategoryType());

    request.setParameter("pageNo",String.valueOf(1));
    request.setParameter("totalRows",String.valueOf(1));
    request.setParameter("serviceId",String.valueOf(serviceDTO.getId()));
    request.setParameter("sName","洗车");
    request.setParameter("cName","");
    request.setParameter("price",String.valueOf(30d));
    request.setParameter("percentageAmount","");
//    categoryController.updateServiceSingle(model,request,categoryServiceSearchDTO);
    categoryController.getCategoryItemSearch(model, request, null);
    categoryServiceSearchDTO = (CategoryServiceSearchDTO)model.get("categoryServiceSearchDTO");
    serviceDTOs = categoryServiceSearchDTO.getServiceDTOs();
    Assert.assertEquals(1,serviceDTOs.length);
    serviceDTO = serviceDTOs[0];
    Assert.assertEquals("洗车",serviceDTO.getName());
    Assert.assertEquals(30d,serviceDTO.getPrice(),0.001);
    Assert.assertNull(serviceDTO.getPercentageAmount());
    Assert.assertNull(serviceDTO.getCategoryName());
    Assert.assertNull(serviceDTO.getCategoryType());

    categoryController.getCategoryItemSearch(model,request,null);
    categoryServiceSearchDTO = (CategoryServiceSearchDTO)model.get("categoryServiceSearchDTO");
    serviceDTOs = categoryServiceSearchDTO.getServiceDTOs();
    Assert.assertEquals(1,serviceDTOs.length);

    categoryController.getServiceNoPercentage(model,request);
    categoryServiceSearchDTO = (CategoryServiceSearchDTO)model.get("categoryServiceSearchDTO");
    serviceDTOs = categoryServiceSearchDTO.getServiceDTOs();
    Assert.assertEquals(1,serviceDTOs.length);

    categoryController.createNewService(model,request);
    categoryServiceSearchDTO = (CategoryServiceSearchDTO)model.get("categoryServiceSearchDTO");
    categoryServiceSearchDTO.setServiceName("打蜡");
    categoryServiceSearchDTO.setCategoryName("美容");
    categoryServiceSearchDTO.setPrice(35d);
    categoryServiceSearchDTO.setPercentageAmount(10d);
//    categoryController.addNewService(model,request,categoryServiceSearchDTO);
    categoryServiceSearchDTO = (CategoryServiceSearchDTO)model.get("categoryServiceSearchDTO");
    serviceDTOs = categoryServiceSearchDTO.getServiceDTOs();
    Assert.assertEquals(2,serviceDTOs.length);
    serviceDTO = serviceDTOs[1];
    Assert.assertEquals("打蜡",serviceDTO.getName());
    Assert.assertEquals(35d,serviceDTO.getPrice(),0.001);
    Assert.assertEquals(10d,serviceDTO.getPercentageAmount(),0.001);
    Assert.assertEquals("美容",serviceDTO.getCategoryName());
    Assert.assertEquals(CategoryType.BUSINESS_CLASSIFICATION,serviceDTO.getCategoryType());

    request.setParameter("pageNo",String.valueOf(1));
    request.setParameter("totalRows",String.valueOf(2));
    request.setParameter("percentageAmount",String.valueOf(8d));
    categoryController.updateServicePercentage(model,request,categoryServiceSearchDTO);
    categoryServiceSearchDTO = (CategoryServiceSearchDTO)model.get("categoryServiceSearchDTO");
    ServiceDTO serviceDTO1 = categoryServiceSearchDTO.getServiceDTOs()[0];
    ServiceDTO serviceDTO2 = categoryServiceSearchDTO.getServiceDTOs()[1];
    Assert.assertEquals("洗车",serviceDTO1.getName());
    Assert.assertEquals(30d,serviceDTO1.getPrice(),0.001);
    Assert.assertEquals(8d,serviceDTO1.getPercentageAmount(),0.001);
    Assert.assertNull(serviceDTO1.getCategoryName());
    Assert.assertNull(serviceDTO1.getCategoryType());
    Assert.assertEquals("打蜡",serviceDTO2.getName());
    Assert.assertEquals(35d,serviceDTO2.getPrice(),0.001);
    Assert.assertEquals(8d,serviceDTO2.getPercentageAmount(),0.001);
    Assert.assertEquals("美容",serviceDTO2.getCategoryName());
    Assert.assertEquals(CategoryType.BUSINESS_CLASSIFICATION,serviceDTO2.getCategoryType());

    Long catogoryId = categoryServiceSearchDTO.getCategoryDTOs()[0].getId();
    request.setParameter("pageNo",String.valueOf(1));
    request.setParameter("totalRows",String.valueOf(2));
    request.setParameter("categoryId",String.valueOf(catogoryId));
    request.setParameter("name","洗车");
    categoryController.updateServiceCategory(model,request,categoryServiceSearchDTO);
    categoryServiceSearchDTO = (CategoryServiceSearchDTO)model.get("categoryServiceSearchDTO");
    serviceDTO1 = categoryServiceSearchDTO.getServiceDTOs()[0];
    serviceDTO2 = categoryServiceSearchDTO.getServiceDTOs()[1];
    Assert.assertEquals("洗车",serviceDTO1.getName());
    Assert.assertEquals(30d,serviceDTO1.getPrice(),0.001);
    Assert.assertEquals(8d,serviceDTO1.getPercentageAmount(),0.001);
    Assert.assertNull(serviceDTO1.getCategoryName());
    Assert.assertNull(serviceDTO1.getCategoryType());
    Assert.assertEquals("打蜡",serviceDTO2.getName());
    Assert.assertEquals(35d,serviceDTO2.getPrice(),0.001);
    Assert.assertEquals(8d,serviceDTO2.getPercentageAmount(),0.001);
    Assert.assertEquals("洗车",serviceDTO2.getCategoryName());
    Assert.assertEquals(CategoryType.BUSINESS_CLASSIFICATION,serviceDTO2.getCategoryType());

    request.setParameter("pageNo",String.valueOf(1));
    request.setParameter("totalRows",String.valueOf(2));
    request.setParameter("categoryId","");
    request.setParameter("name","机修");
    categoryController.updateServiceCategory(model,request,categoryServiceSearchDTO);
    categoryServiceSearchDTO = (CategoryServiceSearchDTO)model.get("categoryServiceSearchDTO");
    serviceDTO1 = categoryServiceSearchDTO.getServiceDTOs()[0];
    serviceDTO2 = categoryServiceSearchDTO.getServiceDTOs()[1];
    Assert.assertEquals("洗车",serviceDTO1.getName());
    Assert.assertEquals(30d,serviceDTO1.getPrice(),0.001);
    Assert.assertEquals(8d,serviceDTO1.getPercentageAmount(),0.001);
    Assert.assertNull(serviceDTO1.getCategoryName());
    Assert.assertNull(serviceDTO1.getCategoryType());
    Assert.assertEquals("打蜡",serviceDTO2.getName());
    Assert.assertEquals(35d,serviceDTO2.getPrice(),0.001);
    Assert.assertEquals(8d,serviceDTO2.getPercentageAmount(),0.001);
    Assert.assertEquals("机修",serviceDTO2.getCategoryName());
    Assert.assertEquals(CategoryType.BUSINESS_CLASSIFICATION,serviceDTO2.getCategoryType());
    Assert.assertEquals(3,categoryServiceSearchDTO.getCategoryDTOs().length);

    request.setParameter("pageNo",String.valueOf(1));
    request.setParameter("totalRows",String.valueOf(2));
    request.setParameter("categoryId","");
    request.setParameter("name","");
    categoryController.updateServiceCategory(model,request,categoryServiceSearchDTO);
    categoryServiceSearchDTO = (CategoryServiceSearchDTO)model.get("categoryServiceSearchDTO");
    serviceDTO1 = categoryServiceSearchDTO.getServiceDTOs()[0];
    serviceDTO2 = categoryServiceSearchDTO.getServiceDTOs()[1];
    Assert.assertEquals("洗车",serviceDTO1.getName());
    Assert.assertEquals(30d,serviceDTO1.getPrice(),0.001);
    Assert.assertEquals(8d,serviceDTO1.getPercentageAmount(),0.001);
    Assert.assertNull(serviceDTO1.getCategoryName());
    Assert.assertNull(serviceDTO1.getCategoryType());
    Assert.assertEquals("打蜡",serviceDTO2.getName());
    Assert.assertEquals(35d,serviceDTO2.getPrice(),0.001);
    Assert.assertEquals(8d,serviceDTO2.getPercentageAmount(),0.001);
    Assert.assertNull(serviceDTO2.getCategoryName());
    Assert.assertNull(serviceDTO2.getCategoryType());
    Assert.assertEquals(3,categoryServiceSearchDTO.getCategoryDTOs().length);
  }
}
