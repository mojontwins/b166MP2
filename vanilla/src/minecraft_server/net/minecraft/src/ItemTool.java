package net.minecraft.src;

public class ItemTool extends Item {
	private Block[] blocksEffectiveAgainst;
	private float efficiencyOnProperMaterial = 4.0F;
	private int damageVsEntity;
	protected EnumToolMaterial toolMaterial;

	protected ItemTool(int i1, int i2, EnumToolMaterial enumToolMaterial3, Block[] block4) {
		super(i1);
		this.toolMaterial = enumToolMaterial3;
		this.blocksEffectiveAgainst = block4;
		this.maxStackSize = 1;
		this.setMaxDamage(enumToolMaterial3.getMaxUses());
		this.efficiencyOnProperMaterial = enumToolMaterial3.getEfficiencyOnProperMaterial();
		this.damageVsEntity = i2 + enumToolMaterial3.getDamageVsEntity();
	}

	public float getStrVsBlock(ItemStack itemStack1, Block block2) {
		for(int i3 = 0; i3 < this.blocksEffectiveAgainst.length; ++i3) {
			if(this.blocksEffectiveAgainst[i3] == block2) {
				return this.efficiencyOnProperMaterial;
			}
		}

		return 1.0F;
	}

	public boolean hitEntity(ItemStack itemStack1, EntityLiving entityLiving2, EntityLiving entityLiving3) {
		itemStack1.func_25125_a(2, entityLiving3);
		return true;
	}

	public boolean func_25007_a(ItemStack itemStack1, int i2, int i3, int i4, int i5, EntityLiving entityLiving6) {
		itemStack1.func_25125_a(1, entityLiving6);
		return true;
	}

	public int getDamageVsEntity(Entity entity1) {
		return this.damageVsEntity;
	}
}
