package com.balugaq.netex.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.imageio.ImageIO;
import lombok.SneakyThrows;

public class ImageUtil {
    public static final List<String> allowedImageExtensions =
            List.of("bmp", "gif", "jpeg", "jpg", "png", "tiff", "wbmp");
    // ex: resourcePath = "/textures/logo.png"

    @SneakyThrows(IOException.class)
    public static BufferedImage getImage(String resourcePath) {
        if (!allowedImageExtensions.contains(resourcePath.substring(resourcePath.lastIndexOf('.') + 1))) {
            throw new IOException("Invalid image extension: " + resourcePath);
        }

        InputStream is = ImageUtil.class.getResourceAsStream(resourcePath);
        if (is == null) {
            throw new IOException("Image not found: " + resourcePath);
        }
        return ImageIO.read(is);
    }
}
