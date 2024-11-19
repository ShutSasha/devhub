using AutoMapper;
using UserService.Dto;
using UserService.Models.User;

namespace UserService.Helpers.Mapping;

public class MappingProfiles : Profile
{
   public MappingProfiles()
   {
      CreateMap<UserDto, User>().ReverseMap();
   }
}