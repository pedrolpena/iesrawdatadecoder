/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rawdatadecoder;

/**
 *
 * @author pedro
 */
public class DeckBoxSignal {
   private  String  frequency = "",
            previousFrequency = "",
            nextFrequency = "";
    private double  deckboxTimerValue = 0.0,
            previousDeckboxTimerValue = 0.0,
            nextDeckboxTimerValue = 0;
    private long    computerTimeStamp = 0,
            previousComputerTimeStamp = 0,
            nextComputerTimeStamp = 0;
    private boolean isMSB = false,
            isFirstFrequencyReceived = false,
            isNoise = false,
            isUDB = true;
    private int position = 999;
    
    public void setUDB(boolean db){
    
        isUDB = db;
    }//end
    
    public void setPosition(String p){
        position = new Integer(p).intValue();
    }//end
    public void setFrequency(String f){
        frequency = f;
    }//end set
            
    public void setPreviousFrequency(String f){
        previousFrequency = f;
    }//end set
    
    public void setNextFrequency(String f){
        nextFrequency = f;
    }//end set
    
    public void setDeckboxTimerValue(String t){
        
        if(isUDB){
            deckboxTimerValue = new Double(t).doubleValue()/1000;            
        }
        else{
           
            deckboxTimerValue = new Double(t).doubleValue();
        }
    }//end set
         
    public void setPreviousDeckboxTimerValue(String t){
        previousDeckboxTimerValue = new Double(t).doubleValue();
    }//end set
    public void setNextDeckboxTimerValue(String t){
        nextDeckboxTimerValue = new Double(t).doubleValue();
    }//end set    
      
    public void setComputerTimeStamp(String t){
        computerTimeStamp = new Long(t).longValue();
    }//end set
   
    public void setPreviousComputerTimeStamp(String t){
        previousComputerTimeStamp = new Long(t).longValue();
    }//end set    
    
        
    public void setNextComputerTimeStamp(String t){        
        nextComputerTimeStamp = new Long(t).longValue();
    }//end set
    
    public void setMSB(boolean b){
        isMSB = b;
    }//end set     
    public void setFirstFrequencyReceived(boolean b){
        isFirstFrequencyReceived = b;
    }//end set   
    public void setNoise(boolean b){
        isNoise = b;
    }//end isNoise
    
    
    public String getFrequency(){
        return frequency;
    }//end set
            
    public String getPreviousFrequency(){
        return previousFrequency;
    }//end set
    
    public String getNextFrequency(){
        return nextFrequency;
    }//end set
    
    public double getDeckboxTimerValue(){
        return deckboxTimerValue;
    }//end set
         
    public double getPreviousDeckboxTimerValue(){
        return previousDeckboxTimerValue;
    }//end set
      
    public long getComputerTimeStamp(){
        return computerTimeStamp;
        
    }//end set
    
    public long getPreviousComputerTimeStamp(){
        return previousComputerTimeStamp;
    }//end set 
    
    public long getNextComputerTimeStamp(){
        return nextComputerTimeStamp;
    }//end seto
    
    public double getNextDeckboxTimerValue(){
        return nextDeckboxTimerValue;
    }//end set    
    
    public boolean isMSB(){
        return isMSB;
    }//end set   
    public boolean isLSB(){
       return getFrequency().matches("0") && !isMSB && !isNoise();
    }//end
    
   public boolean isMarker(){
       return ( isMSB() || isLSB() ) && !isNoise();
   }
    
    public boolean isFirstFrequencyReceived(){
        return isFirstFrequencyReceived;
    }//end set /**
    
    public boolean isNoise(){
        return isNoise;
    }// end isNoise
    
    public boolean isUDB(){
        return isUDB;
    }//end
    public int getPosition(){
        return position;
    }//end
    
}
