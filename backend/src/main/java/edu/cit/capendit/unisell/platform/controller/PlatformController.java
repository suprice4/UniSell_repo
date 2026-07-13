package edu.cit.capendit.unisell.platform.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import edu.cit.capendit.unisell.platform.dto.PlatformRequest;
import edu.cit.capendit.unisell.platform.dto.PlatformResponse;
import edu.cit.capendit.unisell.platform.service.PlatformService;
import edu.cit.capendit.unisell.platform.service.PlatformService.PlatformNotFoundException;

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
    public ResponseEntity<?> createPlatform(@RequestBody PlatformRequest request, Authentication authentication) {
        try {
            PlatformResponse response = platformService.createPlatform(request, authentication.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePlatform(@PathVariable Long id,
                                             @RequestBody PlatformRequest request,
                                             Authentication authentication) {
        try {
            PlatformResponse response = platformService.updatePlatform(id, request, authentication.getName());
            return ResponseEntity.ok(response);
        } catch (PlatformService.PlatformNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlatform(@PathVariable Long id, Authentication authentication) {
        try {
            platformService.deletePlatform(id, authentication.getName());
            return ResponseEntity.noContent().build();
        } catch (PlatformService.PlatformNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}