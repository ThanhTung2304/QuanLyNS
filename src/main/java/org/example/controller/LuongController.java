package org.example.controller;

import org.example.entity.BangLuong;
import org.example.exception.BusinessException;
import org.example.service.LuongService;
import org.example.service.impl.LuongServiceImpl;

import java.util.List;
import java.util.Optional;

public class LuongController {

    private final LuongService luongService;

    public LuongController() {
        this.luongService = new LuongServiceImpl();
    }

    public List<BangLuong> calculatePayroll(int thang, int nam) throws BusinessException {
        return luongService.calculatePayroll(thang, nam);
    }

    public void finalizePayroll(List<BangLuong> bangLuongList) throws BusinessException {
        luongService.finalizePayroll(bangLuongList);
    }

    public List<BangLuong> findPayrollHistory(int thang, int nam) {
        return luongService.findPayrollHistory(thang, nam);
    }

    public Optional<BangLuong> findMyPayroll(String maNv, int thang, int nam) {
        return luongService.findMyPayroll(maNv, thang, nam);
    }

    public void reopenPayroll(int thang, int nam) throws BusinessException {
        luongService.reopenPayroll(thang, nam);
    }
}