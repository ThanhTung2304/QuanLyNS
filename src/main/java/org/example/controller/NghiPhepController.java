package org.example.controller;

import org.example.entity.NghiPhep;
import org.example.exception.BusinessException;
import org.example.exception.ValidationException;
import org.example.service.NghiPhepService;
import org.example.service.impl.NghiPhepServiceImpl;

import java.util.List;

public class NghiPhepController {

    private final NghiPhepService nghiPhepService;

    public NghiPhepController() {
        this.nghiPhepService = new NghiPhepServiceImpl();
    }

    public List<NghiPhep> getByMaNv(String maNv) {
        return nghiPhepService.getByMaNv(maNv);
    }

    public List<NghiPhep> getAll() {
        return nghiPhepService.getAll();
    }

    public NghiPhep create(NghiPhep nghiPhep) throws ValidationException, BusinessException {
        return nghiPhepService.create(nghiPhep);
    }

    public void delete(Integer maNp) throws BusinessException {
        nghiPhepService.delete(maNp);
    }

    public void approve(Integer maNp, String maNguoiDuyet) throws BusinessException {
        nghiPhepService.approve(maNp, maNguoiDuyet);
    }

    public void reject(Integer maNp, String maNguoiDuyet) throws BusinessException {
        nghiPhepService.reject(maNp, maNguoiDuyet);
    }
}