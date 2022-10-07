package RAF.KiDSDomaci1.model.input;

public class ObjectToProcess {

    private final String filePath;
    private final String content;

    public ObjectToProcess(String filePath, String content) {
        this.filePath = filePath;
        this.content = content;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getContent() {
        return content;
    }
}
