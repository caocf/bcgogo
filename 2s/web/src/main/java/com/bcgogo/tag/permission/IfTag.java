package com.bcgogo.tag.permission;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.Tag;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-12-14
 * Time: 下午1:16
 */
public class IfTag extends AbstractPermissionTag {

  // initialize inherited and local state
  public IfTag() {
    super();
    init();
  }

  public int doStartTag() throws JspException {

    Tag parent;

    // make sure we're contained properly
    if (!((parent = getParent()) instanceof PermissionTag))
      throw new JspTagException("IfTag parent is not permissionTag Exception");

    // make sure our parent wants us to continue
    if (!((PermissionTag) parent).gainPermission())
      return SKIP_BODY;                   // we've been reeled in

    // handle conditional behavior
    if (condition()) {
      ((PermissionTag) parent).subtagSucceeded();
      return EVAL_BODY_INCLUDE;
    } else
      return SKIP_BODY;
  }


  // Releases any resources we may have (or inherit)
  public void release() {
    super.release();
    init();
  }

  // resets internal state
  private void init() {
    this.setPermissions(null);
    this.setResourceType(null);
  }
}
