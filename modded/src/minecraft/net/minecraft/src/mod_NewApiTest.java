package net.minecraft.src;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.model.SimpleButtonModel;

import net.minecraft.client.Minecraft;

public class mod_NewApiTest extends BaseMod {
	public ModSettings settings = new ModSettings("mod_settingstest");
	public ModSettingScreen modscreen = new ModSettingScreen("Tests");
	public SettingBoolean sbool = new SettingBoolean("bool", false);
	public WidgetBoolean wbool = new WidgetBoolean(this.sbool, "Boolean", "yes", "no");
	public SettingFloat sfloat;
	public WidgetFloat wfloat;
	public SettingInt sint;
	public WidgetInt wint;
	public SettingKey skey;
	public WidgetKeybinding wkey;
	public SettingMulti smult;
	public WidgetMulti wmult;
	public SettingText stext;
	public WidgetText wtext;
	public SettingKey skey2;

	public mod_NewApiTest() {
		this.modscreen.append(this.wbool);
		this.settings.append(this.sbool);
		this.sfloat = new SettingFloat("float");
		this.wfloat = new WidgetFloat(this.sfloat, "Floating-point");
		this.modscreen.append(this.wfloat);
		this.settings.append(this.sfloat);
		this.sint = new SettingInt("int");
		this.wint = new WidgetInt(this.sint, "Integer");
		this.modscreen.append(this.wint);
		this.settings.append(this.sint);
		this.skey = new SettingKey("key", "F10");
		this.wkey = new WidgetKeybinding(this.skey, "keybinding");
		this.settings.append(this.skey);
		this.smult = new SettingMulti("multi", new String[]{"A", "B", "c"});
		this.wmult = new WidgetMulti(this.smult, "Multi-choice");
		this.settings.append(this.smult);
		this.stext = new SettingText("text", "defvalue");
		this.wtext = new WidgetText(this.stext, "Text");
		this.settings.append(this.stext);
		this.settings.addSetting(this.modscreen, "addtestbool", "addtestbool", false, "yes", "no");
		this.settings.addSetting(this.modscreen, "addtestfloat", "addtestfloat", 0.8F, 0.0F, 0.1F, 2.0F);
		this.settings.addSetting((ModSettingScreen)this.modscreen, "addtestint", "addtestint", 8, 0, 1, 20);
		this.skey2 = this.settings.addSetting((ModSettingScreen)this.modscreen, "addtestkey", "addtestkey", 48);
		this.settings.addSetting((ModSettingScreen)this.modscreen, "addtestmulti", "addtestmulti", 0, new String[]{"one", "two", "three", "five"});
		this.settings.addSetting(this.modscreen, "addtesttext", "addfdsatesttext", "this was adsfa test");
		this.settings.addSetting(this.modscreen, "addtestt1ext", "addtestasdftext", "this wasfads a test");
		this.settings.addSetting(this.modscreen, "addtest3t2ext", "addtefdsasttext", "this wfadsas a test");
		this.settings.addSetting(this.modscreen, "addtes4t3text", "addteasdfsttext", "thiasdfs was a test");
		this.settings.addSetting(this.modscreen, "addtes5ttext", "addtasdfesttext", "this asdfwas a test");
		this.settings.addSetting(this.modscreen, "addtest5text", "addasdftesttext", "thifdss was a test");
		this.settings.addSetting(this.modscreen, "addtes4ttext", "addtesttfsdaext", "this wdsaas a test");
		this.settings.addSetting(this.modscreen, "addtest6text", "addtfdsaaesttext", "this wadsas a test");
		this.settings.addSetting(this.modscreen, "addtes7ttext", "addtesttsfdext", "this wassdf a test");
		Subscreen subscreen = new Subscreen("button", "title", new WidgetSinglecolumn(new Widget[0]));
		subscreen.setText("subscreen");
		this.settings.addSetting((Widget)subscreen, "subscreentest", "subscreentest", 0, new String[]{"one", "two", "three", "five"});
		subscreen.add(this.wkey);
		subscreen.add(this.wmult);
		subscreen.add(this.wtext);
		this.modscreen.append(subscreen);
		SimpleButtonModel resetmodel = new SimpleButtonModel();
		resetmodel.addActionCallback(new ModAction(this.settings, "resetAll", new Class[0]));
		Button reset = new Button(resetmodel);
		reset.setText("Reset all to defaults");
		this.modscreen.append(reset);
		this.settings.load();
		ModLoader.SetInGameHook(this, true, false);
	}

	public void OnTickInGame(Minecraft m) {
		if(m.fontRenderer == null && this.skey2.isKeyDown()) {
			GuiModScreen.show((Widget)(new WidgetSimplewindow(new WidgetClassicTwocolumn(new Widget[0]), "test")));
		}

	}

	public String Version() {
		return "1.4_01";
	}
}
