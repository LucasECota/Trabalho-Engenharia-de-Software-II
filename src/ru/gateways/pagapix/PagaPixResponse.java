package ru.gateways.pagapix;

public record PagaPixResponse(Status status, String chargeId, String declineReason) { }
