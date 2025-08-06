package net.minecraft.client;

public class GameSettingsKeys {
	public static KeyBinding keyBindForward = new KeyBinding("key.forward", 17);
	public static KeyBinding keyBindLeft = new KeyBinding("key.left", 30);
	public static KeyBinding keyBindBack = new KeyBinding("key.back", 31);
	public static KeyBinding keyBindRight = new KeyBinding("key.right", 32);
	public static KeyBinding keyBindJump = new KeyBinding("key.jump", 57);
	public static KeyBinding keyBindInventory = new KeyBinding("key.inventory", 18);
	public static KeyBinding keyBindDrop = new KeyBinding("key.drop", 16);
	public static KeyBinding keyBindChat = new KeyBinding("key.chat", 20);
	public static KeyBinding keyBindSneak = new KeyBinding("key.sneak", 42);
	public static KeyBinding keyBindAttack = new KeyBinding("key.attack", -100);
	public static KeyBinding keyBindUseItem = new KeyBinding("key.use", -99);
	public static KeyBinding keyBindPlayerList = new KeyBinding("key.playerlist", 15);
	public static KeyBinding keyBindPickBlock = new KeyBinding("key.pickItem", -98);
	public static KeyBinding keyBindMapMenu = new KeyBinding("key.mapMenu", 50);
	public static KeyBinding keyBindMapZoom = new KeyBinding("key.mapZoom", 44);
	public static KeyBinding[] keyBindings = new KeyBinding[]{
			GameSettingsKeys.keyBindAttack, GameSettingsKeys.keyBindUseItem, 
			GameSettingsKeys.keyBindForward, GameSettingsKeys.keyBindLeft, 
			GameSettingsKeys.keyBindBack, GameSettingsKeys.keyBindRight, 
			GameSettingsKeys.keyBindJump, GameSettingsKeys.keyBindSneak, 
			GameSettingsKeys.keyBindDrop, GameSettingsKeys.keyBindInventory, 
			GameSettingsKeys.keyBindPickBlock, GameSettingsKeys.keyBindChat, 
			GameSettingsKeys.keyBindMapMenu, GameSettingsKeys.keyBindMapZoom, 
			GameSettingsKeys.keyBindPlayerList
		};
}
