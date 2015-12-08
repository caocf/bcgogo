package com.bcgogo.util;

import com.bcgogo.pojo.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.*;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.PrinterResolution;
import javax.swing.*;
import java.awt.print.Printable;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 * Author: ndong
 * Date: 2015-2-11
 * Time: 09:49
 */
public class PrintHelper {

  private static final Logger logger = LoggerFactory.getLogger(PrintHelper.class);

  public static boolean isSerial(String serialNo) {
    return StringUtil.isAlphaOrDigital(serialNo) && serialNo.length() == 16;
  }



  /**
   * 查找打印机
   *
   * @param flavor
   * @param attrs
   * @return
   */
  private static PrintService getPrintService(DocFlavor flavor, PrintRequestAttributeSet attrs) throws Exception {
    String serverName = ConfigUtil.readOutPropertyFile("PRINT.SERVER.NAME");
    if (StringUtil.isEmpty(serverName)) {
      throw new Exception("请配置打印机名称。");
    }
    PrintService services[] = PrintServiceLookup.lookupPrintServices(flavor, attrs);
    if (services.length == 0) {
      throw new Exception("未查找到任何打印服务。");
    }
    logger.info("search service list...");
    PrintService service = null;
    for (int i = 0; i < services.length; i++) {
      String printerName=services[i].getName();
      logger.info("{})service name : {}: ", i, printerName);
      if (printerName.contains(serverName)) {
        service = services[i];
        logger.info("find the right printer:{}", printerName);
        break;
      }
    }
    if (StringUtil.isEmpty(serverName)) {
      throw new Exception("请检查配置的打印机名称是否正确。");
    }
    return service;
  }

  /**
   * 根据 url 打印
   *
   * @param url
   * @return
   * @throws IOException
   */
  public static Result printHtml(String url) throws Exception {
    if (StringUtil.isEmpty(url)) return new Result(false, "url can't be null");
    logger.info("print url:{}",url);
    JEditorPane editorPane = new JEditorPane();
    editorPane.setEditable(false);
    editorPane.setSize(200, 400);
    editorPane.setPage(url);
    Printable printable = editorPane.getPrintable(null, null);
    return doPrint(printable);
  }


  public static Result doPrint(Printable printable) throws Exception {
    DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
    PrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();
    DocAttributeSet dAttrs = new HashDocAttributeSet();
    MediaPrintableArea area = new MediaPrintableArea(0, 0, 150, 150, MediaPrintableArea.MM);
    dAttrs.add(area);
    attrs.add(area);
    PrinterResolution printerResolution = new PrinterResolution(200, 200, PrinterResolution.DPI);
    dAttrs.add(printerResolution);

    Doc doc = new SimpleDoc(printable, flavor, dAttrs);
    PrintService service = getPrintService(flavor, null);
    if (service == null) {
      logger.error("can't find printer");
      return new Result(false, "can't find printer");
    }
    DocPrintJob job = service.createPrintJob();
    try {
      job.print(doc, attrs); // 进行每一页的具体打印操作
    } catch (PrintException pe) {
      pe.printStackTrace();
    }
    return new Result(true,"打印成功。");
  }


  /**
   * 根据文件流打印
   *
   * @param fileName
   * @return
   * @throws PrintException
   * @throws IOException
   * @sample String fileName = "D:\\test.txt";
   */
  public static Result doPrint(String fileName) throws Exception {
    DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
    PrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();
    PrintService service = getPrintService(flavor, attrs);
    if (service == null) {
      logger.error("can't find printer");
      return new Result(false, "can't find printer");
    }
    DocPrintJob job = service.createPrintJob();
    DocAttributeSet dAttrs = new HashDocAttributeSet();
    MediaPrintableArea area = new MediaPrintableArea(0, 0, 10, 10, MediaPrintableArea.MM);
    dAttrs.add(area);
    FileInputStream stream = null;
    try {
      stream = new FileInputStream(fileName);
      Doc doc = new SimpleDoc(stream, flavor, dAttrs);
      job.print(doc, attrs);
    } finally {
      stream.close();
    }
    return new Result();
  }
  public static void main(String[]args) throws Exception {
    String url="http://www.baidu.com";
    PrintHelper.printHtml(url);
  }

}
