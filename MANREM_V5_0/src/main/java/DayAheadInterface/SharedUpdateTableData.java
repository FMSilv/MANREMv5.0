/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DayAheadInterface;

/**
 *
 * @author Filipe
 */
public class SharedUpdateTableData {
    
    public static int changeFlag = 0;
    public static int tableChanges = 0;
    
    public static String oldPeriod;
    public static String oldPrice;
    public static String oldPower;
    public static String newPeriod;
    public static String newPrice;
    public static String newPower;

    public static String[][] OldSharedTableData;
    public static String[][] NewSharedTableData;
    public static String[][] TableDataChanges;
    
    
    public static void setNewSharedTableData( String[][] OldSharedTableData ){
        SharedUpdateTableData.NewSharedTableData = OldSharedTableData;
    }
    
    public static void setOldSharedTableData( String[][] OldSharedTableData ){
        SharedUpdateTableData.OldSharedTableData = OldSharedTableData;
    }
    
    public static void updateNewSharedTableData(int i, String price, String power){
        NewSharedTableData[i][1] = price;
        NewSharedTableData[i][2] = power;
    }
    
    public static void updateTableDataChanges(int tablechanges, String period, String price, String power){
        TableDataChanges[tablechanges-1][0] = period;
        TableDataChanges[tablechanges-1][1] = price;
        TableDataChanges[tablechanges-1][2] = power;
    }
    
    public static void setchangeFlag( int changeFlag ){
        SharedUpdateTableData.changeFlag = changeFlag;
    }
    
    public static void setOldPeriod( String Period ){
        SharedUpdateTableData.oldPeriod = Period;
    }
    
    public static void setOldPrice( String Price ){
        SharedUpdateTableData.oldPrice = Price;
    }
    
    public static void setOldPower( String Power ){
        SharedUpdateTableData.oldPower = Power;
    }
    
    public static void setNewPeriod( String Period ){
        SharedUpdateTableData.newPeriod = Period;
    }
    
    public static void setNewPrice( String Price ){
        SharedUpdateTableData.newPrice = Price;
    }
    
    public static void setNewPower( String Power ){
        SharedUpdateTableData.newPower = Power;
    }
    
    public static String[][] getOldSharedTableData(){
        return OldSharedTableData;
    }
    
    public static String[][] getNewSharedTableData(){
        return NewSharedTableData;
    }
    
    public static String[][] getTableDataChanges(){
        return TableDataChanges;
    }
    
    public static int getchangeFlag(){
        return changeFlag;
    }
    
    public static String getOldPeriod(){
        return oldPeriod;
    }
    
    public static String getOldPrice(){
        return oldPrice;
    }
    
    public static String getOldPower(){
        return oldPower;
    }
    
    public static String getNewPeriod(){
        return newPeriod;
    }
    
    public static String getNewPrice(){
        return newPrice;
    }
    
    public static String getNewPower(){
        return newPower;
    }
 

    
    
}
