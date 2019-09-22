package com.wk.esclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wk.pojo.Item;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
//添加es的索引、mapping、文档
public class EsClientTest {

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

    //创建es索引
    @Test
    public void createIndex(){
//        3、使用client对象创建一个索引库
        client.admin().indices().prepareCreate("index_item").get();
//        4、关闭client对象
        client.close();
    }

    //设置es的mapping映射
    @Test
    public void createMapping() throws IOException {
//        1）创建一个Settings对象
//        2）创建一个Client对象
//        3）创建一个mapping信息，应该是一个json数据，可以是字符串，也可以是XContextBuilder对象
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()      //对象的第一个花括号
                    .startObject("item")
                        .startObject("properties")
                            .startObject("id")
                                .field("type","long")
                                .field("store",true)
                            .endObject()
                            .startObject("name")
                                .field("type","text")
                                .field("store",true)
                                .field("analyzer","ik_smart")
                            .endObject()
                            .startObject("desc")
                                .field("type","text")
                                .field("store",true)
                                .field("analyzer","ik_smart")
                            .endObject()
                        .endObject()
                    .endObject()
                .endObject();
//        4）使用client把mapping信息设置到索引库中
            client.admin().indices()
                    //设置要做映射的索引
                    .preparePutMapping("index_item")
                    //设置要做映射的type
                    .setType("item")
                    //mapping信息，可以是XContentBuilder对象、json字符串
                    .setSource(builder)
                    //指定操作
                    .get();
//        5）关闭client对象
        client.close();
    }

    //通过XContentBuilder方式添加文档
    @Test
    public void createDocumentByXContentBuilder() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                    .field("id",2l)
                    .field("name","华为Mate30Pro")
                    .field("desc","瀑布屏，奥利奥摄像头，麒麟990处理器")
                .endObject();
        //把文档对象添加到索引库
        client.prepareIndex()
             //设置索引名称
            .setIndex("index_item")
             //设置类型
            .setType("item")
             //设置文档ID，如果不设置会随机生成一个ID
            .setId("2")
             //设置文档内容
            .setSource(builder)
             //执行操作
            .get();
//        4、关闭client对象
        client.close();
    }

    //通过JSON方式添加文档
    @Test
    public void createDocumentByJson() throws JsonProcessingException {
        for (long i = 4; i < 100; i++) {

            Item item = new Item(i,"iPhone"+i,i+"个摄像头");
            //把对象转换成JSON字符串
            ObjectMapper mapper = new ObjectMapper();
            String jsonDocument = mapper.writeValueAsString(item);
//            System.out.println("jsonDocument = " + jsonDocument);
            //使用client把JSON字符串写入到索引库
            client.prepareIndex("index_item","item",i+"")
                    .setSource(jsonDocument, XContentType.JSON)
                    .get();
        }
        client.close();
    }
}
