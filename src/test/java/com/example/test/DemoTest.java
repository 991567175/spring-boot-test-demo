package com.example.test;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.controller.DemoController;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SpringBootTest
public class DemoTest {


    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mvc;

    private Long start;
    private Long end;


    @BeforeEach
    public void before() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        start = System.currentTimeMillis();
    }

    @AfterEach
    void after() {
        end = System.currentTimeMillis();
        System.out.println("请求耗时：" + (end - start));
    }

    private String request(Object object, HttpMethod httpMethod, String url) throws Exception {
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.request(httpMethod, url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(JSONObject.toJSONString(object)))
                .andExpect(MockMvcResultMatchers.status().isOk());
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        response.setCharacterEncoding("UTF-8");
        resultActions.andDo(MockMvcResultHandlers.print());
        String contentAsString = response.getContentAsString();
        return contentAsString;

    }


    @Test
    public void get() throws Exception {
        String url = "/demo/get";
        Integer size = 10;
        url += "?size=" + size;
        String response = this.request(null, HttpMethod.GET, url);
        List<Integer> list = JSON.parseArray(response, Integer.class);
        if (Objects.equals(list.size(), size)) {
            throw new RuntimeException("数据错误");
        }
    }

    @Test
    public void post() throws Exception {
        String url = "/demo/post";
        Integer size = 10;
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(i);
        }
        String response = this.request(list, HttpMethod.POST, url);
        if (!Objects.equals(size, Integer.valueOf(response))) {
            throw new RuntimeException("数据错误");
        }
    }

    @Test
    public void upload() throws Exception {
        String url = "/demo/upload";
        String fileName1 = RandomStringUtils.randomAlphanumeric(6);
        String fileName2 = RandomStringUtils.randomAlphanumeric(6);
        File file1 = new File("E:\\demo\\" + fileName1);
        File file2 = new File("E:\\demo\\" + fileName2);
        file1 = FileUtil.touch(file1);
        file2 = FileUtil.touch(file2);
        FileInputStream fileInputStream1 = new FileInputStream(file1);
        FileInputStream fileInputStream2 = new FileInputStream(file2);
        MockMultipartFile multipartFile1 = new MockMultipartFile("file1", file1.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, fileInputStream1);
        MockMultipartFile multipartFile2 = new MockMultipartFile("file2", file2.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, fileInputStream2);
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.multipart(url)
                .file(multipartFile1)
                .file(multipartFile2)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk());
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        response.setCharacterEncoding("UTF-8");
        resultActions.andDo(MockMvcResultHandlers.print());
        String contentAsString = response.getContentAsString();
        List<String> list = JSON.parseArray(contentAsString, String.class);
        if (list.size() != 2
                || !StringUtils.equals(list.get(0), fileName1)
                || !StringUtils.equals(list.get(1), fileName2)) {
            throw new RuntimeException("数据错误");
        }
    }


    @Test
    public void download() throws Exception {
        String url = "/demo/download";
        DemoController.DownloadParam param = new DemoController.DownloadParam();
        param.setCode("code查询条件");
        param.setGoodsName("goodsName查询条件");
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.request(HttpMethod.GET, url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(JSONObject.toJSONString(param)))
                .andExpect(MockMvcResultMatchers.status().isOk());
        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        FileUtil.writeBytes(response.getContentAsByteArray(), "E:\\demo\\templateExcel.xls");
    }


}
