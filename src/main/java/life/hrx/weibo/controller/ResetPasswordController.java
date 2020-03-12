package life.hrx.weibo.controller;
import life.hrx.weibo.dto.ResultDTO;
import life.hrx.weibo.exception.CustomizeErrorCode;
import life.hrx.weibo.model.User;
import life.hrx.weibo.service.SendEmailService;
import life.hrx.weibo.service.UserService;
import life.hrx.weibo.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

@Slf4j
@Controller
public class ResetPasswordController {

    @Autowired
    private SendEmailService sendEmailService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    /**
     * 密码重置邮箱get视图
     * @return
     */
    @RequestMapping(value = "/resetemail")
    public String resetEmail(){
        return "resetemail";
    }


    /**
     * 发送邮件操作
     * @param email
     * @param authentication
     * @return
     * @throws MessagingException
     */
    @RequestMapping(value = "/resetemail",method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO resetEmail(@RequestParam(value = "email",required = false) String email, Authentication authentication) throws MessagingException {

//        //如果已经登录。那么就让他直接跳转到首页
//        if (SecurityContextHolder.getContext().getAuthentication()!=null){
//            return ResultDTO.errorOf(CustomizeErrorCode.USER_ALREADY_LOGIN,"/");
//        }
        User user = userService.findByEmail(email);
        if (user==null){
            ResultDTO.errorOf(CustomizeErrorCode.EMAIL_NOT_FOUND); //这里虽然给前端返回了，但是就不在页面上显示邮箱未注册了
        }else{
            String token = jwtTokenUtil.generateToken(user);
            sendEmailService.sendEmail(email,token);
        }
        return ResultDTO.okOf();
    }


    /**
     * 重置密码视图
     * @param token
     * @return
     */
    @RequestMapping(value = "/resetpassword/{token}")
    public String resetPassword(@PathVariable("token") String token){
        //验证token,如果token不是我们数据库中的id,或者token已经过期，那么就直接跳转到首页
        if (!jwtTokenUtil.validateToken(token)){
            return "redirect:/";
        }
        return "resetpassword";
    }

    @RequestMapping(value = "/resetpassword/{token}",method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO resetPassword(@RequestParam(value = "username",required = false) String username,
                                @RequestParam(value ="password",required = false) String password,
                                @RequestParam(value = "password1",required = false) String password1,
                                @PathVariable("token") String token, Model model){

        //如果用户在提交的时候，token过期，那么前端逻辑会根据错误码，提示令牌非法或已过期
        if (!jwtTokenUtil.validateToken(token)){
            return ResultDTO.errorOf(CustomizeErrorCode.TOKEN_ERROR);
        }

        //如果没有找到用户
        User user = userService.findByName(username);
        if (user==null){
            return ResultDTO.errorOf(CustomizeErrorCode.USERNAME_ERROR);
        }
        //如果输入的用户用户名和token中的不一致
        if (!StringUtils.equals(String.valueOf(user.getId()),jwtTokenUtil.getUserIdFromToken(token))){
            return ResultDTO.errorOf(CustomizeErrorCode.USER_ERROR);
        }

        //如果两次密码输入不一致
        if (!StringUtils.equals(password,password1)){
            return ResultDTO.errorOf(CustomizeErrorCode.PASSWORD_DIFFERENT);
        }

        String userIdFromToken = jwtTokenUtil.getUserIdFromToken(token);

        userService.resetPasswordByUserId(Long.valueOf(userIdFromToken),password);
        jwtTokenUtil.removeToken(token);
        return ResultDTO.okOf();

    }

}
