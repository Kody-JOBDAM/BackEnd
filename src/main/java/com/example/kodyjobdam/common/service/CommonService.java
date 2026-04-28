package com.example.kodyjobdam.common.service;


import com.example.kodyjobdam.common.dto.request.CreateDTO;
import com.example.kodyjobdam.common.dto.request.LockDTO;
import com.example.kodyjobdam.common.dto.response.StudentReadDTO;
import com.example.kodyjobdam.common.dto.response.TeacherReadDTO;
import com.example.kodyjobdam.common.entity.CommonEntity;
import com.example.kodyjobdam.common.entity.StateEnum;
import com.example.kodyjobdam.common.repository.CommonRepository;
import com.example.kodyjobdam.user.UserRepository;
import com.example.kodyjobdam.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommonService {

    private final CommonRepository commonRepository;

    private final UserRepository userRepository;

    public void commonSave(CommonEntity entity) {
        commonRepository.save(entity);
    }

    public void createReservation(CreateDTO dto, Long id) {

        //여기에 나중에 토큰에서 빼온 id를 매개변수로
        List<CommonEntity> CreateList = commonRepository.findAllByDateAndPeriod(dto.getDate(), dto.getPeriod()); //여기 state확인 해야함. RESERVED인가

        User userId = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원이 없습니다."));

        if (CreateList.isEmpty()) {
            commonSave(dto.toEntity(userId));
        }
        else {
            for (CommonEntity entity : CreateList) {
                if(entity.getState() == StateEnum.LOCKED) {
                    throw new ResponseStatusException(HttpStatus.LOCKED, "잠긴 날짜 입니다.");
                }
                if (
                        entity.getReservation_id().equals(id) &&
                        entity.getState() != StateEnum.CANCEL) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 예약한 시간입니다.");
                }
                if (entity.getState() == StateEnum.RESERVED) {

                    throw new ResponseStatusException(HttpStatus.CONFLICT, "누군가 예약한 시간입니다.");
                }
            }
        }
    }

    public void cancelReservation(Long reservationId, Long userId) {

        CommonEntity entity = commonRepository.findById(reservationId)
                .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "취소 할 수 없습니다."));

        if (!entity.getUser().getId().equals(userId)) { // ???
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }

        entity.setState(StateEnum.CANCEL);

        commonSave(entity);
    }

    public void allow(Long reservationId, Long teacherId) {
        CommonEntity entity = commonRepository.findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "값을 찾을 수 없습니다."));

        if(entity.getState() == StateEnum.CANCEL) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "이미 취소된 에약입니다.");
        }

        entity.setState(StateEnum.RESERVED);
        entity.setTeacher_id(teacherId);

        commonSave(entity);
    }

    public void teacherRock(LockDTO dto) {
        List<CommonEntity> rockList = commonRepository.findAllByDateAndPeriod(dto.getDate(), dto.getPeriod());

        if(rockList.isEmpty()) {
           commonSave(dto.toEntity(dto));
        }
        for(CommonEntity entity : rockList) {
            if(entity.getState() == StateEnum.CANCEL) {
                continue;
            }
            entity.setState(StateEnum.CANCEL);

        }

        commonSave(dto.toEntity(dto));
    }

    public List<TeacherReadDTO> teacherRead(String role, Long id) { //
        List<CommonEntity> entity = commonRepository.findByUser_id(id);

        return entity.stream()
                .map(e -> new TeacherReadDTO(
                        e.getReservation_id(),
                        e.getUser().getName(),
                        e.getDate(),
                        e.getPeriod()
                ))
                .toList();
    }

    public List<StudentReadDTO> studentRead(String role, Long id) { //그냥 user_id를 찾아서 맞는거
        List<CommonEntity> entity = commonRepository.findByUser_id(id);

        return entity.stream()
                .map(e -> new StudentReadDTO(
                        e.getReservation_id(),
                        e.getUser().getName(),
                        e.getDate(),
                        e.getPeriod()
                ))
                .toList();
    }
}