package com.tonggou.andclient.parse;

/**
 * 解析的基类,所以解析类都应继承它
 * @author think
 *
 */
public class TonggouBaseParser implements JSONParseInterface{
	 public boolean parseSuccessfull = false;    //解析是否成功
	  public String errorMessage = "";           //错误提示
	
	@Override
	public void parsing(String dataFormServer) {
		// TODO Auto-generated method stub
	}

	public boolean isSuccessfull() {
		return parseSuccessfull;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

}
