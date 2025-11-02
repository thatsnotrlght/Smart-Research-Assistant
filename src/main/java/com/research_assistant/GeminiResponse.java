package com.research_assistant;

import java.util.List;

import lombok.Data;

@Data
public class GeminiResponse {
	private List<Candidate> candidateList;
	
	private static class Candidate {
		private Content content;
	}
	
	private static class Content {
		private List<Part> parts;
	}
	private static class Part {
		private String text;
	}
	
}
