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
* [X] Correct lilypad texture!

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

# b1.3

* [X] This version adds beds and the sleeping functionality. I removed all this for the base version and now I'll have to add it again :D Sowly but steady!
	* [X] Sleeping in SP works.
	* [X] Sleeping in SMP works.
		* Bug! Crash when trying to translate the "can't sleep" message in the server. How is this even handled in vanilla?
	* [X] Nightmare dynamic.

# b1.4 

* Wolves are already in

# b1.5

* [ ] Statistics & achievements - which took forever to remove :D

# b1.6

* [X] Maps!
	* [X] Working on the client, crash on SMP.
	* [X] No longer crashes but no map data on the map.
* [X] Trapdoors
* [X] Dead bush, tall grass & ferns.
* [X] Make sure beta arrows behave the same way as the arrows I have !!


# Bugs / glitches
	* [X] Beds (and possible other stuff) look bad when re-login into SMP, possibly metadata related!
	* [X] Restore the pane renderer.
	* [X] Sleeping in SMP doesn't advance the day IT DOES BUT ...
		* What was happenning is that I was having a nightmare but ENTITIES ARE NOT SHOWN IN TEH CLIENT. I did break something very nasty. Absolutely no entity works. I have to check when this stopped working.
			* NOPE! This is working... It's just that entities don't render if I turn Advanced OpenGL on!? this is really odd... 
			* But it's not right yet
		Real problem is: 
		* day/night cycle is stuck, but apparently it's daytime as "You can only sleep at night" is shown.

		`isDayTime` is `this.skylightSubtracted < 4`. Skylightsubtracted has to do with the celestial angle, which has to do with worldTime. So worldTime is getting advanced right? But when I log out and then log back in it's still dark.

		When I logged out and back in, the server console sang `sending time: 24316` which is right (316 ticks) after a reset.

		WAIT WHAT

		I keep logging the ticks but when I sleep I get to reads... That should be the two worlds out of sync. And somehow the wrong time gets to the client. Or BOTH.

		Or four of them:

		net.minecraft.world.level.dimension.WorldProviderHell@63fa2a1f 15068
		net.minecraft.world.level.dimension.WorldProviderSurfaceClassic@5b78b878 15068
		net.minecraft.world.level.dimension.WorldProviderSurfaceClassic@688ad29 15068
		net.minecraft.world.level.dimension.WorldProviderSurfaceClassic@667d0971 15068

		1st of all I have to make this only run the active worlds.
		2nd of all I have to check what's wrong with this and how this works in vanilla.

		K - problem was two uncared for overworlds sending updates. Removed this. Still thinking that I should encapsulate all dimension related stuff.
	* [X] Advanced OpenGL -> entities don't show?!
		Superstripped also has this problem. Turning it on makes pass two and entities to disappear. I have to check if MP125 has this problem. This has to be a badly integrated Optifine issue.

		* Advanced OpenGL DOES work in MP125, so I have something to compare shit to. Class affected by teh setting is of course LevelRenderer (which was RenderGlobal in vanilla). Removing anaglyph I had removed a `GL11.glColorMask(true, true, true, true);` too much... YAAY

	* [X] Biomes shown in SMP not correct, also not correct overall (particle decide).
		* GUI gets biomes from `world.getBiomeGenForCoords`...
		* particleDecide gets biomes from the biomegen it gets, which is...
			* Render: `world.getBiomeGenForCoords`.
			* World: `world.getBiomeGenForCoords`...

		So, `world.getBiomeGenForCoords` calls 
			* `Chunk.getBiomeForCoords`, if chunk exists or
			* `worldProvider.worldChunkMgr.getBiomeGenAt` if it doesn't.

		In SMP, meh it seems just right, maybe a problem with the biome configuration (the particle decide).

	* [X] Are seasons OFF in the server?
	* [/] seeds not consistent? revise beta gen -- almost.
	* [X] FLAT is not generating! (freezes?)

* and now we are ready for...

# b16.6.6 r1

* [X] Expand atlases to 256x512.
* [X] Add a means to get cocoa seeds in a custom tree.
	* Cocoa tree = normal tree with hanging cocoa pod blocks.
		* Added a `BlockTreeFruit` which will contain all fruits that hang from trees. 
			* meta = 0 -> Cocoa pods.
		* Added a `withFruit(id)` to add fruit to normal trees.
* [X] Colored sheep spawning.
* [X] Hippoplatimus pistons

# b16.6.6 r1 - More animals!

* [X] Boars
* [ ] Ogre!
	* I've always liked finding ogres in my old worlds but I'm wondering how they appear... I guess totally random. I'd spawn them in swamps very rarely maybe. 
	* I don't think I'll be using the actual code. I'd rather have ogres be like my amazons, i.e. passive but can get angry.
* [ ] Werefolf
* [ ] Wraith
* [ ] goats
* [ ] cold cow
* [ ] black duck.

# b16.6.6 r2

* [ ] Goblins
	[ ] Add new blocks and items.
	[ ] Check the villages out. How are they generated? Could they be rewritten as normal features (i.e. are they less than 31x31 and fit in 2x2 chunks?) or, should they be rewritten in my own feature system?
	[ ] Add Entities.

# b16.6.6 r3

* [ ] Nethercraft

# b16.6.6 r4

* [ ] IC1?


## Diary

#### 20250806 

* Added ILeaves interface to easily detect leaves.
* Added IGetNameBasedOnMeta to be able to easily name blocks with different metadata representing different things.
* Added BlockTreeFruit which will contain all types of hanging fruit from trees. Meta 0 is cocoa pods which will drop cocoa beans.
* Added the ability for normal vanilla trees to receive a Blockstate and a chance so they can grow hanging fruit (only on generation).

#### 20250807

* Added all the classes for the pistons mod. Does sand needs modifications? I can't seem to propel it upwards. Need to change how orientation is decided when placing. EntityFallingSand in beta was different -> a tiny `else` breaks compatibility. I guess the else is to get some oomph if Sand is just supposed to be falling. I've commented it but I better softlock it using a new `GameRule`. So pistons are working
* Pistons work in SMP, but the animation is of course not shown in teh client. Have to think about how I should do this.

#### 20250808

* I've been struggling for a while to make animations server->client. I'm trying to do this via the entity tracker. Problem is, extending or retracting pistons do create & spawn entities in the world to make the animation. That fires up the tracker. `EntityMovingPiston` `onUpdate` method moves the piston using its xmove, ymove, zmove from the position it was created at. I'm using this as a second alternative: i'll store x, y, z and xmove, ymove, zmove in the packet and will try to recreate the entity client-side just like that. And THAT DID IT! YAY! This was hard.

#### 20250809

* Spent too much time figuring out how to produce working jars for betacraft and MultiMC, and then it took too much time making SuperFlat to work... Even though the problem was REALLY STUPID.

#### 20250810 

* Added boars and ogres from Dr.Zhark's Mo'Creatures.

# Credits

* Chocolate Mod (just the inspiration for now, sorry) by X8xScoutx8X
* Pistons Mod by Hyppoplatimus.
* Some animals by Dr.Zark.

# To transfer to sister projects ...

* [ ] EntityArrow arrangements for classic skeletons (EntityClassicSkeleton, EntityArrow)
* [ ] RailDetector is needed for circuits (Block, BlockDetectorRail, CraftingManager, ItemPickaxe)
* [ ] Move trees to .trees 
* [ ] ItemMetadata, names & stuff - wood type names in BlockLog, IGetNameBasedOnMeta, ILeaves, LeavesBase.