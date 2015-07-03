package grammar;

import grammar.exception.TypeException;

import java.util.Arrays;
import java.util.List;

/**
 * PP07 data type sizes.
 */
public class TypeSize {
	public static final int WORD = 4;
	private static final int INT = 4;
	private static final int BOOL = 1;
	private static final int VOID = 0;
	private static final int CHAR = 1;
	private static final List<Type> baseTypes = Arrays.asList(Type.BOOL, Type.CHAR, Type.INT);

	private static int getEnum(int length) {
		return 4 * length;
	}

	private static int getArray(int length, Type elemType) throws TypeException {
		assert baseTypes.contains(elemType);
		return getSize(elemType) * length;
	}

	public static int getSize(Type type) throws TypeException {
		switch (type) {
			case INT:
				return INT;
			case BOOL:
				return BOOL;
			case CHAR:
				return CHAR;
			case VOID:
				throw new TypeException("Cannot add 'void' as a variable");
			case ENUM:
				throw new TypeException("Type not supported: " + type.toString() + ". Use getSize(Type type, int length) instead.");
			case ARRAY:
				throw new TypeException("Type not supported: " + type.toString() + ". Use getSize(Type type, int length, Type elemType) instead.");
			default:
				throw new TypeException("Type not defined: " + type.toString());
		}
	}

	public static int getSize(Type type, int length) throws TypeException {
		switch (type) {
			case INT:
				throw new TypeException("Type not supported: " + type.toString() + ". Use getSize(Type type) instead.");
			case BOOL:
				throw new TypeException("Type not supported: " + type.toString() + ". Use getSize(Type type) instead.");
			case CHAR:
				throw new TypeException("Type not supported: " + type.toString() + ". Use getSize(Type type) instead.");
			case VOID:
				throw new TypeException("Cannot add 'void' as a variable");
			case ENUM:
				return getEnum(length);
			case ARRAY:
				throw new TypeException("Type not supported: " + type.toString() + ". Use getSize(Type type, int length, Type elemType) instead.");
			default:
				throw new TypeException("Type not defined: " + type.toString());
		}
	}

	public static int getSize(Type type, int length, Type elemType) throws TypeException {
		switch (type) {
			case INT:
				throw new TypeException("Type not supported: " + type.toString() + ". Use getSize(Type type) instead.");
			case BOOL:
				throw new TypeException("Type not supported: " + type.toString() + ". Use getSize(Type type) instead.");
			case CHAR:
				throw new TypeException("Type not supported: " + type.toString() + ". Use getSize(Type type) instead.");
			case VOID:
				throw new TypeException("Cannot add 'void' as a variable");
			case ENUM:
				throw new TypeException("Type not supported: " + type.toString() + ". Use getSize(Type type, int length) instead.");
			case ARRAY:
				return getArray(length, elemType);
			default:
				throw new TypeException("Type not defined: " + type.toString());
		}
	}

}
