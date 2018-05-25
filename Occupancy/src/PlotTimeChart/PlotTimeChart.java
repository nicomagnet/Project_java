/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PlotTimeChart;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import occupancy.DataContainer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.data.time.Hour;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class PlotTimeChart extends JFrame {
 DataContainer datos;
    public void PlotFrame() throws ParseException, IOException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-d HH:mm:ss");
        datos = new DataContainer("office.csv");
        Date[] dates = datos.getDates();
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        String[] availableVar = datos.getAvailableVariables();
        JCheckBox[] checkbox = new JCheckBox[datos.getNumberOfVariables()];
        JPanel chartPanel = new ChartPanel(ChartFactory.createTimeSeriesChart("Occupancy Project", "Time", "Values", timeSeriesCollection, true, true, false));
        JPanel eastPanel = new JPanel();
        eastPanel.setLayout(new GridLayout(datos.getNumberOfVariables() + 3, 1));
        for (int i = 0; i < datos.getNumberOfVariables(); i++) {
            checkbox[i] = new JCheckBox(availableVar[i]);
            eastPanel.add(checkbox[i]);
        }
        JPanel southPanel=new JPanel();
        southPanel.setBackground(Color.LIGHT_GRAY);
              
        JButton boton = new JButton("Laptop power consumption");
        southPanel.add(boton);
       
        JButton boton1 = new JButton("Motion detections");
        southPanel.add(boton1);
  
        
        
        JFrame frame = new JFrame("Sensors Graph");
        frame.setLayout(new BorderLayout());
        frame.add(chartPanel, BorderLayout.CENTER);
        frame.add(eastPanel, BorderLayout.EAST);
        frame.add(southPanel, BorderLayout.SOUTH);
        frame.setSize(100, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(chartPanel);
        frame.pack();
        frame.setVisible(true);
        JButton button = new JButton("Plot");
        eastPanel.add(button);
        button.addActionListener(new ActionListener() {
            @Override
        public void actionPerformed(ActionEvent e) {
            timeSeriesCollection.removeAllSeries();
            for (int i = 0; i < datos.getNumberOfVariables(); i++) {
                if (checkbox[i].isSelected()) {
                    TimeSeries timeSeries = new TimeSeries(availableVar[i]);
                    Double[] value1 = datos.getData(availableVar[i]);
                    for (int j = 0; j < datos.getNumberOfSamples(); j++) {
                        timeSeries.add(new Hour(dates[j]), value1[j]);
                    }
                    timeSeriesCollection.addSeries(timeSeries);
                }

            }

        }
        });
         boton.addActionListener(new ActionListener() {
            
            
            @Override
        public void actionPerformed(ActionEvent e) {
            timeSeriesCollection.removeAllSeries();
            
            //Obtengo datos de las computadoras
            Double[] laptop11=datos.getData("power_laptop1_zone1");
            Double[] laptop12=datos.getData("power_laptop1_zone2");
            Double[] laptop22=datos.getData("power_laptop2_zone2");
            Double[] laptop32=datos.getData("power_laptop3_zone2");
            
            Double [] presence = new Double[laptop11.length];
            
            for(int k=0; k<presence.length;k++){
                presence[k]=0.0;
               if (laptop11[k]>=15){
                    presence[k]=presence[k]+1;
                }
                if (laptop12[k]>=15){
                    presence[k]=presence[k]+1;
                }
                if (laptop22[k]>=15){
                    presence[k]=presence[k]+1;
                }
                if (laptop32[k]>=15){
                    presence[k]=presence[k]+1;
                }
            }
                                                 
            TimeSeries timeSeries = new TimeSeries("Estimator based on power consumption");
            for(int k=0; k<presence.length; k++){
                        timeSeries.add(new Hour(dates[k]),presence[k]);
                    }         
            timeSeriesCollection.addSeries(timeSeries);
        }
        });
         
         boton1.addActionListener(new ActionListener() {
            
            
            @Override
        public void actionPerformed(ActionEvent e) {
            timeSeriesCollection.removeAllSeries();
            
            //Obtengo datos de las computadoras
            Double[] laptop11=datos.getData("power_laptop1_zone1");
            Double[] laptop12=datos.getData("power_laptop1_zone2");
            Double[] laptop22=datos.getData("power_laptop2_zone2");
            Double[] laptop32=datos.getData("power_laptop3_zone2");
                                    
            Double [] presence = new Double[laptop11.length];
            Double [] presenceMotion = new Double[laptop11.length];
            for(int k=0; k<presence.length;k++){
                presence[k]=0.0;
               if (laptop11[k]>=15){
                    presence[k]=presence[k]+1;
                }
                if (laptop12[k]>=15){
                    presence[k]=presence[k]+1;
                }
                if (laptop22[k]>=15){
                    presence[k]=presence[k]+1;
                }
                if (laptop32[k]>=15){
                    presence[k]=presence[k]+1;
                }
            }
                
             // con los datos de precencia analizamos los datos de siguimiento
               
             Double [] detectedmotions=datos.getData("detected_motions");
                      
            double l,u,c,a,o;
           
            c=0.0;
            l=0.0;
            u=0.5;
          
            for (int i=0; i<100;i++){//gradient analized

                c=(l+u)/2.0;

                if (Gradiente(l,detectedmotions,presence)< Gradiente(u,detectedmotions,presence) 
                        && Gradiente(c,detectedmotions,presence)< Gradiente(u,detectedmotions,presence) )  {

                     u=c;
                     
                }  else if (Gradiente(c,detectedmotions,presence)< Gradiente(l,detectedmotions,presence) 
                        && Gradiente(u,detectedmotions,presence)< Gradiente(l,detectedmotions,presence) ){
                     l=c;
                } else{
                    break;
                } 
            }
            
            a=c;   
            System.out.println(a);// valor minimo 
               
            
            for (int h=0; h<detectedmotions.length; h++ ){
                
                    presenceMotion[h]=0.0;
                    o=0.0;
                    
                    o=Math.rint(a*detectedmotions[h]);
                    presenceMotion[h]=o;
                    
                }
            TimeSeries timeSeries2 = new TimeSeries("Estimator based on detected motions");
            
            for(int j=0; j<presenceMotion.length; j++){
                        timeSeries2.add(new Hour(dates[j]),presenceMotion[j]);
                    }         
            timeSeriesCollection.addSeries(timeSeries2);
        }
        });
         
    }
    public double Gradiente(double a,Double [] detectedmotions, Double[] presence){
        double f;
        f=0;
        for (int h=0;h<detectedmotions.length;h++){
                    
                    f=f+Math.abs(a*detectedmotions[h]-presence[h]);
                   
                }
        
        return(f);
    }
    public static void main(String[] args) throws Exception {
        PlotTimeChart Graficar = new PlotTimeChart();
        Graficar.PlotFrame();
//      p1.setVisible(true);
        Graficar.pack();
    }

}


