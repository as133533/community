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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//登录controller
@Controller
public class LoginController {

    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String login(){

        return "login";
    }

    /**
     * 登录失败提示信息
     * @param model
     * @return
     */
    @RequestMapping(value = "/login.fail",method = RequestMethod.GET)
    public String loginFail(Model model){
        model.addAttribute("loginMessage","用户名不存在或密码不正确");
        return "login";
    }


//    //登录的post视图
//    @RequestMapping(value = "/login",method = RequestMethod.POST)
//    public String login(@RequestParam(value = "name",required = false) String name,
//                        @RequestParam(value = "password",required =false ) String password,
//                        Model model,
//                        HttpServletRequest request,
//                        HttpServletResponse response
//                        ){
//        model.addAttribute("name",name);
//
//        if (StringUtils.isBlank(name)){
//            model.addAttribute("error","用户名不能为空");
//            return "login";
//        }
//        if (StringUtils.isBlank(password)){
//            model.addAttribute("error","密码不能为空");
//            return "login";
//        }
//        User user = userService.findByName(name);
//        if (user == null){
//            model.addAttribute("error","用户不存在，请检查用户名");
//            return "login";
//        }
//        boolean authPassword = userService.isAuthPassword(name, password);
//        if (!authPassword){
//            model.addAttribute("error","密码错误，请重新输入");
//            return "login";
//        }
//
//        //写cookie和session
////        response.addCookie(new Cookie("token",user.getToken()));
////        request.getSession().setAttribute("user",user);
//        return "redirect:/";
//    }


}
