package net.minecraft.src;

import de.matthiasmann.twl.Widget;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import net.minecraft.client.Minecraft;

public class ModSettings {
	public ArrayList Settings;
	public static ArrayList all = new ArrayList();
	public String backendname;
	public static String currentContext = "";
	public static HashMap contextDatadirs = new HashMap();
	public boolean have_loaded = false;
	public static final boolean debug = false;

	static {
		contextDatadirs.put("", "mods");
	}

	public ModSettings(String modbackendname) {
		this.backendname = modbackendname;
		this.Settings = new ArrayList();
		all.add(this);
	}

	public SettingBoolean addSetting(ModSettingScreen screen, String nicename, String backendname, boolean value) {
		SettingBoolean s = new SettingBoolean(backendname, value);
		WidgetBoolean w = new WidgetBoolean(s, nicename);
		screen.append(w);
		this.append(s);
		return s;
	}

	public SettingBoolean addSetting(ModSettingScreen screen, String nicename, String backendname, boolean value, String truestring, String falsestring) {
		SettingBoolean s = new SettingBoolean(backendname, value);
		WidgetBoolean w = new WidgetBoolean(s, nicename, truestring, falsestring);
		screen.append(w);
		this.append(s);
		return s;
	}

	public SettingFloat addSetting(ModSettingScreen screen, String nicename, String backendname, float value) {
		SettingFloat s = new SettingFloat(backendname, value);
		WidgetFloat w = new WidgetFloat(s, nicename);
		screen.append(w);
		this.append(s);
		return s;
	}

	public SettingFloat addSetting(ModSettingScreen screen, String nicename, String backendname, float value, float min, float step, float max) {
		SettingFloat s = new SettingFloat(backendname, value, min, step, max);
		WidgetFloat w = new WidgetFloat(s, nicename);
		screen.append(w);
		this.append(s);
		return s;
	}

	public SettingInt addSetting(ModSettingScreen screen, String nicename, String backendname, int value, int min, int max) {
		SettingInt s = new SettingInt(backendname, value, min, 1, max);
		WidgetInt w = new WidgetInt(s, nicename);
		screen.append(w);
		this.append(s);
		return s;
	}

	public SettingInt addSetting(ModSettingScreen screen, String nicename, String backendname, int value, int min, int step, int max) {
		SettingInt s = new SettingInt(backendname, value, min, step, max);
		WidgetInt w = new WidgetInt(s, nicename);
		screen.append(w);
		this.append(s);
		return s;
	}

	public SettingKey addSetting(ModSettingScreen screen, String nicename, String backendname, int value) {
		SettingKey s = new SettingKey(backendname, value);
		WidgetKeybinding w = new WidgetKeybinding(s, nicename);
		screen.append(w);
		this.append(s);
		return s;
	}

	public SettingMulti addSetting(ModSettingScreen screen, String nicename, String backendname, int value, String... labels) {
		SettingMulti s = new SettingMulti(backendname, value, labels);
		WidgetMulti w = new WidgetMulti(s, nicename);
		screen.append(w);
		this.append(s);
		return s;
	}

	public SettingText addSetting(ModSettingScreen screen, String nicename, String backendname, String value) {
		SettingText s = new SettingText(backendname, value);
		WidgetText w = new WidgetText(s, nicename);
		screen.append(w);
		this.append(s);
		return s;
	}

	public SettingBoolean addSetting(Widget w2, String nicename, String backendname, boolean value) {
		SettingBoolean s = new SettingBoolean(backendname, value);
		WidgetBoolean w = new WidgetBoolean(s, nicename);
		w2.add(w);
		this.append(s);
		return s;
	}

	public SettingBoolean addSetting(Widget w2, String nicename, String backendname, boolean value, String truestring, String falsestring) {
		SettingBoolean s = new SettingBoolean(backendname, value);
		WidgetBoolean w = new WidgetBoolean(s, nicename, truestring, falsestring);
		w2.add(w);
		this.append(s);
		return s;
	}

	public SettingFloat addSetting(Widget w2, String nicename, String backendname, float value) {
		SettingFloat s = new SettingFloat(backendname, value);
		WidgetFloat w = new WidgetFloat(s, nicename);
		w2.add(w);
		this.append(s);
		return s;
	}

	public SettingFloat addSetting(Widget w2, String nicename, String backendname, float value, float min, float step, float max) {
		SettingFloat s = new SettingFloat(backendname, value, min, step, max);
		WidgetFloat w = new WidgetFloat(s, nicename);
		w2.add(w);
		this.append(s);
		return s;
	}

	public SettingInt addSetting(Widget w2, String nicename, String backendname, int value, int min, int max) {
		SettingInt s = new SettingInt(backendname, value, min, 1, max);
		WidgetInt w = new WidgetInt(s, nicename);
		w2.add(w);
		this.append(s);
		return s;
	}

	public SettingInt addSetting(Widget w2, String nicename, String backendname, int value, int min, int step, int max) {
		SettingInt s = new SettingInt(backendname, value, min, step, max);
		WidgetInt w = new WidgetInt(s, nicename);
		w2.add(w);
		this.append(s);
		return s;
	}

	public SettingKey addSetting(Widget w2, String nicename, String backendname, int value) {
		SettingKey s = new SettingKey(backendname, value);
		WidgetKeybinding w = new WidgetKeybinding(s, nicename);
		w2.add(w);
		this.append(s);
		return s;
	}

	public SettingMulti addSetting(Widget w2, String nicename, String backendname, int value, String... labels) {
		SettingMulti s = new SettingMulti(backendname, value, labels);
		WidgetMulti w = new WidgetMulti(s, nicename);
		w2.add(w);
		this.append(s);
		return s;
	}

	public SettingText addSetting(Widget w2, String nicename, String backendname, String value) {
		SettingText s = new SettingText(backendname, value);
		WidgetText w = new WidgetText(s, nicename);
		w2.add(w);
		this.append(s);
		return s;
	}

	public static void setContext(String name, String location) {
		if(name != null) {
			contextDatadirs.put(name, location);
			currentContext = name;
			if(!name.equals("")) {
				loadAll(currentContext);
			}
		} else {
			currentContext = "";
		}

	}

	public void append(Setting s) {
		this.Settings.add(s);
		s.parent = this;
	}

	public void remove(Setting s) {
		this.Settings.remove(s);
		s.parent = null;
	}

	public int size() {
		return this.Settings.size();
	}

	public void resetAll(String context) {
		for(int i = 0; i < this.Settings.size(); ++i) {
			((Setting)this.Settings.get(i)).reset(context);
		}

	}

	public void resetAll() {
		this.resetAll(currentContext);
	}

	public void copyContextAll(String src, String dest) {
		for(int i = 0; i < this.Settings.size(); ++i) {
			((Setting)this.Settings.get(i)).copyContext(src, dest);
		}

	}

	public static File getAppDir(String app) {
		return Minecraft.getAppDir(app);
	}

	public void save(String context) {
		if(this.have_loaded) {
			try {
				File e = getAppDir("minecraft/" + (String)contextDatadirs.get(context) + "/" + this.backendname + "/");
				dbgout("saving context " + context + " (" + e.getAbsolutePath() + " [" + (String)contextDatadirs.get(context) + "])");
				if(!e.exists()) {
					e.mkdirs();
				}

				File file = new File(e, "guiconfig.properties");
				Properties p = new Properties();

				for(int out = 0; out < this.Settings.size(); ++out) {
					Setting z = (Setting)this.Settings.get(out);
					p.put(z.backendname, z.toString(context));
				}

				FileOutputStream fileOutputStream8 = new FileOutputStream(file);
				p.store(fileOutputStream8, "");
			} catch (Exception exception7) {
				exception7.printStackTrace();
			}

		}
	}

	public void load(String context) {
		try {
			if(contextDatadirs.get(context) != null) {
				File e = getAppDir("minecraft/" + (String)contextDatadirs.get(context) + "/" + this.backendname + "/");
				if(e.exists()) {
					File file = new File(e, "guiconfig.properties");
					if(file.exists()) {
						Properties p = new Properties();
						p.load(new FileInputStream(file));

						for(int i = 0; i < this.Settings.size(); ++i) {
							if(this.Settings.get(i) instanceof Setting) {
								dbgout("setting load");
								Setting z = (Setting)this.Settings.get(i);
								if(p.containsKey(z.backendname)) {
									dbgout("setting " + (String)p.get(z.backendname));
									z.fromString((String)p.get(z.backendname), context);
								}
							}
						}
					}
				}
			}
		} catch (Exception exception7) {
			System.out.println(exception7);
		}

	}

	public void load() {
		this.load("");
		this.have_loaded = true;
	}

	public static void loadAll(String context) {
		for(int i = 0; i < all.size(); ++i) {
			((ModSettings)all.get(i)).load(context);
		}

	}

	public static void dbgout(String s) {
	}
}
