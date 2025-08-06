package net.minecraft.world.level.tile;

import net.minecraft.world.GameRules;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AxisAlignedBB;

public class BlockFence extends Block {
	public BlockFence(int i1, int i2) {
		super(i1, i2, Material.wood);
		this.displayOnCreativeTab = CreativeTabs.tabDeco;
	}

	public BlockFence(int i1, int i2, Material material3) {
		super(i1, i2, material3);
		this.displayOnCreativeTab = CreativeTabs.tabDeco;
	}

	public boolean canPlaceBlockAt(World world1, int i2, int i3, int i4) {
		return super.canPlaceBlockAt(world1, i2, i3, i4);
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		if(GameRules.boolRule("oldFences")) return super.getCollisionBoundingBoxFromPool(world, x, y, z);
		
		boolean z5 = this.canConnectFenceTo(world, x, y, z - 1);
		boolean z6 = this.canConnectFenceTo(world, x, y, z + 1);
		boolean z7 = this.canConnectFenceTo(world, x - 1, y, z);
		boolean z8 = this.canConnectFenceTo(world, x + 1, y, z);
		float f9 = 0.375F;
		float f10 = 0.625F;
		float f11 = 0.375F;
		float f12 = 0.625F;
		if(z5) {
			f11 = 0.0F;
		}

		if(z6) {
			f12 = 1.0F;
		}

		if(z7) {
			f9 = 0.0F;
		}

		if(z8) {
			f10 = 1.0F;
		}

		return AxisAlignedBB.getBoundingBoxFromPool((double)((float)x + f9), (double)y, (double)((float)z + f11), (double)((float)x + f10), (double)((float)y + 1.5F), (double)((float)z + f12));
	}

	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		if(GameRules.boolRule("oldFences")) {
			this.setBlockBounds(0, 0, 0, 1, 1, 1);
		} else {
			
			boolean z5 = this.canConnectFenceTo(world, x, y, z - 1);
			boolean z6 = this.canConnectFenceTo(world, x, y, z + 1);
			boolean z7 = this.canConnectFenceTo(world, x - 1, y, z);
			boolean z8 = this.canConnectFenceTo(world, x + 1, y, z);
			float f9 = 0.375F;
			float f10 = 0.625F;
			float f11 = 0.375F;
			float f12 = 0.625F;
			if(z5) {
				f11 = 0.0F;
			}
	
			if(z6) {
				f12 = 1.0F;
			}
	
			if(z7) {
				f9 = 0.0F;
			}
	
			if(z8) {
				f10 = 1.0F;
			}
	
			this.setBlockBounds(f9, 0.0F, f11, f10, 1.0F, f12);
		}
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public boolean getBlocksMovement(IBlockAccess iBlockAccess1, int i2, int i3, int i4) {
		return false;
	}

	public int getRenderType() {
		return 11;
	}

	public boolean canConnectFenceTo(IBlockAccess iBlockAccess1, int x, int y, int z) {
		int blockID = iBlockAccess1.getBlockId(x, y, z);
		if(blockID != this.blockID) {
			if (!GameRules.boolRule("connectFences")) return false;
			Block block = Block.blocksList[blockID];
			return block != null && block.blockMaterial.isOpaque() && block.renderAsNormalBlock() ? block.blockMaterial != Material.pumpkin : false;
		} else {
			return true;
		}
	}
	
	@Override
	public boolean supportsTorch(int meta) {
		return true;
	}
}
