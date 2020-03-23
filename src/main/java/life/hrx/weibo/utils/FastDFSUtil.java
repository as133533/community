package life.hrx.weibo.utils;

import life.hrx.weibo.dto.FastDFSDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * FastDFS上传工具类
 */

@Slf4j
@Component
public class FastDFSUtil {

//    @Value("${fastdfs.properties_path}") 注意@Value不能够直接给静态变量赋值，必须在public class类上加上@Component 再使用set的方式赋值
    private   static String  propertiesPath;



    @Value("${fastdfs.properties_path}")//必须要使用此方式才能够注入配置
    public  void setPropertiesPath(String path) {
        propertiesPath = path;
    }

    public static String getPropertiesPath() {
        return propertiesPath;
    }

    public static void initProperties(){
        try {

            ClientGlobal.initByProperties(propertiesPath);

        }catch (Exception e)
        {
            log.error("FastDFS 配置加载失败,请检查配置是否存在或路径是否正确",e);
        }
    }

    /**
     * 文件上传至FastDFS服务器
     * @param fastDFSDTO
     * @return
     */
    public static String[] upload(FastDFSDTO fastDFSDTO){


        NameValuePair[] nameValuePairs = new NameValuePair[1]; //创建一个NameValuePair对象的数组，NameValuePair主要用来添加String的键值对，放到post请求中
        nameValuePairs[0] =new NameValuePair("author",fastDFSDTO.getAuthor());

        //获得上传开始时的时间
        long startTime=System.currentTimeMillis();

        String[] uploadResults=null;
        StorageClient storageClient=null;
        try {
            storageClient = getStorageClient();
            uploadResults=storageClient.upload_file(fastDFSDTO.getContent(),fastDFSDTO.getExt(),nameValuePairs);
        } catch (IOException e) {
            log.error("上传文件时，文件IO异常"+fastDFSDTO.getName(),e);
        } catch (Exception e) {
            log.error("上传文件时,未知异常"+fastDFSDTO.getName(),e);
        }

        if (uploadResults==null && storageClient!=null){
            log.error("上传文件失败，错误码"+storageClient.getErrorCode());
        }

        log.info("上传文件用时" +(System.currentTimeMillis()-startTime)+"ms");


        String groupName=uploadResults[0]; //上传结果返回的第一个是该文件所处的组名
        String remoteFileName=uploadResults[1];//返回的是在storage中的文件名

        return uploadResults;

    }

    /**
     * 通过groupName和remoteFileName获得某个文件在FastDFS中的全部信息
     * @param groupName
     * @param remoteFileName
     * @return
     */
    public static FileInfo getFile(String groupName,String remoteFileName){



        try {
            StorageClient storageClient = getStorageClient();
            return storageClient.get_file_info(groupName,remoteFileName);
        } catch (IOException e) {
            log.error("获得文件信息时，IO异常",e);
        } catch (Exception e) {
            log.error("获得文件时，发生未知错误",e);
        }
        return null;
    }

    /**
     * 下载文件使用，返回字节流，暂时还没有用到
     * @param groupName
     * @param remoteFileName
     * @return
     */
    public static InputStream downFile(String groupName,String remoteFileName){


        try {
            StorageClient storageClient = getStorageClient();
            byte[] fileByte = storageClient.download_file(groupName, remoteFileName);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileByte);
            return byteArrayInputStream;
        } catch (IOException e) {
            log.error("下载文件时发生IO异常",e);
        } catch (Exception e) {
            log.error("下载文件时发生未知错误",e);
        }
        return null;
    }

    /**
     * 删除文件方法，暂时还没有用到
     * @param groupName
     * @param remoteFileName
     * @throws Exception
     */
    public static void deleteFile(String groupName,String remoteFileName) throws Exception {



        StorageClient storageClient = getStorageClient();
        int i = storageClient.delete_file(groupName, remoteFileName);
        log.info("删除文件成功"+i);
    }



    public static StorageClient getStorageClient() throws IOException {
        initProperties();
        TrackerServer trackerServer = getTrackerServer();
        return new StorageClient(trackerServer);

    }
    public static TrackerServer getTrackerServer() throws IOException {
        initProperties();
        TrackerClient trackerClient = new TrackerClient();
        return trackerClient.getTrackerServer();

    }

}
