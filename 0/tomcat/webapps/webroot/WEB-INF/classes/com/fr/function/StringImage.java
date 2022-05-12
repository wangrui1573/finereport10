//图片在下文字在上
package com.fr.function;

import com.fr.base.GraphHelper;
import com.fr.data.core.db.BinaryObject;
import com.fr.log.FineLoggerFactory;
import com.fr.script.AbstractFunction;
import com.fr.stable.CoreGraphHelper;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;


/**
 * 图片在下文字在上
 */
public class StringImage extends AbstractFunction {
    @Override
    public Object run(Object[] args) {
        Image result = null;
        int p = 0;
        Object[] ob = new Object[2];
        for (int i = 0; (i < args.length && p <= 1); i++) {
            if (args[i] == null) {
                continue;
            }
            ob[p] = args[i];
            p++;

        }

        if (ob[1] instanceof BinaryObject) {
            BinaryObject binaryObject = (BinaryObject) ob[1];
            try {
                result = initStringImage((String) ob[0], ImageIO.read(binaryObject.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (ob[1] instanceof Image) {
            result = initStringImage((String) ob[0], (Image) ob[1]);
        } else {
            FineLoggerFactory.getLogger().warn("Unsupported type of " + ob[1].getClass());
        }

        return result;
    }

    private Image initStringImage(String name, Image image) {
        BufferedImage splashBuffedImage = CoreGraphHelper.toBufferedImage(image);
        Graphics2D splashG2d = splashBuffedImage.createGraphics();
        double centerX = 25;
        double centerY = 25;
        GraphHelper.drawString(splashG2d, name, centerX, centerY);
        return splashBuffedImage;
    }

}