package grammar;

public class Value {
	private final Integer value;

	public Value(Integer value) {
		this.value = value;
	}

	public Value(String value) {
		this.value = Integer.parseInt(value);
	}

	public Value(boolean value) {
		this.value = value ? 1 : 0;
	}

	public Integer getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value.toString();
	}
}