import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
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
        Pattern pattern = Pattern.compile("(\\([0-9]+,[0-9]+\\))");
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
    JLabel text;

    JButton setImport;

    DefaultXYDataset Coords;
    JFreeChart chartgen;
    ChartPanel chartholder = new ChartPanel(null);


    Protoframe(){

        Coords = new DefaultXYDataset();
        chartgen = ChartFactory.createXYLineChart("","","",Coords);
        chartholder.setChart(chartgen);

        content = new JPanel();
        content.setPreferredSize(new Dimension(1440,810));
        content.add(chartholder);

        text = new JLabel("Please select a .txt file to import");
        text.setFont(new Font("Times New Roman",Font.PLAIN,20));

        setImport = new JButton("import dataset");
        setImport.setFocusable(false);
        setImport.addActionListener(this);

        west = new JPanel();
        west.setPreferredSize(new Dimension(480,480));
        west.setLayout(new FlowLayout());
        west.add(text);
        west.add(setImport);


        south = new JPanel();
        south.setPreferredSize(new Dimension(270,270));




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
                        Coords.addSeries("test",rawCoords);
                        chartgen = ChartFactory.createXYLineChart("test","test","test",Coords);
                        chartholder.setChart(chartgen);
                        chartholder.repaint();
                        content.repaint();
                    }
                    catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
    }
}
