package com.community.exception;

/**
 * 组合商品上架失败异常
 */
public class ProductBundleEnableFailedException extends BaseException {

    public ProductBundleEnableFailedException(){}

    public ProductBundleEnableFailedException(String msg){
        super(msg);
    }
}
