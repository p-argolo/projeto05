package com.BaneseLabes.LocalSeguro.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class Authorization {
    private boolean registerVirtualCard;
    private BigDecimal loan;
    private BigDecimal pix;
    private BigDecimal ted;
    private BigDecimal banksplit;
    private boolean changePassword;

    public boolean canRegisterVirtualCard(){
        return this.registerVirtualCard;
    }
    public boolean canChangePassword(){
        return this.changePassword;
    }

    public Authorization(){}

    public boolean pixHasLimit(){
        return this.pix.compareTo(BigDecimal.valueOf(-1)) != 0;
    }
    public boolean loanHasLimit(){
        return this.loan.compareTo(BigDecimal.valueOf(-1)) != 0;
    }
    public boolean tedHasLimit(){
        return this.ted.compareTo(BigDecimal.valueOf(-1)) != 0;
    }
    public boolean bankSplitHasLimit(){
        return this.banksplit.compareTo(BigDecimal.valueOf(-1)) != 0;
    }
}

