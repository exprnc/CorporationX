package school.faang.user_service.controller.skill;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@Validated
@RequestMapping("api/v1/")
@RequiredArgsConstructor
@RestController
public class SkillController {

    private final SkillService skillService;
    private final UserContext userContext;

    @PostMapping("/skills")
    public ResponseEntity<SkillDto> create(@RequestBody @Valid CreateSkillDto skillDto) {
        return ResponseEntity.ok(skillService.create(skillDto));
    }

    @GetMapping("users/{userId}/skills")
    public ResponseEntity<List<SkillDto>> getByUserId(@PathVariable long userId) {
        return ResponseEntity.ok(skillService.getByUserId(userId));
    }

    @GetMapping("/skills/offered")
    public ResponseEntity<List<SkillCandidateDto>> getOfferedSkills() {
        var userId = userContext.getUserId();
        return ResponseEntity.ok(skillService.getOfferedSkills(userId));
    }

    @PostMapping("/skills/{skillId}/acquire")
    public ResponseEntity<Void> acquireSkillFromOffers(@PathVariable long skillId) {
        var userId = userContext.getUserId();
        skillService.acquireSkillFromOffers(skillId, userId);
        return ResponseEntity.ok().build();
    }

}
