package com.room.reservation.system.global.error.exception;


import com.room.reservation.system.global.error.model.ErrorCode;

public class BadRequestException extends ReservationException {
	public BadRequestException(ErrorCode errorCode) {
		super(errorCode);
	}
}
