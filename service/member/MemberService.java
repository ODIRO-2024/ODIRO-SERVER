package odiro.service.member;

import lombok.RequiredArgsConstructor;
import odiro.config.jwt.JwtToken;
import odiro.config.jwt.JwtTokenDto;
import odiro.domain.member.Authority;
import odiro.dto.member.SignInRequestDto;
import odiro.dto.member.SignUpDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import odiro.domain.member.Member;
import odiro.repository.member.MemberRepository;

import java.time.Duration;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Long join(Member member) {
        memberRepository.save(member);
        return member.getId();
    }

    public Optional<Member> findById(Long memberId) {
        return memberRepository.findById(memberId);
    }
    @Transactional
    public Member signUp(SignUpDto signUpDto) {
        signUpDto.setAuthority(Authority.valueOf("ROLE_USER"));
        //email과 닉네임에 대한 중복 확인
//        Optional <Member> memberEmail = memberRepository.findByEmail(signUpDto.getEmail());
//        Optional<Member> memberNickname = memberRepository.findByNickname(signUpDto.getNickname());
//        if (memberEmail.isPresent()) {
//            throw new DuplicateEmailException();
//        }
//
//        if (memberNickname.isPresent()) {
//            throw new DuplicateNicknameException();
//        }
        Member member = signUpDto.toEntity();
        member.passwordEncoding(passwordEncoder);
        memberRepository.save(member);
        return member;
    }
//    @Transactional
//    public JwtTokenDto login(SignInRequestDto signInRequestDto) {
//
//    }
}