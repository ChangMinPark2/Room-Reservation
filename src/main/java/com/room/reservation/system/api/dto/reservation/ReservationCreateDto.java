package com.room.reservation.system.api.dto.reservation;

import jakarta.validation.constraints.*;

import java.time.LocalTime;

public record ReservationCreateDto(
        @NotNull(message = "회의실 ID는 필수입니다.")
        Long meetingRoomId,

        @NotNull(message = "시작 시간은 필수입니다.")
        LocalTime startTime,

        @NotNull(message = "종료 시간은 필수입니다.")
        LocalTime endTime,

        @NotNull(message = "사용자 ID는 필수입니다.")
        Long userId,

        @NotBlank(message = "이름은 필수입니다.")
        @Size(min = 1, max = 20, message = "이름은 1자 이상 20자 이하여야 합니다.")
        String name,

        @NotBlank(message = "전화번호는 필수입니다.")
        @Pattern(regexp = "^01[0-9]-[0-9]{3,4}-[0-9]{4}$", message = "올바른 전화번호 형식이 아닙니다. (예: 010-1234-5678)")
        String phoneNumber
) {
}
