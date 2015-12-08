package com.bcgogo.api.controller;

import com.bcgogo.api.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-11-4
 * Time: 下午5:02
 */
@Controller
public class MallProductController {
  private static final Logger LOG = LoggerFactory.getLogger(MallProductController.class);

  @ResponseBody
  @RequestMapping(value = "/mall/product/list", method = RequestMethod.POST)
  public ApiResponse collect(HttpServletRequest request, HttpServletResponse response) {
    return null;
  }

}
