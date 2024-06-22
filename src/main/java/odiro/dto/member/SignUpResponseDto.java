package odiro.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import odiro.domain.Member;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpResponseDto {
    private String email;
    private String nickname;


    public static SignUpResponseDto toDto(Member member) {
        return new SignUpResponseDto(member.getEmail(), member.getNickname());
    }
}
