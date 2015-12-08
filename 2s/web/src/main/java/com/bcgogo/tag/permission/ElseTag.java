package com.bcgogo.tag.permission;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;
import javax.servlet.jsp.tagext.Tag;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-12-14
 * Time: 下午1:18
 */
public class ElseTag extends ConditionalTagSupport {

  /*
  * <otherwise> is just a <when> that always tries to evaluate its body
  * if it has permission from its parent tag.
  */

  // Don't let the condition stop us... :-)
  protected boolean condition() {
    return true;
  }

  public int doStartTag() throws JspException {

    Tag parent;

    // make sure we're contained properly
    if (!((parent = getParent()) instanceof PermissionTag))
      throw new JspTagException("ElseTag parent is not permissionTag Exception");

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
}
