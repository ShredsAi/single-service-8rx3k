package ai.shreds.application.ports;

import ai.shreds.shared.dtos.SharedMessageRequestDTO;
import ai.shreds.shared.dtos.SharedMessageResponseDTO;

public interface ApplicationMessageInputPort {

    SharedMessageResponseDTO saveMessage(SharedMessageRequestDTO request);

}
