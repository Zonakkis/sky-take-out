package com.sky.config;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.sky.properties.AliyunOssProperties;
import com.sky.properties.QiniuOssProperties;
import com.sky.service.impl.AliyunOssServiceImpl;
import com.sky.service.impl.QiniuOssServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OssServiceConfiguration {

    @Bean
    @ConditionalOnBean(AliyunOssServiceImpl.class)
    public OSS ossClient(AliyunOssProperties aliyunOssProperties) {
        DefaultCredentialProvider credentialProvider = new DefaultCredentialProvider(
                aliyunOssProperties.getAccessKeyId(),
                aliyunOssProperties.getAccessKeySecret());
        // 创建OSSClient实例。
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
        return OSSClientBuilder.create()
                .endpoint(aliyunOssProperties.getEndpoint())
                .credentialsProvider(credentialProvider)
                .clientConfiguration(clientBuilderConfiguration)
                .region(aliyunOssProperties.getRegion())
                .build();
    }


    @Bean
    @ConditionalOnBean(QiniuOssServiceImpl.class)
    public UploadManager uploadManager(QiniuOssProperties qiniuOssProperties) {
        Region region = Region.createWithRegionId(qiniuOssProperties.getRegion());
        com.qiniu.storage.Configuration cfg = com.qiniu.storage.Configuration.create(region);
        cfg.resumableUploadAPIVersion = com.qiniu.storage.Configuration.ResumableUploadAPIVersion.V2;
        return new UploadManager(cfg);
    }
}
