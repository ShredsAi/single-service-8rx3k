package ai.shreds.shared.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedMessageResponseDTO {

    @NotNull(message = "MySQL ID cannot be null")
    private Long mysqlId;

    @NotNull(message = "MongoDB ID cannot be null")
    private String mongoId;
}
