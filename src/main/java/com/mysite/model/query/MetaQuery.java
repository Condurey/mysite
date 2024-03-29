package com.mysite.model.query;

import lombok.Data;

/**
 * meta查询条件
 * Created by Donghua.Chen on 2018/4/30.
 */
@Data
public class MetaQuery {

    /**
     * meta Name
     */
    private String name;

    /**
     * 类型
     */
    private String type;

    /**
     * 排序字段
     */
    private String order;

    /**
     * 查询数量
     */
    private int limit;

}
