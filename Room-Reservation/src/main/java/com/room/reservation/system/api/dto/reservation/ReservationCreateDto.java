package com.room.reservation.system.api.dto.reservation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalTime;

@Schema(description = "예약 생성 요청 정보")
public record ReservationCreateDto(
        @Schema(description = "회의실 ID", example = "1", required = true)
        @NotNull(message = "회의실 ID는 필수입니다.")
        Long meetingRoomId,

        @Schema(description = "시작 시간", example = "16:30", required = true, type = "string", pattern = "HH:mm")
        @NotNull(message = "시작 시간은 필수입니다.")
        LocalTime startTime,

        @Schema(description = "종료 시간", example = "17:30", required = true, type = "string", pattern = "HH:mm")
        @NotNull(message = "종료 시간은 필수입니다.")
        LocalTime endTime,

        @Schema(description = "사용자 이름", example = "홍길동", required = true)
        @NotBlank(message = "이름은 필수입니다.")
        @Size(min = 1, max = 20, message = "이름은 1자 이상 20자 이하여야 합니다.")
        String name,

        @Schema(description = "전화번호", example = "010-1234-5678", required = true)
        @NotBlank(message = "전화번호는 필수입니다.")
        @Pattern(regexp = "^01[0-9]-[0-9]{3,4}-[0-9]{4}$", message = "올바른 전화번호 형식이 아닙니다. (예: 010-1234-5678)")
        String phoneNumber
) {
}
