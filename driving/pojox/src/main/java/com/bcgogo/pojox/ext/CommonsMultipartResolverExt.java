package com.bcgogo.pojox.ext;

/**
 * 扩展spring的CommonsMultipartResolver
 *  增加文件上传进度检测程序
 * Author: ndong
 * Date: 2015-4-21
 * Time: 09:10
 */

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class CommonsMultipartResolverExt extends CommonsMultipartResolver {
  @Override
  protected MultipartParsingResult parseRequest(HttpServletRequest request)
    throws MultipartException {
    String[] params = request.getRequestURL().toString().split("/");
    String uuid = params[params.length-1];
    FileUploadListener listener = new FileUploadListener(uuid);
    String encoding = determineEncoding(request);
    FileUpload fileUpload = prepareFileUpload(encoding);
    fileUpload.setProgressListener(listener);
    try {
      List<FileItem> fileItems = ((ServletFileUpload) fileUpload).parseRequest(request);
      return parseFileItems(fileItems, encoding);
    } catch (FileUploadBase.SizeLimitExceededException ex) {
      throw new MaxUploadSizeExceededException(fileUpload.getSizeMax(), ex);
    } catch (FileUploadException ex) {
      throw new MultipartException("Could not parse multipart servlet request", ex);
    }
  }
}
