package net.minecraft.src;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class StatBase {
	public final int statId;
	public final String statName;
	public boolean field_27058_g;
	public String field_27057_h;
	private final IStatType field_25065_a;
	private static NumberFormat field_25066_b = NumberFormat.getIntegerInstance(Locale.US);
	public static IStatType field_27056_i = new StatTypeSimple();
	private static DecimalFormat field_25068_c = new DecimalFormat("########0.00");
	public static IStatType field_27055_j = new StatTypeTime();
	public static IStatType field_27054_k = new StatTypeDistance();

	public StatBase(int i1, String string2, IStatType iStatType3) {
		this.field_27058_g = false;
		this.statId = i1;
		this.statName = string2;
		this.field_25065_a = iStatType3;
	}

	public StatBase(int i1, String string2) {
		this(i1, string2, field_27056_i);
	}

	public StatBase func_27052_e() {
		this.field_27058_g = true;
		return this;
	}

	public StatBase func_27053_d() {
		if(StatList.field_25104_C.containsKey(this.statId)) {
			throw new RuntimeException("Duplicate stat id: \"" + ((StatBase)StatList.field_25104_C.get(this.statId)).statName + "\" and \"" + this.statName + "\" at id " + this.statId);
		} else {
			StatList.field_25123_a.add(this);
			StatList.field_25104_C.put(this.statId, this);
			this.field_27057_h = AchievementMap.func_25132_a(this.statId);
			return this;
		}
	}

	public String toString() {
		return this.statName;
	}
}
