package life.hrx.weibo.controller;
import life.hrx.weibo.dto.RegisterCheckDTO;
import life.hrx.weibo.model.User;
import life.hrx.weibo.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;


/**
 * 注册视图,这一块暂时挺乱的，将来需要重构
 */
@Controller
public class RegisterController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/register",method = RequestMethod.GET)
    public String register(){
        return "register";
    }

    /**
     *
     * @param registerCheckDTO
     * @return 返回0代表可以被注册。返回1代表不能被注册
     */
    @RequestMapping(value = "/registercheck",method = RequestMethod.POST)
    @ResponseBody
    public Integer register(@RequestBody RegisterCheckDTO registerCheckDTO){
        Integer type=registerCheckDTO.getType();
        if (type!=1&&type!=2&&type!=3){
            return null;
        }
        if (registerCheckDTO.getType()==1){
            User user = userService.findByName(registerCheckDTO.getCheckName());
            if (user ==null){
                return 0;
            }
        }else if (registerCheckDTO.getType()==2){
            if (userService.findByEmail(registerCheckDTO.getCheckName())==null ){
                return 0;
            }
        }
        else{
            if (!userService.findByPhone(registerCheckDTO.getCheckName())){
                return 0;
            }
        }
        return 1;
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
        if (StringUtils.length(username)<3 || StringUtils.length(username) >15){
            model.addAttribute("userMsg","用户名必须是3-15个字符");
            return "register";
        }
        if (StringUtils.length(password)<6||StringUtils.length(password)>16){
            model.addAttribute("pwdMsg","密码必须是6-16个字符");
            return "register";
        }
        if (StringUtils.isBlank(username)){
            model.addAttribute("userMsg","用户名不能为空");
            return "register";
        }
        if (StringUtils.isBlank(email)){
            model.addAttribute("emailMsg","邮箱不能为空");
            return "register";
        }

        if (!Pattern.matches("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$",email)){
            model.addAttribute("emailMsg","邮箱格式错误");
            return "register";
        }

        if (StringUtils.isBlank(phone)){
            model.addAttribute("phoneMsg","电话号码不能为空");
            return "register";
        }
        if (!Pattern.matches("^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\\d{8}$",phone)){
            model.addAttribute("phoneMsg","电话号码格式错误，请输入正确的电话号码");
            return "register";
        }

        if (StringUtils.isBlank(password)){
            model.addAttribute("pwdMsg","请输入密码");
            return "register";
        }
        if (StringUtils.isBlank(password1)){
            model.addAttribute("pwdMsg","请输入确认密码");
            return "register";
        }

        if (!password.equals(password1)){
            model.addAttribute("pwdMsg","两次输入密码不相同");
            return "register";
        }

        User user = userService.findByName(username);
        if (user != null){
            model.addAttribute("userMsg","用户名已经被注册，请重新填写用户名");
            return "register";
        }

        if (userService.findByEmail(email) != null){
            model.addAttribute("emailMsg","该邮箱已被注册，请换一个邮箱");
            return "register";
        }

        userService.hashPassowrd(username,email,password,phone);
        return "redirect:/login";

    }

}
