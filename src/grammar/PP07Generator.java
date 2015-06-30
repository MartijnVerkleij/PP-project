package grammar;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;

public class PP07Generator {
	private BufferedWriter writer;
	private BufferedReader reader;
	private File file;


	private void emit(Op op, String... strings) {
		emit(null, op, strings);
	}

	private void emit(Label label, Op op, String[] strings) {

	}
}
