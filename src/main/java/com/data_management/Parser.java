package com.data_management;

public class Parser {
    public static double parse(String type, String data) {
        try {
            if (type.equals("Saturation")) {
                return Double.parseDouble(data.substring(0, data.length() - 1)) / 100.0;
            }
            else if (type.equals("Alert")) {
                return data.equals("triggered") ? 1.0 : 0.0;
            }
            return Double.parseDouble(data);
        }
        catch (Exception e) {
            return 0.0;
        }
    }
}
