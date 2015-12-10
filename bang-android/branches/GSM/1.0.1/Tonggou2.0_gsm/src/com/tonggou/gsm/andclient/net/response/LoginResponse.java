
package com.tonggou.gsm.andclient.net.response;

import com.google.gson.annotations.Expose;
import com.tonggou.gsm.andclient.bean.AppShopDTO;
import com.tonggou.gsm.andclient.bean.AppUserDTO;
import com.tonggou.gsm.andclient.bean.AppVehicleDTO;

/**
 * 登录
 * @author lwz
 *
 */
public class LoginResponse extends BaseResponse {

	private static final long serialVersionUID = -4482683702411363116L;
	
	@Expose
	private String imei;
	@Expose
    private Object appConfig;
    @Expose
    private Object appUserConfig;
    @Expose
    private AppShopDTO appShopDTO;
    @Expose
    private AppUserDTO appUserDTO;
    @Expose
    private AppVehicleDTO appVehicleDTO;

    public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

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

    public AppShopDTO getAppShopDTO() {
		return appShopDTO;
	}

	public void setAppShopDTO(AppShopDTO appShopDTO) {
		this.appShopDTO = appShopDTO;
	}

	public AppUserDTO getAppUserDTO() {
        return appUserDTO;
    }

    public void setAppUserDTO(AppUserDTO appUserDTO) {
        this.appUserDTO = appUserDTO;
    }

    public AppVehicleDTO getAppVehicleDTO() {
        return appVehicleDTO;
    }

    public void setAppVehicleDTO(AppVehicleDTO appVehicleDTO) {
        this.appVehicleDTO = appVehicleDTO;
    }
}
