package odiro.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import odiro.config.auth.PrincipalDetails;
import odiro.config.redis.RedisService;
import odiro.domain.DayPlan;
import odiro.domain.Memo;
import odiro.domain.member.Member;
import odiro.dto.dayPlan.DayPlanInDetailPage;
import odiro.dto.comment.CommentInDetailPage;
import odiro.dto.location.LocationInDetailPage;
import odiro.dto.member.*;
import odiro.dto.memo.MemoInDetailPage;
import odiro.dto.memo.PostMemoRequest;
import odiro.dto.memo.PostMemoResponse;
import odiro.dto.plan.EditPlanRequest;
import odiro.dto.plan.GetDetailPlanResponse;
import odiro.dto.plan.InitPlanRequest;
import odiro.dto.plan.InitPlanResponse;
import odiro.service.DayPlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import odiro.domain.Plan;
import odiro.service.PlanService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//@Controller +ResponseBody = RestController
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PlanController {

    private final PlanService planService;
    private final DayPlanService dayPlanService;
    private final RedisService redisService;

    @GetMapping("/home")
    public List<HomeResponse> homeForm( @AuthenticationPrincipal PrincipalDetails principalDetails) {

        List<Plan> planList = planService.findPlansByParticipantId(principalDetails.getMember().getId()); //memberId 1로 임시 지정
        return mapToHomeResponseList(planList);
    }

    @PostMapping("/plan/create")
    public InitPlanResponse initPlan(@RequestBody InitPlanRequest request, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Plan savedPlan = planService.initPlanV2(
                principalDetails.getMember().getId(), request);

        //DayPlan 생성
        LocalDateTime currentDateTime = request.getFirstDay();
        while (!currentDateTime.isAfter(request.getLastDay())) {
            dayPlanService.postDayPlan(savedPlan.getId(), currentDateTime);
            currentDateTime = currentDateTime.plusDays(1);
        }

        InitPlanResponse response = new InitPlanResponse(savedPlan.getId());

        return response;
    }

    @GetMapping("/plan/{planId}")
    public GetDetailPlanResponse getDetailPlan(@PathVariable("planId") Long planId) {

        Plan plan = planService.findById(planId).orElseThrow(() -> new RuntimeException("Plan not found with id " + planId));


        Member initializer = plan.getInitializer();
        InitializerInDetailPage initializerResponse = new InitializerInDetailPage(
                initializer.getId(), initializer.getUsername(), initializer.getEmail(), initializer.getProfileImage());

        List<Member> participants = planService.findParticipantsByPlanId(planId);
        List<MemberInDetailPage> memberResponses = participants.stream()
                .map(member -> new MemberInDetailPage(member.getId(), member.getUsername(), member.getEmail(), member.getProfileImage()))
                .collect(Collectors.toList());


        //Location, memo,, comment는 day별로 묶어서

        List<DayPlanInDetailPage> dayPlanResponses = plan.getDayPlans().stream()
                .map(dayPlan -> {
                    List<LocationInDetailPage> locations = dayPlan.getLocations().stream()
                            .map(location -> new LocationInDetailPage( location.getLocationOrder(),
                                    location.getId(), location.getAddressName(), location.getKakaoMapId(), location.getPhone(),
                                    location.getPlaceName(), location.getPlaceUrl(), location.getLat(), location.getLng(),
                                    location.getRoadAddressName(), location.getImgUrl(),location.getCategoryGroupName()))
                            .collect(Collectors.toList());

                    List<MemoInDetailPage> memos = dayPlan.getMemos().stream()
                            .map(memo -> new MemoInDetailPage(memo.getId(), memo.getContent()))
                            .collect(Collectors.toList());

                    List<CommentInDetailPage> comments = dayPlan.getComments().stream()
                            .map(comment -> new CommentInDetailPage(comment.getId(), comment.getWriter().getId(), comment.getContent(), comment.getWriteTime()))
                            .collect(Collectors.toList());

                    return new DayPlanInDetailPage(dayPlan.getId(), dayPlan.getDate(), locations, memos, comments);
                })
                .collect(Collectors.toList());

        GetDetailPlanResponse response = new GetDetailPlanResponse(
                plan.getId(), plan.getTitle(), plan.getFirstDay(), plan.getLastDay(), initializerResponse, memberResponses, dayPlanResponses
        );

        return response;
    }

    @PutMapping("/plan/edit")
    public ResponseEntity<InitPlanResponse> editPlan(@RequestBody EditPlanRequest request, @AuthenticationPrincipal PrincipalDetails principalDetails) {

        Plan updatedPlan = planService.editPlan(request.getId(), request.getTitle(), request.getFirstDay(), request.getLastDay(), principalDetails.getMember().getId());

        InitPlanResponse response = new InitPlanResponse(updatedPlan.getId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/plan/delete/{planId}")
    public ResponseEntity<Void> deleteMemo(@PathVariable("planId") Long planId, @AuthenticationPrincipal PrincipalDetails principalDetails) {

        planService.deletePlan(planId,principalDetails.getMember().getId());
        return ResponseEntity.noContent().build();
    }


    private List<HomeResponse> mapToHomeResponseList(List<Plan> planList) {

        List<HomeResponse> responses = new ArrayList<>();
        for (Plan plan : planList) {
            HomeResponse response = new HomeResponse(
                    plan.getId(), plan.getTitle(),plan.getFirstDay(), plan.getLastDay());
            responses.add(response);
        }
        return responses;
    }
}
//    @Operation(summary = "Plan 카테고리 선택", description = "Plan 카테고리 선택")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "OK")
//    })
//    @GetMapping(value = "/create")
//    public ResponseEntity<SignUpResponseDto> singUp (@RequestBody SignUpDto signUpDto){
//        Member member = memberService.signUp(signUpDto);
//        SignUpResponseDto res = SignUpResponseDto.toDto(member);
//        //response 전달을 위한 dto 변환
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(res);
//    }
//
//    @Operation(summary = "Plan 카테고리 수정", description = "Plan 카테고리 수정")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "OK")
//    })
//    @GetMapping(value = "/edit")
//    public ResponseEntity<SignUpResponseDto> singUp (@RequestBody SignUpDto signUpDto){
//        Member member = memberService.signUp(signUpDto);
//        SignUpResponseDto res = SignUpResponseDto.toDto(member);
//        //response 전달을 위한 dto 변환
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(res);
//    }

//    @PostMapping("/plan/create")
//    public InitPlanResponse initPlan(@RequestBody InitPlanData inputData) {
//
//        Member member = memberService.findById(inputData.getMemberId()).orElseThrow(() -> new RuntimeException("Member not found"));
//
//        Plan newPlan = new Plan(member, inputData.getTitle(), inputData.getFirstday(), inputData.getLastday());
//        Plan savedPlan = planService.initPlan(newPlan);
//
//        if (savedPlan != null) {
//            InitPlanResponse response = new InitPlanResponse(
//                    savedPlan.getId(), savedPlan.getInitializer().getId(), savedPlan.getTitle(), savedPlan.getFirstDay(), savedPlan.getLastDay());
//            return response;
//        } else {
//            log.error("Plan 저장 실패");
//        }
//    }