package co.crystaldev.client.feature.impl.hud;

import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.network.ServerDisconnectEvent;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.Toggle;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.HudModule;
import co.crystaldev.client.util.enums.AnchorRegion;
import co.crystaldev.client.util.enums.ChatColor;
import co.crystaldev.client.util.objects.ModulePosition;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.scoreboard.*;
import net.minecraft.util.EnumChatFormatting;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@ModuleInfo(name = "Scoreboard", description = "Modify the default Minecraft scoreboard", category = Category.HUD)
public class CustomScoreboard extends HudModule implements IRegistrable {
    @Toggle(label = "Show Scores")
    public boolean showScores = false;

    @Toggle(label = "Text Shadow")
    public boolean textShadow = false;

    @Toggle(label = "Show Background")
    public boolean background = true;

    private static CustomScoreboard INSTANCE;

    private final FontRenderer fontRenderer = (Minecraft.getMinecraft()).fontRendererObj;

    private ScoreObjective dummyObjective;

    public CustomScoreboard() {
        this.enabled = true;
        this.position = new ModulePosition(AnchorRegion.CENTER_RIGHT, 0.0F, 0.0F);
        INSTANCE = this;
    }

    public void draw() {
        ScoreObjective objective = getScoreboard();
        if (objective != null)
            renderScoreboard(objective);
    }

    public void drawDefault() {
        if (this.dummyObjective == null)
            setDummyScoreboard();
        ScoreObjective objective = getScoreboard();
        if (objective == null) {
            renderScoreboard(this.dummyObjective);
        } else {
            renderScoreboard(objective);
        }
    }

    private void renderScoreboard(ScoreObjective objective) {
        Scoreboard scoreboard = objective.getScoreboard();
        Collection<Score> collection = scoreboard.getSortedScores(objective);
        List<Score> list = (List<Score>) collection.stream().filter(p -> (p.getPlayerName() != null && !p.getPlayerName().startsWith("#"))).collect(Collectors.toList());

        if (list.size() > 15) {
            collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
        } else {
            collection = list;
        }
        int maxTextWidth = this.fontRenderer.getStringWidth(objective.getDisplayName());

        for (Score score : collection) {
            ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(score.getPlayerName());
            String s = ScorePlayerTeam.formatPlayerName((Team) scoreplayerteam, score.getPlayerName()) + (this.showScores ? (": " + EnumChatFormatting.RED + score.getScorePoints()) : "");
            maxTextWidth = Math.max(maxTextWidth, this.fontRenderer.getStringWidth(s));
        }

        int margin = 3;
        this.width = maxTextWidth + 3;
        this.height = collection.size() * this.fontRenderer.FONT_HEIGHT + this.fontRenderer.FONT_HEIGHT;
        int boardX = getRenderX();
        int boardY = getRenderY() + this.height;
        int index = 0;

        for (Score score : collection) {
            index++;
            ScorePlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
            String nameString = ScorePlayerTeam.formatPlayerName((Team) team, score.getPlayerName());
            String pointsString = this.showScores ? (EnumChatFormatting.RED + "" + score.getScorePoints()) : "";
            int currentY = boardY - index * this.fontRenderer.FONT_HEIGHT;
            int boardMaxX = boardX + this.width;

            if (this.background) {
                GuiIngame.drawRect(boardX - 2, currentY, boardMaxX, currentY + this.fontRenderer.FONT_HEIGHT, 1342177280);
            }
            this.fontRenderer.drawString(nameString, boardX, currentY, 553648127, this.textShadow);
            this.fontRenderer.drawString(pointsString, (boardMaxX - this.fontRenderer.getStringWidth(pointsString)), currentY, 553648127, this.textShadow);

            if (index == collection.size()) {
                String title = objective.getDisplayName();
                if (this.background) {
                    GuiIngame.drawRect(boardX - 2, currentY - this.fontRenderer.FONT_HEIGHT - 1, boardMaxX, currentY - 1, 1610612736);
                    GuiIngame.drawRect(boardX - 2, currentY - 1, boardMaxX, currentY, 1342177280);
                }
                this.fontRenderer.drawString(title, boardX + maxTextWidth / 2.0F - this.fontRenderer.getStringWidth(title) / 2.0F, (currentY - this.fontRenderer.FONT_HEIGHT), 553648127, this.textShadow);
            }
        }
    }

private void setDummyScoreboard() {
    String teamName = ChatColor.translate('&', "&d&lCrystal Client");
    List<String> scoresToRegister = Arrays.asList(new String[] {
            "&7&o  https://discord.gg/mmVWkk93E9", "&7 ", "  &f&l* &dDummy: &fScoreboard7", "  &f&l* &dDummy: &fScoreboard6", "  &f&l* &dDummy: &fScoreboard5", "  &f&l* &dDummy: &fScoreboard4", "&f&lAnother Section", "&7 ", "  &f&l* &dDummy: &fScoreboard3", "  &f&l* &dDummy: &fScoreboard2",
            "  &f&l* &dDummy: &fScoreboard1", "&f&l" +

            Minecraft.getMinecraft().getSession().getUsername() });
    Scoreboard dummyScoreboard = new Scoreboard();
    ScoreObjective objective = dummyScoreboard.addScoreObjective("test", IScoreObjectiveCriteria.DUMMY);
    objective.setDisplayName(teamName);
    for (int i = scoresToRegister.size() - 1; i >= 0; i--) {
        Score score = dummyScoreboard.getValueFromObjective(ChatColor.translate('&', scoresToRegister.get(i)), objective);
        score.setScorePoints(i);
    }
    objective.setRenderType(IScoreObjectiveCriteria.EnumRenderType.INTEGER);
    this.dummyObjective = objective;
}

    private ScoreObjective getScoreboard() {
        Scoreboard scoreboard = this.mc.theWorld.getScoreboard();
        ScoreObjective scoreobjective = null;
        ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(this.mc.thePlayer.getName());

        if (scoreplayerteam != null) {
            int i1 = scoreplayerteam.getChatFormat().getColorIndex();

            if (i1 >= 0) {
                scoreobjective = scoreboard.getObjectiveInDisplaySlot(3 + i1);
            }
        }
        ScoreObjective scoreobjective1 = (scoreobjective != null) ? scoreobjective : scoreboard.getObjectiveInDisplaySlot(1);
        return scoreobjective1;
    }


    public void registerEvents() {
        EventBus.register(this, ServerDisconnectEvent.class, ev -> this.dummyObjective = null);
    }

    public static CustomScoreboard getInstance() {
        return INSTANCE;
    }
}