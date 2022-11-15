public class Location {
	// Location Variables
	private int row;
	private int column;

	public Location(int row, int column) {
		this.row = row;
		this.column = column;
	}

	public int getR() {
		return row;
	}

	public int getC() {
		return column;
	}

	public void incR(int val) {
		row += val;
	}

	public void incC(int val) {
		column += val;
	}

	public void set(int row, int column) {
		this.row = row;
		this.column = column;
	}

	public boolean equals(Location loc) {
		if(this.getR() == loc.getR() && this.getC() == loc.getC())
			return true;
		return false;
	}

	public String toString() {
		return "(" + row + ", " + column + ")";
	}
}