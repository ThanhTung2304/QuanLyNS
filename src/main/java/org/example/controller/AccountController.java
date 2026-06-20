package org.example.controller;

import org.example.entity.Account;
import org.example.exception.BusinessException;
import org.example.security.SessionManager;
import org.example.service.AccountService;
import org.example.service.impl.AccountServiceImpl;

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
}
