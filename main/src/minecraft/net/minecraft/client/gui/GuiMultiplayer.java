package net.minecraft.client.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.mojang.nbt.CompressedStreamTools;
import com.mojang.nbt.NBTTagCompound;
import com.mojang.nbt.NBTTagList;

import net.minecraft.network.packet.Packet;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.StringTranslate;
import net.minecraft.util.Translator;

public class GuiMultiplayer extends GuiScreen {
	private static int threadsPending = 0;
	private static Object lock = new Object();
	private GuiScreen parentScreen;
	private GuiSlotServer serverSlotContainer;
	private List<ServerNBTStorage> serverList = new ArrayList<ServerNBTStorage>();
	private int selectedServer = -1;
	private GuiButton buttonEdit;
	private GuiButton buttonSelect;
	private GuiButton buttonDelete;
	private boolean deleteClicked = false;
	private boolean addClicked = false;
	private boolean editClicked = false;
	private boolean directClicked = false;
	private String lagTooltip = null;
	private ServerNBTStorage tempServer = null;

	public GuiMultiplayer(GuiScreen guiScreen1) {
		this.parentScreen = guiScreen1;
	}

	public void updateScreen() {
	}

	public void initGui() {
		this.loadServerList();
		Keyboard.enableRepeatEvents(true);
		this.controlList.clear();
		this.serverSlotContainer = new GuiSlotServer(this);
		this.initGuiControls();
	}

	private void loadServerList() {
		try {
			NBTTagCompound compoundTag = CompressedStreamTools.read(new File(this.mc.mcDataDir, "servers.dat"));
			NBTTagList nBTTagList2 = compoundTag.getTagList("servers");
			this.serverList.clear();

			for(int i3 = 0; i3 < nBTTagList2.tagCount(); ++i3) {
				this.serverList.add(ServerNBTStorage.createServerNBTStorage((NBTTagCompound)nBTTagList2.tagAt(i3)));
			}
		} catch (Exception exception4) {
			exception4.printStackTrace();
		}

	}

	private void saveServerList() {
		try {
			NBTTagList nBTTagList1 = new NBTTagList();

			for(int i2 = 0; i2 < this.serverList.size(); ++i2) {
				nBTTagList1.appendTag(((ServerNBTStorage)this.serverList.get(i2)).getCompoundTag());
			}

			NBTTagCompound nBTTagCompound4 = new NBTTagCompound();
			nBTTagCompound4.setTag("servers", nBTTagList1);
			CompressedStreamTools.safeWrite(nBTTagCompound4, new File(this.mc.mcDataDir, "servers.dat"));
		} catch (Exception exception3) {
			exception3.printStackTrace();
		}

	}

	public void initGuiControls() {
		StringTranslate stringTranslate1 = StringTranslate.getInstance();
		this.controlList.add(this.buttonEdit = new GuiButton(7, this.width / 2 - 154, this.height - 28, 70, 20, stringTranslate1.translateKey("selectServer.edit")));
		this.controlList.add(this.buttonDelete = new GuiButton(2, this.width / 2 - 74, this.height - 28, 70, 20, stringTranslate1.translateKey("selectServer.delete")));
		this.controlList.add(this.buttonSelect = new GuiButton(1, this.width / 2 - 154, this.height - 52, 100, 20, stringTranslate1.translateKey("selectServer.select")));
		this.controlList.add(new GuiButton(4, this.width / 2 - 50, this.height - 52, 100, 20, stringTranslate1.translateKey("selectServer.direct")));
		this.controlList.add(new GuiButton(3, this.width / 2 + 4 + 50, this.height - 52, 100, 20, stringTranslate1.translateKey("selectServer.add")));
		this.controlList.add(new GuiButton(8, this.width / 2 + 4, this.height - 28, 70, 20, stringTranslate1.translateKey("selectServer.refresh")));
		this.controlList.add(new GuiButton(0, this.width / 2 + 4 + 76, this.height - 28, 75, 20, stringTranslate1.translateKey("gui.cancel")));
		boolean z2 = this.selectedServer >= 0 && this.selectedServer < this.serverSlotContainer.getSize();
		this.buttonSelect.enabled = z2;
		this.buttonEdit.enabled = z2;
		this.buttonDelete.enabled = z2;
	}

	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	protected void actionPerformed(GuiButton guiButton1) {
		if(guiButton1.enabled) {
			if(guiButton1.id == 2) {
				String string2 = ((ServerNBTStorage)this.serverList.get(this.selectedServer)).name;
				if(string2 != null) {
					this.deleteClicked = true;
					StringTranslate stringTranslate3 = StringTranslate.getInstance();
					String string4 = stringTranslate3.translateKey("selectServer.deleteQuestion");
					String string5 = "\'" + string2 + "\' " + stringTranslate3.translateKey("selectServer.deleteWarning");
					String string6 = stringTranslate3.translateKey("selectServer.deleteButton");
					String string7 = stringTranslate3.translateKey("gui.cancel");
					GuiYesNo guiYesNo8 = new GuiYesNo(this, string4, string5, string6, string7, this.selectedServer);
					this.mc.displayGuiScreen(guiYesNo8);
				}
			} else if(guiButton1.id == 1) {
				this.joinServer(this.selectedServer);
			} else if(guiButton1.id == 4) {
				this.directClicked = true;
				this.mc.displayGuiScreen(new GuiScreenServerList(this, this.tempServer = new ServerNBTStorage(Translator.translateToLocal("selectServer.defaultName"), "")));
			} else if(guiButton1.id == 3) {
				this.addClicked = true;
				this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.tempServer = new ServerNBTStorage(Translator.translateToLocal("selectServer.defaultName"), "")));
			} else if(guiButton1.id == 7) {
				this.editClicked = true;
				ServerNBTStorage serverNBTStorage9 = (ServerNBTStorage)this.serverList.get(this.selectedServer);
				this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.tempServer = new ServerNBTStorage(serverNBTStorage9.name, serverNBTStorage9.host)));
			} else if(guiButton1.id == 0) {
				this.mc.displayGuiScreen(this.parentScreen);
			} else if(guiButton1.id == 8) {
				this.mc.displayGuiScreen(new GuiMultiplayer(this.parentScreen));
			} else {
				this.serverSlotContainer.actionPerformed(guiButton1);
			}

		}
	}

	public void confirmClicked(boolean z1, int i2) {
		if(this.deleteClicked) {
			this.deleteClicked = false;
			if(z1) {
				this.serverList.remove(i2);
				this.saveServerList();
			}

			this.mc.displayGuiScreen(this);
		} else if(this.directClicked) {
			this.directClicked = false;
			if(z1) {
				this.joinServer(this.tempServer);
			} else {
				this.mc.displayGuiScreen(this);
			}
		} else if(this.addClicked) {
			this.addClicked = false;
			if(z1) {
				this.serverList.add(this.tempServer);
				this.saveServerList();
			}

			this.mc.displayGuiScreen(this);
		} else if(this.editClicked) {
			this.editClicked = false;
			if(z1) {
				ServerNBTStorage serverNBTStorage3 = (ServerNBTStorage)this.serverList.get(this.selectedServer);
				serverNBTStorage3.name = this.tempServer.name;
				serverNBTStorage3.host = this.tempServer.host;
				this.saveServerList();
			}

			this.mc.displayGuiScreen(this);
		}

	}

	private int parseIntWithDefault(String string1, int i2) {
		try {
			return Integer.parseInt(string1.trim());
		} catch (Exception exception4) {
			return i2;
		}
	}

	protected void keyTyped(char c1, int i2) {
		if(c1 == 13) {
			this.actionPerformed((GuiButton)this.controlList.get(2));
		}

	}

	protected void mouseClicked(int i1, int i2, int i3) {
		super.mouseClicked(i1, i2, i3);
	}

	public void drawScreen(int i1, int i2, float f3) {
		this.lagTooltip = null;
		StringTranslate stringTranslate4 = StringTranslate.getInstance();
		this.drawDefaultBackground();
		this.serverSlotContainer.drawScreen(i1, i2, f3);
		this.drawCenteredString(this.fontRenderer, stringTranslate4.translateKey("multiplayer.title"), this.width / 2, 20, 0xFFFFFF);
		super.drawScreen(i1, i2, f3);
		if(this.lagTooltip != null) {
			this.func_35325_a(this.lagTooltip, i1, i2);
		}

	}

	private void joinServer(int i1) {
		this.joinServer((ServerNBTStorage)this.serverList.get(i1));
	}

	private void joinServer(ServerNBTStorage serverNBTStorage1) {
		String string2 = serverNBTStorage1.host;
		String[] string3 = string2.split(":");
		if(string2.startsWith("[")) {
			int i4 = string2.indexOf("]");
			if(i4 > 0) {
				String string5 = string2.substring(1, i4);
				String string6 = string2.substring(i4 + 1).trim();
				if(string6.startsWith(":") && string6.length() > 0) {
					string6 = string6.substring(1);
					string3 = new String[]{string5, string6};
				} else {
					string3 = new String[]{string5};
				}
			}
		}

		if(string3.length > 2) {
			string3 = new String[]{string2};
		}

		this.mc.displayGuiScreen(new GuiConnecting(this.mc, string3[0], string3.length > 1 ? this.parseIntWithDefault(string3[1], 25565) : 25565));
	}

	private void pollServer(ServerNBTStorage serverNBTStorage1) throws IOException {
		String string2 = serverNBTStorage1.host;
		String[] string3 = string2.split(":");
		if(string2.startsWith("[")) {
			int i4 = string2.indexOf("]");
			if(i4 > 0) {
				String string5 = string2.substring(1, i4);
				String string6 = string2.substring(i4 + 1).trim();
				if(string6.startsWith(":") && string6.length() > 0) {
					string6 = string6.substring(1);
					string3 = new String[]{string5, string6};
				} else {
					string3 = new String[]{string5};
				}
			}
		}

		if(string3.length > 2) {
			string3 = new String[]{string2};
		}

		String string29 = string3[0];
		int i30 = string3.length > 1 ? this.parseIntWithDefault(string3[1], 25565) : 25565;
		Socket socket31 = null;
		DataInputStream dataInputStream7 = null;
		DataOutputStream dataOutputStream8 = null;

		try {
			socket31 = new Socket();
			socket31.setSoTimeout(3000);
			socket31.setTcpNoDelay(true);
			socket31.setTrafficClass(18);
			socket31.connect(new InetSocketAddress(string29, i30), 3000);
			dataInputStream7 = new DataInputStream(socket31.getInputStream());
			dataOutputStream8 = new DataOutputStream(socket31.getOutputStream());
			dataOutputStream8.write(254);
			if(dataInputStream7.read() != 255) {
				throw new IOException("Bad message");
			}

			String string9 = Packet.readString(dataInputStream7, 256);
			char[] c10 = string9.toCharArray();

			int i11;
			for(i11 = 0; i11 < c10.length; ++i11) {
				if(c10[i11] != 167 && ChatAllowedCharacters.allowedCharacters.indexOf(c10[i11]) < 0) {
					c10[i11] = 63;
				}
			}

			string9 = new String(c10);
			string3 = string9.split("\u00a7");
			string9 = string3[0];
			i11 = -1;
			int i12 = -1;

			try {
				i11 = Integer.parseInt(string3[1]);
				i12 = Integer.parseInt(string3[2]);
			} catch (Exception exception27) {
			}

			serverNBTStorage1.motd = "\u00a77" + string9;
			if(i11 >= 0 && i12 > 0) {
				serverNBTStorage1.playerCount = "\u00a77" + i11 + "\u00a78/\u00a77" + i12;
			} else {
				serverNBTStorage1.playerCount = "\u00a78???";
			}
		} finally {
			try {
				if(dataInputStream7 != null) {
					dataInputStream7.close();
				}
			} catch (Throwable throwable26) {
			}

			try {
				if(dataOutputStream8 != null) {
					dataOutputStream8.close();
				}
			} catch (Throwable throwable25) {
			}

			try {
				if(socket31 != null) {
					socket31.close();
				}
			} catch (Throwable throwable24) {
			}

		}

	}

	protected void func_35325_a(String string1, int i2, int i3) {
		if(string1 != null) {
			int i4 = i2 + 12;
			int i5 = i3 - 12;
			int i6 = this.fontRenderer.getStringWidth(string1);
			this.drawGradientRect(i4 - 3, i5 - 3, i4 + i6 + 3, i5 + 8 + 3, -1073741824, -1073741824);
			this.fontRenderer.drawStringWithShadow(string1, i4, i5, -1);
		}
	}

	static List<ServerNBTStorage> getServerList(GuiMultiplayer guiMultiplayer0) {
		return guiMultiplayer0.serverList;
	}

	static int setSelectedServer(GuiMultiplayer guiMultiplayer0, int i1) {
		return guiMultiplayer0.selectedServer = i1;
	}

	static int getSelectedServer(GuiMultiplayer guiMultiplayer0) {
		return guiMultiplayer0.selectedServer;
	}

	static GuiButton getButtonSelect(GuiMultiplayer guiMultiplayer0) {
		return guiMultiplayer0.buttonSelect;
	}

	static GuiButton getButtonEdit(GuiMultiplayer guiMultiplayer0) {
		return guiMultiplayer0.buttonEdit;
	}

	static GuiButton getButtonDelete(GuiMultiplayer guiMultiplayer0) {
		return guiMultiplayer0.buttonDelete;
	}

	static void joinServer(GuiMultiplayer guiMultiplayer0, int i1) {
		guiMultiplayer0.joinServer(i1);
	}

	public static Object getLock() {
		return lock;
	}

	static int getThreadsPending() {
		return threadsPending;
	}

	static int incrementThreadsPending() {
		return threadsPending++;
	}

	public static void pollServer(GuiMultiplayer guiMultiplayer0, ServerNBTStorage serverNBTStorage1) throws IOException {
		guiMultiplayer0.pollServer(serverNBTStorage1);
	}

	public static int decrementThreadsPending() {
		return threadsPending--;
	}

	static String setTooltipText(GuiMultiplayer guiMultiplayer0, String string1) {
		return guiMultiplayer0.lagTooltip = string1;
	}
}
