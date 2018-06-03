 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location ,goodsService, uploadService,itemCatService,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(){	
		//$location 要让$location来获取那么URL中必须AngularJS的规则：加#
		//参数说明：$location获取路由URL中的参数的；search()：查找的URL中的左右的参数；3、['id']：指定参数的名称
		var id = $location.search()['id']
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;	
				//把商品介绍赋值到富文本编辑中
				editor.html($scope.entity.goodsDesc.introduction);
				//把图片数据转成对象
				$scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);
				//把扩展数据转成对象
				$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);
				//把规格转成对象
				$scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
				
				//把商品的规格SKU获取出来转对象
				//把集合遍历出来，把每一个对象中的spec属性值转成对象
				for(var i = 0; i < $scope.entity.itemList.length; i++){
					$scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
				}
				
				
			}
		);				
	}
	
	//保存 
	$scope.save=function(){		
		$scope.entity.goodsDesc.introduction = editor.html();
		var serviceObject;//服务层对象  				
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
//		        	$scope.reloadList();//重新加载
		        	location.href="goods.html";
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	//新增商品
	$scope.add=function(){
		//获取富文本编辑器中的额数据Html
		$scope.entity.goodsDesc.introduction = editor.html();
		goodsService.add($scope.entity).success(
				function(response){
					if(response.success){
						//提示添加成功
						alert(response.message);
						//清空数据
						$scope.entity={};
					}else{
						alert(response.message);
					}
				}		
			);	
	}
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
	//定义一个数组
//	$scope.entity.goodsDesc.itemImages={};
	
	$scope.uploadFile=function(){
		uploadService.uploadFile().success(function(response){
			if(response.success){
				$scope.image_entity.url=response.message;
			}else{
				alert(response.message);
			}
			
		});
	}
	
	//声明一个数组：专门来保存前段的images_entity
	$scope.entity={goodsDesc:{itemImages:[],specificationItems:[]}};
	$scope.add_images_entity=function(){
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
	}
	
	$scope.dele_image_entity=function(index){
		$scope.entity.goodsDesc.itemImages.splice(index,1);
	}
	
	$scope.findItemCatList=function(parentId){
		itemCatService.findByParentId(parentId).success(function(response){
			$scope.itemCat1List=response;
		});
	}
	//监控一级下拉框数据的时候那么触发事件
	//参数说明：1、变动之后的新值！2、变动之前的值
	$scope.$watch('entity.goods.category1Id',function(newValue,oldValue){
		itemCatService.findByParentId(newValue).success(function(response){
			$scope.itemCat2List=response;
		});
	});
	$scope.$watch('entity.goods.category2Id',function(newValue,oldValue){
		itemCatService.findByParentId(newValue).success(function(response){
			$scope.itemCat3List=response;
		});
	});
	//监控第三级动的时候，获取模板的ID
	$scope.$watch('entity.goods.category3Id',function(newValue,oldValue){
		itemCatService.findOne(newValue).success(function(response){
			$scope.entity.goods.typeTemplateId=response.typeId;
		});
	});
	//同上分析，根据模板的ID变动的时候，获取品牌数据
	$scope.$watch('entity.goods.typeTemplateId',function(newValue,oldValue){
		//1、根据模板ID查询品牌列表
		typeTemplateService.findOne(newValue).success(function(response){
			$scope.typeTemplate=response;
			$scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);
			
			//2、根据模板ID查询扩展属性
			if($location.search()['id'] == null){
				$scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
			}
			
		});
		
		//2、根据模板的ID查询的规格选项的列表
		typeTemplateService.findSpecList(newValue).success(function(response){
			$scope.specList=response;
		});
		
	});
	
	
	$scope.updateSpecAttribute=function($event,name,value){
		//获取这个集合
		var items = $scope.entity.goodsDesc.specificationItems;
		//判断此集合中是否存在要添加的这个attributeName值
		var objectName = $scope.searchObjectByKey(items,'attributeName',name);
		
		if(objectName != null){
			if($event.target.checked){//选中了
				objectName.attributeValue.push(value);
			}else{//取消勾选
				//根据这个value值获取到集合中的位置，然后删除
				objectName.attributeValue.splice(objectName.attributeValue.indexOf(value),1);
				//如果点击取消，当attributeValue里面是空的时候，直接把对象attributeName也给清空了
				if(objectName.attributeValue.length == 0){
					var idx = items.indexOf(objectName);
					items.splice(idx,1);
				}
			}
		} else{
			items.push({"attributeName":name,"attributeValue":[value]});
		}
		
	}
	
	//创建SKU列表
	$scope.createItemList=function(){
		//定义一个集合；格式一定，但是为空
		$scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0' }];//初始
		
		//把我们的规格选项的这个集合获取过来
		var items=  $scope.entity.goodsDesc.specificationItems;	
		
		for(var i=0;i< items.length;i++){
			//添加列表的方法
			//$scope.entity.itemList : 是后台添加商品规格列表的集合
			$scope.entity.itemList = addColumn( $scope.entity.itemList,items[i].attributeName,items[i].attributeValue );    
		}	
	}
	
	//添加列值 
	addColumn=function(list,columnName,conlumnValues){
		var newList=[];//新的集合
		for(var i=0;i<list.length;i++){
			var oldRow= list[i];
			for(var j=0;j<conlumnValues.length;j++){
				//JSON.stringify( oldRow ): 把对象转json
				//JSON.parse : 把json转对象
				var newRow= JSON.parse( JSON.stringify( oldRow )  );//深克隆(新的)
				newRow.spec[columnName]=conlumnValues[j];
				newList.push(newRow);
			}    		 
		} 		
		return newList;
	}

	//定义一个数组变量：根据坐标来设置状态;
	$scope.statusList=["未审核","审核通过","已驳回","审核关闭"];
	
	//定义一个集合
	$scope.itemCatList=[];
	$scope.selectItemCatList=function(){
		itemCatService.findAll().success(function(response){
			for(var i = 0; i < response.length; i++){
				$scope.itemCatList[response[i].id]=response[i].name;
			}
		});
	}
	
	//定义一个方法来判断规格选项是否在数据哭中存在
	$scope.checkAttributeValue=function(attributeName,attributeValue){
		
		//获取集合
		var items = $scope.entity.goodsDesc.specificationItems;
		
		var object = $scope.searchObjectByKey(items,'attributeName',attributeName);
		if(object == null){
			return false;
		} else{
			if(object.attributeValue.indexOf(attributeValue) >= 0 ){
				return true;
			}else{
				return false;
			}
		}
		
	}
	
	//点击修改跳转页面
	$scope.goToEdit=function(id){
		location.href="/admin/goods_edit.html#?id="+id;
	}
	
});	
