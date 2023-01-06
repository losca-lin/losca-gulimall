package com.losca.gulimall.search;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class GulimallSearchApplicationTests {

    @Autowired
    RestHighLevelClient client;

    @Data
    class User {
        private String username;
        private Integer age;
        private String gender;
    }

    @Test
    void contextLoads() throws IOException {

        IndexRequest indexRequest = new IndexRequest("users2");
        indexRequest.id("2");   //数据的id

        // indexRequest.source("userName","zhangsan","age",18,"gender","男");

        User user = new User();
        user.setUsername("zhangsan");
        user.setAge(18);
        user.setGender("男");

        String jsonString = JSON.toJSONString(user);
        indexRequest.source(jsonString, XContentType.JSON);  //要保存的内容

        //执行操作
        IndexResponse index = client.index(indexRequest, RequestOptions.DEFAULT);

        //提取有用的响应数据
        System.out.println(index);
    }

    @Test
    void delete() throws IOException {
        // 1.创建Request对象
        DeleteIndexRequest request = new DeleteIndexRequest("users");
        // 2.发送请求
        client.indices().delete(request, RequestOptions.DEFAULT);
    }

    @Test
    void search() throws IOException{
        SearchRequest request = new SearchRequest("bank");
        SearchSourceBuilder source = request.source();
        source.query(QueryBuilders.matchQuery("address", "Mill"));
        source.aggregation(AggregationBuilders.terms("ageAgg").field("age").size(10));
        source.aggregation(AggregationBuilders.avg("ageAvg").field("age"));
        source.aggregation(AggregationBuilders.avg("balanceAvg").field("balance"));
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        Avg ageAvg = response.getAggregations().get("ageAvg");
        Terms ageAgg = response.getAggregations().get("ageAgg");
    }


}
