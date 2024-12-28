package co.crystaldev.client.gui.buttons;

import co.crystaldev.client.Reference;
import co.crystaldev.client.font.FontRenderer;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Pane;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.ScissorManager;
import co.crystaldev.client.util.objects.FadingColor;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextInputField extends Button {
  private static final Pattern WORD_PATTERN = Pattern.compile("[\\w\\d]+(?:\\s+)?");

  private static int ACTIVE_ID = -1;

  private String validInputPattern = ".+";

  public String getValidInputPattern() {
    return this.validInputPattern;
  }

  public void setValidInputPattern(String validInputPattern) {
    this.validInputPattern = validInputPattern;
  }

  protected int maxLength = -1;

  protected final String placeholderText;

  public int getMaxLength() {
    return this.maxLength;
  }

  public void setMaxLength(int maxLength) {
    this.maxLength = maxLength;
  }

  public String getPlaceholderText() {
    return this.placeholderText;
  }

  protected String text = "";

  public String getText() {
    return this.text;
  }

  protected boolean enabled = true;

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  protected boolean typing = false;

  public boolean isTyping() {
    return this.typing;
  }

  protected boolean censor = false;

  protected boolean canMakeSelections = true;

  protected TextSelection currentSelection = null;

  protected Consumer<String> onTextInput = null;

  public void setOnTextInput(Consumer<String> onTextInput) {
    this.onTextInput = onTextInput;
  }

  protected final Caret caret = new Caret();

  public Caret getCaret() {
    return this.caret;
  }

  private int textOffsetX = 0;

  private final FadingColor backgroundColor;

  private final FadingColor outlineColor;

  private final FadingColor textColor;

  private final FadingColor caretColor;

  public TextInputField(int id, int x, int y, int width, int height, String placeholderText) {

    super(id, x, y, width, height);
    this.placeholderText = placeholderText;
    this.fontRenderer = Fonts.NUNITO_REGULAR_16;
    this.backgroundColor = new FadingColor(this.opts.neutralButtonBackground, this.opts.getColor(this.opts.neutralButtonBackground, 70));
    this.outlineColor = new FadingColor(this.opts.getColor(this.opts.neutralTextColor, 0), this.opts.hoveredTextColor);
    this.textColor = new FadingColor(this.opts.neutralTextColor, this.opts.hoveredTextColor);
    this.caretColor = new FadingColor(this.opts.getColor(this.opts.neutralTextColor, 100), this.opts.hoveredTextColor, 100L);
    this.caretColor.fade(true);
  }

  public TextInputField(int id, int x, int y, int width, int height, String placeholderText, boolean censor) {

    this(id, x, y, width, height, placeholderText);
    this.censor = censor;
  }

  private void trimLength() {
    if (this.maxLength != -1) {
      String text = this.text;
      while (text.length() > this.maxLength)
        text = text.substring(0, text.length() - 1);
      setText(text);
      this.caret.setLocation(this.caret.index);
    }
  }

  private boolean validateInput(String input) {
    return input.matches(this.validInputPattern);
  }

  public void setText(String input) {
    this.text = input;
    if (this.onTextInput != null)
      this.onTextInput.accept(this.text);
  }

  public void drawButton(int mouseX, int mouseY, boolean hovered) {
    int textScissorY = (this.scissorPane != null) ? (int)(this.scissorPane.y / this.scale) : this.y;
    int textScissorHeight = (this.scissorPane != null) ? (int)(this.scissorPane.height / this.scale) : this.height;
    Pane textScissorArea = (new Pane(this.x + 1, textScissorY, this.width - 2, textScissorHeight)).scale(this.scale);
    ScissorManager.getInstance().push(this.scissorPane);
    if (ACTIVE_ID != this.BUTTON_ID)
      this.typing = false;
    ensureIndexVisible(this.caret.getIndex());
    hovered = (hovered && this.enabled);
    this.backgroundColor.fade((hovered || this.typing));
    this.outlineColor.fade(this.typing);
    this.textColor.fade((hovered || this.typing));
    this.caretColor.fade((this.typing && this.caret.shouldRender()));
    String display = this.text.isEmpty() ? this.placeholderText : this.text;
    display = (this.censor && !this.text.isEmpty()) ? StringUtils.repeat('*', display.length()) : display;
    if (!this.typing)
      this.textOffsetX = 0;
    RenderUtils.drawRoundedRectWithBorder(this.x, this.y, (this.x + this.width), (this.y + this.height), 9.0D, 1.0F, this.outlineColor
        .getCurrentColor().getRGB(), this.backgroundColor.getCurrentColor().getRGB());
    ScissorManager.getInstance().push(textScissorArea);
    this.fontRenderer.drawString(display, this.x + 2 + this.textOffsetX, this.y + this.height / 2 - this.fontRenderer.getStringHeight() / 2, this.textColor
        .getCurrentColor().getRGB());
    if (this.currentSelection != null) {
      int x = this.x + 2 + this.textOffsetX;
      String sel = this.censor ? StringUtils.repeat('*', this.text.length()) : this.text;
      if (this.currentSelection.from > 0)
        x += this.fontRenderer.getStringWidth(sel.substring(0, this.currentSelection.from));
      RenderUtils.drawRect(x, this.y + this.height / 2.0F - this.fontRenderer.getStringHeight() / 2.0F, (x + this.fontRenderer.getStringWidth(this.currentSelection.getText())), this.y + this.height / 2.0F + this.fontRenderer
          .getStringHeight() / 2.0F, this.opts.mainColor.getRGB());
      String selection = this.censor ? StringUtils.repeat('*', this.currentSelection.length()) : this.currentSelection.getText();
      this.fontRenderer.drawString(selection, x, this.y + this.height / 2 - this.fontRenderer.getStringHeight() / 2, this.opts.hoveredTextColor
          .getRGB());
      RenderUtils.resetColor();
    }
    if (this.typing) {
      int x = this.x + 2 + this.textOffsetX;
      if (this.caret.getIndex() > 0) {
        if (!this.text.isEmpty()) {
          String str = this.censor ? StringUtils.repeat('*', this.caret.getIndex() + 1) : this.text.substring(0, Math.min(this.caret.getIndex(), this.text.length()));
          x += this.fontRenderer.getStringWidth(str);
        }
      } else if (this.caret.getIndex() < 0) {
        this.caret.setLocation(0);
      }
      FontRenderer fr = Fonts.PT_SANS_BOLD_18;
      fr.drawString((this.maxLength == -1 || this.text.length() < this.maxLength) ? "|" : "_", x, this.y + this.height / 2 - fr.getStringHeight() / 2, this.caretColor
          .getCurrentColor().getRGB());
      RenderUtils.resetColor();
    }
    ScissorManager.getInstance().pop();
    ScissorManager.getInstance().pop();
  }

  public boolean onKeyTyped(char key, int code) {
    super.onKeyTyped(key, code);
    if (!this.typing || !this.enabled)
      return true;
    if (GuiScreen.isCtrlKeyDown()) {
      int index;
      boolean charFound;
      int i;
      int index1;
      boolean charFound1;
      int j;
      switch (code) {
        case 30:
          if (!this.text.isEmpty())
            this.currentSelection = new TextSelection(0, this.text.length() - 1);
          return false;
        case 46:
          if (this.currentSelection != null) {
            this.currentSelection.copyToClipboard();
          } else {
            StringSelection selection = new StringSelection(this.text);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
          }
          return false;
        case 45:
          if (this.currentSelection != null) {
            this.currentSelection.cutText();
          } else {
            StringSelection selection = new StringSelection(this.text);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
            this.caret.setLocation(0);
            this.currentSelection = null;
            setText("");
          }
          return false;
        case 47:
          if (this.currentSelection != null) {
            this.currentSelection.pasteFromClipboard();
          } else {
            try {
              String clipboard = (String)Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
              if (clipboard != null && clipboard.length() > 0)
                this.caret.insertText(clipboard);
            } catch (UnsupportedFlavorException|java.io.IOException unsupportedFlavorException) {}
            return false;
          }
        case 205:
        case 211:
          index = (code == 205 && this.currentSelection != null) ? (this.currentSelection.to + 1) : this.caret.getIndex();
          charFound = false;
          for (i = index; i < this.text.length(); i++) {
            boolean whitespace = Character.toString(this.text.charAt(i)).matches("^\\s*$");
            int offset = 1;
            if (i != this.text.length() - 1) {
              if (whitespace && !charFound)
                continue;
              if (!whitespace) {
                charFound = true;
                continue;
              }
            } else {
              offset = 0;
            }
            if (code == 211) {
              this.currentSelection = new TextSelection(index, i - offset);
              this.currentSelection.removeText();
              this.caret.setLocation(index);
              break;
            }
            if (GuiScreen.isShiftKeyDown())
              if (this.currentSelection != null) {
                this.currentSelection = new TextSelection(this.currentSelection.from, i - offset);
              } else {
                this.currentSelection = new TextSelection(index, i - offset);
              }
            this.caret.setLocation(i + ((offset == 1) ? 0 : 1));
          }
          return false;
        case 14:
        case 203:
          index1 = Math.min(this.text.length(), this.caret.getIndex()) - 1;
          charFound1 = false;
          for (j = index1; j >= 0; j--) {
            boolean whitespace = Character.toString(this.text.charAt(Math.min(this.text.length() - 1, j))).matches("^\\s*$");
            if (j == 0 || (charFound1 && whitespace)) {
              if (code == 14) {
                this.currentSelection = new TextSelection(j, index1);
                this.currentSelection.removeText();
                this.caret.setLocation(j);
                break;
              }
              if (GuiScreen.isShiftKeyDown())
                if (this.currentSelection != null) {
                  this.currentSelection = new TextSelection(j, this.currentSelection.to);
                } else {
                  this.currentSelection = new TextSelection(j, index1);
                }
              this.caret.setLocation(j + ((j == 0) ? 0 : 1));
              break;
            }
            if (!charFound1 && !whitespace)
              charFound1 = true;
          }
          return false;
      }
      return true;
    }
    switch (code) {
      case 199:
      case 200:
        this.currentSelection = null;
        this.caret.setLocation(0);
        return true;
      case 207:
      case 208:
        this.currentSelection = null;
        this.caret.setLocation(this.text.length());
        return true;
      case 203:
        if (this.currentSelection != null) {
          this.caret.setLocation(this.currentSelection.from);
        } else {
          this.caret.setLocation(this.caret.getIndex() - 1);
        }
        this.currentSelection = null;
        return true;
      case 205:
        if (this.currentSelection != null) {
          this.caret.setLocation(this.currentSelection.to + 1);
        } else {
          this.caret.setLocation(this.caret.getIndex() + 1);
        }
        this.currentSelection = null;
        return true;
      case 14:
        if (this.currentSelection != null) {
          this.currentSelection.removeText();
        } else {
          this.caret.removeText();
        }
        this.currentSelection = null;
        return true;
      case 211:
        if (this.currentSelection != null) {
          this.currentSelection.removeText();
        } else {
          this.caret.setLocation(this.caret.getIndex() + 1);
          this.caret.removeText();
          this.currentSelection = null;
        }
        return true;
    }
    if (ChatAllowedCharacters.isAllowedCharacter(key)) {
      if (this.currentSelection != null)
        this.currentSelection.removeText();
      if (this.maxLength == -1 || this.text.length() < this.maxLength)
        this.caret.insertText(Character.toString(key));
    }
    return true;
  }

  public void onInteract(int mouseX, int mouseY, int mouseButton) {
    int lastMouseX = this.lastClickedMouseX, lastMouseY = this.lastClickedMouseY;
    super.onInteract(mouseX, mouseY, mouseButton);
    if (!this.enabled)
      return;
    if (!this.typing)
      setTyping(true);
    if (!this.text.isEmpty()) {
      String text = this.censor ? StringUtils.repeat('*', this.text.length()) : this.text;
      for (int i = 0; i <= text.length(); i++) {
        String str = text.substring(0, i);
        if (mouseX <= this.x + 2 + this.textOffsetX + this.fontRenderer.getStringWidth(str)) {
          this.caret.setLocation(i);
          if (this.currentSelection == null && lastMouseX == mouseX && lastMouseY == mouseY &&
            System.currentTimeMillis() - this.lastClickedMouseTime < 200L) {
            for (i = Math.max(this.caret.getIndex() - 1, 0); i >= 0; i--) {
              if (i == 0 || !WORD_PATTERN.matcher(String.valueOf(this.text.charAt(i))).find()) {
                Matcher matcher = WORD_PATTERN.matcher(this.text.substring((i == 0) ? 0 : (i + 1)));
                if (matcher.find()) {
                  this.currentSelection = new TextSelection((i == 0) ? 0 : (i + 1), i - ((i == 0) ? 1 : 0) + matcher.end(0));
                  return;
                }
              }
            }
            this.currentSelection = new TextSelection(this.caret.getIndex() - 1, this.caret.getIndex() - 1);
          } else {
            this.currentSelection = null;
          }
          return;
        }
      }
    }
    this.caret.setLocation(this.text.length());
    this.currentSelection = null;
  }

  public void mouseDown(Screen screen, int mouseX, int mouseY, int mouseButton) {
    if (!isHovered(mouseX, mouseY))
      setTyping(false);
  }

  private void ensureIndexVisible(int index) {
    String[] split = getText().split("");
    for (int i = 0; i < split.length; i++) {
      if (i == index - 1) {
        String sel = this.censor ? StringUtils.repeat('*', this.text.length()) : getText();
        int x = this.x + 2 + this.textOffsetX + this.fontRenderer.getStringWidth(sel.substring(0, this.caret.getIndex()));
        if (x < this.x + 2) {
          this.textOffsetX = Math.min(0, this.textOffsetX + this.x + 2 - x);
        } else if (x > this.x + this.width - 6) {
          this.textOffsetX = Math.max(-this.fontRenderer.getStringWidth(getText()), this.textOffsetX - x - this.x + this.width - 6);
        }
        return;
      }
    }
    this.textOffsetX = 0;
  }

  protected void setTyping(boolean typing) {
    this.typing = typing;
    if (this.typing)
      ACTIVE_ID = this.BUTTON_ID;
    this.caret.setLocation(0);
    this.currentSelection = null;
  }

  public final class Caret {
    private int index = 0;

    private long lastUpdate = -1L;

    public boolean shouldRender() {
      if (this.lastUpdate == -1L)
        this.lastUpdate = System.currentTimeMillis();
      return (System.currentTimeMillis() - this.lastUpdate < 500L || System.currentTimeMillis() / 500L % 2L == 0L);
    }

    public void setLocation(int index) {
      this.index = MathHelper.clamp_int(index, 0, TextInputField.this.text.length());
      this.lastUpdate = System.currentTimeMillis();
    }

    public int getIndex() {
      return this.index;
    }

    public void insertText(String str) {
      String inserted = (new StringBuilder(TextInputField.this.text)).insert(this.index, str).toString();
      if (!TextInputField.this.validateInput(inserted))
        return;
      TextInputField.this.setText(inserted);
      this.index += str.length();
      TextInputField.this.trimLength();
    }

    public void removeText() {
      if (TextInputField.this.text.isEmpty() || this.index - 1 == -1)
        return;
      StringBuilder bld = new StringBuilder(TextInputField.this.text);
      TextInputField.this.setText(bld.deleteCharAt(this.index - 1).toString());
      setLocation(this.index - 1);
      TextInputField.this.currentSelection = null;
    }
  }

  protected class TextSelection {
    private final int from;

    private final int to;

    public TextSelection(int from, int to) {
      this.from = Math.min(from, to);
      this.to = Math.max(from, to);
      TextInputField.this.caret.setLocation(this.from);
    }

    public int length() {
      return this.to + 1 - this.from;
    }

    public String getText() {
      return TextInputField.this.text.substring(this.from, this.to + 1);
    }

    public void copyToClipboard() {
      StringSelection selection = new StringSelection(getText().trim());
      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
    }

    public void cutText() {
      StringBuilder sb = new StringBuilder(TextInputField.this.text);
      copyToClipboard();
      sb.replace(this.from, this.to + 1, "");
      TextInputField.this.setText(sb.toString());
      TextInputField.this.caret.setLocation(this.from);
      TextInputField.this.currentSelection = null;
    }

    public void pasteFromClipboard() {
      try {
        String clipboard = (String)Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        if (clipboard != null && clipboard.length() > 0) {
          char[] arr = clipboard.toCharArray();
          int before = TextInputField.this.text.length();
          String concat = TextInputField.this.text;
          for (char c : arr) {
            String input = Character.toString(c);
            if (ChatAllowedCharacters.isAllowedCharacter(c))
              concat = concat.concat(input);
          }
          if (!TextInputField.this.validateInput(concat))
            return;
          TextInputField.this.setText(concat);
          TextInputField.this.caret.setLocation(this.to + TextInputField.this.text.length() - before);
        }
        TextInputField.this.trimLength();
      } catch (UnsupportedFlavorException|java.io.IOException ex) {
        Reference.LOGGER.error("Unable to retrieve text from clipboard", ex);
      }
      TextInputField.this.currentSelection = null;
    }

    public void removeText() {
      TextInputField.this.setText(TextInputField.this.text.substring(0, this.from) + TextInputField.this.text.substring(this.to + 1));
      TextInputField.this.caret.setLocation(this.to - 1);
      TextInputField.this.currentSelection = null;
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\buttons\TextInputField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */