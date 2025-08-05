package net.minecraft.world.item.map;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet131MapData;
import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.tile.Block;

public class ItemMap extends ItemMapBase {
	public ItemMap(int i1) {
		super(i1);
		this.setMaxStackSize(1);
		
		this.displayOnCreativeTab = CreativeTabs.tabMisc;
	}

	public static MapData getMPMapData(short s0, World world1) {
		(new StringBuilder()).append("map_").append(s0).toString();
		MapData mapData3 = (MapData)world1.loadItemData(MapData.class, "map_" + s0);
		if(mapData3 == null) {
			int i4 = world1.getUniqueDataId("map");
			String string2 = "map_" + i4;
			mapData3 = new MapData(string2);
			world1.setItemData(string2, mapData3);
		}

		return mapData3;
	}

	public MapData getMapData(ItemStack itemStack1, World world2) {
		(new StringBuilder()).append("map_").append(itemStack1.getItemDamage()).toString();
		MapData mapData4 = (MapData)world2.loadItemData(MapData.class, "map_" + itemStack1.getItemDamage());
		if(mapData4 == null) {
			itemStack1.setItemDamage(world2.getUniqueDataId("map"));
			String string3 = "map_" + itemStack1.getItemDamage();
			mapData4 = new MapData(string3);
			mapData4.xCenter = world2.getWorldInfo().getSpawnX();
			mapData4.zCenter = world2.getWorldInfo().getSpawnZ();
			mapData4.scale = 3;
			mapData4.dimension = (byte)world2.worldProvider.worldType;
			mapData4.markDirty();
			world2.setItemData(string3, mapData4);
		}

		return mapData4;
	}

	public void updateMapData(World world1, Entity entity2, MapData mapData3) {
		if(world1.worldProvider.worldType == mapData3.dimension) {
			short s4 = 128;
			short s5 = 128;
			int i6 = 1 << mapData3.scale;
			int i7 = mapData3.xCenter;
			int i8 = mapData3.zCenter;
			int i9 = MathHelper.floor_double(entity2.posX - (double)i7) / i6 + s4 / 2;
			int i10 = MathHelper.floor_double(entity2.posZ - (double)i8) / i6 + s5 / 2;
			int i11 = 128 / i6;
			if(world1.worldProvider.hasNoSky) {
				i11 /= 2;
			}

			++mapData3.field_28175_g;

			for(int i12 = i9 - i11 + 1; i12 < i9 + i11; ++i12) {
				if((i12 & 15) == (mapData3.field_28175_g & 15)) {
					int i13 = 255;
					int i14 = 0;
					double d15 = 0.0D;

					for(int i17 = i10 - i11 - 1; i17 < i10 + i11; ++i17) {
						if(i12 >= 0 && i17 >= -1 && i12 < s4 && i17 < s5) {
							int i18 = i12 - i9;
							int i19 = i17 - i10;
							boolean z20 = i18 * i18 + i19 * i19 > (i11 - 2) * (i11 - 2);
							int i21 = (i7 / i6 + i12 - s4 / 2) * i6;
							int i22 = (i8 / i6 + i17 - s5 / 2) * i6;
							int[] i26 = new int[256];
							Chunk chunk27 = world1.getChunkFromBlockCoords(i21, i22);
							int i28 = i21 & 15;
							int i29 = i22 & 15;
							int i30 = 0;
							double d31 = 0.0D;
							int i33;
							int i34;
							int i35;
							int i38;
							if(world1.worldProvider.hasNoSky) {
								i33 = i21 + i22 * 231871;
								i33 = i33 * i33 * 31287121 + i33 * 11;
								if((i33 >> 20 & 1) == 0) {
									i26[Block.dirt.blockID] += 10;
								} else {
									i26[Block.stone.blockID] += 10;
								}

								d31 = 100.0D;
							} else {
								for(i33 = 0; i33 < i6; ++i33) {
									for(i34 = 0; i34 < i6; ++i34) {
										i35 = chunk27.getHeightValue(i33 + i28, i34 + i29) + 1;
										int i36 = 0;
										if(i35 > 1) {
											boolean z37 = false;

											do {
												z37 = true;
												i36 = chunk27.getBlockID(i33 + i28, i35 - 1, i34 + i29);
												if(i36 == 0) {
													z37 = false;
												} else if(i35 > 0 && i36 > 0 && Block.blocksList[i36].blockMaterial.materialMapColor == MapColor.airColor) {
													z37 = false;
												}

												if(!z37) {
													--i35;
													i36 = chunk27.getBlockID(i33 + i28, i35 - 1, i34 + i29);
												}
											} while(i35 > 0 && !z37);

											if(i35 > 0 && i36 != 0 && Block.blocksList[i36].blockMaterial.isLiquid()) {
												i38 = i35 - 1;
												int i43;
												do {
													i43 = chunk27.getBlockID(i33 + i28, i38--, i34 + i29);
													++i30;
												} while(i38 > 0 && i43 != 0 && Block.blocksList[i43].blockMaterial.isLiquid());
											}
										}

										d31 += (double)i35 / (double)(i6 * i6);
										++i26[i36];
									}
								}
							}

							i30 /= i6 * i6;
							i33 = 0;
							i34 = 0;

							for(i35 = 0; i35 < 256; ++i35) {
								if(i26[i35] > i33) {
									i34 = i35;
									i33 = i26[i35];
								}
							}

							double d41 = (d31 - d15) * 4.0D / (double)(i6 + 4) + ((double)(i12 + i17 & 1) - 0.5D) * 0.4D;
							byte b42 = 1;
							if(d41 > 0.6D) {
								b42 = 2;
							}

							if(d41 < -0.6D) {
								b42 = 0;
							}

							i38 = 0;
							if(i34 > 0) {
								MapColor mapColor44 = Block.blocksList[i34].blockMaterial.materialMapColor;
								if(mapColor44 == MapColor.waterColor) {
									d41 = (double)i30 * 0.1D + (double)(i12 + i17 & 1) * 0.2D;
									b42 = 1;
									if(d41 < 0.5D) {
										b42 = 2;
									}

									if(d41 > 0.9D) {
										b42 = 0;
									}
								}

								i38 = mapColor44.colorIndex;
							}

							d15 = d31;
							if(i17 >= 0 && i18 * i18 + i19 * i19 < i11 * i11 && (!z20 || (i12 + i17 & 1) != 0)) {
								byte b45 = mapData3.colors[i12 + i17 * s4];
								byte b40 = (byte)(i38 * 4 + b42);
								if(b45 != b40) {
									if(i13 > i17) {
										i13 = i17;
									}

									if(i14 < i17) {
										i14 = i17;
									}

									mapData3.colors[i12 + i17 * s4] = b40;
								}
							}
						}
					}

					if(i13 <= i14) {
						mapData3.func_28170_a(i12, i13, i14);
					}
				}
			}

		}
	}

	public void onUpdate(ItemStack itemStack1, World world2, Entity entity3, int i4, boolean z5) {
		if(!world2.isRemote) {
			MapData mapData6 = this.getMapData(itemStack1, world2);
			if(entity3 instanceof EntityPlayer) {
				EntityPlayer entityPlayer7 = (EntityPlayer)entity3;
				mapData6.func_28169_a(entityPlayer7, itemStack1);
			}

			if(z5) {
				this.updateMapData(world2, entity3, mapData6);
			}

		}
	}

	public void onCreated(ItemStack itemStack1, World world2, EntityPlayer entityPlayer3) {
		itemStack1.setItemDamage(world2.getUniqueDataId("map"));
		String string4 = "map_" + itemStack1.getItemDamage();
		MapData mapData5 = new MapData(string4);
		world2.setItemData(string4, mapData5);
		mapData5.xCenter = MathHelper.floor_double(entityPlayer3.posX);
		mapData5.zCenter = MathHelper.floor_double(entityPlayer3.posZ);
		mapData5.scale = 3;
		mapData5.dimension = (byte)world2.worldProvider.worldType;
		mapData5.markDirty();
	}

	public Packet getUpdatePacket(ItemStack theStack, World theWorld, EntityPlayer thePlayer) {
		byte[] b4 = this.getMapData(theStack, theWorld).s_func_28154_a(theStack, theWorld, thePlayer);
		return b4 == null ? null : new Packet131MapData((short)Item.map.shiftedIndex, (short)theStack.getItemDamage(), b4);
	}
}
