package de.colorscheme.app;

import de.colorscheme.clustering.ColorData;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.file.Path;
import java.util.Objects;
import java.util.ResourceBundle;

import static de.colorscheme.app.ChooseDirectory.autoSave;
import static de.colorscheme.app.ChooseDirectory.chooseDirectory;
import static de.colorscheme.app.SelectImage.chooseFile;
import static de.colorscheme.output.OutputColors.createOutput;

/**
 * Creates the interface, where the user can select a file, the amount of colors and where the file will be
 * downloaded or if it will be downloaded automatically.
 *
 * @author &copy; 2023 Elisa Johanna Woelk | elisa-johanna.woelk@outlook.de | @fenris_22127
 * @version 1.2
 * @since 17.0.1
 */
public class App {

    static {
        String language = System.getProperty("user.language");
        if (language.equals("de")) {
            ressourceLanguage = "messages_DE";
        }
        else {
            ressourceLanguage = "messages_EN";
        }
    }
    private static String ressourceLanguage;

    /**
     * The {@link JProgressBar progress bar} displaying the progress of the currently generating color scheme
     */
    protected static final JProgressBar progressBar = new JProgressBar();

    /**
     * The main {@link JFrame frame} containing all components of the user interface
     */
    private static final JFrame frame = new JFrame("Color Scheme Generator");

    /**
     * The {@link JCheckBox} to enable/disable automatic downloading of the resulting color scheme file after
     * finishing the generation of the color scheme
     */
    private static final JCheckBox checkbox = new JCheckBox();

    /**
     * An instance of the class extending a {@link SwingWorker} to update the GUI while executing methods
     */
    private static StartProcess task;

    /**
     * The upload {@link JButton button} to let the user choose an image
     */
    protected static JButton upload = new JButton();

    /**
     * The download {@link JButton button} to let the user download the color scheme
     */
    protected static JButton download = new JButton();

    /**
     * The {@link Path} containing the path to the image chosen by the user
     */
    protected static Path fileName = Path.of("");

    /**
     * The {@link Boolean} storing if auto-download is enabled
     * Default: false - Auto-Download is disabled
     */
    protected static boolean autoDownload = false;

    /**
     * The {@link ColorData} object used for all processes for the currently inspected image
     */
    private static ColorData colorData;

    /**
     * The amount of Centroids and therefore the amount of colors to be generated from the image <br>
     * Default: 4 Centroids - The color scheme will contain 4 colors
     */
    private static int selectedCentroids = 4;

    /**
     * The {@link JTextArea} updating the user about the progress of the color scheme generation
     */
    private static JTextArea outputField = new JTextArea();

    /**
     * The path, where the resulting color scheme file will be saved, depending on whether auto-download is enabled or
     * disabled
     */
    private static String downloadPath;

    /**
     * Private constructor to hide the public one
     */
    private App() {
    }

    /**
     * A Getter for {@link #colorData}
     * @return A {@link ColorData} object: The {@link #colorData}
     */
    public static ColorData getColorData() {
        return colorData;
    }

    /**
     * A Setter for {@link #colorData}
     * @param colorData A {@link ColorData} object: The new value for {@link #colorData}
     */
    public static void setColorData(ColorData colorData) {
        App.colorData = colorData;
    }

    /**
     * A Getter for {@link #selectedCentroids}
     * @return An {@link Integer}: The amount of selected centroids
     */
    public static int getSelectedCentroids() {
        return selectedCentroids;
    }

    /**
     * Returns the {@link JTextArea} updating the user about the progress of the color scheme generation
     *
     * @return A {@link JTextArea}: The {@link #outputField}
     */
    public static JTextArea getOutputField() {
        return outputField;
    }

    /**
     * Returns the path, where the resulting color scheme file will be saved
     *
     * @return A {@link String}: The {@link #downloadPath}
     */
    public static String getDownloadPath() {
        return downloadPath;
    }

    /**
     * Returns the instance of the class extending a {@link SwingWorker} to update the GUI while executing methods
     *
     * @return A {@link StartProcess} instance: The {@link #task}
     */
    public static StartProcess getTask() {
        return task;
    }

    public static String getRessourceLanguage() {
        return ressourceLanguage;
    }

    /**
     * Sets up the user interface to let the user upload an image, select the amount of colors to be generated, if the
     * resulting color scheme file will be downloaded automatically and display the progress and current step
     * <ol>
     *     <li>
     *         At first, the icon displayed in the title bar is {@link JFrame#setIconImage(Image) set}.
     *     </li>
     *     <li>
     *         Creates a new {@link JPanel}, sets the layout of the panel to {@link BoxLayout} and specifies how the
     *         components will be aligned on the panel. <br>
     *         {@link BoxLayout#PAGE_AXIS PAGE_AXIS} specifies, that the components will be aligned similarly to how
     *         text will be aligned on a page, based on the {@link ComponentOrientation} property of the container.
     *         For example: Western languages are written from left to right, therefore the
     *         {@link ComponentOrientation} would be {@link ComponentOrientation#LEFT_TO_RIGHT LEFT_TO_RIGHT}.
     *     </li>
     *     <li>
     *         A 5 pixel wide and 10 pixel high {@link Box#createRigidArea(Dimension) rigid area} is created to add
     *         spacing between the title bar and the title. <br>
     *         Then, a {@link JLabel label}, containing the title for the interface is created, the {@link Font font}
     *         is set to "Tahoma", font style set to {@link Font#BOLD bold} and the font size is set to 22pt. <br>
     *         Next, the {@link JComponent#setPreferredSize(Dimension) dimension} of the label is specified with the
     *         width taking 100% of the panels width. <br>
     *         The title is then set to {@link JComponent#setAlignmentX align} in the
     *         {@link Component#CENTER_ALIGNMENT center} of the label, which is then added to the panel.
     *     </li>
     *     <li>
     *         Following that, a 5 pixel wide and 30 pixel high {@link Box#createRigidArea(Dimension) rigid area} is
     *         created to add spacing between the title and the area of the title for area where the amount of colors
     *         to be picked can be set. <br>
     *         For that, a new {@link JPanel} is created, its {@link JComponent#setMaximumSize(Dimension) maximum size}
     *         is set to a certain {@link Dimension} and a {@link JLabel label} is added at the center to set that
     *         sections title. <br>
     *         The {@link JPanel panel} is then added to the main {@link JPanel panel}.
     *     </li>
     *     <li>
     *         Afterwards, a new {@link JPanel panel} is created to hold the {@link JSpinner spinner} to set the amount
     *         of colors to be picked. The default value of the spinner is {@link JSpinner#setValue(Object) set} to 4,
     *         the font, font style and font size is set and a {@link ChangeListener} is
     *         {@link JSpinner#addChangeListener(ChangeListener) added}. <br>
     *         The bounds are then set and the {@link JSpinner spinner} is added to the {@link JPanel panel}, which is
     *         then added to the main panel.
     *     </li>
     *     <li>
     *          {@link JButton#setText(String) Set} the text of the {@link #upload} button, its {@link Font font} to
     *          "Tahoma", font style to {@link Font#BOLD bold} and the font size to 15pt. <br>
     *          The {@link JButton button} is then set to {@link JComponent#setAlignmentX(float) align} in the
     *          {@link Component#CENTER_ALIGNMENT center} of the {@link JPanel panel}, to which it is then added to.
     *     </li>
     *     <li>
     *          Then, a 5 pixel wide and 15 pixel high {@link Box#createRigidArea(Dimension) rigid area} is
     *          created to add spacing between the {@link #upload} {@link JButton button} and the
     *          {@link JCheckBox checkbox}.
     *     </li>
     *     <li>
     *          A new {@link JPanel} is then created to hold the {@link JCheckBox checkbox} to set, whether the
     *          resulting color scheme file will be downloaded automatically or only on click of the {@link #download}
     *          {@link JButton button}. Its dimension is {@link JComponent#setMaximumSize(Dimension) set} to
     *          <code color="#B5B5B5">500px x 30px</code>. <br>
     *          Afterwards, the text for the {@link #checkbox} is {@link JButton#setText(String) added}, the position
     *          is {@link AbstractButton#setHorizontalAlignment(int) set} to align {@link JCheckBox#LEFT left} in the
     *          checkbox {@link JPanel panel}, the {@link #checkbox} is then added to that {@link JPanel panel}, which
     *          in turn is added to the content {@link JPanel panel}.
     *     </li>
     *     <li>
     *          Then, another 5 pixel wide and 15 pixel high {@link Box#createRigidArea(Dimension) rigid area} is
     *          created to add spacing between the {@link #checkbox} and the {@link #progressBar}.
     *     </li>
     *     <li>
     *         A new {@link JPanel panel} is created for the {@link JProgressBar progress bar} and the status updates, which is
     *         then passed to {@link #startProgressBar(JPanel) startProgressBar()}, where the
     *         {@link #progressBar progress bar}, a {@link JTextArea text area} in a {@link JScrollPane scroll pane}
     *         for status updates and {@link java.awt.event.ActionListener ActionListener}s are added. <br>
     *         The {@link JPanel panel} has its border then set a {@link Color#black} {@link LineBorder} with a width
     *         of <code color="#B5B5B5">1px</code>. <br>
     *     </li>
     *     <li>
     *         {@link JButton#setText(String) Set} the text of the {@link #download} button, its {@link Font font} to
     *         "Tahoma", font style to {@link Font#BOLD bold} and the font size to 15pt. <br>
     *         The {@link JButton button} is then set to {@link JComponent#setAlignmentX(float) align} in the
     *         {@link Component#CENTER_ALIGNMENT center} of the {@link JPanel panel}, to which it is then added to.
     *     </li>
     *     <li>
     *         Finally, the {@link JPanel panel} containing the content is added to the {@link #frame}. <br>
     *         The size of the {@link #frame} is then set to <code color="#B5B5B5">600px x 600px</code> and its
     *         behaviour when clicking the close button is {@link JFrame#setDefaultCloseOperation(int) set} to
     *         {@link JFrame#EXIT_ON_CLOSE exit} the program. <br>
     *         The {@link #frame} is then {@link JFrame#setVisible(boolean) set} to visible.
     *     </li>
     * </ol>
     */
    public static void frame() {
        final String FONT = "Tahoma";
        frame.setIconImage(new ImageIcon("src/main/resources/img/logo.png")
                .getImage()
                .getScaledInstance(100, 100, Image.SCALE_SMOOTH));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        panel.add(Box.createRigidArea(new Dimension(5, 10)));

        JLabel titel = new JLabel(ResourceBundle.getBundle(ressourceLanguage).getString("appTitle"));
        titel.setFont(new Font(FONT, Font.BOLD, 22));
        titel.setPreferredSize(new Dimension(500, 50));
        titel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titel);

        panel.add(Box.createRigidArea(new Dimension(5, 30)));

        JPanel countTitle = new JPanel();
        countTitle.setMaximumSize(new Dimension(500, 30));
        JLabel colorAmount = new JLabel(ResourceBundle.getBundle(ressourceLanguage).getString("appColorAmount"));
        colorAmount.setHorizontalAlignment(SwingConstants.CENTER);
        countTitle.add(colorAmount);
        panel.add(countTitle);

        JPanel count = new JPanel();
        count.setMaximumSize(new Dimension(500, 50));
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, 8, 1));
        spinner.setValue(4);
        spinner.setFont(new Font(FONT, Font.PLAIN, 18));
        spinner.addChangeListener(e -> selectedCentroids = (int) spinner.getValue());
        spinner.setBounds(70, 70, 60, 50);
        count.add(spinner);
        panel.add(count);

        upload.setText(ResourceBundle.getBundle(ressourceLanguage).getString("appUpload"));
        upload.setFont(new Font(FONT, Font.PLAIN, 15));
        upload.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(upload);

        panel.add(Box.createRigidArea(new Dimension(5, 15)));

        JPanel check = new JPanel();
        check.setMaximumSize(new Dimension(500, 30));
        checkbox.setText(ResourceBundle.getBundle(ressourceLanguage).getString("appAutoDownload"));
        checkbox.setHorizontalAlignment(SwingConstants.LEFT);
        check.add(checkbox);
        panel.add(check);

        panel.add(Box.createRigidArea(new Dimension(5, 15)));

        JPanel status = new JPanel();
        startProgressBar(status);

        status.setBorder(new LineBorder(Color.black));
        status.setMaximumSize(new Dimension(300, 200));
        panel.add(status);

        panel.add(Box.createRigidArea(new Dimension(5, 30)));

        download.setText(ResourceBundle.getBundle(ressourceLanguage).getString("appDownload"));
        download.setFont(new Font(FONT, Font.PLAIN, 15));
        download.setAlignmentX(Component.CENTER_ALIGNMENT);
        download.setEnabled(false);
        panel.add(download);

        panel.add(Box.createRigidArea(new Dimension(5, 30)));

        JPanel language = new JPanel();
        language.setMinimumSize(new Dimension(550, 70));
        language.setSize(new Dimension(550, 70));
        String[] comboBoxList = {"English", "German"};
        JComboBox<String> languageChoice = new JComboBox<>(comboBoxList);
        languageChoice.setMaximumSize(new Dimension(80, 20));
        language.add(languageChoice);
        languageChoice.addActionListener(e -> {
            if (Objects.equals(languageChoice.getSelectedItem(), "English")) {
                ressourceLanguage = "messages_EN";
                updateText(titel, colorAmount);
            }
            else {
                ressourceLanguage = "messages_DE";
                updateText(titel, colorAmount);
            }
            frame.repaint();
        });

        panel.add(languageChoice);

        frame.add(panel);
        frame.setSize(600, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static void updateText(JLabel titel, JLabel colorAmount) {
        titel.setText(
                ResourceBundle.getBundle(ressourceLanguage).getString("appTitle")
        );
        colorAmount.setText(
                ResourceBundle.getBundle(ressourceLanguage).getString("appColorAmount")
        );
        upload.setText(
                ResourceBundle.getBundle(ressourceLanguage).getString("appUpload")
        );
        checkbox.setText(
                ResourceBundle.getBundle(ressourceLanguage).getString("appAutoDownload")
        );
        download.setText(
                ResourceBundle.getBundle(ressourceLanguage).getString("appDownload")
        );
    }

    /**
     * A {@link JProgressBar} and a {@link JTextArea} in a {@link JScrollPane} are added to the passed {@link JPanel}
     * and {@link java.awt.event.ActionListener ActionListener}s are added to the {@link #upload} and {@link #download}
     * {@link JButton button}, from which the process of generating a color scheme is started.
     * <ol>
     *     <li>
     *         First, the {@link JProgressBar progress bar} has its minimum {@link JProgressBar#setMinimum(int) set} to
     *         <code color="#B5B5B5">0</code> and its maximum {@link JProgressBar#setMaximum(int) set} to
     *         <code color="#B5B5B5">100</code>. <br>
     *         It is then specified, that the {@link #progressBar progress bar} should display a {@link String} to
     *         display the current value. <br>
     *         The {@link #progressBar progress bar} is then added to the passed {@link JPanel}.
     *     </li>
     *     <li>
     *         Then, a new {@link JTextArea} with <code color="#B5B5B5">0</code> rows and
     *         <code color="#B5B5B5">24</code> columns and a {@link JTextArea#setMargin(Insets) margin} of
     *         <code color="#B5B5B5">5px</code> {@link Insets inside} the {@link JTextArea text area} is created and
     *         is {@link JTextArea#setEditable(boolean) set} to be not editable.
     *     </li>
     *     <li>
     *         The {@link #outputField text area} is then added to a new {@link JScrollPane} and placed in the
     *         {@link BorderLayout#CENTER center} of the passed {@link JPanel panel}.
     *     </li>
     *     <li>
     *         An {@link java.awt.event.ActionListener ActionListener} is then added to the {@link #upload}
     *         {@link JButton button}. <br>
     *         When clicked, the text content of the {@link #outputField text area} for status updates is
     *         {@link JTextArea#setText(String) cleared} and the {@link #fileName filepath} is determined by calling
     *         the {@link SelectImage#chooseFile() chooseFile()} method. <br>
     *         If the {@link #fileName} {@link Objects#equals(Object, Object) equals} "cancel", meaning the selection
     *         process has been cancelled and no file has been selected, the {@link #frame} is
     *         {@link JFrame#repaint() refreshed}. <br>
     *         If the previous case doesn't apply, meaning a file has been selected, the state of the {@link #checkbox}
     *         will be testet and if it has been {@link JCheckBox#isSelected() checked}, {@link #autoDownload} will be
     *         set to <code>true</code> and the {@link #downloadPath} will be set, using
     *         {@link ChooseDirectory#autoSave() autoSave()}. <br>
     *         Afterwards, the process is started by instantiating {@link StartProcess#StartProcess(Path) StartProcess}
     *         and passing the {@link #fileName} to it. <br>
     *         A {@link PropertyChangeListener PropertyChangeListener} is
     *         {@link StartProcess#addPropertyChangeListener(PropertyChangeListener) added} to that instance and if the
     *         {@link PropertyChangeEvent#getPropertyName() name} of the changed property
     *         {@link String#equals(Object) equals} "progress", the progress of the {@link #progressBar} is updated to
     *         the {@link PropertyChangeEvent#getNewValue() new value}. <br>
     *         Finally, the {@link SwingWorker} is scheduled to {@link SwingWorker#execute() start} the worker thread.
     *     </li>
     *     <li>
     *         Afterwards, an {@link java.awt.event.ActionListener ActionListener} is added to the {@link #download}
     *         {@link JButton button}. <br>
     *         If the {@link JProgressBar#getValue() value} of the {@link #progressBar} is
     *         <code color="#B5B5B5">100</code> and the {@link ColorData} instance used for the current process is not
     *         <code>null</code>, the {@link #downloadPath} is set by the
     *         {@link ChooseDirectory#chooseDirectory() chooseDirectory()} method and the file containing the color
     *         scheme is created, using
     *         {@link de.colorscheme.output.OutputColors#createOutput(ColorData, Path) createOutput()} and passing
     *         the {@link ColorData} instance and {@link #fileName path} to the selected image.
     *     </li>
     * </ol>
     *
     * @param status A {@link JPanel}: The passed {@link JPanel panel} that will hold the
     *               {@link JProgressBar progress bar} and the {@link JTextArea text fiel} for progress updates.
     */
    private static void startProgressBar(JPanel status) {
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setStringPainted(true);
        status.add(progressBar);

        outputField = new JTextArea(8, 24);
        outputField.setMargin(new Insets(5, 5, 5, 5));
        outputField.setEditable(false);
        outputField.setWrapStyleWord(true);
        outputField.setLineWrap(true);

        JScrollPane scrollPane = new JScrollPane(
                outputField,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        status.add(scrollPane, BorderLayout.CENTER);

        upload.addActionListener(e -> {
            progressBar.setValue(0);
            download.setEnabled(false);
            outputField.setText("");
            fileName = Path.of(chooseFile());
            if (Objects.equals(fileName.toString(), "cancel")) {
                frame.repaint();
            }
            else {
                if (checkbox.isSelected()) {
                    autoDownload = true;
                    downloadPath = autoSave();
                }
                task = new StartProcess(fileName);
                task.addPropertyChangeListener(
                        evt -> {
                            if (evt.getPropertyName().equals("progress")) {
                                progressBar.setValue((Integer) evt.getNewValue());
                            }
                        });

                task.execute();
            }
        });
        download.addActionListener(e -> {
            if ((progressBar.getValue() == 100) && colorData != null) {
                downloadPath = chooseDirectory();
                createOutput(colorData, fileName);
            }
        });
    }
}