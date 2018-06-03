package com.pinyougou.sellergoods.service;
import java.util.List;

import com.pinyougou.common.entity.PageResult;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.vo.GoodsVo;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface GoodsService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbGoods> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum,int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(GoodsVo goods);
	
	
	/**
	 * 修改
	 */
	public void update(GoodsVo goods);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public GoodsVo findOne(Long id);
	
	
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
	public PageResult findPage(TbGoods goods, int pageNum,int pageSize);
	
	/**
	 * 根据多个ID来修改商品的状态
	 */
	public void updateStatus(Long[] ids, String status);
	
	
}
