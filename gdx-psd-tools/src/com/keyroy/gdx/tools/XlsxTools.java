package com.keyroy.gdx.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.json.m.JSONObject;

public class XlsxTools {
	public static String version = "v1.0.3";
	
	public static String importFolder = "excel";

	public static String jsonFolder = "json";

	public static String jsonZipFolder = "json zip";

	public static boolean format = false;
	// 合并json
	public static boolean merge = true;
	
	public static final String CHANGELOG_STRING = "更新日志：\n"
			+ "v1.0.3 \n修改 使用 zip 的 json 文件名后缀为 bin";

	public static void main(String[] args) {
		HashMap<String, String> cmds = new HashMap<String, String>();
		String[] arrayOfString1 = args;
		int j = args.length;
		for (int i = 0; i < j; i++) {
			String cmd = arrayOfString1[i];
			try {
				String[] sp = cmd.split("=");
				String key = sp[0].trim();
				String val = sp[1].trim();				
				cmds.put(key, val);
			} catch (Exception localException) {
			}
		}
		if (cmds.containsKey("importFolder")) {
			importFolder = (String) cmds.get("importFolder");
		}

		if (cmds.containsKey("jsonFolder")) {
			jsonFolder = (String) cmds.get("jsonFolder");
		}

		if (cmds.containsKey("jsonZipFolder")) {
			jsonZipFolder = (String) cmds.get("jsonZipFolder");
		}
		if (cmds.containsKey("format")) {
			format = true;
		} else {
			format = false;
		}

		if (cmds.containsKey("merge")) {
			merge = true;
		} else {
			merge = false;
		}
		
//		if(cmds.containsKey("changelog")) {
//			
//		}
		System.out.println(CHANGELOG_STRING);
		System.out.println();
//		Logcat logcat = new Logcat("E:\\test.txt");
		System.out.println("工具版本: "+version);
		System.out.println("格式化: " + format);
		System.out.println("合并 json: " + merge);
		System.out.println("文档输入目录: " + importFolder);
		System.out.println("json 输出目录: " + jsonFolder);
		System.out.println("json zip 输出目录: " + jsonFolder);
		System.out.println("当前目录："+System.getProperty("user.dir"));
		System.out.println("--------------开始执行程序--------------");
		execute();
		System.out.println("--------------程序执行完毕--------------");
		System.out.println("------------- 工具版本: "+version+" --------------");
	}

	public static final void execute() {
		createFolder(jsonFolder);
		createFolder(jsonZipFolder);
		
		File folder = new File(importFolder);
		if (folder.exists()) {
			File[] files = folder.listFiles();
			if (files != null) {
				for (File file : files) {
					String fileName = file.getName();

					if (fileName != null && fileName.startsWith("!")) {
						System.out.println("跳过文件：" + fileName);
						continue;
					}
					try {
						List<JsonPack> arrays = XlsxParser.parser(file);
						// 合并数据
						if (merge) {
							if(arrays.size() == 0) {
								continue;
							}
							String baseName = fileName.split("\\.")[0];
							JSONObject object = new JSONObject();
							for (JsonPack jsonPack : arrays) {
								object.put(jsonPack.getName(), jsonPack.getJsonObject());
							}
							JsonPack mergeJsonPack = new JsonPack(baseName + "_config", object);
							File jsonFile = writeJson(new File(jsonFolder), mergeJsonPack, false);
							jsonFile = writeJson(new File(jsonZipFolder), mergeJsonPack, true);
							System.out.println("write json : " + jsonFile.getName());
						} else {
							// 不合并数据
							for (JsonPack jsonPack : arrays) {
								File jsonFile = writeJson(new File(jsonFolder), jsonPack, false);
								jsonFile = writeJson(new File(jsonZipFolder), jsonPack, true);
								System.out.println("write json : " + jsonFile.getName());
							}
						}

					} catch (Exception e) {
						System.out.println("the catch msg " + e.getMessage());
						e.printStackTrace();
					}
				}
			}

		}
	}

	private static final File writeJson(File jsonFolder, JsonPack jsonPack, boolean zip) throws Exception {
		File jsonFile = new File(jsonFolder, jsonPack.getName() + (zip? ".bin":".json"));
		String json = null;
		if (format)
			json = jsonPack.getJsonObject().toString(2);
		else {
			json = jsonPack.getJsonObject().toString();
		}
		FileOutputStream fileOutputStream = new FileOutputStream(jsonFile);
		if (zip) {
			GZIPOutputStream outputStream = new GZIPOutputStream(fileOutputStream);
			outputStream.write(json.getBytes(Charset.forName("UTF-8")));
			outputStream.close();
		} else {
//	      FileWriter fileWriter = new FileWriter(jsonFile);
//	      fileWriter.write(json);
//	      fileWriter.flush();
//	      fileWriter.close();

			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(jsonFile), "UTF-8");
			out.write(json);
			out.flush();
			out.close();
		}
		return jsonFile;
	}

	private static final File createFolder(String path) {
		File file = new File(path);
		delete(file);
		file.mkdirs();
		return file;
	}

	public static final void delete(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					delete(files[i]);
				}
			}
		}
		file.delete();
	}
}
