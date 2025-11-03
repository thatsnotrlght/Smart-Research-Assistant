package com.research_assistant;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ResearchService {
	private String geminiApiUrl;
	private String geminiApiKey;
	private final WebClient webClient;
	private final ObjectMapper objectMapper;
	
	public ResearchService(WebClient.Builder webClientBuilder, 
			ObjectMapper objectMapper, 
			@Value("${gemini.api.url}") String geminiApiUrl, 	
			@Value("${gemini.api.key}") String geminiApiKey) {
		
		this.webClient = webClientBuilder.build(); // This helps get instance of Web Client
		this.objectMapper = new ObjectMapper();
		this.geminiApiKey = geminiApiKey;
		this.geminiApiUrl = geminiApiUrl;
	}

	public String processContent(ResearchRequest request) {
		// Build the prompt
		String prompt = buildPrompt(request);
		
		// Query the AI Model API
		Map<String, Object> requestBody = Map.of( // Format of JSON Request Body 
				"contents", new Object[] {
						Map.of("parts", new Object[] {
							Map.of("text", prompt)
					})
				}
			);
		
			// Making the actual API call using WebClient
		String response = webClient.post()
				.uri(geminiApiUrl + geminiApiKey)
				.bodyValue(requestBody)
				.retrieve()
				.bodyToMono(String.class)
				.block();
		// Parse the response
		// Return response
		
		return extractTextFromResponse(response);
	}
	
	private String extractTextFromResponse(String response) {
		try {
			// With ObjectMapping, the response we get in JSON format is mapped to structured Java class hierarchy in GeminiResponse class
			GeminiResponse geminiResponse = objectMapper.readValue(response, GeminiResponse.class);
			// Checking if there are candidates inside of GeminiReponse
			if (geminiResponse.getCandidates() != null && !geminiResponse.getCandidates().isEmpty()) { 
				
				// If candidates -> set candidate to first element in List at index 0 (.get(0))
				GeminiResponse.Candidate firstCandidate = geminiResponse.getCandidates().get(0); 
				if (firstCandidate.getContent() != null && // If content is NOT empty
						firstCandidate.getContent().getParts() != null && // AND if Parts in content is NOT empty
						!firstCandidate.getContent().getParts().isEmpty() ) { // AND if !(Parts in content is empty) 
					
					// Return the text from first element in List
					return firstCandidate.getContent().getParts().get(0).getText(); 
					
				}
			}
		} catch (Exception e) {
			return "Error Parsing: " + e.getMessage();
		}
		return response;
	}

	// Crafting entire prompt
	private String buildPrompt(ResearchRequest request) {
		StringBuilder prompt = new StringBuilder();
		
		switch (request.getOperation()) {
			case "summarize":
				prompt.append("Act as a diligent academic editor and perform a thorough analysis of the text provided below. Your task is to generate a summary that clearly identifies the core topic and primary conclusion of the text. Structure your response into two distinct and labeled sections:\r\n"
						+ "\r\n"
						+ "Main Topic: Identify the central subject or theme of the text in a single, clear sentence. This should encapsulate the essence of the content being analyzed.\r\n"
						+ "\r\n"
						+ "Summary: Provide a concise distillation of the text's content in 3 to 7 sentences. This summary should highlight the key points and conclusions drawn in the text while maintaining an objective and informative tone.\r\n"
						+ "\r\n"
						+ "Please ensure that your analysis is comprehensive yet succinct, capturing the essential elements without superfluous details.\r\n"
						+ "\r\n"
						+ "TEXT: ${text}");
				break;
			case "suggest":
				prompt.append("You are an expert Research Librarian and Content Analyst. Your task is to analyze the following piece of text and provide suggestions for further study. The output MUST be formatted using clear headings and bulleted lists. Exclude any introductory or concluding sentences outside of the requested structure.\r\n"
						+ "\r\n"
						+ "Your response must contain two separate sections:\r\n"
						+ "\r\n"
						+ "1. Related Topics for Further Research\r\n"
						+ "List 3 to 5 conceptual areas or sub-disciplines that are closely related to the main subject of the text provided. Ensure that the topics are pertinent and conducive to enhancing understanding of the main subject.\r\n"
						+ "2. Suggested Keywords and Reading Material\r\n"
						+ "Provide 5 to 10 keywords that are relevant to the text for use in further searches and research.\r\n"
						+ "Suggest specific readings, articles, or books that would be beneficial for deeper exploration of the subject matter.\r\n"
						+ "\r\n"
						+ "TEXT FOR ANALYSIS: {text}");
				break;
			case "Cite":
				prompt.append("\"You are a meticulous Academic Librarian specializing in student research papers. Your primary goal is to generate a perfectly structured citation template based on the provided text, using the requested citation style.\r\n"
						+ "\r\n"
						+ "**Instructions & Constraints:**\r\n"
						+ "\r\n"
						+ "1.  **Style:** Generate a citation in the **MLA 9th Edition** format.\r\n"
						+ "2.  **Required Information:** You must attempt to infer the following necessary metadata from the selected text, and if it cannot be inferred, you must use a clear placeholder in ALL CAPS and brackets.\r\n"
						+ "    * **Author:** Infer from text or use `[AUTHOR'S LAST NAME, FIRST NAME]`.\r\n"
						+ "    * **Title of Source:** Infer from text or use `[TITLE OF ARTICLE OR WEBPAGE]`.\r\n"
						+ "    * **Publication Date:** Infer from text or use `[DD MONTH YEAR]`.\r\n"
						+ "3.  **URL:** If provided, include the URL. If not, use `https://www.computerhope.com/jargon/w/webpage.htm`.\r\n"
						+ "4.  **Output:** Output the complete, single citation string **only**. Do not include any introductory sentences, labels, or explanations.\r\n"
						+ "\r\n"
						+ "**METADATA HINTS (USE IF AVAILABLE):**\r\n"
						+ "URL: {url}\r\n"
						+ "\r\n"
						+ "**TEXT EXCERPT FOR ANALYSIS:**\r\n"
						+ "{text}\"");
				break;
			default:
				throw new IllegalArgumentException("Unknown Operation: " + request.getOperation());
		}
		prompt.append(request.getContent());
		return prompt.toString();
	}

}
