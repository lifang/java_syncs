package com.comdosoft.serverReadZip;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DownZip {

	private static String date;
	private static String path = "F:\\file\\";

	public static void main(String[] args) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		date = df.format(new Date());
		DownloadFile("http://admin.gankao.co/zip_dirs/" + date.substring(0, 7)
				+ "//" + date + "//2.zip", "F:\\file\\" + date.substring(0, 7)
				+ "\\" + date + ".zip");
	}

	public static void DownloadFile(String url, String target) {
		URLConnection con = null;
		URL theUrl = null;
		try {
			theUrl = new URL(url);
			con = theUrl.openConnection();
			con.setConnectTimeout(3000);
			con.connect();
		} catch (MalformedURLException e) {
		} catch (IOException e) {
		}
		createFile();
		String type = con.getContentType();
		if (type != null) {
			byte[] buffer = new byte[4 * 1024];
			int read;
			try {
				FileOutputStream os = new FileOutputStream(target);
				InputStream in = con.getInputStream();
				while ((read = in.read(buffer)) > 0) {
					os.write(buffer, 0, read);
				}
				ReadZip.loadZipFile(path + date.substring(0, 7) + "\\" + date
						+ ".zip");
				System.out.println("success");
				os.close();
				in.close();
			} catch (FileNotFoundException e) {
				try {
					BufferedWriter bw  = new BufferedWriter(new FileWriter(path+"LOG.txt",true));    
					bw.write(date+"    "+"未找到zip文件  \r\n");    
					bw.flush();
					bw.close();
					System.out.println("未找到zip文件");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (IOException e) {
			}
		} 
	}

	public static void createFile() {
		File file = new File(path + date.substring(0, 7));
		if (!file.exists()) {
			file.mkdirs();
		}
	}
}
