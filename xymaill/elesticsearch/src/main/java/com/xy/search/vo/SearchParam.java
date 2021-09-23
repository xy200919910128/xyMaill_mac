package com.xy.search.vo;

import lombok.Data;

import java.util.List;

@Data
public class SearchParam {
    private String keyword;//页面传递过来的全文检索匹配关键字    搜索栏输入的
    private Long catalog3Id;//三级分类id          侧边栏分类点击

    /**
     * sort=saleCount_asc/desc  销量升序降序
     * sort=skuPrice_asc/desc   价格升序降序
     * sort=hotScore_asc/desc   热度评分
     */
    private String sort;//排序条件

    private Integer hasStock=1;//是否显示有货 0无库存 1有库存
    private String skuPrice;//价格区间查询 1_500/_500/500_
    private List<Long> brandId;//按照品牌进行传销，可以多选  brandId = 1?brandId =2
    private List<String> attrs;//按照属性进行筛选   attrs = 1_其他：2:cpu

    private Integer pageNum = 1;//页码

    private String _queryString;//原生的所有查询条件

}
