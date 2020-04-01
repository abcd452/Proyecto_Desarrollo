package controller;

import connection.DbManager;
import model.Bank;

public class DaoBank {

    private DbManager dbManager = new DbManager("postgres", "postgres", "MobilePlan", "localhost");
    public DaoBank() {
    }

    public String saveNewBank(String bankName, String accountNumber, String bankNIT){
        dbManager.openDBConnection();
        String response = dbManager.saveBank(bankName, accountNumber, bankNIT);
        dbManager.closeDBConnection();
        return  response;
    }
    public String editBank(boolean state, String bankNIT){
        dbManager.openDBConnection();
        String response = dbManager.setStateBank(state, bankNIT);
        dbManager.closeDBConnection();
        return  response;
    }

    public Bank loadBank(String bankName) {
        dbManager.openDBConnection();
        Bank bank = dbManager.loadBank(bankName);
        dbManager.closeDBConnection();
        return bank;
    }

    public String[] loadAllBanks(){
        dbManager.openDBConnection();
        String[] banks = dbManager.loadAllBank();
        dbManager.closeDBConnection();
        return banks;
    }
}
