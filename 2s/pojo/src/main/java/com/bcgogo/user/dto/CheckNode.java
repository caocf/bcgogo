package com.bcgogo.user.dto;

import com.bcgogo.user.dto.permission.ModuleDTO;
import com.bcgogo.user.dto.permission.RoleDTO;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-11-26
 * Time: 下午12:43
 * tree Check
 */
public class CheckNode extends Node {
  private Boolean checked = false;
  private Boolean expanded = false;

  public void reBuildTreeForChecked() {
    this.setChecked(this.hasThisNode());
    if (this.hasChildren()) {
      for (Node child : this.getChildren()) {
        child.reBuildTreeForChecked();
      }
    }
  }

  //base on all children are checked
  public boolean hasThisNode() {
    if (!this.hasChildren()) {
      return this.getChecked() == null ? false : this.getChecked();
    }
    for (Node child : this.getChildren()) {
      if (!child.hasThisNode()) {
        return false;
      }
    }
    return true;
  }

  public Boolean getChecked() {
    return checked;
  }

  public void setChecked(Boolean checked) {
    this.checked = checked;
  }

  public Boolean getExpanded() {
    return expanded;
  }

  public void setExpanded(Boolean expanded) {
    this.expanded = expanded;
  }
}
