package com.bcgogo.user.service;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.AgentsDTO;
import com.bcgogo.user.dto.SalesManDTO;
import com.bcgogo.user.dto.totalMonthTarget;
import com.bcgogo.user.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zyj
 * Date: 12-2-26
 * Time: 上午10:01
 * To change this template use File | Settings | File Templates.
 */
@Component
public class AgentService implements IAgentService{
   private static final Logger LOG = LoggerFactory.getLogger(AgentService.class);
   public List<AgentsDTO> getAllAgentsByNameORByAgentCode(int month, String year, String name, String agentCode, int pageNo, int pageSize) {
    UserWriter writer = userDaoManager.getWriter();
    List<Agents> agentList = writer.getAllAgentsByNameORByAgentCode(name, agentCode, pageNo, pageSize);
    List<AgentsDTO> agentDTOs = new ArrayList();
    for (Agents agent : agentList) {
      AgentsDTO agentsDTO = new AgentsDTO();
      agentsDTO.setAgentCode(agent.getAgentCode());
      agentsDTO.setName(agent.getName());
      agentsDTO.setMobile(agent.getMobile());
      agentsDTO.setAddress(agent.getAddress());
      agentsDTO.setRespArea(agent.getRespArea());
      agentsDTO.setPersonInCharge(agent.getPersonInCharge());
      agentsDTO.setState(agent.getState());
      agentsDTO.setId(agent.getId());
      AgentTargt agentTarget = writer.getAgentTargetByMonthAndYearAndAgentId(month, year, agent.getId());
      agentsDTO.setMonth(month);
      agentsDTO.setYear(year);
      if (agentTarget != null) {

        agentsDTO.setMonthTarget(agentTarget.getMonthTarget());
        agentsDTO.setSeasonTarget(agentTarget.getSeasonTarget());
        agentsDTO.setYearTarget(agentTarget.getYearTarget());
      }
      agentDTOs.add(agentsDTO);
    }
    return agentDTOs;

  }

  public List<SalesManDTO> getAllSalesManByAgentIdSalesManNameSalesManCode(int month, String year, Long agentId, String salesManCode, String salesManName, int pageNo, int pageSize) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    UserWriter writer = userDaoManager.getWriter();
    List<SalesMan> salesManList = writer.getAllSalesManByAgentIdSalesManNameSalesManCode(agentId, salesManCode, salesManName, pageNo, pageSize);
    List<SalesManDTO> salesManDTOs = new ArrayList<SalesManDTO>();
    for (SalesMan salesMan : salesManList) {
      SalesManDTO salesManDTO = new SalesManDTO();
      salesManDTO.setId(salesMan.getId());
      salesManDTO.setSalesManCode(salesMan.getSalesManCode());
      salesManDTO.setMobile(salesMan.getMobile());
      salesManDTO.setName(salesMan.getName());
      salesManDTO.setStatus(salesMan.getStatus());
      salesManDTO.setMonth(month);
      salesManDTO.setYear(year);
      SalesManTarget salesManTarget = writer.getSalesManTargetByMonthYearSalesManId(month, year, salesMan.getId());
      if (salesManTarget != null) {
        salesManDTO.setMonthTarget(salesManTarget.getMonthTarget());
        salesManDTO.setYearTarget(salesManTarget.getYearTarget());
      }
//      try {
//        String start = "";
//        String end = "";
//        String endStr = String.valueOf(month + 1);
//        int yearInt = Integer.parseInt(year);
//        String endYear = String.valueOf(yearInt + 1);
//        String startStr = String.valueOf(month);
//        if (month >= 10) {
//          start = year + "-" + startStr + "-01";
//        } else {
//          start = year + "-0" + startStr + "-01";
//        }
//        if (month >= 9) {
//          end = year + "-" + endStr + "-01";
//        } else {
//          end = year + "-0" + endStr + "-01";
//        }
//
//
//        int monthFinish = configService.countShopByAgentIdAndTime(4007L, start, end);
//        salesManDTO.setMonthActual(monthFinish);
//        int yearFinish = configService.countShopByAgentIdAndTime(4007L, year + "-01-01", endYear + "-01-01");
//        salesManDTO.setYearActual(yearFinish);
//      } catch (Exception e) {
//        LOG.info(e.getMessage());
//      }

      salesManDTOs.add(salesManDTO);
    }
    return salesManDTOs;
  }

  public int countAllAgentsByNameORByAgentCode(String name, String agentCode) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.countAllAgentsByNameORByAgentCode(name, agentCode);
  }

  public List<Agents> getAgentByAgentCode(String agentCode){
     UserWriter writer = userDaoManager.getWriter();
     return writer.getAgentByAgentCode(agentCode);
  }

  public int countSalesManByAgentIdSalesManNameSalesManCode(Long agentId, String salesManCode, String salesManName) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.countSalesManByAgentIdSalesManNameSalesManCode(agentId, salesManCode, salesManName);
  }

  public AgentTargt getAgentTargetByMonthAndYearAndAgentId(int month, String year, Long agentId) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getAgentTargetByMonthAndYearAndAgentId(month, year, agentId);
  }

  public List<AgentsDTO> getMonthTargetByAgentIdAndYear(String year, Long agentId) {
    UserWriter writer = userDaoManager.getWriter();

    List<AgentTargt> agentTargtList = writer.getMonthTargetByAgentIdAndYear(year, agentId);
    List<AgentsDTO> agentsDTOs = new ArrayList<AgentsDTO>();
    for (AgentTargt at : agentTargtList) {
      AgentsDTO agentsDTO = new AgentsDTO();
      agentsDTO.setMonthTarget(at.getMonthTarget());
      agentsDTOs.add(agentsDTO);
    }
    if (agentsDTOs != null && agentsDTOs.size() < 12) {
      while (agentsDTOs.size() < 12) {
        AgentsDTO agentsDTO = new AgentsDTO();
        agentsDTOs.add(agentsDTO);
      }
    }
    return (List<AgentsDTO>) agentsDTOs;
  }

  public AgentsDTO saveAgent(AgentsDTO agentDTO) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    Agents agent = new Agents();

    agent.setAgentCode(agentDTO.getAgentCode());
    agent.setName(agentDTO.getName());
    agent.setMobile(agentDTO.getMobile());
    agent.setAddress(agentDTO.getAddress());
    agent.setPersonInCharge(agentDTO.getPersonInCharge());
    agent.setRespArea(agentDTO.getRespArea());
    agent.setState(agentDTO.getState());
    try {
      writer.save(agent);
      writer.commit(status);
      agentDTO.setId(agent.getId());
      return agentDTO;
    } finally {
      writer.rollback(status);
    }
  }

  public void updateAgents(AgentsDTO agentDTO) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    Agents agent = writer.getById(Agents.class, agentDTO.getAgentId());
    agent.setState(agentDTO.getState());
    try {
      writer.save(agent);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  public void updateAgentTarget(AgentsDTO agentDTO) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    AgentTargt atDTO = getAgentTargetByMonthAndYearAndAgentId(agentDTO.getMonth(), agentDTO.getYear(), agentDTO.getAgentId());
    try {
      if (atDTO == null) {
        AgentTargt agentTargt = new AgentTargt();
        agentTargt.setAgentId(agentDTO.getAgentId());
        agentTargt.setMonthTarget(agentDTO.getMonthTarget());
        agentTargt.setMonth(agentDTO.getMonth());
        agentTargt.setYear(agentDTO.getYear());
        agentTargt.setYearTarget(agentDTO.getYearTarget());
        writer.save(agentTargt);
      } else {
        AgentTargt at = writer.getById(AgentTargt.class, atDTO.getId());

        at.setAgentId(agentDTO.getAgentId());
        at.setMonthTarget(agentDTO.getMonthTarget());
        at.setMonth(agentDTO.getMonth());
        at.setYear(agentDTO.getYear());
        at.setYearTarget(agentDTO.getYearTarget());
        writer.save(at);
      }
      writer.commit(status);
    } catch (Exception e) {
      LOG.info(e.getMessage());
    } finally {
      writer.rollback(status);
    }
  }

  public void saveAgentTarget(AgentsDTO agentDTO) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    AgentTargt agentTarget = new AgentTargt();
    agentTarget.setAgentId(agentDTO.getAgentId());
    agentTarget.setMonth(agentDTO.getMonth());
    agentTarget.setYear(agentDTO.getYear());
    agentTarget.setMonthTarget(agentDTO.getMonthTarget());
    agentTarget.setSeasonTarget(agentDTO.getSeasonTarget());
    agentTarget.setYearTarget(agentDTO.getYearTarget());
    try {
      writer.save(agentTarget);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  public List<totalMonthTarget> monthTarget(String year) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.monthTarget(year);
  }
  @Autowired
  private UserDaoManager userDaoManager;
}
