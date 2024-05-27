package de.colorscheme.output;

import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import static de.colorscheme.app.AppController.getResBundle;

/**
 * Represents the metadata of an image file and allows values to be assigned to the metadata types.
 *
 * @author &copy; 2024 Elisa Johanna Woelk | elisa-johanna.woelk@outlook.de | @fenris_22127
 * @version 1.3
 * @since 18.0.1
 */
public interface MetaData extends Comparable<MetaData> {

    /**
     * Creates a {@link Set} of {@link MetaData} with the given {@link Function} to assign the values to a
     * {@link MetaType metadata type}
     *
     * @param metaDataCreator A {@link Function}: The function used to assign the values to a metadata type
     * @return A {@link Set} of {@link MetaData}: The metadata with the assigned values
     */
    static Set<MetaData> createMetaData(Function<MetaType, String> metaDataCreator) {
        Set<MetaData> metaData = new TreeSet<>();
        for (MetaType type : MetaType.values()) {
            metaData.add(type.create(metaDataCreator.apply(type)));
        }
        return metaData;
    }

    /**
     * Creates a {@link Set} of {@link MetaData} with the given {@link Function} to assign the value representing no
     * access to the image
     *
     * @return A {@link Set} of {@link MetaData}: The metadata with the assigned no-access values
     */
    static Set<MetaData> createNoAccessMetaData() {
        Set<MetaData> metaData = new TreeSet<>();
        for (MetaType type : MetaType.values()) {
            metaData.add(type.create(getResBundle().getString("noAccess")));
        }
        return metaData;
    }

    /**
     * Gets the data of a {@link MetaType metadata type}
     *
     * @return A {@link String}: The value of the metadata type
     */
    String getData();

    /**
     * Gets the {@link MetaType metadata type} as a {@link String}
     *
     * @return A {@link String}: The metadata type
     */
    String getDescriptor();

    /**
     * Gets the {@link MetaType metadata type}
     *
     * @return A {@link MetaType}: The metadata type
     */
    MetaType getType();
}
