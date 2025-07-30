package net.minecraft.world;

public class Direction {
	public static final int[] offsetX = new int[]{0, -1, 0, 1};
	public static final int offsetY[] = {  0,  0,  0,  0,  1, -1,  0,    0,  0,  0,  0 };
	public static final int[] offsetZ = new int[]{1, 0, -1, 0};
	
	public static final int[] headInvisibleFace = new int[]{3, 4, 2, 5};
	public static final int[] vineGrowth = new int[]{-1, -1, 2, 0, 1, 3};
	public static final int[] footInvisibleFaceRemap = new int[]{2, 3, 0, 1};
	
	public static final int[] enderEyeMetaToDirection = new int[]{1, 2, 3, 0};
	public static final int[] field_35868_g = new int[]{3, 0, 1, 2};
	public static final int[][] bedDirection = new int[][]{{1, 0, 3, 2, 5, 4}, {1, 0, 5, 4, 2, 3}, {1, 0, 2, 3, 4, 5}, {1, 0, 4, 5, 3, 2}};
	
	public static final int SOUTH = 0;
	public static final int WEST = 1;
	public static final int NORTH = 2;
	public static final int EAST = 3;
	
	public static final int UP = 4;
	public static final int DOWN = 5;
	
	public static final int NONE = 6;
	
	public static final int SW = 7, NW = 8, NE = 9, SE = 10;
	
	public static final int HORZ_PLANE[] = {SOUTH, WEST, NORTH, EAST};
	public static final int VERT_AXIS[] = {UP, NONE, DOWN};
	
	public static final int HORZ_PLANE_FULL[] = {SOUTH, SW, WEST, NW, NORTH, NE, EAST, SE};
	public static final int HORZ_PLANE_DIAGONALS[] = {SW, NW, NE, SE};

	public static int getCW(int direction) {
		if(direction < 4) return (direction + 1) & 3;
		else if(direction > 6) return 7 + ((direction - 6) & 3);
		return direction;
	}
}
