package net.minecraft.src;

public class StatBasic extends StatBase {
	public StatBasic(int i1, String string2, IStatType iStatType3) {
		super(i1, string2, iStatType3);
	}

	public StatBasic(int i1, String string2) {
		super(i1, string2);
	}

	public StatBase func_27053_d() {
		super.func_27053_d();
		StatList.field_25122_b.add(this);
		return this;
	}
}
