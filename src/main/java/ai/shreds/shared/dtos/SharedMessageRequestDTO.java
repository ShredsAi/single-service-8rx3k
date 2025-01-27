package ai.shreds.shared.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedMessageRequestDTO {

    @NotBlank(message = "Message cannot be empty")
    @Size(max = 255, message = "Message cannot exceed 255 characters")
    private String message;
}
