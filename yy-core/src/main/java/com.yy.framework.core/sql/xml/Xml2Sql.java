package com.yy.framework.core.sql.xml;

import com.yy.framework.core.sql.vo.SqlCondition;
import com.yy.framework.core.sql.vo.SqlScript;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

public class Xml2Sql {
	
	private static Logger logger = Logger.getLogger(Xml2Sql.class.getName());
	
	private FilenameFilter filenameFilter;
	private Map<String, SqlScript> sqlScriptMap;
	
	public Xml2Sql() {
		this.filenameFilter = new XmlFilenameFilter();
		this.sqlScriptMap = new HashMap<>();
	}
	
	public SqlScript getSqlScript(String namespace, String id) {
		return this.sqlScriptMap.get(namespace + "." + id);
	}
	
	public void scanXml(String pkg) {
		String scanPkg = "";
		if(StringUtils.isNotBlank(pkg)) {
			scanPkg = pkg.replace(".", "/");
		}
		URL xmlpath = this.getClass().getClassLoader().getResource(scanPkg);
		File dir = new File(xmlpath.getPath());
		logger.info("scan path: " + dir);
		if(!dir.exists()) {
			return;
		}
		
		List<File> files = new ArrayList<>();
		findXml(dir, files);
		
		for (File file : files) {
			SAXReader reader = new SAXReader();
			logger.info("file path: " + file);
			Document document = null;
			try {
				document = reader.read(file);
			} catch (DocumentException e) {
				e.printStackTrace();
				logger.warning("解析xml失败，" + e.getMessage());
			}
			if(document != null) {
				Element root = document.getRootElement();
				parseSql(file.getAbsolutePath(), root.attributeValue("namespace"), document);
			}
		}
	}
	
	private void parseSql(String location, String namespace, Document document) {
		Element root = document.getRootElement();
		Iterator<Element> iterator = root.elementIterator();
		
		while (iterator.hasNext()) {
			Element element = iterator.next();
			
			SqlScript sqlScript = new SqlScript();
			sqlScript.setNamespace(namespace);
			sqlScript.setLocation(location);
			sqlScript.setId(element.attributeValue("id"));
			sqlScript.setData(element.getStringValue());
			
			List<SqlCondition> conditions = new ArrayList<>();
			Iterator<Element> childIterator = element.elementIterator();
			while (childIterator.hasNext()) {
				Element child = childIterator.next();
				SqlCondition condition = new SqlCondition();
				condition.setField(child.attributeValue("property"));
				condition.setFieldType(child.attributeValue("type"));
				condition.setType(child.getName());
				condition.setText(child.getText());
				conditions.add(condition);
			}
			sqlScript.setConditions(conditions);
			this.sqlScriptMap.put(sqlScript.key(), sqlScript);
		}
	}

	private void findXml(File file, List<File> files) {
		if(file.isDirectory()) {
			File[] children = file.listFiles(filenameFilter);
			for (File child : children) {
				findXml(child, files);
			}
		}else {
			if(file.getName().endsWith("xml")) {
				files.add(file);
			}
		}
	}
}
