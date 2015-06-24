package grammar;

import grammar.exception.ParseException;

import java.util.Arrays;
import java.util.List;

/**
 * PP07 data type sizes.
 */
public class TypeSize {
	private static final int INT = 4;
	private static final int BOOL = 1;
	private static final int VOID = 0;
	private static final int CHAR = 1;

	private static final List<Type> baseTypes = Arrays.asList(Type.BOOL, Type.CHAR, Type.INT);

	private static int getEnum(int length) {
		return 4 * length;
	}

	private static int getArray(int length, Type elemType) throws ParseException {
		assert baseTypes.contains(elemType);
		return getSize(elemType) * length;
	}

	public static int getSize(Type type) throws ParseException {
		switch (type) {
			case INT:
				return INT;
			case BOOL:
				return BOOL;
			case CHAR:
				return CHAR;
			case VOID:
				throw new ParseException("Cannot add 'void' as a variable");
			case ENUM:
				throw new ParseException("Type not supported: " + type.toString() + ". Use getSize(Type type, int length) instead.");
			case ARRAY:
				throw new ParseException("Type not supported: " + type.toString() + ". Use getSize(Type type, int length, Type elemType) instead.");
			default:
				throw new ParseException("Type not defined: " + type.toString());
		}
	}

	public static int getSize(Type type, int length) throws ParseException {
		switch (type) {
			case INT:
				throw new ParseException("Type not supported: " + type.toString() + ". Use getSize(Type type) instead.");
			case BOOL:
				throw new ParseException("Type not supported: " + type.toString() + ". Use getSize(Type type) instead.");
			case CHAR:
				throw new ParseException("Type not supported: " + type.toString() + ". Use getSize(Type type) instead.");
			case VOID:
				throw new ParseException("Cannot add 'void' as a variable");
			case ENUM:
				return getEnum(length);
			case ARRAY:
				throw new ParseException("Type not supported: " + type.toString() + ". Use getSize(Type type, int length, Type elemType) instead.");
			default:
				throw new ParseException("Type not defined: " + type.toString());
		}
	}

	public static int getSize(Type type, int length, Type elemType) throws ParseException {
		switch (type) {
			case INT:
				throw new ParseException("Type not supported: " + type.toString() + ". Use getSize(Type type) instead.");
			case BOOL:
				throw new ParseException("Type not supported: " + type.toString() + ". Use getSize(Type type) instead.");
			case CHAR:
				throw new ParseException("Type not supported: " + type.toString() + ". Use getSize(Type type) instead.");
			case VOID:
				throw new ParseException("Cannot add 'void' as a variable");
			case ENUM:
				throw new ParseException("Type not supported: " + type.toString() + ". Use getSize(Type type, int length) instead.");
			case ARRAY:
				return getArray(length, elemType);
			default:
				throw new ParseException("Type not defined: " + type.toString());
		}
	}

}
