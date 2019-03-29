package org.semi.util;

import org.semi.contract.Contract;

import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.util.regex.Pattern;

public final class StringUtils {
    private StringUtils(){}

    /**
     * Normalize character, Ex: Â, Ă, Ấ => A
     * @param str A string need to be normalize.
     * @return A Normalize string.
     */
    public static String normalize(String str) {
        String temp = Normalizer.normalize(str.toLowerCase(), Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replaceAll("đ", "d");
    }

    public static String toVNDCurrency(long value){
        String res;
        if (value < 1000) {
            res = String.valueOf(value);
        } else {
            NumberFormat formatter = new DecimalFormat("###,###");
            res = formatter.format(value);
        }
        return res + Contract.VN_CURRENCY;
    }

    public static String toDistanceFormat(double distance) {
        double inMeter = distance * 1000;
        if(inMeter < 1000) {
            return String.format("%.1fm", inMeter);
        } else {
            return String.format("%.1fkm", distance);
        }
    }
}
