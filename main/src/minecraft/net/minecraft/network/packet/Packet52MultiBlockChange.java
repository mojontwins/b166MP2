package net.minecraft.network.packet;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.Chunk;

public class Packet52MultiBlockChange extends Packet {
	public int xPosition;
	public int zPosition;
	public byte[] metadataArray;
	public int size;
	private static byte[] field_48168_e = new byte[0];

	public Packet52MultiBlockChange() {
		this.isChunkDataPacket = true;
	}

	public Packet52MultiBlockChange(int i1, int i2, short[] s3, int i4, World world5) {
		this.isChunkDataPacket = true;
		this.xPosition = i1;
		this.zPosition = i2;
		this.size = i4;
		int i6 = 4 * i4;
		Chunk chunk7 = world5.getChunkFromChunkCoords(i1, i2);

		try {
			if(i4 >= 64) {
				System.out.println("ChunkTilesUpdatePacket compress " + i4);
				if(field_48168_e.length < i6) {
					field_48168_e = new byte[i6];
				}
			} else {
				ByteArrayOutputStream byteArrayOutputStream8 = new ByteArrayOutputStream(i6);
				DataOutputStream dataOutputStream9 = new DataOutputStream(byteArrayOutputStream8);

				for(int i10 = 0; i10 < i4; ++i10) {
					int i11 = s3[i10] >> 12 & 15;
					int i12 = s3[i10] >> 8 & 15;
					int i13 = s3[i10] & 255;
					dataOutputStream9.writeShort(s3[i10]);
					dataOutputStream9.writeShort((short)((chunk7.getBlockID(i11, i13, i12) & 4095) << 4 | chunk7.getBlockMetadata(i11, i13, i12) & 15));
				}

				this.metadataArray = byteArrayOutputStream8.toByteArray();
				if(this.metadataArray.length != i6) {
					throw new RuntimeException("Expected length " + i6 + " doesn\'t match received length " + this.metadataArray.length);
				}
			}
		} catch (IOException iOException14) {
			System.err.println(iOException14.getMessage());
			this.metadataArray = null;
		}

	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.xPosition = dataInputStream1.readInt();
		this.zPosition = dataInputStream1.readInt();
		this.size = dataInputStream1.readShort() & 65535;
		int i2 = dataInputStream1.readInt();
		if(i2 > 0) {
			this.metadataArray = new byte[i2];
			dataInputStream1.readFully(this.metadataArray);
		}

	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.xPosition);
		dataOutputStream1.writeInt(this.zPosition);
		dataOutputStream1.writeShort((short)this.size);
		if(this.metadataArray != null) {
			dataOutputStream1.writeInt(this.metadataArray.length);
			dataOutputStream1.write(this.metadataArray);
		} else {
			dataOutputStream1.writeInt(0);
		}

	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleMultiBlockChange(this);
	}

	public int getPacketSize() {
		return 10 + this.size * 4;
	}
}
