package life.hrx.weibo.service;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;

@Service
public class SendEmailService {

    @Autowired
    private JavaMailSenderImpl javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${web.domain}")
    private String webDomain;

    @Async //为发送邮件方法开启异步，因为发送邮件是一个很容易阻塞的方法，，未来可能加入rabbitmq
    public void sendEmail(String receiver,String token) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);//true表示允许使用模板的方式发送
        mimeMessageHelper.setSubject("【图灵社区】重置您的密码");
        mimeMessageHelper.setFrom(javaMailSender.getJavaMailProperties().getProperty("from")); //获取配置中设置好的发件人
        mimeMessageHelper.setTo(receiver);
        mimeMessageHelper.setSentDate(new Date());

        Context context = new Context();
        context.setVariable("token",token);
        if (!StringUtils.endsWith(webDomain,"/")){
            webDomain=webDomain+'/';
        }
        context.setVariable("webDomain",webDomain);
        String process = templateEngine.process("email.html", context);//使用Thymeleaf做邮件模板发送
        mimeMessageHelper.setText(process,true);//true代表发送的是html
        javaMailSender.send(mimeMessage);


    }
}
