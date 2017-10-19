package com.dosomething.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Props extends Properties {

	private static final long serialVersionUID = -1368358106311563329L;
	public static final String SYSPR_PROPBASEDIR = "properties.base.dir";
	private static Props theInstance;

	private static final Logger logger = Logger.getLogger(Props.class.getName());

	public Props(String filename) {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
		if (is == null) {
			String baseDir = getSystemProperty("properties.base.dir");
			baseDir = baseDir + File.separator;
			baseDir = "";
			String wholePath = baseDir + filename;
			File f = new File(wholePath);
			try {
				is = new FileInputStream(f);
			} catch (FileNotFoundException e) {
				System.err.println(wholePath);
				logger.log(Level.SEVERE, "Properties not found", e);
			}
		}
		try {
			load(is);
		} catch (IOException e) {
			logger.log(Level.SEVERE,"Properties load failure", e);
		}
	}

	public static Props getInstance(String properties_file) {
		if (theInstance == null) {
			theInstance = new Props(properties_file);
		}
		return theInstance;
	}

	public Properties getProperties(String prefix) {
		Properties props = new Properties();
		if (prefix != null) {
			Enumeration _enum = propertyNames();
			while (_enum.hasMoreElements()) {
				String key = (String) _enum.nextElement();
				if (key.startsWith(prefix)) {
					String newKey = key.substring(prefix.length());
					if ((newKey != null) && (newKey.length() > 0)) {
						if (newKey.charAt(0) == '.') {
							newKey = newKey.substring(1);
						}
						props.put(newKey, getProperty(key));
					}
				}
			}
		}
		return props;
	}

	public static String getSystemProperty(String name) {
		return System.getProperty(name);
	}
}
