package net.minecraft.server.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import net.minecraft.server.ICommandListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.player.PlayerListBox;

public class ServerGUI extends JComponent implements ICommandListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6692422902172041230L;
	public static Logger logger = Logger.getLogger("Minecraft");
	private MinecraftServer mcServer;

	public static void initGui(MinecraftServer minecraftServer0) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception exception3) {
		}

		ServerGUI serverGUI1 = new ServerGUI(minecraftServer0);
		JFrame jFrame2 = new JFrame("Minecraft server");
		jFrame2.add(serverGUI1);
		jFrame2.pack();
		jFrame2.setLocationRelativeTo((Component)null);
		jFrame2.setVisible(true);
		jFrame2.addWindowListener(new ServerWindowAdapter(minecraftServer0));
	}

	public ServerGUI(MinecraftServer minecraftServer1) {
		this.mcServer = minecraftServer1;
		this.setPreferredSize(new Dimension(854, 480));
		this.setLayout(new BorderLayout());

		try {
			this.add(this.getLogComponent(), "Center");
			this.add(this.getStatsComponent(), "West");
		} catch (Exception exception3) {
			exception3.printStackTrace();
		}

	}

	private JComponent getStatsComponent() {
		JPanel jPanel1 = new JPanel(new BorderLayout());
		jPanel1.add(new GuiStatsComponent(this.mcServer), "North");
		jPanel1.add(this.getPlayerListComponent(), "Center");
		jPanel1.setBorder(new TitledBorder(new EtchedBorder(), "Stats"));
		return jPanel1;
	}

	private JComponent getPlayerListComponent() {
		PlayerListBox playerListBox1 = new PlayerListBox(this.mcServer);
		JScrollPane jScrollPane2 = new JScrollPane(playerListBox1, 22, 30);
		jScrollPane2.setBorder(new TitledBorder(new EtchedBorder(), "Players"));
		return jScrollPane2;
	}

	private JComponent getLogComponent() {
		JPanel jPanel1 = new JPanel(new BorderLayout());
		JTextArea jTextArea2 = new JTextArea();
		logger.addHandler(new GuiLogOutputHandler(jTextArea2));
		JScrollPane jScrollPane3 = new JScrollPane(jTextArea2, 22, 30);
		jTextArea2.setEditable(false);
		JTextField JTextField4 = new JTextField();
		JTextField4.addActionListener(new ServerGuiCommandListener(this, JTextField4));
		jTextArea2.addFocusListener(new ServerGuiFocusAdapter(this));
		jPanel1.add(jScrollPane3, "Center");
		jPanel1.add(JTextField4, "South");
		jPanel1.setBorder(new TitledBorder(new EtchedBorder(), "Log and chat"));
		return jPanel1;
	}

	public void log(String string1) {
		logger.info(string1);
	}

	public String getUsername() {
		return "CONSOLE";
	}

	static MinecraftServer getMinecraftServer(ServerGUI serverGUI0) {
		return serverGUI0.mcServer;
	}
}
