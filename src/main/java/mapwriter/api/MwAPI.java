package mapwriter.api;

import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MwAPI {
  private static final HashBiMap<Class<? extends IMwDataProvider>, IMwDataProvider> REGISTERED_PROVIDERS = HashBiMap.create();
  
  private static final List<IMwDataProvider> ENABLED_PROVIDERS = new ArrayList<>();
  
  public static void registerDataProvider(@NotNull String name, @NotNull IMwDataProvider handler) {
    handler.setName(name);
    REGISTERED_PROVIDERS.put(handler.getClass(), handler);
  }
  
  public static void registerDataProvider(@NotNull String name, @NotNull IMwDataProvider handler, boolean enabled) {
    registerDataProvider(name, handler);
    setEnabled(handler, enabled);
  }
  
  public static Collection<IMwDataProvider> getRegisteredProviders() {
    return REGISTERED_PROVIDERS.values();
  }
  
  public static IMwDataProvider getDataProvider(@NotNull String name) {
    for (IMwDataProvider provider : REGISTERED_PROVIDERS.values()) {
      if (name.equalsIgnoreCase(provider.getName()))
        return provider; 
    } 
    throw new RuntimeException(String.format("Data provider with name '%s' was not found.", name));
  }
  
  public static <P extends IMwDataProvider> P getDataProvider(@NotNull Class<P> clazz) {
    IMwDataProvider provider = (IMwDataProvider)REGISTERED_PROVIDERS.get(clazz);
    if (provider != null)
      return (P)provider; 
    throw new RuntimeException("Provider must be registered!");
  }
  
  public static void toggleDataProvider(@NotNull IMwDataProvider provider) {
    if (!isProviderRegistered(provider))
      throw new RuntimeException("Provider must be registered!"); 
    if (ENABLED_PROVIDERS.contains(provider)) {
      ENABLED_PROVIDERS.remove(provider);
    } else {
      ENABLED_PROVIDERS.add(provider);
    } 
  }
  
  public static void setEnabled(@NotNull IMwDataProvider provider, boolean enabled) {
    if (enabled && !ENABLED_PROVIDERS.contains(provider)) {
      ENABLED_PROVIDERS.add(provider);
    } else if (!enabled) {
      ENABLED_PROVIDERS.remove(provider);
    } 
  }
  
  public static boolean isProviderRegistered(@NotNull IMwDataProvider provider) {
    return getRegisteredProviders().contains(provider);
  }
  
  public static List<IMwDataProvider> getEnabledDataProviders() {
    return ENABLED_PROVIDERS;
  }
  
  public static List<String> getEnabledProviderNames() {
    return (List<String>)getEnabledDataProviders().stream().map(IMwDataProvider::getName).collect(Collectors.toList());
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\api\MwAPI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */