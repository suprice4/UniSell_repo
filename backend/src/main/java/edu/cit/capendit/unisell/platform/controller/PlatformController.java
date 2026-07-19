package edu.cit.capendit.unisell.platform.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import edu.cit.capendit.unisell.platform.dto.PlatformRequest;
import edu.cit.capendit.unisell.platform.dto.PlatformResponse;
import edu.cit.capendit.unisell.platform.service.PlatformService;

import java.util.List;

@RestController
@RequestMapping("/api/platforms")
public class PlatformController {

    private final PlatformService platformService;

    public PlatformController(PlatformService platformService) {
        this.platformService = platformService;
    }

    @GetMapping
    public ResponseEntity<List<PlatformResponse>> getMyPlatforms(Authentication authentication) {
        return ResponseEntity.ok(platformService.getMyPlatforms(authentication.getName()));
    }

    @PostMapping
    public ResponseEntity<PlatformResponse> createPlatform(@RequestBody PlatformRequest request,
                                                             Authentication authentication) {
        PlatformResponse response = platformService.createPlatform(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlatformResponse> updatePlatform(@PathVariable Long id,
                                                             @RequestBody PlatformRequest request,
                                                             Authentication authentication) {
        PlatformResponse response = platformService.updatePlatform(id, request, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlatform(@PathVariable Long id, Authentication authentication) {
        platformService.deletePlatform(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}