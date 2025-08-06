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
	FAIL_NOT_MEETING_ROOM("해당 회의실을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

	private String message;
	private HttpStatus statusCode;
}
