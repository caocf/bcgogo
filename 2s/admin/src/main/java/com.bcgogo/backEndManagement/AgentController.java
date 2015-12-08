package com.bcgogo.backEndManagement;

import com.bcgogo.product.service.IProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.AgentsDTO;
import com.bcgogo.user.dto.SalesManDTO;
import com.bcgogo.user.dto.totalMonthTarget;
import com.bcgogo.user.model.Agents;
import com.bcgogo.user.service.IAgentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zyj
 * Date: 12-2-16
 * Time: 下午12:56
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/agents.do")
public class AgentController {
  private static final Logger LOG = LoggerFactory.getLogger(BackEndShopController.class);

  @RequestMapping(params = "method=getAgents")
  public String getAgents(HttpServletRequest request) {
    IAgentService agentService = ServiceManager.getService(IAgentService.class);
    String pageNoStr = request.getParameter("pageNo");

    String agentCode = request.getParameter("searchAgentCode");
    String name = request.getParameter("searchName");

    if (agentCode != null && "代理商编号".equals(agentCode)) {
      agentCode = "";
    }
    if (name != null && "代理商名称".equals(name)) {
      name = "";
    }
    int pageNo = 1;
    if (pageNoStr != null) {
      pageNo = Integer.parseInt(pageNoStr);
    }
    Calendar calendar = Calendar.getInstance();
    int yearInt = calendar.get(Calendar.YEAR);

    int month = 1;
    String year = String.valueOf(yearInt);
    if (request.getParameter("year") != null) {
      year = request.getParameter("year");
    }
    if (request.getParameter("month") != null) {
      month = Integer.parseInt(request.getParameter("month"));
    }

    int pageSize = 5;
    List<AgentsDTO> agentsList = agentService.getAllAgentsByNameORByAgentCode(month, year, name, agentCode, pageNo - 1, pageSize);
    int count = 0;
    try {
      count = agentService.countAllAgentsByNameORByAgentCode(name, agentCode);
    } catch (Exception e) {
      LOG.info(e.getMessage(), e);
    }
    int pageCount = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;

    List<totalMonthTarget> totalList = agentService.monthTarget(year);
    request.setAttribute("totalList", totalList);
    request.setAttribute("month", month);
    request.setAttribute("year", year);
    request.setAttribute("name", name);
    request.setAttribute("agentCode", agentCode);
    request.setAttribute("pageNo", pageNo);
    request.setAttribute("pageCount", pageCount);
    request.setAttribute("count", count);
    request.setAttribute("agentsList", agentsList);
    return "/backEndManagement/backAgent";
  }

  @RequestMapping(params = "method=detailAgents")
  public String detailAgents(HttpServletRequest request) {
    IAgentService agentService = ServiceManager.getService(IAgentService.class);
    Long agentId = Long.parseLong(request.getParameter("agentId"));
    String state = request.getParameter("state");
    String agentCode = request.getParameter("agentCode");
    String personInCharge = request.getParameter("personInCharge");
    String name = request.getParameter("name");
    String address = request.getParameter("address");
    String mobile = request.getParameter("mobile");
    String respArea = request.getParameter("respArea");
    String monthTarget = request.getParameter("monthTarget");
    String yearTarget = request.getParameter("yearTarget");
    String year = request.getParameter("year");
//    int month=Integer.parseInt(request.getParameter("month"));
    request.setAttribute("agentId", agentId);
    request.setAttribute("agentCode", agentCode);
    request.setAttribute("name", name);
    request.setAttribute("personInCharge", personInCharge);
    request.setAttribute("address", address);
    request.setAttribute("mobile", mobile);
    request.setAttribute("respArea", respArea);
    request.setAttribute("monthTarget", monthTarget);
    request.setAttribute("yearTarget", yearTarget);
    request.setAttribute("state", state);
    request.setAttribute("year", year);
//    List<AgentsDTO> totalList = agentService.getMonthTargetByAgentIdAndYear("2012", agentId);
//    request.setAttribute("totalList", totalList);
    String pageNoStr = request.getParameter("pageNo");
    int pageNo = 1;
    int pageSize = 2;
    if (pageNoStr != "" && pageNoStr != null) {
      pageNo = Integer.parseInt(pageNoStr);
    }
    int count = agentService.countSalesManByAgentIdSalesManNameSalesManCode(agentId, null, null);
    int pageCount = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;

    List<SalesManDTO> salesManDTOList = agentService.getAllSalesManByAgentIdSalesManNameSalesManCode(1, "2012", agentId, null, null, pageNo - 1, pageSize);
    request.setAttribute("pageNo", pageNo);
    request.setAttribute("count", count);
    request.setAttribute("pageCount", pageCount);
    request.setAttribute("salesManDTOList", salesManDTOList);
    return "/backEndManagement/backAgent_son";
  }

  @RequestMapping(params = "method=saveAgent")
  public String saveAgents(HttpServletRequest request) {
    IAgentService agentService = ServiceManager.getService(IAgentService.class);
    String agentCode = request.getParameter("agentCode");
    String name = request.getParameter("name");
    String personInCharge = request.getParameter("personInCharge");
    String address = request.getParameter("address");
    String mobile = request.getParameter("mobile");
    String respArea = request.getParameter("respArea");
    String state = request.getParameter("state");
    String year = request.getParameter("year");
    String[] monthTarget = request.getParameterValues("monthTarget");
    String yearTarget = request.getParameter("yearTarget");
    AgentsDTO agent = new AgentsDTO();
    agent.setAgentCode(agentCode);
    agent.setName(name);
    agent.setPersonInCharge(personInCharge);
    agent.setAddress(address);
    agent.setMobile(mobile);
    agent.setRespArea(respArea);
    agent.setState(Integer.parseInt(state));
    agentService.saveAgent(agent);
    Long agentId = agent.getId();
    for (int i = 0; i < monthTarget.length; i++) {
      AgentsDTO agentTarget = new AgentsDTO();
      agentTarget.setAgentId(agentId);
      agentTarget.setMonth(i + 1);
      agentTarget.setMonthTarget(Double.parseDouble(monthTarget[i]));
      agentTarget.setYear(year);
      agentTarget.setYearTarget(Double.parseDouble(yearTarget));
      agentService.saveAgentTarget(agentTarget);
    }
    request.removeAttribute("agentCode");
    request.removeAttribute("name");
    return getAgents(request);


  }

  @RequestMapping(params = "method=updateAgent")
  public String updateAgent(HttpServletRequest request, String monthTargets, String stateStr, Integer year) {
    IAgentService agentService = ServiceManager.getService(IAgentService.class);
    Long agentId = Long.parseLong(request.getParameter("agentId"));
    String state = stateStr;
    String yearStr = year.toString();
    String[] monthTarget = monthTargets.split(",");
    String yearTarget = request.getParameter("yearTarget");
    AgentsDTO agent = new AgentsDTO();

    agent.setState(Integer.parseInt(state));
    agent.setAgentId(agentId);
    agentService.updateAgents(agent);

    for (int i = 0; i < monthTarget.length; i++) {
      AgentsDTO agentTarget = new AgentsDTO();
      agentTarget.setAgentId(agentId);
      agentTarget.setMonth(i + 1);
      agentTarget.setMonthTarget(Double.parseDouble(monthTarget[i]));
      agentTarget.setYear(yearStr);
      agentTarget.setYearTarget(Double.parseDouble(yearTarget));
      agentService.updateAgentTarget(agentTarget);
    }
    request.removeAttribute("agentCode");
    request.removeAttribute("name");
    return detailAgents(request);
  }

  @RequestMapping(params = "method=getAgentTarget")
  public void getAgentTarget(HttpServletRequest request, HttpServletResponse response, String agentIdStr, Integer year) {
    IAgentService agentService = ServiceManager.getService(IAgentService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    Long agentId = Long.parseLong(agentIdStr);
    String yearStr = String.valueOf(year);
    List<AgentsDTO> targets = agentService.getMonthTargetByAgentIdAndYear(yearStr, agentId);
    String jsonStr = "";
    jsonStr = productService.getJsonWithList(targets);
    try {
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }

  }


  @RequestMapping(params = "method=addAgent")
  public String addAgent(HttpServletRequest request) {
    return "/backEndManagement/addAgent";
  }

  @RequestMapping(params = "method=getSalesMan")
  public void getSalesMan(HttpServletRequest request, HttpServletResponse response, String agentIdStr, Integer startPageNo, Integer maxRows, Integer month, Integer year) {
    IAgentService agentService = ServiceManager.getService(IAgentService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    Long agentId = Long.parseLong(agentIdStr);
    String yearStr = year.toString();
    int count = agentService.countSalesManByAgentIdSalesManNameSalesManCode(agentId, null, null);
    int pageCount = count % maxRows == 0 ? count / maxRows : count / maxRows + 1;
    List<SalesManDTO> salesManDTOList = agentService.getAllSalesManByAgentIdSalesManNameSalesManCode(month, yearStr, agentId, null, null, startPageNo - 1, maxRows);
    String jsonStr = "";
    jsonStr = productService.getJsonWithList(salesManDTOList);
    jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
    if (pageCount <= startPageNo&&salesManDTOList.size() != 0) {
      jsonStr = jsonStr + ",{\"isTheLastPage\":\"true\"}]";
    } else if (salesManDTOList == null || salesManDTOList.size() == 0) {
      jsonStr = jsonStr + "{\"isTheLastPage\":\"true\"}]";
    } else {
      jsonStr = jsonStr + ",{\"isTheLastPage\":\"false\"}]";
    }
    try {
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }

  }

  @RequestMapping(params = "method=checkAgent")
  public void checkAgent(HttpServletRequest request, HttpServletResponse response, String agentCode) {
    IAgentService agentService = ServiceManager.getService(IAgentService.class);
    List<Agents> agentsList = agentService.getAgentByAgentCode(agentCode);
    try {
      PrintWriter writer = response.getWriter();
      if (agentsList != null && agentsList.size() > 0) {
        writer.write("yes");
      } else {
        writer.write("no");
      }
    } catch (Exception e) {
      LOG.info(e.getMessage());
    }
  }
}
