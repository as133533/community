package life.hrx.weibo.advice;


import life.hrx.weibo.exception.CustomizeException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class CustomizeExceptionHandler {

    @ExceptionHandler(CustomizeException.class)
    public ModelAndView handle(Throwable e ,Model model){
        model.addAttribute("message",e.getMessage());
        return new ModelAndView("error");

    }

}
