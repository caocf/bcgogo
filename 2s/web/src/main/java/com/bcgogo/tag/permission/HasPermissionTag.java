package com.bcgogo.tag.permission;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-12-14
 * Time: 上午10:25
 */
public class HasPermissionTag extends AbstractPermissionTag {

  public HasPermissionTag() {
    super();
    init();
  }

  // resets internal state
  private void init() {
    this.setPermissions(null);
    this.setResourceType(null);
  }

  @Override
  public void release() {
    super.release();
    this.setPermissions(null);
  }

}
