package cn.wilmar.cg_energy.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * AWS S3增删服务
 * @Author: Qianwei
 * @Date: 2023/11/07
 */
@Data
@Slf4j
public class S3Service {

    private String accessKey = "AKIATZY25J4X3YXWCQRB";
    private String secretKey = "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";
    private String clientRegion = "cn-northwest-1";
    private String bucketName = "saptest1-awss3";
//    String stringObjKeyName = "novatestkey";
    String fileObjKeyName = "test.csv";
    String sourFileName = "C:/EPS_LastGuid.processed.txt";

    AmazonS3 s3 = null;

    /**
     * 通过构造器实现s3的初始化，
     * 可以根据实际的场景和需求修改初始化的方式
     */
    public void AmazonS3Util() {
        ClientConfiguration config = new ClientConfiguration();
        config.setProtocol(Protocol.HTTP);

        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        s3 = AmazonS3ClientBuilder.standard()
                .withRegion(clientRegion)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }

    /**
     * 获取bucket中所有文件
     */
    public List<S3ObjectSummary> listObject() {
        ListObjectsV2Result result = s3.listObjectsV2(bucketName);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        for (S3ObjectSummary os : objects) {
            System.out.println("* " + os.getKey()+"  ："+os.toString());
        }
        return objects;
    }

    /**
     * 上传文件
     * bucket同上，可以根据实际需要提取出来
     * file_path: 本地文件的地址，不包含文件名
     * key_name: 文件名称
     */
    public PutObjectResult putObject(String file_sour_path, String file_key_name) {
        try {
            PutObjectRequest request = new PutObjectRequest(bucketName, file_key_name, new File(file_sour_path));
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("plain/csv");
            metadata.addUserMetadata("x-amz-meta-title", "someTitle");
            request.setMetadata(metadata);
            PutObjectResult putObjectResult = s3.putObject(request);
            return putObjectResult;
        } catch (AmazonServiceException e) {
            log.error(e.getErrorMessage());
        }
        return null;
    }

    /**
     * 下载文件
     * bucket同上，可以根据实际需要提取出来
     * file_path: 要存到本地的地址，不包含文件名
     * key_name: 文件名称
     */
    public void getObject(String file_path, String key_name) {
        try {
            S3Object o = s3.getObject(bucketName, key_name);
            S3ObjectInputStream s3is = o.getObjectContent();
            FileOutputStream fos = new FileOutputStream(new File(file_path+key_name));
            byte[] read_buf = new byte[1024];
            int read_len = 0;
            while ((read_len = s3is.read(read_buf)) > 0) {
                fos.write(read_buf, 0, read_len);
            }
            s3is.close();
            fos.close();
        } catch (AmazonServiceException e) {
            log.error(e.getMessage());
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


    /**
     * 复制文件
     */
    public void copyObject(String object_key, String to_object_key) {
        try {
            CopyObjectResult copyObjectResult = s3.copyObject(bucketName, object_key, bucketName, to_object_key);
        } catch (AmazonServiceException e) {
            log.error(e.getErrorMessage());
        }
    }

    /**
     * 删除文件
     * @param object_key
     */
    public void deleteObject(String object_key){
        try {
            s3.deleteObject(bucketName, object_key);
        } catch (AmazonServiceException e) {
            log.error(e.getErrorMessage());
        }
    }

    // 调用示例
//    public static void main(String[] args) {
//        S3Service test = new S3Service();
//        test.AmazonS3Util();
//        test.putObject(test.sourFileName,test.fileObjKeyName);
//        test.listObject();
//    }

//    public static void main(String[] args) throws IOException {
//        String clientRegion = "cn-northwest-1";
//        String bucketName = "saptest1-awss3";
//        String stringObjKeyName = "novatestkey";
//        String fileObjKeyName = "test.txt";
//        String fileName = "C:/EPS_LastGuid.processed.txt";
//
//        try {
//            AWSCredentials awsCredentials = new BasicAWSCredentials("AKIATZY25J4X3YXWCQRB", "wbtwHIE9xI8Dee/4v614XQuvDuELwamdFx4CSB/6");
//            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
//                    .withRegion(clientRegion)
//                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
//                    .build();
//
//            // Upload a text string as a new object.
//            s3Client.putObject(bucketName, stringObjKeyName, "Uploaded String Object");
//
//            // Upload a file as a new object with ContentType and title specified.
//            PutObjectRequest request = new PutObjectRequest(bucketName, fileObjKeyName, new File(fileName));
//            ObjectMetadata metadata = new ObjectMetadata();
//            metadata.setContentType("plain/text");
//            metadata.addUserMetadata("x-amz-meta-title", "someTitle");
//            request.setMetadata(metadata);
//            s3Client.putObject(request);
//        }
//        catch(AmazonServiceException e) {
//            // The call was transmitted successfully, but Amazon S3 couldn't process
//            // it, so it returned an error response.
//            e.printStackTrace();
//        }
//    }
}

