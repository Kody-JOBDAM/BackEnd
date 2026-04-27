package com.example.kodyjobdam.common.service;

import com.example.kodyjobdam.User.User;
import com.example.kodyjobdam.common.entity.CommonEntity;
import com.example.kodyjobdam.common.entity.StateEnum;
import com.example.kodyjobdam.common.repository.CommonRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommonServiceTest {

    @Mock
    private CommonRepository commonrepository;

    @InjectMocks
    private CommonService commonService;

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
