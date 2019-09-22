package com.wk;

import com.wk.dao.ItemDao;
import com.wk.pojo.Item;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:applicationContext.xml")
public class EsDemo {

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private ElasticsearchTemplate template;

    //spring-data 创建索引
    @Test
    public void createIndex(){
        //配置映射关系
//        template.putMapping(Item.class);
        //创建索引并配置映射关系
        template.createIndex(Item.class);
    }

    //添加文档
    @Test
    public void addDocument(){
//        itemDao.save(new Item(2l,"红米K20ProMax","全面屏 升降式摄像头 骁龙855处理器"));
        for (long i = 0; i < 20; i++) {
            String replace = "全面屏 升降式摄像头 骁龙"+i+"处理器";
            String replace1 = "红米K"+i+"Pro";
            itemDao.save(new Item(i, replace1,replace));
        }
    }

    //删除文档
    @Test
    public void delDocumentById(){
//        itemDao.deleteById(1l);
        /*全部删除
        itemDao.deleteAll();*/
    }

    //查询全部
    @Test
    public void findAll(){
        Iterable<Item> all = itemDao.findAll();
        all.forEach(a-> System.out.println("a = " + a));
    }

    //根据ID查找
    @Test
    public void findById(){
        Optional<Item> item = itemDao.findById(2l);
        Item item1 = item.get();
        System.out.println("item1 = " + item1);
    }

    //自定义方法的查询
    @Test
    public void findByName(){
        List<Item> items = itemDao.findByName("红米");
        items.forEach(a-> System.out.println("a = " + a));
    }

    @Test
    public void findByNameOrDesc(){
        Pageable pageable = PageRequest.of(0,15);       //默认10条 设置15条
        List<Item> items = itemDao.findByNameOrDesc("红米","全面屏",pageable);
        items.forEach(a-> System.out.println("a = " + a));
    }

    //原生查询方法
    @Test
    public void testNativeSearch(){
        //创建一个查询对象
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.queryStringQuery("红米是一个性价比很高的手机")
                        .defaultField("name"))
                        .withPageable(PageRequest.of(0,15))
                        .build();
        //执行查询
        List<Item> items = template.queryForList(query, Item.class);
        items.forEach(a-> System.out.println("a = " + a));
    }

}
