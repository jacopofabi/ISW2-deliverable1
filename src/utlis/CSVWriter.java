package utlis;

import java.io.FileWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import entity.FixedBug;

public class CSVWriter {

	private static final String FILENAME = "processControlData.csv";
	
	private CSVWriter() {}
	
	@SuppressWarnings("unchecked")
	public static void write(String projectName, List<FixedBug> fixedBugs) {
		
		String outputName = projectName + FILENAME;
		HashMap<String, Integer> frequencies = calculateFreq(fixedBugs);
		try (FileWriter fileWriter = new FileWriter(outputName)) {
			StringBuilder outputBuilder = new StringBuilder("Month; #Bug Fixed\n");
			
			Iterator<?> it = frequencies.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>)it.next();
		        outputBuilder.append(entry.getKey()+ ";" + entry.getValue()+ "\n");
		    }
		    
		    fileWriter.append(outputBuilder.toString());

		} catch (Exception e) {
			Logger logger = Logger.getLogger(CSVWriter.class.getName());
			logger.log(Level.SEVERE, "csvError", e);
		}
	}
	
	private static HashMap<String, Integer> calculateFreq(List<FixedBug> fixedBugs){
		HashMap<String, Integer> frequencies = new HashMap<>();
		Calendar calendar = new GregorianCalendar();

		for (FixedBug fb : fixedBugs) {
			calendar.setTime(fb.getDate());
			String yearAndMonth = String.format("%d-%d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1);
			if (frequencies.containsKey(yearAndMonth)) {
				Integer counter = frequencies.get(yearAndMonth)+1;
				frequencies.put(yearAndMonth, counter);
			}else {
				frequencies.put(yearAndMonth, 1);
			}
		}
		
		return frequencies;
	}
	
}
