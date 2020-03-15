package life.hrx.weibo.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import life.hrx.weibo.security.auth.imagecode.CaptchaImageCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 该类用来返回用户请求的验证码
 */


@Controller
public class CaptchaController {
    @Autowired
    private DefaultKaptcha captchaProducer; //单例类，共用一个对象

    /**
     * 该类用来设置将验证码谜底对象设置到session中即CaptchaImageCode对象，设置到session中
     * @param session
     * @param response
     */

    @RequestMapping(value = "/kaptcha")
    public void kaptcha(HttpSession session, HttpServletResponse response) throws IOException {
        //响应图片的固定格式
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");

        String capText=captchaProducer.createText();//生成验证码谜底

        session.setAttribute("captcha_key",new CaptchaImageCode(capText,2*60));//两分钟过期

        try (ServletOutputStream outputStream=response.getOutputStream();){
            BufferedImage bufferedImage = captchaProducer.createImage(capText);//生成验证码图片
            ImageIO.write(bufferedImage,"jpg",outputStream);
            outputStream.flush();
        }

    }




}
