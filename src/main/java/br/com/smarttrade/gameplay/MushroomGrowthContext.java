package br.com.smarttrade.gameplay;

public final class MushroomGrowthContext {
	private static final ThreadLocal<Integer> FIXED_TREE_HEIGHT = new ThreadLocal<>();

	private MushroomGrowthContext() {
	}

	public static void begin(int treeHeight) {
		FIXED_TREE_HEIGHT.set(treeHeight);
	}

	public static Integer fixedTreeHeight() {
		return FIXED_TREE_HEIGHT.get();
	}

	public static void clear() {
		FIXED_TREE_HEIGHT.remove();
	}
}
