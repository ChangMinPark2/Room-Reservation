package com.room.reservation.system.global.error.exception;

import com.room.reservation.system.global.error.model.ErrorCode;

public class NotFoundException extends ReservationException {
	public NotFoundException(ErrorCode errorCode) {
		super(errorCode);
	}
}
