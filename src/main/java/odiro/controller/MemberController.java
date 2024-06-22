package odiro.controller;

import lombok.RequiredArgsConstructor;
import odiro.domain.Member;
import odiro.dto.member.SignUpDto;
import odiro.dto.member.SignUpResponseDto;
import odiro.repository.MemberRepository;
import odiro.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final MemberService memberService;

        @GetMapping(value={"","/"})
        public String dashboard() {
            return "/dashboard";
        }

        @GetMapping(value="/user")
        public String user() {
            return "/user";
        }

        @GetMapping(value="/manager")
        public String manager() {
            return "/manager";
        }

        @GetMapping(value="/admin")
        public String admin() {
            return "/admin";
        }

        @PostMapping(value = "/singup")
        public ResponseEntity<SignUpResponseDto> join (SignUpDto signUpDto){
            Member member = memberService.signUp(signUpDto);
            SignUpResponseDto res = SignUpResponseDto.toDto(member);
            //response 전달을 위한 dto 변환
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        }
}
