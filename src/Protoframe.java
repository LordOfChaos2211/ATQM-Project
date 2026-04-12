import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class Protoframe  implements ActionListener{

    boolean validateFile(String path){
        Path filePath = Path.of(path);
        if(!Files.exists(filePath)){
            JOptionPane.showMessageDialog(null,"This file does not exist","Import error",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if(!Files.isRegularFile(filePath)){
            JOptionPane.showMessageDialog(null,"Invalid file imported","Import error",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        path = path.toLowerCase();
        if(!path.endsWith(".txt")){
            JOptionPane.showMessageDialog(null,"Invalid file imported","Import error",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    double[][] extractCoords(String path){
        Pattern pattern = Pattern.compile("(\\(-?[0-9]+,-?[0-9]+\\))");
        String[] matches = pattern.matcher(path).results().map(MatchResult::group).toArray(String[]::new);
        double[][] rawCoords = new double[2][matches.length];
        for(int i = 0; i < matches.length; i++){
            matches[i] = matches[i].replaceAll("[()]","");
            String [] temp = matches[i].split(",");
            rawCoords[0][i] = Double.parseDouble(temp[0]);
            rawCoords[1][i] = Double.parseDouble(temp[1]);
        }
        return rawCoords;
    }

    JFrame frame;

    JPanel content;
    JPanel west;
    JPanel south;

    JLabel frameTitle;
    JLabel importPrompt;
    JLabel titlePrompt;
    JLabel XAxisPrompt;
    JLabel YAxisPrompt;
    JLabel datapointPrompt;
    JLabel datasetPrompt;
    JLabel upperTitle;
    JLabel lowerTitle;
    JLabel meanTitle;

    JButton setImport;
    JButton addPoint;
    JButton clearChart;
    JButton createChart;
    JButton addData;

    JTextField XTitle;
    JTextField YTitle;
    JTextField chartTitle;
    JTextField datasetName;
    JTextField datapoint;
    JTextField upperLimIn;
    JTextField lowerLimIn;
    JTextField meanIn;

    DefaultXYDataset coordinates;
    JFreeChart chartGenerator;
    ChartPanel chartHolder = new ChartPanel(null);

    ValueMarker UCL;
    ValueMarker LCL;
    ValueMarker Mean;

    Protoframe(){

        float[] dashPattern = {2.0f, 6.0f};
        BasicStroke dottedLine = new BasicStroke(2.0f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,1.0f,dashPattern,0.0f);

        coordinates = new DefaultXYDataset();
        chartGenerator = ChartFactory.createXYLineChart("","","", coordinates);
        chartHolder.setChart(chartGenerator);


        UCL = new ValueMarker(5.0);
        UCL.setPaint(Color.red);
        UCL.setStroke(dottedLine);

        LCL = new ValueMarker(1.0);
        LCL.setPaint(Color.red);
        LCL.setStroke(dottedLine);

        Mean = new ValueMarker(3.0);
        Mean.setPaint(Color.green);
        Mean.setStroke(dottedLine);

        content = new JPanel();
        content.setPreferredSize(new Dimension(1440,810));
        content.add(chartHolder);


        frameTitle = new JLabel("SPC Chart Maker");
        frameTitle.setFont(new Font("Times New Roman",Font.PLAIN,25));
        frameTitle.setPreferredSize(new Dimension(400,25));

        importPrompt = new JLabel("Please select a .txt file to import");
        importPrompt.setFont(new Font("Times New Roman",Font.PLAIN,20));

        titlePrompt = new JLabel("Please enter a title for your chart");
        titlePrompt.setFont(new Font("Times New Roman",Font.PLAIN,20));

        XAxisPrompt = new JLabel("Please enter the name of the X axis");
        XAxisPrompt.setFont(new Font("Times New Roman",Font.PLAIN,20));

        YAxisPrompt = new JLabel("Please enter the name of the Y axis");
        YAxisPrompt.setFont(new Font("Times New Roman",Font.PLAIN,20));

        datapointPrompt = new JLabel("Please enter the data point you want to add");
        datapointPrompt.setFont(new Font("Times New Roman",Font.PLAIN,20));

        datasetPrompt = new JLabel("Please enter the name of the Dataset");
        datasetPrompt.setFont(new Font("Times New Roman",Font.PLAIN,20));

        upperTitle = new JLabel("Please enter the value for the upper control limit");
        upperTitle.setFont(new Font("Times New Roman",Font.PLAIN,20));

        lowerTitle = new JLabel("Please enter the value for the lower control limit");
        lowerTitle.setFont(new Font("Times New Roman",Font.PLAIN,20));

        meanTitle = new JLabel("Please enter the value for the mean");
        meanTitle.setFont(new Font("Times New Roman",Font.PLAIN,20));


        setImport = new JButton("Import Dataset");
        setImport.setFont(new Font("Times New Roman",Font.PLAIN,20));
        setImport.setFocusable(false);
        setImport.addActionListener(this);

        addPoint = new JButton("Add Datapoint");
        addPoint.setFont(new Font("Times New Roman",Font.PLAIN,20));
        addPoint.setFocusable(false);
        addPoint.addActionListener(this);

        clearChart = new JButton("Clear Chart");
        clearChart.setFont(new Font("Times New Roman",Font.PLAIN,20));
        clearChart.setFocusable(false);
        clearChart.addActionListener(this);

        createChart = new JButton("Create Chart");
        createChart.setFont(new Font("Times New Roman",Font.PLAIN,20));
        createChart.setFocusable(false);
        createChart.addActionListener(this);

        addData = new JButton("Add Data");
        addData.setFont(new Font("Times New Roman",Font.PLAIN,20));
        addData.setFocusable(false);
        addData.addActionListener(this);


        XTitle = new JTextField();
        XTitle.setFont(new Font("Times New Roman",Font.PLAIN,20));
        XTitle.setPreferredSize(new Dimension(400,30));

        YTitle = new JTextField();
        YTitle.setFont(new Font("Times New Roman",Font.PLAIN,20));
        YTitle.setPreferredSize(new Dimension(400,30));

        chartTitle = new JTextField();
        chartTitle.setFont(new Font("Times New Roman",Font.PLAIN,20));
        chartTitle.setPreferredSize(new Dimension(400,30));

        datasetName = new JTextField();
        datasetName.setFont(new Font("Times New Roman",Font.PLAIN,20));
        datasetName.setPreferredSize(new Dimension(400,30));

        datapoint = new JTextField();
        datapoint.setFont(new Font("Times New Roman",Font.PLAIN,20));
        datapoint.setPreferredSize(new Dimension(400,30));

        upperLimIn = new JTextField();
        upperLimIn.setFont(new Font("Times New Roman",Font.PLAIN,20));
        upperLimIn.setPreferredSize(new Dimension(400,30));

        lowerLimIn = new JTextField();
        lowerLimIn.setFont(new Font("Times New Roman",Font.PLAIN,20));
        lowerLimIn.setPreferredSize(new Dimension(400,30));

        meanIn = new JTextField();
        meanIn.setFont(new Font("Times New Roman",Font.PLAIN,20));
        meanIn.setPreferredSize(new Dimension(400,30));

        west = new JPanel();
        west.setPreferredSize(new Dimension(480,480));
        //west.setBackground(Color.BLUE);
        west.setLayout(new FlowLayout(FlowLayout.CENTER,10,20));
        west.add(frameTitle);
        west.add(titlePrompt);
        west.add(chartTitle);
        west.add(XAxisPrompt);
        west.add(XTitle);
        west.add(YAxisPrompt);
        west.add(YTitle);

        west.add(createChart);


        south = new JPanel();
        south.setPreferredSize(new Dimension(270,270));
        south.setBackground(Color.red);




        frame = new JFrame();
        frame.setSize(1920,1080);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(content,BorderLayout.CENTER);
        frame.add(west,BorderLayout.WEST);
        frame.add(south,BorderLayout.SOUTH);
        frame.setVisible(true);
        frame.setTitle("test");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == setImport){
            JFileChooser fileChooser = new JFileChooser();
            int respose = fileChooser.showOpenDialog(null);
            if(respose == JFileChooser.APPROVE_OPTION){
                File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                if(validateFile(file.getAbsolutePath())){
                    try{
                        double[][] rawCoords = extractCoords(Files.readString(Path.of(file.getAbsolutePath())));
                        coordinates.addSeries(datasetName.getText(),rawCoords);
                        JOptionPane.showMessageDialog(null,"Dataset successfully imported","Successful import",JOptionPane.INFORMATION_MESSAGE);
                    }
                    catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
        else if(e.getSource() == createChart){
            chartGenerator = ChartFactory.createXYLineChart(chartTitle.getText(),XTitle.getText(),YTitle.getText(),coordinates);
            chartHolder.setChart(chartGenerator);
            chartHolder.repaint();
            content.repaint();
            west.removeAll();
            west.add(importPrompt);
            west.add(setImport);
            west.add(datasetPrompt);
            west.add(datasetName);
            west.add(upperTitle);
            west.add(upperLimIn);
            west.add(lowerTitle);
            west.add(lowerLimIn);
            west.add(meanTitle);
            west.add(meanIn);
            west.add(addData);
            west.add(datapointPrompt);
            west.add(datapoint);
            west.add(addPoint);
            west.revalidate();
            west.repaint();
        }
        else if(e.getSource() == addData){
            try{
                UCL.setValue(Double.parseDouble(upperLimIn.getText()));
                LCL.setValue(Double.parseDouble(lowerLimIn.getText()));
                Mean.setValue(Double.parseDouble(meanIn.getText()));
                chartGenerator = ChartFactory.createXYLineChart(chartTitle.getText(),XTitle.getText(),YTitle.getText(),coordinates);
                chartHolder.setChart(chartGenerator);
                XYPlot plot = chartGenerator.getXYPlot();
                plot.addRangeMarker(UCL);
                plot.addRangeMarker(LCL);
                plot.addRangeMarker(Mean);
            }
            catch(Exception ex){
                JOptionPane.showMessageDialog(null,"Invalid input detected, please check your inputs", "Input error",JOptionPane.ERROR_MESSAGE);
            }
        }
        else if(e.getSource() == addPoint){

        }
        else if(e.getSource() == clearChart){

        }
    }
}
