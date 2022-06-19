package rmr.kairos.model;

/**
 * Clase modelo de las etiquetas
 * @author Rafa M.
 * @version 1.0
 * @since 1.0
 */
public class Tag {
    private int id;
    private String tagName;
    private String tagColor;
    private String tagColorCode;

    public Tag(String tagName, String tagColor) {
        this.tagName = tagName;
        this.tagColor = tagColor;
    }
    public Tag(){

    }

    public String getTagColorCode() {
        return tagColorCode;
    }

    public void setTagColorCode(String tagColorCode) {
        this.tagColorCode = tagColorCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagColor() {
        return tagColor;
    }

    public void setTagColor(String tagColor) {
        this.tagColor = tagColor;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "tagName='" + tagName + '\'' +
                ", tagColor='" + tagColor + '\'' +
                '}';
    }
}