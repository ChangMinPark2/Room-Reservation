package com.room.reservation.system.global.error.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ErrorCode {
	//FailNotFound 404 error
	FAIL_NOT_MEETING_ROOM("해당 회의실을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	FAIL_NOT_USER("해당 유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	
	//BadRequest 400 error
	INVALID_TIME_FORMAT("시간 입력은 00분 단위, 30분 단위로만 가능합니다.", HttpStatus.BAD_REQUEST),
	INVALID_TIME_RANGE("시작 시간은 종료 시간보다 이전이어야 합니다.", HttpStatus.BAD_REQUEST),
	PAST_TIME_RESERVATION("현재 시간 이후만 예약 가능합니다.", HttpStatus.BAD_REQUEST),
	NOT_TODAY_RESERVATION("오늘 날짜만 예약 가능합니다.", HttpStatus.BAD_REQUEST),
	ALREADY_RESERVED_TIME("이미 예약된 시간대입니다.", HttpStatus.BAD_REQUEST);

	private String message;
	private HttpStatus statusCode;
}
