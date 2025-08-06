package net.minecraft.client.gui;

import java.util.Random;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.player.PlayerControllerCreative;
import net.minecraft.client.player.PlayerControllerSP;
import net.minecraft.src.MathHelper;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.StringTranslate;
import net.minecraft.util.Translator;
import net.minecraft.world.level.WorldSettings;
import net.minecraft.world.level.WorldType;
import net.minecraft.world.level.chunk.storage.ISaveFormat;

public class GuiCreateWorld extends GuiScreen {
	private GuiScreen parentGuiScreen;
	private GuiTextField textboxWorldName;
	private GuiTextField textboxSeed;
	private String folderName;
	private String gameMode = "survival";
	private boolean generateStructures = true;
	private boolean hardcoreEnabled = false;
	private boolean enableCheats = false;
	private boolean createClicked;
	private boolean moreOptions;
	private GuiButton gameModeButton;
	private GuiButton moreWorldOptions;
	private GuiButton generateStructuresButton;
	private GuiButton worldTypeButton;
	private GuiButton enableCheatsButton;
	private String gameModeDescriptionLine1;
	private String gameModeDescriptionLine2;
	private String seed; 
	private String localizedNewWorldText;
	private int worldType = 0;

	public GuiCreateWorld(GuiScreen guiScreen1) {
		this.parentGuiScreen = guiScreen1;
		this.seed = "";
		this.localizedNewWorldText = Translator.translateToLocal("selectWorld.newWorld");
	}

	public void updateScreen() {
		this.textboxWorldName.updateCursorCounter();
		this.textboxSeed.updateCursorCounter();
	}

	public void initGui() {
		StringTranslate stringTranslate1 = StringTranslate.getInstance();
		Keyboard.enableRepeatEvents(true);
		this.controlList.clear();
		this.controlList.add(new GuiButton(0, this.width / 2 - 155, this.height - 28, 150, 20, stringTranslate1.translateKey("selectWorld.create")));
		this.controlList.add(new GuiButton(1, this.width / 2 + 5, this.height - 28, 150, 20, stringTranslate1.translateKey("gui.cancel")));
		this.controlList.add(this.gameModeButton = new GuiButton(2, this.width / 2 - 75, 100, 150, 20, stringTranslate1.translateKey("selectWorld.gameMode")));
		this.controlList.add(this.moreWorldOptions = new GuiButton(3, this.width / 2 - 75, 172, 150, 20, stringTranslate1.translateKey("selectWorld.moreWorldOptions")));
		this.controlList.add(this.generateStructuresButton = new GuiButton(4, this.width / 2 - 155, 100, 150, 20, stringTranslate1.translateKey("selectWorld.mapFeatures")));
		this.generateStructuresButton.drawButton = false;
		this.controlList.add(this.worldTypeButton = new GuiButton(5, this.width / 2 + 5, 100, 150, 20, stringTranslate1.translateKey("selectWorld.mapType")));
		this.worldTypeButton.drawButton = false;
		this.textboxWorldName = new GuiTextField(this, this.fontRenderer, this.width / 2 - 100, 60, 200, 20);
		this.textboxWorldName.setFocused(true);
		this.controlList.add(this.enableCheatsButton = new GuiButton(6, this.width / 2 - 155, 124, 150, 20, stringTranslate1.translateKey("selectWorld.enableCheats")));
		this.enableCheatsButton.drawButton = false;
		this.textboxWorldName.setText(this.localizedNewWorldText);
		this.textboxSeed = new GuiTextField(this, this.fontRenderer, this.width / 2 - 100, 60, 200, 20);
		this.textboxSeed.setText(this.seed);
		this.makeUseableName();
		this.updateCaptions();
	}

	private void makeUseableName() {
		this.folderName = this.textboxWorldName.getText().trim();
		char[] c1 = ChatAllowedCharacters.allowedCharactersArray;
		int i2 = c1.length;

		for(int i3 = 0; i3 < i2; ++i3) {
			char c4 = c1[i3];
			this.folderName = this.folderName.replace(c4, '_');
		}

		if(MathHelper.stringNullOrLengthZero(this.folderName)) {
			this.folderName = "World";
		}

		this.folderName = func_25097_a(this.mc.getSaveLoader(), this.folderName);
	}

	private void updateCaptions() {
		StringTranslate stringTranslate1 = StringTranslate.getInstance();
		this.gameModeButton.displayString = stringTranslate1.translateKey("selectWorld.gameMode") + " " + stringTranslate1.translateKey("selectWorld.gameMode." + this.gameMode);
		this.gameModeDescriptionLine1 = stringTranslate1.translateKey("selectWorld.gameMode." + this.gameMode + ".line1");
		this.gameModeDescriptionLine2 = stringTranslate1.translateKey("selectWorld.gameMode." + this.gameMode + ".line2");
		this.generateStructuresButton.displayString = stringTranslate1.translateKey("selectWorld.mapFeatures") + " ";
		if(this.generateStructures) {
			this.generateStructuresButton.displayString = this.generateStructuresButton.displayString + stringTranslate1.translateKey("options.on");
		} else {
			this.generateStructuresButton.displayString = this.generateStructuresButton.displayString + stringTranslate1.translateKey("options.off");
		}
		this.enableCheatsButton.displayString = stringTranslate1.translateKey("selectWorld.enableCheats") + ": " + (this.enableCheats ? stringTranslate1.translateKey("options.on") : stringTranslate1.translateKey("options.off"));
		
		this.worldTypeButton.displayString = stringTranslate1.translateKey("selectWorld.mapType") + " " + stringTranslate1.translateKey(WorldType.worldTypes[this.worldType].getTranslateName());
	}

	public static String func_25097_a(ISaveFormat iSaveFormat0, String string1) {
		for(string1 = string1.replaceAll("[\\./\"]|COM", "_"); iSaveFormat0.getWorldInfo(string1) != null; string1 = string1 + "-") {
		}

		return string1;
	}

	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	protected void actionPerformed(GuiButton guiButton1) {
		if(guiButton1.enabled) {
			if(guiButton1.id == 1) {
				this.mc.displayGuiScreen(this.parentGuiScreen);
			} else if(guiButton1.id == 0) {
				this.mc.displayGuiScreen((GuiScreen)null);
				if(this.createClicked) {
					return;
				}

				this.createClicked = true;
				long j2 = (new Random()).nextLong();
				String string4 = this.textboxSeed.getText();
				if(!MathHelper.stringNullOrLengthZero(string4)) {
					try {
						long j5 = Long.parseLong(string4);
						if(j5 != 0L) {
							j2 = j5;
						}
					} catch (NumberFormatException numberFormatException7) {
						j2 = (long)string4.hashCode();
					}
				}

				byte b9 = 0;
				if(this.gameMode.equals("creative")) {
					b9 = 1;
					this.mc.playerController = new PlayerControllerCreative(this.mc);
				} else {
					this.mc.playerController = new PlayerControllerSP(this.mc);
				}

				this.mc.startWorld(this.folderName, this.textboxWorldName.getText(), 
						new WorldSettings(j2, b9, this.generateStructures, this.hardcoreEnabled, this.enableCheats, WorldType.worldTypes[this.worldType]));
				this.mc.displayGuiScreen((GuiScreen)null);
			} else if(guiButton1.id == 3) {
				this.moreOptions = !this.moreOptions;
				this.gameModeButton.drawButton = !this.moreOptions;
				this.generateStructuresButton.drawButton = this.moreOptions;
				this.worldTypeButton.drawButton = this.moreOptions;
				this.enableCheatsButton.drawButton = this.moreOptions;
				StringTranslate stringTranslate8;
				if(this.moreOptions) {
					stringTranslate8 = StringTranslate.getInstance();
					this.moreWorldOptions.displayString = stringTranslate8.translateKey("gui.done");
				} else {
					stringTranslate8 = StringTranslate.getInstance();
					this.moreWorldOptions.displayString = stringTranslate8.translateKey("selectWorld.moreWorldOptions");
				}
			} else if(guiButton1.id == 2) {
				// Select game mode
				
				if(this.gameMode.equals("survival")) {
					this.hardcoreEnabled = false;
					this.gameMode = "creative";
					this.updateCaptions();
					this.hardcoreEnabled = false;
				} else if(this.gameMode.equals("hardcore")) {
					this.gameMode = "survival";
					this.updateCaptions();
					this.hardcoreEnabled = false;
				} else {
					this.hardcoreEnabled = false;
					this.gameMode = "hardcore";
					this.hardcoreEnabled = true;
					this.updateCaptions();
				}

				this.updateCaptions();
			} else if(guiButton1.id == 4) {
				this.generateStructures = !this.generateStructures;
				this.updateCaptions();
			} else if(guiButton1.id == 5) {
				++this.worldType;
				if(this.worldType >= WorldType.worldTypes.length) {
					this.worldType = 0;
				}

				while(WorldType.worldTypes[this.worldType] == null || !WorldType.worldTypes[this.worldType].getCanBeCreated()) {
					++this.worldType;
					if(this.worldType >= WorldType.worldTypes.length) {
						this.worldType = 0;
					}
				}

				this.updateCaptions();
			} else if(guiButton1.id == 6) {
				this.enableCheats = !this.enableCheats;
				this.updateCaptions(); 
			}

		}
	}

	protected void keyTyped(char c1, int i2) {
		if(this.textboxWorldName.getIsFocused() && !this.moreOptions) {
			this.textboxWorldName.textboxKeyTyped(c1, i2);
			this.localizedNewWorldText = this.textboxWorldName.getText();
		} else if(this.textboxSeed.getIsFocused() && this.moreOptions) {
			this.textboxSeed.textboxKeyTyped(c1, i2);
			this.seed = this.textboxSeed.getText();
		}

		if(c1 == 13) {
			this.actionPerformed((GuiButton)this.controlList.get(0));
		}

		((GuiButton)this.controlList.get(0)).enabled = this.textboxWorldName.getText().length() > 0;
		this.makeUseableName();
	}

	protected void mouseClicked(int i1, int i2, int i3) {
		super.mouseClicked(i1, i2, i3);
		if(!this.moreOptions) {
			this.textboxWorldName.mouseClicked(i1, i2, i3);
		} else {
			this.textboxSeed.mouseClicked(i1, i2, i3);
		}

	}

	public void drawScreen(int i1, int i2, float f3) {
		StringTranslate stringTranslate4 = StringTranslate.getInstance();
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, stringTranslate4.translateKey("selectWorld.create"), this.width / 2, 20, 0xFFFFFF);
		if(!this.moreOptions) {
			this.drawString(this.fontRenderer, stringTranslate4.translateKey("selectWorld.enterName"), this.width / 2 - 100, 47, 10526880);
			this.drawString(this.fontRenderer, stringTranslate4.translateKey("selectWorld.resultFolder") + " " + this.folderName, this.width / 2 - 100, 85, 10526880);
			this.textboxWorldName.drawTextBox();
			this.drawString(this.fontRenderer, this.gameModeDescriptionLine1, this.width / 2 - 100, 122, 10526880);
			this.drawString(this.fontRenderer, this.gameModeDescriptionLine2, this.width / 2 - 100, 134, 10526880);
		} else {
			this.drawString(this.fontRenderer, stringTranslate4.translateKey("selectWorld.enterSeed"), this.width / 2 - 100, 47, 10526880);
			this.drawString(this.fontRenderer, stringTranslate4.translateKey("selectWorld.seedInfo"), this.width / 2 - 100, 85, 10526880);
			//this.drawString(this.fontRenderer, stringTranslate4.translateKey("selectWorld.mapFeatures.info"), this.width / 2 - 150, 122, 10526880);
			this.textboxSeed.drawTextBox();
		}

		super.drawScreen(i1, i2, f3);
	}
}
