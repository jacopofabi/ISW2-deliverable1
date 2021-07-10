package entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FixedBug {
	
	private String key;
	private String jiraDate;
	private Date date;
	
	private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	
	public FixedBug(String key, String date) throws ParseException {
		this.key = key;
		this.jiraDate = date;
		this.date = formatter.parse(date);
	}
	public FixedBug(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return String.format("%s | %s | %s ", key, jiraDate, date.toString());
	}
	
	public String getFinalDate() {
		return this.jiraDate;
	}
	
	public void setFinalDate(String date) {
		this.jiraDate = date;
	}
	
}
