
public enum Side {
	TOP(0), RIGHT(1), BOTTOM(2), LEFT(3);

	private int index;

	private Side(int i) {
		index = i;
	}

	int getIndex() {
		return index;
	}

}
