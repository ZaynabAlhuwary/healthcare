package com.healthcare.healthcare_system.service;

import com.healthcare.healthcare_system.dto.ChatRequest;
import com.healthcare.healthcare_system.dto.ChatResponse;
import com.healthcare.healthcare_system.dto.FacilityDto;
import com.healthcare.healthcare_system.dto.PatientDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * The type Ai service.
 */
@Service
@RequiredArgsConstructor
public class AIService {
    private final FacilityService facilityService;
    private final PatientService patientService;
    private final HttpClient httpClient;

    @Value("${ollama.api.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;

    @Value("${ollama.model:llama2}")
    private String ollamaModel;

//    public ChatResponse processQuery(ChatRequest chatRequest) {
//        String query = chatRequest.getQuery().toLowerCase();
//        ChatResponse response = new ChatResponse();
//
//        // Handle known query patterns
//        if (query.contains("list patients from facility") || query.contains("patients in facility")) {
//            Long facilityId = extractIdFromQuery(query);
//            if (facilityId != null) {
//                List<PatientDto> patients = patientService.getPatientsByFacility(facilityId, Pageable.unpaged()).getContent();
//                response.setResponse("Here are the patients from facility " + facilityId);
//                response.setData(patients);
//                return response;
//            }
//        } else if (query.contains("facilities with more than") || query.contains("facilities having more than")) {
//            int count = extractNumberFromQuery(query);
//            List<FacilityDto> facilities = facilityService.getFacilitiesWithPatientCountGreaterThan(count);
//            response.setResponse("Facilities with more than " + count + " patients:");
//            response.setData(facilities);
//            return response;
//        }
//
//        // Fallback to Ollama for complex queries
//        return handleComplexQueryWithOllama(chatRequest);
//    }

    /**
     * Process query chat response.
     *
     * @param chatRequest the chat request
     * @return the chat response
     */
//    public ChatResponse processQuery(ChatRequest chatRequest) {
//        String query = chatRequest.getQuery().toLowerCase();
//        ChatResponse response = new ChatResponse();
//
//        try {
//            // Handle known query patterns
//            if (query.contains("list patients from facility") || query.contains("patients in facility")) {
//                Long facilityId = extractIdFromQuery(query);
//                if (facilityId != null) {
//                    List<PatientDto> patients = patientService.getPatientsByFacility(facilityId, Pageable.unpaged()).getContent();
//                    response.setResponse("Here are the patients from facility " + facilityId);
//                    response.setData(patients);
//                    return response;
//                }
//            } else if (query.contains("facilities with more than") || query.contains("facilities having more than")) {
//                int count = extractNumberFromQuery(query);
//                List<FacilityDto> facilities = facilityService.getFacilitiesWithPatientCountGreaterThan(count);
//                System.out.println("***************************************************"+facilities.size());
//                if (facilities != null && !facilities.isEmpty()) {
//                    response.setResponse("Facilities with more than " + count + " patients:");
//                    response.setData(facilities);
//                } else {
//                    response.setResponse("No facilities found with more than " + count + " patients.");
//                }
//                return response;
//            }
//
//            // Fallback to Ollama for complex queries
//            return handleComplexQueryWithOllama(chatRequest);
//        } catch (Exception e) {
//            ChatResponse errorResponse = new ChatResponse();
//            errorResponse.setResponse("Sorry, I encountered an error processing your request: " + e.getMessage());
//            return errorResponse;
//        }
//    }

    public ChatResponse processQuery(ChatRequest chatRequest) {
        String query = chatRequest.getQuery().toLowerCase();
        ChatResponse response = new ChatResponse();

        try {
            // Handle known query patterns
            if (query.contains("patients from facility") || query.contains("patients in facility")) {
                Long facilityId = extractIdFromQuery(query);
                if (facilityId != null) {
                    List<PatientDto> patients = patientService.getPatientsByFacility(facilityId, Pageable.unpaged()).getContent();
                    response.setResponse("Here are the patients from facility " + facilityId);
                    response.setData(patients);
                    return response;
                }
            } else if (query.matches(".*facilit(y|ies).*more than.*\\d+.*patient.*") ||
                    query.matches(".*which facilit(y|ies).*have more than.*\\d+.*patient.*")) {
                int count = extractNumberFromQuery(query);
                List<FacilityDto> facilities = facilityService.getFacilitiesWithPatientCountGreaterThan(count);
                if (facilities != null && !facilities.isEmpty()) {
                    response.setResponse("Facilities with more than " + count + " patients:");
                    response.setData(facilities);
                } else {
                    response.setResponse("No facilities found with more than " + count + " patients.");
                }
                return response;
            }

            // Fallback to Ollama for complex queries
            return handleComplexQueryWithOllama(chatRequest);
        } catch (Exception e) {
            ChatResponse errorResponse = new ChatResponse();
            errorResponse.setResponse("Sorry, I encountered an error processing your request: " + e.getMessage());
            return errorResponse;
        }
    }

    private ChatResponse handleComplexQueryWithOllama(ChatRequest chatRequest) {
        try {
            String ollamaResponse = callOllamaApi(chatRequest.getQuery()).join();

            ChatResponse response = new ChatResponse();
            response.setResponse(ollamaResponse);
            return response;
        } catch (Exception e) {
            ChatResponse errorResponse = new ChatResponse();
            errorResponse.setResponse("Sorry, I encountered an error processing your request. Please try again.");
            return errorResponse;
        }
    }

    private CompletableFuture<String> callOllamaApi(String query) {
        String requestBody = String.format("""
                {
                    "model": "%s",
                    "prompt": "%s",
                    "stream": false
                }
                """, ollamaModel, escapeJson(query));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ollamaBaseUrl + "/api/generate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .timeout(Duration.ofSeconds(30))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::extractResponseFromOllama);
    }

    private String extractResponseFromOllama(String ollamaResponse) {
        try {
            // Simple JSON parsing - in a real application you might want to use a proper JSON library
            int responseStart = ollamaResponse.indexOf("\"response\":\"") + 12;
            int responseEnd = ollamaResponse.indexOf("\"", responseStart);
            return ollamaResponse.substring(responseStart, responseEnd)
                    .replace("\\n", "\n")
                    .replace("\\\"", "\"");
        } catch (Exception e) {
            return "I couldn't understand that. Could you rephrase your question?";
        }
    }

    private String escapeJson(String input) {
        return input.replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private Long extractIdFromQuery(String query) {
        try {
            String[] parts = query.split(" ");
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].equals("facility") && i + 1 < parts.length) {
                    return Long.parseLong(parts[i + 1]);
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private int extractNumberFromQuery(String query) {
        try {
            String[] parts = query.split(" ");
            for (String part : parts) {
                if (part.matches("\\d+")) {
                    return Integer.parseInt(part);
                }
            }
        } catch (Exception e) {
            return 50;
        }
        return 50;
    }
}