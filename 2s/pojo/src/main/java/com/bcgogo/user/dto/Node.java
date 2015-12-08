package com.bcgogo.user.dto;

import com.bcgogo.enums.SystemType;
import com.bcgogo.enums.user.Status;
import com.bcgogo.user.dto.permission.ModuleDTO;
import com.bcgogo.user.dto.permission.RoleDTO;
import com.bcgogo.enums.Product.ProductCategoryStatus;
import com.bcgogo.enums.Product.ProductCategoryType;
import com.bcgogo.product.ProductCategory.ProductCategoryDTO;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-11-26
 * Time: 下午12:43
 * tree node
 */
public class Node {
  private Long id;
  private String idStr;
  private String text;        //tree name
  private String value;
  private String name;
  private String description; //描述
  private Type type;
  private SystemType systemType;
  private String iconCls;   //css class
  private boolean allowDrop = true;
  private boolean allowDrag = true;
  private Long sort;        //同级顺序
  private Long parentId;
  private String parentIdStr;
  private Long shopId;
  private Boolean leaf;
  private Boolean hasThisNode;
  private List<Node> children = new ArrayList<Node>();
  private int childSize;
  private Integer level;



  public enum Type {
    //部门-职位
    DEPARTMENT,
    OCCUPATION,
    //模块-角色
    MODULE,
    ROLE,
    TOP_CATEGORY,
    FIRST_CATEGORY,
    SECOND_CATEGORY,
    THIRD_CATEGORY,
    RECOMMEND_SHOP
  }

  public DepartmentDTO toDepartmentDTO() {
    DepartmentDTO dto = new DepartmentDTO();
    dto.setId(this.getId());
    dto.setName(this.getText());
    dto.setParentId(this.getParentId());
    dto.setShopId(this.getShopId());
    return dto;
  }

  public OccupationDTO toOccupationDTO() {
    OccupationDTO dto = new OccupationDTO();
    dto.setId(this.getId());
    dto.setName(this.getText());
    dto.setDepartmentId(this.getParentId());
    dto.setShopId(this.getShopId());
    return dto;
  }

  public ModuleDTO toModuleDTO() {
    ModuleDTO dto = new ModuleDTO();
    dto.setId(this.getId());
    dto.setValue(this.getValue());
    dto.setName(this.getName());
    dto.setParentId(this.getParentId());
    dto.setType(this.getSystemType());
    dto.setSort(this.getSort());
    return dto;
  }

  public RoleDTO toRoleDTO() {
    RoleDTO dto = new RoleDTO();
    dto.setId(this.getId());
    dto.setValue(this.getValue());
    dto.setModuleId(this.getParentId());
    dto.setType(this.getSystemType());
    dto.setName(this.getName());
    dto.setStatus(Status.active.name());
    dto.setSort(this.getSort());
    return dto;
  }

  public ProductCategoryDTO toProductCategoryDTO() {
    ProductCategoryDTO productCategoryDTO = new ProductCategoryDTO();
    productCategoryDTO.setId(this.getId());
    productCategoryDTO.setName(this.getText());
    productCategoryDTO.setParentId(this.getParentId());
    productCategoryDTO.setShopId(this.getShopId());
    productCategoryDTO.setStatus(ProductCategoryStatus.ENABLED);
    if (this.getType().toString().equals(String.valueOf(Node.Type.TOP_CATEGORY))) {
      productCategoryDTO.setCategoryType(ProductCategoryType.TOP_CATEGORY);
    } else if (this.getType().toString().equals(String.valueOf(Node.Type.FIRST_CATEGORY))) {
      productCategoryDTO.setCategoryType(ProductCategoryType.FIRST_CATEGORY);
    } else if (this.getType().toString().equals(String.valueOf(Node.Type.SECOND_CATEGORY))) {
      productCategoryDTO.setCategoryType(ProductCategoryType.SECOND_CATEGORY);
    } else if (this.getType().toString().equals(String.valueOf(Node.Type.THIRD_CATEGORY))) {
      productCategoryDTO.setCategoryType(ProductCategoryType.THIRD_CATEGORY);
    }
    return productCategoryDTO;
  }

  public void findNodeBySearchWord(String searchWord,Node node,List<Node> result){
    if(node.getText().indexOf(searchWord)>-1){
      result.add(node);
      node.getAllChildren(result);
    } else if (node.hasChildren()) {
      for (Node n : node.getChildren()) {
        findNodeBySearchWord(searchWord,n,result);
      }
    }
  }



  //创建各节点的树状关系（递归方式设置各节点的子节点）
  public void mergeAndBuildTree(Node node, List<Node> nodeList) {
    this.filterChildren(nodeList);
    for (Node dto : this.getChildren()) {
      //department tree or module tree
      if (dto.getType() == Type.DEPARTMENT || dto.getType() == Type.MODULE)
        dto.mergeAndBuildTree(dto, nodeList);
    }
  }

  //创建各节点的树状关系（递归方式设置各节点的子节点）
  public void buildTree(Node node, List<Node> nodeList) {
    this.filterChildren(nodeList);
    for (Node dto : this.getChildren()) {
      dto.buildTree(dto, nodeList);
    }
  }

  public void buildTree(List<Node> nodeList) {
    this.filterChildren(nodeList);
    for (Node dto : this.getChildren()) {
      dto.buildTree(nodeList);
    }
  }

  //根据列表设置当前节点的子节点
  private void filterChildren(List<Node> nodeList) {
    Iterator iterator = nodeList.iterator();
    Node node;
    while (iterator.hasNext()) {
      node = (Node) iterator.next();
      if (NumberUtil.isEqual(node.getParentId(), this.getId())) {
        this.getChildren().add(node);
      }
    }
  }

  //获得 node tree 下所有的 child
  public void getAllChildren(List<Node> children) {
    if (!this.hasChildren()) {
      children.add(this);
      return;
    }
    for (Node n : this.getChildren()) {
      n.getAllChildren(children);
      if (n.hasChildren())
        children.add(n);
    }
  }

  //根据id获得 该 node tree中的node
  public Node findNodeInTree(Long id) {
    if (NumberUtil.isEqual(id, this.getId())) {
      return this;
    } else if (this.hasChildren()) {
      Node needNode;
      for (Node n : this.getChildren()) {
        needNode = n.findNodeInTree(id);
        if (needNode != null) return needNode;
      }
    }
    return null;
  }

  //rebuild the tree in the node(checked)
  public void reBuildTreeForChecked() {
    this.setHasThisNode(this.hasThisNode());
    if (this.hasChildren()) {
      for (Node child : this.getChildren()) {
        child.reBuildTreeForChecked();
      }
    }
  }

  //rebuild the tree in the node(sort)
  public void reBuildTreeForSort() {
    if (this.hasChildren()) {
      this.sortChildren();
      for (Node child : this.getChildren()) {
        child.reBuildTreeForSort();
      }
    }
  }

  //rebuild the tree in the node to remove module
  public void reBuildTreeForRemoveEmptyModule() {
    if (this.hasChildren()) {
      Iterator<Node> iterator = this.getChildren().iterator();
      while (iterator.hasNext()) {
        Node child = iterator.next();
        if (Type.MODULE.equals(child.getType())) {
          if (child.hasChildren()) {
            child.reBuildTreeForRemoveEmptyModule();
          }
          if (!child.hasChildren()) {
            iterator.remove();
          }
        }
      }
    }
  }




  //base on all children are checked
  public boolean hasThisNode() {
    if (!this.hasChildren()) {
      return this.getHasThisNode() == null ? false : this.getHasThisNode();
    }
    for (Node child : this.getChildren()) {
      if (!child.hasThisNode()) {
        return false;
      }
    }
    return true;
  }

  //排序
  public List<Node> sortChildren() {
    Collections.sort(this.getChildren(), new Comparator<Node>() {
      @Override
      public int compare(Node n1, Node n2) {
        if (n1.getSort() == null) {
          return -1;
        }
        if (n2.getSort() == null) {
          return 1;
        }
        try {
          return n1.getSort().compareTo(n2.getSort());
        } catch (Exception e) {
          return -1;
        }
      }
    });
    return children;
  }

  public Integer getLevel() {
    return level;
  }

  public void setLevel(Integer level) {
    this.level = level;
  }

  public boolean isAllowDrop() {
    return allowDrop;
  }

  public void setAllowDrop(boolean allowDrop) {
    this.allowDrop = allowDrop;
  }

  public boolean hasChildren() {
    return CollectionUtils.isNotEmpty(children);
  }

  public Boolean getHasThisNode() {
    return hasThisNode;
  }

  public void setHasThisNode(Boolean hasThisNode) {
    this.hasThisNode = hasThisNode;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public String getParentIdStr() {
    return parentIdStr;
  }

  public void setParentIdStr(String parentIdStr) {
    this.parentIdStr = parentIdStr;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    if (id != null) {
      this.setIdStr(id.toString());
    }
    this.id = id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public String getIconCls() {
    return iconCls;
  }

  public void setIconCls(String iconCls) {
    this.iconCls = iconCls;
  }

  public Long getSort() {
    return sort;
  }

  public void setSort(Long sort) {
    this.sort = sort;
  }

  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    if (parentId != null) {
      this.setParentIdStr(parentId.toString());
    }
    this.parentId = parentId;
  }

  public Boolean getLeaf() {
    return leaf;
  }

  public void setLeaf(Boolean leaf) {
    this.leaf = leaf;
  }

  public List<Node> getChildren() {
    return children;
  }

  public void setChildren(List<Node> children) {
    this.children = children;
  }

  public int getChildSize() {
    return childSize;
  }

  public void setChildSize(int childSize) {
    this.childSize = childSize;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public SystemType getSystemType() {
    return systemType;
  }

  public void setSystemType(SystemType systemType) {
    this.systemType = systemType;
  }

  public boolean isAllowDrag() {
    return allowDrag;
  }

  public void setAllowDrag(boolean allowDrag) {
    this.allowDrag = allowDrag;
  }


  public boolean containProductCategory(Node node){
    if(node.getType() == Type.TOP_CATEGORY || node.getType() == Type.FIRST_CATEGORY || node.getType() == Type.SECOND_CATEGORY || node.getType() == Type.THIRD_CATEGORY){
      return true;
    }
    return false;
  }

  /**
   * shopProductCategoryIdSet（second node） 会优先排在 前面
   * @param shopProductCategoryIdSet
   */
  public void resetProductCategoryNodeSortByShopProductCategoryIdSet(Set<Long> shopProductCategoryIdSet) {
    if(CollectionUtils.isEmpty(shopProductCategoryIdSet)) return;
    if (this.hasChildren()) {
      for (Node child : this.getChildren()) {
        if(shopProductCategoryIdSet.contains(child.getId())){
          child.setSort(1l);
        }else{
          child.setSort(0l);
        }
        child.resetProductCategoryNodeSortByShopProductCategoryIdSet(shopProductCategoryIdSet);
      }
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Node that = (Node) o;

    if (id != null ? !id.equals(that.id) : that.id != null) return false;

    return true;
  }
}
