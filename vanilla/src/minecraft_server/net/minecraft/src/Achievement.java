package net.minecraft.src;

public class Achievement extends StatBase {
	public final int field_25067_a;
	public final int field_27991_b;
	public final Achievement field_27992_c;
	private final String field_27063_l;
	public final ItemStack theItemStack;
	private boolean field_27062_m;

	public Achievement(int i1, String string2, int i3, int i4, Item item5, Achievement achievement6) {
		this(i1, string2, i3, i4, new ItemStack(item5), achievement6);
	}

	public Achievement(int i1, String string2, int i3, int i4, Block block5, Achievement achievement6) {
		this(i1, string2, i3, i4, new ItemStack(block5), achievement6);
	}

	public Achievement(int i1, String string2, int i3, int i4, ItemStack itemStack5, Achievement achievement6) {
		super(5242880 + i1, StatCollector.translateToLocal("achievement." + string2));
		this.theItemStack = itemStack5;
		this.field_27063_l = StatCollector.translateToLocal("achievement." + string2 + ".desc");
		this.field_25067_a = i3;
		this.field_27991_b = i4;
		if(i3 < AchievementList.field_27114_a) {
			AchievementList.field_27114_a = i3;
		}

		if(i4 < AchievementList.field_27113_b) {
			AchievementList.field_27113_b = i4;
		}

		if(i3 > AchievementList.field_27112_c) {
			AchievementList.field_27112_c = i3;
		}

		if(i4 > AchievementList.field_27111_d) {
			AchievementList.field_27111_d = i4;
		}

		this.field_27992_c = achievement6;
	}

	public Achievement a() {
		this.field_27058_g = true;
		return this;
	}

	public Achievement func_27060_b() {
		this.field_27062_m = true;
		return this;
	}

	public Achievement c() {
		super.func_27053_d();
		AchievementList.field_25129_a.add(this);
		return this;
	}

	public StatBase func_27053_d() {
		return this.c();
	}

	public StatBase func_27052_e() {
		return this.a();
	}
}
