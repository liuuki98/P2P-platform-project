package com.liuuki.srb.service.imp;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.liuuki.srb.service.FileService;
import com.liuuki.srb.util.OssProperties;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

@Service
@Slf4j
public class FileServiceImp implements FileService {
    @Override
    public String upload(InputStream inputStream, String module, String fileName) {

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(
                OssProperties.ENDPOINT, //地域节点
                OssProperties.KEY_ID,   //密钥号
                OssProperties.KEY_SECRET    //密钥密码
        );

        //判断oss实例是否存在：如果不存在则创建，如果存在则获取
        if(!ossClient.doesBucketExist(OssProperties.BUCKET_NAME)){
            //创建bucket
            ossClient.createBucket(OssProperties.BUCKET_NAME);

            //设置oss实例的访问权限：公共读
            ossClient.setBucketAcl(OssProperties.BUCKET_NAME, CannedAccessControlList.PublicRead);
        }


        //构建日期路径：{module}/{日期}/{filename}
        //构建日期
        String date=new DateTime().toString("/yyyy/MM/dd/");
        //拼接文件名：uuid.扩展名
        fileName= UUID.randomUUID().toString()+fileName.substring(fileName.lastIndexOf("."));
        //文件根路径
        String path =module+date+fileName;

        //上传文件到oss客户端指定位置:path
        ossClient.putObject(OssProperties.BUCKET_NAME,path,inputStream);

        //关闭oss客户端
        ossClient.shutdown();

        //阿里云文件绝对路径,访问该路径即可下载相应文件
        return "https://" + OssProperties.BUCKET_NAME + "." + OssProperties.ENDPOINT + "/" + path;
    }


    @Override
    public void deleteByUrl(String url) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(
                OssProperties.ENDPOINT, //地域节点
                OssProperties.KEY_ID,   //密钥号
                OssProperties.KEY_SECRET    //密钥密码
        );


        //文件名（服务器上的文件路径）
        String host = "https://" + OssProperties.BUCKET_NAME + "." + OssProperties.ENDPOINT + "/";
        String objectName = url.substring(host.length());
        log.info("objectName:  "+objectName);


        // 删除文件。
        ossClient.deleteObject(OssProperties.BUCKET_NAME, objectName);

        // 关闭OSSClient。
        ossClient.shutdown();
    }
}
