package com.example.kodyjobdam.common.controller;

import com.example.kodyjobdam.common.dto.request.CreateDTO;
import com.example.kodyjobdam.common.dto.request.LockDTO;
import com.example.kodyjobdam.common.dto.response.StudentReadDTO;
import com.example.kodyjobdam.common.dto.response.TeacherReadDTO;
import com.example.kodyjobdam.common.service.CommonService;
import com.example.kodyjobdam.user.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Common", description = "공통 API")
@RequestMapping("/common")
@RestController
@RequiredArgsConstructor
public class CommonController {

    private final CommonService commonService;

    SecurityUtil securityUtil;

    @Operation(summary = "예약 생성", description = "새로운 예약을 생성합니다. 성공 시 '선생님께서 요청 검토중 입니다.' 메시지를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공"),
            @ApiResponse(responseCode = "409", description = "이미 예약된 시간이거나 동일 사용자의 다른 예약이 존재"),
            @ApiResponse(responseCode = "423", description = "잠긴 시간대")
    })
    @PostMapping("/create/reservation")
    public ResponseEntity<?> createReservation(@RequestBody CreateDTO dto) {
        commonService.createReservation(dto, securityUtil.getCurrentUserId());
        return ResponseEntity.ok().body("선생님께서 요청 검토중 입니다.");
    }

    @Operation(summary = "예약 취소", description = "기존 예약을 취소합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 취소 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "취소할 예약을 찾을 수 없음")
    })
    @PatchMapping("/cancel/{id}/reservation")
    public ResponseEntity<?> cancelReservation(@Parameter(description = "예약 ID") @PathVariable Long id) {
        commonService.cancelReservation(id, securityUtil.getCurrentUserId());
        return ResponseEntity.ok().body("취소되었습니다.");
    }

    @Operation(summary = "예약 수락 (선생님)", description = "선생님이 학생의 예약 요청을 수락합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 수락 성공"),
            @ApiResponse(responseCode = "404", description = "수락할 예약을 찾을 수 없거나 이미 취소된 예약")
    })
    @PatchMapping("/teacher/{id}/allow")
    public ResponseEntity<?> reservationAllow(@Parameter(description = "예약 ID") @PathVariable Long id) {
        commonService.allow(id, securityUtil.getCurrentUserId());
        return ResponseEntity.ok().body("요청을 수락했습니다.");
    }

    @Operation(summary = "시간 잠금 (선생님)", description = "선생님이 특정 시간을 잠급니다. 해당 시간에 기존 예약이 있다면 취소 처리됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "시간 잠금 성공")
    })
    @PostMapping("/teacher/rock")
    public ResponseEntity<?> teacherRock(@RequestBody LockDTO dto) {
        commonService.teacherRock(dto);
        return ResponseEntity.ok().body("해당 시간을 잠궜습니다.");
    }

    @Operation(summary = "학생 예약 조회", description = "특정 학생의 예약 목록을 조회합니다. (현재는 ID 로만 조회)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
    })
    @GetMapping("/read")
    public List<StudentReadDTO> S_read() {
        return commonService.S_Read(securityUtil.getCurrentUserId());
    }

    @Operation(summary = "선생님 예약 조회", description = "특정 선생님에게 접수된 예약 목록을 조회합니다. (현재는 ID 로만 조회)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
    })
    @GetMapping("/teacher/read")
    public List<TeacherReadDTO> T_read() {
        return commonService.T_Read(securityUtil.getCurrentUserId());
    }
}