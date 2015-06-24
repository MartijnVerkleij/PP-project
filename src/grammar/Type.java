package grammar;

/**
 * Code reused from <code>pp.block5.cc.simple.Type</code>
 */

/**
 * PP07 data type.
 */
abstract public class Type {
	/**
	 * The singleton instance of the {@link Bool} type.
	 */
	public static final Type BOOL = new Bool();
	/**
	 * The singleton instance of the {@link Int} type.
	 */
	public static final Type INT = new Int();
	/**
	 * The singleton instance of the {@link Char} type.
	 */
	public static final Type CHAR = new Char();
	
	//TODO add void and enum
	private final TypeKind kind;

	public Type(TypeKind kind) {
		this.kind = kind;
	}

	public TypeKind getKind() {
		return this.kind;
	}

	/**
	 * returns the size (in bytes) of a value of this type.
	 */
	abstract public int size();

	static public class Bool extends Type {
		private Bool() {
			super(TypeKind.BOOL);
		}

		@Override
		public int size() {
			return 1;
		}

		@Override
		public String toString() {
			return "Boolean";
		}
	}

	static public class Int extends Type {
		private Int() {
			super(TypeKind.INT);
		}

		@Override
		public int size() {
			return 4;
		}

		@Override
		public String toString() {
			return "Integer";
		}
	}

	static public class Char extends Type {
		private Char() {
			super(TypeKind.CHAR);
		}

		@Override
		public int size() {
			return 1;
		}

		@Override
		public String toString() {
			return "Char";
		}
	}

	static public class Array extends Type {
		private final int lower;
		private final int upper;
		private final Type elemType;

		private Array(int lower, int upper, Type elemType) {
			super(TypeKind.ARRAY);
			assert upper >= lower;
			this.lower = lower;
			this.upper = upper;
			this.elemType = elemType;
		}

		public int getLower() {
			return this.lower;
		}

		public int getUpper() {
			return this.upper;
		}

		public Type getElemType() {
			return this.elemType;
		}

		@Override
		public int size() {
			return (getUpper() - getLower() + 1) * this.elemType.size();
		}

		@Override
		public String toString() {
			return "Array [" + this.lower + ".." + this.upper + "] of "
					+ this.elemType;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof Array)) {
				return false;
			}
			Array other = (Array) obj;
			return this.elemType.equals(other.elemType) &&
					this.lower == other.lower && this.upper == other.upper;
		}

	}
}
