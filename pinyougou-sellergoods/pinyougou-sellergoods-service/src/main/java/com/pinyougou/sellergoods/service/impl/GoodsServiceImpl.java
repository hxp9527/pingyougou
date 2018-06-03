package com.pinyougou.sellergoods.service.impl;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.common.entity.PageResult;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbGoodsExample;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.pojo.vo.GoodsVo;
import com.pinyougou.sellergoods.service.GoodsService;

/**
 * 服务实现层
 * @author Administrator
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	
	@Autowired
	private TbGoodsDescMapper goodsDescMapper; 
	
	@Autowired
	private TbItemMapper itemMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	@Autowired
	private TbBrandMapper brandMapper;
	
	@Autowired
	private TbSellerMapper sellerMapper;
	
	/**
	 * 增加
	 */
	@Override
	public void add(GoodsVo goodsVo) {
		//添加商品及商品扩展
		goodsVo.getGoods().setAuditStatus("0");
		goodsMapper.insert(goodsVo.getGoods());	
		
		//添加商品扩展
		//把商品扩展中商品的ID设置
		goodsVo.getGoodsDesc().setGoodsId(goodsVo.getGoods().getId());
		this.goodsDescMapper.insertSelective(goodsVo.getGoodsDesc());
		int i = 1/0;
		//添加SKU
		saveItemList(goodsVo);
		
	}

	private void setItemValus(GoodsVo goods,TbItem item) {
		
		item.setGoodsId(goods.getGoods().getId());//商品SPU编号
		item.setSellerId(goods.getGoods().getSellerId());//商家编号
		item.setCategoryid(goods.getGoods().getCategory3Id());//商品分类编号（3级）
		item.setCreateTime(new Date());//创建日期
		item.setUpdateTime(new Date());//修改日期 
		
		//品牌名称
		TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
		item.setBrand(brand.getName());
		//分类名称
		TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
		item.setCategory(itemCat.getName());
		
		//商家名称
		TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
		item.setSeller(seller.getNickName());
		
		//图片地址（取spu的第一个图片）
		List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class) ;
		if(imageList.size()>0){
			item.setImage ( (String)imageList.get(0).get("url"));
		}

	}
	//添加商品的SKU
	private void saveItemList(GoodsVo goodsVo){
		if("1".equals(goodsVo.getGoods().getIsEnableSpec())){
			//商品的规格表
			for (TbItem item : goodsVo.getItemList()) {
				
				String title= goodsVo.getGoods().getGoodsName();
				Map<String,Object> specMap = JSON.parseObject(item.getSpec());
				for(String key:specMap.keySet()){
					title+=" "+ specMap.get(key);
				}
				item.setTitle(title);
				setItemValus(goodsVo,item);

				this.itemMapper.insertSelective(item);
			}
		} else{
			TbItem item=new TbItem();
			item.setTitle(goodsVo.getGoods().getGoodsName());//商品KPU+规格描述串作为SKU名称
			item.setPrice( goodsVo.getGoods().getPrice() );//价格			
			item.setStatus("1");//状态
			item.setIsDefault("1");//是否默认			
			item.setNum(99999);//库存数量
			item.setSpec("{}");			
			setItemValus(goodsVo,item);	
			this.itemMapper.insertSelective(item);
		}
	}
	
	/**
	 * 修改
	 */
	@Override
	public void update(GoodsVo goodsVo){
		//修改完后要保存还是把状态为0
		goodsVo.getGoods().setAuditStatus("0");
		goodsMapper.updateByPrimaryKey(goodsVo.getGoods());
		
		//修改商品的扩展信息
		goodsDescMapper.updateByPrimaryKeySelective(goodsVo.getGoodsDesc());
		
		//修改SKU
		//先删除修改的商品的ID的这些SKU，然后再添加
		TbItemExample example = new TbItemExample();
		example.createCriteria().andGoodsIdEqualTo(goodsVo.getGoods().getId());
		itemMapper.deleteByExample(example);
		
		//再添加
		//添加SKU
		saveItemList(goodsVo);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public GoodsVo findOne(Long id){
		
		GoodsVo vo = new GoodsVo();
		//查询商品基本信息
		TbGoods goods = goodsMapper.selectByPrimaryKey(id);
		vo.setGoods(goods);
		//查询商品的扩展信息
		TbGoodsDesc goodsDesc = this.goodsDescMapper.selectByPrimaryKey(id);
		vo.setGoodsDesc(goodsDesc);
		//过会再写itemList
		//获取SKU
		TbItemExample example = new TbItemExample();
		example.createCriteria().andGoodsIdEqualTo(id);
		List<TbItem> list = this.itemMapper.selectByExample(example);
		vo.setItemList(list);
		
		return vo;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
//			goodsMapper.deleteByPrimaryKey(id);
			
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			//修改状态
			goods.setIsDelete("1");
			goodsMapper.updateByPrimaryKeySelective(goods);
			
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		//在查询的额时候要设置
		criteria.andIsDeleteIsNull();
		
		if(goods!=null){			
			if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

		@Override
		public void updateStatus(Long[] ids, String status) {
			for (Long id : ids) {
				//先查询出来，然后再修改
				TbGoods goods = this.goodsMapper.selectByPrimaryKey(id);
				//设置状态
				goods.setAuditStatus(status);
				this.goodsMapper.updateByPrimaryKeySelective(goods);
			}
			
		}
	
}
