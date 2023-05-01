package de.colorscheme.app;

import darrylbu.util.SwingUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Opens the file directory and lets the user open an image file to generate a color scheme from
 * @author &copy; 2022 Elisa Johanna Woelk | elisa-johanna.woelk@outlook.de | @fenris_22127
 * @version 1.1
 * @since 17.0.1
 */
public class SelectImage {

    /**
     * Private constructor to hide the public one
     */
    private SelectImage() {}

    /**
     * Opens the users default directory in a customized {@link JFileChooser} to select an image to generate
     * a color scheme from <br>
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
     *         {@link JFileChooser#setAcceptAllFileFilterUsed(boolean) Disables the "Accept All Files"-Filter}, adds
     *         a {@link FileNameExtensionFilter Filter} for image files only,
     *         {@link JFileChooser#addChoosableFileFilter(FileFilter) adds} it as an available filter and
     *         {@link JFileChooser#setFileFilter(FileFilter) sets} it as the filter used for the file directory
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
     *         Creates a {@link JLabel label} to preview images, sets its {@link Dimension dimensions},
     *         {@link #getScaledDimension(BufferedImage, Dimension) scales} the previewed image to fit those dimensions
     *         and {@link JFileChooser#setAccessory(JComponent) adds} it to the file chooser
     *     </li>
     *     <li>
     *         Adds a {@link JFileChooser#addPropertyChangeListener(PropertyChangeListener) listener} to detect actions
     *         being performed
     *     </li>
     *     <li>
     *         Creates a {@link SwingWorker} to be able to handle heavy tasks smoothly
     *     </li>
     *     <li>
     *         Runs protected method in background and if an action was performed, tests if the action was an image
     *         being selected
     *     </li>
     *     <li>
     *         If an image was selected, gets image as {@link File}, creates a {@link FileInputStream} into a
     *         {@link BufferedImage} and gives it with the dimension of the preview to
     *         {@link #getScaledDimension(BufferedImage, Dimension) getScaledDimension()} and sets scaled version as
     *         preview
     *     </li>
     *     <li>
     *         Stores the result of the "Cancel" or "Upload" button
     *     </li>
     *     <li>
     *         Adds the {@link JFileChooser} to the {@link JFrame}, lays out its contents according to the available
     *         size, centers the panel and sets {@link JFrame} to invisible
     *     </li>
     *     <li>
     *         If "Upload" was clicked, gets the {@link JFileChooser#getSelectedFile() selected} image as a
     *         {@link File} object, gets the {@link File#getAbsolutePath() path} and disposes of the {@link JFrame}
     *     </li>
     *     <li>
     *         If "Cancel" was clicked, exits program
     *     </li>
     * </ol>
     * @return A {@link String} with the absolute filepath to the selected image
     */
    public static String chooseFile() {
        //Create JFrame, set DefaultCloseOperation and set icon to be displayed in the top left corner
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon("src/main/resources/searchFile.png").getImage());

        //Create FileChooser, set its dimensions, font, display the control buttons and set dialog title
        JFileChooser fileChooser = new JFileChooser("user.home");
        fileChooser.setPreferredSize(new Dimension(800, 600));
        setFileChooserFont(fileChooser.getComponents());
        fileChooser.setControlButtonsAreShown(true);
        fileChooser.setDialogTitle("Upload file");

        //Set icons of files to System default icons
        fileChooser.setFileView(new FileView() {
            @Override
            public Icon getIcon(File f) {
                return FileSystemView.getFileSystemView().getSystemIcon(f);
            }
        });

        //Set filter to display image files only and add as available filter
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image file", "jpg", "jpeg", "png");
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setFileFilter(filter);

        //Set file view to List
        Action details = fileChooser.getActionMap().get("viewTypeDetails");
        details.actionPerformed(null);

        //Get files in listview as a table, set width of 'modified' and 'type' and remove 'size' tab
        JTable table = SwingUtils.getDescendantsOfType(JTable.class, fileChooser).get(0);
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(table.getColumnCount() - 1).setPreferredWidth(140);
        columnModel.getColumn(table.getColumnCount() - 2).setPreferredWidth(100);
        table.removeColumn(table.getColumnModel().getColumn(table.getColumnCount() - 3));
        columnModel.getColumn(table.getColumnCount() - 3).setPreferredWidth(150);


        //Add new JLabel to display previews of images, set its size and add it as an accessory to the FileChooser
        JLabel img = new JLabel();
        Dimension boundary = new Dimension(300,400);
        img.setPreferredSize(boundary);
        fileChooser.setAccessory(img);


        //When any JFileChooser property changes, like an item being selected, this handler is executed
        fileChooser.addPropertyChangeListener(pe -> {
            if (pe.getPropertyName().equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
                JTable jtable = SwingUtils.getDescendantsOfType(JTable.class, fileChooser).get(0);
                TableColumnModel tableColumnModel = jtable.getColumnModel();
                tableColumnModel.getColumn(jtable.getColumnCount() - 1).setPreferredWidth(130);
                tableColumnModel.getColumn(jtable.getColumnCount() - 2).setPreferredWidth(100);
                jtable.removeColumn(jtable.getColumnModel().getColumn(jtable.getColumnCount() - 3));
                tableColumnModel.getColumn(jtable.getColumnCount() - 3).setPreferredWidth(220);
            }
            // Create SwingWorker for smooth experience when handling heavy tasks
            SwingWorker<Image,Void> worker = new SwingWorker<>() {

                protected Image doInBackground() {
                    //If selected file changes
                    if (pe.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
                        // Get selected file
                        File f = fileChooser.getSelectedFile();

                        try {
                            //Create FileInputStream from file and read it into a BufferedImage via ImageIO
                            FileInputStream fileInputStream = new FileInputStream(f);
                            BufferedImage bufferedImage = ImageIO.read(fileInputStream);
                            Dimension imageDimension = getScaledDimension(bufferedImage, boundary);

                            // Return the scaled version of image
                            return bufferedImage.getScaledInstance(
                                    imageDimension.width,
                                    imageDimension.height,
                                    Image.SCALE_SMOOTH);

                        }
                        catch (NullPointerException e) {
                            // If there is a problem reading image (invalid image or unable to read)
                            img.setText("Not valid image/Unable to read");
                        }
                        catch (Exception e) {
                            img.setText("");

                        }
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        // Get the image
                        Image image = get(1L, TimeUnit.NANOSECONDS);

                        // If image doesn't exist, return
                        if (image == null) return;

                        // Set icon otherwise
                        img.setIcon(new ImageIcon(image));
                    }
                    catch (InterruptedException | ExecutionException | TimeoutException e) {
                        Thread.currentThread().interrupt();
                        img.setText("Couldn't load image.");
                    }
                }
            };
            // Start worker thread
            worker.execute();
        });


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
        if (result == JFileChooser.CANCEL_OPTION) {
            frame.dispose();
            return "cancel";
        }
        return filePath;
    }

    /**
     * Scales the passed image to fit the given boundary without losing its aspect ratio
     * @param img The image to be scaled
     * @param boundary A two-dimensional Object specifying the maximum width and height for the image
     * @return A {@link Dimension} Object, containing the new dimensions of the image
     */
    private static Dimension getScaledDimension(BufferedImage img, Dimension boundary) {
        Dimension imgSize = new Dimension(img.getWidth(), img.getHeight());

        int originalWidth = imgSize.width;
        int originalHeight = imgSize.height;
        int boundWidth = boundary.width;
        int boundHeight = boundary.height;
        int newWidth = originalWidth;
        int newHeight = originalHeight;

        // first check if we need to scale width
        if (originalWidth > boundWidth) {
            //scale width to fit
            newWidth = boundWidth;
            //scale height to maintain aspect ratio
            newHeight = (newWidth * originalHeight) / originalWidth;
        }

        // then check if we need to scale even with the new height
        if (newHeight > boundHeight) {
            //scale height to fit instead
            newHeight = boundHeight;
            //scale width to maintain aspect ratio
            newWidth = (newHeight * originalWidth) / originalHeight;
        }
        return new Dimension(newWidth, newHeight);
    }

    /**
     * Sets the font used for the File Explorer
     * @param comp An {@link java.util.Arrays Array} with all components of the File Explorer
     */
    private static void setFileChooserFont(Component[] comp) {
        Font font = new Font("Verdana",Font.PLAIN,14);
        for (Component component : comp) {
            if (component instanceof Container container) setFileChooserFont(container.getComponents());
            component.setFont(font);
        }
    }
}
