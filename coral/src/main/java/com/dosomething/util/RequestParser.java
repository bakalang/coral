/*
 * Created on 2005/6/5
 *
 */
package com.dosomething.util;

import javax.servlet.ServletRequest;

import com.dosomething.commons.exceptions.ParameterNotFoundException;


public class RequestParser {

	public static String getStringParameter(ServletRequest request, int maxLength, String name) throws ParameterNotFoundException {
		String[] values = request.getParameterValues(name);

		if (values == null) {
			throw new ParameterNotFoundException(name + " not found");
		}
		if (values[0].length() == 0) {
			throw new ParameterNotFoundException(name + " was empty");
		}
		if (values[0].length() > maxLength) {
			return values[0].substring(0, maxLength);
		}
		return values[0];
	}

	public static String getStringParameter(ServletRequest request, int maxLength, String name, String def) {
		try {
			return getStringParameter(request, maxLength, name);
		} catch (Exception e) {
			return def;
		}
	}

	public static String[] getStringParameterValues(ServletRequest request, int maxLength, String name) throws ParameterNotFoundException {

		String[] values = request.getParameterValues(name);
		if (values == null) {
			throw new ParameterNotFoundException(name + " not found");
		} else if (values[0].length() == 0) {
			throw new ParameterNotFoundException(name + " was empty");
		} else {
			for (int i=0; i<values.length; i++) {

				if (values[i].length() > maxLength) {
					values[i] = values[i].substring(0, maxLength);
				}
			}
			return values;
		}
	}

	public static String[] getStringParameterValues(ServletRequest request, int maxLength, String name, String[] def) {
		try {
			return getStringParameterValues(request, maxLength, name);
		} catch (Exception e) {
			return def;
		}
	}
	
	// public getString 一定要有長度限制，避免sql injection
	/*
	public static String getStringParameter(ServletRequest request, String name, String def) {
		try {
			return getStringParameter(request, name);
		} catch (Exception e) {
			return def;
		}
	}
	*/
	
	private static String getStringParameter(ServletRequest request, String name)
		throws ParameterNotFoundException {
		String[] values = request.getParameterValues(name);

		if (values == null) {
			throw new ParameterNotFoundException(name + " not found");
		} else if (values[0].length() == 0) {
			throw new ParameterNotFoundException(name + " was empty");
		} else {
			return values[0];
		}
	}

	public static int[] getIntParameterValues(ServletRequest request, String name) throws ParameterNotFoundException {
		String[] values = request.getParameterValues(name);
		if (values == null) {
			throw new ParameterNotFoundException(name + " not found");
		} else if (values[0].length() == 0) {
			throw new ParameterNotFoundException(name + " was empty");
		} else {
			int[] temp = new int[values.length];
			for (int i = 0; i < values.length; i++) {
				try {
					temp[i] = Integer.parseInt(values[i]);
				} catch (Exception e) {
				}
			}
			return temp;
		}
	}


	public static int[] getIntParameterValues(ServletRequest request, String name, int[] def) throws ParameterNotFoundException {
		try {
			return getIntParameterValues(request, name);
		} catch (Exception e) {
			return def;
		}
	}

	public static boolean getBooleanParameter(ServletRequest request, String name) throws ParameterNotFoundException, NumberFormatException {
		String value = getStringParameter(request, name).toLowerCase();
		if (value.equalsIgnoreCase("1") || (value.equalsIgnoreCase("true")) || (value.equalsIgnoreCase("on")) || (value.equalsIgnoreCase("yes"))) {
			return true;
		} else if (value.equalsIgnoreCase("0") || (value.equalsIgnoreCase("false")) || (value.equalsIgnoreCase("off")) || (value.equalsIgnoreCase("no"))) {
			return false;
		} else {
			throw new NumberFormatException("Parameter " + name + " value " + value + " is not a boolean");
		}
	}

	public static boolean getBooleanParameter(ServletRequest request, String name, boolean def) {
		try {
			return getBooleanParameter(request, name);
		} catch (Exception e) {
			return def;
		}
	}

	public static double getDoubleParameter(ServletRequest request, int maxLength, String name) throws ParameterNotFoundException, NumberFormatException {
		return Double.parseDouble(getStringParameter(request, maxLength, name).trim().replaceAll(",", ""));
	}

	public static double getDoubleParameter(ServletRequest request, String name) throws ParameterNotFoundException, NumberFormatException {
		return Double.parseDouble(getStringParameter(request, name).trim().replaceAll(",", ""));
	}

	public static double getDoubleParameter(ServletRequest request, int maxLength, String name, double def) throws ParameterNotFoundException, NumberFormatException {
		try {
			return getDoubleParameter(request, maxLength, name);
		} catch (Exception e) {
			return def;
		}
	}

	public static double getDoubleParameter(ServletRequest request, String name, double def) throws ParameterNotFoundException, NumberFormatException {
		try {
			return getDoubleParameter(request, name);
		} catch (Exception e) {
			return def;
		}
	}

	public static int getIntParameter(ServletRequest request, int maxLength, String name) throws NumberFormatException, ParameterNotFoundException {
		return Integer.parseInt(getStringParameter(request, maxLength, name).trim().replace(",", ""));
	}

	public static int getIntParameter(ServletRequest request, String name) throws NumberFormatException, ParameterNotFoundException {
		return Integer.parseInt(getStringParameter(request, name).trim().replace(",", ""));
	}

	public static int getIntParameter(ServletRequest request, int maxLength, String name, int def) {
		try {
			return getIntParameter(request, maxLength, name);
		} catch (Exception e) {
			return def;
		}
	}

	public static int getIntParameter(ServletRequest request, String name, int def) {
		try {
			return getIntParameter(request, name);
		} catch (Exception e) {
			return def;
		}
	}

	public static long getLongParameter(ServletRequest request, int maxLength, String name) {
		return Long.parseLong(getStringParameter(request, maxLength, name).trim().replaceAll(",", ""));
	}

	public static long getLongParameter(ServletRequest request, String name) {
		return Long.parseLong(getStringParameter(request, name).trim().replaceAll(",", ""));
	}

	public static long getLongParameter(ServletRequest request, int maxLength, String name, long def) {
		try {
			return getLongParameter(request, maxLength, name);
		} catch (Exception e) {
			return def;
		}
	}

	public static long getLongParameter(ServletRequest request, String name, long def) {
		try {
			return getLongParameter(request, name);
		} catch (Exception e) {
			return def;
		}
	}

	public static long[] getLongParameterValues(ServletRequest request, String name) throws ParameterNotFoundException {
		String[] values = request.getParameterValues(name);
		if (values == null) {
			throw new ParameterNotFoundException(name + " not found");
		} else if (values[0].length() == 0) {
			throw new ParameterNotFoundException(name + " was empty");
		} else {
			long[] temp = new long[values.length];
			for (int i = 0; i < values.length; i++) {
				try {
					temp[i] = Long.parseLong(values[i]);
				} catch (Exception e) {
				}
			}
			return temp;
		}
	}

}