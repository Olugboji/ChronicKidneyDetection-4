import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
/**
 * This class is used to partition provided data in 2 parts. 2/3 for training purpose
 * and 1/3 for testing purpose. 
 * 
 *
 */
public class TrainTestDataPartition {
	
	public static void main(String args[]) {
		BufferedReader br = null;

		try {

			String sCurrentLine;
			br = new BufferedReader(new FileReader(
					"chronic_kidney_disease_full.arff"));
			File fileOne = new File("training_data.arff");
			File fileTwo = new File("testing_data.arff");
			fileOne.createNewFile();
			fileTwo.createNewFile();

			FileWriter fwOne = new FileWriter(fileOne.getAbsoluteFile());
			FileWriter fwTwo = new FileWriter(fileTwo.getAbsoluteFile());
			BufferedWriter bwOne = new BufferedWriter(fwOne);
			BufferedWriter bwTwo = new BufferedWriter(fwTwo);
			int cnt = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				if (sCurrentLine.length() > 1)
					if (Character.isDigit(sCurrentLine.charAt(0))
							|| sCurrentLine.charAt(0) == '?') {
						if (cnt % 3 != 0)
							bwOne.write(sCurrentLine + "\n");
						else
							bwTwo.write(sCurrentLine + "\n");
						cnt++;
					}
			}
			bwOne.close();
			bwTwo.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
