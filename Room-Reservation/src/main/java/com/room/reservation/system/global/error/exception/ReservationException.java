package com.room.reservation.system.global.error.exception;

import com.room.reservation.system.global.error.model.ErrorCode;

public class ReservationException extends RuntimeException {
	private ErrorCode errorCode;

	public ReservationException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
