package life.hrx.weibo.controller;
import life.hrx.weibo.security.auth.smscode.SmsCode;
import life.hrx.weibo.dto.ResultDTO;
import life.hrx.weibo.config.AliSmsConfig;
import life.hrx.weibo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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



        SmsCode smsCode = new SmsCode(RandomStringUtils.randomNumeric(6), 120, phone);

        //调用阿里云sdk发送短信
        aliSmsConfig.sendSms(phone,smsCode.getCode());

        session.setAttribute("sms_key",smsCode);//保存验证码

        return ResultDTO.okOf();
    }

    @RequestMapping(value = "/smslogin")
    public String smsLogin(){
        return "smslogin";
    }

}
