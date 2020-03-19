package life.hrx.weibo.utils;

import life.hrx.weibo.dto.FastDFSDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;

import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * FastDFS上传工具类
 */

@Slf4j
public class FastDFSUtil {
    static { //在类开始加载的时候，首先应该进行初始化
        try {
            String filePath=new ClassPathResource("application.properties").getFile().getAbsolutePath();//获得完整路径
            ClientGlobal.initByProperties(filePath);

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

    /**
     * 获得访问文件的地址
     * @return http://ip:端口(为配置中的(fastdfs.http_tracker_http_port)/
     * @throws IOException
     */
    public static String getTrackerUrl() throws IOException {
        return "http://"+getTrackerServer().getInetSocketAddress().getHostString()+":"+ClientGlobal.getG_tracker_http_port()+"/";
    }

    public static StorageClient getStorageClient() throws IOException {
        TrackerServer trackerServer = getTrackerServer();
        return new StorageClient(trackerServer);

    }
    public static TrackerServer getTrackerServer() throws IOException {

        TrackerClient trackerClient = new TrackerClient();
        return trackerClient.getTrackerServer();

    }

}
