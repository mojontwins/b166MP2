package net.minecraft.world.level.levelgen.feature.trees;

import java.util.Random;

import net.minecraft.world.level.BlockState;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.BlockLeaves;
import net.minecraft.world.level.tile.BlockLeavesEx1;
import net.minecraft.world.level.tile.BlockLeavesEx2;
import net.minecraft.world.level.tile.BlockLog;
import net.minecraft.world.level.tile.BlockLogEx1;
import net.minecraft.world.level.tile.BlockLogEx2;

public enum EnumTreeType {
	OAK("Oak", new BlockState(Block.leaves, BlockLeaves.OakMetadata), new BlockState(Block.wood, BlockLog.OakMetadata), new BlockState(Block.sapling, BlockLeaves.OakMetadata)) {
		@Override
		public WorldGenerator getGen(Random rand) {
			return rand.nextInt(8) == 0 ? new WorldGenBigTree(true) : new WorldGenTrees(true);
		}
	},
	
	BIRCH("Birch", new BlockState(Block.leaves, BlockLeaves.BirchMetadata), new BlockState(Block.wood, BlockLog.BirchMetadata), new BlockState(Block.sapling, BlockLeaves.BirchMetadata)) {
		@Override
		public WorldGenerator getGen(Random rand) {
			return new WorldGenForest(true);
		}
	},
	
	TAIGA("Taiga", new BlockState(Block.leaves, BlockLeaves.SpruceMetadata), new BlockState(Block.wood, BlockLog.SpruceMetadata), new BlockState(Block.sapling, BlockLeaves.SpruceMetadata)) {
		@Override
		public WorldGenerator getGen(Random rand) {
			return rand.nextBoolean() ? new WorldGenTaiga1() : new WorldGenTaiga2();
		}
	},
	
	HUGE("Huge", new BlockState(Block.leaves, BlockLeaves.JungleMetadata), new BlockState(Block.wood, BlockLog.JungleMetadata), new BlockState(Block.sapling, BlockLeaves.JungleMetadata), true) {
		@Override
		public WorldGenerator getGen(Random rand) {
			return new WorldGenHugeTrees(true, 16 + rand.nextInt(16));
		}
	},
	
	SWAMP("Swamp", new BlockState(Block.leavesEx1, BlockLeavesEx1.SwampMetadata), new BlockState(Block.woodEx1, BlockLogEx1.SwampMetadata), new BlockState(Block.saplingEx1, BlockLeavesEx1.SwampMetadata)) {
		@Override
		public WorldGenerator getGen(Random rand) {
			return new WorldGenSwamp();
		}
	},
	
	BAOBAB("Baobab", new BlockState(Block.leavesEx1, BlockLeavesEx1.BaobabMetadata), new BlockState(Block.woodEx1, BlockLogEx1.BaobabMetadata), new BlockState(Block.saplingEx1, BlockLeavesEx1.BaobabMetadata)) {
		@Override
		public WorldGenerator getGen(Random rand) {
			return rand.nextInt(10) == 0 ? new WorldGenBaobab(6 + rand.nextInt(7)) : new WorldGenBaobab(2 + rand.nextInt(3));
		}
	},
	
	PINE("Pine", new BlockState(Block.leavesEx1, BlockLeavesEx1.PineMetadata), new BlockState(Block.woodEx1, BlockLogEx1.PineMetadata), new BlockState(Block.saplingEx1, BlockLeavesEx1.PineMetadata), true) {
		@Override
		public WorldGenerator getGen(Random rand) {
			return new WorldGenPineTree(6 + rand.nextInt(8), false);
		}
	},
	
	ACACIA("Acacia", new BlockState(Block.leavesEx1, BlockLeavesEx1.AcaciaMetadata), new BlockState(Block.woodEx1, BlockLogEx1.AcaciaMetadata), new BlockState(Block.saplingEx1, BlockLeavesEx1.AcaciaMetadata)) {
		@Override
		public WorldGenerator getGen(Random rand) {
			return new WorldGenAcacia(true);
		}
	},
	
	ALDER("Alder", new BlockState(Block.leavesEx2, BlockLeavesEx2.AlderMetadata), new BlockState(Block.woodEx2, BlockLogEx2.AlderMetadata), new BlockState(Block.saplingEx2, BlockLeavesEx2.AlderMetadata)) {
		@Override
		public WorldGenerator getGen(Random rand) {
			return new WorldGenAlder(6 + rand.nextInt(3), 5, 4);
		}
	},
	
	ASPEN("Aspen", new BlockState(Block.leavesEx2, BlockLeavesEx2.AspenMetadata), new BlockState(Block.woodEx2, BlockLogEx2.AspenMetadata), new BlockState(Block.saplingEx2, BlockLeavesEx2.AspenMetadata)) {
		@Override
		public WorldGenerator getGen(Random rand) {
			return new WorldGenAspen(8 + rand.nextInt(3));
		}
	},
	
	EUCALYPTUS("Eucalyptus", new BlockState(Block.leavesEx2, BlockLeavesEx2.EucalyptusMetadata), new BlockState(Block.woodEx2, BlockLogEx2.EucalyptusMetadata), new BlockState(Block.saplingEx2, BlockLeavesEx2.EucalyptusMetadata), true) {
		@Override
		public WorldGenerator getGen(Random rand) {
			return new WorldGenEucalyptusBig(true);
		}
	},
	
	CHERRYTREE("CherryTree", new BlockState(Block.leavesEx2, BlockLeavesEx2.CherryTreeMetadata), new BlockState(Block.woodEx2, BlockLogEx2.CherryTreeMetadata), new BlockState(Block.saplingEx2, BlockLeavesEx2.CherryTreeMetadata)) {
		@Override
		public WorldGenerator getGen(Random rand) {
			return new WorldGenBigTree(true, this);
		}
	},
	;
	
	public final BlockState leaves;
	public final BlockState wood;
	public final BlockState sapling;
	
	public final String name;
	
	public final boolean needsFourSaplings;
	
	public WorldGenerator getGen(Random rand) {
		return new WorldGenTrees(false);
	}
	
	public static BlockState getSaplingFromLeaves(BlockState leaves) {
		return new BlockState(Block.sapling, leaves.getMetadata());
	}
	
	public static EnumTreeType findTreeTypeFromLeaves(BlockState leaves) {
		for(EnumTreeType e : EnumTreeType.values()) {
			if(leaves.equals(e.leaves)) {
				return e;
			}
		}
		
		return OAK;
	}
	
	public static EnumTreeType findTreeTypeFromSapling(BlockState sapling) {
		for(EnumTreeType e : EnumTreeType.values()) {
			if(sapling.equals(e.sapling)) {
				return e;
			}
		}
		
		return OAK;
	}
	
	public static EnumTreeType findTreeTypeFromWood(BlockState wood) {
		for(EnumTreeType e : EnumTreeType.values()) {
			if(wood.equals(e.wood)) {
				return e;
			}
		}
		
		return OAK;
	}
	
	EnumTreeType(String name, BlockState leaves, BlockState wood, BlockState sapling) {
		this(name, leaves, wood, sapling, false);
	}
	
	EnumTreeType(String name, BlockState leaves, BlockState wood, BlockState sapling, boolean needsFourSaplings) {
		this.name = name;
		this.leaves = leaves;
		this.wood = wood;
		this.sapling = sapling;
		this.needsFourSaplings = needsFourSaplings;
	}
	
}
