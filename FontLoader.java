import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

public class FontLoader {

    public static Font loadFont(String path) throws IOException, FontFormatException {
        try {
            InputStream is = FontLoader.class.getResourceAsStream(path);
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            
            // Register the font
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
            
            return font;
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            return null;
        }
    }
}