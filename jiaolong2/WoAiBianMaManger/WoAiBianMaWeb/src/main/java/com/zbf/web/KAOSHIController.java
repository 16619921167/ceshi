package com.zbf.web;

import com.alibaba.fastjson.JSON;
import com.zbf.common.ResponseResult;
import com.zbf.core.CommonUtils;
import com.zbf.enmu.MyRedisKey;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.Response;
import java.util.*;
//222
@Controller
@RequestMapping("kaoshi")
public class KAOSHIController {
    @Resource
    private RedisTemplate redisTemplate;
     //
    @RequestMapping("getShiJuanData")
    public ResponseResult getShiJuanData(){
        ResponseResult responseResult = ResponseResult.getResponseResult();

       // List<Map<String,Object>> range = redisTemplate.opsForList().range(MyRedisKey.SHI_JUAN.getKey(), 0, -1);

     //   responseResult.setResult(range);
        return responseResult;
    }
    /**
     * 根据 试卷按照分数区间段 进行分数统计
     * @param request
     * @return
     */
    @RequestMapping("getScoreRangData")
    public ResponseResult getScoreRangData(HttpServletRequest request){
        //获取ResponseResult
        ResponseResult responseResult=ResponseResult.getResponseResult ();
        //获取参数
        // shijuanid,
        // fenshu1 json字符串 可以解析成数组, fenshu2
        Map<String, Object> parameterMap = CommonUtils.getParameterMap ( request );
        //区间值的开始部分
        List<Integer> fenshu1 = JSON.parseArray(parameterMap.get("fenshu1").toString()).toJavaList(Integer.class);
        //区间值的结束部分
        List<Integer> fenshu2 = JSON.parseArray(parameterMap.get("fenshu2").toString()).toJavaList(Integer.class);
        List<Map<String,Object>> listbingdata=new ArrayList<>(  ); //bingdata:[{name:" ",value:0}],//饼图的数据，
        List<String> listbingdatatext=new ArrayList<> (  );//bingtextdata:[" "],//饼图的项的数据一个数组
         //试卷id和成绩
        for(int i=0;i<fenshu1.size ();i++){
            Set shijuanid = redisTemplate.opsForZSet ().rangeByScore ( parameterMap.get ( "shijuanid" ).toString (), fenshu1.get ( i ), fenshu2.get ( i ) );
            Map<String,Object> map=new HashMap<>(  );
            String name=""+fenshu1.get ( i )+"-"+ fenshu2.get ( i );
            map.put ( "name",name);
            map.put ( "value",shijuanid.size ());
            listbingdata.add ( map );
            listbingdatatext.add ( name );
        }

        Map<String,Object> mapdata=new HashMap<> (  );

        mapdata.put ( "listbingdata" ,listbingdata);
        mapdata.put ( "listbingdatatext" ,listbingdatatext);

        responseResult.setResult ( mapdata );

        return responseResult;
    }

}
