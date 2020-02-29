package com.yy.framework.core.sql.xml;

import java.io.File;
import java.io.FilenameFilter;

public class XmlFilenameFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
		return name.endsWith("xml") || dir.isDirectory();
	}

}
