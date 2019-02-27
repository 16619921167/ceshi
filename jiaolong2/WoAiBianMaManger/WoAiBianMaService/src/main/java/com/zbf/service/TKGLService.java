package com.zbf.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zbf.core.page.Page;
import com.zbf.core.utils.UID;
import com.zbf.mapper.TKGLMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TKGLService {
    @Resource
    private TKGLMapper tkglMapper;

    public int toaddtk(Map<String,Object> map){
      return  tkglMapper.toaddtk(map);
    }

    public List<Map<String, Object>> gettikulist(Page<Map<String, Object>> page) {
        List<Map<String, Object>> gettikulist = tkglMapper.gettikulist(page);
        return gettikulist;
    }

    public int toupdatetk(Map<String, Object> map) {
        return  tkglMapper.toupdatetk(map);
    }

    public void addshiti(Map<String, Object> parameterMap) {

        String daan =  parameterMap.get("checkList").toString();
        String xuangzeA = parameterMap.get("kuanglist").toString();
        String xuanxiang = parameterMap.get("inputValues").toString();
        parameterMap.entrySet().forEach((shiti)->{
            if(shiti.getKey().equals("checkList")){
                parameterMap.put("checkList", shiti.getValue().toString().substring(2, shiti.getValue().toString().length()-2));
            }
        });
        long timuid = UID.next();
        parameterMap.put("id",timuid);
        //添加试题
        tkglMapper.addshiti(parameterMap);
        List<String> xuangzeAa = JSON.parseArray(xuangzeA).toJavaList(String.class);

        List<String> xuanxiangg = JSON.parseArray(xuanxiang).toJavaList(String.class);
        HashMap<String,Object> hashMap=new HashMap<>();
         hashMap.put("timuid", timuid);
        for (int i = 0; i <xuangzeAa.size() ; i++) {
              hashMap.put("xuanxiangbianhao",xuangzeAa.get(i) );
              hashMap.put("xuanxiang", xuanxiangg.get(i));
              hashMap.put("id",UID.next());
              //添加选项  A：此选项的描述
            tkglMapper.addxuanxiang(hashMap);
        }


    }

    public List<Map<String, Object>> gettimulist(Page<Map<String, Object>> page) {
       return tkglMapper.gettimulist(page);
    }
}
