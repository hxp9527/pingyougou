package com.pinyougou.shop.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pinyougou.common.entity.Result;
import com.pinyougou.common.util.FastDFSClient;

@RestController
@RequestMapping("upload")
public class UploadController {

	@Value("${PYG_IMAGES_URL}")
	private String PYG_IMAGES_URL;
	
	//映射到方法中
	@RequestMapping(method = RequestMethod.POST)
	public Result uploadFile(MultipartFile file){
		
		try {
			//获取文件扩展名
			String filename = file.getOriginalFilename();//文件全名
			
			String ext = filename.substring(filename.lastIndexOf(".") + 1);
			
			//上传
			FastDFSClient client = new FastDFSClient("classpath:config/tracker_server.conf");
			
			String url_s = client.uploadFile(file.getBytes(), ext);//已经把gourp1/path路径了
			
			String url = PYG_IMAGES_URL +  url_s;
			
			return new Result(true, url);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new Result(false, "上传失败");
	}
	
}
