package com.pinyougou.shop.controller;

import org.apache.commons.io.FilenameUtils;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UploadController {

    @Value("${fileServerUrl}")
    private String fileServerUrl;

    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam("file")MultipartFile multipartFile) {
        Map<String, Object> data = new HashMap<>();
        data.put("status",500);
        try {
//        加载配置文件用来初始化fastdfs
            String conf_filename = this.getClass().getResource("/fastdfs_client.conf").getPath();
//        初始化客户端
            ClientGlobal.init(conf_filename);
//            创建存储客户端对象
            StorageClient storageClient = new StorageClient();
//            获取原文件
            String originalFilename = multipartFile.getOriginalFilename();
//            上传文件到FastDFS服务器
            String[] arr = storageClient.upload_file(multipartFile.getBytes(), FilenameUtils.getExtension(originalFilename), null);
            //拼接返回的URL和ip地址,拼装成完成的URL
            StringBuilder url = new StringBuilder(fileServerUrl);

            for (String str : arr) {
                url.append("/" + str);
            }
            data.put("status", 200);
            data.put("url", url.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

}
