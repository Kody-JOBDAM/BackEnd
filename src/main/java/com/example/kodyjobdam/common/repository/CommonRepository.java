package com.example.kodyjobdam.common.repository;


import com.example.kodyjobdam.User.UserEntity;
import com.example.kodyjobdam.common.entity.CommonEntity;
import com.example.kodyjobdam.common.entity.StateEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommonRepository extends JpaRepository<CommonEntity, Long> {

    List<CommonEntity> findAllByDateAndPeriod(LocalDate date, String period);

    List<CommonEntity> findByUser_id(Long userId); //?

    List<CommonEntity> findByStateIn(List<StateEnum> state);
}