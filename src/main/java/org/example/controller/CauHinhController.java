package org.example.controller;

import org.example.service.CauHinhService;
import org.example.service.impl.CauHinhServiceImpl;

import java.time.LocalTime;

public class CauHinhController {
    private final CauHinhService cauHinhService;

    public CauHinhController() {
        this.cauHinhService = new CauHinhServiceImpl();
    }

    public LocalTime getGioVaoSang() {
        return cauHinhService.getGioVaoSang();
    }

    public void setGioVaoSang(LocalTime time) {
        cauHinhService.setGioVaoSang(time);
    }

    public LocalTime getGioRaChieu() {
        return cauHinhService.getGioRaChieu();
    }

    public void setGioRaChieu(LocalTime time) {
        cauHinhService.setGioRaChieu(time);
    }

    public int getSoPhutDiMuonChoPhep() {
        return cauHinhService.getSoPhutDiMuonChoPhep();
    }

    public void setSoPhutDiMuonChoPhep(int minutes) {
        cauHinhService.setSoPhutDiMuonChoPhep(minutes);
    }
}