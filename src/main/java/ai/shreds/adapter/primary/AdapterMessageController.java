package ai.shreds.adapter.primary;

import ai.shreds.application.ports.ApplicationMessageInputPort;
import ai.shreds.shared.dtos.SharedMessageRequestDTO;
import ai.shreds.shared.dtos.SharedMessageResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class AdapterMessageController {
    private final ApplicationMessageInputPort applicationMessageInputPort;

    @PostMapping
    public ResponseEntity<SharedMessageResponseDTO> saveMessage(@RequestBody SharedMessageRequestDTO request) {
        SharedMessageResponseDTO response = applicationMessageInputPort.saveMessage(request);
        return ResponseEntity.ok(response);
    }
}
