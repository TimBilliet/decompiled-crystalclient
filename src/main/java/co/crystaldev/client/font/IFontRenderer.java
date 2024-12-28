package co.crystaldev.client.font;

public interface IFontRenderer {
  int drawString(FontData paramFontData, String paramString, int paramInt1, int paramInt2, int paramInt3);
  
  int drawString(String paramString, int paramInt1, int paramInt2, int paramInt3);
  
  FontData getFontData();
}