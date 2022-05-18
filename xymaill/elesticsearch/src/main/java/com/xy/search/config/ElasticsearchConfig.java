package com.xy.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 导入依赖
 * 编写配置
 * 参照 api进行操作：https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.x/java-rest-high.html
 */

@Configuration
public class ElasticsearchConfig {

    //rest请求前需要验证
    public static final RequestOptions COMMON_OPTIONS;
    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        COMMON_OPTIONS = builder.build();
    }

    //获取请求bean
    @Bean
    public RestHighLevelClient getRestHighLevelClient() {
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(RestClient.builder(
                new HttpHost("192.168.31.53",9200,"http")
        ));
        return restHighLevelClient;
    }

}
