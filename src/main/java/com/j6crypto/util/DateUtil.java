package com.j6crypto.util;

import java.time.LocalDateTime;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 */
public class DateUtil {
  public static boolean isSameDay(LocalDateTime date, LocalDateTime date2) {
    if (date.getYear() == date2.getYear() && date.getDayOfYear() == date2.getDayOfYear()) {
      return true;
    }
    return false;
  }
}
