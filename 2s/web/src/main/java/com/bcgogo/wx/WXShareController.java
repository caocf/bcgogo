package com.bcgogo.wx;

import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.app.IWXShareService;
import com.bcgogo.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/12/4
 * Time: 11:41.
 */
@Controller
public class WXShareController {

    private static final Logger LOG = LoggerFactory.getLogger(WXShareController.class);

    @RequestMapping(value = "wxShare",method = RequestMethod.GET)
    public String wxShare(ModelMap model , HttpServletRequest request , HttpServletResponse response , @RequestParam String appUserNo){
        WXShareDTO shareDTO = null;
        try {
            IWXShareService shareService = ServiceManager.getService(IWXShareService.class);
            shareDTO = shareService.shareInfo(appUserNo);

        }catch (Exception e){
            LOG.error(e.getMessage());
        }
        model.addAttribute("wxShareInfo",shareDTO);
        return "wxShare";
    }
}
