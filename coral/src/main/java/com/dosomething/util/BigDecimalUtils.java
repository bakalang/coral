package com.dosomething.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;

import org.apache.commons.lang.math.NumberUtils;

/**
 * 處理 BigDecimal 的 建立, 比較, 加減乘除
 * 
 * @author SYSTEM
 * 
 */
public class BigDecimalUtils {
	public static final BigDecimal ZERO = BigDecimal.valueOf(0);
	public static final BigDecimal NEGTIVE_ONE = BigDecimal.valueOf(-1);
	public static final BigDecimal TWO = BigDecimal.valueOf(2);
	public static final BigDecimal FIVE = BigDecimal.valueOf(5);
	public static final BigDecimal FIVE_IN_THOUSANDTH = BigDecimal.valueOf(0.005);
	public static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
	
	//new BigDecimal(double val)會有問題，ex : new BigDecimal(0.01);
	// 怕大家誤用，所以統一方式在這邊
	public static BigDecimal getInstance(double value) {
		return BigDecimal.valueOf(value);
	}
	
	public static BigDecimal getInstance(String value) {
		return new BigDecimal(value);
	}
	
	/**
	 * 加法
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static BigDecimal add(BigDecimal v1, BigDecimal v2) {
		return v1.add(v2);
	}

	/**
	 * 加法
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static BigDecimal add(BigDecimal v1, double v2) {
		return BigDecimalUtils.add(v1, new BigDecimal(Double.toString(v2)));
	}

	/**
	 * 加法
	 * 
	 * @param numbers
	 * @return
	 */
	public static BigDecimal add(Collection<Number> numbers) {

		BigDecimal b = BigDecimal.ZERO;
		for (Number number : numbers) {
			if (number instanceof BigDecimal) {
				b = b.add((BigDecimal) number);
			} else {
				b = b.add(NumberUtils.createBigDecimal(number.toString()));
			}
		}

		return b;

	}

	/**
	 * 加法
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static BigDecimal add(double v1, BigDecimal v2) {
		return BigDecimalUtils.add(new BigDecimal(Double.toString(v1)), v2);
	}

	/**
	 * 加法
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static BigDecimal add(double v1, double v2) {
		return BigDecimalUtils.add(new BigDecimal(Double.toString(v1)), new BigDecimal(Double.toString(v2)));
	}

	/**
	 * 加法
	 * 
	 * @param numbers
	 * @return
	 */
	public static BigDecimal add(Number... numbers) {

		BigDecimal b = BigDecimal.ZERO;
		for (Number number : numbers) {
			if (number instanceof BigDecimal) {
				b = b.add((BigDecimal) number);
			} else {
				b = b.add(NumberUtils.createBigDecimal(number.toString()));
			}
		}

		return b;

	}

	/**
	 * 取得 BigDecimal
	 * 
	 * @param v
	 * @return
	 */
	public static BigDecimal build(double v) {
		return v == 0 ? BigDecimal.ZERO : v == 1 ? BigDecimal.ONE : BigDecimal.valueOf(v);
	}

	/**
	 * 取得 BigDecimal
	 * 
	 * @param v
	 * @param scale
	 * @param roundingMode
	 * @return
	 */
	public static BigDecimal build(double v, int scale, RoundingMode roundingMode) {
		return BigDecimal.valueOf(v).setScale(scale, roundingMode);
	}

	/**
	 * 取得 BigDecimal
	 * 
	 * @param number
	 * @return
	 */
	public static BigDecimal build(Number number) {
		return BigDecimalUtils.build(number, null);
	}

	/**
	 * 取得 BigDecimal
	 * 
	 * @param number
	 * @param def
	 * @return
	 */
	public static BigDecimal build(Number number, BigDecimal def) {

		if (number == null) {
			return def;
		}

		if (number instanceof BigDecimal) {
			return (BigDecimal) number;
		}

		return BigDecimalUtils.build(number.doubleValue());

	}

	/**
	 * 比較
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static int compareTo(BigDecimal v1, BigDecimal v2) {
		return v1.compareTo(v2);
	}

	/**
	 * 比較
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static int compareTo(BigDecimal v1, double v2) {
		return BigDecimalUtils.compareTo(v1, new BigDecimal(Double.toString(v2)));
	}

	/**
	 * 比較
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static int compareTo(double v1, BigDecimal v2) {
		return BigDecimalUtils.compareTo(new BigDecimal(Double.toString(v1)), v2);
	}

	/**
	 * 比較
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static int compareTo(double v1, double v2) {
		return BigDecimalUtils.compareTo(new BigDecimal(Double.toString(v1)), new BigDecimal(Double.toString(v2)));
	}

	/**
	 * 除法
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static BigDecimal divide(BigDecimal v1, BigDecimal v2) {
		return v1.divide(v2, 10, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 除法
	 * 
	 * @param target
	 * @param numbers
	 * @return
	 */
	public static BigDecimal divide(BigDecimal target, Collection<Number> numbers) {

		BigDecimal b = new BigDecimal(target.toString());
		for (Number number : numbers) {
			if (number instanceof BigDecimal) {
				b = divide(b, (BigDecimal) number);
			} else {
				b = divide(b, NumberUtils.createBigDecimal(number.toString()));
			}
		}

		return b;

	}

	/**
	 * 除法
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static BigDecimal divide(BigDecimal v1, double v2) {
		return BigDecimalUtils.divide(v1, new BigDecimal(Double.toString(v2)));
	}

	/**
	 * 除法
	 * 
	 * @param target
	 * @param numbers
	 * @return
	 */
	public static BigDecimal divide(BigDecimal target, Number... numbers) {

		BigDecimal b = new BigDecimal(target.toString());
		for (Number number : numbers) {
			if (number instanceof BigDecimal) {
				b = divide(b, (BigDecimal) number);
			} else {
				b = divide(b, NumberUtils.createBigDecimal(number.toString()));
			}
		}

		return b;

	}

	/**
	 * 除法
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static BigDecimal divide(double v1, BigDecimal v2) {
		return BigDecimalUtils.divide(BigDecimalUtils.build(v1), v2);
	}

	/**
	 * 提供精確的除法運算, 當發生除不盡時, 由scale指定精準度, 以後的數字四捨五入
	 * 
	 * @param v1
	 * @param v2
	 * @param scale
	 * @return
	 */
	public static BigDecimal divide(double v1, double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		return BigDecimal.valueOf(v1).divide(BigDecimal.valueOf(v2), scale, BigDecimal.ROUND_HALF_UP);
	}

	public static BigDecimal divide(double v1, double v2, int scale, int type) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		return BigDecimal.valueOf(v1).divide(BigDecimal.valueOf(v2), scale, type);
	}

	/**
	 * 除法
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static BigDecimal divide(double v1, double v2) {
		return BigDecimalUtils.divide(new BigDecimal(Double.toString(v1)), new BigDecimal(Double.toString(v2)));
	}

	/**
	 * 提供小數點的無條件捨去
	 * 
	 * @param v
	 *            需要無條件捨去的數字
	 * @param scale
	 *            小數點後保留幾位
	 * @return 無條件捨去後的結果
	 */
	public static BigDecimal floor(double v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		return BigDecimal.valueOf(v).setScale(scale, RoundingMode.FLOOR);
	}
	
	/**
	 * 提供小數點的無條件捨去(包含負數)
	 * 
	 * @param v
	 *            需要無條件捨去的數字
	 * @param scale
	 *            小數點後保留幾位
	 * @return 無條件捨去後的結果
	 */
	public static BigDecimal down(double v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		return BigDecimal.valueOf(v).setScale(scale, RoundingMode.DOWN);
	}

	/**
	 * 乘法
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static BigDecimal multiply(BigDecimal v1, BigDecimal v2) {
		return v1.multiply(v2);
	}

	/**
	 * 乘法
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static BigDecimal multiply(BigDecimal v1, double v2) {
		return BigDecimalUtils.multiply(v1, new BigDecimal(Double.toString(v2)));
	}

	/**
	 * 乘法
	 * 
	 * @param numbers
	 * @return
	 */
	public static BigDecimal multiply(Collection<Number> numbers) {

		BigDecimal b = BigDecimal.ONE;
		for (Number number : numbers) {
			if (number instanceof BigDecimal) {
				b = b.multiply((BigDecimal) number);
			} else {
				b = b.multiply(NumberUtils.createBigDecimal(number.toString()));
			}
		}

		return b;

	}

	/**
	 * 乘法
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static BigDecimal multiply(double v1, BigDecimal v2) {
		return BigDecimalUtils.multiply(new BigDecimal(Double.toString(v1)), v2);
	}

	/**
	 * 乘法
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static BigDecimal multiply(double v1, double v2) {
		return BigDecimalUtils.multiply(new BigDecimal(Double.toString(v1)), new BigDecimal(Double.toString(v2)));
	}

	/**
	 * 乘法
	 * 
	 * @param numbers
	 * @return
	 */
	//	public static BigDecimal multiply(Number... numbers) {
	//
	//		BigDecimal b = BigDecimal.ONE;
	//		for (Number number : numbers) {
	//			if (number instanceof BigDecimal) {
	//				b = b.multiply((BigDecimal) number);
	//			} else {
	//				b = b.multiply(NumberUtils.createBigDecimal(number.toString()));
	//			}
	//		}
	//		return b;
	//	}
	public static BigDecimal multiply(BigDecimal... numbers) {
		BigDecimal b = BigDecimal.ONE;
		for (BigDecimal number : numbers) {
			b = b.multiply(number);
		}
		return b;
	}

	public static BigDecimal multiply(double... numbers) {
		BigDecimal b = BigDecimal.ONE;
		for (double number : numbers) {
			b = b.multiply(BigDecimal.valueOf(number));
		}
		return b;
	}

	/**
	 * 提供精確的小數點四捨五入處理
	 * 
	 * @param v
	 *            需要四捨五入的數字
	 * @param scale
	 *            小數點後保留幾位
	 * @return 四捨五入後的結果
	 */
	public static BigDecimal round(double v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		return BigDecimal.valueOf(v).setScale(scale, RoundingMode.HALF_UP);
	}
	
	/**
	 * 提供精確的小數點處理
	 * 
	 * @param v
	 *            需要處理的數字
	 * @param scale
	 *            小數點後保留幾位
	 * @param roundType
	 *            進位模式            
	 * @return 處理後的結果
	 */
	public static BigDecimal round(double v, int scale, int roundType) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		return BigDecimal.valueOf(v).setScale(scale, roundType);
	}	

	public static BigDecimal round(BigDecimal v, int scale, int roundType) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		return v.setScale(scale, roundType);
	}
	
	/**
	 * 減法
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static BigDecimal subtract(BigDecimal v1, BigDecimal v2) {
		return v1.subtract(v2);
	}

	/**
	 * 減法
	 * 
	 * @param target
	 * @param numbers
	 * @return
	 */
	public static BigDecimal subtract(BigDecimal target, Collection<Number> numbers) {

		BigDecimal b = new BigDecimal(target.toString());
		for (Number number : numbers) {
			if (number instanceof BigDecimal) {
				b = b.subtract((BigDecimal) number);
			} else {
				b = b.subtract(NumberUtils.createBigDecimal(number.toString()));
			}
		}

		return b;

	}

	/**
	 * 減法
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static BigDecimal subtract(BigDecimal v1, double v2) {
		return BigDecimalUtils.subtract(v1, BigDecimal.valueOf(v2));
	}

	/**
	 * 減法
	 * 
	 * @param target
	 * @param numbers
	 * @return
	 */
	public static BigDecimal subtract(BigDecimal target, Number... numbers) {
		BigDecimal b = target;
		for (Number number : numbers) {
			if (number instanceof BigDecimal) {
				b = b.subtract((BigDecimal) number);
			} else {
				b = b.subtract(NumberUtils.createBigDecimal(number.toString()));
			}
		}
		return b;
	}

	/**
	 * 減法
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static BigDecimal subtract(double v1, BigDecimal v2) {
		return BigDecimalUtils.subtract(BigDecimal.valueOf(v1), v2);
	}

	/**
	 * 減法
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static BigDecimal subtract(double v1, double v2) {
		return BigDecimalUtils.subtract(BigDecimal.valueOf(v1), BigDecimal.valueOf(v2));
	}

	private BigDecimalUtils() {
		throw new AssertionError();
	}

}
