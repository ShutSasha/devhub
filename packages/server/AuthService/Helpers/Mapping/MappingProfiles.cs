using AuthService.Dtos;
using AuthService.Models;
using AutoMapper;

namespace AuthService.Helpers.Mapping;

public class MappingProfiles : Profile
{
   public MappingProfiles()
   {
      CreateMap<UserDto, User>().ReverseMap();
   }
}