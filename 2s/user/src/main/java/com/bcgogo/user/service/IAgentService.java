package com.bcgogo.user.service;

import com.bcgogo.user.dto.AgentsDTO;
import com.bcgogo.user.dto.SalesManDTO;
import com.bcgogo.user.dto.totalMonthTarget;
import com.bcgogo.user.model.AgentTargt;
import com.bcgogo.user.model.Agents;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zyj
 * Date: 12-2-26
 * Time: 上午10:12
 * To change this template use File | Settings | File Templates.
 */
public interface IAgentService {
   List<AgentsDTO> getAllAgentsByNameORByAgentCode(int month, String year, String name, String agentCode, int pageNo, int pageSize);

  public int countAllAgentsByNameORByAgentCode(String name, String agentCode);

 public List<Agents> getAgentByAgentCode(String agentCode);

  public AgentTargt getAgentTargetByMonthAndYearAndAgentId(int month, String year, Long agentId);

  public List<AgentsDTO> getMonthTargetByAgentIdAndYear(String year, Long agentId);

  public AgentsDTO saveAgent(AgentsDTO agentDTO);

  public void updateAgents(AgentsDTO agentDTO);

  public void updateAgentTarget(AgentsDTO agentDTO);

  public void saveAgentTarget(AgentsDTO agentDTO);

  public List<totalMonthTarget> monthTarget(String year);

 public List<SalesManDTO> getAllSalesManByAgentIdSalesManNameSalesManCode(int month, String year, Long agentId, String salesManCode, String salesManName, int pageNo, int pageSize);

  public int countSalesManByAgentIdSalesManNameSalesManCode(Long agentId, String salesManCode, String salesManName);
}
