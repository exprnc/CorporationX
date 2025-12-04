package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.entity.user.UserSkillGuarantee;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = UserMapper.class)
public interface UserSkillGuaranteeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "recommendation.receiver", target = "user")
    @Mapping(source = "recommendation.author", target = "guarantor")
    UserSkillGuarantee skillOfferToGuarantee(SkillOffer skillOffer);

    @Mapping(source = "guarantor", target = ".")
    UserDto getGuarantorDto(UserSkillGuarantee guarantee);

    List<UserDto> getGuarantors(List<UserSkillGuarantee> guarantees);
}
