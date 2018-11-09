package com.wjb.zero.util.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;


public class Ftp {

	/**
	 * 
	 * <b>登陆ftp 返回ftpClient事件<b>
	 * 
	 * @param ip
	 *            ftp所在ip
	 * @param user
	 *            登陆名
	 * @param password
	 *            密码
	 */
	public static FTPClient ftp(String ip, String user, String password) {

		/*
		 * FTPClient ftpClient = new FTPClient(); try { ftpClient.connect(ip);
		 * ftpClient.login(user, password); } catch (SocketException e) {
		 * e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }
		 * 
		 * if (!ftpClient.isConnected()) { ftpClient = null; }
		 * 
		 * return ftpClient;
		 */
		int intPort = Integer.parseInt(ip.substring(ip.lastIndexOf(":") + 1));
		String strIp = ip.substring(0, ip.indexOf(":"));
		FTPClient ftpClient = new FTPClient();
		FTPClientConfig ftpClientConfig = new FTPClientConfig();
		ftpClientConfig.setServerTimeZoneId(TimeZone.getDefault().getID());
		ftpClient.setControlEncoding("utf-8");
		ftpClient.configure(ftpClientConfig);
		try {
			ftpClient.connect(strIp, intPort);
			// FTP服务器连接回答
			int reply = ftpClient.getReplyCode();
			if (FTPReply.isPositiveCompletion(reply)) {
				ftpClient.login(user, password);
				// 设置传输协议
				ftpClient.enterLocalPassiveMode();
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
				// 设置缓冲区大小
				ftpClient.setBufferSize(1024 * 2);
				// 设置连接超时
				ftpClient.setDataTimeout(30 * 1000);

			} else {
				ftpClient.disconnect();
				ftpClient = null;

			}
		} catch (Exception e) {
			ftpClient = null;
			e.printStackTrace();

		}
		return ftpClient;
	}

	/**
	 * 退出关闭ftp服务器链接
	 * 
	 * @param ftpClient
	 */
	public static void ftpLogOut(FTPClient ftpClient) {
		if (null != ftpClient && ftpClient.isConnected()) {
			try {
				boolean reuslt = ftpClient.logout();// 退出FTP服务器
				if (reuslt) {
					System.out.println("成功退出服务器");
				}
			} catch (IOException e) {
				e.printStackTrace();

			} finally {
				try {
					ftpClient.disconnect();// 关闭FTP服务器的连接
				} catch (IOException e) {
					e.printStackTrace();

				}
			}
		}
	}


	/**
	 * 使用CSVRecord
	 * 
	 * @param ftp1
	 */
	public static ArrayList<Map<String, Object>> listMapUtil(FTPClient ftp1, String fileName, String pathFile,
			String[] key) {
		ArrayList<Map<String, Object>> csvList = new ArrayList<Map<String, Object>>();
		if (null != ftp1) {
			try {

				// 进入需要访问的目录
				ftp1.changeWorkingDirectory(pathFile);
				// 从ftp上获取"pahtFile"目录下的文件
				FTPFile[] file = ftp1.listFiles();

				// 遍历所有文件，匹配需要查找的文件
				for (int i = 0; i < file.length; i++) {
					// 匹配到则进入
					if (file[i].getName().contains(fileName)) {
						CSVParser parser = new CSVParser(
								new InputStreamReader(ftp1.retrieveFileStream(file[i].getName()),
										Charset.forName("GBK")),
								CSVFormat.DEFAULT.withHeader(key));

						for (CSVRecord record : parser) {
							// String [] keys=key.split(",");
							Map<String, Object> channelMap = new HashMap<String, Object>();
							for (int k = 0; k < key.length; k++) {
								channelMap.put(key[k], record.get(key[k]));
							}
							csvList.add(channelMap);
						}
						parser.close();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return csvList;
	}
	
	 public static ArrayList<Map<String, Object>>  readFile(FTPClient ftp1, String fileName, String pathFile,String[] key) throws ParseException {
		 ArrayList<Map<String, Object>> txtList = new ArrayList<Map<String, Object>>();  
		 InputStream ins = null;
	    	if (null != ftp1) {
	        try {
	            // 从服务器上读取指定的文件
	        	// 进入需要访问的目录
				ftp1.changeWorkingDirectory(pathFile);
				// 从ftp上获取"pahtFile"目录下的文件
				FTPFile[] file = ftp1.listFiles();

				// 遍历所有文件，匹配需要查找的文件
				for (int i = 0; i < file.length; i++) {
					// 匹配到则进入
					if (file[i].getName().contains(fileName)) {
						ins=  ftp1.retrieveFileStream(file[i].getName());
						 BufferedReader reader = new BufferedReader(new InputStreamReader(ins,Charset.forName("GBK")));
				            String line;
				          
				            while ((line = reader.readLine()) != null) {
			               // System.out.println(line);
			                String [] lineTxt=line.split(",");
			                Map<String, Object> channelMap = new HashMap<String, Object>();
							for (int k = 0; k < lineTxt.length; k++) {
								channelMap.put(key[k], lineTxt[k]);
								
							}
							txtList.add(channelMap);
				            }
				            reader.close();
				            if (ins != null) {
				                ins.close();
				            }
				           
					}
				}
	            
				 // 主动调用一次getReply()把接下来的226消费掉. 这样做是可以解决这个返回null问题
	          //  ftp1.getReply();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    	
	    }
	        return txtList;
	    }

}