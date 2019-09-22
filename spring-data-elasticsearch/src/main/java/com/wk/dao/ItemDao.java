package com.wk.dao;

import com.wk.pojo.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

//泛型1：对哪一个对象进行操作，2：对象的主键
public interface ItemDao extends ElasticsearchRepository<Item,Long> {

    //自定义查询方法，根据要求设置方法名，无需实现该接口
    //根据name查询
    List<Item> findByName(String name);

    //name和desc符合一个即可
    List<Item> findByNameOrDesc(String name,String desc);

    //pageable设置分页信息
    List<Item> findByNameOrDesc(String name, String desc, Pageable pageable);
}
