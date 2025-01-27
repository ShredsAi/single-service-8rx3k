package ai.shreds.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedMessageResponseDTO {
    private Long mysqlId;
    private String mongoId;
}
