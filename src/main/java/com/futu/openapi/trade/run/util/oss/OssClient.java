/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.futu.openapi.trade.run.util.oss;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.futu.openapi.trade.run.util.PropUtil;

/**
 * @author zhenmin
 * @version $Id: OssClient.java, v 0.1 2024-05-07 15:45 xuxu Exp $$
 */
public class OssClient {

    private static OssClient client;

    private static final String BUCKET_NAME = "goodstocks";

    /*
     * Create an empty folder without request body, note that the key must be
     * suffixed with a slash
     */
    private static final String keySuffixWithSlash = "WeekPoint/";

    private OssClient() {}

    public synchronized static OssClient newInstance() {

        if (client == null) {
            client = new OssClient();
        }
        return client;
    }

    public synchronized OSS genOssClient() {

        //log
        return createOssClient();
    }

    public String generateUrl(String key) {

        OSS ossClient = genOssClient();
        try {
            String objectName = keySuffixWithSlash + key;   // OSS上的图片文件名

            // 设置URL过期时间
            Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000 * 240);
            // 生成URL
            URL url = ossClient.generatePresignedUrl(BUCKET_NAME, objectName, expiration);

            return url.toString();
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    public void loadData(String filePath, String key) {
        OSS ossClient = genOssClient();
        // 指定要读取的文件路径
        File file = new File(filePath);

        try (FileInputStream fis = new FileInputStream(file)) {

            // 创建一个与文件大小相等的byte数组
            byte[] fileContent = new byte[(int)file.length()];

            // 读取文件内容到byte数组中
            fis.read(fileContent);

            InputStream in = new BufferedInputStream(
                new FileInputStream(file));

            ossClient.putObject(BUCKET_NAME, keySuffixWithSlash, in);

            PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME,
                keySuffixWithSlash + key,
                new ByteArrayInputStream(fileContent));

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType("text/plain;charset=utf-8");
            putObjectRequest.setMetadata(objectMetadata);

            ossClient.putObject(putObjectRequest);

            /*
             * Verify whether the size of the empty folder is zero
             */
            OSSObject object = ossClient.getObject(BUCKET_NAME, keySuffixWithSlash);
            object.getObjectContent().close();
        } catch (Exception e) {e.printStackTrace();} finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    private static OSS createOssClient() {

        OSS ossClient = null;
        try {

            // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
            String endpoint = "https://oss-cn-beijing.aliyuncs.com";
            // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
            EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory
                .newEnvironmentVariableCredentialsProvider();

            // 创建OSSClient实例。
            ossClient = new OSSClientBuilder().build(endpoint, PropUtil.getProp("OSS_ACCESS_KEY"),
                PropUtil.getProp("OSS_ACCESS_KEY_SECRET"));

        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                + "a serious internal problem while trying to communicate with OSS, "
                + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } catch (Exception e) {
            System.out.println("Error Message:" + e.getMessage());
        }

        return ossClient;
    }

}