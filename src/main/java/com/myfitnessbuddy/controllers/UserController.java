package com.myfitnessbuddy.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    // mpas this method to a http GET request
    @RequestMapping(value = "info", method = RequestMethod.GET)
    public String info(){
        return "app is running";
    }
}
