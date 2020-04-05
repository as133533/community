package life.hrx.weibo.config;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

@Configuration
//@PropertySource(value = {"classpath:a.properties"},encoding = "UTF-8")
public class AliSmsConfig {
    @Value("${AliSms.accessKeyId}")
    private String accessKeyId;

    @Value("${AliSms.accessKeySecret}")
    private String accessKeySecret;

    @Value("${AliSms.signName}")
    private String signName;

    @Value("${AliSms.templateCode}")
    private String templateCode;

    public void sendSms(String phone,String code){
        //设置超时时间-可自行调整

        //初始化ascClient需要的几个参数

        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phone);


        //这个必须加入，因为spring boot读取properties的配置为ISO_8859_1。即使将文件类型改为utf-8也会因为读取方式的不同而乱码,未来会寻求一个更有效的方法
        this.signName=new String(signName.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParam", "{\"code\":\""+code+"\"}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }


    }

}
