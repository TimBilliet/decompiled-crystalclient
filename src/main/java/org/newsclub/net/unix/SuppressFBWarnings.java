package org.newsclub.net.unix;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
@interface SuppressFBWarnings {
  String[] value() default {};
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\org\newsclub\ne\\unix\SuppressFBWarnings.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */