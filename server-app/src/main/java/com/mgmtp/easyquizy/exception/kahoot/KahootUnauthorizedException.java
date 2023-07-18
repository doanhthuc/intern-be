package com.mgmtp.easyquizy.exception.kahoot;

public class KahootUnauthorizedException extends KahootException {
    public KahootUnauthorizedException() {super("User is not logged into Kahoot");}
}