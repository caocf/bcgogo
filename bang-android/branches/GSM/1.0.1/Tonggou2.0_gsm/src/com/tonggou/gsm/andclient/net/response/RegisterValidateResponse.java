
package com.tonggou.gsm.andclient.net.response;

import com.google.gson.annotations.Expose;
import com.tonggou.gsm.andclient.bean.AppVehicleDTO;

/**
 * 验证注册响应
 * @author lwz
 *
 */
public class RegisterValidateResponse extends BaseResponse {

	private static final long serialVersionUID = -8519086594912529157L;
	
	@Expose
    private Object appConfig;
    @Expose
    private Object appUserConfig;
    @Expose
    private Object shopDTO;
    @Expose
    private Object appUserDTO;
    @Expose
    private AppVehicleDTO appVehicleDTO;

    public Object getAppConfig() {
        return appConfig;
    }

    public void setAppConfig(Object appConfig) {
        this.appConfig = appConfig;
    }

    public Object getAppUserConfig() {
        return appUserConfig;
    }

    public void setAppUserConfig(Object appUserConfig) {
        this.appUserConfig = appUserConfig;
    }

    public Object getShopDTO() {
        return shopDTO;
    }

    public void setShopDTO(Object shopDTO) {
        this.shopDTO = shopDTO;
    }

    public Object getAppUserDTO() {
        return appUserDTO;
    }

    public void setAppUserDTO(Object appUserDTO) {
        this.appUserDTO = appUserDTO;
    }

    public AppVehicleDTO getAppVehicleDTO() {
        return appVehicleDTO;
    }

    public void setAppVehicleDTO(AppVehicleDTO appVehicleDTO) {
        this.appVehicleDTO = appVehicleDTO;
    }

}
