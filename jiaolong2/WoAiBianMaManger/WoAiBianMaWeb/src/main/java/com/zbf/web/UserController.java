package com.zbf.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zbf.common.ResponseResult;
import com.zbf.core.CommonUtils;
import com.zbf.core.page.Page;
import com.zbf.service.RoleService;
import com.zbf.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 作者：LCG
 * 创建时间：2019/1/27 10:15
 * 描述：用户相关的API
 */

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @RequestMapping("/getUserInfo")
    public ResponseResult getUserInfo(HttpServletRequest request){

        Map<String, Object> parameterMap = CommonUtils.getParamsJsonMap ( request );
        ResponseResult responseResult = ResponseResult.getResponseResult ();
        if(parameterMap.get ( "userid" )!=null){
            Map<String, Object> userid = userService.getUserById ( parameterMap.get ( "userid" ).toString () );
            responseResult.setResult ( userid );
        }
         return responseResult;
    }


    /**
     * 用户管理用户列表 分页数据
     * @param request
     * @return
     */
    @RequestMapping("/getUserPageList")
    public ResponseResult getUserPageList(HttpServletRequest request){

        ResponseResult responseResult = ResponseResult.getResponseResult ();
        Map<String, Object> paramsJsonMap = CommonUtils.getParamsJsonMap ( request );
        Page<Map<String,Object>> page=new Page<Map<String,Object>> ();
        //设置查询参数
        page.setParams ( paramsJsonMap );
        //设置分页信息
        Page.setPageInfo ( page,paramsJsonMap );
        //查询分页
        userService.getUserList ( page );
        //设置角色数据
        Page<Map<String,Object>> page2=new Page<> ();
        page2.setParams ( paramsJsonMap );
        Page.setPageInfo ( page2,paramsJsonMap );
        roleService.getRolePage ( page2 );

        Map<String,Object> forreturn=new HashMap<> (  );
        forreturn.put ( "userPage",page);
        forreturn.put ( "rolelist",page2.getResultList ());
        responseResult.setResult ( forreturn );

        return responseResult;
    }

    //elementUI 的图片上传
    @RequestMapping("/addUserInfo")
    public ResponseResult addUserInfo(@RequestParam("file")MultipartFile[] file,
                                      HttpServletRequest request){


        String canshu = request.getParameter ( "canshu" );
        Map<String,Object> map = JSON.parseObject ( canshu, Map.class );

        //file表示上传的图片文件

        ResponseResult responseResult=ResponseResult.getResponseResult ();

        return responseResult;
    }

    /**
     * 给用户绑定角色
     * @param request
     * @return
     */
    @RequestMapping("toBangDingRoleForUser")
    public ResponseResult toBangDingRoleForUser(HttpServletRequest request){

        Map<String, Object> paramsJsonMap = CommonUtils.getParamsJsonMap ( request );
        ResponseResult responseResult=ResponseResult.getResponseResult ();

        userService.toBangDingRoleForUser ( paramsJsonMap );

        responseResult.setSuccess ( "ok" );

        return responseResult;
    }


    /**
     * elementUI上传案例 使用
     * @return
     */
    @RequestMapping("/uploadAnli")
    public ResponseResult uploadAnli(@RequestParam("file") MultipartFile[] file,HttpServletRequest request){
        Map<String, Object> parameterMap = CommonUtils.getParameterMap ( request );
        try {
           byte[] bytes=file[0].getBytes();
            FileOutputStream fileOutputStream = new FileOutputStream(new File("G:/123.jpg"));
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            bufferedOutputStream.write(bytes);
            bufferedOutputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace ();
        }
        ResponseResult responseResult=ResponseResult.getResponseResult ();
        return responseResult;
    }
     //仅仅更改userinfo
    @RequestMapping("/onlyUpdateUserInfo")
    public ResponseResult onlyUpdateUserInfo(HttpServletRequest request){
           //获取消息
        Map<String, Object> parameterMap = CommonUtils.getParamsJsonMap ( request );
          //返回
        ResponseResult responseResult=ResponseResult.getResponseResult ();
        return responseResult;
    }
    @RequestMapping("testpage")
    public ResponseResult testpage(HttpServletRequest request){
        Map<String, Object> map = CommonUtils.getParamsJsonMap(request);
        ResponseResult responseResult=new ResponseResult();
         Page<Map<String,Object>> page=new Page<>();
         page.setPageInfo(page, map);
         userService.getUserList(page);
         responseResult.setResult(page);
         return  responseResult;
    }
}
