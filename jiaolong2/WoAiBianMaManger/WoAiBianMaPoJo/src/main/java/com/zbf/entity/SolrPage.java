package com.zbf.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SolrPage<T> {
    //当前页
    private int pageNo=1;
    //页面size
    private int pageSize=10;
    //总页数
    private long totalCount;
    //总页数
    private long totalPage;

    private List<T> resultList;//查询结果集list
//计算总页数
    public long getTotalPage(){
        if((totalCount%pageSize)==0){
            totalPage=totalCount/pageSize;
        }else{
            totalPage=totalCount/pageSize-1;
        }
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    /**
     * 设置page参数
     */
    public static void setPageInfo(SolrPage page, Map<String,Object> mapp){

        if(mapp.get ( "pageNo" )!=null){
            page.setPageNo ( Integer.valueOf ( mapp.get ( "pageNo" ).toString () ) );
        }
        if(mapp.get ( "pageSize" )!=null){
            page.setPageSize ( Integer.valueOf ( mapp.get ( "pageSize" ).toString () ) );
        }

    }
}
