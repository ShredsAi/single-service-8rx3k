package ai.shreds.infrastructure.external_services;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class InfrastructureReservationServiceClient {

    private final WebClient webClient;

    @Value("${external.service.reservation.path:/api/reservations}")
    private String reservationPath;

    public Mono<String> makeReservation(String reservationData) {
        return webClient.post()
                .uri(reservationPath)
                .bodyValue(reservationData)
                .retrieve()
                .bodyToMono(String.class);
    }
}
