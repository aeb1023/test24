package edu.sru.schedule.scheduleadvisement.secure;

public class UnauthenticatedUserException extends RuntimeException {
    public UnauthenticatedUserException() {
        super("User is not authenticated");
    }
}

