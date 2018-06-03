package com.pinyougou.sellergoods.service;
import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.vo.SpecificationVo;
import com.pinyougou.common.entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SpecificationService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbSpecification> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum,int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbSpecification specification);
	
	
	/**
	 * 修改
	 */
	public void update(TbSpecification specification);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbSpecification findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long [] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbSpecification specification, int pageNum,int pageSize);
	
	/**
	 * 保存了规格和规格选项
	 * @param specificationVo
	 */
	void save(SpecificationVo specificationVo);

	/**
	 * 根据规格ID查询规格及规格选项
	 * @param id
	 * @return
	 */
	public SpecificationVo findVo(Long id);

	/**
	 * 修改规格及规格选项
	 * @param specificationVo
	 */
	public void updateVo(SpecificationVo specificationVo);
	
	/**
	 * 根据自定义格式查询规格列表
	 * @return
	 */
	List<Map> selectSpecificationList();
	
}
