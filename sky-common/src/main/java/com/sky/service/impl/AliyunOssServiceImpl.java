package com.sky.service.impl;

import com.aliyun.oss.*;
import com.sky.constant.MessageConstant;
import com.sky.exception.OssException;
import com.sky.properties.AliyunOssProperties;
import com.sky.service.OssService;
import com.sky.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.ByteArrayInputStream;
import java.util.UUID;

@Service
@Slf4j
@Primary
public class AliyunOssServiceImpl implements OssService {

    @Autowired
    private OSS ossClient;
    private final String endpoint;
    private final String bucketName;

    public AliyunOssServiceImpl(AliyunOssProperties aliyunOssProperties) throws Exception {
        endpoint = aliyunOssProperties.getEndpoint();
        bucketName = aliyunOssProperties.getBucketName();
    }

    /**
     * 文件上传
     *
     * @param bytes
     * @param fileName
     * @return
     */
    @Override
    public String upload(byte[] bytes, String fileName) {
        String extension = FileUtil.getExtension(fileName);
        String objectName = UUID.randomUUID() + "." + extension;
        try {
            // 创建PutObject请求。
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes));
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
            throw new OssException(MessageConstant.UPLOAD_FAILED);
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
            throw new OssException(MessageConstant.UPLOAD_FAILED);
        }

        //文件访问路径规则 https://BucketName.Endpoint/ObjectName
        StringBuilder url = new StringBuilder("https://")
                .append(bucketName)
                .append(".")
                .append(endpoint)
                .append("/")
                .append(objectName);

        log.info("文件上传到:{}", url);
        return url.toString();
    }

    @PreDestroy
    private void close() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }
}
