package com.sky.service.impl;

import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.sky.constant.MessageConstant;
import com.sky.exception.OssException;
import com.sky.properties.QiniuOssProperties;
import com.sky.service.OssService;
import com.sky.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@Primary
public class QiniuOssServiceImpl implements OssService {

    @Autowired
    private UploadManager uploadManager;
    private final String domain;
    private final String uploadToken;

    public QiniuOssServiceImpl(QiniuOssProperties qiniuOssProperties) {
        Auth auth = Auth.create(qiniuOssProperties.getAccessKey(), qiniuOssProperties.getSecretKey());
        uploadToken = auth.uploadToken(qiniuOssProperties.getBucketName());
        domain = qiniuOssProperties.getDomain();
    }


    public String upload(byte[] bytes, String fileName) {
        String extension = FileUtil.getExtension(fileName);
        String objectName = UUID.randomUUID() + "." + extension;

        try {
            uploadManager.put(bytes, objectName, uploadToken);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new OssException(MessageConstant.UPLOAD_FAILED);
        }

        // 文件访问路径规则 http://domain/objectName
        StringBuilder url = new StringBuilder("http://")
                .append(domain)
                .append("/")
                .append(objectName);

        log.info("文件上传到:{}", url);
        return url.toString();
    }
}