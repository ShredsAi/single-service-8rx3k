package ai.shreds.adapter.primary;

import ai.shreds.shared.dtos.SharedMessageRequestDTO;
import ai.shreds.shared.dtos.SharedMessageResponseDTO;
import ai.shreds.application.ports.ApplicationMessageInputPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/messages")
@Validated
@Tag(name = "Message Controller", description = "API endpoints for message management")
public class AdapterMessageController {

    private final ApplicationMessageInputPort applicationMessageInputPort;

    @Autowired
    public AdapterMessageController(ApplicationMessageInputPort applicationMessageInputPort) {
        this.applicationMessageInputPort = applicationMessageInputPort;
    }

    @Operation(summary = "Save a new message", description = "Saves a message to both MySQL and MongoDB databases")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Message successfully saved"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SharedMessageResponseDTO> saveMessage(@Valid @RequestBody SharedMessageRequestDTO request) {
        log.info("Received request to save message: {}", request);
        
        SharedMessageResponseDTO response = applicationMessageInputPort.saveMessage(request);
        
        log.info("Message successfully saved with MySQL ID: {} and MongoDB ID: {}", 
                response.getMysqlId(), response.getMongoId());
        
        return ResponseEntity.ok(response);
    }
}
