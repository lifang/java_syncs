package com.comdosoft.serverReadZip;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
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
	private static String dateDay;
	private static String id;
	private static String hour;
	private static String path = "//opt//projects//public//bam_public//syncs_data//";
	private static String urlPath = "http://official.gankao.co//syncs_data//";
	private static String logName = "log.log";
	
	public static void main(String[] args) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		date = df.format(new Date());
		dateDay = date.substring(0, 7);
		DownloadTxt(urlPath + dateDay + "//" + date + "//" + logName, 
				path+ dateDay + "//" + date + "//" + logName);
		readTxt(path + dateDay + "//" + date + "//" + logName);
		DownloadFile(urlPath + dateDay + "//" + date + "//" + hour + "//" + id
				+ ".zip", path + dateDay + "//" + date + "//" + hour + "//"
				+ id + ".zip");
		System.out.println(urlPath + dateDay + "//" + date + "//" + hour + "//"
				+ id + ".zip");
		ReadZip.loadZipFile(path + dateDay + "//" + date + "//" + hour + "//"
				+ id + ".zip");
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
		createFile(path + dateDay + "//" + date + "//" + hour);
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
				System.out.println("DownZip Success!");
				os.close();
				in.close();
			} catch (FileNotFoundException e) {
				try {
					BufferedWriter bw = new BufferedWriter(new FileWriter(path
							+ "LOG.txt", true));
					bw.write(date + "    " + "未找到zip文件  \r\n");
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

	public static void DownloadTxt(String url, String target) {
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
		createFile(path + dateDay + "//" + date);
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
				System.out.println("DownLog Success!");
				os.close();
				in.close();
			} catch (FileNotFoundException e) {
				try {
					BufferedWriter bw = new BufferedWriter(new FileWriter(path
							+ "Log.txt", true));
					bw.write(date + "    " + "未找到txt文件  \r\n");
					bw.flush();
					bw.close();
					System.out.println("未找到txt文件");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (IOException e) {
			}
		}
	}

	public static void readTxt(String url) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(url));
			String s;
			String[] arrStr = new String[2];
			while ((s = reader.readLine()) != null) {
				arrStr = s.split("\\|\\|");
				id = arrStr[0];
				hour = arrStr[1];
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void createFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
	}
}
