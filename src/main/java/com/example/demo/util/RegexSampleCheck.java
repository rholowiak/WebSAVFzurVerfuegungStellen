package com.example.demo.util;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class RegexSampleCheck {
    public static boolean useRegex(final String checkText1) {

        //Add null or empty check to avoid NullPointerException:
        if (checkText1 == null || checkText1.trim().isEmpty()) {
            return false;
        }

        // Compile regular expression
        // Define the regex pattern for the specific formats "ZRHOLOWIAK/OL009966|ZRHOLOWIAK/OL009966H1|BDVISAVH1/SL78442|BDVISAVH1/SL79172H1|BDVISAVH1/SL78442new"
        final Pattern pattern = Pattern.compile(
                "^(?:[A-Za-z]{10}/[A-Za-z]{2}[0-9]{6}" +                      // ZRHOLOWIAK/OL009966
                        "|[A-Za-z]{10}/[A-Za-z]{2}[0-9]{6}[A-Za-z][0-9]" +      // ZRHOLOWIAK/OL009966H1
                        "|[A-Za-z]{8}[0-9]/[A-Za-z]{2}[0-9]{5}" +                  // BDVISAVH1/SL78442
                        "|[A-Za-z]{8}[0-9]/[A-Za-z]{2}[0-9]{5}[A-Za-z][0-9]" + // BDVISAVH1/SL79172H1
                        "|[A-Za-z]{8}[0-9]/[A-Za-z]{2}[0-9]{5}[A-Za-z]{3})$",      // BDVISAVH1/SL78442new
                Pattern.CASE_INSENSITIVE
        );
        // Match regex against input
        final Matcher matcher = pattern.matcher(checkText1);
        // Use results...
        return matcher.matches();
    }
}