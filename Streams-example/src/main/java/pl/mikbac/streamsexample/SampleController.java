package pl.mikbac.streamsexample;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by MikBac on 17.05.2025
 */

@RestController
@RequiredArgsConstructor
public class SampleController {

    private final SampleService sampleService;

    @PostMapping("/test")
    public ResponseEntity<Void> sendSample() {
        sampleService.sendMessage();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
