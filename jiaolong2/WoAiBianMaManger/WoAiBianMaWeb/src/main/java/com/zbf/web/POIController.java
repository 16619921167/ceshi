package com.zbf.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.xml.internal.ws.api.ha.StickyFeature;
import com.zbf.common.ResponseResult;
import com.zbf.core.CommonUtils;
import com.zbf.core.page.Page;
import com.zbf.core.utils.FileUploadDownUtils;
import com.zbf.core.utils.UID;
import com.zbf.mapper.TKGLMapper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.ptg.MemAreaPtg;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
@Controller
@RequestMapping("POITS")
public class POIController {
    /*https://blog.csdn.net/vbirdbest/article/details/72870714*/
    @Resource
    private TKGLMapper tkglMapper;

    /**
     * 导入excel的数据到我们的数据库中，就tm是题目批量添加
     * Excel文件上传，导入数据
     * @param file
     * @param request
     * @return
     */
    @RequestMapping("toImportExcelData")
    public ResponseResult toImportExcelData(@RequestParam("file") MultipartFile file,HttpServletRequest request) throws IOException {
                /*shitileixing:"",
                nanduid:"1",
                tikuid:"",
                laiyuan:"",
                shitizhuangtai:""*/

        String canshu = request.getParameter("canshu");
        Map<String, Object> innerMap = JSON.parseObject(canshu).getInnerMap();

        ResponseResult responseResult=ResponseResult.getResponseResult ();
        //得到表格的输入流
        InputStream inputStream = file.getInputStream ();

        XSSFWorkbook xssfWorkbook=new XSSFWorkbook ( inputStream );

        XSSFSheet sheetAt = xssfWorkbook.getSheetAt ( 0 );

        int physicalNumberOfRows = sheetAt.getPhysicalNumberOfRows ();//获取数据的行数
        //获取第一行
        XSSFRow row1 = sheetAt.getRow ( 0 );
        ///第一行的第一列
        XSSFCell cell = row1.getCell ( 0 );
        cell.getStringCellValue ();//获取字符数据
        //此存储表中每一行的数据
        List<Map<String,Object>> listdata=new ArrayList<> (  );
        //循环表中的数据
        for(int i=1;i<physicalNumberOfRows;i++){
            XSSFRow row = sheetAt.getRow ( i );
            row.getPhysicalNumberOfCells ();
            Map<String,Object> maprow=new HashMap<String,Object>();
            maprow.putAll(innerMap);
            maprow.put ("tigan",row.getCell ( 0 ).getStringCellValue ());
            String xuanxiangbianhao = row.getCell(1).getStringCellValue();
            long timuid = UID.next();
            maprow.put("id", timuid);
            maprow.put ( "checkList",row.getCell ( 6 ).getStringCellValue ());
            if(row.getCell ( 7 )!=null){
                maprow.put ( "timujiexi",row.getCell ( 7 ).getStringCellValue ());
            }

            List<String> xuangzeAa = JSON.parseArray(xuanxiangbianhao).toJavaList(String.class);
            List<String> xuanxiangg=new ArrayList<> (  );
            xuanxiangg.add ( row.getCell ( 2 ).getStringCellValue () );
            xuanxiangg.add ( row.getCell ( 3 ).getStringCellValue () );
            xuanxiangg.add ( row.getCell ( 4 ).getStringCellValue () );
            xuanxiangg.add ( row.getCell ( 5 ).getStringCellValue () );
            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("timuid", timuid);
            for (int j = 0; j <xuangzeAa.size() ; j++) {
                hashMap.put("xuanxiangbianhao",xuangzeAa.get(j) );
                hashMap.put("xuanxiang", xuanxiangg.get(j));
                hashMap.put("id",UID.next());
                //添加选项  A：此选项的描述
                tkglMapper.addxuanxiang(hashMap);
            }
            listdata.add ( maprow );
        }
        System.out.println (JSON.toJSONString ( listdata ));
        for (int i = 0; i <listdata.size() ; i++) {
            tkglMapper.addshiti(listdata.get(i));
        }
         responseResult.setSuccess("ok");
        return responseResult;
    }
    @RequestMapping("getExceltemplate")   //下载excel模板供批量添加
    public void getExceltemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {

        File excelTemplate = FileUploadDownUtils.getExcelTemplate ( "exceltemplate/timu.xlsx" );

        FileUploadDownUtils.responseFileBuilder ( response,excelTemplate,"数据模板【题目】.xlsx" );

    }

    /**
     * 导出数据，生成excel
     * @param request
     * @param response
     */
    @RequestMapping("exportExcelData")
    public void exportExcelData(HttpServletRequest request,HttpServletResponse response) throws IOException {
//以下为查询所需要导出题库的所有题目===========================================================
        //获取数据
        Map<String, Object> parameterMap = CommonUtils.getParameterMap ( request );
        String tikuname = parameterMap.get("tikuname").toString();
        List<Map<String, Object>> shitiListByTikuid = tkglMapper.getShitiDataListByTiKu ( parameterMap );
//以下为poi操作===========================================================
        //POI的api的操作
        //获取工作簿
        XSSFWorkbook xssfWorkbook=new XSSFWorkbook (  );
        //创建工作表
        XSSFSheet sheet = xssfWorkbook.createSheet ( "真好听" );
        //创建表中的一行
        XSSFRow row1 = sheet.createRow ( 0 );
        //向其列中添加单元格
        row1.createCell ( 0 ).setCellValue ( "ID" );
        row1.createCell ( 1 ).setCellValue ( "题干描述" );
        row1.createCell ( 2 ).setCellValue ( "答案" );
        row1.createCell ( 3 ).setCellValue ( "答案解析" );
        row1.createCell ( 4 ).setCellValue ( "试题类型" );

        for(int i=0;i<shitiListByTikuid.size ();i++){

            Map<String, Object> map = shitiListByTikuid.get ( i );
            XSSFRow row = sheet.createRow ( i+1 );
            //map的键的集合，下面根据此来判断如果值不为null则写入excel中
            List<String> collect = new ArrayList<>(map.keySet ());
            //用流也可以
           // List<String> collect1 = map.keySet().stream().collect(Collectors.toList());
            for(int j=0;j<collect.size ();j++){
                XSSFCell cell =row.createCell ( j );
                cell.setCellValue (map.get ( collect.get ( j ) )!=null?map.get ( collect.get ( j ) ).toString ():"");
            }
        }
        //输出工作簿
        //设置文件名为byte数组
        String filename=new String((tikuname+"的试题.xlsx").getBytes (),"ISO8859-1");
        //响应头设置
        response.setContentType ( "application/octet-stream;charset=ISO8859-1" );
        response.setHeader("Content-Disposition", "attachment;filename="+filename);
        //获取servlet输出流
        ServletOutputStream outputStream = response.getOutputStream();
        //通过excel文件对象写出为excel文件
        xssfWorkbook.write ( outputStream );
    }
    @RequestMapping("我的练习")
    public ResponseResult imp(@RequestParam("file") MultipartFile file,HttpServletRequest request) throws IOException {
        ResponseResult responseResult = new ResponseResult();
        InputStream inputStream = file.getInputStream();
        //获取表文件
        XSSFWorkbook sheets = new XSSFWorkbook(inputStream);
        //获取表
        XSSFSheet sheetAt = sheets.getSheetAt(0);
        int physicalNumberOfRows = sheetAt.getPhysicalNumberOfRows();
         //此为表头
        XSSFRow row1 = sheetAt.getRow(0);
        XSSFCell cell = row1.getCell(0);
        //获取表格的字符数据
        String stringCellValue = cell.getStringCellValue();
        List<Map<String,Object>> list=new ArrayList<>();

        // 因为第一行是表头所以跳过
        for (int i = 1; i <physicalNumberOfRows ; i++) {
            //获取每一行
            XSSFRow row = sheetAt.getRow(i);
            int physicalNumberOfCells = row.getPhysicalNumberOfCells();
            Map<String,Object> map=new HashMap<>();
            //循环单元格
            map.put("tigan",row.getCell(0).getStringCellValue() );
            map.put("xuanxiangbianhao", row.getCell(1).getStringCellValue());
            List<String> xuanxiangmiaoshu=new ArrayList<>();
            xuanxiangmiaoshu.add(row.getCell(2).getStringCellValue());
            xuanxiangmiaoshu.add(row.getCell(3).getStringCellValue());
            xuanxiangmiaoshu.add(row.getCell(4).getStringCellValue());
            xuanxiangmiaoshu.add(row.getCell(5).getStringCellValue());
            map.put("xuanxiangmiaoshu",JSON.toJSONString(xuanxiangmiaoshu) );
            map.put("daan",row.getCell(6).getStringCellValue() );
            if(row.getCell(7)!=null){
                map.put("timujiexi",row.getCell(7).getStringCellValue() );
            }
            list.add(map);
        }
         return  responseResult;
    }

}
