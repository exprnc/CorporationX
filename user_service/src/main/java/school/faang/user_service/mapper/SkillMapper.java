package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.user.Skill;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = UserSkillGuaranteeMapper.class)
public interface SkillMapper {

    Skill toSkill(CreateSkillDto skillDto);

    @Mapping(source = "guarantees", target = "guarantors")
    SkillDto toSkillDto(Skill skill);

    List<SkillDto> toSkillDtoList(List<Skill> skills);

}
