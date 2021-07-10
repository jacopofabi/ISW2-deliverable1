package entity;

import java.sql.Date;

import org.eclipse.jgit.lib.ObjectId;

public class Commit {
	private ObjectId id;
	private Date date;
	private String message;
	private ObjectId parentId;
	
	public Commit(ObjectId id, Date date, String message, ObjectId parentId) {
		super();
		this.id = id;
		this.date = date;
		this.message = message;
		this.parentId = parentId;
	}
	
	public ObjectId getId() {
		return id;
	}
	public Date getDate() {
		return date;
	}
	public String getComment() {
		return message;
	}
	
	public ObjectId getParentId() {
		return parentId;
	}
	
}