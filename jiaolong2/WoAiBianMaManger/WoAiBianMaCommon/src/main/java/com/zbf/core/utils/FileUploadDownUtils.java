package com.zbf.core.utils;

import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * 作者：LCG
 * 创建时间：2019/2/18 14:27
 * 描述：用来处理一些上传下载的工具类
 */
public class FileUploadDownUtils {

    /**
     * 根据资源路径获取文件资料
     * @param path  注意传入的路径不需要 / 开头
     * @return
     */
    //这个默认是类路径下的资源
    public static File getExcelTemplate(String path) throws FileNotFoundException {
        //spring提供的根据路径获取file对象的方法
        File file = ResourceUtils.getFile ( "classpath:" + path );
        return file;
    }
    /**
     * 写出文件到客户端
     * @param response          请求的响应头
     * @param file              欲下载的文件
     * @param filename          下载的文件名
     */
    public static void responseFileBuilder(HttpServletResponse response,File file,String filename){
        FileInputStream inputStream=null;
        ByteArrayOutputStream bos=null;
        ServletOutputStream outputStream=null;
        try{
            inputStream=new FileInputStream ( file );
            //处理文件名  将字符串转换成字节数组
            filename=new String(filename.getBytes ("utf-8"),"ISO-8859-1");
            //处理请求头
            response.setHeader("Content-Disposition", "attachment;fileName="+ filename);
            response.setContentType("multipart/form-data");
            response.setCharacterEncoding("UTF-8");
            //创建字节数组输出流
            //bos=new ByteArrayOutputStream (  );
            byte[] buffer=new byte[1024*4];
            //每次读取字节长度
            int n=0;
            outputStream= response.getOutputStream();
            while ( (n=inputStream.read(buffer)) !=-1) {
               // 通过写入输出流
               outputStream.write(buffer);
            }
        }catch (Exception e){
            e.printStackTrace ();
        }finally {
            if(outputStream!=null){
                try {
                    outputStream.close ();
                } catch (IOException e) {
                    e.printStackTrace ();
                }
            }
            if(bos!=null){
                try {
                    bos.close ();
                } catch (IOException e) {
                    e.printStackTrace ();
                }
            }
            if(inputStream!=null){
                try {
                    inputStream.close ();
                } catch (IOException e) {
                    e.printStackTrace ();
                }
            }
        }

    }

    /**
     * excel表格数据导入
     * @param file
     */
    public static void excelTemplateImport(MultipartFile file){



    }


}
