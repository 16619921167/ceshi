package com.zbf.web;

import com.alibaba.fastjson.JSON;
import com.zbf.common.ResponseResult;
import com.zbf.core.CommonUtils;
import com.zbf.core.page.Page;
import com.zbf.core.utils.UID;
import com.zbf.entity.SolrPage;
import com.zbf.entity.Timu;
import com.zbf.oauthLogin.User;
import com.zbf.service.TKGLService;
import io.jsonwebtoken.Claims;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.security.krb5.internal.PAData;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("tkgl")
public class TKGLController {
    @Resource
    private SolrClient solrClient;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private TKGLService tkglService;
    @RequestMapping("toaddtk")
    public ResponseResult toaddtk(HttpServletRequest request){
        Map<String,Object> map = CommonUtils.getParamsJsonMap(request);
        map.put("id", UID.next());
        tkglService.toaddtk(map);

       // redisTemplate.opsForList().rightPush("tiku", map);
        //添加redis中题库信息   每个小key代表一条题库信息
        redisTemplate.opsForHash().put("tiku",map.get("id"), map);

        ResponseResult responseResult=new ResponseResult();
        responseResult.setSuccess("ok");
        return  responseResult;
    }
    @RequestMapping("toupdatetk")
    public ResponseResult toupdatetk(HttpServletRequest request){
        Map<String, Object> map = CommonUtils.getParamsJsonMap(request);

        tkglService.toupdatetk(map);

        ResponseResult responseResult=new ResponseResult();
        responseResult.setSuccess("ok");
        return  responseResult;
    }
   @RequestMapping("gettikulist")
    public ResponseResult gettklist(HttpServletRequest request){
       Map<String, Object> map = CommonUtils.getParamsJsonMap(request);
       Page<Map<String,Object>> page=new Page<>();
       Page.setPageInfo(page, map);
       List<Map<String, Object>> gettikulist = tkglService.gettikulist(page);
       page.setResultList(gettikulist);
       ResponseResult responseResult=new ResponseResult();
       responseResult.setResult(page);
       return responseResult;
   }
   //获得题目list========================================================================
   @RequestMapping("gettimulist")
    public ResponseResult gettimulist(HttpServletRequest request){
       Map<String, Object> map = CommonUtils.getParamsJsonMap(request);
       Page<Map<String,Object>> page=new Page<>();
       Page.setPageInfo(page, map);
       List<Map<String, Object>> shitilist = tkglService.gettimulist(page);
       page.setResultList(shitilist);
       ResponseResult responseResult=new ResponseResult();
       responseResult.setResult(page);
       return responseResult;
   }
   @RequestMapping("getTikuListFromRedis")
   public ResponseResult getTikuListFromRedis(){
        List<Map<String,Object>> tikutype=redisTemplate.opsForHash().values("tiku");
       ResponseResult responseResult = ResponseResult.getResponseResult();
       responseResult.setResult(tikutype);
       return  responseResult;
   }
   @RequestMapping("toAddShiTi")
   public ResponseResult toAddShiTi(HttpServletRequest request) throws IOException, SolrServerException {
       ResponseResult responseResult = ResponseResult.getResponseResult();
       Map<String, Object> parameterMap = CommonUtils.getParameterMap(request);
       tkglService.addshiti(parameterMap);
       //添加solr库
       Timu timu = new Timu();
       timu.setId(UID.next()+"");
       timu.setTikuid(parameterMap.get("tikuid").toString());
       timu.setTixingid(parameterMap.get("shitileixing").toString());
       timu.setNanduid(parameterMap.get("nanduid").toString());
       timu.setShitizhuangtai(parameterMap.get("shitizhuangtai").toString());
       timu.setTigan(parameterMap.get("tigan").toString());
       timu.setCreateuserid(parameterMap.get("userID").toString());
       timu.setUserName(parameterMap.get("userName").toString());
       Map<String,Object> tikuname = (Map<String, Object>) redisTemplate.opsForHash().get("tiku", Long.valueOf(timu.getTikuid()));

       timu.setTikuname(tikuname.get("tikuname").toString());
       solrClient.addBean(timu);
       solrClient.commit();

       return  responseResult;
   }

   @RequestMapping("solrts")
   public ResponseResult solrtest(HttpServletRequest request) throws IOException, SolrServerException {
       Map<String, Object> parameterMap = CommonUtils.getParamsJsonMap(request);
       SolrPage<Timu> solrPage = new SolrPage<>();
       SolrPage.setPageInfo(solrPage,parameterMap);
       SolrQuery solrQuery = new SolrQuery();
       //如果是这样的说明没有条件查询
       if ("".equals(parameterMap.get("copytest"))||parameterMap.get("copytest")==null){
           solrQuery.set("q","*:*" );
       }else{
           solrQuery.set("q", "copytest:"+parameterMap.get("copytest"));
       }
       solrQuery.setHighlight(true);
       solrQuery.setHighlightSimplePre("<font style='color:red'>");
       solrQuery.setHighlightSimplePost("</font>");
       solrQuery.addHighlightField("tigan");
       solrQuery.addHighlightField("userName");

       solrQuery.setStart((solrPage.getPageNo()-1)*solrPage.getPageSize());
       solrQuery.setRows(solrPage.getPageSize());
       QueryResponse query = solrClient.query(solrQuery);
       List<Timu> timus = query.getBeans(Timu.class);
       //第一个key是document的id  第二个是  高亮的field的key
       Map<String, Map<String, List<String>>> highlighting = query.getHighlighting();
       timus.forEach((timu)->{   //题目对象
           highlighting.entrySet().forEach((entryhigh)->{  //所有高亮的字段
               if (entryhigh.getKey().equals(timu.getId())){
                   entryhigh.getValue().entrySet().forEach((entryfield)->{
                       //
                       if (entryfield.getKey().equals("tigan")){
                                    timu.setTigan(entryfield.getValue().get(0).toString());
                       }
                       if(entryfield.getKey().equals("userName")){
                           timu.setUserName(entryfield.getValue().get(0).toString());
                       }

                   });
               }

           });
       });

       ResponseResult responseResult=new ResponseResult();
       solrPage.setTotalCount(query.getResults().getNumFound());
      solrPage.setResultList(timus);
       responseResult.setResult(solrPage);

       return responseResult;
   }
}
