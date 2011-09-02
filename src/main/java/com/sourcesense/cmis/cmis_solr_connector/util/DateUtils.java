package com.sourcesense.cmis.cmis_solr_connector.util;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.text.SimpleDateFormat;

public class DateUtils {
  public static final String DATE_FORMAT_NOW = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

  public static String now() {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
    return sdf.format(cal.getTime());
  }
  
  public static String yesterday(){
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
    String formattedData=sdf.format(cal.getTime());
    int indexOfLineSep=formattedData.lastIndexOf("-");
    int indexOfT=formattedData.indexOf("T");
    String first=formattedData.substring(0, indexOfLineSep+1);
    String last=formattedData.substring(indexOfT);
    int day=Integer.parseInt(formattedData.substring(indexOfLineSep+1,indexOfT));
    day--;
    String yesterday=String.valueOf(day);
    if(yesterday.length()==1)
      yesterday=0+yesterday;
    return first+yesterday+last;
  }
  
  public static String render(Calendar cal) {
    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
    return sdf.format(cal.getTime());
  }

  public static void  main(String arg[]) {
    System.out.println("tyestardah : " + DateUtils.yesterday());
  }
}
