package com.bu.getactivecore.error;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 @ai-generated,
 Tool: Google Gemini,
 Prompt: "how to add error message in API spring boot",
 Generated on: 2025-05-22,
 Modified by: Jin Hao Li,
 Modifications: Add more fields
 Verified: ✅ Unit tested, reviewed
*/

public class ErrorResponse {
        private int status;
        private String message;
        private List<String> errors;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime timestamp;
    
        public ErrorResponse(int status, String message, List<String> errors) {
            this.status = status;
            this.message = message;
            this.errors = errors;
            this.timestamp = LocalDateTime.now();
        }
    
        public int getStatus() {
            return status;
        }

        public List<String> getErrors() {
            return errors;
        }
    
        public String getMessage() {
            return message;
        }
    
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
 }