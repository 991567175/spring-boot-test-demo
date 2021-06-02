package com.example.controller;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import lombok.Data;
import org.apache.poi.ss.usermodel.Font;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/demo")
public class DemoController {


    @GetMapping("/get")
    public List<Integer> get(@RequestParam Integer size) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(i);
        }
        return list;
    }

    @PostMapping("/post")
    public Integer post(@RequestBody List<Integer> list) {
        return list.size();
    }


    @PostMapping("/upload")
    public List<String> upload(MultipartFile file1, MultipartFile file2) {
        return new ArrayList<String>() {{
            add(file1.getOriginalFilename());
            add(file2.getOriginalFilename());
        }};
    }

    @GetMapping("/download")
    public void download(@RequestBody DownloadParam export, HttpServletResponse response) {
        DownloadParam downloadParam = new DownloadParam();

        try (ExcelWriter writer = ExcelUtil.getWriter()) {

            writer.addHeaderAlias("orgName", "机构名称");
            writer.addHeaderAlias("goodsTypeName", "商品类型名称");
            writer.addHeaderAlias("goodsName", "商品名称");
            writer.addHeaderAlias("parValue", "面值");
            writer.addHeaderAlias("code", "兑换券码");
            writer.writeRow(downloadParam, true);

            writer.setColumnWidth(0, 40);
            writer.setColumnWidth(1, 40);
            writer.setColumnWidth(2, 40);
            writer.setColumnWidth(3, 40);
            writer.setColumnWidth(4, 40);

            Font font = writer.createFont();
            font.setFontName("微软雅黑");
            font.setFontHeight((short) (10 * 20));
            writer.getStyleSet().setFont(font, false);

            //response为HttpServletResponse对象
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
            response.setHeader("Content-Disposition", "attachment;filename=" + "excel.xls");

            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "POST,GET,PUT,OPTIONS,DELETE");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "Origin,X-Requested-With,Content-Type,Accept,Authorization,token,Content-Disposition");
            response.setHeader("Access-Control-Expose-Headers", "Origin,X-Requested-With,Content-Type,Accept,Authorization,token,Content-Disposition");


            writer.flush(response.getOutputStream(), true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }


    @Data
    public static class DownloadParam {

        private String orgName;
        private String goodsTypeName;
        private String goodsName;
        private String parValue;
        private String code;

    }

}
