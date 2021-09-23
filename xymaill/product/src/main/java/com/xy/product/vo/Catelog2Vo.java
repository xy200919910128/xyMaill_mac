package com.xy.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 2级分类vo
 */
@NoArgsConstructor//无参构造器
@AllArgsConstructor//全参构造器
@Data
public class Catelog2Vo {
    private String catalog1Id;//1级父分类id
    private List<Catelog3Vo> catalog3List;//3级子分类
    private String id;
    private String name;

    /**
     * 三级分类vo
     */
    @NoArgsConstructor//无参构造器
    @AllArgsConstructor//全参构造器
    @Data
    public static class Catelog3Vo{
        private String catalog2Id;//2级父分类id
        private String id;
        private String name;
    }
}
