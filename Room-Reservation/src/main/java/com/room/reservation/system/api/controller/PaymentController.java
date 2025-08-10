package com.room.reservation.system.api.controller;

import com.room.reservation.system.api.dto.payment.request.PaymentRequestDto;
import com.room.reservation.system.api.dto.payment.response.PaymentPendingResponseDto;
import com.room.reservation.system.api.dto.payment.PaymentStatusRequestDto;
import com.room.reservation.system.api.dto.payment.PaymentStatusResponseDto;
import com.room.reservation.system.api.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "결제 관리", description = "비동기 결제 처리 및 상태 조회 API")
@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentController {
    
    private final PaymentService paymentService;

    @Operation(
        summary = "결제 처리",
        description = "예약에 대한 결제를 처리합니다. 즉시 PENDING 상태로 응답하며, 실제 결제는 비동기로 처리됩니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "결제 처리 요청 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "예약을 찾을 수 없음")
    })
                @PostMapping("/reservations/{reservationId}/payment")
            public ResponseEntity<PaymentPendingResponseDto> processPayment(
                @Parameter(description = "예약 ID", required = true, example = "1")
                @PathVariable("reservationId") Long reservationId,
        @Parameter(description = "결제 요청 정보", required = true)
        @Valid @RequestBody PaymentRequestDto request
    ) {
        PaymentPendingResponseDto response = paymentService.processPayment(reservationId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "결제 상태 조회",
        description = "결제 ID로 결제 상태를 조회합니다. 사용자 인증 정보가 필요합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "결제 상태 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "결제를 찾을 수 없음")
    })
                @PostMapping("/payments/{paymentId}/status")
            public ResponseEntity<PaymentStatusResponseDto> getPaymentStatus(
                @Parameter(description = "결제 ID", required = true, example = "1")
                @PathVariable("paymentId") Long paymentId,
        @Parameter(description = "사용자 인증 정보", required = true)
        @Valid @RequestBody PaymentStatusRequestDto request
    ) {
        PaymentStatusResponseDto response = paymentService.getPaymentStatus(paymentId, request);
        return ResponseEntity.ok(response);
    }
} 