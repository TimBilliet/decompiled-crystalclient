package org.newsclub.net.unix;

import java.io.Closeable;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class Closeables implements Closeable {
  private List<WeakReference<Closeable>> list;
  
  public Closeables() {}
  
  public Closeables(Closeable... closeable) {
    for (Closeable cl : closeable)
      this.list.add(new HardReference<>(cl)); 
  }
  
  public void close() throws IOException {
    close(null);
  }
  
  public void close(IOException superException) throws IOException {
    IOException exc = superException;
    if (this.list != null)
      for (WeakReference<Closeable> ref : this.list) {
        Closeable cl = ref.get();
        if (cl == null)
          continue; 
        try {
          cl.close();
        } catch (IOException e) {
          if (exc == null) {
            exc = e;
            continue;
          } 
          exc.addSuppressed(e);
        } 
      }  
    if (exc != null)
      throw exc; 
  }
  
  private static final class HardReference<V> extends WeakReference<V> {
    private final V strongRef;
    
    private HardReference(V referent) {
      super((V)null);
      this.strongRef = referent;
    }
    
    public V get() {
      return this.strongRef;
    }
  }
  
  public synchronized boolean add(WeakReference<Closeable> closeable) {
    Closeable cl = closeable.get();
    if (cl == null)
      return false; 
    if (this.list == null) {
      this.list = new ArrayList<>();
    } else {
      for (WeakReference<Closeable> ref : this.list) {
        if (ref.get() == cl)
          return false; 
      } 
    } 
    this.list.add(closeable);
    return true;
  }
  
  public synchronized boolean add(Closeable closeable) {
    return add(new HardReference<>(closeable));
  }
  
  public synchronized boolean remove(Closeable closeable) {
    if (this.list == null || closeable == null)
      return false; 
    for (Iterator<WeakReference<Closeable>> it = this.list.iterator(); it.hasNext();) {
      if (((WeakReference<Closeable>)it.next()).get() == closeable) {
        it.remove();
        return true;
      } 
    } 
    return false;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\org\newsclub\ne\\unix\Closeables.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */