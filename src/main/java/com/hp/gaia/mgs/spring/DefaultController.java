package com.hp.gaia.mgs.spring;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;

/**
 * Created by belozovs on 6/8/2015.
 */
@Controller
public class DefaultController {

    @PostConstruct
    void init(){
        System.out.println("Type something here...");
    }

    @RequestMapping("/")
    public ModelAndView login(){
        return  new ModelAndView("index");
    }

}
