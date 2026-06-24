package org.example.service;

import org.example.entity.NghiPhep;
import org.example.exception.BusinessException;
import org.example.exception.ValidationException;

import java.util.List;

public interface NghiPhepService {
    List<NghiPhep> getByMaNv(String maNv);
    List<NghiPhep> getAll();
    NghiPhep create(NghiPhep nghiPhep) throws ValidationException, BusinessException;
    void delete(Integer maNp) throws BusinessException;
    void approve(Integer maNp, String maNguoiDuyet) throws BusinessException;
    void reject(Integer maNp, String maNguoiDuyet) throws BusinessException;
}