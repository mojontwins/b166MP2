package net.minecraft.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.util.StringTranslate;
import net.minecraft.world.GameRule;
import net.minecraft.world.GameRules;

public class GuiGameRules extends GuiScreen {
	private GuiScreen parentScreen;
	private boolean ingame;
	protected String screenTitle = "Game rules";
	
	private List<GuiButton> categoryButtons = new ArrayList<>();
	private List<List<GuiButton>> optionButtons = new ArrayList<>();
	private Map<Integer,GameRule> buttonIdToRuleMap = new HashMap<>();
	
	private int maxCategories = 0;
	private int curCategory;
	
	public GuiGameRules(GuiScreen parent, boolean ingame) {
		this.parentScreen = parent;
		this.ingame = ingame;
		this.maxCategories = GameRules.categories.length;
	}
	
	public void initGui() {
		// If not igname, reload defaults.
		if(!this.ingame) {
			GameRules.loadRulesFromOptions();
		}
		
		StringTranslate st = StringTranslate.getInstance();
		int catId = 100;
		int catButtonW = 80;
		int ruleButtonW = 140;
		int x = this.width / 2 - (catButtonW + 2) * maxCategories / 2;
		int yCatButtons = 32;
		
		// Read categories
		for(String category : GameRules.categories) {
			GuiButton catButton = new GuiButton(catId, x, yCatButtons, catButtonW, 20, category);
			
			this.controlList.add(catButton);
			this.categoryButtons.add(catButton);
			
			// Read options
			List<GameRule> thisCatRules = GameRules.getRulesOfCategory(category);
			int lr = 0; int y = yCatButtons + 32; int ruleId = 1;
			List<GuiButton> thisCatButtons = new ArrayList<>();
			
			for(GameRule rule : thisCatRules) {				
				int buttonId = catId + ruleId;
				GuiButton ruleButton = new GuiCheckButton(buttonId, this.width / 2 - (ruleButtonW + 2) + lr * (ruleButtonW + 4), y, ruleButtonW, 20, st.translateKey("gamerule." + rule.getCaption()));
				ruleButton.forcedOn = rule.getValue();
				
				this.controlList.add(ruleButton);
				thisCatButtons.add(ruleButton);
				buttonIdToRuleMap.put(buttonId, rule);
				
				lr = 1 - lr; if (lr == 0) y += 22;
				ruleId ++;
			}
			
			optionButtons.add(thisCatButtons);

			catId += 100;
			x += catButtonW + 2;
						
		}
		
		this.controlList.add(new GuiButton(1000, this.width / 2 - 100, this.height / 6 + 168, st.translateKey("gui.done")));
		this.setCurCategory(0);
	}
	
	public void onGuiClosed() {
		if(!this.ingame) {
			GameRules.saveRulesAsOptions();
		} else {
			this.parentScreen.updateCounter = 0;
		}
	}
	
	protected void setCurCategory(int i) {
		curCategory = i;
		for(int j = 0; j < maxCategories; j ++) {
			List<GuiButton> thisCatButtons = this.optionButtons.get(j);
			
			if(j == i) {
				this.categoryButtons.get(j).forcedOn = true;
				for(GuiButton button : thisCatButtons) button.drawButton = true;
				
			} else {
				this.categoryButtons.get(j).forcedOn = false;
				for(GuiButton button : thisCatButtons) button.drawButton = false;
				
			}
		}
	}
	
	protected void actionPerformed(GuiButton guiButton) {
		for(int i = 0; i < maxCategories; i ++) {
			if(guiButton.id == (i + 1) * 100 && i != curCategory) {
				this.setCurCategory(i);
				return;
			}
		} 
		
		if(guiButton.id == 1000) {
			this.mc.displayGuiScreen(this.parentScreen);
		} else {
			GameRule rule = this.buttonIdToRuleMap.get(guiButton.id);
			if(rule != null) {
				rule.toggle();
				guiButton.forcedOn = rule.getValue();
			}
		}
	}
	
	public void drawScreen(int i1, int i2, float f3) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 20, 0xFFFFFF);
		super.drawScreen(i1, i2, f3);
	}
}
