package net.minecraft.world.level.tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.world.Facing;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AxisAlignedBB;

public class BlockStep extends Block {
	public String[] blockStepTypes = new String[]{"stone", "sand", "wood", "cobble", "brick", "smoothStoneBrick", "netherBrick", "quartz"};
	protected boolean blockIsDouble;

	public BlockStep(int i1, boolean z2) {
		super(i1, 6, Material.rock);
		this.blockIsDouble = z2;
		if(!z2) {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
		} else {
			opaqueCubeLookup[i1] = true;
		}

		this.setLightOpacity(255);
		this.displayOnCreativeTab = CreativeTabs.tabBlock;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iBlockAccess1, int i2, int i3, int i4) {
		if(this.blockIsDouble) {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		} else {
			if(BlockStep.isUpper(iBlockAccess1.getBlockMetadata(i2, i3, i4))) {
				this.setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
			} else {
				this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
			}
		}

	}

	@Override
	public void setBlockBoundsForItemRender() {
		if(this.blockIsDouble) {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		} else {
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
		}

	}

	@Override
	public void getCollidingBoundingBoxes(World world1, int i2, int i3, int i4, AxisAlignedBB axisAlignedBB5, ArrayList<AxisAlignedBB> arrayList6) {
		this.setBlockBoundsBasedOnState(world1, i2, i3, i4);
		super.getCollidingBoundingBoxes(world1, i2, i3, i4, axisAlignedBB5, arrayList6);
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int i1, int i2) {
		int i3 = i2 & 7;
		
		//return i3 == 0 ? (i1 <= 1 ? 6 : 5) : (i3 == 1 ? (i1 == 0 ? 208 : (i1 == 1 ? 176 : 192)) : (i3 == 2 ? 4 : (i3 == 3 ? 16 : (i3 == 4 ? Block.brick.blockIndexInTexture : (i3 == 5 ? Block.stoneBrick.blockIndexInTexture : 6)))));
		switch(i3) {
		case 0: return i1 <= 1 ? 6 : 5;
		case 1: return i1 == 0 ? 208 : (i1 == 1 ? 176 : 192);
		case 2: return 4;
		case 3: return 16;
		case 4: return Block.brick.blockIndexInTexture;
		default: return 6;
		}
	}

	@Override
	public int getBlockTextureFromSide(int i1) {
		return this.getBlockTextureFromSideAndMetadata(i1, 0);
	}

	@Override
	public boolean isOpaqueCube() {
		return this.blockIsDouble;
	}

	@Override
	public void onBlockPlaced(World world, int x, int y, int z, int side, float xWithinSide, float yWithinSide, float zWithinSide, boolean keyPressed)	{
		if (this.blockIsDouble) return;
		if (side == 0 || (side != 1 && yWithinSide > 0.5F && !keyPressed)) {
			int i = world.getBlockMetadata(x, y, z) & 7;
			world.setBlockMetadataWithNotify(x, y, z, i | 8);
		}
	}
	
	@Override
	public int idDropped(int i1, Random random2, int i3) {
		return Block.stairSingle.blockID;
	}

	@Override
	public int quantityDropped(Random random1) {
		return this.blockIsDouble ? 2 : 1;
	}

	@Override
	public int damageDropped(int i1) {
		return i1 & 7;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return this.blockIsDouble;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		if(this.blockIsDouble) {
			super.shouldSideBeRendered(world, x, y, z, side);
		}

		if(side != 1 && side != 0 && !super.shouldSideBeRendered(world, x, y, z, side)) {
			return false;
		} else {
			int xx = x + Facing.offsetsXForSide[Facing.faceToSide[side]];
			int yy = y + Facing.offsetsYForSide[Facing.faceToSide[side]];
			int zz = z + Facing.offsetsZForSide[Facing.faceToSide[side]];

			
			return !BlockStep.isUpper(world.getBlockMetadata(xx, yy, zz)) ? 
				(side == 1 ? 
						true 
					: 
						(side == 0 && super.shouldSideBeRendered(world, x, y, z, side) ? 
								true 
							: 
								world.getBlockId(x, y, z) != this.blockID || BlockStep.isUpper(world.getBlockMetadata(x, y, z))
						)
				) 
			: 
				(side == 0 ? 
						true 
					: 
						(side == 1 && super.shouldSideBeRendered(world, x, y, z, side) ? 
								true 
							: 
								world.getBlockId(x, y, z) != this.blockID || !BlockStep.isUpper(world.getBlockMetadata(x, y, z))
						)
				);
		}
	}

	@Override
	protected ItemStack createStackedBlock(int i1) {
		return new ItemStack(Block.stairSingle.blockID, 1, i1 & 7);
	}
	
	@Override
	public Material getBlockMaterialBasedOnmetaData(int meta) {
		switch(meta & 7) {
			case 2: 
				return Material.wood;
			default:
				return Material.rock;
		}		
	}
	
	@Override
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
		if(par1 != Block.stairDouble.blockID) {
			for(int i = 0; i < 5; i ++) {	
				par3List.add(new ItemStack(par1, 1, i));
			}
		}
	}
	
	public static boolean isUpper(int meta) {
		return (meta & 8) != 0;
	}
	
	@Override
	public boolean hasSolidTop(int meta) {
		return BlockStep.isUpper(meta);
	}
	
	@Override
	public boolean supportsTorch(int meta) {
		return this.blockIsDouble || BlockStep.isUpper(meta);
	}
	
	@Override
	public boolean canPlaceTorchBy(int meta, int side) {
		return this.blockIsDouble;
	}
}
