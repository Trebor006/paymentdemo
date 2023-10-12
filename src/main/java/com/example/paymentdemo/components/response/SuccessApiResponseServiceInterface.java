package com.example.paymentdemo.components.response;

public interface SuccessApiResponseServiceInterface {
    <T> ApiResponse createSuccessResponse(T data);
}
