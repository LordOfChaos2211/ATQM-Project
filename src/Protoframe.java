import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.Layer;
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

public class Protoframe  implements ActionListener {

    boolean validateFile(String path) {
        Path filePath = Path.of(path);
        if (!Files.exists(filePath)) {
            JOptionPane.showMessageDialog(null, "This file does not exist", "Import error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!Files.isRegularFile(filePath)) {
            JOptionPane.showMessageDialog(null, "Invalid file imported", "Import error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        path = path.toLowerCase();
        if (!path.endsWith(".txt")) {
            JOptionPane.showMessageDialog(null, "Invalid file imported", "Import error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    double[][] extractCoords(String path) {
        Pattern pattern = Pattern.compile("(\\(-?[0-9]+,-?[0-9]+\\))");
        String[] matches = pattern.matcher(path).results().map(MatchResult::group).toArray(String[]::new);
        double[][] rawCoords = new double[2][matches.length];
        for (int i = 0; i < matches.length; i++) {
            matches[i] = matches[i].replaceAll("[()]", "");
            String[] temp = matches[i].split(",");
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
    JButton setUCL;
    JButton setLCL;
    JButton setMean;
    JButton titleChange;
    JButton xAxisChange;
    JButton yAxisChange;

    JTextField XTitle;
    JTextField YTitle;
    JTextField chartTitle;
    JTextField datasetName;
    JTextField datapoint;
    JTextField upperLimIn;
    JTextField lowerLimIn;
    JTextField meanIn;
    JTextField adjustUCL;
    JTextField adjustLCL;
    JTextField adjustMean;
    JTextField titleField;
    JTextField xAxisField;
    JTextField yAxisField;

    XYSeriesCollection coords;
    JFreeChart chartGenerator;
    ChartPanel chartHolder = new ChartPanel(null);
    XYSeries series;

    ValueMarker UCL;
    ValueMarker LCL;
    ValueMarker Mean;

    Protoframe() {

        float[] dashPattern = {2.0f, 6.0f};
        BasicStroke dottedLine = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, dashPattern, 0.0f);

        coords = new XYSeriesCollection();

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
        content.setPreferredSize(new Dimension(1440, 810));
        content.add(chartHolder);


        frameTitle = new JLabel("SPC Chart Maker");
        frameTitle.setFont(new Font("Times New Roman", Font.PLAIN, 25));
        frameTitle.setPreferredSize(new Dimension(400, 25));

        importPrompt = new JLabel("Please select a .txt file to import");
        importPrompt.setFont(new Font("Times New Roman", Font.PLAIN, 20));

        titlePrompt = new JLabel("Please enter a title for your chart");
        titlePrompt.setFont(new Font("Times New Roman", Font.PLAIN, 20));

        XAxisPrompt = new JLabel("Please enter the name of the X axis");
        XAxisPrompt.setFont(new Font("Times New Roman", Font.PLAIN, 20));

        YAxisPrompt = new JLabel("Please enter the name of the Y axis");
        YAxisPrompt.setFont(new Font("Times New Roman", Font.PLAIN, 20));

        datapointPrompt = new JLabel("Please enter the data point you want to add");
        datapointPrompt.setFont(new Font("Times New Roman", Font.PLAIN, 20));

        datasetPrompt = new JLabel("Please enter the name of the Dataset");
        datasetPrompt.setFont(new Font("Times New Roman", Font.PLAIN, 20));

        upperTitle = new JLabel("Please enter the value for the upper control limit");
        upperTitle.setFont(new Font("Times New Roman", Font.PLAIN, 20));

        lowerTitle = new JLabel("Please enter the value for the lower control limit");
        lowerTitle.setFont(new Font("Times New Roman", Font.PLAIN, 20));

        meanTitle = new JLabel("Please enter the value for the mean");
        meanTitle.setFont(new Font("Times New Roman", Font.PLAIN, 20));


        setImport = new JButton("Import Dataset");
        setImport.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        setImport.setFocusable(false);
        setImport.addActionListener(this);

        addPoint = new JButton("Add Datapoint");
        addPoint.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        addPoint.setFocusable(false);
        addPoint.addActionListener(this);

        clearChart = new JButton("Clear Chart");
        clearChart.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        clearChart.setFocusable(false);
        clearChart.addActionListener(this);

        createChart = new JButton("Create Chart");
        createChart.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        createChart.setFocusable(false);
        createChart.addActionListener(this);

        addData = new JButton("Add Data");
        addData.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        addData.setFocusable(false);
        addData.addActionListener(this);

        setUCL = new JButton("Update UCL");
        setUCL.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        setUCL.setFocusable(false);
        setUCL.addActionListener(this);

        setLCL = new JButton("Update LCL");
        setLCL.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        setLCL.setFocusable(false);
        setLCL.addActionListener(this);

        setMean = new JButton("Update Mean");
        setMean.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        setMean.setFocusable(false);
        setMean.addActionListener(this);

        titleChange = new JButton("Change/Add Title");
        titleChange.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        titleChange.setFocusable(false);
        titleChange.addActionListener(this);

        xAxisChange = new JButton("Add/Change X axis Title");
        xAxisChange.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        xAxisChange.setFocusable(false);
        xAxisChange.addActionListener(this);

        yAxisChange = new JButton("Add/Change Y axis Title");
        yAxisChange.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        yAxisChange.setFocusable(false);
        yAxisChange.addActionListener(this);

        XTitle = new JTextField();
        XTitle.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        XTitle.setPreferredSize(new Dimension(400, 30));

        YTitle = new JTextField();
        YTitle.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        YTitle.setPreferredSize(new Dimension(400, 30));

        chartTitle = new JTextField();
        chartTitle.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        chartTitle.setPreferredSize(new Dimension(400, 30));

        datasetName = new JTextField();
        datasetName.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        datasetName.setPreferredSize(new Dimension(400, 30));

        datapoint = new JTextField();
        datapoint.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        datapoint.setPreferredSize(new Dimension(400, 30));

        upperLimIn = new JTextField();
        upperLimIn.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        upperLimIn.setPreferredSize(new Dimension(400, 30));

        lowerLimIn = new JTextField();
        lowerLimIn.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        lowerLimIn.setPreferredSize(new Dimension(400, 30));

        meanIn = new JTextField();
        meanIn.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        meanIn.setPreferredSize(new Dimension(400, 30));

        adjustUCL = new JTextField();
        adjustUCL.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        adjustUCL.setPreferredSize(new Dimension(450, 30));

        adjustLCL = new JTextField();
        adjustLCL.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        adjustLCL.setPreferredSize(new Dimension(450, 30));

        adjustMean = new JTextField();
        adjustMean.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        adjustMean.setPreferredSize(new Dimension(450, 30));

        titleField = new JTextField();
        titleField.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        titleField.setPreferredSize(new Dimension(400, 30));

        xAxisField = new JTextField();
        xAxisField.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        xAxisField.setPreferredSize(new Dimension(400, 30));

        yAxisField = new JTextField();
        yAxisField.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        yAxisField.setPreferredSize(new Dimension(400, 30));

        west = new JPanel();
        west.setPreferredSize(new Dimension(480, 480));
        //west.setBackground(Color.BLUE);
        west.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));
        west.add(frameTitle);
        west.add(titlePrompt);
        west.add(chartTitle);
        west.add(XAxisPrompt);
        west.add(XTitle);
        west.add(YAxisPrompt);
        west.add(YTitle);

        west.add(createChart);


        south = new JPanel();
        south.setPreferredSize(new Dimension(270, 270));
        //south.setBackground(Color.red);

        frame = new JFrame();
        frame.setSize(1920, 1080);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(content, BorderLayout.CENTER);
        frame.add(west, BorderLayout.WEST);
        frame.add(south, BorderLayout.SOUTH);
        frame.setVisible(true);
        frame.setTitle("test");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == setImport) {
            JFileChooser fileChooser = new JFileChooser();
            int respose = fileChooser.showOpenDialog(null);
            if (respose == JFileChooser.APPROVE_OPTION) {
                File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                if (validateFile(file.getAbsolutePath())) {
                    try {
                        double[][] rawCoords = extractCoords(Files.readString(Path.of(file.getAbsolutePath())));
                        series = new XYSeries(datasetName.getText());
                        for (int i = 0; i < rawCoords[0].length; i++) {
                            series.add(rawCoords[0][i], rawCoords[1][i]);
                        }
                        coords.addSeries(series);
                        JOptionPane.showMessageDialog(null, "Dataset successfully imported", "Successful import", JOptionPane.INFORMATION_MESSAGE);
                    }
                    catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
        else if (e.getSource() == createChart) {
            if (chartTitle.getText().isEmpty() || XTitle.getText().isEmpty() || YTitle.getText().isEmpty()) {
                int response = JOptionPane.showConfirmDialog(null, "You currently left some important fields empty. Are you sure you want to proceed?", "Input warning", JOptionPane.YES_NO_OPTION);
                if (response == 0) {
                    chartGenerator = ChartFactory.createXYLineChart(chartTitle.getText(), XTitle.getText(), YTitle.getText(), null);
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
                    west.add(clearChart);
                    south.add(setUCL);
                    south.add(adjustUCL);
                    south.add(setLCL);
                    south.add(adjustLCL);
                    south.add(setMean);
                    south.add(adjustMean);
                    south.add(titleChange);
                    south.add(titleField);
                    south.add(xAxisChange);
                    south.add(xAxisField);
                    south.add(yAxisChange);
                    south.add(yAxisField);
                    south.revalidate();
                    south.repaint();
                    west.revalidate();
                    west.repaint();
                }
            }
            else {
                chartGenerator = ChartFactory.createXYLineChart(chartTitle.getText(), XTitle.getText(), YTitle.getText(), null);
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
                west.add(clearChart);
                south.add(setUCL);
                south.add(adjustUCL);
                south.add(setLCL);
                south.add(adjustLCL);
                south.add(setMean);
                south.add(adjustMean);
                south.add(titleChange);
                south.add(titleField);
                south.add(xAxisChange);
                south.add(xAxisField);
                south.add(yAxisChange);
                south.add(yAxisField);
                south.revalidate();
                south.repaint();
                west.revalidate();
                west.repaint();
            }
        }
        else if (e.getSource() == addData) {
            try {
                if (coords.getSeriesCount() < 1) {
                    if (JOptionPane.showConfirmDialog(null, "You appear to have not imported a dataset. Do you wish to continue?", "Input error", JOptionPane.YES_NO_OPTION) == 0) {
                        series = new XYSeries(datasetName.getText());
                        coords.addSeries(series);
                    }
                    else throw new RuntimeException();
                }
                else chartGenerator = ChartFactory.createXYLineChart(chartTitle.getText(), XTitle.getText(), YTitle.getText(), coords);
                try{
                    if(coords.getSeries("") != null){
                        XYSeries series = coords.getSeries("");
                        series.setKey(datasetName.getText());
                    }
                }
                catch (Exception ex){
                    System.out.println("error");
                }
                XYPlot plot = chartGenerator.getXYPlot();
                if (upperLimIn.getText().isEmpty()) {
                    if (JOptionPane.showConfirmDialog(null, "The value of the upper control limit is missing. Do you want to continue?", "Input error", JOptionPane.YES_NO_OPTION) != 0) {
                        throw new RuntimeException();
                    }
                }
                else {
                    UCL.setValue(Double.parseDouble(upperLimIn.getText()));
                    plot.addRangeMarker(UCL);
                }
                if (lowerLimIn.getText().isEmpty()) {
                    if (JOptionPane.showConfirmDialog(null, "The value of the lower control limit is missing. Do you want to continue?", "Input error", JOptionPane.YES_NO_OPTION) != 0) {
                        throw new RuntimeException();
                    }
                }
                else {
                    LCL.setValue(Double.parseDouble(lowerLimIn.getText()));
                    plot.addRangeMarker(LCL);
                }
                if (meanIn.getText().isEmpty()) {
                    if (JOptionPane.showConfirmDialog(null, "The value of the mean is missing. Do you want to continue?", "Input error", JOptionPane.YES_NO_OPTION) != 0) {
                        throw new RuntimeException();
                    }
                }
                else {
                    Mean.setValue(Double.parseDouble(meanIn.getText()));
                    plot.addRangeMarker(Mean);
                }
                chartHolder.setChart(chartGenerator);
                datasetName.setText("");
                upperLimIn.setText("");
                lowerLimIn.setText("");
                meanIn.setText("");
            }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Invalid input detected, please check your inputs", "Input error", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if (e.getSource() == addPoint) {
            double[][] rawCoords = extractCoords(datapoint.getText());
            datapoint.setText("");
            if (rawCoords[0].length == 0) {
                JOptionPane.showMessageDialog(null, "No valid input detected", "Input error", JOptionPane.INFORMATION_MESSAGE);
            }
            else {
                for (int i = 0; i < rawCoords[0].length; i++) {
                    series.add(rawCoords[0][i], rawCoords[1][i]);
                }
            }
        }
        else if (e.getSource() == clearChart) {
            coords.removeAllSeries();
            XYPlot plot = chartGenerator.getXYPlot();
            plot.removeRangeMarker(UCL);
            plot.removeRangeMarker(LCL);
            plot.removeRangeMarker(Mean);
        }
        else if (e.getSource() == setUCL) {
            try {
                XYPlot plot = chartGenerator.getXYPlot();
                if (plot.getRangeMarkers(Layer.FOREGROUND) == null || !plot.getRangeMarkers(Layer.FOREGROUND).contains(UCL))
                    plot.addRangeMarker(UCL);
                double input = Double.parseDouble(adjustUCL.getText());
                adjustUCL.setText("");
                UCL.setValue(input);
            }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Invalid input detected. Please input numbers only", "Input error", JOptionPane.ERROR_MESSAGE);
                adjustUCL.setText("");
            }
        }
        else if (e.getSource() == setLCL) {
            try {
                XYPlot plot = chartGenerator.getXYPlot();
                if (plot.getRangeMarkers(Layer.FOREGROUND) == null || !plot.getRangeMarkers(Layer.FOREGROUND).contains(LCL)) plot.addRangeMarker(LCL);
                double input = Double.parseDouble(adjustLCL.getText());
                adjustLCL.setText("");
                LCL.setValue(input);
            }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Invalid input detected. Please input numbers only", "Input error", JOptionPane.ERROR_MESSAGE);
                adjustLCL.setText("");
            }
        }
        else if (e.getSource() == setMean) {
            try {
                XYPlot plot = chartGenerator.getXYPlot();
                if (plot.getRangeMarkers(Layer.FOREGROUND) == null || !plot.getRangeMarkers(Layer.FOREGROUND).contains(Mean))
                    plot.addRangeMarker(Mean);
                double input = Double.parseDouble(adjustMean.getText());
                adjustMean.setText("");
                Mean.setValue(input);
            }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Invalid input detected. Please input numbers only", "Input error", JOptionPane.ERROR_MESSAGE);
                adjustMean.setText("");
            }
        }
        else if (e.getSource() == titleChange) {
            if(titleField.getText().isEmpty()){
                if(JOptionPane.showConfirmDialog(null,"You appear to have left this field empty. Do you wish to remove the title?", "Input error", JOptionPane.YES_NO_OPTION) == 0) {
                    chartGenerator.setTitle(titleField.getText());
                }
            }
            else {
                chartGenerator.setTitle(titleField.getText());
                titleField.setText("");
            }
        }
        else if (e.getSource() == xAxisChange) {
            XYPlot plot = chartGenerator.getXYPlot();
            if (xAxisField.getText().isEmpty()) {
                if (JOptionPane.showConfirmDialog(null, "You appear to have left this field empty. Do you wish to remove the title?", "Input error", JOptionPane.YES_NO_OPTION) == 0) {
                    plot.getDomainAxis().setLabel("");
                }
            }
            else {
                plot.getDomainAxis().setLabel(xAxisField.getText());
                xAxisField.setText("");
            }
        }
        else if (e.getSource() == yAxisChange) {
            XYPlot plot = chartGenerator.getXYPlot();
            if (yAxisField.getText().isEmpty()) {
                if (JOptionPane.showConfirmDialog(null, "You appear to have left this field empty. Do you wish to remove the title?", "Input error", JOptionPane.YES_NO_OPTION) == 0) {
                    plot.getRangeAxis().setLabel("");
                }
            }
            else {
                plot.getRangeAxis().setLabel(yAxisField.getText());
                yAxisField.setText("");
            }
        }
    }
}
