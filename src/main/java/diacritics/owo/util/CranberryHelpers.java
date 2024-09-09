package diacritics.owo.util;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;
import com.google.common.base.Splitter;

public class CranberryHelpers {
  public static String padLeft(String pad, String str, int n) {
    return pad.repeat(n).substring(str.length()) + str;
  }

  public static String padLeft(String pad, int str, int n) {
    return padLeft(pad, String.valueOf(str), n);
  }

  public static String toTimeString(int s) {
    int hours = s / 3600;
    int minutes = (s - (hours * 3600)) / 60;
    int seconds = (s - (minutes * 60));

    return (hours != 0 ? padLeft("0", hours, 2) + ":" : "") + padLeft("0", minutes, 2) + ":"
        + padLeft("0", seconds, 2);
  }

  public static List<Integer> readColors(String data) {
    return Splitter.fixedLength(8).splitToStream(data)
        .map(n -> new BigInteger(n, 16).intValue())
        .collect(Collectors.toList());
  }
}
