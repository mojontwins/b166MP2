package net.minecraft.world.item;

public enum EnumRarity {
	common(15, "Common"),
	uncommon(14, "Uncommon"),
	rare(11, "Rare"),
	epic(13, "Epic");

	public final int nameColor;
	public final String field_40532_f;

	private EnumRarity(int i3, String string4) {
		this.nameColor = i3;
		this.field_40532_f = string4;
	}
}
