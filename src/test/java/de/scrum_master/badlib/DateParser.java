package de.scrum_master.badlib;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateParser {
  private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");

  public Date toDate(String dateToBeParsed, String formatPattern) throws ParseException {
    dateFormat.applyPattern(formatPattern);
/*
    // Make date parsing slower so we can see the effect of parallel streams
    try {
      Thread.sleep(5);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
*/
    return dateFormat.parse(dateToBeParsed);
  }
}
