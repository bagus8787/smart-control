package com.example.smart_control.services;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ServiceConfigurationError;

public class CekConnectionService {

    private InetAddress giriAddress;

    public void cek_connection()
    {
        try {
            this.giriAddress=InetAddress.getByName("www.google.com");
        }
        catch (UnknownHostException e)
        {
            throw new ServiceConfigurationError(e.toString(),e);
        }
    }
}
