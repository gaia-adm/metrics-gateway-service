package com.hp.gaia.mgs.spring;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by belozovs on 6/8/2015.
 */
@Controller
public class DefaultController {

    @RequestMapping("/")
    public ModelAndView login(){
        return  new ModelAndView("index");
    }

}
