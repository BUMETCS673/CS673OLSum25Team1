package com.bu.getactivecore.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
    @ai-generated,
    Tool: Google Gemini,
    Prompt: "how to add error message in API spring boot",
    Generated on: 2025-05-22,
    Modified by: Jin Hao Li,
    Modification: add ResponseStatus
    Verified: ✅ Unit tested, reviewed
    */


@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
       
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

