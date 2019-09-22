package com.wk.esclient;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

//测试es的查询
public class SearchTest {
    private TransportClient client;

    //将创建setting对象和客户端client对象的代码抽取出来
    @Before
    public void init(){
        //        1、创建一个Settings对象，相当于是一个配置信息。主要配置集群的名称。
        Settings settings = Settings.builder().put("cluster.name","my-elasticsearch").build();
//        2、创建一个客户端Client对象
        //指定集群中的节点列表
        try {
            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"),9301))
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"),9302))
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"),9303));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void searchByCondition(QueryBuilder builder,String highLightField){
        //设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //高亮显示的字段
        highlightBuilder.field(highLightField);
        //高亮显示的前缀
        highlightBuilder.preTags("<em>");
        //高亮显示的后缀
        highlightBuilder.postTags("</em>");
        //执行查询
        SearchResponse response = client.prepareSearch("index_item")
                .setTypes("item")
                .setQuery(builder)
                //起始行号，从0开始
                .setFrom(0)
                //每页显示几条数据
                .setSize(5)
                //设置高亮
                .highlighter(highlightBuilder)
                .get();
        //获取查询结果
        SearchHits hits = response.getHits();
        //获取查询结果的总记录数
        System.out.println("查询结果的总记录数：" + hits.getTotalHits());
        //查询查询列表
        SearchHit[] result = hits.getHits();
        for (SearchHit searchHitFields : result) {
            //打印文档对象，以json格式输出
            System.out.println("searchHitFields = " + searchHitFields.getSourceAsString());
            //获取文档属性
            Map<String, Object> source = searchHitFields.getSource();
            System.out.println("source = " + source.get("id"));
            System.out.println("source = " + source.get("name"));
            System.out.println("source = " + source.get("desc"));
            //获取高亮结果
            Map<String, HighlightField> highlightFields = searchHitFields.getHighlightFields();
            System.out.println("高亮结果 = " + highlightFields);
            //单独取出高亮结果
            HighlightField field = highlightFields.get(highLightField);
            Text[] fragments = field.getFragments();
            if (fragments != null) {
                String highLightResult = fragments[0].toString();
                System.out.println("高亮结果 = " + highLightResult);
            }
        }
        client.close();
    }

    //根据ID查询
    @Test
    public void searchById(){
        //创建查询对象
        QueryBuilder builder = QueryBuilders.idsQuery().addIds("1","2");
        searchByCondition(builder,"title");
    }

    //根据term查询
    @Test
    public void searchByTerm(){
        String field = "desc";          //要搜索的字段
        String keyWord = "摄像头";        //要搜索的关键字
        QueryBuilder builder = QueryBuilders.termQuery(field,keyWord);
        searchByCondition(builder,"title");
    }

    //根据queryString查询
    @Test
    public void searchByQueryString(){
        QueryBuilder builder = QueryBuilders.queryStringQuery("摄像头").defaultField("desc");
        //高亮查询，查询那个字段，就传那个字段到方法中
        searchByCondition(builder,"desc");
    }


}
