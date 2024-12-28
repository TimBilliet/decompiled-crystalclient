package org.newsclub.net.unix;

public enum AFUNIXSocketCapability {
  CAPABILITY_PEER_CREDENTIALS(0),
  CAPABILITY_ANCILLARY_MESSAGES(1),
  CAPABILITY_FILE_DESCRIPTORS(2),
  CAPABILITY_ABSTRACT_NAMESPACE(3);
  
  private final int bitmask;
  
  AFUNIXSocketCapability(int bit) {
    this.bitmask = 1 << bit;
  }
  
  int getBitmask() {
    return this.bitmask;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\org\newsclub\ne\\unix\AFUNIXSocketCapability.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */