package com.itheima.dubbox.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.dubbox.service.TestService;

@RestController
@RequestMapping(value="test")//映射
public class TestController {
@Reference
private TestService testService;
@RequestMapping(value="hello")
public String hello(){
	return this.testService.queryString();
}
	
}
