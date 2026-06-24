package org.example.service;

import java.time.LocalTime;

public interface CauHinhService {
    LocalTime getGioVaoSang();
    void setGioVaoSang(LocalTime time);
    
    LocalTime getGioRaChieu();
    void setGioRaChieu(LocalTime time);
    
    int getSoPhutDiMuonChoPhep();
    void setSoPhutDiMuonChoPhep(int minutes);
}