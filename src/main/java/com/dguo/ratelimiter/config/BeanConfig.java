package com.dguo.ratelimiter.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BeanConfig {
  private static Properties prop;

  static {
    prop = new Properties();
    InputStream in = null;
    try {
      in = BeanConfig.class.getClassLoader().getResourceAsStream("bean.properties");
      prop.load(in);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(-1);
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private BeanConfig() {}

  public static String getProperty(String key) {
    return prop.getProperty(key);
  }
}
