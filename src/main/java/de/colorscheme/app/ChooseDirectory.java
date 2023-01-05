package de.colorscheme.app;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Lets the user choose a directory or finds the default download directory. <br>
 *
 * @author &copy; 2022 Elisa Johanna Woelk | elisa-johanna.woelk@outlook.de | @fenris_22127
 * @version 1.1
 * @since 17.0.1
 */
public class ChooseDirectory {

    /**
     * Private constructor to hide the public one
     */
    private ChooseDirectory() {}

    /**
     * Opens the users default directory in a customized {@link JFileChooser} to select a folder to save the generated
     * Color Scheme in<br>
     * <ol>
     *     <li>
     *         Creates a {@link JFrame}, sets the operation that will happen when the "close" element is clicked and
     *         the icon being displayed in the top left corner
     *     </li>
     *     <li>
     *         Creates a {@link JFileChooser} and customises it by setting the dimensions, font, control elements
     *         and dialog title
     *         </li>
     *     <li>
     *         {@link JFileChooser#setFileView(FileView) Sets} the icons displayed in the file directory to the
     *         {@link FileView#getIcon(File) default icons} of the user's system
     *     </li>
     *     <li>
     *         {@link JFileChooser#setAcceptAllFileFilterUsed(boolean) Disables the "Accept All Files"-Filter} and sets
     *         a {@link FileNameExtensionFilter Filter} for directories only
     *     </li>
     *     <li>
     *         Sets the file view to "list" by {@link ActionMap#get(Object) getting} the key for "viewTypeDetails" (the
     *         {@link Action} name of the list view), giving it to an {@link Action} object and
     *         {@link Action#actionPerformed(ActionEvent) invoking} an action
     *     </li>
     *     <li>
     *         Gets files displayed as a {@link JTable table}, gets all {@link TableColumnModel columns}, gets the
     *         {@link TableColumnModel#getColumn(int) columns} "modified", "type" and "size",
     *         {@link TableColumn#setPreferredWidth(int) sets width} of "modified" and "type" and
     *         {@link JTable#removeColumn(TableColumn) removes} the "size" column
     *     </li>
     *     <li>
     *         Stores the result of the "Cancel" or "Select" button
     *     </li>
     *     <li>
     *         Adds the {@link JFileChooser} to the {@link JFrame}, lays out its contents according to the available
     *         size, centers the panel and sets {@link JFrame} to invisible
     *     </li>
     *     <li>
     *         If "Select" was clicked, gets the {@link JFileChooser#getSelectedFile() selected} folder's path as a
     *         {@link String} and disposes of the {@link JFrame}
     *     </li>
     *     <li>
     *         If "Cancel" was clicked, exits program
     *     </li>
     * </ol>
     *
     * @return A {@link String} with the filepath to the selected download folder
     */
    protected static String chooseDirectory() {
        //Create JFrame, set DefaultCloseOperation and set icon to be displayed in the top left corner
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon("src/main/resources/downloadIcon.png").getImage());

        //Create FileChooser, set its dimensions, font, display the control buttons and set dialog title
        JFileChooser fileChooser = new JFileChooser("user.home");
        fileChooser.setPreferredSize(new Dimension(800, 600));
        setFileChooserFont(fileChooser.getComponents());
        fileChooser.setControlButtonsAreShown(true);
        fileChooser.setDialogTitle("Save in...");
        fileChooser.setApproveButtonText("Select");

        //Set icons of files to System default icons
        fileChooser.setFileView(new FileView() {
            @Override
            public Icon getIcon(File f) {
                return FileSystemView.getFileSystemView().getSystemIcon(f);
            }
        });

        //Set filter to display directories only and disable other filters
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        //Set file view to List
        Action details = fileChooser.getActionMap().get("viewTypeDetails");
        details.actionPerformed(null);

        //Get files in listview as a table, set width of 'modified' and 'type' and remove 'size' tab
        JTable table = darrylbu.util.SwingUtils.getDescendantsOfType(JTable.class, fileChooser).get(0);
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(table.getColumnCount() - 1).setPreferredWidth(180);
        columnModel.getColumn(table.getColumnCount() - 2).setPreferredWidth(100);
        table.removeColumn(table.getColumnModel().getColumn(table.getColumnCount() - 3));

        //Store result of 'Upload' or 'Cancel' button
        int result = fileChooser.showOpenDialog(frame);

        //Add FileChooser to JFrame, lay out components according to size, center panel and set invisible
        frame.add(fileChooser);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(false);

        String filePath = "";
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            filePath = selectedFile.getAbsolutePath();
            frame.dispose();
        }
        if (result == JFileChooser.CANCEL_OPTION) System.exit(0);
        return filePath;
    }

    /**
     * Sets the font used for the File Explorer
     *
     * @param comp An {@link java.util.Arrays Array} with all components of the File Explorer
     */
    private static void setFileChooserFont(Component[] comp) {
        Font font = new Font("Verdana", Font.PLAIN, 14);
        for (Component component : comp) {
            if (component instanceof Container container) setFileChooserFont(container.getComponents());
            component.setFont(font);
        }
    }

    /**
     * Finds the users default download directory.
     * @return A {@link String}: The path to the default download directory
     */
    protected static String autoSave() {
        return System.getProperty("user.home").concat("/Downloads");
    }
}
