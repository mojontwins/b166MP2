package net.minecraft.src;

public class ModCallback implements Runnable {
	public static final int BACK = 0;
	public static final int SELECTMOD = 1;
	public Object data;
	public int type;

	public ModCallback(int t, Object _data) {
		this.type = t;
		this.data = _data;
	}

	public void run() {
		if(this.type == 0) {
			GuiModScreen.back();
			GuiModScreen.clicksound();
		} else if(this.type == 1) {
			Integer i = (Integer)this.data;
			int modnum = i.intValue();
			GuiModScreen.show(((ModSettingScreen)ModSettingScreen.modscreens.get(modnum)).thewidget);
			GuiModScreen.clicksound();
		}

	}
}
