package com.xy.search.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.xy.common.to.es.SkuEsModel;
import com.xy.search.config.ElasticsearchConfig;
import com.xy.search.constant.EsConstant;
import com.xy.search.service.ProductSearchService;
import com.xy.search.vo.SearchParam;
import com.xy.search.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ProductSearchServiceImpl implements ProductSearchService {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Override
    public SearchResult getSearchResult(SearchParam searchParam) {
        SearchResult searchResult = null;
        //动态拼接 searchParam
        SearchRequest searchRequest = buildSearchRequest(searchParam);
        try {
            //发送 es查询请求
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, ElasticsearchConfig.COMMON_OPTIONS);
            //解析 searchResponse 转为 SearchResult
            searchResult = buildSearchResult(searchParam,searchResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return searchResult;
    }


    //根据searchParam 动态拼接 SearchRequest
    private SearchRequest buildSearchRequest(SearchParam searchParam) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder(); //构建dsl语句
        //拼接查询条件
        BoolQueryBuilder boolQueryBuilder = buildQuery(searchParam);
        searchSourceBuilder = searchSourceBuilder.query(boolQueryBuilder);
        //拼接 排序 分页 高亮
        searchSourceBuilder = buildSortPageHeight(searchParam,searchSourceBuilder);
        //拼接 聚合查询
        searchSourceBuilder = buildAggs(searchParam,searchSourceBuilder);
        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX},searchSourceBuilder);
        return searchRequest;
    }

    private SearchSourceBuilder buildAggs(SearchParam searchParam, SearchSourceBuilder searchSourceBuilder) {

        //brand 聚合
        TermsAggregationBuilder termsBrandAggregationBuilder = AggregationBuilders.terms("brand_agg");
        termsBrandAggregationBuilder.field("brandId");
        TermsAggregationBuilder termsSubBrandNameAggregationBuilder = AggregationBuilders.terms("brandName_agg");
        termsSubBrandNameAggregationBuilder.field("brandName");
        TermsAggregationBuilder termsSubBrandImgAggregationBuilder = AggregationBuilders.terms("brandImage_agg");
        termsSubBrandImgAggregationBuilder.field("brandImg");
        termsBrandAggregationBuilder.subAggregation(termsSubBrandNameAggregationBuilder);
        termsBrandAggregationBuilder.subAggregation(termsSubBrandImgAggregationBuilder);

        //cat 聚合
        TermsAggregationBuilder termsCatAggregationBuilder = AggregationBuilders.terms("cate_agg");
        termsCatAggregationBuilder.field("catalogId");
        TermsAggregationBuilder termsCatNameAggregationBuilder = AggregationBuilders.terms("catName_agg");
        termsCatNameAggregationBuilder.field("catalogName.keyword");
        termsCatAggregationBuilder.subAggregation(termsCatNameAggregationBuilder);

        //attr聚合
        NestedAggregationBuilder nestedAggregationBuilder = AggregationBuilders.nested("attr_agg","attrs");
        TermsAggregationBuilder termsAttrIdAggregationBuilder = AggregationBuilders.terms("attr_id_agg");
        termsAttrIdAggregationBuilder.field("attrs.attrId");
        TermsAggregationBuilder termsAttrNameAggregationBuilder = AggregationBuilders.terms("attr_Name_agg");
        termsAttrNameAggregationBuilder.field("attrs.attrName");
        TermsAggregationBuilder termsAttrValueAggregationBuilder = AggregationBuilders.terms("attr_value_agg");
        termsAttrValueAggregationBuilder.field("attrs.attrValue");
        termsAttrIdAggregationBuilder.subAggregation(termsAttrNameAggregationBuilder);
        termsAttrIdAggregationBuilder.subAggregation(termsAttrValueAggregationBuilder);
        nestedAggregationBuilder.subAggregation(termsAttrIdAggregationBuilder);


        searchSourceBuilder.aggregation(termsBrandAggregationBuilder);
        searchSourceBuilder.aggregation(termsCatAggregationBuilder);
        searchSourceBuilder.aggregation(nestedAggregationBuilder);
        return searchSourceBuilder;
    }

    private SearchSourceBuilder buildSortPageHeight(SearchParam searchParam,SearchSourceBuilder searchSourceBuilder) {
        //排序
        if(StringUtils.isNotEmpty(searchParam.getSort())){
            String[] sort = searchParam.getSort().split("_");
            searchSourceBuilder.sort(sort[0],StringUtils.equalsIgnoreCase(sort[1],"asc")? SortOrder.ASC:SortOrder.DESC);
        }
        //分页
        searchSourceBuilder.from((searchParam.getPageNum()-1)*EsConstant.PRODUCT_PAGE_SIZE);
        searchSourceBuilder.size(EsConstant.PRODUCT_PAGE_SIZE);
        //高亮
        if(StringUtils.isNotEmpty(searchParam.getKeyword())){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            searchSourceBuilder.highlighter(highlightBuilder);
        }
        return  searchSourceBuilder;
    }

    private BoolQueryBuilder buildQuery(SearchParam searchParam) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(StringUtils.isNotEmpty(searchParam.getKeyword())){
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle",searchParam.getKeyword()));
        }
        if(searchParam.getCatalog3Id()!=null){
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId",searchParam.getCatalog3Id()));
        }
        if(searchParam.getBrandId()!=null&&searchParam.getBrandId().size()>0){
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId",searchParam.getBrandId()));
        }
        if(searchParam.getHasStock()!=null){
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock",searchParam.getHasStock()==1));
        }
        if(StringUtils.isNotEmpty(searchParam.getSkuPrice())){
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("skuPrice");
            String[] skuPrices = searchParam.getSkuPrice().split("_");
            if(skuPrices.length==2){
                rangeQueryBuilder.gte(skuPrices[0]).lte(skuPrices[1]);
            }else if(searchParam.getSkuPrice().startsWith("_")){
                rangeQueryBuilder.lte(skuPrices[0]);
            }else if(searchParam.getSkuPrice().endsWith("_")){
                rangeQueryBuilder.gte(skuPrices[0]);
            }
        }
        //组装 attrs
        if(searchParam.getAttrs()!=null && searchParam.getAttrs().size()>0){
            for (String  attr: searchParam.getAttrs()) {
                BoolQueryBuilder boolNestQueryBuilder = QueryBuilders.boolQuery();
                String[] attrIdAndValue = attr.split("_");
                String attrId = attrIdAndValue[0];
                String[] values = attrIdAndValue[1].split(":");
                boolNestQueryBuilder.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                boolNestQueryBuilder.must(QueryBuilders.termsQuery("attrs.attrValue",values));
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs",boolNestQueryBuilder, ScoreMode.None);
                boolQueryBuilder.filter(nestedQueryBuilder);
            }
        }
        return boolQueryBuilder;
    }




    //解析 searchResponse 转为 SearchResult
    private SearchResult buildSearchResult(SearchParam searchParam,SearchResponse searchResponse) {
        SearchResult searchResult = new SearchResult();

        if(searchResponse!=null){
            SearchHits searchHits =  searchResponse.getHits();
            searchResult = getSearchResultWithSearchHits(searchParam,searchResult,searchHits);
            Aggregations aggregations =  searchResponse.getAggregations();
            //聚合参数结果封装
            searchResult = getBrandResultWithAggs(searchResult,aggregations);
            searchResult = getCatResultWithAggs(searchResult,aggregations);
            searchResult = getAttrsResultWithAggs(searchResult,aggregations);
            searchResult.setPageNum(searchParam.getPageNum());
            //总记录数： d
            Long totalSize = searchHits.getTotalHits().value;
            //总页数
            Long totalPage = totalSize%EsConstant.PRODUCT_PAGE_SIZE==0?totalSize/EsConstant.PRODUCT_PAGE_SIZE:totalSize/EsConstant.PRODUCT_PAGE_SIZE+1;
            searchResult.setTotal(totalSize);
            searchResult.setTotalPages(Integer.valueOf(totalPage.toString()));

            List<Integer> pageNavs = new ArrayList<>();
            for(int i = 1; i <=searchResult.getTotalPages();i++) {
                pageNavs.add(i);
            }
            searchResult.setPageNavs(pageNavs);
        }
        return  searchResult;
    }

    //得到聚合 品牌信息
    private SearchResult getBrandResultWithAggs(SearchResult searchResult, Aggregations aggregations) {
        List<SearchResult.BrandVo> brandVoList = new ArrayList<>();
        ParsedLongTerms brandAgg = aggregations.get("brand_agg");
         List<? extends Terms.Bucket> brandBukList = brandAgg.getBuckets();
         if(brandBukList!=null&&brandBukList.size()>0){
             for (Terms.Bucket  bucket:brandBukList ) {
                 SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
                 brandVo.setBrandId(bucket.getKeyAsNumber().longValue());
                 ParsedStringTerms brandNameAgg = bucket.getAggregations().get("brandName_agg");
                 brandVo.setBrandName(brandNameAgg.getBuckets().get(0).getKeyAsString());
                 ParsedStringTerms brandImgAgg = bucket.getAggregations().get("brandImage_agg");
                 brandVo.setBrandImg(brandImgAgg.getBuckets().get(0).getKeyAsString());
                 brandVoList.add(brandVo);
             }
        }
        searchResult.setBrands(brandVoList);
        return searchResult;
    }

    //得到聚合 分类信息
    private SearchResult getCatResultWithAggs(SearchResult searchResult, Aggregations aggregations) {
        List<SearchResult.CatalogVo> catalogVoList = new ArrayList<>();
        ParsedLongTerms cateAgg = aggregations.get("cate_agg");
        List<? extends Terms.Bucket> catBukList = cateAgg.getBuckets();
        if(catBukList!=null&&catBukList.size()>0){
            for (Terms.Bucket  bucket:catBukList ) {
                SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
                catalogVo.setCatalogId(bucket.getKeyAsNumber().longValue());
                ParsedStringTerms catNameAgg = bucket.getAggregations().get("catName_agg");
                catalogVo.setCatalogName(catNameAgg.getBuckets().get(0).getKeyAsString());
                catalogVoList.add(catalogVo);
            }
        }
        searchResult.setCatalogs(catalogVoList);
        return searchResult;
    }


    //得到聚合 Attr信息
    private SearchResult getAttrsResultWithAggs(SearchResult searchResult, Aggregations aggregations) {
        List<SearchResult.AttrVo> attrVoList = new ArrayList<>();
        ParsedNested attrAgg = aggregations.get("attr_agg");
       ParsedLongTerms  attrIdTerms = attrAgg.getAggregations().get("attr_id_agg");
        List<? extends Terms.Bucket> attrIdBukList =  attrIdTerms.getBuckets();
        if(attrIdBukList!=null&&attrIdBukList.size()>0){
            for (Terms.Bucket  idbucket:attrIdBukList ) {
                SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
                attrVo.setAttrId(idbucket.getKeyAsNumber().longValue());
                ParsedStringTerms attrNameAgg = idbucket.getAggregations().get("attr_Name_agg");
                List<? extends Terms.Bucket> attrNameAggList =  attrNameAgg.getBuckets();
                attrVo.setAttrName(attrNameAggList.get(0).getKeyAsString());
                ParsedStringTerms parsedStringTerms  = idbucket.getAggregations().get("attr_value_agg");
                List<? extends Terms.Bucket> attrValueAggList =  parsedStringTerms.getBuckets();
                List<String> attrValueList = new ArrayList<>();
                if(attrValueAggList!=null&&attrValueAggList.size()>0){
                    for(Terms.Bucket  valueBucket:attrValueAggList){
                        attrValueList.add(valueBucket.getKeyAsString());
                    }
                }
                attrVo.setAttrValue(attrValueList);
                attrVoList.add(attrVo);
            }
        }
        searchResult.setAttrs(attrVoList);
        return searchResult;
    }


    private SearchResult getSearchResultWithSearchHits(SearchParam searchParam,SearchResult searchResult,SearchHits searchHits) {
        List<SkuEsModel> skuEsModelList = new ArrayList<>();
       SearchHit[] searchHit =  searchHits.getHits();
       if(searchHit!=null&&searchHit.length>0){
           for (SearchHit ser :searchHit ) {
               SkuEsModel skuEsModel = new SkuEsModel();
               SkuEsModel sku = JSONObject.parseObject(ser.getSourceAsString(),skuEsModel.getClass());
               if(StringUtils.isNotEmpty(searchParam.getKeyword())){
                  HighlightField highlightField =  ser.getHighlightFields().get("skuTitle");
                   sku.setSkuTitle(highlightField.getFragments()[0].string());
               }
               skuEsModelList.add(sku);
           }
       }
        searchResult.setProducts(skuEsModelList);
       return searchResult;

    }
}
