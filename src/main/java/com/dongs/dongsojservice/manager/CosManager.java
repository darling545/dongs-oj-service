package com.dongs.dongsojservice.manager;

import com.dongs.dongsojservice.config.CosClientConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

@Component
public class CosManager {

    @Resource
    private CosClientConfig clientConfig;

    @Resource
    private COSClient cosClient;


    /**
     * 上传对象
     * @param key 唯一键
     * @param localFilePath 本地路径
     * @return
     */
    public PutObjectResult putObject(String key,String localFilePath){
        PutObjectRequest putObjectRequest = new PutObjectRequest(clientConfig.getBucket(),key,
                new File(localFilePath));
        return cosClient.putObject(putObjectRequest);
    }


    /**
     * 上文对象
     * @param key
     * @param file
     * @return
     */
    public PutObjectResult putObject(String key,File file){
        PutObjectRequest putObjectRequest = new PutObjectRequest(clientConfig.getBucket(),key,
                file);
        return cosClient.putObject(putObjectRequest);
    }
}
