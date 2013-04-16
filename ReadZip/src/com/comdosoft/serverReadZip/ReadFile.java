package com.comdosoft.serverReadZip;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReadFile {
	// /opt/projects/public/headoffice_public/syncs_data/
	private static String path = "/opt/projects/public/headoffice_public/syncs_data/";
	private static String x = File.separator;
	private static String hour = "";
	private static String count = "";
	private static boolean hourFlag = false;
	private static String date;
	private static String dateDay;
	private static String mLogName = "myLog.log";

	public static void main(String[] args) {
		System.out.println("开始执行!");
		long runTime = System.currentTimeMillis();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		date = df.format(new Date());
		dateDay = date.substring(0, 7);
		createLog();
		readHour();
		readFile(path + dateDay + x + date);
		writeLog(path + dateDay + x + date + x + mLogName);
		System.out.println("数据已同步!用时:" + (System.currentTimeMillis() - runTime)
				/ 1000f + "s");
	}

	public static void readFile(String filePach) {
		File dayFile = new File(filePach);
		if (dayFile.isDirectory()) {
			String[] dayStr = dayFile.list();
			for (int k = 0; k < dayStr.length; k++) {
				File file = new File(filePach + x + dayStr[k]);
				if (file.isDirectory()) {
					count = dayStr[k];
				}
				if (hourFlag && file.isDirectory()) {
					if (Integer.parseInt(dayStr[k]) > Integer.parseInt(hour)) {
						readZipFile(filePach + x + dayStr[k]);
					}
				} else {
					readZipFile(filePach + x + dayStr[k]);
				}
			}
		}
	}

	public static void readZipFile(String path) {
		File hourFileX = new File(path);
		if (hourFileX.isDirectory()) {
			String[] hourStr = hourFileX.list();
			for (int l = 0; l < hourStr.length; l++) {
				System.out.println(hourFileX + x + hourStr[l]);
				ReadZip.loadZipFile(hourFileX + x + hourStr[l]);
			}
		}
	}

	public static void readHour() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path + x
					+ dateDay + x + date + x + mLogName));
			String s;
			while ((s = reader.readLine()) != null) {
				hour = s;
			}
			if (!hour.equals("") && !hour.isEmpty()) {
				hourFlag = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeLog(String path) {
		try {
			BufferedWriter bf = new BufferedWriter(new FileWriter(path));
			bf.write(count);
			bf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void createLog() {
		try {
			File file = new File(path + x + dateDay + x + date + x + mLogName);
			if (!file.exists())
				file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
