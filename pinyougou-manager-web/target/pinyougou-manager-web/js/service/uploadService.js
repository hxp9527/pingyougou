app.service('uploadService',function($http){
	
	this.uploadFile=function(){
		
		//H5前段语言来创建一个表单对象FormData
		var formData = new FormData();
		//添加表单数据
		formData.append('file',file.files[0]);//指定上传文件的在表单的第一个位置
		
		return $http({
			url:'../upload.do',
			method:'POST',
			data:formData,
			headers:{'Content-Type':undefined},//如果设置了此类型那么就是让表单上传文件是默认是"multiPart/form-data"
			transformRequest : angular.identity 
		});
	}
	
});