package com.zbf.entity;

import lombok.Data;

import java.io.Serializable;
import org.apache.solr.client.solrj.beans.Field;
@Data
public class Timu implements Serializable {
    @Field
    private String id;
    @Field
    private String tikuid;
    @Field
    private String tikuname;
    @Field
     private String tixingid;
    @Field
     private String nanduid;
    @Field
     private String shitizhuangtai;
    @Field
     private String tigan;
    @Field
     private String createuserid;
    @Field
     private String userName;


}
