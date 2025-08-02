# What's new ! a1.1.2 -> b1.6.6

# a1.2

## Blocks

* netherrack
* soul sand
* glowstone + recipes
* pumpkin
* jack'o'lantern + recipes
* nether portal

## Items

* clock + recipes
* raw / cooked fish

## Mobs

* Ghast
* Pigman
* Zombie pigmen

## THE FUCKING NETHER

* But only client-side

## Before you go

* Fix textures for iron, gold, diamond blocks.
* Fix GUI textures i.e. ping for network etc.

# b1.0

## THE FUCKING NETHER

* This time, server side! 
	* [X] I'm lighting the portal, but I can't see portal blocks in the portal.
		Problem was advanced OpenGL ! I'm on mondernest Intel. Check elsewhere.
	* [X] Crash on teleport
		* Player was moved 0->-1, but no portal was created or maybe x/z were not changed.
		* Fails reading data in  `Packet9Respawn`:
```
java.io.IOException: Received string length longer than maximum allowed (17 > 16)
	at net.minecraft.network.packet.Packet.readString(Packet.java:118)
	at net.minecraft.network.packet.Packet9Respawn.readPacketData(Packet9Respawn.java:37)
	at net.minecraft.network.packet.Packet.readPacket(Packet.java:85)
	at net.minecraft.network.NetworkManager.readPacket(NetworkManager.java:146)
	at net.minecraft.network.NetworkManager.readNetworkPacket(NetworkManager.java:261)
	at net.minecraft.network.NetworkReaderThread.run(NetworkReaderThread.java:32)
```

	* Problem is in the string telling the world type, expected 16 bytes, got 17 bytes.
	* Problem was that for some reason worldtype.toString included "WorldType " prepended.

	* [X] When being teleported player is not being placed correctly in the portal.
	Check if this was just a glitch or if it happens always. - There's defintely something terribly wrong with the teleporter or the location you are moved to. -- Solved. The teleporter creation code was commented out in `ServerConfigurationManager.java`
	* [X] Restore the special cactus renderer.
	* [ ] Restore the pane renderer.
	* [ ] Correct lilypad texture!

# b1.2

## New blocks / recipes

* Cake block, cake item, cake recipe.
* Dispensers, GUI, TileEntity, recipe.
* Lapis lazuli block.
* Lapis lazuli ore drops lapis lazuli item.
* Note block,  TileEntity, recipe.
* Sandstone recipe & gen.
* Spruce / Birch trunks & recipes to get planks.
* Spruce / Birch leaves.
* Wool dying
* Bones -> Bone meal & making shit grow.

# b16.6.6

* Add a means to get cocoa seeds in a custom tree.
* Colored sheep spawning.
* Squids