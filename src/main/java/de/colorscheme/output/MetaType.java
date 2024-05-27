package de.colorscheme.output;

import java.util.Objects;

import static de.colorscheme.app.AppController.getResBundle;

/**
 * An enum containing the different types of metadata, that uses the enclosing interface to provide accessors and
 * functionality
 *
 * @author &copy; 2024 Elisa Johanna Woelk | elisa-johanna.woelk@outlook.de | @fenris_22127
 * @version 2.0
 * @since 18.0.1
 */
public enum MetaType {
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
     * The file's date of creation
     */
    FILE_CREATION_DATE(getResBundle().getString("metaCreationDate")),
    /**
     * The file's date of the last modification
     */
    FILE_LAST_MODIFIED_DATE(getResBundle().getString("metaLastModifiedDate")),
    /**
     * The file's date of the last access
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
     * The image's number of color components
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
     * Creates a new {@link MetaType metadata type} with the given {@link String} representation
     *
     * @param type A {@link String}: The metadata type
     */
    MetaType(String type) {
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
            public MetaType getType() {
                return MetaType.this;
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
