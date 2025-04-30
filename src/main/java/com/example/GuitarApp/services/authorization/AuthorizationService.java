package com.example.GuitarApp.services.authorization;

public interface AuthorizationService {

    boolean canDelete(int targetId);

    boolean canUpdate(int targetUserId);
}
