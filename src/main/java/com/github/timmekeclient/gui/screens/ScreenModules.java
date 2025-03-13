package com.github.timmekeclient.gui.screens;

import com.github.timmekeclient.feature.base.Category;
import com.github.timmekeclient.feature.base.Module;
import com.github.timmekeclient.font.Fonts;
import com.github.timmekeclient.gui.Pane;
import com.github.timmekeclient.gui.buttons.ModuleButton;
import com.github.timmekeclient.gui.buttons.NavigationButton;
import com.github.timmekeclient.gui.buttons.SearchButton;
import com.github.timmekeclient.handler.ModuleHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class ScreenModules extends ScreenBase {
    private NavigationButton<Category> navbar;

    private SearchButton search;

    public void init() {
        super.init();
        this.content.setScrollIf(b -> b instanceof ModuleButton);
        int half = this.header.height / 2 - Fonts.NUNITO_SEMI_BOLD_24.getStringHeight() / 2;
        int h = this.header.height - half * 2;
        addButton((this.navbar = new NavigationButton(Category.ALL, this.header.x + this.header.width / 2, this.header.y + this.header.height / 2)));
        addButton((this.search = new SearchButton(this.header.x + this.header.width - half - h, this.header.y + half, h, h * 6, h)));
        initModules();
    }

    public void draw(int mouseX, int mouseY, float partialTicks) {
        super.draw(mouseX, mouseY, partialTicks);
        if (this.navbar.wasUpdated() || this.search.wasUpdated())
            initModules();
        this.content.scroll(this, mouseX, mouseY);
    }

    public void initModules() {
        removeButton(b -> b instanceof ModuleButton);
        int x = this.content.x + 14;
        int y = this.content.y + 5;
        int w = (this.content.width - 56) / 3;
        int h = 40;
        final Pane scissor = this.content.scale(getScaledScreen());
        scissor.x -= 6;
        scissor.width += 12;
        List<Module> sortedModules = new ArrayList<>(ModuleHandler.getModules());
        sortedModules.sort(Comparator.comparing(m -> !((Module) m).hoisted).thenComparing(m -> ((Module) m).getSanitizedName()));
        int rowIndex = 0;
        for (Module module : sortedModules) {
            if ((this.navbar.getCurrent() != Category.ALL && module.category != this.navbar.getCurrent()) || (
                    !this.search.matchesQuery(module.name) && Stream.<String>of(module.nameAliases).noneMatch(this.search::matchesQuery)))
                continue;
            addButton(new ModuleButton(module, x + rowIndex * (w + 14), y, w, h), b -> {
                b.setScissorPane(scissor);
            });
            rowIndex++;
            if (rowIndex == 3) {
                y += h + 10;
                rowIndex = 0;
            }
        }
        this.content.updateMaxScroll(this, 5);
        this.content.addScrollbarToScreen(this);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\screens\ScreenModules.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */