import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MyFile {
	private FileWriter fw;
	private BufferedWriter bw;
	private File file;
	
	public MyFile(String name) {
		file = new File(name);
	}
	
	public void openFile(){
		try {
			if (!file.exists()) {
				file.createNewFile();
			}

			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeInFile(String input){
		try {
			bw.write(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void closeFile(){
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
