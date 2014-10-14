import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileSaveMethod {

	public static void saveFile(File F, String S){
		try {
			File file = F;
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			//
			//write process
			bw.write(S);
			//end write process
			//
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}


