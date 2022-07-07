package com.xy.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * 随机数工具类(随机生成数字、字母、数字字母组合、中文姓名)
 */
@Slf4j
public class RandomCodeUtils {
    public static final String  NOZERORANDNUMS = "123456789";
    public static final String RANDCHAR = "ABCDEFGHJKLMNPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz";


    /**
     * 生成指定长度的数字随机数
     * @param length 长度
     * @return String
     */
    public static  String getRandNumberCode (int length)    {
        Random random = new Random();
        String result="";
        for(int i=0;i<length;i++){
            result+=random.nextInt(10);
        }
        return result;
    }

    /**
     * 生成指定长度的奇数数字随机数
     * @param length 长度
     * @return String
     */
    public static  String getRandNumberOddCode (int length)    {
        Random random = new Random();
        String result="";
        for(int i=0;i<length;i++){
            result+=random.nextInt(10);
            if(result!=null){
                Long res = Long.valueOf(result);
                if(res%2==0){
                    res = res+1;
                    result = res.toString();
                }
            }
        }
        return result;
    }


    /**
     * 生成指定长度的偶数数字随机数
     * @param length 长度
     * @return String
     */
    public static  String getRandNumberEvenCode (int length)    {
        Random random = new Random();
        String result="";
        for(int i=0;i<length;i++){
            result+=random.nextInt(10);
            if(result!=null){
                Long res = Long.valueOf(result);
                if(res%2==1){
                    res = res+1;
                    result = res.toString();
                }
            }
        }
        return result;
    }


    /**
     * 生成指定长度的数字随机数,不能以0开头
     * @param length 长度
     * @return String
     */
    public static  String getRandNumber (int length)    {
        //第一位随机数
        String temp = NOZERORANDNUMS;
        int len = temp.length();
        int p;
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        p = r.nextInt(len);
        sb.append(temp.substring(p, p + 1));

        //除第一位以外其他随机数
        for(int i=0;i<length-1;i++){
            sb.append(r.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * 生成相应长度的数字字母组合的随机数
     * @param size 长度
     * @return String
     */
    public static String getRandStrCode(int size) {
        String temp = RANDCHAR;
        int length = temp.length();
        int p;
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            p = r.nextInt(length);
            sb.append(temp.substring(p, p + 1));
        }
        return sb.toString();
    }

    /**
     * 生成指定长度的字母随机数
     * @param size 长度
     * @return 字符串
     */
    public static String getRandEnglishCode(int size) {
        String temp = RANDCHAR;
        int length = temp.length();
        int p;
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            p = r.nextInt(length);
            sb.append(temp.substring(p, p + 1));
        }
        return sb.toString();
    }


    public static void main(String[] args){
//        System.out.println(getRandNumberOddCode(9));
//        System.out.println(getRandNumberEvenCode(9));
        log.info(getRandNumberOddCode(9));
        log.info(getRandNumberEvenCode(9));
    }
}