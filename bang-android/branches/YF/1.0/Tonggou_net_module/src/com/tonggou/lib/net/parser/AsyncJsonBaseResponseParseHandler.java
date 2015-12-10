package com.tonggou.lib.net.parser;

import com.tonggou.lib.net.response.BaseResponse;

/**
 * 异步网络请求通用解析处理器
 * <p> 请求是异步的，JSON解析是同步的
 * <p> 该类是对 {@link AsyncJsonResponseParseHandler} 类的扩展，在其的 OnSuccess 方法中进行 JSON 解析
 * @author lwz
 *
 * @param <T>
 */
public abstract class AsyncJsonBaseResponseParseHandler<T extends BaseResponse> extends AsyncJsonResponseParseHandler<T> {
	
	@Override
	public IResponseParser<T> getJsonResponseParser() {
		return new BaseResponseParser<T>(getTypeClass());
	}
	
	public abstract Class<T> getTypeClass();
	
}
