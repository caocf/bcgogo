package com.bcgogo.tag;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.ShopConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;
import javax.servlet.jsp.tagext.JspTag;

/**
 * User: ZhangJuntao
 * Date: 13-6-6
 * Time: 上午10:55
 */
public class HasConfig extends ConditionalTagSupport {
  private static final Logger LOG = LoggerFactory.getLogger(HasConfig.class);
  private String configValue;
  private String expectResult;


  public HasConfig() {
    super();
    init();
  }

  private void init() {
    this.setConfigValue(null);
    this.setExpectResult(null);
  }

  @Override
  protected boolean condition() throws JspTagException {
    return expectResult.equals(ServiceManager.getService(IConfigService.class)
        .getConfig(configValue, ShopConstant.BC_SHOP_ID));
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  public String getConfigValue() {
    return configValue;
  }

  public void setConfigValue(String configValue) {
    this.configValue = configValue;
  }

  public String getExpectResult() {
    return expectResult;
  }

  public void setExpectResult(String expectResult) {
    this.expectResult = expectResult;
  }
}
