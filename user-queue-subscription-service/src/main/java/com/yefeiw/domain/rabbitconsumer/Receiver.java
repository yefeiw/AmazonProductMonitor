package com.yefeiw.domain.rabbitconsumer;


public interface Receiver {

    public void receiveMessage(byte[] message) throws Exception;

}