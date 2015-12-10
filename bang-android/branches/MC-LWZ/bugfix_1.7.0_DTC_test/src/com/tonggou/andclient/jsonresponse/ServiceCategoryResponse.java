package com.tonggou.andclient.jsonresponse;

import java.util.List;
import com.tonggou.andclient.vo.ServiceCategoryDTO;

/**
 * 服务类目请求
 * @author lwz
 *
 */
public class ServiceCategoryResponse extends BaseResponse {
	
	private static final long serialVersionUID = 3249737226296877934L;
	
	private List<ServiceCategoryDTO>  serviceCategoryDTOList;

	public List<ServiceCategoryDTO> getServiceCategoryDTOList() {
		return serviceCategoryDTOList;
	}
	public void setServiceCategoryDTOList(
			List<ServiceCategoryDTO> serviceCategoryDTOList) {
		this.serviceCategoryDTOList = serviceCategoryDTOList;
	}
}
