package com.itheima.fastDFS;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

public class fastDFSTest {

	@Test
	public void test() throws Exception {
	//1.加载配置文件，配置文件的内容就是tracker服务的地址
		ClientGlobal.init(System.getProperty("user.dir")+"/src/main/resources/tracker_server.conf");
		//2、创建TrackerClient客户端
		TrackerClient trackerClient = new TrackerClient();
		//3、由这个TrackerClient来创建TrackerServer对象
		TrackerServer trackerServer = trackerClient.getConnection();
		//4、声明一个StorageServer为 null
		StorageServer storageServer = null;
		//5、StorageClient对象来上传图片：需要两个参数1、TrackerServer；2、StorageServer
		StorageClient client = new StorageClient(trackerServer, storageServer);
		//6、StorageClient对象来上传图片，返回数组：{包含这个组名和文件路径}
				//参数说明：1、指定文件路径；2、指定文件的扩展名（不包含.）
		String[] strings = client.upload_file("E:/PinYouGouXiangGuan/312007.jpg", "jpg", null);
		//7、打印图片地址
		for (String string : strings) {
			System.err.println(string);
		}
		
	}

	@Test
	public void dir(){
		System.out.println(System.getProperty("user.dir"));
	}
}
