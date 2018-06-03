package com.itheima.dubbox.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dubbox.service.TestService;
@Service
public class TsetServiceImpl implements TestService {

	public String queryString() {
		// TODO Auto-generated method stub
		return "www.itheima.com";
	}

}
