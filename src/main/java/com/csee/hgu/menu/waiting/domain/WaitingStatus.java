package com.csee.hgu.menu.waiting.domain;

public enum WaitingStatus {
    ISSUED,
    // 결제 완료 후 접수된 상태 (주방에서 확인)
    CONFIRMED,
    // 조리 완료 (손님 호출 가능)
    COOKED,
    // 손님 수령 완료 (완료 탭)
    PICKED_UP,
    CANCELLED
}

