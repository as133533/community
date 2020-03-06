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
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;


/**
 * 注册视图
 */
@Controller
public class RegisterController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/register",method = RequestMethod.GET)
    public String register(){
        return "register";
    }

    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public String register(Model model, @RequestParam(value = "username",required = false) String username,
                           @RequestParam(value = "email",required = false) String email,
                           @RequestParam(value = "password",required = false) String password,
                           @RequestParam(value = "password1",required = false) String password1,
                           @RequestParam(value="phone",required = false) String phone) throws NoSuchAlgorithmException {

        //先将值返回，用于在页面上显示
        model.addAttribute("username",username);
        model.addAttribute("email",email);
        model.addAttribute("phone",phone);
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

        if (StringUtils.isBlank(phone)){
            model.addAttribute("error","电话号码不能为空");
            return "register";
        }
        if (!Pattern.matches("0\\d{2,3}[-]?\\d{7,8}|0\\d{2,3}\\s?\\d{7,8}|13[0-9]\\d{8}|15[1089]\\d{8}",phone)){
            model.addAttribute("error","电话号码格式错误，请输入正确的电话号码");
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

        userService.hashPassowrd(username,email,password,phone);
        return "redirect:/login";

    }

}
