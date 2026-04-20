package com.example.kodyjobdam.common.controller;

import com.example.kodyjobdam.common.dto.request.CancelDTO;
import com.example.kodyjobdam.common.dto.request.CreateDTO;
import com.example.kodyjobdam.common.dto.request.LockDTO;
import com.example.kodyjobdam.common.dto.response.StudentReadDTO;
import com.example.kodyjobdam.common.dto.response.TeacherReadDTO;
import com.example.kodyjobdam.common.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/common")
@RestController
@RequiredArgsConstructor
public class CommonController {

    private final CommonService commonService;

    @PostMapping("/create/reservation") //뒤에 reservation다 지워버리고 싶다
    public ResponseEntity<?> createReservation(@RequestBody CreateDTO dto) {
        commonService.createReservation(dto, 1L);
        return ResponseEntity.ok().body("선생님께서 요청 검토중 입니다.");
    }

    @PatchMapping("/cancel/{id}/reservation")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id) {
        commonService.cancelReservation(id, 1L);
        return ResponseEntity.ok().body("취소되었습니다.");
    }

    @PatchMapping("/teacher/{id}/allow") //이거 선생님 쪽으로 가야함
    public ResponseEntity<?> reservationAllow(@PathVariable Long id) {//
        commonService.allow(id, 1L);
        return ResponseEntity.ok().body("요청을 수락했습니다.");
    }

    @PostMapping("/teacher/rock")
    public ResponseEntity<?> teacherRock(@RequestBody LockDTO dto) {
        commonService.teacherRock(dto);
        return ResponseEntity.ok().body("해당 시간을 잠궜습니다.");
    }


    @GetMapping("/read")
    public List<StudentReadDTO> S_read(String role) {
        return commonService.studentRead(role, 1L);
    }

    @GetMapping("/teacher/read")
    public List<TeacherReadDTO> T_read(String role) {
        return commonService.teacherRead(role, 1L);
    }


}