package com.bcgogo.download;

import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.ExportFileDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IExportService;
import com.bcgogo.enums.download.FileFormat;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.ShopConstant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 13-2-4
 * Time: 上午9:42
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/download.do")
public class DownloadController {
  private static final Logger LOG = LoggerFactory.getLogger(DownloadController.class);

  /**
   *
   * @param request
   * @param response
   * @param fileName     要导出的文件名
   * @param relativePath 相对路径
   * @throws Exception
   */
  @RequestMapping(params = "method=downloadStaticFile")
  public void download(HttpServletRequest request,HttpServletResponse response,String fileName,String relativePath) throws Exception{

    Long shopId = WebUtil.getShopId(request);

    if(StringUtils.isBlank(relativePath))
    {
      return;
    }

    if(StringUtils.isBlank(fileName))
    {
      fileName = "下载的文件";
    }

    String[] str = relativePath.split("\\.");

    String format = str[str.length-1];

    fileName += "."+format;

    String ctxPath = request.getSession().getServletContext()
        .getRealPath("/");

    String downLoadPath = ctxPath + relativePath ;

    File file = new File(downLoadPath);

    if(!file.exists())
    {
      return;
    }

    long fileLength = file.length();

    BufferedInputStream bis = null;
    BufferedOutputStream bos = null;

    try{
      if(FileFormat.xls.toString().equals(format))
      {
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
      }
      if(FileFormat.rar.toString().equals(format))
      {
        response.setContentType("application/x-tar;charset=UTF-8");
      }
      response.setHeader("Content-disposition", "attachment; filename=" + new String(fileName.getBytes("gb2312"), "ISO8859-1" ) );
      response.setHeader("Content-Length", String.valueOf(fileLength));

      bis = new BufferedInputStream(new FileInputStream(downLoadPath));
      bos = new BufferedOutputStream(response.getOutputStream());
      byte[] buff = new byte[1024];
      int bytesRead;
      while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
        bos.write(buff, 0, bytesRead);
      }
    }catch (Exception e){
      LOG.error("method=downloadStaticFile");
      LOG.error("shopId="+shopId.toString());
      LOG.error("relativePath="+relativePath);
      LOG.error(e.getMessage(),e);
    }finally {
      bis.close();
      bos.close();
    }
  }

  @RequestMapping(params = "method=downloadExportFile")
  public void downloadExportFile(HttpServletRequest request,HttpServletResponse response) throws Exception{
      BufferedInputStream bis = null;
      BufferedOutputStream bos = null;
      String path = ServiceManager.getService(IConfigService.class).getConfig("ExportFileDir", ShopConstant.BC_SHOP_ID);
      String exportFileName = request.getParameter("exportFileName");
      String exportFileIdStr = request.getParameter("exportFileId");
      if(StringUtils.isEmpty(exportFileIdStr)) {
          return;
      }
      try {
          //设置成文件格式
          response.setContentType("application/vnd.ms-excel");
          response.setHeader("Content-disposition","attachment;filename=" + new String(exportFileName.getBytes("UTF-8"),"iso-8859-1"));
          ExportFileDTO exportFileDTO = ServiceManager.getService(IExportService.class).getExportFileDTOById(Long.valueOf(exportFileIdStr));
          if(exportFileDTO != null) {
              String fileFullName = path + exportFileDTO.getFileName();
              bis = new BufferedInputStream(new FileInputStream(fileFullName));
              bos = new BufferedOutputStream(response.getOutputStream());
              byte[] buff = new byte[1024];
              int bytesRead;
              while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                  bos.write(buff, 0, bytesRead);
              }
              ServiceManager.getService(IExportService.class).updateExportFileById(Long.valueOf(exportFileIdStr));
          }
      } catch (Exception e) {
          LOG.error("下载文件" + exportFileName + "失败");
          LOG.error(e.getMessage(),e);
      } finally {
          bis.close();
          bos.close();
      }


  }
}
