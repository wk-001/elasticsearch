package com.wk.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "item_index",type = "item")     //代表当前是一个文档，会映射到一个文档，需要指定索引
public class Item {

    @Id
    @Field(type = FieldType.Long,store = true)
    private Long id;

    @Field(type = FieldType.text,store = true,analyzer = "ik_smart")
    private String name;

    @Field(type = FieldType.text,store = true,analyzer = "ik_smart")
    private String desc;

    public Item() {
    }

    public Item(Long id, String name, String desc) {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
