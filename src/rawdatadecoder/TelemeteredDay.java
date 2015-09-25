/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rawdatadecoder;
import java.util.Date;
import java.util.Calendar;
import java.text.DecimalFormat;
import java.util.TimeZone;
/**
 *
 * @author pedro
 */
public class TelemeteredDay {
    private double deckBoxRolloverValue = 104.857;
    private Double timeConstants[] = {-.25, -14.5, -20.75, -23.0, -26.25};
    private Double MSBFactors[] = {500000.0, 1.5, 200.0, 83.333, 133.33};
    private Double LSBFactors[] = {142.857, 0.08333, 200.0, 13.333, 133.33};
    
    DecimalFormat pressureFormat = null;
    DecimalFormat tauFormat = null;
    DecimalFormat yearDayFormat = null;
    DecimalFormat speedFormat = null;
    DecimalFormat headingFormat = null;
    
    
   
    
    private double 
            pressure,
            tau,
            speed,
            direction,
            markerArrivalTime,
            heading,
            yearDay;
    private boolean
            ismsb=false,
            isPressure=false,
            isTau=false,
            isYearDay=false,
            isSpeed=false,
            isHeading=false,
            isMarker = false;
    private int
            downloadCount;
    private long
            markerTimeStamp = 0; 
    private Date date;
    private Calendar calendar;
    
    
    public TelemeteredDay(){
        
        //pressureFormat = new DecimalFormat("#.####");
        pressureFormat = new DecimalFormat("#");
        tauFormat = new DecimalFormat("#.####");
        yearDayFormat = new DecimalFormat("#");
        speedFormat = new DecimalFormat("#.####");
        headingFormat = new DecimalFormat("###.####");  
        
            
        pressure = -99.0000;
        tau = -99.0000;
        speed = -99.0000;
        direction = -99.0000;
        markerArrivalTime = -99.0000;
        heading = -99.0000; 
        yearDay = -99.0000;
    
    }//end constructor
    
    void setMarkerArrivalTime(double t){
        markerArrivalTime = t;
        isMarker = true;
    
    }//end setmarkerdeckbxtime
    
    void setMarkerTimeStamp(long t){
        markerTimeStamp = t;
        setDate(markerTimeStamp);
    }
    
    void setDate(long d){
        date = new Date(d);
       calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTime(date);
    
    }//end Set Date
    
    
    void setPressure(double t)
    {
        
        if(ismsb){
            pressure = MSBFactors[0] * (getDeckboxElapsedTime(t) + timeConstants[0]); 
            
        }// end if
        
        else{
            pressure = LSBFactors[0] * (getDeckboxElapsedTime(t) + timeConstants[0]);
        }//end else
        isPressure = true;
    }//end
    
    
              
    void setTau(double t)
    {
        if(ismsb){
            tau = MSBFactors[1] * (getDeckboxElapsedTime(t) + timeConstants[1]);   
        }// end if
        
        else{
            tau = LSBFactors[1] * (getDeckboxElapsedTime(t) + timeConstants[1]);
        }//end else  
        isTau = true;
    }//end  
    
    
    
    
    void setYearDay(double t)
    {
        if(ismsb){
            yearDay = (MSBFactors[2] * (getDeckboxElapsedTime(t) + timeConstants[2]));   
        }// end if
        
        else{
            yearDay = (LSBFactors[2] * (getDeckboxElapsedTime(t) + timeConstants[2]));
        }//end else  
        isYearDay = true;
    }//end 
    
    
    void setSpeed(double t)
    {
        if(ismsb){
            speed = MSBFactors[3] * (getDeckboxElapsedTime(t) + timeConstants[3]);   
        }// end if
        
        else{
            speed = LSBFactors[3] * (getDeckboxElapsedTime(t) + timeConstants[3]);
        }//end else 
        isSpeed = true;
    }//end 
    
    
   
    void setHeading(double t)
    {
        if(ismsb){
            heading = MSBFactors[4] * (getDeckboxElapsedTime(t) + timeConstants[4]);   
        }// end if
        
        else{
            heading = LSBFactors[4] * (getDeckboxElapsedTime(t) + timeConstants[4]);
        }//end else 
        
        isHeading = true;
    }//end  
    
    
    void setMSB(boolean b)
    {
        ismsb = b;
        
    }//end
    void resetMarkers(){
            
        ismsb=false;
        isPressure=false;
        isTau=false;
        isYearDay=false;
        isSpeed=false;
        isHeading=false;
        isMarker = false;        
    
    }
    
    public void setPressureHasArrived()
    {
        isPressure = true;
    }
    public void setTauHasArrived()
    {
        isTau = true;
    } 
    
    public void setYearDayHasArrived()
    {
        isYearDay = true;
    } 
    public void setSpeedHasArrived()
    {
        isSpeed = true;
    } 
    
    public void setHeadingHasArrived()
    {
        isHeading = true;
    }    
 
    
    long getMarkerTimeStamp(){
        return markerTimeStamp;
    }//end
    
    
    String getPressure()
    {
            return pressureFormat.format(pressure);
    
    }//end
    
            
    String getTau()
    {
        return tauFormat.format(tau);
    }//end  
    
    String getSpeed()
    {
        return speedFormat.format(speed);
        
    }//end    
  
    String getHeading()
    {
        return headingFormat.format(heading);
    }//end    
    boolean isMSB()
    {
        return ismsb;
    }//end    
    String getYearDay()
    {
        return yearDayFormat.format(yearDay);
    }//end  
    
    int getYear(){
    return calendar.get(Calendar.YEAR);
   
    }
    
    int getMonth(){
    return calendar.get(Calendar.MONTH) + 1;
   
    }
    
        
    int getDay(){
    
        return calendar.get(Calendar.DAY_OF_MONTH);
   
    }
    
    int getHour(){
    
        return calendar.get(Calendar.HOUR_OF_DAY);
    }
    
    int getMinute(){
    
        return calendar.get(Calendar.MINUTE);
   
        
    }
    
    int getSeconds(){
    
        return calendar.get(Calendar.SECOND);
   
    }    
        
    
    
    boolean isPressureSet(){
    return isPressure;
    }//end isSet
    
    boolean isTauSet(){
    return isTau;
    }//end isSet
    boolean isYearDaySet(){
    return isYearDay;
    }//end isSet
    boolean isSpeedSet(){
    return isSpeed;
    }//end isSet            
    boolean isHeadingSet(){
    return isHeading;
    }//end isSet
    boolean isMarkerSet(){
        return isMarker;
    }//end is marker
    
    boolean isEmpty(){
        
        return !(isPressure || isTau || isYearDay || isSpeed || isHeading || isMarker);
    
    }//end
    
    
    private  double getDeckboxElapsedTime(double time){
        
        time = time - markerArrivalTime;
        if (time < 0) {
            time += deckBoxRolloverValue;
        }

        return time;
    }//end
    
    
    
}// end class
