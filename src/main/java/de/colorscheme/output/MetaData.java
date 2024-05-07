package de.colorscheme.output;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

/*import static de.colorscheme.app.AppController.getResBundle;*/
import static de.colorscheme.app.NewController.getResBundle;

/**
 * Represents the metadata of an image file and allows values to be assigned to the metadata types.
 *
 * @author &copy; 2023 Elisa Johanna Woelk | elisa-johanna.woelk@outlook.de | @fenris_22127
 * @version 1.3
 * @since 18.0.1
 */
public interface MetaData extends Comparable<MetaData> {

    /**
     * Creates a {@link Set} of {@link MetaData} with the given {@link Function} to assign the values to a
     * {@link MetaData.Type metadata type}
     *
     * @param metaDataCreator A {@link Function}: The function used to assign the values to a metadata type
     * @return A {@link Set} of {@link MetaData}: The metadata with the assigned values
     */
    static Set<MetaData> createMetaData(Function<MetaData.Type, String> metaDataCreator) {
        Set<MetaData> metaData = new TreeSet<>();
        for (MetaData.Type type : MetaData.Type.values()) {
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
        for (MetaData.Type type : MetaData.Type.values()) {
            metaData.add(type.create(getResBundle().getString("noAccess")));
        }
        return metaData;
    }

    /**
     * Gets the data of a {@link MetaData.Type metadata type}
     *
     * @return A {@link String}: The value of the metadata type
     */
    String getData();

    /**
     * Gets the {@link MetaData.Type metadata type} as a {@link String}
     *
     * @return A {@link String}: The metadata type
     */
    String getDescriptor();

    /**
     * Gets the {@link MetaData.Type metadata type}
     *
     * @return A {@link MetaData.Type}: The metadata type
     */
    Type getType();

    /**
     * An enum containing the different types of metadata, that uses the enclosing interface to provide accessors and
     * functionality
     */
    enum Type {
        /**
         * The name of the file
         */
        FILE_NAME(getResBundle().getString("metaFileName")),
        /**
         * The type of the file
         */
        FILE_TYPE(getResBundle().getString("metaFileType")),
        /**
         * The size of the file
         */
        FILE_SIZE(getResBundle().getString("metaSize")),
        /**
         * The date of creation of the file
         */
        FILE_CREATION_DATE(getResBundle().getString("metaCreationDate")),
        /**
         * The date of the last modification of the file
         */
        FILE_LAST_MODIFIED_DATE(getResBundle().getString("metaLastModifiedDate")),
        /**
         * The date of the last access of the file
         */
        FILE_LAST_ACCESSED_DATE(getResBundle().getString("metaLastAccessedDate")),
        /**
         * The height of the image
         */
        FILE_HEIGHT(getResBundle().getString("metaHeight")),
        /**
         * The width of the image
         */
        FILE_WIDTH(getResBundle().getString("metaWidth")),
        /**
         * The image type of the image
         */
        FILE_IMAGE_TYPE(getResBundle().getString("metaImageType")),
        /**
         * The amount of color components of the image
         */
        FILE_COLOR_COMPONENTS(getResBundle().getString("metaColorComponents")),
        /**
         * The bit depth of the image
         */
        FILE_BIT_DEPTH(getResBundle().getString("metaBitDepth")),
        /**
         * The transparency type of the image
         */
        FILE_TRANSPARENCY(getResBundle().getString("metaTransparency")),
        /**
         * Whether the image type supports transparency
         */
        FILE_ALPHA(getResBundle().getString("metaAlpha")),
        /**
         * Whether the alpha of the image is premultiplied
         */
        FILE_ALPHA_TYPE(getResBundle().getString("metaAlphaType"));

        /**
         * The {@link String} representation of the metadata type
         */
        private final String typeDescriptor;

        /**
         * Creates a new {@link MetaData.Type metadata type} with the given {@link String} representation
         *
         * @param type A {@link String}: The metadata type
         */
        Type(String type) {
            typeDescriptor = type;
        }

        /**
         * Creates a new {@link MetaData} and assigns given {@link String}
         *
         * @param data A {@link String}: The value of the metadata
         * @return A {@link MetaData} object: The metadata with the assigned value
         */
        public MetaData create(String data) {
            return new MetaData() {

                @Override
                public String getData() {
                    return data;
                }

                @Override
                public String getDescriptor() {
                    return typeDescriptor + ": ";
                }

                @Override
                public Type getType() {
                    return Type.this;
                }

                @Override
                public int compareTo(MetaData otherMeta) {
                    return Objects.requireNonNull(otherMeta).getType().compareTo(getType());
                }

                @Override
                public String toString() {
                    return getDescriptor() + getData();
                }
            };
        }
    }
}
