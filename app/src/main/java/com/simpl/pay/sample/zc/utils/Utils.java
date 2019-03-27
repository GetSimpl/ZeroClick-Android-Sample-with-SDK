package com.simpl.pay.sample.zc.utils;

public class Utils {
    public static String hasTokenPayload (long phoneNumber) {
        String payload = "{\n" +
                " \"number\": " + phoneNumber + "\n" +
                "}";

        return payload;
    }

    public static String getEligibilityPayload(Long phoneNo, int amount, String fingerPrintData) {
        String payload = "{\n" +
                " \"amount_in_paise\": " + amount + ",\n" +
                " \"fingerprint_data\": \"" + fingerPrintData + "\",\n" +
                " \"number\": " + phoneNo + ",\n" +
                "  \"items\": [\n" +
                "    {\n" +
                "      \"sku\": \"some unique id\",\n" +
                "      \"quantity\": \"12\",\n" +
                "      \"rate_per_item\": \"1200\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        return payload;
    }

    public static String getChargePayload(Long phoneNo, int amount) {
        String payload = "{\n" +
                " \"amount_in_paise\": " + amount + ",\n" +
                " \"number\": " + phoneNo + ",\n" +
                "  \"items\": [\n" +
                "    {\n" +
                "      \"sku\": \"some unique id\",\n" +
                "      \"quantity\": \"12\",\n" +
                "      \"rate_per_item\": \"1200\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        return payload;
    }
}
