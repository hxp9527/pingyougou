package com.pinyougou.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.common.entity.PageResult;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.pojo.vo.SpecificationVo;
import com.pinyougou.sellergoods.service.SpecificationService;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;
	
	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSpecification specification) {
		specificationMapper.insert(specification);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbSpecification specification){
		specificationMapper.updateByPrimaryKey(specification);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbSpecification findOne(Long id){
		return specificationMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//删除的规格
			specificationMapper.deleteByPrimaryKey(id);
			
			//删除规格选项
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			example.createCriteria().andSpecIdEqualTo(id);
			this.specificationOptionMapper.deleteByExample(example);
		}
		
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

		@Override
		public void save(SpecificationVo specificationVo) {
			
			//保存规格
			this.specificationMapper.insert(specificationVo.getSpecification());
			
			//保存规格选项
			for (TbSpecificationOption option : specificationVo.getSpecificationOptionList()) {
				option.setSpecId(specificationVo.getSpecification().getId());
				this.specificationOptionMapper.insertSelective(option);
			}
			
		}

		@Override
		public SpecificationVo findVo(Long id) {
			
			//先查询规格
			TbSpecification specification = this.specificationMapper.selectByPrimaryKey(id);
			//再查询规格选项
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			example.createCriteria().andSpecIdEqualTo(id);
			List<TbSpecificationOption> specificationOptionList = this.specificationOptionMapper.selectByExample(example);
			//封装返回对象
			SpecificationVo vo = new SpecificationVo();
			vo.setSpecification(specification);
			vo.setSpecificationOptionList(specificationOptionList);
			
			return vo;
		}

		@Override
		public void updateVo(SpecificationVo specificationVo) {
			
			//修改规格
			this.specificationMapper.updateByPrimaryKeySelective(specificationVo.getSpecification());
			
			//直接删除再添加
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			example.createCriteria().andSpecIdEqualTo(specificationVo.getSpecification().getId());
			this.specificationOptionMapper.deleteByExample(example);
			
			//遍历添加
			for (TbSpecificationOption option : specificationVo.getSpecificationOptionList()) {
				option.setSpecId(specificationVo.getSpecification().getId());
				this.specificationOptionMapper.insertSelective(option);
			}
			
		}

		@Override
		public List<Map> selectSpecificationList() {
			List<Map> selectSpecificationList = this.specificationMapper.selectSpecificationList();
			return selectSpecificationList;
		}
	
}
