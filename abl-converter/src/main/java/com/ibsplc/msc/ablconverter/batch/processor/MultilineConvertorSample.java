package com.ibsplc.msc.ablconverter.batch.processor;

/**
 * Created by maghesh on 22/05/2016.
 */
public class MultilineConvertorSample extends  BaseConverter {
    @Override
    protected String convert(String code) {
        if(code.startsWith("1")){
            code = "6\n7\n8\n9";
        }
        return code ;
    }
}
