package com.yy.framework.commons.file;

import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * <p>类名: ZipUtils</p>
 * <p>描述: 文件操作公共类</p>
 * <p>公司： www.eversec.com.cn</p>
 * <p>修改时间: 2015年11月28日 下午12:12:05</p> 
 * @author lidongyang@eversec.cn
 * @author mateng@eversec.cn
 */
public class ZipUtils {
	
	/** 
	 * <p>方法名: fileToZip</p>
	 * <p>描述: 压缩文件</p>
	 * <p>修改时间: 2015年11月28日 上午11:39:23</p>  
	 * @author lidongyang@eversec.cn  
	 * @param fileNames 文件名的数组
	 * @param zipFilePath zip文件路径
	 */
    public static void fileToZip(String[] fileNames ,String zipFilePath) {  
    	FileInputStream fis = null;
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		
		File zipFile = new File(zipFilePath);
		try {
			fos = new FileOutputStream(zipFile);
			zos = new ZipOutputStream(new BufferedOutputStream(fos));
			for (int i = 0; i < fileNames.length; i++) {
				File sourceFile = new File(fileNames[i]);
				if (!sourceFile.exists()) {
					throw new RuntimeException("待压缩的文件目录：" + fileNames[i] + "不存在.");
				} 
				byte[] bufs = new byte[1024 * 10];
				// 创建ZIP实体，并添加进压缩包
				ZipEntry zipEntry = new ZipEntry(sourceFile.getName());
				zos.putNextEntry(zipEntry);
				// 读取待压缩的文件并写进压缩包里
				fis = new FileInputStream(sourceFile);
				bis = new BufferedInputStream(fis, 1024 * 10);
				int read = 0;
				while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
					zos.write(bufs, 0, read);
				}
				bis.close();
				fis.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			// 关闭流
			if (null != bis){
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(null != fis){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != zos){
				try {
					zos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(null != fos){
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    }  
    
    /**
     * <p>方法名: unZip</p>
     * <p>描述: 解压文件到指定目录</p>
     * <p>修改时间: 2015年11月29日 下午12:07:04</p>  
     * @author lidongyang@eversec.cn  
     * @param toPath 解压后文件存放路径，toPath为空时默认加压到zip文件的当前目录下
     * @param zipFile 要解压的文件路径
     */
    public static void unZip(String toPath, String zipFile) {
		if (StringUtils.isBlank(toPath)) {
			File objZipFile = new File(zipFile);
			toPath = objZipFile.getParent();
		}
		
		File toDir = new File(toPath);
		if(!toDir.exists()) {
			toDir.mkdirs();
		}
		
		OutputStream os = null;
		InputStream is = null;
		ZipFile zfile = null;
		try {
			zfile = new ZipFile(zipFile);
			Enumeration<?> zList = zfile.entries();
			ZipEntry ze = null;
			byte[] buf = new byte[1024];
			while (zList.hasMoreElements()) {
				ze = (ZipEntry) zList.nextElement();
				if (ze.isDirectory()) {
					String dir = (toPath + File.separator + ze.getName());
					File file = new File(dir);
					if (!file.exists()) {
						file.mkdirs();
					}
				} else {
					String filePath = (toPath + File.separator + ze.getName());
					is = zfile.getInputStream(ze);
					os = new FileOutputStream(filePath);
					int readLen = 0;
					while ((readLen = is.read(buf, 0, 1024)) != -1) {
						os.write(buf, 0, readLen);
					}
					is.close();
					os.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			// 关闭流
			if (null != is){
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(null != os){
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != zfile){
				try {
					zfile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

    
    /**
     * <p>方法名: getZipFileNames</p>
     * <p>描述: 获取zip文件根目录下的文件名称</p>
     * <p>修改时间: 2015年11月29日 下午1:46:17</p>  
     * @author lidongyang@eversec.cn  
     * @param zipFile
     * @return
     */
    public static List<String> getZipFileNames (String zipFile) {
    	List<String> zipFileNames = new ArrayList<String>();
   
		ZipFile zfile = null;
		try {
			zfile = new ZipFile(zipFile);
			Enumeration<?> zList = zfile.entries();
			ZipEntry ze = null;
			while (zList.hasMoreElements()) {
				ze = (ZipEntry) zList.nextElement();
				zipFileNames.add( ze.getName());	
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(null != zfile) {
				try {
					zfile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    	return zipFileNames; 	
    }
	
	public static void main(String[] args) {
		String[] fileNames = new String[3];
		fileNames[0] = "D:/导出测试.xls";
		fileNames[1] = "D:/导出测试 - 副本.xls";
		fileNames[2] = "D:/导出测试 - 副本 (2).xls";
		String outZipFileName = "D:/test.zip";
		fileToZip(fileNames, outZipFileName);
		
		unZip("D:/test", outZipFileName);
		
		List<String> zipFileNames = getZipFileNames(outZipFileName);
		for(String name : zipFileNames){
			System.out.println("name="+name);
		}
		
	}
}
