/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *
 * 1.14.2013  removed one day advance, a hack to address the erroneous timestamp issue
 * with the aquisition program.
 */
package rawdatadecoder;
import java.io.*;
import java.util.*;

/**
 *
 * @author pedro
 */
public class RawDataDecoder {
private static Hashtable freq2Index = new Hashtable();
private static Hashtable theNextFreqIs = new Hashtable();
private static double deckBoxRolloverValue = 104.857;
private static String pwd,URIOutputFileName;


    /**
     * @param args the command line arguments
     */
    
    

public static void main(String[] args)
{
    
    File inputFile;
   
    
    
    if(args.length == 4 && (args[0].toLowerCase().equals("ds7000")|| args[0].toLowerCase().equals("udb")) &&
                           (args[1].toLowerCase().equals("cpies")|| args[1].toLowerCase().equals("pies")) &&
                           (args[2].toLowerCase().equals("true")|| args[2].toLowerCase().equals("false")))
                           
    {
                      
        try
        {    
            inputFile = new File(args[3]);    
            pwd = inputFile.getParent();    
            decode(args[0].toLowerCase().equals("udb"),
                   args[1].toLowerCase().equals("cpies"),
                   args[2].toLowerCase().equals("true"),
                   inputFile);    
        }//end try

        catch(Exception e)
        {    
            e.printStackTrace();
        }//end catch
   
    }//end if
    else
    {
        System.out.println("usage:\n\n\n"
                + " \"RawDataDecoder deckBox Instrument timeStamp fileName\" \n");
        System.out.println("deckBox values are udb,ds7000\nInstrument values are cpies,pies\ntimeStamp values are true,false\nfilename is the path and filename");
    }//end else
    

}//end main


public static void decode(boolean isUDB, boolean isCPies,boolean useTimeStamp, File inputFile) {
        
        String cPiesWithDS7000 = "(10.00)|(10.50)|(11.00)|(11.50)|(12.00)|(12.50)|(13.00)",
                piesWithDS7000 = "(10.00)|(10.50)|(11.00)|(11.50)|(12.00)",
                cPiesWithUDB="(10.00)|(10.50)|(11.00)|(11.50)|(12.00)|(12.50)|(13.00)",
                piesWithUDB="(10.00)|(10.50)|(11.00)|(11.50)|(12.00)",
                deckBoxMatch="";        
        

        boolean DS7000 = false,
                endOfDay = false;
        int numberOfFieldsInLine = 5,
                freqIndex = 0,
                deckBoxTimerIndex = 0,
                timeStampIndex = 0;
        double lengthOfDay = 30;
        
        
       
        DS7000 = !isUDB;
        
        
        
       if(DS7000){             
           
           numberOfFieldsInLine = 5;
           freqIndex = 1;
           deckBoxTimerIndex = 3;
           timeStampIndex = 4;
           deckBoxRolloverValue = 104.857;
           
           if(isCPies){           
               deckBoxMatch = cPiesWithDS7000;
               lengthOfDay = 30;
           }//endif        
           else{               
               deckBoxMatch = piesWithDS7000;
               lengthOfDay = 24;
           }        
       }
      
       if(isUDB){
           
           numberOfFieldsInLine = 6;           
           freqIndex = 0;
           deckBoxTimerIndex = 2;
           timeStampIndex = 6;
           deckBoxRolloverValue = 65000000;
           
           if(isCPies){           
               deckBoxMatch = cPiesWithUDB;
               lengthOfDay = 30;
           }//endif        
           else{
               deckBoxMatch = piesWithUDB;
               lengthOfDay = 24;
        }
       }//endif

        
        
       
        
        
        
        
        String freqPosition="-1";
        freq2Index.put("10.00", "0");
        freq2Index.put("11.00", "0");
        freq2Index.put("10.50", "1");
        freq2Index.put("11.50", "2");
        freq2Index.put("12.00", "3");
        freq2Index.put("12.50", "4");
        freq2Index.put("13.00", "5");
        
        theNextFreqIs.put("0","1");
        theNextFreqIs.put("1","2");
        theNextFreqIs.put("2","3");
        theNextFreqIs.put("3","4");
        theNextFreqIs.put("4","5");
        theNextFreqIs.put("5","0");
        LinkedList telemetryBlock = new LinkedList();
        
        
        try{
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            String line = "";
            String []tokenizedLine;
            String  frequency = "0",
                    deckboxTimerValue = "0",
                    computerTimeStamp = "0",
                    previousFrequency = "0",
                    nextFrequency = "0",
                    previousComputerTimeStamp = "0",
                    previousDeckboxTimerValue = "0";
            LinkedList listOfSignals = new LinkedList();
                    
            while(br.ready()){
                line = br.readLine();
                line = line.replaceAll("  ", " ");
                line = line.replaceAll("  ", " ");
                line = line.replaceAll("  ", " ");
                line = line.replaceAll("[^0-9 .]","");
                
                tokenizedLine=line.split(" ");
                
                
                    if(tokenizedLine.length >= numberOfFieldsInLine  && tokenizedLine[freqIndex].matches(deckBoxMatch) && !tokenizedLine[0].startsWith(".")){  
                    frequency = freq2Index.get(tokenizedLine[freqIndex])+"";
                    
                    deckboxTimerValue = tokenizedLine[deckBoxTimerIndex];
                    computerTimeStamp = tokenizedLine[timeStampIndex];
                   

                    DeckBoxSignal dbs = new DeckBoxSignal();
                    dbs.setUDB(isUDB);
                    dbs.setFrequency(frequency);
                    dbs.setPreviousFrequency(previousFrequency);
                    dbs.setNextFrequency(theNextFreqIs.get(frequency)+"");
                    dbs.setPosition(freqPosition);
                    try{
                    dbs.setDeckboxTimerValue(deckboxTimerValue);
                    }catch(Exception e){
                    System.out.println(line);
                    }
                    dbs.setPreviousDeckboxTimerValue(previousDeckboxTimerValue);
                    dbs.setComputerTimeStamp(computerTimeStamp);
                    dbs.setPreviousComputerTimeStamp(previousComputerTimeStamp);
                    
                    
                    if(!frequency.equals(previousFrequency) || 
                            ((frequency.equals(previousFrequency) &&
                            (getDeckboxElapsedTime(dbs.getDeckboxTimerValue(),dbs.getPreviousDeckboxTimerValue())) > 50 &&
                            (dbs.getComputerTimeStamp()-dbs.getPreviousComputerTimeStamp() >50000))))
                    {
                        dbs.setFirstFrequencyReceived(true);

                    
                    }//end if
                    
                    
                      if(tokenizedLine[freqIndex].equals("10.00")){                          
                        dbs.setMSB(true);                                                                
                    }// end if
                                        
                    listOfSignals.add(dbs);
                    previousFrequency = frequency;
                    previousDeckboxTimerValue = deckboxTimerValue;
                    previousComputerTimeStamp = computerTimeStamp;
                    
                }//end if
                
            
            
            }//end while
            
            double maker = 0.0;
        
            ListIterator i = listOfSignals.listIterator();
            DeckBoxSignal dbs,dbsN;// = new DeckBoxSignal();
            LinkedList listOfSignalsNoMultiPath = new LinkedList();
            String Freq = "";
         
            
            // remove multipath and create new linked;st
           
            while(i.hasNext()){
               
               dbs = (DeckBoxSignal) i.next(); 
             if(/*dbs.isFirstFrequencyReceived()*/ true){
                 Freq = dbs.getFrequency();
                 //System.out.println(Freq + " = " + dbs.isMSB());
                 
                 if(i.hasNext()){
                     dbsN = (DeckBoxSignal)i.next();
                     if(dbsN.getDeckboxTimerValue() < dbs.getDeckboxTimerValue() && (dbs.getDeckboxTimerValue()-dbsN.getDeckboxTimerValue()) < 1)
                     {
                         dbsN.setNoise(true);
                         //System.out.println(dbsN.getComputerTimeStamp());
                         if(i.hasNext())
                         {
                             dbsN = (DeckBoxSignal)i.next();

                         }//end if

                     }//end if

                     //dbs.setComputerTimeStamp(dbsN.getComputerTimeStamp()+""); // hack since time stamp has an error
                     dbs.setNextDeckboxTimerValue(dbsN.getDeckboxTimerValue()+"");
                     i.previous();
                 }
                 listOfSignalsNoMultiPath.add(dbs);
                 
             }
             
           

             
             
             
             
             
  
    }//end while
            
            
            
                 double  markerArrivalTime = 0,
                         pressureArrivalTime = -1,
                         tauArrivalTime = -1,
                         yearDayArrivalTime = -1,
                         speedArrivalTime = -1,
                         headingArrivalTime = -1; 
                 double markerComputerArrivalTime = 0,
                         previousMarkerArrivalTime = -1,
                         markerDiff = 0,
                         lastGoodSignalTimeStamp = 0;
                 boolean isFirst = true; // first line in the file.                 
            
            
             
                 
                 TelemeteredDay day = new TelemeteredDay();
                 ListIterator y = listOfSignalsNoMultiPath.listIterator();
             
             
                 
             while(y.hasNext()){
                 int f = 0;
                 String previousFreq,
                         currentFreq;

                 isFirst = !y.hasPrevious();
                 double elapsedComputerTime = 0;
                 double timeDiff = 0;
                 
                 
                 
                 dbs = (DeckBoxSignal) y.next();

                 currentFreq = dbs.getFrequency();
                 previousFreq = dbs.getPreviousFrequency();
                 f = new Integer(currentFreq).intValue();
                 
                 if(isFirst){
                     
                     markerArrivalTime = dbs.getDeckboxTimerValue();        
                     previousMarkerArrivalTime = markerArrivalTime;
                     //
                 
                 }//emdif                 
                                 
                 timeDiff = 0; 
                
                 timeDiff = getDeckboxElapsedTime(dbs.getDeckboxTimerValue(),markerArrivalTime);  
                 
                 markerDiff = getDeckboxElapsedTime(markerArrivalTime,previousMarkerArrivalTime);
                 markerComputerArrivalTime = dbs.getComputerTimeStamp();
                
                 elapsedComputerTime = dbs.getComputerTimeStamp()-lastGoodSignalTimeStamp;

                                  
                 
                 if(( endOfDay || timeDiff >= lengthOfDay || ( elapsedComputerTime/1000 ) > 60 ) && !isFirst){
                                 
                     telemetryBlock.add(day);                                                                                                                                                                                                       
                     day = null;                                                                                                                                                                                                  
                     day = new TelemeteredDay();
                     lastGoodSignalTimeStamp = dbs.getComputerTimeStamp();
                     endOfDay = false;
                     
                 }//end if
                 
                 
                 //moves freq over when time elapses
                 if((timeDiff > 14.25 && timeDiff < 14.5) && day.isMarkerSet())
                 {
                    day.setPressureHasArrived();
                     
                 }
                 
                 if((timeDiff > 20.5 && timeDiff < 20.75) && day.isPressureSet())
                 {
                    day.setTauHasArrived();
                     
                 } 
                 
                 if((timeDiff > 22.75 && timeDiff < lengthOfDay) && day.isTauSet())
                 {
                    day.setYearDayHasArrived();
                     
                 } 
                 
                 if(isCPies && (timeDiff > 26 && timeDiff < 26.25) && day.isYearDaySet())
                 {
                    day.setSpeedHasArrived();
                   
                     
                 } 
                 
                 if(isCPies && (timeDiff > 29.5 && timeDiff < lengthOfDay) && day.isSpeedSet())
                 {
                    day.setHeadingHasArrived();
                     
                 }                 
                 
                 
                 switch (f){                                                               
                     case 0:                                                            
                             
                         if (!day.isMarkerSet() && ( (timeDiff >= lengthOfDay) || (isFirst) )){ 
                             
                             if(!isFirst){

                                 previousMarkerArrivalTime = markerArrivalTime;                                 
                                 markerArrivalTime = dbs.getDeckboxTimerValue();                                                                                              
                                 markerComputerArrivalTime = dbs.getComputerTimeStamp();
                     
                                     
                                                                  
                             }//endif

                             
                             
                             day.setMarkerTimeStamp(dbs.getComputerTimeStamp());
                             day.setMSB(dbs.isMSB());                             
                             day.setMarkerArrivalTime(markerArrivalTime); 
                             lastGoodSignalTimeStamp = dbs.getComputerTimeStamp();
                         }// end if
                                                                                                                                                                                                                                                        
                         isFirst = false;
                         
                         break;
                     
                     case 1:
                         
                         if(!day.isPressureSet() && day.isMarkerSet() && (timeDiff > .25 && timeDiff < 14.25)){                                                                                                                          
                             pressureArrivalTime = dbs.getDeckboxTimerValue();                                                                                                                      
                             day.setPressure(pressureArrivalTime); 
                             lastGoodSignalTimeStamp = dbs.getComputerTimeStamp();
                         }// end if
                         
                         break;
                         
                     case 2:
                         
                         if(!day.isTauSet() && day.isPressureSet() && (timeDiff > 14.5 && timeDiff < 20.5)){                                                
                             tauArrivalTime = dbs.getDeckboxTimerValue();                                                                                                                                                    
                             day.setTau(tauArrivalTime);
                             lastGoodSignalTimeStamp = dbs.getComputerTimeStamp();
                         }//end if
                         
                         break;
                     
                     case 3:
                         
                         if(!day.isYearDaySet() && day.isTauSet() && (timeDiff > 20.75 && timeDiff < 22.75)){                                                 
                             yearDayArrivalTime = dbs.getDeckboxTimerValue();                         
                             day.setYearDay(yearDayArrivalTime);
                             lastGoodSignalTimeStamp = dbs.getComputerTimeStamp();
                             if(!isCPies) endOfDay = true;
                         }//end if
                         
                         break;
                     
                     case 4:
                         
                         if(!day.isSpeedSet() && day.isYearDaySet() && (timeDiff > 23 && timeDiff < 26)){                                                                           
                             speedArrivalTime = dbs.getDeckboxTimerValue();                         
                             day.setSpeed(speedArrivalTime);
                             lastGoodSignalTimeStamp = dbs.getComputerTimeStamp();
                         } // end if
                         
                         break;
                     
                     case 5:
                         
                         if(!day.isHeadingSet() && day.isSpeedSet() && (timeDiff > 26.25 && timeDiff < 29.25)){                                                  
                             headingArrivalTime = dbs.getDeckboxTimerValue();                         
                             day.setHeading(headingArrivalTime);
                             lastGoodSignalTimeStamp = dbs.getComputerTimeStamp();
                             if(isCPies) endOfDay = true;
                         }// end if
                             
                                                  
                         break;
                        
                         
                 }//end switch                                                                                                                                        
                  

                 
             }//end while
                 telemetryBlock.add(day);
             
             
             ListIterator j = telemetryBlock.listIterator();
             TelemeteredDay day1,dayTemp;
             String timeStamp="",Year="",Month="",Day="",Hour="",Minute="",Second="";
             

             URIOutputFileName = /*pwd + inputFile.separator + */"uri_format_" + inputFile.getName();
             
             File f = new File(URIOutputFileName);
             if(f.exists())
             {
                 f.delete();
             
             }//end if
             while(j.hasNext()){
                 
                 
                 
                 day1 = (TelemeteredDay)j.next();
                 
                 if(!day1.isEmpty()){

                 if(useTimeStamp){timeStamp = " "+day1.getMarkerTimeStamp() +"";}


                 Year=day1.getYear()+"";
                 Month = day1.getMonth()+"";
                 Day = day1.getDay()+"";
                 Hour = day1.getHour()+"";
                 Minute = day1.getMinute()+"";
                 Second = day1.getSeconds()+"";
/*
                 if(j.hasNext())
                 {
                     dayTemp=(TelemeteredDay)j.next();
                     if(!dayTemp.isEmpty())
                     {
                     
                         Year=dayTemp.getYear()+"";
                         Month = dayTemp.getMonth()+"";
                         Day = dayTemp.getDay()+"";
                         Hour = dayTemp.getHour()+"";
                         Minute = dayTemp.getMinute()+"";
                         Second = dayTemp.getSeconds()+"";
                         j.previous();
                     }
                     
                 }//end if
*/
                 //uri format
                 log(day1.getYearDay() + " " + 
                         day1.getTau() + " " + 
                         day1.getPressure() + " " + 
                         day1.getSpeed() + " " + 
                         day1.getHeading() + " " +
                         Year + " " +
                         Month + " " +
                         Day + " " +
                         Hour + " " +
                         Minute + " " +
                         Second + timeStamp,URIOutputFileName);
                 
                 
                 
                 }// end if
             }//end while
             
             
        }//end try
        
        catch(Exception e){
            
            e.printStackTrace();
        }// end catch`
        
        
 
        
        
        // TODO code application logic here
    }
    
        private static void log(String s,String fileName) {
        try {

            File f = new File(fileName);
            FileWriter fw = new FileWriter(f, true);
            BufferedWriter bw = new BufferedWriter(fw);
            if (fw != null && bw != null) {
                bw.append(s + "\n");
                bw.flush();
                bw.close();
                fw.close();
            }

        }// end try
        catch (Exception e) {
            e.printStackTrace();
        }// end catch


    }// end log 
        
            
        private static double getDeckboxElapsedTime(double time2,double time1){
                    
            double dTime = 0.0;
            dTime = time2 - time1;

            if ( dTime < 0 )
            {
                dTime += deckBoxRolloverValue;
            }//end if

            return dTime;
        }//end getDeckboxElapsedTime

}