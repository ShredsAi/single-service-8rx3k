package ai.shreds.application.services;

import ai.shreds.application.exceptions.ApplicationMessageException;
import ai.shreds.application.ports.ApplicationMessageInputPort;
import ai.shreds.domain.services.DomainServiceMessage;
import ai.shreds.shared.dtos.SharedMessageRequestDTO;
import ai.shreds.shared.dtos.SharedMessageResponseDTO;
import ai.shreds.shared.utils.SharedMessageValidator;
import ai.shreds.domain.value_objects.DomainValueSavedIDs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationMessageService implements ApplicationMessageInputPort {
    private final DomainServiceMessage domainServiceMessage;
    private final ApplicationMessageException applicationMessageException;

    @Override
    public SharedMessageResponseDTO saveMessage(SharedMessageRequestDTO request) {
        try {
            SharedMessageValidator.validateMessage(request.getMessage());
            DomainValueSavedIDs savedIds = domainServiceMessage.saveMessage(request.getMessage());
            return new SharedMessageResponseDTO(savedIds.getMysqlId(), savedIds.getMongoId());
        } catch (Exception ex) {
            applicationMessageException.handleMessageValidationError(ex);
            return null; // This line will never be reached due to exception handling
        }
    }
}
