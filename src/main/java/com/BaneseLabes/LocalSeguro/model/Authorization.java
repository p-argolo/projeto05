package com.BaneseLabes.LocalSeguro.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
public class Authorization {


    private boolean registerVirtualCard;
    private double loan;
    private double Pix;
    private double TED;
    private double BankSplit;
    private boolean changePassword;

    public boolean canRegisterVirtualCard(){
        return this.registerVirtualCard;
    }
    public boolean canChangePassword(){
        return this.changePassword;
    }

}

