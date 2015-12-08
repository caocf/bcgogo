package com.bcgogo.member;

import com.bcgogo.AbstractTest;
import com.bcgogo.customer.MemberController;
import com.bcgogo.enums.user.SalesManStatus;
import com.bcgogo.enums.Sex;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.TxnController;
import com.bcgogo.txn.service.IMemberCheckerService;
import com.bcgogo.user.dto.SalesManDTO;
import com.bcgogo.user.service.IMembersService;
import com.bcgogo.utils.SalesManConstant;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;

import java.util.List;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-7-21
 * Time: 下午4:48
 * To change this template use File | Settings | File Templates.
 */
public class SalesManTest extends AbstractTest {
  @Before
  public void setUp() throws Exception {
    this.request = new MockHttpServletRequest();
    this.response = new MockHttpServletResponse();
    this.txnController = new TxnController();
    this.memberController = new MemberController();

  }

  /**
   * 业务场景:现在店铺都没有员工信息，点击员工管理出现的场景
   * 校验获取员工列表
   *
   * @throws Exception
   */
  @Test
  public void testSalesManData() throws Exception {
    ModelMap model = new ModelMap();
    Long shopId = createShop();
    request.getSession().setAttribute("shopId", shopId);
    request.setParameter("pageNo", "1");

    String url = memberController.salesManData(model, request);
    Assert.assertEquals("/customer/salesManData", url);
    int inServiceSalesManNum = (Integer) request.getAttribute("totalInService");
    Assert.assertEquals(0, inServiceSalesManNum);
    List<SalesManDTO> salesManDTOList = (List<SalesManDTO>) request.getAttribute("salesManDTOList");
    Assert.assertEquals(salesManDTOList, null);
  }

  /**
   * 业务场景:循环增加员工、校验、保存、验证、更新员工信息
   *
   * @throws Exception
   */
  @Test
  public void checkSalesManInfo() throws Exception {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    IMemberCheckerService memberCheckerService = ServiceManager.getService(IMemberCheckerService.class);
    ModelMap model = new ModelMap();
    Long shopId = createShop();
    request.getSession().setAttribute("shopId", shopId);

    String salesManCode = "000"; //员工编号
    String salesManName = "员工姓名"; //员工姓名

    //创建员工的数量
    int salesManNum = 25;
    int dividend = 2;

    String checkResultStr = ""; //校验的结果
    String url = "";//返回的url
    Long salesManId = null;//员工的id

    //循环新建员工信息->校验->保存->验证->修改-> 校验->保存
    for (int i = 0; i < salesManNum; i++) {

      //新建员工信息
      SalesManDTO salesManDTO = createSalesMan(shopId, salesManCode + i, salesManName + i);

      //校验
      salesManDTO.setDepartmentName("test-department");
      checkResultStr = memberCheckerService.checkSalesManInfo(salesManDTO, shopId);
      Assert.assertEquals(SalesManConstant.SALES_MAN_INFO_VALIDATE_SUCCESS, checkResultStr);

      //保存
      staffManageController.checkAndSaveSalesManInfo( request,salesManDTO);

      //验证
      salesManId = salesManDTO.getId();
      Assert.assertNotNull(salesManId);
      request.getSession().setAttribute("shopId", shopId);
      url = memberController.getSaleManInfoById(request,salesManId);
      Assert.assertEquals("/customer/salesManInfo", url);
      SalesManDTO returnSalesManDTO = (SalesManDTO) request.getAttribute("salesManDTO");
      Assert.assertNotNull(returnSalesManDTO);
      Assert.assertEquals(salesManDTO.getSalesManCode(), returnSalesManDTO.getSalesManCode());
      Assert.assertEquals(salesManDTO.getName(), returnSalesManDTO.getName());

      //更新 校验
      returnSalesManDTO.setSalesManCode("");
      checkResultStr = memberCheckerService.checkSalesManInfo(returnSalesManDTO, shopId);
      Assert.assertEquals(SalesManConstant.SALES_MAN_INFO_VALIDATE_SUCCESS, checkResultStr);


      //把员工编号修改为已有的员工号码
      if (i > 0) {
        returnSalesManDTO.setSalesManCode(salesManCode + String.valueOf(i - 1));

        //验证员工编号是否已经存在
        checkResultStr = memberCheckerService.checkSalesManInfo(returnSalesManDTO, shopId);
        Assert.assertEquals(SalesManConstant.SALES_MAN_CODE_EXIST, checkResultStr);
      }

      //把员工编号修改为正确的员工号码
      returnSalesManDTO.setSalesManCode(salesManCode + i);
      checkResultStr = memberCheckerService.checkSalesManInfo(returnSalesManDTO, shopId);

      //验证成功
      Assert.assertEquals(SalesManConstant.SALES_MAN_INFO_VALIDATE_SUCCESS, checkResultStr);

      //更新 校验
      returnSalesManDTO.setName("");
      checkResultStr = memberCheckerService.checkSalesManInfo(returnSalesManDTO, shopId);
      Assert.assertEquals(SalesManConstant.SALES_MAN_NAME_EMPTY, checkResultStr);

      //把员工姓名修改为已有的员工号码
      if (i > 0) {
        returnSalesManDTO.setName(salesManName + String.valueOf(i - 1));
        //验证员工姓名是否已经存在
        checkResultStr = memberCheckerService.checkSalesManInfo(returnSalesManDTO, shopId);
        Assert.assertEquals(SalesManConstant.SALES_MAN_NAME_EXIST, checkResultStr);
      } else {
        returnSalesManDTO.setName(salesManName);
      }

      //把员工姓名修改为正确的员工号码
      returnSalesManDTO.setName(salesManName + i);
      checkResultStr = memberCheckerService.checkSalesManInfo(returnSalesManDTO, shopId);
      //验证成功
      Assert.assertEquals(SalesManConstant.SALES_MAN_INFO_VALIDATE_SUCCESS, checkResultStr);

      if (i % dividend == 0) {
        returnSalesManDTO.setStatus(SalesManStatus.DEMISSION);
      }
      salesManDTO.setDepartmentName("test-department");
      //保存
      staffManageController.checkAndSaveSalesManInfo( request,  returnSalesManDTO);
    }

    //验证有效员工数量
    List<SalesManDTO> salesManDTOList = membersService.searchSaleManByShopIdAndKeyword(shopId, null);
    Assert.assertNotNull(salesManDTOList);
    Assert.assertEquals(salesManNum / dividend, salesManDTOList.size());

    //分页读取数据
    request.setParameter("pageNo", String.valueOf(memberController.DEFAULT_PAGE_NO));
    url = memberController.salesManData(model, request);
    Assert.assertEquals("/customer/salesManData", url);
    int inServiceSalesManNum = (Integer) request.getAttribute("totalInService");
    Assert.assertEquals(salesManNum / dividend, inServiceSalesManNum);
    salesManDTOList = (List<SalesManDTO>) request.getAttribute("salesManDTOList");
    Assert.assertNotNull(salesManDTOList);
    Assert.assertEquals(memberController.PAGE_SIZE, salesManDTOList.size());

    //分页读取数据
    request.setParameter("pageNo", String.valueOf(memberController.DEFAULT_PAGE_NO + 1));
    url = memberController.salesManData(model, request);
    Assert.assertEquals("/customer/salesManData", url);
    inServiceSalesManNum = (Integer) request.getAttribute("totalInService");
    Assert.assertEquals(salesManNum / dividend, inServiceSalesManNum);
    salesManDTOList = (List<SalesManDTO>) request.getAttribute("salesManDTOList");
    Assert.assertNotNull(salesManDTOList);
    Assert.assertEquals(salesManNum - (memberController.PAGE_SIZE * memberController.DEFAULT_PAGE_NO), salesManDTOList.size());

    //创建一个新员工
    SalesManDTO anotherSalesManDTO = createSalesMan(shopId, salesManCode, salesManName);

    //更新 校验
    anotherSalesManDTO.setSalesManCode("");
    checkResultStr = memberCheckerService.checkSalesManInfo(anotherSalesManDTO, shopId);
    Assert.assertEquals(SalesManConstant.SALES_MAN_INFO_VALIDATE_SUCCESS, checkResultStr);

    //更新 校验
    anotherSalesManDTO.setSalesManCode(salesManCode + new Random().nextInt(salesManNum - 1));
    checkResultStr = memberCheckerService.checkSalesManInfo(anotherSalesManDTO, shopId);
    Assert.assertEquals(SalesManConstant.SALES_MAN_CODE_EXIST, checkResultStr);

    //更新 校验
    anotherSalesManDTO.setSalesManCode(salesManCode + salesManNum);
    anotherSalesManDTO.setName("");
    checkResultStr = memberCheckerService.checkSalesManInfo(anotherSalesManDTO, shopId);
    Assert.assertEquals(SalesManConstant.SALES_MAN_NAME_EMPTY, checkResultStr);

    //更新 校验
    anotherSalesManDTO.setName(salesManName + new Random().nextInt(salesManNum - 1));
    checkResultStr = memberCheckerService.checkSalesManInfo(anotherSalesManDTO, shopId);
    Assert.assertEquals(SalesManConstant.SALES_MAN_NAME_EXIST, checkResultStr);

    //更新 校验
    anotherSalesManDTO.setName(salesManName + salesManNum);
    anotherSalesManDTO.setCareerDateStr("abcdefg");
    checkResultStr = memberCheckerService.checkSalesManInfo(anotherSalesManDTO, shopId);
    Assert.assertEquals(SalesManConstant.SALES_MAN_DATE_FORMAT_ERROR, checkResultStr);

    //更新 校验
    anotherSalesManDTO.setCareerDateStr("2012-08-01");
    checkResultStr = memberCheckerService.checkSalesManInfo(anotherSalesManDTO, shopId);
    Assert.assertEquals(SalesManConstant.SALES_MAN_INFO_VALIDATE_SUCCESS, checkResultStr);

    //校验成功后 保存
    anotherSalesManDTO.setDepartmentName("test-department");
    staffManageController.checkAndSaveSalesManInfo( request,  anotherSalesManDTO);
    salesManId = anotherSalesManDTO.getId();
    Assert.assertNotNull(salesManId);

    request.setParameter("pageNo", String.valueOf(memberController.DEFAULT_PAGE_NO));
    url = memberController.salesManData(model, request);
    Assert.assertEquals("/customer/salesManData", url);
    inServiceSalesManNum = (Integer) request.getAttribute("totalInService");
    Assert.assertEquals((salesManNum / dividend) + 1, inServiceSalesManNum);
    salesManDTOList = (List<SalesManDTO>) request.getAttribute("salesManDTOList");
    Assert.assertNotNull(salesManId);
    Assert.assertEquals(memberController.PAGE_SIZE, salesManDTOList.size());
  }

  /**
   * 创建一个员工dto
   * @param shopId       店铺
   * @param salesManCode 员工工号
   * @param name         员工姓名
   * @return
   */
  public SalesManDTO createSalesMan(long shopId, String salesManCode, String name) {
    SalesManDTO salesManDTO = new SalesManDTO();

    salesManDTO.setShopId(shopId);
    salesManDTO.setSalesManCode(salesManCode);
    salesManDTO.setName(name);
    salesManDTO.setMobile("13800001234");
    salesManDTO.setStatus(SalesManStatus.INSERVICE);
    salesManDTO.setAddress("苏州统购");
    salesManDTO.setSex(Sex.MALE.toString());
    salesManDTO.setDepartment("洗车美容部");
    salesManDTO.setPosition("洗车工");
    salesManDTO.setIdentityCard("341222198806155252");
    salesManDTO.setSalary(2000d);
    salesManDTO.setAllowance(500d);
    salesManDTO.setCareerDateStr("2012-08-01");
    salesManDTO.setMemo("1234");

    return salesManDTO;
  }
}
