package life.hrx.weibo.controller;


import life.hrx.weibo.model.User;
import life.hrx.weibo.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.Email;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;


//注册视图
@Controller
public class RegisterController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/register",method = RequestMethod.GET)
    public String register(){
        return "register";
    }

    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public String register(Model model, @RequestParam(value = "name",required = false) String username,
                           @RequestParam(value = "email",required = false) String email,
                           @RequestParam(value = "password",required = false) String password,
                           @RequestParam(value = "password1",required = false) String password1) throws NoSuchAlgorithmException {

        //先将值返回，用于在页面上显示
        model.addAttribute("name",username);
        model.addAttribute("email",email);
        if (StringUtils.isBlank(username)){
            model.addAttribute("error","用户名不能为空");
            return "register";
        }
        if (StringUtils.isBlank(email)){
            model.addAttribute("error","邮箱不能为空");
            return "register";
        }

        if (!Pattern.matches("\\w+@(\\w+.)+[a-z]{2,3}",email)){
            model.addAttribute("error","邮箱格式错误");
            return "register";
        }

        if (StringUtils.isBlank(password)){
            model.addAttribute("error","请输入密码");
            return "register";
        }
        if (StringUtils.isBlank(password1)){
            model.addAttribute("error","请输入确认密码");
            return "register";
        }

        if (!password.equals(password1)){
            model.addAttribute("error","两次输入密码不相同");
            return "register";
        }

        User user = userService.findByName(username);
        if (user != null){
            model.addAttribute("error","用户名已经被注册，请重新填写用户名");
            return "register";
        }
        boolean byEmail = userService.findByEmail(email);
        if (byEmail){
            model.addAttribute("error","该邮箱已被注册，请换一个邮箱");
            return "register";
        }

        userService.hashPassowrd(username,email,password);



        return "redirect:/login";

    }

}
