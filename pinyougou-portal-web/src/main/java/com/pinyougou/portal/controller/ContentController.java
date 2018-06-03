package com.pinyougou.portal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.TbContent;

@RestController
@RequestMapping("content")
public class ContentController {

	@Reference
	private ContentService contentService;
	
	//根据分类ID来查询的内容列表
	@RequestMapping("findContentByCategoryId")
	public List<TbContent> findContentByCategoryId(Long categoryId){
		return this.contentService.findContentByCategoryId(categoryId);
	}
	
}
