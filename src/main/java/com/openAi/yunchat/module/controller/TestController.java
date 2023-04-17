package com.openAi.yunchat.module.controller;

import com.openAi.yunchat.core.base.JsonResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestController {

    @RequestMapping("/sayHello")
    public JsonResult<String> sayHello(){
        return JsonResult.ok("Holle World!");
    }
}
