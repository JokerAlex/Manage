package com.dzhy.manage.constants;

/**
 * @ClassName Constants
 * @Description 进度阶段常量值
 * @Author alex
 * @Date 2018/10/31
 **/
public final class Constants {

    private Constants() {}

    /**
     * 表头
     */
    public static final String PRODUCE_TITLE = "进度报表";
    public static final String OUTPUT_TITLE = "产值报表";

    /**
     * 产品表列名称
     */

    public static final String PRICE = "价格";

    public static final String COMMENT = "备注";

    /**
     * 进度表列名称
     */
    public static final String PRODUCT_NAME = "产品名称";

    public static final String XIA_DAN = "下单";

    public static final String XIA_DAN_COMMENT = "下单备注";

    public static final String MU_GONG = "木工";

    public static final String MU_GONG_COMMENT = "木工总价值";

    public static final String YOU_FANG = "油房";

    public static final String YOU_FANG_COMMENT = "油房总价值";

    public static final String BAO_ZHUANG = "包装";

    public static final String BAO_ZHUANG_COMMENT = "包装总价值";

    public static final String TE_DING = "特定";

    public static final String TE_DING_COMMENT = "特定总价值";

    public static final String BEI_JING = "北京";

    public static final String BEI_JING_COMMENT = "北京总价值";

    public static final String BEI_JING_TE_DING = "北京特定";

    public static final String BEI_JING_TE_DING_COMMENT = "北京特定总价值";

    public static final String BEN_DI_HE_TONG = "本地合同";

    public static final String BEN_DI_HE_TONG_COMMENT = "本地合同总价值";

    public static final String WAI_DI_HE_TONG = "外地合同";

    public static final String WAI_DI_HE_TONG_COMMENT = "外地合同总价值";

    //public static final String DENG = "等";

    //public static final String DENG_COMMENT = "等备注";

    public static final int IS_OUTPUT = 1;

    public static final int NOT_OUTPUT = 0;


    /**
     * 产值表列名
     */

    public static final String BAOZHUNAG_TOTAL_PRICE = "包装总金额";

    public static final String TEDING_TOTAL_PRICE = "特定总金额";

    public static final String MU_GONG_TOTAL_PRICE = "木工总金额";

    public static final String YOU_FANG_TOTAL_PRICE = "油房总金额";

    public static final String BEI_JING_INPUT = "北京入库";

    public static final String BEI_JING_INPUT_TOTAL_PRICE = "北京入库总金额";

    public static final String BEI_JING_TEDING_INPUT = "北京特定入库";

    public static final String BEI_JING_TEDING_INPUT_TOTAL_PRICE = "北京特定入库总金额";

    public static final String FACTORY_OUTPUT = "工厂出货";

    public static final String FACTORY_OUTPUT_TOTAL_PRICE = "工厂出货总金额";

    public static final String TEDING_FACTORY_OUTPUT = "特定工厂出货";

    public static final String TEDING_FACTORY_OUTPUT_TOTAL_PRICE = "特定工厂出货总金额";

    public static final String BEIJING_STOCK = "北京剩余";

    public static final String BEIJING_STOCK_TOTAL_PRICE = "北京剩余总金额";

    public static final String BEIJINGTEDING_STOCK = "北京特定剩余";

    public static final String BEIJINGTEDING_STOCK_TOTAL_PRICE = "北京特定剩余总金额";

    /**
     * 更新用户信息
     */
    public static final int ADMIN_UPDATE_USER_INFO = 0;
    public static final int OTHER_UPDAT_USER_INFOE = 1;
}
