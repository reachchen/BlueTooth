package com.tanjiaming.www.bluetooth;

/**
 * Created by AndyChen on 2018/6/30.
 */

public class Util {

    public static  int OxStringtoInt(String ox)  {
        ox = ox.toLowerCase();
        if (ox.startsWith("0x")) {
            ox = ox.substring(2, ox.length());
        }
        int ri = 0;
        int oxlen = ox.length();
        for (int i = 0; i < oxlen; i++) {
            char c = ox.charAt(i);
            int h;
            if (('0' <= c && c <= '9')) {
                h = c - 48;
            } else if (('a' <= c && c <= 'f')) {
                h = c - 87;

            } else if ('A' <= c && c <= 'F') {
                h = c - 55;
            } else {
                h= 100;
            }
            byte left = (byte) ((oxlen - i - 1) * 4);
            ri |= (h << left);
        }
        return ri;
    }
}
