package org.newsclub.net.unix;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

@SuppressFBWarnings({"RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE"})
final class NativeLibraryLoader implements Closeable {
  private static final String PROP_LIBRARY_OVERRIDE = "org.newsclub.net.unix.library.override";
  
  private static final String PROP_LIBRARY_OVERRIDE_FALSE = "org.newsclub.net.unix.library.override.force";
  
  private static final String PROP_LIBRARY_TMPDIR = "org.newsclub.net.unix.library.tmpdir";
  
  private static final File TEMP_DIR;
  
  private static final String ARCHITECTURE_AND_OS = architectureAndOS();
  
  private static final String LIBRARY_NAME = "junixsocket-native";
  
  private static boolean loaded = false;
  
  static {
    String dir = System.getProperty("org.newsclub.net.unix.library.tmpdir", null);
    TEMP_DIR = (dir == null) ? null : new File(dir);
  }
  
  private List<LibraryCandidate> tryProviderClass(String providerClassname, String artifactName) throws IOException, ClassNotFoundException {
    Class<?> providerClass = Class.forName(providerClassname);
    String version = getArtifactVersion(providerClass, new String[] { artifactName });
    String libraryNameAndVersion = "junixsocket-native-" + version;
    return findLibraryCandidates(artifactName, libraryNameAndVersion, providerClass);
  }
  
  public static String getJunixsocketVersion() throws IOException {
    return getArtifactVersion(AFUNIXSocket.class, new String[] { "junixsocket-common" });
  }
  
  private static String getArtifactVersion(Class<?> providerClass, String... artifactNames) throws IOException {
    String[] arrayOfString = artifactNames;
    int i = arrayOfString.length;
    byte b = 0;
    if (b < i) {
      String artifactName = arrayOfString[b];
      Properties p = new Properties();
      String resource = "/META-INF/maven/com.kohlschutter.junixsocket/" + artifactName + "/pom.properties";
      InputStream in = providerClass.getResourceAsStream(resource);
      try {
        if (in == null)
          throw new FileNotFoundException("Could not find resource " + resource + " relative to " + providerClass); 
        p.load(in);
        String version = p.getProperty("version");
        Objects.requireNonNull(version, "Could not read version from pom.properties");
        String str1 = version;
        if (in != null)
          in.close(); 
        return str1;
      } catch (Throwable throwable) {
        if (in != null)
          try {
            in.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    } 
    throw new IllegalStateException("No artifact names specified");
  }
  
  private static abstract class LibraryCandidate implements Closeable {
    protected final String libraryNameAndVersion;
    
    protected LibraryCandidate(String libraryNameAndVersion) {
      this.libraryNameAndVersion = libraryNameAndVersion;
    }
    
    abstract String load() throws Exception;
    
    public abstract void close();
    
    public String toString() {
      return super.toString() + "[" + this.libraryNameAndVersion + "]";
    }
  }
  
  private static final class StandardLibraryCandidate extends LibraryCandidate {
    StandardLibraryCandidate(String version) {
      super((version == null) ? null : ("junixsocket-native-" + version));
    }
    
    String load() throws Exception, LinkageError {
      if (this.libraryNameAndVersion != null) {
        System.loadLibrary(this.libraryNameAndVersion);
        return this.libraryNameAndVersion;
      } 
      return null;
    }
    
    public void close() {}
    
    public String toString() {
      return super.toString() + "(standard library path)";
    }
  }
  
  private static final class ClasspathLibraryCandidate extends LibraryCandidate {
    private final String artifactName;
    
    private final InputStream libraryIn;
    
    private final String path;
    
    ClasspathLibraryCandidate(String artifactName, String libraryNameAndVersion, String path, InputStream libraryIn) {
      super(libraryNameAndVersion);
      this.artifactName = artifactName;
      this.path = path;
      this.libraryIn = libraryIn;
    }
    
    synchronized String load() throws IOException, LinkageError {
      File libFile;
      if (this.libraryNameAndVersion == null)
        return null; 
      try {
        libFile = NativeLibraryLoader.createTempFile("libtmp", System.mapLibraryName(this.libraryNameAndVersion));
        try {
          OutputStream out = new FileOutputStream(libFile);
          try {
            byte[] buf = new byte[4096];
            int read;
            while ((read = this.libraryIn.read(buf)) >= 0)
              out.write(buf, 0, read); 
            out.close();
          } catch (Throwable throwable) {
            try {
              out.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            } 
            throw throwable;
          } 
        } finally {
          this.libraryIn.close();
        } 
      } catch (IOException e) {
        throw e;
      } 
      System.load(libFile.getAbsolutePath());
      if (!libFile.delete())
        libFile.deleteOnExit(); 
      return this.artifactName + "/" + this.libraryNameAndVersion;
    }
    
    public void close() {
      try {
        this.libraryIn.close();
      } catch (IOException iOException) {}
    }
    
    public String toString() {
      return super.toString() + "(" + this.artifactName + ":" + this.path + ")";
    }
  }
  
  private synchronized void setLoaded(String library) {
    if (!loaded) {
      loaded = true;
      AFUNIXSocket.loadedLibrary = library;
      try {
        NativeUnixSocket.init();
      } catch (RuntimeException e) {
        throw e;
      } catch (Exception e) {
        throw new IllegalStateException(e);
      } 
    } 
  }
  
  private Throwable loadLibraryOverride() {
    String libraryOverride = System.getProperty("org.newsclub.net.unix.library.override", "");
    if (!libraryOverride.isEmpty())
      try {
        System.load(libraryOverride);
        setLoaded(libraryOverride);
        return null;
      } catch (Exception|LinkageError e) {
        if (Boolean.valueOf(System.getProperty("org.newsclub.net.unix.library.override.force", "false")).booleanValue())
          throw e; 
        return e;
      }  
    return new Exception("No library specified with -Dorg.newsclub.net.unix.library.override=");
  }
  
  private static Object loadLibrarySyncMonitor() {
    Object monitor = NativeLibraryLoader.class.getClassLoader();
    if (monitor == null)
      return NativeLibraryLoader.class; 
    return monitor;
  }
  
  public synchronized void loadLibrary() {
    synchronized (loadLibrarySyncMonitor()) {
      if (loaded)
        return; 
      List<Throwable> suppressedThrowables = new ArrayList<>();
      Throwable ex = loadLibraryOverride();
      if (ex == null)
        return; 
      suppressedThrowables.add(ex);
      List<LibraryCandidate> candidates = initLibraryCandidates(suppressedThrowables);
      String loadedLibraryId = null;
      for (LibraryCandidate candidate : candidates) {
        try {
          if ((loadedLibraryId = candidate.load()) != null)
            break; 
        } catch (Exception|LinkageError e) {
          suppressedThrowables.add(e);
        } 
      } 
      for (LibraryCandidate candidate : candidates)
        candidate.close(); 
      if (loadedLibraryId == null)
        throw initCantLoadLibraryError(suppressedThrowables); 
      setLoaded(loadedLibraryId);
    } 
  }
  
  private UnsatisfiedLinkError initCantLoadLibraryError(List<Throwable> suppressedThrowables) {
    String message = "Could not load native library junixsocket-native for architecture " + ARCHITECTURE_AND_OS;
    String cp = System.getProperty("java.class.path", "");
    if (cp.contains("junixsocket-native-custom/target-eclipse") || cp.contains("junixsocket-native-common/target-eclipse"))
      message = message + "\n\n*** ECLIPSE USERS ***\nIf you're running from within Eclipse, please close the projects \"junixsocket-native-common\" and \"junixsocket-native-custom\"\n"; 
    UnsatisfiedLinkError e = new UnsatisfiedLinkError(message);
    for (Throwable suppressed : suppressedThrowables)
      e.addSuppressed(suppressed); 
    throw e;
  }
  
  private List<LibraryCandidate> initLibraryCandidates(List<Throwable> suppressedThrowables) {
    List<LibraryCandidate> candidates = new ArrayList<>();
    try {
      candidates.add(new StandardLibraryCandidate(getArtifactVersion(getClass(), new String[] { "junixsocket-common", "junixsocket-core" })));
    } catch (Exception e) {
      suppressedThrowables.add(e);
    } 
    try {
      candidates.addAll(tryProviderClass("org.newsclub.lib.junixsocket.custom.NarMetadata", "junixsocket-native-custom"));
    } catch (Exception e) {
      suppressedThrowables.add(e);
    } 
    try {
      candidates.addAll(tryProviderClass("org.newsclub.lib.junixsocket.common.NarMetadata", "junixsocket-native-common"));
    } catch (Exception e) {
      suppressedThrowables.add(e);
    } 
    return candidates;
  }
  
  private static String architectureAndOS() {
    return System.getProperty("os.arch") + "-" + System.getProperty("os.name").replaceAll(" ", "");
  }
  
  private List<LibraryCandidate> findLibraryCandidates(String artifactName, String libraryNameAndVersion, Class<?> providerClass) {
    String mappedName = System.mapLibraryName(libraryNameAndVersion);
    List<LibraryCandidate> list = new ArrayList<>();
    for (String compiler : new String[] { 
        "gpp", "g++", "linker", "clang", "gcc", "cc", "CC", "icpc", "icc", "xlC", 
        "xlC_r", "msvc", "icl", "ecpc", "ecc" }) {
      String path = "/lib/" + ARCHITECTURE_AND_OS + "-" + compiler + "/jni/" + mappedName;
      InputStream in = providerClass.getResourceAsStream(path);
      if (in != null)
        list.add(new ClasspathLibraryCandidate(artifactName, libraryNameAndVersion, path, in)); 
      String nodepsPath = nodepsPath(path);
      if (nodepsPath != null) {
        in = providerClass.getResourceAsStream(nodepsPath);
        if (in != null)
          list.add(new ClasspathLibraryCandidate(artifactName, libraryNameAndVersion, nodepsPath, in)); 
      } 
    } 
    return list;
  }
  
  private String nodepsPath(String path) {
    int lastDot = path.lastIndexOf('.');
    if (lastDot == -1)
      return null; 
    return path.substring(0, lastDot) + ".nodeps" + path.substring(lastDot);
  }
  
  private static File createTempFile(String prefix, String suffix) throws IOException {
    return File.createTempFile(prefix, suffix, TEMP_DIR);
  }
  
  public void close() {}
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\org\newsclub\ne\\unix\NativeLibraryLoader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */