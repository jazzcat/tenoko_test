package com.woowacourse.ternoko.controller;

import com.woowacourse.ternoko.config.AuthenticationPrincipal;
import com.woowacourse.ternoko.dto.CrewResponse;
import com.woowacourse.ternoko.dto.request.CrewUpdateRequest;
import com.woowacourse.ternoko.service.CrewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/crews")
public class CrewController {

    private final CrewService crewService;

    @GetMapping("/me")
    public ResponseEntity<CrewResponse> findCrew(@AuthenticationPrincipal final Long crewId) {
        log.info("findCrew");
        return ResponseEntity.ok(crewService.findCrew(crewId));
    }

    @PatchMapping("/me")
    public ResponseEntity<Void> updateCrew(@AuthenticationPrincipal final Long crewId,
                                           @RequestBody final CrewUpdateRequest crewUpdateRequest) {
        log.info("updateCrew");
        crewService.partUpdateCrew(crewId, crewUpdateRequest);
        return ResponseEntity.ok().build();
    }
}
