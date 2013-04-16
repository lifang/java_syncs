package com.comdosoft.uploadimage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReadImage {
	private static String x = File.separator;
	private static String storeId;
	private static String HTTPURL = "http://headoffice.gankao.co/api/syncs_datas/syncs_pics";
	// /opt/projects/public/bam_public
	private static String ServerPath = x + "opt" + x + "projects" + x
			+ "public" + x + "bam_public" + x;
	private static String product = "products";
	private static String sales = "sales";
	private static String package_cards = "package_cards";
	private static String pathParam;
	private static List<String> proIdList = new ArrayList<String>();
	private static List<String> salIdList = new ArrayList<String>();
	private static List<String> packIdList = new ArrayList<String>();
	private static MysqlDAOImpl md = new MysqlDAOImpl();
	private static UpLoadImage ul = new UpLoadImage();

	public static void main(String[] args) {
		storeId = String.valueOf(md.getStoresId());
		readProducts();
		readSales();
		readPack();
		//ul.start(HTTPURL, "F:\\file\\ad.jpg","file\\ad.jpg");
	}

	public static void readProducts() {
		proIdList = md.getId(product);
		for (int i = 0; i < proIdList.size(); i++) {
			readUrl(getPath("product_pics", proIdList.get(i)));
		}
		System.out.println(product + "表图片同步成功!");
	}

	public static void readSales() {
		salIdList = md.getId(sales);
		for (int i = 0; i < salIdList.size(); i++) {
			readUrl(getPath("sales_pics", salIdList.get(i)));
		}
		System.out.println(sales + "表图片同步成功!");
	}

	public static void readPack() {
		packIdList = md.getId(package_cards);
		for (int i = 0; i < packIdList.size(); i++) {
			readUrl(getPath("pcard_pics", packIdList.get(i)));
		}
		System.out.println(package_cards + "表图片同步成功!");
	}

	public static String getPath(String table, String id) {
		pathParam = x + table + x + storeId + x + id;
		return ServerPath + table + x + storeId + x + id;
	}

	public static void readUrl(String path) {
		File file = new File(path);
		if (file.isDirectory()) {
			String[] strArr = file.list();
			for (int i = 0; i < strArr.length; i++) {
				System.out.println(path + x + strArr[i]);
				ul.start(HTTPURL, path + x + strArr[i],pathParam);
			}
		}
	}
}
