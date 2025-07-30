package net.minecraft.server.rcon;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RConOutputStream {
	private ByteArrayOutputStream byteArrayOutput;
	private DataOutputStream output;

	public RConOutputStream(int i1) {
		this.byteArrayOutput = new ByteArrayOutputStream(i1);
		this.output = new DataOutputStream(this.byteArrayOutput);
	}

	public void writeByteArray(byte[] b1) throws IOException {
		this.output.write(b1, 0, b1.length);
	}

	public void writeString(String string1) throws IOException {
		this.output.writeBytes(string1);
		this.output.write(0);
	}

	public void writeInt(int i1) throws IOException {
		this.output.write(i1);
	}

	public void writeShort(short s1) throws IOException {
		this.output.writeShort(Short.reverseBytes(s1));
	}

	public byte[] toByteArray() {
		return this.byteArrayOutput.toByteArray();
	}

	public void reset() {
		this.byteArrayOutput.reset();
	}
}
