package com.jordanrobin.financial_erp.shared.exception.auth;

public class InvalidInvitationTokenException extends RuntimeException {
    public InvalidInvitationTokenException() {
        super("Token d'invitation déjà utilisé ou dont la date d'expiration est passée.");
    }
}
