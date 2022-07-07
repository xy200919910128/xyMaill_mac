/*
 * BigDecimalUtils.java       1.0    2015年6月10日
 *
 * Copyright (c) 2011, 2014 Tianjin YiDianFu Network Technology Co. Ltd.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * YiDianFu Network Technology Co. Ltd. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you
 * entered into with YiDianFu.
 */
package com.xy.maillThirdParty.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * BigDecimal工具类
 *
 * @author 许悦 2015年6月10日 下午3:26:56
 * @version 1.0
 */
@Slf4j
public class BigDecimalUtils {

    /** 显示用 保留小数点后2位 */
    public static final int SCALE_VIEW = 2;
    /** 显示用 保留小数点后0位 */
    public static final int SCALE_ZERO = 0;
    /** 持久化用 保留小数点后4位 */
    public static final int SCALE_RESULT = 4;
    /** 计算用 保留小数点后16位 */
    public static final int SCALE_CALCULATE = 16;

    /**
     * 设置BigDecimal模式为: 保留4位小数, 四舍五入模式为: 只舍不进
     *
     * @param input
     *            BigDecimal对象
     * @return BigDecimal对象
     *
     * @author 许悦 2015年6月11日 上午9:41:13
     * @version 1.0
     */
    public static BigDecimal getDecimal_4(BigDecimal input) {
        return getDecimal_X(input, SCALE_RESULT);
    }

    /**
     * 设置BigDecimal模式为: 保留16位小数, 四舍五入模式为: 只舍不进
     *
     * @param input
     *            BigDecimal对象
     * @return BigDecimal对象
     *
     * @author 许悦 2015年6月11日 上午9:41:13
     * @version 1.0
     */
    public static BigDecimal getDecimal_16(BigDecimal input) {
        return getDecimal_X(input, SCALE_CALCULATE);
    }

    /**
     * 设置BigDecimal模式为: 保留2位小数, 四舍五入模式为: 四舍五入
     *
     * @param input
     *            BigDecimal对象
     * @return BigDecimal对象
     *
     * @author 许悦 2015年6月11日 上午9:41:13
     * @version 1.0
     */
    public static BigDecimal getDecimal_2(BigDecimal input) {
        return getDecimal_X(input,BigDecimalUtils.SCALE_VIEW,BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 设置BigDecimal模式为: 保留2位小数, 四舍五入模式为: 只舍不进
     *
     * @param input
     *            BigDecimal对象
     * @return BigDecimal对象
     *
     * @author 许悦 2015年6月11日 上午9:41:13
     * @version 1.0
     */
    public static BigDecimal getDecimal_2_DOWN(BigDecimal input) {
        return getDecimal_X(input,BigDecimalUtils.SCALE_VIEW,BigDecimal.ROUND_DOWN);
    }

    
    /**
     * 设置BigDecimal模式为: 保留0位小数, 四舍五入模式为: 四舍五入
     *
     * @param input
     *            BigDecimal对象
     * @return BigDecimal对象
     *
     * @author 许悦 2015年6月11日 上午9:41:13
     * @version 1.0
     */
    public static BigDecimal getDecimal_0(BigDecimal input) {
        return getDecimal_X(input, SCALE_ZERO,BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 设置BigDecimal模式为: 保留0位小数, 四舍五入模式为: 四舍五入
     *
     * @param input
     *            String对象
     * @return BigDecimal对象
     *
     * @author 许悦 2015年6月11日 上午9:41:13
     * @version 1.0
     */
    public static  BigDecimal getDecimal_0(String input) {
        BigDecimal bigDecimal = null;
        if(input!=null){
             bigDecimal = new BigDecimal(input);
            bigDecimal = getDecimal_0(bigDecimal);
        }
        return  bigDecimal;
    }


    /**
     * 设置BigDecimal模式为: 保留{@code scalex}位小数 四舍五入模式为:根据calType 取舍
     *
     * @param input
     *            BigDecimal对象
     * @param scalex
     *            小数点后保留位数
     * @param calType
     *            四舍五入类型
     * @return BigDecimal对象
     *
     * @author 许悦 2015年6月11日 上午9:41:13
     * @version 1.0
     */
    public static BigDecimal getDecimal_X(BigDecimal input, int scalex, int calType) {
        return input.setScale(scalex, calType);
    }

    /**
     * 设置BigDecimal模式为: 保留{@code scalex}位小数 四舍五入模式为:只舍不入
     *
     * @param input
     *            BigDecimal对象
     * @param scalex
     *            小数点后保留位数
     * @return BigDecimal对象
     *
     * @author 许悦 2015年6月11日 上午9:41:13
     * @version 1.0
     */
    public static BigDecimal getDecimal_X(BigDecimal input, int scalex) {
        return input.setScale(scalex, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 设置BigDecimal模式为: 保留{@code scalex}位小数 四舍五入模式为:只舍不入
     *
     * @param input
     *            BigDecimal对象
     * @param scalex
     *            小数点后保留位数
     * @return BigDecimal对象
     *
     * @author 许悦 2015年6月11日 上午9:41:13
     * @version 1.0
     */
    public static BigDecimal getDecimal_X_ROUND_UP(BigDecimal input, int scalex) {
        return input.setScale(scalex, BigDecimal.ROUND_UP);
    }

    /**
     * BigDecimal模式为: 加法 四舍五入模式为: 只舍不进
     *
     * @param d1
     *            被除数
     * @param d2
     *            除数
     * @return BigDecimal对象
     *
     * @author 许悦 2015年6月11日 上午9:41:13
     * @version 1.0
     */
    public static BigDecimal add(BigDecimal d1, BigDecimal d2)
    {       
	// 进行加法运算
    return d1.add(d2);
    }

    // 进行连续加法运算
    public static BigDecimal add(BigDecimal... d)
    {
        BigDecimal sum = BigDecimal.ZERO;
        if(d!=null&&d.length>0){
            for (BigDecimal num :d ) {
               sum  = sum.add(num);
            }
        }
        return sum;
    }

    
    /**
     * BigDecimal模式为: 减法 四舍五入模式为: 只舍不进
     *
     * @param d1
     *            被除数
     * @param d2
     *            除数
     * @return BigDecimal对象
     *
     * @author 许悦 2015年6月11日 上午9:41:13
     * @version 1.0
     */
    public static BigDecimal sub(BigDecimal d1, BigDecimal d2)
    {        
    	// 进行减法运算
	    return d1.subtract(d2);
     }
    
    /**
     * BigDecimal模式为: 乘法 四舍五入模式为: 只舍不进
     *
     * @param d1
     *            被除数
     * @param d2
     *            除数
     * @return BigDecimal对象
     *
     * @author 许悦 2015年6月11日 上午9:41:13
     * @version 1.0
     */
    public static BigDecimal mul(BigDecimal d1, BigDecimal d2)
    {        
    	// 进行乘法运算
    	return d1.multiply(d2);
     }
    
    /**
     * BigDecimal模式为: 除法 四舍五入模式为: 只舍不进
     *
     * @param b1
     *            被除数
     * @param b2
     *            除数
     * @param len
     *            保留位数
     * @return BigDecimal对象
     *
     * @author 许悦 2015年6月11日 上午9:41:13
     * @version 1.0
     */
    public static BigDecimal div(BigDecimal b1,BigDecimal b2,int len) 
    {    // 进行除法运算
         return b1.divide(b2, len, BigDecimal.ROUND_UP);
     }

    /**
     * list equals?
     * @return
     * @throws Exception
     */
    public static boolean isEqualsList(List<String> list1,List<String> list2){
        boolean equalFlag = list1.stream().sorted().collect(Collectors.joining())
                .equals(list2.stream().sorted().collect(Collectors.joining()));
        return equalFlag;
    }

    public static String getEncryptAcocuntEmail(String account) {
        if(org.apache.commons.lang3.StringUtils.isNotBlank(account)){
            String middle = account.substring(3,account.length()-4);
            account = account.replaceAll(middle,"******");
        } else {
            account ="****";
        }
        return  account;
    }

    public static BigDecimal doubleToBD(Double val) {
        return new BigDecimal(val.toString());
    }

    private static final BigDecimal EXCHANGEABLE_UNIT = new BigDecimal("100");

    public static long convertDollarToPenny(BigDecimal amount) {
        return amount.multiply(EXCHANGEABLE_UNIT).intValue();
    }


    public static String convertDollarToPennyString(BigDecimal amount) {
        return String.valueOf(convertDollarToPenny(amount));
    }

    /**
     * the final result with scale 2
     *
     * @param pennyCount
     * @return
     */
    public static BigDecimal convertPennyToDollar(Integer pennyCount) {
        return convertPennyToDollar(pennyCount, 2);
    }

    /**
     * @param pennyCount
     * @param scale
     *            you want to the number of digits
     * @return
     */
    public static BigDecimal convertPennyToDollar(Integer pennyCount, int scale) {
        return new BigDecimal(String.valueOf(pennyCount)).divide(
                EXCHANGEABLE_UNIT, scale, RoundingMode.HALF_UP);
    }

    /**
     * @param num
     * @param isContainEqual true:contain equ 0; false not equ 0
     * @return
     */
    public static Boolean isMoreZeroOrEquZero(BigDecimal num,Boolean isContainEqual) {
            return num1IsMoreNum2OrEquNum2(num,BigDecimal.ZERO,isContainEqual);
    }

    /**
     * @param num1
     * @param num2
     * @param isContainEqual true:contain equ num2; false not equ num2
     *            you want to the number of digits
     * @return
     */
    public static Boolean num1IsMoreNum2OrEquNum2(BigDecimal num1,BigDecimal num2,Boolean isContainEqual) {
        return isContainEqual?num1.compareTo(num2)>=0:num1.compareTo(num2)>0;
    }

    public static void main(String[] args) throws  Exception{
//        //System.out.println(getDecimal_0("12.46"));
//
//        System.out.println(StringUtils.upperCase("MasterCard"));
//
//        List<String> list2 = Arrays.asList("1");
//
//        System.out.println(list2.containsAll(list));

            //System.out.println(encryptGovermentId("120101199009142022"));

//        String base64NeedURL = "www.baodi.com"+"?passCode="+"12345"+"&email="+"xy200919910128@163.com";
//        base64NeedURL = Base64Utils.encodeToString(base64NeedURL.getBytes(StandardCharsets.UTF_8));
//        System.out.println("加密："+base64NeedURL);
//        base64NeedURL = new String(Base64Utils.decodeFromString(base64NeedURL), StandardCharsets.UTF_8);;
//        System.out.println("解密："+base64NeedURL);

//        System.out.println(add(new BigDecimal(1),new BigDecimal(2),new BigDecimal(3)));

//       System.out.println(BigDecimalUtils.isMoreZeroOrEquZero(new BigDecimal(-1),Boolean.FALSE));
       log.info(BigDecimalUtils.isMoreZeroOrEquZero(new BigDecimal(-1),Boolean.FALSE).toString());
    }


}


