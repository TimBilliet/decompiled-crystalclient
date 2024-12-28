package co.crystaldev.client.shader;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum VertexFormat {
  POSITION(VertexFormatElement.POSITION),
  POSITION_COLOR(VertexFormatElement.POSITION, VertexFormatElement.COLOR);
  
  private final List<VertexFormatElement> vertexFormatElements;
  
  VertexFormat(VertexFormatElement... vertexFormatElements) {
    this.vertexFormatElements = Collections.unmodifiableList(Arrays.asList(vertexFormatElements));
  }
  
  public List<VertexFormatElement> getVertexFormatElements() {
    return this.vertexFormatElements;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\shader\VertexFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */