package com.bcgogo.config.dto;

import com.bcgogo.api.ApiArea;
import com.bcgogo.config.dto.juhe.JuheViolateRegulationCitySearchConditionDTO;
import com.bcgogo.enums.config.JuheStatus;
import com.bcgogo.user.dto.CheckNode;
import com.bcgogo.user.dto.Node;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.MapUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-11-18
 * Time: 下午5:55
 */
public class AreaDTO implements Serializable {
  private Long id;
  private String name;
  private String fullName;
  private Long no;
  private Long parentNo;
  private List<AreaDTO> childAreaDTOList;
  private AreaDTO parentAreaDTO;//父节点
  private int level;//深度
  private boolean leaf;//是否叶子节点
  private Integer cityCode;
  private String juheCityCode;
  private JuheStatus juheStatus;

  public AreaDTO() {
  }

  public AreaDTO(Long id, Integer cityCode, String name) {
    this.id = id;
    this.cityCode = cityCode;
    this.name = name;
  }

  public List<ApiArea> getChildAppList(Set<Long> includeAreaNo) {
    List<ApiArea> apiAreas = new ArrayList<ApiArea>();
    if (CollectionUtil.isNotEmpty(getChildAreaDTOList())) {
      for (AreaDTO areaDTO : getChildAreaDTOList()) {
        if (includeAreaNo.contains(areaDTO.getNo()))
          apiAreas.add(areaDTO.toApiArea());
      }
    }
    return apiAreas;
  }

  public List<ApiArea> getChildJuheList() {
    return getChildJuheList(null);
  }

  public List<ApiArea> getChildJuheList(Map<String, JuheViolateRegulationCitySearchConditionDTO> map) {
    List<ApiArea> apiAreas = new ArrayList<ApiArea>();
    if (CollectionUtil.isNotEmpty(getChildAreaDTOList())) {
      for (AreaDTO areaDTO : getChildAreaDTOList()) {
        if (hasJuheCityCode(areaDTO)) {
          ApiArea apiArea = areaDTO.toApiArea();
          if (MapUtils.isNotEmpty(map))
            apiArea.setJuheViolateRegulationCitySearchCondition(map.get(apiArea.getJuheCityCode()));
          apiAreas.add(apiArea);
        }
      }
    }
    return apiAreas;
  }

  public List<ApiArea> getChildrenJuheList() {
    return getChildrenJuheList(null);
  }

  public List<ApiArea> getChildrenJuheList(Map<String, JuheViolateRegulationCitySearchConditionDTO> map) {
    List<ApiArea> areaList = new ArrayList<ApiArea>();
    if (CollectionUtil.isNotEmpty(getChildAreaDTOList())) {
      for (AreaDTO child : getChildAreaDTOList()) {
        getChildrenJuheList(child, areaList, map);
      }
    }
    return areaList;
  }

  private void getChildrenJuheList(AreaDTO areaDTO, List<ApiArea> apiAreaList, Map<String, JuheViolateRegulationCitySearchConditionDTO> map) {
    if (areaDTO != null) {
      if (StringUtil.isNotEmpty(areaDTO.getJuheCityCode()) && areaDTO.isJuheAreaActive()) {
        ApiArea apiArea = areaDTO.toApiArea();
        if (MapUtils.isNotEmpty(map))
          apiArea.setJuheViolateRegulationCitySearchCondition(map.get(apiArea.getJuheCityCode()));
        apiAreaList.add(apiArea);
      }
      if (CollectionUtil.isNotEmpty(areaDTO.getChildAreaDTOList())) {
        for (AreaDTO child : areaDTO.getChildAreaDTOList()) {
          getChildrenJuheList(child, apiAreaList, map);
        }
      }
    }
  }

  private boolean hasJuheCityCode(AreaDTO areaDTO) {
    if (StringUtil.isNotEmpty(areaDTO.getJuheCityCode()) && areaDTO.isJuheAreaActive()) return true;
    if (CollectionUtil.isNotEmpty(areaDTO.getChildAreaDTOList())) {
      for (AreaDTO dto : areaDTO.getChildAreaDTOList()) {
        if (hasJuheCityCode(dto))
          return true;
      }
    }
    return false;
  }

  public ApiArea toApiArea() {
    ApiArea apiArea = new ApiArea();
    apiArea.setId(getNo());
    apiArea.setCityCode(getCityCode());
    apiArea.setName(getName());
    apiArea.setJuheCityCode(getJuheCityCode());
    apiArea.setJuheStatus(getJuheStatus());
    return apiArea;
  }

  public CheckNode toFirstLevelNode() {
    CheckNode node = new CheckNode();
    node.setId(this.getNo());
    node.setText(this.getName());
    node.setValue(this.getName());
    node.setParentId(-1L);
    node.setType(Node.Type.FIRST_CATEGORY);
    node.setLeaf(false);
    node.setSort(this.getNo());
    return node;
  }

  public CheckNode toSecondLevelNode() {
    CheckNode node = new CheckNode();
    node.setId(this.getNo());
    node.setText(this.getName());
    node.setValue(this.getName());
    node.setParentId(this.getParentNo());
    node.setType(Node.Type.SECOND_CATEGORY);
    node.setLeaf(true);
    node.setSort(this.getNo());
    return node;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getNo() {
    return no;
  }

  public void setNo(Long no) {
    this.no = no;
  }

  public Long getParentNo() {
    return parentNo;
  }

  public void setParentNo(Long parentNo) {
    this.parentNo = parentNo;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public AreaDTO getParentAreaDTO() {
    return parentAreaDTO;
  }

  public void setParentAreaDTO(AreaDTO parentAreaDTO) {
    this.parentAreaDTO = parentAreaDTO;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public boolean isLeaf() {
    return leaf;
  }

  public void setLeaf(boolean leaf) {
    this.leaf = leaf;
  }

  public List<AreaDTO> getChildAreaDTOList() {
    return childAreaDTOList;
  }

  public void setChildAreaDTOList(List<AreaDTO> childAreaDTOList) {
    this.childAreaDTOList = childAreaDTOList;
  }

  //当出现4个直辖市，3个行政区，5个自治区时，当用户导入这些市时，则默认导入到省
  public static final Long[] importAreaIncludeProvince = {1022L, 1001L, 1009L, 1002L,      //4个直辖市
    1032L, 1033L, 1034L,              //3个行政区
    1020L, 1005L, 1026L, 1030L, 1031L    //5个自治区
  };

  //当出现 市辖区  市    县   时，将提示输入不合法
  public static final String[] importAreaExcludeCity = {"市辖区", "市", "县"};

  public Integer getCityCode() {
    return cityCode;
  }

  public void setCityCode(Integer cityCode) {
    this.cityCode = cityCode;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }


  public String getJuheCityCode() {
    return juheCityCode;
  }

  public void setJuheCityCode(String juheCityCode) {
    this.juheCityCode = juheCityCode;
  }

  public JuheStatus getJuheStatus() {
    return juheStatus;
  }

  public boolean isJuheAreaActive() {
    return JuheStatus.ACTIVE == juheStatus;
  }

  public void setJuheStatus(JuheStatus juheStatus) {
    this.juheStatus = juheStatus;
  }

  public void setJuheStatus(boolean contains) {
    this.setJuheStatus(contains ? JuheStatus.ACTIVE : JuheStatus.IN_ACTIVE);
  }
}
