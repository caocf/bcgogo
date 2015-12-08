package com.bcgogo.common;

import com.bcgogo.txn.dto.PrintTemplateDTO;
import com.bcgogo.utils.ArrayUtil;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-3-19
 * Time: 上午5:59
 * To change this template use File | Settings | File Templates.
 */
public class PrintHelper {

  private static VelocityEngine vEngine = null;

  public static VelocityEngine getEngine() throws Exception {
    if(vEngine!=null){
      return vEngine;
    }else {
      vEngine=new VelocityEngine();
      vEngine.setProperty(VelocityEngine.RESOURCE_LOADER, "string");
      vEngine.setProperty("string.resource.loader.class", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
      vEngine.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
      vEngine.setProperty("runtime.log.logsystem.log4j.category", "velocity");
      vEngine.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
      vEngine.init();
      return vEngine;

    }
  }


  public static void generatePrintPage(HttpServletResponse response, byte[] templateHtml, String templateName, VelocityContext context) throws Exception {
      PrintWriter out = response.getWriter();
      response.setContentType("text/html");
      response.setCharacterEncoding("UTF-8");
      try {
        if (ArrayUtil.isEmpty(templateHtml)||StringUtil.isEmpty(templateName)) {
          out.print("<html><head><title></title></head><body>没有可用的模板</body><html>");
          return;
        }
        //初始化并取得Velocity引擎
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(VelocityEngine.RESOURCE_LOADER, "string");
        ve.setProperty("string.resource.loader.class", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
        ve.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
        ve.setProperty("runtime.log.logsystem.log4j.category", "velocity");
        ve.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
        ve.init();
        //创建资源库
        StringResourceRepository repo = StringResourceLoader.getRepository();
        String myTemplate = new String(templateHtml, "UTF-8");
        //模板资源存放 资源库 中
        repo.putStringResource(templateName, myTemplate);
        //取得velocity的模版
        Template t = ve.getTemplate(templateName, "UTF-8");
        //输出流
        StringWriter writer = new StringWriter();
        //转换输出
        t.merge(context, writer);
        out.print(writer);
        writer.close();
      } finally {
        out.close();
      }

    }

}
