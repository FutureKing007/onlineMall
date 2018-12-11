package com.pinyougou.utils;

import java.io.UnsupportedEncodingException;

public class ConvertEnodingUtils {
    /**
     * 把IOS8859-1转为UTF-8
     * @param iso  OS8859-1
     * @return UTF-8
     */
    public static String ConvertIosToUTF(String iso) {
        if (iso != null && !iso.isEmpty()) {
            try {
                return new String(iso.getBytes("ISO8859-1"),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException("illegal params");
        }
        throw new RuntimeException("illegal params");
    }
}
