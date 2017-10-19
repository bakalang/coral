package com.dosomething.commons.model;

import com.dosomething.commons.annotation.HttpUpdate;

public class Setting {
	
		// JavaScript File Version
		@HttpUpdate
		public static int JS_FILE_VERSION = 0;
		
		@HttpUpdate
		public static boolean ENABLE_API_POOLING_HTTP_CONNECTION = true;

		public static boolean ENABLE_CONNECTION_SHOW_SQL = false;
		public static boolean ENABLE_CONNECTION_DEBUG = false;
}
