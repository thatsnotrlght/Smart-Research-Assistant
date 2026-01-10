package com.research_assistant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/research") // Mapping all endpoints defined in Controller to this URL
@CrossOrigin(origins = "*") // Allows accessing all endpoints in this controller from frontend
public class ResearchController {
	private final ResearchService researchService;
	
	public ResearchController(ResearchService researchService) {
        this.researchService = researchService;
    }
	
	@PostMapping("/process")
	public ResponseEntity<String> processContent(@RequestBody ResearchRequest request) {
		String result = researchService.processContent(request);
		return ResponseEntity.ok(result);
	}
	
}
