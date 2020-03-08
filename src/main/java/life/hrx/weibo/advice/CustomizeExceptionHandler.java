package life.hrx.weibo.advice;


import life.hrx.weibo.exception.CustomizeException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;


@ControllerAdvice
public class CustomizeExceptionHandler {

    /**
     * 抛出我们自定义的异常类
     * @param e
     * @param model
     * @return
     */

    @ExceptionHandler(CustomizeException.class)
    public ModelAndView myHandle(Throwable e ,Model model){
        model.addAttribute("message",e.getMessage());
        return new ModelAndView("error");

    }

}
