package com.tonggou.lib.net.parser;

import com.tonggou.lib.net.response.BaseResponse;

/**
 * 加载本地缓存的异步网络请求基础解析处理器
 * 
 * <p>当无网络时，会调用 {@link #onLoadCache(T)}, 其他的网络请求生命周期方法<b>不会调用</b>
 * @author lwz
 *
 * @param <T>
 */
public abstract class AsyncLoadCacheJsonBaseResponseParseHandler<T extends BaseResponse> 
								extends AsyncLoadCacheJsonResponseParseHandler<T> {

	@Override
	public IResponseParser<T> getJsonResponseParser() {
		return new BaseResponseParser<T>(getTypeClass());
	}
	
	public abstract Class<T> getTypeClass();
	
}
