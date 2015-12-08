package com.bcgogo.generator;

import com.bcgogo.exception.BcgogoException;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-2-17
 * Time: 上午10:38
 * To change this template use File | Settings | File Templates.
 */
public class GeneratorException extends BcgogoException {

    public  GeneratorException(){
        super();
    }

    public GeneratorException(Exception e){
        super(e);
    }

    public GeneratorException(String message){
        super(message);
    }
}
