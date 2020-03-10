package life.hrx.weibo.controller;
import life.hrx.weibo.auth.smscode.SmsCode;
import life.hrx.weibo.dto.ResultDTO;
import life.hrx.weibo.properties.AliSmsConfig;
import life.hrx.weibo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
public class SmsLoginController {

    @Autowired(required = false)
    private UserService userService;

    @Autowired
    private AliSmsConfig aliSmsConfig;

    @RequestMapping(value = "/smscode")
    @ResponseBody
    public ResultDTO smsGetCode(@RequestParam String phone, HttpSession session){

        if (!userService.findByPhone(phone)){
            //这里的结果处理需要做的更好，用枚举类来封装
            return ResultDTO.errorOf(5001,"你输入的手机号未曾注册");
        }

        SmsCode smsCode = new SmsCode(RandomStringUtils.randomNumeric(6), 120, phone);

        //TODO这里需要调用短信服务商提供的接口发送短信，但是暂时还没有，所以这里只是一个模拟
        aliSmsConfig.sendSms(phone,smsCode.getCode());

        session.setAttribute("sms_key",smsCode);//保存验证码

        return ResultDTO.okOf();
    }

    @RequestMapping(value = "/smslogin")
    public String smsLogin(){
        return "smslogin";
    }

}
