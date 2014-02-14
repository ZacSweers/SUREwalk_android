package org.utexas.surewalk.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by GHawkins on 11/15/13.
 */
public class SureCalendar {
   public String version;
   public String encoding;
   public Feed feed;

   public void setVersion(String v){version = v;}
   public void setEncoding(String e){encoding = e;}
   public void setFeed(Feed f){feed = f;}



   public class Feed {
      public List<Entry> entry = new ArrayList<Entry>();
      //getters&setters
   }

   public class Entry{
      public Map<String, String> title = new HashMap<String, String>();
      public Map<String, String> content = new HashMap<String, String>();
       public List<Map<String, String>> gd$when = new ArrayList<Map<String, String>>();
   }
}