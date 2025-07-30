package net.minecraft.src;

import java.io.ByteArrayOutputStream;

class RegionFileChunkBuffer extends ByteArrayOutputStream {
	private int field_22156_b;
	private int field_22158_c;
	final RegionFile field_22157_a;

	public RegionFileChunkBuffer(RegionFile regionFile1, int i2, int i3) {
		super(8096);
		this.field_22157_a = regionFile1;
		this.field_22156_b = i2;
		this.field_22158_c = i3;
	}

	public void close() {
		this.field_22157_a.write(this.field_22156_b, this.field_22158_c, this.buf, this.count);
	}
}
