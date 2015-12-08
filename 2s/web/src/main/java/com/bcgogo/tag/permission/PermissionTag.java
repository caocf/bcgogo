package com.bcgogo.tag.permission;

import org.apache.taglibs.standard.resources.Resources;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-12-14
 * Time: 下午1:15
 */
public class PermissionTag extends TagSupport {
  public PermissionTag() {
    super();
    init();
  }

  // Releases any resources we may have (or inherit)
  public void release() {
    super.release();
    init();
  }


  //*********************************************************************
  // Private state

  private boolean subtagGateClosed;      // has one subtag already executed?


  //*********************************************************************
  // Public methods implementing exclusivity checks

  /**
   * Returns status indicating whether a subtag should run or not.
   *
   * @return <tt>true</tt> if the subtag should evaluate its condition
   *         and decide whether to run, <tt>false</tt> otherwise.
   */
  public synchronized boolean gainPermission() {
    return (!subtagGateClosed);
  }

  /**
   * Called by a subtag to indicate that it plans to evaluate its
   * body.
   */
  public synchronized void subtagSucceeded() {
    if (subtagGateClosed)
      throw new IllegalStateException(
          Resources.getMessage("CHOOSE_EXCLUSIVITY"));
    subtagGateClosed = true;
  }


  //*********************************************************************
  // Tag logic

  // always include body
  public int doStartTag() throws JspException {
    subtagGateClosed = false;  // when we start, no children have run
    return EVAL_BODY_INCLUDE;
  }


  //*********************************************************************
  // Private utility methods

  private void init() {
    subtagGateClosed = false;                          // reset flag
  }
}
