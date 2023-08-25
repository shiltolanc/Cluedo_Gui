import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ImageLoader {
    private Map<String, BufferedImage> images = new HashMap<>();

    public ImageLoader() {
        loadImages();
    }

    private void loadImages() {
        try {
            images.put("Broom", ImageIO.read(getClass().getResource("/images/broom.png")));
            images.put("Scissors", ImageIO.read(getClass().getResource("/images/scissors.png")));
            images.put("Knife", ImageIO.read(getClass().getResource("/images/knife.png")));
            images.put("Shovel", ImageIO.read(getClass().getResource("/images/shovel.png")));
            images.put("iPad", ImageIO.read(getClass().getResource("/images/ipad.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, BufferedImage> getImages() {
        return images;
    }

    public BufferedImage getImage(String imageName) {
        return images.get(imageName);
    }
}