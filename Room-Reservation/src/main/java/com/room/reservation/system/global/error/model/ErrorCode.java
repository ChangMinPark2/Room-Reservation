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
	FAIL_NOT_RESERVATION("해당 예약을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	FAIL_NOT_PAYMENT("해당 결제를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	
	//BadRequest 400 error
	INVALID_TIME_FORMAT("시간 입력은 00분 단위, 30분 단위로만 가능합니다.", HttpStatus.BAD_REQUEST),
	INVALID_TIME_RANGE("시작 시간은 종료 시간보다 이전이어야 합니다.", HttpStatus.BAD_REQUEST),
	PAST_TIME_RESERVATION("현재 시간 이후만 예약 가능합니다.", HttpStatus.BAD_REQUEST),
	NOT_TODAY_RESERVATION("오늘 날짜만 예약 가능합니다.", HttpStatus.BAD_REQUEST),
	ALREADY_RESERVED_TIME("이미 예약된 시간대입니다.", HttpStatus.BAD_REQUEST),
	FAIL_INVALID_USER("예약자 정보가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
	FAIL_ALREADY_PAID("이미 결제가 완료된 예약입니다.", HttpStatus.BAD_REQUEST),
	FAIL_NOT_PAYMENT_PROVIDER("해당 결제사를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	FAIL_INVALID_WEBHOOK("웹훅 검증에 실패했습니다.", HttpStatus.BAD_REQUEST);

	private String message;
	private HttpStatus statusCode;
}
