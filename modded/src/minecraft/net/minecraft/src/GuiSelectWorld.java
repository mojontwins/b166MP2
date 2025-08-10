package net.minecraft.src;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

public class GuiSelectWorld extends GuiScreen {
	private final DateFormat dateFormatter = new SimpleDateFormat();
	protected GuiScreen parentScreen;
	protected String screenTitle = "Select world";
	private boolean selected = false;
	private int selectedWorld;
	private List saveList;
	private GuiWorldSlot worldSlotContainer;
	private String field_22098_o;
	private String field_22097_p;
	private boolean deleting;
	private GuiButton buttonRename;
	private GuiButton buttonSelect;
	private GuiButton buttonDelete;

	public GuiSelectWorld(GuiScreen cy1) {
		this.parentScreen = cy1;
	}

	public void initGui() {
		StringTranslate nd1 = StringTranslate.getInstance();
		this.screenTitle = nd1.translateKey("selectWorld.title");
		this.field_22098_o = nd1.translateKey("selectWorld.world");
		this.field_22097_p = nd1.translateKey("selectWorld.conversion");
		this.loadSaves();
		this.worldSlotContainer = new GuiWorldSlot(this);
		this.worldSlotContainer.registerScrollButtons(this.controlList, 4, 5);
		this.initButtons();
	}

	private void loadSaves() {
		ISaveFormat nh1 = this.mc.getSaveLoader();
		this.saveList = nh1.func_22176_b();
		Collections.sort(this.saveList);
		this.selectedWorld = -1;
	}

	protected String getSaveFileName(int i1) {
		return ((SaveFormatComparator)this.saveList.get(i1)).getFileName();
	}

	protected String getSaveName(int i1) {
		String s1 = ((SaveFormatComparator)this.saveList.get(i1)).getDisplayName();
		if(s1 == null || MathHelper.stringNullOrLengthZero(s1)) {
			StringTranslate nd1 = StringTranslate.getInstance();
			s1 = nd1.translateKey("selectWorld.world") + " " + (i1 + 1);
		}

		return s1;
	}

	public void initButtons() {
		StringTranslate nd1 = StringTranslate.getInstance();
		this.controlList.add(this.buttonSelect = new GuiButton(1, this.width / 2 - 154, this.height - 52, 150, 20, nd1.translateKey("selectWorld.select")));
		this.controlList.add(this.buttonRename = new GuiButton(6, this.width / 2 - 154, this.height - 28, 70, 20, nd1.translateKey("selectWorld.rename")));
		this.controlList.add(this.buttonDelete = new GuiButton(2, this.width / 2 - 74, this.height - 28, 70, 20, nd1.translateKey("selectWorld.delete")));
		this.controlList.add(new GuiButton(3, this.width / 2 + 4, this.height - 52, 150, 20, nd1.translateKey("selectWorld.create")));
		this.controlList.add(new GuiButton(0, this.width / 2 + 4, this.height - 28, 150, 20, nd1.translateKey("gui.cancel")));
		this.buttonSelect.enabled = false;
		this.buttonRename.enabled = false;
		this.buttonDelete.enabled = false;
	}

	protected void actionPerformed(GuiButton ka1) {
		if(ka1.enabled) {
			if(ka1.id == 2) {
				String s1 = this.getSaveName(this.selectedWorld);
				if(s1 != null) {
					this.deleting = true;
					StringTranslate nd1 = StringTranslate.getInstance();
					String s2 = nd1.translateKey("selectWorld.deleteQuestion");
					String s3 = "\'" + s1 + "\' " + nd1.translateKey("selectWorld.deleteWarning");
					String s4 = nd1.translateKey("selectWorld.deleteButton");
					String s5 = nd1.translateKey("gui.cancel");
					GuiYesNo qo1 = new GuiYesNo(this, s2, s3, s4, s5, this.selectedWorld);
					this.mc.displayGuiScreen(qo1);
				}
			} else if(ka1.id == 1) {
				this.selectWorld(this.selectedWorld);
			} else if(ka1.id == 3) {
				this.mc.displayGuiScreen(new GuiCreateWorld(this));
			} else if(ka1.id == 6) {
				this.mc.displayGuiScreen(new GuiRenameWorld(this, this.getSaveFileName(this.selectedWorld)));
			} else if(ka1.id == 0) {
				this.mc.displayGuiScreen(this.parentScreen);
			} else {
				this.worldSlotContainer.actionPerformed(ka1);
			}

		}
	}

	public void selectWorld(int i1) {
		this.mc.displayGuiScreen((GuiScreen)null);
		if(!this.selected) {
			this.selected = true;
			this.mc.playerController = new PlayerControllerSP(this.mc);
			String s1 = this.getSaveFileName(i1);
			if(s1 == null) {
				s1 = "World" + i1;
			}

			this.mc.startWorld(s1, this.getSaveName(i1), 0L);
			this.mc.displayGuiScreen((GuiScreen)null);
			ModSettings.setContext("SP" + this.mc.theWorld.worldInfo.getWorldName(), "saves/" + this.mc.theWorld.worldInfo.getWorldName() + "/mods/");
		}
	}

	public void deleteWorld(boolean flag, int i1) {
		if(this.deleting) {
			this.deleting = false;
			if(flag) {
				ISaveFormat nh1 = this.mc.getSaveLoader();
				nh1.flushCache();
				nh1.func_22172_c(this.getSaveFileName(i1));
				this.loadSaves();
			}

			this.mc.displayGuiScreen(this);
		}

	}

	public void drawScreen(int i1, int j1, float f1) {
		this.worldSlotContainer.drawScreen(i1, j1, f1);
		this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 20, 0xFFFFFF);
		super.drawScreen(i1, j1, f1);
	}

	static List getSize(GuiSelectWorld rl1) {
		return rl1.saveList;
	}

	static int onElementSelected(GuiSelectWorld rl1, int i1) {
		return rl1.selectedWorld = i1;
	}

	static int getSelectedWorld(GuiSelectWorld rl1) {
		return rl1.selectedWorld;
	}

	static GuiButton getSelectButton(GuiSelectWorld rl1) {
		return rl1.buttonSelect;
	}

	static GuiButton getRenameButton(GuiSelectWorld rl1) {
		return rl1.buttonRename;
	}

	static GuiButton getDeleteButton(GuiSelectWorld rl1) {
		return rl1.buttonDelete;
	}

	static String func_22087_f(GuiSelectWorld rl1) {
		return rl1.field_22098_o;
	}

	static DateFormat getDateFormatter(GuiSelectWorld rl1) {
		return rl1.dateFormatter;
	}

	static String func_22088_h(GuiSelectWorld rl1) {
		return rl1.field_22097_p;
	}
}
