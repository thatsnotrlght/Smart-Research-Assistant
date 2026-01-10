package com.research_assistant;

import java.util.Objects;

import lombok.Data;

@Data
public class ResearchRequest {
	private String content;
	private String operation; // Different operations (Summarize, suggest, etc. of content)
	
	public ResearchRequest() {

	}
	
	public ResearchRequest(String content, String operation) {
		this.content = content;
		this.operation = operation;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}

	@Override
	public int hashCode() {
		return Objects.hash(content, operation);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResearchRequest other = (ResearchRequest) obj;
		return Objects.equals(content, other.content) && Objects.equals(operation, other.operation);
	}
}
