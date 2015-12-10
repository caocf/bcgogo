
package com.tonggou.gsm.andclient.net.response;

/**
 * 主动拍照响应结果
 * @author peter
 *
 */
public class QueryMonitorPictureResponse extends BaseResponse {

	private static final long serialVersionUID = 6240883855666792423L;

	private int data;

	public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }
}