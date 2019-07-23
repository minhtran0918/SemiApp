package org.semi.utils;

import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class StringUtils {
    private StringUtils() {
    }

    /**
     * Normalize character, Ex: Â, Ă, Ấ => A
     *
     * @param str A string need to be normalize.
     * @return A Normalize string.
     */
    public static String normalize(String str) {
        String temp = Normalizer.normalize(str.toLowerCase(), Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replaceAll("đ", "d");
    }

    public static String toVNDCurrency(long value) {
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
        double inMeter = distance * 1000f;
        if (inMeter < 1000) {
            if ((double) Math.round(inMeter * 10) / 10 % 1.0 == 0) {
                return String.format("%.0f m", inMeter);
            } else {
                return String.format("%.1f m", inMeter);
            }
        } else {
            if (distance % 1 == 0) {
                return String.format("%d km", (int) distance);
            } else {
                return String.format("%.1f km", distance);
            }
        }
    }

    public static String convertStandardAddress(String street, String ward, String district, String city) {
        StringBuilder sb = new StringBuilder();
        sb.append(street).append(", ");

        //Ward
        if (ward.contains("Phường") && ward.length() > 12) { //6char + 1
            sb.append("P. ").append(ward.substring(7)).append(", ");
        } else if (ward.contains("Xã") && ward.length() > 12) {
            sb.append("X. ").append(ward.substring(3)).append(", ");
        } else if (ward.contains("Thị trấn") && ward.length() > 12) {
            sb.append("TT. ").append(ward.substring(9)).append(", ");
        } else {
            sb.append(ward).append(", ");
        }

        //District
        if (district.contains("Quận") && district.length() > 12) {
            sb.append("Q. ").append(district.substring(5)).append(", ");
        } else if (district.contains("Huyện") && district.length() > 12) {
            sb.append("H. ").append(district.substring(6)).append(", ");
        } else if (district.contains("Thành phố") && district.length() > 13) {
            sb.append("TP. ").append(district.substring(10)).append(", ");
        } else if (district.contains("Thị xã") && district.length() > 12) {
            sb.append("TX. ").append(district.substring(7)).append(", ");
        } else {
            sb.append(district).append(", ");
        }

        //City
        if (city.contains("Tỉnh") && city.length() > 13) {
            sb.append("T. ").append(city.substring(5));
        } else if (city.contains("Thành phố") && city.length() > 18) {
            sb.append("TP. ").append(city.substring(10));
        } else {
            sb.append(city);
        }
        return sb.toString();
    }

    public static List<String> getListPath(String path) {
        List<String> result = new ArrayList<>();
        if (!path.equals("")) {
            if (path.contains("?")) {
                String[] lists = path.split("\\?");
                for (String item : lists) {
                    result.add(item);
                }
            } else {
                result.add(path);
            }

        }
        return result;
    }
}
