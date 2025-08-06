package com.mojontwins.utils;

public class Idx2uv {
	public static int u, v;
	public static void calc (int idx) {
		u = (idx & 15) << 4;
		v = (idx & 0xff0);
	}
}
