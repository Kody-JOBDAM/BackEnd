package com.example.kodyjobdam.common.service;

import com.example.kodyjobdam.common.dto.request.CreateDTO;
import com.example.kodyjobdam.common.entity.CommonEntity;
import com.example.kodyjobdam.common.entity.KindEnum;
import com.example.kodyjobdam.common.entity.StateEnum;
import com.example.kodyjobdam.common.repository.CommonRepository;
import com.example.kodyjobdam.user.UserRepository;
import com.example.kodyjobdam.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommonServiceTest {

    @Mock
    private CommonRepository commonrepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommonService commonService;

    @Test
    @DisplayName("예약 생성 성공 - 해당 시간대에 예약이 없는 경우")
    void createReservation_Success() {
        // given
        Long userId = 1L;
        CreateDTO dto = new CreateDTO();
        dto.setDate(LocalDate.now());
        dto.setPeriod("1");
        dto.setTitle("테스트 예약");
        dto.setContent("테스트 내용");
        dto.setKind(KindEnum.COMMON);

        User user = new User();
        ReflectionTestUtils.setField(user, "id", userId);

        when(commonrepository.findAllByDateAndPeriod(dto.getDate(), dto.getPeriod())).thenReturn(List.of());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        commonService.createReservation(dto, userId);

        // then
        verify(commonrepository, times(1)).save(any(CommonEntity.class));
    }

    @Test
    @DisplayName("예약 생성 실패 - 사용자를 찾을 수 없는 경우")
    void createReservation_UserNotFound() {
        // given
        Long userId = 1L;
        CreateDTO dto = new CreateDTO();
        dto.setDate(LocalDate.now());
        dto.setPeriod("1");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResponseStatusException.class, () -> {
            commonService.createReservation(dto, userId);
        });
    }

    @Test
    @DisplayName("예약 생성 실패 - 날짜가 잠긴 경우")
    void createReservation_Locked() {
        // given
        Long userId = 1L;
        CreateDTO dto = new CreateDTO();
        dto.setDate(LocalDate.now());
        dto.setPeriod("1");

        User user = new User();
        ReflectionTestUtils.setField(user, "id", userId);

        CommonEntity lockedEntity = CommonEntity.builder()
                .state(StateEnum.LOCKED)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commonrepository.findAllByDateAndPeriod(dto.getDate(), dto.getPeriod())).thenReturn(List.of(lockedEntity));

        // when & then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            commonService.createReservation(dto, userId);
        });
        assertEquals(HttpStatus.LOCKED, exception.getStatusCode());
    }

    @Test
    @DisplayName("예약 생성 실패 - 누군가 이미 예약한 경우")
    void createReservation_AlreadyReserved() {
        // given
        Long userId = 1L;
        CreateDTO dto = new CreateDTO();
        dto.setDate(LocalDate.now());
        dto.setPeriod("1");

        User user = new User();
        ReflectionTestUtils.setField(user, "id", userId);

        CommonEntity reservedEntity = CommonEntity.builder()
                .state(StateEnum.RESERVED)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commonrepository.findAllByDateAndPeriod(dto.getDate(), dto.getPeriod())).thenReturn(List.of(reservedEntity));

        // when & then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            commonService.createReservation(dto, userId);
        });
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }

    @Test
    @DisplayName("예약 수락 성공")
    void allow_Success() {
        // given
        Long reservationId = 1L;
        Long teacherId = 2L;

        CommonEntity reservation = CommonEntity.builder()
                .reservation_id(reservationId)
                .state(StateEnum.WAITING)
                .build();

        when(commonrepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        // when
        commonService.allow(reservationId, teacherId);

        // then
        assertEquals(StateEnum.RESERVED, reservation.getState());
        assertEquals(teacherId, reservation.getTeacher_id());
        verify(commonrepository, times(1)).save(reservation);
    }

    @Test
    @DisplayName("예약 수락 실패 - 예약을 찾을 수 없는 경우")
    void allow_NotFound() {
        // given
        Long reservationId = 1L;
        Long teacherId = 2L;

        when(commonrepository.findById(reservationId)).thenReturn(Optional.empty());

        // when & then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            commonService.allow(reservationId, teacherId);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    @DisplayName("예약 수락 실패 - 이미 취소된 예약인 경우")
    void allow_AlreadyCancelled() {
        // given
        Long reservationId = 1L;
        Long teacherId = 2L;

        CommonEntity cancelledReservation = CommonEntity.builder()
                .reservation_id(reservationId)
                .state(StateEnum.CANCEL)
                .build();

        when(commonrepository.findById(reservationId)).thenReturn(Optional.of(cancelledReservation));

        // when & then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            commonService.allow(reservationId, teacherId);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    @DisplayName("예약 취소 성공 테스트")
    void cancelReservation_Success() {
        // given
        Long reservationId = 1L;
        Long userId = 1L;

        User user = new User();
        ReflectionTestUtils.setField(user, "id", userId);

        CommonEntity reservation = CommonEntity.builder()
                .reservation_id(reservationId)
                .user(user)
                .state(StateEnum.WAITING)
                .build();

        when(commonrepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        // when
        commonService.cancelReservation(reservationId, userId);

        // then
        assertEquals(StateEnum.CANCEL, reservation.getState());
        verify(commonrepository, times(1)).save(any(CommonEntity.class));
    }

    @Test
    @DisplayName("예약 취소 실패 - 권한 없음 (다른 사용자의 예약)")
    void cancelReservation_Forbidden() {
        // given
        Long reservationId = 1L;
        Long userId = 1L;
        Long otherUserId = 2L;

        User otherUser = new User();
        ReflectionTestUtils.setField(otherUser, "id", otherUserId);

        CommonEntity reservation = CommonEntity.builder()
                .reservation_id(reservationId)
                .user(otherUser)
                .state(StateEnum.WAITING)
                .build();

        when(commonrepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        // when & then
        assertThrows(ResponseStatusException.class, () -> {
            commonService.cancelReservation(reservationId, userId);
        });
    }
}
