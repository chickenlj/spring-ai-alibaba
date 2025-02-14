package com.alibaba.cloud.ai.example.rag.local;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RagController {

	private final RagService ragService;

	@Autowired
	public RagController(RagService ragService) {
		this.ragService = ragService;
	}

	@GetMapping("/ai/rag")
	public AssistantMessage generate(
			@RequestParam(value = "message", defaultValue = "What bike is good for city commuting?") String message) {
		return ragService.retrieve(message);
	}

}
