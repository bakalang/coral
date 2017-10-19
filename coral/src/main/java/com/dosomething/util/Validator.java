package com.dosomething.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dosomething.commons.exceptions.Deviation;


/**
 * server side check
 * 
 */
public class Validator {

	private Validator() {
		throw new AssertionError();
	}

	public static void isTrue(boolean value, String message) throws Deviation {
		if (!value) {
			throw new Deviation(message);
		}
	}

	public static boolean isEmpty(String value) {
		if (value == null || value.trim().length() == 0) {
			return true;
		}
		return false;
	}

	public static void isEmpty(String value, String exceptionMessage) throws Deviation {
		if (!isEmpty(value)) {
			throw new Deviation(exceptionMessage);
		}
	}

	public static boolean notEmpty(String value) {
		return !isEmpty(value);
	}

	public static void notEmpty(String value, String exceptionMessage) throws Deviation {
		if (!notEmpty(value)) {
			throw new Deviation(exceptionMessage);
		}
	}

	public static boolean isValidatedDate(String value) {
		if (isEmpty(value)) {
			return false;
		}
		// if (Pattern.matches("\\d{2,2}-\\d{2,2}-\\d{4,4}", value)) {
		if (Pattern.matches("\\d{2,2}/\\d{2,2}/\\d{4,4}", value)) {
			return true;
		}
		return false;
	}

	public static void isValidatedDate(String value, String exceptionMessage) throws Deviation {
		if (!isValidatedDate(value)) {
			throw new Deviation(exceptionMessage);
		}
	}

	public static boolean isValidatedUserID(String value) {
		return isAlphaNumeric(value, false);
	}

	public static boolean isValidatedLoginName(String value) {
		if (isEmpty(value)) {
			return false;
		}// (?=^[a-zA-Z].*$)字首為字母
		if (Pattern.matches("^(?=^[0-9a-zA-Z]+$).{6,15}$", value)) {
			return true;
		}
		return false;
	}

	private static Pattern passwordBasePattern = Pattern
		.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,15}$");// 至少一個大寫,
																			// 一個小寫,
																			// 一個數字,
																			// 不含特殊符號

	public static boolean isValidatedPassword(String value) {
		if (isEmpty(value)) {
			return false;
		}
		return passwordBasePattern.matcher(value).matches();
	}

	private static Pattern opPasswordBasePattern = Pattern.compile("^[0-9a-zA-Z]{8,15}$");// 可避免特殊符號!@#$%^&*()

	public static boolean isValidatedOpPassword(String value) {
		if (isEmpty(value)) {
			return false;
		}
		if (!opPasswordBasePattern.matcher(value).matches()) {
			return false;
		}
		return checkSpecialPasswordRule(value);
	}

	private static boolean checkSpecialPasswordRule(String value) {
		byte[] valueBytes = value.getBytes();
		String checkedLetter = "";
		String checkedDigit = "";
		boolean charOk = false;
		boolean digitOk = false;
		for (byte valueByte : valueBytes) {
			// 97~122 小寫
			// 65~90 大寫
			if (charOk && digitOk) {
				return true;
			}
			if (!charOk && (valueByte >= 65 && valueByte <= 90 || valueByte >= 97 && valueByte <= 122)) {
				if (checkedLetter.length() > 0
					&& !checkedLetter.contains(Character.toString((char) valueByte))) {
					charOk = true;
					if (digitOk) {
						return true;
					}
					continue;
				}
				checkedLetter += (char) valueByte;
			}
			// 48~57 數字
			if (!digitOk && valueByte >= 48 && valueByte < 57) {
				if (checkedDigit.length() > 0 && !checkedDigit.contains(Character.toString((char) valueByte))) {
					digitOk = true;
					if (charOk) {
						return true;
					}
					continue;
				}
				checkedDigit += (char) valueByte;
			}
		}
		return false;
	}

	private static Pattern securityCodeBasePattern = Pattern.compile("[0-9]{6}");
	private static Pattern allSamDigitPattern = Pattern.compile("([0-9])\\1\\1+");

	public static boolean isValidatedSecurityCode(String value) {
		if (isEmpty(value)) {
			return false;
		}
		if (!securityCodeBasePattern.matcher(value).matches()) {
			return false;
		}
		if (allSamDigitPattern.matcher(value).matches()) {// All same digit
			return false;
		}
		if (Pattern.matches(".*" + value + "{1}.*", "0123456789")) {
			return false;
		}
		if (Pattern.matches(".*" + value + "{1}.*", "9876543210")) {
			return false;
		}
		return true;
	}

	public static boolean isNumeric(String value) {
		if (isEmpty(value)) {
			return false;
		}
		if (Pattern.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+", value)) {
			return true;
		}
		return false;
	}

	public static boolean isAlphaNumeric(String value, boolean isContainSpace) {
		if (isEmpty(value)) {
			return false;
		}
		Matcher matcher = null;
		if (isContainSpace) {
			matcher = Pattern.compile("^[0-9a-zA-z\\-\\ ]+$").matcher(value);
		} else {
			matcher = Pattern.compile("^[0-9a-zA-z\\-]+$").matcher(value);
		}
		if (!matcher.matches()) {
			return false;
		}
		return true;
	}

	public static void isNumeric(String value, String exceptionMessage) throws Deviation {
		if (!isNumeric(value)) {
			throw new Deviation(exceptionMessage);
		}
	}

	public static boolean inRange(double value, double minValue, double maxValue) {
		if (value < minValue || value > maxValue) {
			return false;
		}
		return true;
	}

	public static void inRange(double value, double minValue, double maxValue, String exceptionMessage)
		throws Deviation {
		if (!inRange(value, minValue, maxValue)) {
			throw new Deviation(exceptionMessage);
		}
	}

	public static boolean includesNumeric(int value, int[] array) throws Deviation {
		for (int i = 0, len = array.length; i < len; i++) {
			if (value == array[i]) {
				return true;
			}
		}
		return false;
	}

	public static void includesNumeric(int value, int[] array, String exceptionMessage) throws Deviation {
		if (!includesNumeric(value, array)) {
			throw new Deviation(exceptionMessage);
		}
	}

	public static boolean isDouble(String value) {
		try {
			Double.parseDouble(value);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static boolean isPositiveInteger(String value) {
		if (isEmpty(value)) {
			return false;
		}
		if (Pattern.matches("^\\d+$", value)) {
			return true;
		}
		return false;
	}

	public static void isPositiveInteger(String value, String exceptionMessage) throws Deviation {
		if (!isPositiveInteger(value)) {
			throw new Deviation(exceptionMessage);
		}
	}

	public static boolean isValidatedTax(String value) {
		if (isEmpty(value)) {
			return false;
		}
		if (Pattern.matches("^0(\\.\\d{0,4})?$", value)) {
			return true;
		}
		return false;
	}

	public static boolean isPositiveDouble(String value) {
		if (isEmpty(value)) {
			return false;
		}
		if (Pattern.matches("^\\d+(\\.\\d+)?$", value)) {
			return true;
		}
		return false;
	}

	public static void isPositiveDouble(String value, String exceptionMessage) throws Deviation {
		if (!isPositiveDouble(value)) {
			throw new Deviation(exceptionMessage);
		}
	}

	public static boolean includesDouble(double value, double[] array) {
		for (int i = 0, len = array.length; i < len; i++) {
			if (value == array[i]) {
				return true;
			}
		}
		return false;
	}

	public static void includesDouble(double value, double[] array, String exceptionMessage) throws Deviation {
		if (!includesDouble(value, array)) {
			throw new Deviation(exceptionMessage);
		}
	}

	public static boolean isValidatedExchangeRebate(String value) {
		if (isDouble(value) && Math.abs(Double.parseDouble(value)) < 10) {// ExchangeRebate資料庫欄位精準度為[5,4].
																			// 不可超過"個位數"
			return true;
		}
		return false;
	}

	public static boolean isValidatedExchangeTax(String value) {
		if (isPositiveDouble(value)) {
			return true;
		}
		return false;
	}

	public static boolean isValidatedCommission(String value) {
		if (isEmpty(value)) {
			return false;
		}
		if (Pattern.matches("^0(\\.\\d{0,4})?$", value)) {
			return true;
		}
		return false;
	}

	public static boolean isIpAddress(String value) {
		if (isEmpty(value)) {
			return false;
		}
		if (Pattern.matches(
			"^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$", value)) {
			return true;
		}
		return false;
	}
	
	public static boolean isValidatedEMail(String value) {
		if (isEmpty(value)) {
			return false;
		}
		if (Pattern.matches("^[_a-zA-z0-9-]+([.][_a-zA-z0-9-]+)*@[a-zA-z0-9-]+([.][a-zA-z0-9-]+)*$", value)) {			
			return true;
		}
		return false;
	}
}