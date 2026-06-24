package org.example.controller;

import org.example.entity.Account;
import org.example.exception.BusinessException;
import org.example.exception.ValidationException;
import org.example.security.SessionManager;
import org.example.service.AccountService;
import org.example.service.impl.AccountServiceImpl;

import java.util.List;

public class AccountController {
    private final AccountService accountService;

    public AccountController() {
        this(new AccountServiceImpl());
    }

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    public Account login(String tenDangNhap, String matKhau) throws BusinessException {
        Account account = accountService.login(tenDangNhap, matKhau);
        SessionManager.getInstance().login(account);
        return account;
    }

    public void logout() {
        SessionManager.getInstance().logout();
    }

    public boolean isLoggedIn() {
        return SessionManager.getInstance().isLoggedIn();
    }

    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    public Account createAccount(String maNv, String tenDangNhap, String matKhau, Account.Role vaiTro)
            throws ValidationException, BusinessException {
        return accountService.createAccount(maNv, tenDangNhap, matKhau, vaiTro);
    }
    
    public void resetPassword(Integer maTk, String matKhauMoi) throws ValidationException, BusinessException {
        accountService.resetPassword(maTk, matKhauMoi);
    }

    public void lockAccount(Integer maTk) throws BusinessException {
        accountService.lockAccount(maTk);
    }

    public void unlockAccount(Integer maTk) throws BusinessException {
        accountService.unlockAccount(maTk);
    }
    
    public void changeRole(Integer maTk, Account.Role newRole) throws BusinessException {
        accountService.changeRole(maTk, newRole);
    }
}