package com.bcgogo.exception;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-2-15
 * Time: 下午3:30
 * To change this template use File | Settings | File Templates.
 */
public class PageException extends BcgogoException {
     public PageException(){
         super();
     }

    public PageException(String message){
        super(message);
    }
}
