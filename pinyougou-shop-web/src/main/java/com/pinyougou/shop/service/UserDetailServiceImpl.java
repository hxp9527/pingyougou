package com.pinyougou.shop.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;

/**
 * 认证类
 * @author lj520
 *
 */
public class UserDetailServiceImpl implements UserDetailsService {

	private SellerService sellerService;
	
	public void setSellerService(SellerService sellerService){
		this.sellerService = sellerService;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		//制定用户过来的权限列表
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		
		TbSeller seller = sellerService.findOne(username);
		if(seller != null){
			if("1".equals(seller.getStatus())){
				//如果成功返回的是User对象，成功，放行了
				//如果失败返回的是null
				//参数说明：1、提交的用户名；2、正确的密码；3、权限集合
				return new User(username, seller.getPassword(), authorities);
			} else{
				return null;
			}
		}
		return null;
	}

}
