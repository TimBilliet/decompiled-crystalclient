package co.crystaldev.client.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.BaseConfiguration;
import org.apache.logging.log4j.core.impl.Log4jContextFactory;
import org.apache.logging.log4j.core.lookup.Interpolator;
import org.apache.logging.log4j.core.lookup.StrLookup;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.apache.logging.log4j.core.selector.ContextSelector;
import org.apache.logging.log4j.spi.LoggerContextFactory;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class Log4jPatch {
  static Field factoryField;
  
  static Field tempLookupField;
  
  static Field lookupField;
  
  static Field contextSelectorField;
  
  public static void patchLogger() {
    try {
      LoggerContextFactory contextFactory = (LoggerContextFactory)factoryField.get((Object)null);
      if (contextFactory instanceof Log4jContextFactory) {
        ContextSelector contextSelector = ((Log4jContextFactory)contextFactory).getSelector();
        List<LoggerContext> loggerContextList = contextSelector.getLoggerContexts();
        for (LoggerContext loggerContext : loggerContextList)
          sanitizeContext(loggerContext); 
        contextSelectorField.set(contextFactory, new SafeWrappedContextSelector(contextSelector));
      } 
    } catch (Throwable throwable) {}
  }
  
  public static LoggerContext sanitizeContext(LoggerContext loggerContext) {
    if (loggerContext.getConfiguration() instanceof BaseConfiguration)
      try {
        BaseConfiguration baseConfiguration = (BaseConfiguration)loggerContext.getConfiguration();
        Interpolator strLookup = (Interpolator)tempLookupField.get(baseConfiguration);
        Map<String, StrLookup> map = (Map<String, StrLookup>)lookupField.get(strLookup);
        map.clear();
        StrSubstitutor strSubstitutor = baseConfiguration.getStrSubstitutor();
        strLookup = (Interpolator)strSubstitutor.getVariableResolver();
        map = (Map<String, StrLookup>)lookupField.get(strLookup);
        map.clear();
      } catch (Throwable throwable) {} 
    return loggerContext;
  }
  
  static {
    try {
      factoryField = LogManager.class.getDeclaredField("factory");
      factoryField.setAccessible(true);
      tempLookupField = BaseConfiguration.class.getDeclaredField("tempLookup");
      tempLookupField.setAccessible(true);
      lookupField = Interpolator.class.getDeclaredField("lookups");
      lookupField.setAccessible(true);
      contextSelectorField = Log4jContextFactory.class.getDeclaredField("selector");
      contextSelectorField.setAccessible(true);
    } catch (Throwable throwable) {}
  }
  
  private static class SafeWrappedContextSelector implements ContextSelector {
    private final ContextSelector contextSelector;
    
    private SafeWrappedContextSelector(ContextSelector contextSelector) {
      this.contextSelector = contextSelector;
    }
    
    public LoggerContext getContext(String fqcn, ClassLoader loader, boolean currentContext) {
      return Log4jPatch.sanitizeContext(this.contextSelector.getContext(fqcn, loader, currentContext));
    }
    
    public LoggerContext getContext(String fqcn, ClassLoader loader, boolean currentContext, URI configLocation) {
      return Log4jPatch.sanitizeContext(this.contextSelector.getContext(fqcn, loader, currentContext, configLocation));
    }
    
    public List<LoggerContext> getLoggerContexts() {
      return this.contextSelector.getLoggerContexts();
    }
    
    public void removeContext(LoggerContext context) {
      this.contextSelector.removeContext(context);
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\Log4jPatch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */