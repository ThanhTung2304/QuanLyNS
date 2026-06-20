package org.example.security;

import org.example.entity.Account;

public final class SessionManager {
    private static SessionManager instance;

    private Account currentAccount;
    private long loginTimeMillis;

    private SessionManager() {
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void login(Account account) {
        this.currentAccount = account;
        this.loginTimeMillis = System.currentTimeMillis();
    }

    public void logout() {
        this.currentAccount = null;
        this.loginTimeMillis = 0;
    }

    public boolean isLoggedIn() {
        return currentAccount != null;
    }

    public Account getCurrentAccount() {
        return currentAccount;
    }

    public Account.Role getCurrentRole() {
        return currentAccount != null ? currentAccount.getVaiTro() : null;
    }

    public String getCurrentMaNv() {
        return currentAccount != null ? currentAccount.getMaNv() : null;
    }

    public long getLoginTimeMillis() {
        return loginTimeMillis;
    }
}
