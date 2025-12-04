package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.user.UserSkillGuarantee;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.mapper.UserSkillGuaranteeMapper;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserSkillGuaranteeRepository;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SkillServiceImpl implements SkillService {

    private final UserService userService;
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillMapper skillMapper;
    private final UserSkillGuaranteeMapper userSkillGuaranteeMapper;

    @Value("${skill.offers.min.count}")
    private int skillOffersMinCount;

    @Transactional
    @Override
    public SkillDto create(CreateSkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.title())) {
            log.error("Скилл с названием {} уже существует.", skillDto.title());
            throw new DataValidationException("Скилл с таким названием уже существует");
        }
        var savedSkill = skillRepository.save(skillMapper.toSkill(skillDto));
        log.info("Скилл с названием {} успешно создан.", savedSkill.getTitle());
        return skillMapper.toSkillDto(savedSkill);
    }

    @Transactional(readOnly = true)
    @Override
    public List<SkillDto> getByUserId(long userId) {
        userService.getById(userId);
        var skills = skillRepository.findAllByUserId(userId);
        return skillMapper.toSkillDtoList(skills);
    }

    @Transactional(readOnly = true)
    @Override
    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        userService.getById(userId);
        var skillDtoList = skillMapper.toSkillDtoList(skillRepository.findSkillsOfferedToUser(userId));
        return skillDtoList.stream()
                .map(skill -> new SkillCandidateDto(skill, skillOfferRepository.countAllOffersOfSkill(skill.id(), userId)))
                .toList();
    }

    @Transactional
    @Override
    public void acquireSkillFromOffers(long skillId, long userId) {
        userService.getById(userId);
        if (skillRepository.findUserSkill(skillId, userId).isPresent()) {
            log.error("У пользователя {} уже есть скилл {}", userId, skillId);
            throw new DataValidationException("У вас уже есть такой скилл");
        }
        var offersOfSkill = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        if (offersOfSkill.size() < skillOffersMinCount) {
            log.error("Минимальное количество рекомендаций для скилла {} должно быть {}", skillId, skillOffersMinCount);
            throw new DataValidationException("Не достигнуто минимальное количество рекомендаций для присвоения скила");
        }
        skillRepository.assignSkillToUser(skillId, userId);
        var userSkillGuaranties = offersOfSkill.stream()
                .map(userSkillGuaranteeMapper::skillOfferToGuarantee)
                .toList();
        userSkillGuaranteeRepository.saveAll(userSkillGuaranties);
    }

}
