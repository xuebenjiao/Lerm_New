package com.dcyx.app.global.config.enums;

/**
 * Created by xue on 2018/10/24.
 * 出库计划中跳转的类型
 */

public enum StockOutPlanJumpType {
    RETURN("返回",0),NEW("新建",1),UPDATE("修改",2),STOCK_OUT("出库",3);
    public String type;
    public int typeCode;
    private StockOutPlanJumpType(String type,int typeCode){
        this.type  = type;
        this.typeCode = typeCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(int typeCode) {
        this.typeCode = typeCode;
    }
}
