package com.comdosoft.uploadimage;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class UpLoadImage {
	URL url;
	HttpURLConnection conn;
	String boundary = "--------httppost123";
	Map<String, String> textParams = new HashMap<String, String>();
	Map<String, File> fileparams = new HashMap<String, File>();
	Map<String, String> pathparams = new HashMap<String, String>();
	DataOutputStream ds;

	public UpLoadImage(String url) throws Exception {
		this.url = new URL(url);
	}

	public UpLoadImage() {
	}

	public void setUrl(String url) throws Exception {
		this.url = new URL(url);
	}

	public void addTextParameter(String name, String value) {
		textParams.put(name, value);
	}

	public void addFileParameter(String name, File value) {
		fileparams.put(name, value);
	}

	public void addPathParameter(String name, String value) {
		pathparams.put(name, value);
	}

	public void clearAllParameters() {
		textParams.clear();
		fileparams.clear();
	}

	public byte[] send() throws Exception {
		initConnection();
		try {
			conn.connect();
		} catch (SocketTimeoutException e) {
			throw new RuntimeException();
		}
		ds = new DataOutputStream(conn.getOutputStream());
		writeFileParams();
		// writeStringParams();
		paramsEnd();
		InputStream in = conn.getInputStream();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int b;
		while ((b = in.read()) != -1) {
			out.write(b);
		}
		conn.disconnect();
		return out.toByteArray();
	}

	private void initConnection() throws Exception {
		conn = (HttpURLConnection) this.url.openConnection();
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setConnectTimeout(10000); // 连接超时为10秒
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type",
				"multipart/form-data; boundary=" + boundary);
	}

	// 普通字符串数据
	// private void writeStringParams() throws Exception {
	// Set<String> keySet = textParams.keySet();
	// for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
	// String name = it.next();
	// String value = textParams.get(name);
	// ds.writeBytes("--" + boundary + "\r\n");
	// ds.writeBytes("Content-Disposition: form-data; name=\"" + name
	// + "\"\r\n");
	// ds.writeBytes("\r\n");
	// ds.writeBytes(encode(value) + "\r\n");
	// }
	// }
	// 文件数据
	private void writeFileParams() throws Exception {
		Set<String> keySet = fileparams.keySet();
		for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
			String name = it.next();
			File value = fileparams.get(name);
			ds.writeBytes("--" + boundary + "\r\n");
			ds.writeBytes("Content-Disposition: form-data; name=\"" + name
					+ "\"; path=\"" + encode(pathparams.get(name))
					+ "\"; filename=\"" + encode(value.getName()) + "\"\r\n");
			ds.writeBytes("Content-Type: " + getContentType(value) + "\r\n");
			ds.writeBytes("\r\n");
			ds.write(getBytes(value));
			ds.writeBytes("\r\n");
		}
	}

	// 获取文件的上传类型，图片格式为image/png,image/jpg等。非图片为application/octet-stream
	private String getContentType(File f) throws Exception {

		// return "application/octet-stream"; //
		// 此行不再细分是否为图片，全部作为application/octet-stream
		// 类型
		ImageInputStream imagein = ImageIO.createImageInputStream(f);
		if (imagein == null) {
			return "application/octet-stream";
		}
		Iterator<ImageReader> it = ImageIO.getImageReaders(imagein);
		if (!it.hasNext()) {
			imagein.close();
			return "application/octet-stream";
		}
		imagein.close();
		return "image/" + it.next().getFormatName().toLowerCase();

	}

	private byte[] getBytes(File f) throws Exception {
		FileInputStream in = new FileInputStream(f);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int n;
		while ((n = in.read(b)) != -1) {
			out.write(b, 0, n);
		}
		in.close();
		return out.toByteArray();
	}

	// 添加结尾数据
	private void paramsEnd() throws Exception {
		ds.writeBytes("--" + boundary + "--" + "\r\n");
		ds.writeBytes("\r\n");
	}

	// 转码
	private String encode(String value) throws Exception {
		return URLEncoder.encode(value, "UTF-8");
	}

	public void start(String URL, String filepath, String pathParam) {
		UpLoadImage u;
		try {
			u = new UpLoadImage(URL);
			u.addFileParameter("url", new File(filepath));
			u.addPathParameter("url", pathParam);
			System.out.println("图片发送中...");
			byte[] b = u.send();
			String result = new String(b);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}