package net.minecraft.network.packet;

import java.util.HashMap;
import java.util.Map;

public class PacketCount {
	public static boolean allowCounting = true;
	private static final Map<Integer, Long> packetCountForID = new HashMap<Integer, Long>();
	private static final Map<Integer, Long> sizeCountForID = new HashMap<Integer, Long>();
	private static final Object lock = new Object();

	public static void countPacket(int i0, long j1) {
		if(allowCounting) {
			synchronized(lock) {
				if(packetCountForID.containsKey(i0)) {
					packetCountForID.put(i0, ((Long)packetCountForID.get(i0)).longValue() + 1L);
					sizeCountForID.put(i0, ((Long)sizeCountForID.get(i0)).longValue() + j1);
				} else {
					packetCountForID.put(i0, 1L);
					sizeCountForID.put(i0, j1);
				}

			}
		}
	}
}
