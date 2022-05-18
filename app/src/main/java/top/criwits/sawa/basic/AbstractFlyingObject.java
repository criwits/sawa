package top.criwits.sawa.basic;

import android.graphics.Bitmap;

import top.criwits.sawa.aircraft.AbstractAircraft;
import top.criwits.sawa.config.Graphics;
import top.criwits.sawa.media.ImageManager;


/**
 * 用于各种飞行实体的抽象类
 * @author hitsz, hans
 */
public abstract class AbstractFlyingObject {
    // Both location X and Y are at the centre of image assets!
    protected int locationX;
    protected int locationY;
    protected int speedX;
    protected int speedY;
    // Image. Use null as undefined
    protected Bitmap image = null;

    // Width, -1 for unset
    protected int width = -1;
    // Height, -1 for unset
    protected int height = -1;


    // 'Valid' or 'Survive' mark
    protected boolean isValid = true;

    public AbstractFlyingObject() {
    }

    public AbstractFlyingObject(int locationX, int locationY, int speedX, int speedY) {
        this.locationX = locationX;
        this.locationY = locationY;
        this.speedX = speedX;
        this.speedY = speedY;
    }

    /**
     * Move forward
     */
    public void forward() {
        locationX += speedX;
        locationY += speedY;
        if (locationX <= 0 || locationX >= Graphics.screenWidth) {
            // Reverse if beyond the edge
            speedX = -speedX;
        }
    }

    /**
     * Crash / Hit detect
     */
    public boolean crash(AbstractFlyingObject abstractFlyingObject) {
        // Scale factor to control the size of hitbox
        int factor = this instanceof AbstractAircraft ? 2 : 1;
        int fFactor = abstractFlyingObject instanceof AbstractAircraft ? 2 : 1;

        int x = abstractFlyingObject.getLocationX();
        int y = abstractFlyingObject.getLocationY();
        int fWidth = abstractFlyingObject.getWidth();
        int fHeight = abstractFlyingObject.getHeight();

        return x + (fWidth+this.getWidth())/2 > locationX
                && x - (fWidth+this.getWidth())/2 < locationX
                && y + ( fHeight/fFactor+this.getHeight()/factor )/2 > locationY
                && y - ( fHeight/fFactor+this.getHeight()/factor )/2 < locationY;
    }

    public int getLocationX() {
        return locationX;
    }

    public int getLocationY() {
        return locationY;
    }

    public void setLocation(double locationX, double locationY){
        this.locationX = (int) locationX;
        this.locationY = (int) locationY;
    }

    public int getSpeedY() {
        return speedY;
    }

    public int getSpeedX() {
        return speedX;
    }

    public Bitmap getImage() {
        if (image == null){
            image = ImageManager.get(this);
        }
        return image;
    }

    public int getWidth() {
        if (width == -1){
            // Use the size of image assets
            width = (int)(ImageManager.get(this).getWidth() * Graphics.imageScalingFactor);
        }
        return width;
    }

    public int getHeight() {
        if (height == -1){
            // Use the size of image assets
            height = (int)(ImageManager.get(this).getHeight() * Graphics.imageScalingFactor);
        }
        return height;
    }
    public boolean notValid() {
        return !this.isValid;
    }

    // Make invalid
    public void vanish() {
        isValid = false;
    }

}

